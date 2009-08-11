package zildo.fwk.net.packet;

import zildo.fwk.file.EasyBuffering;
import zildo.fwk.net.Packet;

public class EventPacket extends Packet {

	public enum EventType {
		DIALOG_ENDED;
	}
	
	public EventType type;
	
	/**
	 * Empty constructor (called by {@link Packet#receive(java.nio.ByteBuffer)})
	 */
	public EventPacket() {
		super();
	}
	
	public EventPacket(EventType p_type) {
		type=p_type;
	}
	
	protected void buildPacket() {
		b.put(type.toString());
	}
	
	protected void deserialize(EasyBuffering p_buffer) {
		type=EventType.valueOf(p_buffer.readString());
	}
}
