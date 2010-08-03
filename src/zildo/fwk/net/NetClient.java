/**
 * Legend of Zildo
 * Copyright (C) 2006-2010 Evariste Boussaton
 * Based on original Zelda : link to the past (C) Nintendo 1992
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package zildo.fwk.net;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import zildo.client.Client;
import zildo.client.ClientEngineZildo;
import zildo.client.gui.menu.PlayerNameMenu;
import zildo.fwk.ZUtils;
import zildo.fwk.file.EasyBuffering;
import zildo.fwk.input.KeyboardInstant;
import zildo.fwk.net.Packet.PacketType;
import zildo.fwk.net.packet.AcceptPacket;
import zildo.fwk.net.packet.AskPacket;
import zildo.fwk.net.packet.ConnectPacket;
import zildo.fwk.net.packet.EventPacket;
import zildo.fwk.net.packet.GetPacket;
import zildo.fwk.net.packet.AskPacket.ResourceType;
import zildo.fwk.net.packet.EventPacket.EventType;
import zildo.monde.WaitingSound;
import zildo.monde.dialog.WaitingDialog;
import zildo.monde.map.Area;
import zildo.monde.map.Case;
import zildo.monde.sprites.SpriteEntity;
import zildo.server.SpriteManagement;
import zildo.server.state.PlayerState;

/**
 * Client on a LAN network.
 * 
 * On the start:
 * -get the map
 * -get all entities
 * 
 * On a regular frame:
 * -send the keyboard commands
 * -get the entities moves
 * 
 * @author tchegito
 *
 */
public class NetClient extends NetSend {

	boolean serverFound;
	boolean serverAccepted;
	boolean askedMap;
	boolean gotMap;
	boolean gotEntities;
	
	int delayConnect=0;
	int nFrame=0;
	int frameWithoutEntity=0;
	
	private static int TIMEOUT_CONNECT = 20;
	
	TransferObject server;
	Client client;
	String playerName=PlayerNameMenu.loadPlayerName();
    KeyboardInstant kbInstant;
    
	public NetClient(Client p_client) {
		super(null, NetSend.NET_PORT_CLIENT);
		
		client=p_client;
		
		serverFound=false;
		serverAccepted=false;
		askedMap=false;
		gotMap=false;
		gotEntities=false;
		
		log("En attente d'un serveur...");
	}
	
