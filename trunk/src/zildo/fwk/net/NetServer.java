package zildo.fwk.net;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;
import java.util.Set;

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
import zildo.monde.WaitingDialog;
import zildo.monde.WaitingSound;
import zildo.monde.map.Area;
import zildo.monde.map.Case;
import zildo.monde.map.Point;
import zildo.monde.sprites.SpriteEntity;
import zildo.server.ClientState;
import zildo.server.EngineZildo;
import zildo.server.MapManagement;
import zildo.server.Server;
import zildo.server.SpriteManagement;

/**
 * Network server engine
 * 
 * Here we deals with packet between clients and server.
 * 
 * @author tchegito
 *
 */
public class NetServer extends NetSend {

	Server server;
	int counter;
	
	int nFrame=0;
	
	public NetServer(Server p_server) {
		super(null, NetSend.NET_PORT_SERVER);
		server=p_server;
	}
	
	protected void addClient(TransferObject client) {
	}
	
	public void run() {
		//System.out.println("server"+nFrame++);
		
		TransferObject source=null;
		try {
			if (isOpen()) {

				PacketSet packets=receiveAll();
				// Emits signal so then client could connect (every 20 frames)
				if (counter % 20 == 0) {
					sendPacket(PacketType.SERVER, null);
				}
				counter++;
				
				Packet clientConnect=packets.getUniqueTyped(PacketType.CLIENT_CONNECT);
				if (clientConnect != null) {
					boolean in=((ConnectPacket)clientConnect).isJoining();
					source=clientConnect.getSource();
					if (in) {
						// Client is coming
						log("Serveur:Un client est arriv� !"+source.address.getHostName()+" port:"+source.address.getPort());
	
						int zildoId=server.connectClient(source);
	
						AcceptPacket accept=new AcceptPacket(zildoId);
						sendPacket(accept, source);
					} else {
						// Client is leaving
						server.disconnectClient(source);
					}
					
				}

				if (server.isClients())  {
					// We got clients
					entitiesBuffer=null;	// Reset entities temporary buffer
					PacketSet asks=packets.getTyped(PacketType.ASK_RESOURCE);
					for (Packet packet : asks) {
						AskPacket askPacket = (AskPacket) packet;
						//log("Somebody ask for a resource:"+askPacket.resourceType);
						
						// Send the resource
						switch (askPacket.resourceType) {
						case MAP:
							sendMap(askPacket.getSource());
							break;
						case ENTITY:
							sendEntities(askPacket.getSource());
							break;
							
						default:
								throw new RuntimeException("This resource type is not managed yet.");
						}
                    }
					sendSounds();
					
					sendMapChanges();
					
					sendDialogs();
					
					receiveKeyboards(packets.getTyped(PacketType.GET_RESOURCE));
					
					receiveEvents(packets.getTyped(PacketType.EVENT));
				}
				ZUtils.sleep(5);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Receive keyboard from this GetResource's packet set.
	 * @param p_packets
	 */
	private void receiveKeyboards(PacketSet p_packets) {
		// Receive resource from clients (keyboard commands)
		for (Packet packet : p_packets) {
			GetPacket getPacket=(GetPacket) packet;
            EasyBuffering buffer = new EasyBuffering(getPacket.getBuffer());
            KeyboardInstant i = KeyboardInstant.deserialize(buffer);
            // Update clients command
            TransferObject client=getPacket.getSource();
            server.updateClientKeyboard(client, i);
		}
	}

	/**
	 * Send entities location to client.
	 * @param p_client target
	 */
	private EasyBuffering entitiesBuffer;
	private EasyBuffering entitiesSent;
    private void sendEntities(TransferObject p_client) {
        SpriteManagement spriteManagement = EngineZildo.spriteManagement;
        if (entitiesBuffer == null) {
        	entitiesBuffer = spriteManagement.serializeEntities();
        }
        ClientState cl=Server.getClientState(p_client);
        if (cl != null) {
	        if (cl.zildo.isInventoring()) {
	        	// Extra sprites : only for this client
	        	List<SpriteEntity> sprites=cl.zildo.guiCircle.getSprites();
	        	entitiesSent=new EasyBuffering(entitiesBuffer, sprites.size() * 100);
	        	entitiesSent.put(spriteManagement.serializeEntities(sprites, true));
	        } else {
	        	entitiesSent=entitiesBuffer;
	        }
	        GetPacket getPacket = new GetPacket(ResourceType.ENTITY, entitiesSent.getAll(), null);
	        sendPacket(getPacket, p_client);
        }
        
    }
	
	/**
	 * Send map to a given client.
	 * @param p_client
	 */
	private void sendMap(TransferObject p_client) {
		MapManagement mapManagement=EngineZildo.mapManagement;
		GetPacket getPacket=null;
		EasyBuffering buffer;
		Area area=mapManagement.getCurrentMap();
		buffer=area.serialize();
		
		getPacket=new GetPacket(ResourceType.MAP, buffer.getAll(), area.getName());
		log("Sending map ("+getPacket.length+" bytes)");
		
		sendPacket(getPacket, p_client);
	}
	
	private void sendSounds() {
        List<WaitingSound> queue = EngineZildo.soundManagement.getQueue();
        EasyBuffering broadCastbuffer = new EasyBuffering();
        EasyBuffering singleClientbuffer = new EasyBuffering();

        for (WaitingSound snd : queue) {
        	ByteBuffer b=snd.serialize().getAll();
        	b.flip();
        	if (snd.broadcast) {
        		broadCastbuffer.put(b);
        	} else if (snd.client != null){
        		// Send sound to the given client
        		singleClientbuffer.put(b);
                GetPacket getPacket = new GetPacket(ResourceType.SOUND, singleClientbuffer.getAll(), null);
                sendPacket(getPacket, snd.client);
        	}
        }
        if (queue.size() != 0) {
            // Send the sound info packet
            GetPacket getPacket = new GetPacket(ResourceType.SOUND, broadCastbuffer.getAll(), null);
            broadcastPacketToAllCients(getPacket);
        }
        EngineZildo.soundManagement.resetQueue();
    }

    public void broadcastPacketToAllCients(Packet p_packet) {
        Set<TransferObject> clientsLocation = server.getClientsLocation();
        for (TransferObject cl : clientsLocation) {
            if (cl != null) {
                sendPacket(p_packet, cl);
            }
        }
    }
    
    private void sendMapChanges() {
		Area map=EngineZildo.mapManagement.getCurrentMap();
		if (map.isModified()) {
			// Map has changed, so we must diffuse to clients
			Collection<Point> changes=map.getChanges();
			EasyBuffering buffer = new EasyBuffering();
			for (Point p :changes) {
				Case c=map.get_mapcase(p.getX(), p.getY());
				buffer.put(p.getX());
				buffer.put(p.getY());
				c.serialize(buffer);
			}
            GetPacket getPacket = new GetPacket(ResourceType.MAP_PART, buffer.getAll(), null);
            broadcastPacketToAllCients(getPacket);
			
			map.resetChanges();
		}
	}
	
	/**
	 * Send all waiting dialogs to clients (1 per client)
	 */
    private void sendDialogs() {
		List<WaitingDialog> dialogQueue=EngineZildo.dialogManagement.getQueue();
		EasyBuffering buffer = new EasyBuffering();
		if( dialogQueue.size() !=0) {
			for (WaitingDialog dial : dialogQueue) {
				if (dial.client != null || dial.console) {
					dial.serialize(buffer);
					
			        GetPacket getPacket = new GetPacket(ResourceType.DIALOG, buffer.getAll(), null);
			        if (!dial.console) {
			        	sendPacket(getPacket, dial.client);
			        } else {
			        	broadcastPacketToAllCients(getPacket);
			        }
			        buffer.clear();
				}
			}
			EngineZildo.dialogManagement.resetQueue();
		}
	}

    /**
     * 
     * @param p_events
     */
    private void receiveEvents(PacketSet p_events) {
    	for (Packet p : p_events) {
    		EventPacket event=(EventPacket) p;
    		TransferObject source=event.getSource();
    		switch (event.type) {
    		case DIALOG_ENDED:
    			ClientState client=Server.getClientState(source);
    			EngineZildo.dialogManagement.stopDialog(client);
    			break;
    		default:
    			throw new RuntimeException("This kind of event ("+event.type+") is unknown.");
    		}
    	}
    }
}