package zildo.fwk.net;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import zildo.client.Client;
import zildo.client.ClientEngineZildo;
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
import zildo.monde.WaitingDialog;
import zildo.monde.WaitingSound;
import zildo.monde.decors.SpriteEntity;
import zildo.monde.map.Area;
import zildo.monde.map.Case;
import zildo.server.SpriteManagement;

/**
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
	boolean refreshEntity;
	
	int delayConnect=0;
	int nFrame=0;
	
	private static int TIMEOUT_CONNECT = 20;
	
	TransferObject server;
	Client client;
	
	public NetClient(Client p_client) {
		super(null, NetSend.NET_PORT_CLIENT);
		
		client=p_client;
		
		serverFound=false;
		serverAccepted=false;
		askedMap=false;
		gotMap=false;
		gotEntities=false;
		refreshEntity=false;
		
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
					ConnectPacket connectPacket=new ConnectPacket(true);
					sendPacket(connectPacket, server);
				}
			} else if (!serverAccepted) {
				// 2) Sending a request to the server in order to join game
				p=packets.getUniqueTyped(PacketType.SERVER_ACCEPT);
				if (p!=null) {
					AcceptPacket packet=(AcceptPacket) p;
					ClientEngineZildo.spriteDisplay.setZildoId(packet.zildoId);
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
				sendPacket(new AskPacket(ResourceType.MAP), server);
				askedMap=true;
			} else {
                // 4) Server sent resources
                PacketSet set = packets.getTyped(PacketType.GET_RESOURCE);
                for (Packet packet : set) {
                    GetPacket getPacket = (GetPacket) packet;

                    switch (getPacket.resourceType) {
                        case MAP:
                            receiveMap(getPacket);
                            if (!gotMap) {
                            	// Ask entities for the first time
                            	sendPacket(new AskPacket(ResourceType.ENTITY), server);
                            }
                            gotMap = true;
                            break;
                        case MAP_PART:
                        	receiveMapPart(getPacket);
                        	break;
                        case DIALOG:
                        	receiveDialog(getPacket);
                        	break;
                        case ENTITY:
                            receiveEntities(getPacket);
                            refreshEntity = true;
                            gotEntities = true;
                            break;
                        case SOUND:
                            receiveSounds(getPacket);
                            break;
                    }
                }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sendKeyboard() {
		KeyboardInstant instant = KeyboardInstant.getKeyboardInstant();
		GetPacket packet=new GetPacket(ResourceType.KEYBOARD, instant.serialize().getAll(), null);
		
		sendPacket(packet, server);
	}
	
	public void receiveMap(GetPacket p_packet) {
		EasyBuffering buffer=new EasyBuffering(p_packet.getBuffer());
		Area map=Area.deserialize(buffer, false);
		
		map.setName(p_packet.name);
		ClientEngineZildo.mapDisplay.setCurrentMap(map);
	}
	
	public void receiveMapPart(GetPacket p_packet) {
		EasyBuffering buffer=new EasyBuffering(p_packet.getBuffer());
		Area map=ClientEngineZildo.mapDisplay.getCurrentMap();
		while (!buffer.eof()) {
			int x=buffer.readInt();
			int y=buffer.readInt();
			Case c=Case.deserializeCase(buffer);
			map.set_mapcase(x, y, c);
		}
	}
	
    /**
     * Receive entities and re-ask for next frame.
     * @param p_packet
     */
    public void receiveEntities(GetPacket p_packet) {
        EasyBuffering buffer = new EasyBuffering(p_packet.getBuffer());
        List<SpriteEntity> list = SpriteManagement.deserializeEntities(buffer);
        ClientEngineZildo.spriteDisplay.setEntities(list);

        // Re-ask
        sendPacket(new AskPacket(ResourceType.ENTITY), server);
    }

    public void receiveSounds(GetPacket p_packet) {
        EasyBuffering buffer = new EasyBuffering(p_packet.getBuffer());
        List<WaitingSound> sounds=new ArrayList<WaitingSound>();
        while (!buffer.eof()) {
            WaitingSound s = WaitingSound.deserialize(buffer);
            sounds.add(s);
        }
        ClientEngineZildo.soundPlay.playSounds(sounds);
    }
    
    public void receiveDialog(GetPacket p_packet) {
        EasyBuffering buffer = new EasyBuffering(p_packet.getBuffer());
    	WaitingDialog dial=WaitingDialog.deserialize(buffer);
    	
    	boolean dialogEnded=ClientEngineZildo.dialogDisplay.launchDialog(Collections.singletonList(dial));
    	if (dialogEnded) {
    		EventPacket packet=new EventPacket(EventType.DIALOG_ENDED);
    		sendPacket(packet, server);
    	}
    }
    
    public void close() {
    	// Send a disconnect packet
    	ConnectPacket connectPacket=new ConnectPacket(false);
    	sendPacket(connectPacket, server);
    	super.close();
    }
	public boolean isConnected() {
		return serverAccepted && gotMap && gotEntities;
	}

}
