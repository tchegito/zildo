package zildo.fwk.net;


import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import zildo.fwk.net.Packet.PacketType;


public class NetSend extends TransferObject {

	public final static int NET_PORT_SERVER = 8080;
	public final static int NET_PORT_CLIENT = 80;
	public final static int MAX_PACKET_PER_FRAME = 20;

	public final static int PACKET_MAX_SIZE = 20000;
	
	// Map to store the different packet senders
	private final static Map<InetSocketAddress, TransferObject> connexions=
		new HashMap<InetSocketAddress, TransferObject>();

    // Create the broadcast adress
    private final static TransferObject objectForBroadCast = TransferObject.createBroadCastObject(NET_PORT_CLIENT);
    
	public NetSend(Socket p_socket) {
		super(p_socket.getInetAddress(), p_socket.getPort());		
	}
	
	public NetSend(InetAddress p_address, int p_port) {
		super(p_address, p_port);
	}
	
	private ByteBuffer bBuf=ByteBuffer.allocateDirect(PACKET_MAX_SIZE);
	
	/**
	 * Receive all packets
	 * @return List<Packet>
	 */
	public PacketSet receiveAll() {
		PacketSet set=new PacketSet();
		Packet pp=null;
		while (set.size() < MAX_PACKET_PER_FRAME) {
			pp=receive();
			if (pp == null) {
				break;
			}
			set.add(pp);
		}
		if (set.size() >= MAX_PACKET_PER_FRAME) {
			System.out.println("Max packet !");
		}
		return set;
	}

	public static TransferObject getTransferObject(InetSocketAddress p_address, boolean p_create) {
		TransferObject o=connexions.get(p_address);
		if (o==null && p_create) {
			o=new TransferObject(p_address.getAddress(), p_address.getPort());
			connexions.put(p_address, o);
		}
		return o;
	}
	
	public static void addTransferObject(TransferObject p_object) {
		connexions.put(p_object.address, p_object);
	}
	
	/**
	 * Send data to the host described by p_transObject.
	 * @param p_buffer
	 * @param p_transObject host
	 */
	public void sendBuffer(ByteBuffer p_buffer, TransferObject p_transObject) {
		
		try {
			this.channel.send(p_buffer, p_transObject.address);
		} catch (IOException e) {
			
		}

	}
	
	/**
	 * Receive a packet and store the sender.
	 * @return
	 */
	public Packet receive() {
		Packet p=null;
		
		try {
			bBuf.clear();
			InetSocketAddress srcSocket = (InetSocketAddress)channel.receive(bBuf);
			if (srcSocket == null) {
				return null;
			}
			bBuf.flip();
			p=Packet.receive(bBuf);
			if (p != null) {
				TransferObject source=getTransferObject(srcSocket, true);
				p.setSource(source);
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		return p;
	}
	
	/**
	 * Send a simple packet to a given host.
	 * @param p_type
	 * @param p_object
	 */
	public void sendPacket(PacketType p_type, TransferObject p_object) {
		Packet p=null;

		try {
			p=p_type.clazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Packet type not implemented ("+p_type+")");
		}
		sendPacket(p, p_object);
	}
	
	/**
	 * Send the packet to target. If target is null, we broadcast.
	 * @param p_packet
	 * @param p_object
	 */
	public void sendPacket(Packet p_packet, TransferObject p_target) {
		if (p_target == null) {
			// Want to broadcast (only for LAN)
			p_target=objectForBroadCast;
		}
		ByteBuffer b=p_packet.getPacket();
		sendBuffer(b, p_target);
	}
	
	public void close() {
		super.close();
		if (objectForBroadCast != null) {
			objectForBroadCast.close();
		}
	}
}