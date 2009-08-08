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
import zildo.fwk.net.packet.GetPacket;
import zildo.fwk.net.packet.AskPacket.ResourceType;
import zildo.monde.WaitingSound;
import zildo.monde.map.Area;
import zildo.monde.map.Case;
import zildo.monde.map.Point;
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
					source=clientConnect.getSource();
					log("Serveur:Un client est arrivé !"+source.address.getHostName()+" port:"+source.address.getPort());

					int zildoId=server.connectClient(source);

					AcceptPacket accept=new AcceptPacket(zildoId);
					sendPacket(accept, source); 
				}

				if (server.isClients())  {
					// We got one client
					Packet askResource=packets.getUniqueTyped(PacketType.ASK_RESOURCE);
					if (askResource != null) {
						AskPacket askPacket=(AskPacket) askResource;
						log("Somebody ask for a resource:"+askPacket.resourceType);
						
						// Send the resource
						switch (askPacket.resourceType) {
						case MAP:
							sendMap(askPacket.getSource());
							break;
						case ENTITY:
							sendEntities();
							break;
							
						default:
								throw new RuntimeException("This resource type is not managed yet.");
						}
                    }
					//sendEntities();
					
					sendSounds();
					
					sendMapChanges();
					
					// Receive resource from client (keyboard commands)
					Packet getResource=packets.getUniqueTyped(PacketType.GET_RESOURCE);
                	if (getResource != null) {
                        GetPacket getPacket = (GetPacket) getResource;
                        EasyBuffering buffer = new EasyBuffering(getPacket.getBuffer());
                        KeyboardInstant i = KeyboardInstant.deserialize(buffer);
                        // Update clients command
                        TransferObject client=getResource.getSource();
                        server.updateClientKeyboard(client, i);
                	}
				}
				ZUtils.sleep(5);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Send entities location to the clients.
	 */
	
    private void sendEntities() {
        SpriteManagement spriteManagement = EngineZildo.spriteManagement;
        EasyBuffering buffer = spriteManagement.serializeEntities();
        GetPacket getPacket = new GetPacket(ResourceType.ENTITY, buffer.getAll(), null);
        broadcastPacketToAllCients(getPacket);
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
	
    public void sendSounds() {
        List<WaitingSound> queue = EngineZildo.soundManagement.getQueue();
        EasyBuffering buffer = new EasyBuffering();

        for (WaitingSound snd : queue) {
        	ByteBuffer b=snd.serialize().getAll();
        	b.flip();
            buffer.put(b);
        }
        if (queue.size() != 0) {
            // Send the sound info packet
            GetPacket getPacket = new GetPacket(ResourceType.SOUND, buffer.getAll(), null);
            broadcastPacketToAllCients(getPacket);
        }
        EngineZildo.soundManagement.resetQueue();
    }

    public void broadcastPacketToAllCients(Packet p_packet) {
        Set<TransferObject> clientsLocation = server.getClientsLocation();
        for (TransferObject cl : clientsLocation) {
            sendPacket(p_packet, cl);
        }
    }
    
	public void sendMapChanges() {
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
			
			//Collection<>
			map.resetChanges();
		}
	}

}