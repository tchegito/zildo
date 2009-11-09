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
	String playerName;
	
	/**
	 * Empty constructor (called by {@link Packet#receive(java.nio.ByteBuffer)})
	 */
	public ConnectPacket() {
		super();
	}
	
	public ConnectPacket(boolean p_connect, String p_playerName) {
		super();
		connect=p_connect;
		playerName=p_playerName;
	}
	
	public boolean isJoining() {
		return connect;
	}
	
	public String getPlayerName() {
		return playerName;
	}
	
	@Override
	protected void buildPacket() {
		b.put(connect);
		b.put(playerName);
	}

	protected void deserialize(EasyBuffering p_buffer) {
		connect=p_buffer.readBoolean();
		playerName=p_buffer.readString();
	}
}
