package zildo.fwk.net;

import java.nio.ByteBuffer;

import zildo.fwk.file.EasyBuffering;
import zildo.fwk.net.packet.AcceptPacket;
import zildo.fwk.net.packet.AskPacket;
import zildo.fwk.net.packet.ConnectPacket;
import zildo.fwk.net.packet.GetPacket;
import zildo.fwk.net.packet.ServerPacket;

public abstract class Packet {

	public enum PacketType {
		// Header should be 8-long.
		SERVER("SRVCREAT", ServerPacket.class),
		CLIENT_CONNECT("CLNCONNE", ConnectPacket.class),
		SERVER_ACCEPT("SRVACCEP", AcceptPacket.class),
		ASK_RESOURCE("ASK_DATA", AskPacket.class),
		GET_RESOURCE("GET_DATA", GetPacket.class);
		
		Class<? extends Packet> clazz;
		String header;
		
		private PacketType(String p_header, Class<? extends Packet> p_clazz) {
			this.clazz=p_clazz;
			this.header=p_header;
			if (p_header.length() != 8) {
				throw new RuntimeException("Header should be an 8-long string.");
			}
		}
	}
	
	protected EasyBuffering b;
	TransferObject source;
	PacketType type;
	
    public Packet() {
        b=new EasyBuffering(NetSend.PACKET_MAX_SIZE);
        // Initializes class
        for (PacketType typ : PacketType.values()) {
        	if (this.getClass().equals(typ.clazz)) {
        		type=typ;
        	}
        }
    }

    public void setSource(TransferObject p_object) {
    	source=p_object;
    }
    
    public TransferObject getSource() {
    	return source;
    }
    
    protected final String getHeader() {
    	return type.header;
    }
    
    public PacketType getType() {
    	return type;
    }
    
    /**
     * This method should be implemented by every subclasses, considering that the buffer is just filled
     * with the header. (@see {@link Packet#send()})
     */
    protected abstract void buildPacket();

    /**
     * This method could be implemented by sublcasses, if they need extra-initializations.
     * Default is empty.
     * @param p_buffer
     */
    protected void deserialize(EasyBuffering p_buffer) {
    	
    }
    
    /**
     * Receive a ByteBuffer and return a Packet.
     * @param p_buffer
     * @return Packet
     */
    public static Packet receive(ByteBuffer p_buffer) {
        // Read the header
    	EasyBuffering eb=new EasyBuffering(p_buffer);
    	String header=eb.readString();
    	Packet p=null;
    	for (PacketType typ : PacketType.values()) {
    		if (typ.header.equals(header)) {
    			// Create the packet
    			try {
    				p=typ.clazz.newInstance();
    				p.deserialize(eb);
    			} catch (Exception e) {
    				throw new RuntimeException(e);
    			}
    			
    		}
    	}
        return p;
    }
    
    /**
     * Build packet into a ByteBuffer.
     * We put the header, and the packet content. Then, we flip the buffer.
     */
    public ByteBuffer getPacket() {
    	b.clear();
        b.put(getHeader());
        buildPacket();
        b.getAll().flip();
        return b.getAll();
    }
}