	public void run() {
		//System.out.println("client"+nFrame++);

		try {
			PacketSet packets=receiveAll();
			
			Packet p;
			if (!serverFound) {
				// 1) Awaiting for a server to create a game
				p=packets.getUniqueTyped(PacketType.SERVER);
				if (p!=null) {
					server=p.getSource();
					log("Serveur trouvé"+server.address.getHostName());
					
					serverFound=true;
					ConnectPacket connectPacket=new ConnectPacket(true, playerName);
					sendPacket(connectPacket, server);
				}
			} else if (!serverAccepted) {
				// 2) Sending a request to the server in order to join game
				p=packets.getUniqueTyped(PacketType.SERVER_ACCEPT);
				if (p!=null) {
					AcceptPacket packet=(AcceptPacket) p;
					ClientEngineZildo.spriteDisplay.setZildoId(packet.zildoId);
					client.registerClient(new PlayerState(playerName, packet.zildoId));
					log("Le serveur a accepté");
					serverAccepted=true;
				} else {
					delayConnect++;
					if (delayConnect == TIMEOUT_CONNECT) {
						delayConnect=0;
						serverFound=false;
					}
				}

				ZUtils.sleep(5);
			} else if (!askedMap) {
				// 3) Server accepted. So, we ask for the map
				sendPacket(new AskPacket(ResourceType.MAP, false), server);
				askedMap=true;
			}
			
            // 4) Server sent resources
            PacketSet set = packets.getTyped(PacketType.GET_RESOURCE);
            boolean refreshEntities=false;
            for (Packet packet : set) {
                GetPacket getPacket = (GetPacket) packet;

                switch (getPacket.resourceType) {
                case MAP:
                    try {
                        receiveMap(getPacket);
                        if (!gotMap) {
                            // Ask entities for the first time
                            sendPacket(new AskPacket(ResourceType.ENTITY, true), server);
                        }
                        gotMap = true;
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                        // Map receiving has failed. So re-ask.
                        sendPacket(new AskPacket(ResourceType.MAP, false), server);
                    }
                    break;
                case MAP_PART:
                    if (gotMap) {
                        receiveMapPart(getPacket);
                    }
                    break;
                case DIALOG:
                	receiveDialog(getPacket);
                	break;
                case ENTITY:
                    receiveEntities(getPacket);
                    refreshEntities = true;
                    gotEntities = true;
                    break;
                case SOUND:
                    receiveSounds(getPacket);
                    break;
                case CLIENTINFO:
                	receiveClientInfos(getPacket);
                	break;
                }

                // Reask every entities if we haven't anyone since a fixed number of frames
                if (gotMap && !refreshEntities) {
                	frameWithoutEntity++;
                	if (frameWithoutEntity==5) {
                        sendPacket(new AskPacket(ResourceType.ENTITY, true), server);
                        frameWithoutEntity=0;
                        log("Reask entities");
                	}
                } else {
                	frameWithoutEntity=0;
                }
                
                // Has server leaved the game ?
                p=packets.getUniqueTyped(PacketType.CLIENT_CONNECT);
                if (p != null) {
                	client.serverLeft();
                }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sendKeyboard() {
		kbInstant = KeyboardInstant.getKeyboardInstant();
		client.setKbInstant(kbInstant);
		GetPacket packet=new GetPacket(ResourceType.KEYBOARD, kbInstant.serialize().getAll(), null);
		
		sendPacket(packet, server);
	}
	
	private void receiveMap(GetPacket p_packet) {
		EasyBuffering buffer=new EasyBuffering(p_packet.getBuffer());
		Area map=Area.deserialize(buffer, false);
		
		map.setName(p_packet.name);
		ClientEngineZildo.mapDisplay.setCurrentMap(map);
	}
	
	private void receiveMapPart(GetPacket p_packet) {
		EasyBuffering buffer=new EasyBuffering(p_packet.getBuffer());
		Area map=ClientEngineZildo.mapDisplay.getCurrentMap();
		while (!buffer.eof()) {
			int x=buffer.readInt();
			int y=buffer.readInt();
			Case c=Case.deserialize(buffer);
			map.set_mapcase(x, y, c);
		}
	}
	
    /**
     * Receive entities and re-ask for next frame.
     * @param p_packet
     */
	private void receiveEntities(GetPacket p_packet) {
        EasyBuffering buffer = new EasyBuffering(p_packet.getBuffer());
        List<SpriteEntity> list = SpriteManagement.deserializeEntities(buffer);
        ClientEngineZildo.spriteDisplay.setEntities(list);

		// Focus on Zildo
      	SpriteEntity zildo=ClientEngineZildo.spriteDisplay.getZildo();
   		ClientEngineZildo.mapDisplay.setFocusedEntity(zildo);

        // Re-ask
        sendPacket(new AskPacket(ResourceType.ENTITY, false), server);
    }

    private void receiveSounds(GetPacket p_packet) {
        EasyBuffering buffer = new EasyBuffering(p_packet.getBuffer());
        List<WaitingSound> sounds=new ArrayList<WaitingSound>();
        while (!buffer.eof()) {
            WaitingSound s = WaitingSound.deserialize(buffer);
            sounds.add(s);
        }
        ClientEngineZildo.soundPlay.playSounds(sounds);
    }
    
    private void receiveDialog(GetPacket p_packet) {
        EasyBuffering buffer = new EasyBuffering(p_packet.getBuffer());
    	WaitingDialog dial=WaitingDialog.deserialize(buffer);
    	
    	boolean dialogEnded=ClientEngineZildo.dialogDisplay.launchDialog(Collections.singletonList(dial));
    	if (dialogEnded) {
    		EventPacket packet=new EventPacket(EventType.DIALOG_ENDED);
    		sendPacket(packet, server);
    	}
    }
    
    
    private void receiveClientInfos(GetPacket p_packet) {
        EasyBuffering buffer = new EasyBuffering(p_packet.getBuffer());
        while (!buffer.eof()) {
        	PlayerState state=PlayerState.deserialize(buffer);
        	client.registerClient(state);
        }
    }
    
    @Override
	public void close() {
    	// Send a disconnect packet
    	ConnectPacket connectPacket=new ConnectPacket(false, null);
    	sendPacket(connectPacket, server);
    	super.close();
    }
	public boolean isConnected() {
		return serverAccepted && gotMap && gotEntities;
	}

}
