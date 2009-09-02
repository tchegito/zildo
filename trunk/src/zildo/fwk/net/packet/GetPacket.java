package zildo.fwk.net.packet;

import java.nio.ByteBuffer;

import zildo.fwk.file.EasyBuffering;
import zildo.fwk.net.Packet;
import zildo.fwk.net.packet.AskPacket.ResourceType;

public class GetPacket extends Packet {

	public int length;
	public String name;
	private ByteBuffer buffer;
	public ResourceType resourceType;
	
	/**
	 * Empty constructor (called by {@link Packet#receive(java.nio.ByteBuffer)})
	 */
	public GetPacket() {
		super();
	}
	
	public GetPacket(ResourceType p_resType, ByteBuffer p_buffer, String p_name) {
		length=p_buffer.position();
		buffer=p_buffer;
		name=p_name==null?"dummy":p_name;
		resourceType=p_resType;
		buffer.flip();
	}
	
	@Override
    protected void buildPacket() {
        buffer.position(0);

        b.put(resourceType.toString());
        b.put(length);
        b.put(name);
        b.put(buffer);
    }
	
	protected void deserialize(EasyBuffering p_buffer) {
		resourceType=ResourceType.fromString(p_buffer.readString());
		length=p_buffer.readInt();
		name=p_buffer.readString();
		buffer=ByteBuffer.allocate(length);
		buffer.put(p_buffer.getAll());
		buffer.flip();
	}
	
	public ByteBuffer getBuffer() {
		return buffer;
	}

}
