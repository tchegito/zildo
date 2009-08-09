package zildo.fwk.net.packet;

import zildo.fwk.file.EasyBuffering;
import zildo.fwk.net.Packet;

/**
 * Packet intended to join/leave a game.
 * 
 * @author tchegito
 *
 */
public class ConnectPacket extends Packet {

	boolean connect;	// TRUE=client is joining / FALSE=client is leaving
	
	/**
	 * Empty constructor (called by {@link Packet#receive(java.nio.ByteBuffer)})
	 */
	public ConnectPacket() {
		super();
	}
	
	public ConnectPacket(boolean p_connect) {
		super();
		connect=p_connect;
	}
	
	public boolean isJoining() {
		return connect;
	}
	
	@Override
	protected void buildPacket() {
		b.put(connect);
	}

	protected void deserialize(EasyBuffering p_buffer) {
		connect=p_buffer.readBoolean();
	}
}
