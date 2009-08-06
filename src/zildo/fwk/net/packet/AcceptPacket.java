package zildo.fwk.net.packet;

import zildo.fwk.file.EasyBuffering;
import zildo.fwk.net.Packet;

/**
 * Server send an acknowledgement to client for him to join the game.
 * 
 * Send a new Zildo's id by the way.
 * 
 * @author tchegito
 *
 */
public class AcceptPacket extends Packet {

	public int zildoId;
	
	public AcceptPacket() {
	}
	
	public AcceptPacket(int p_zildoId) {
		zildoId=p_zildoId;
	}
	
	@Override
	protected void buildPacket() {
		b.put(zildoId);
	}
	
	protected void deserialize(EasyBuffering p_buffer) {
		zildoId=p_buffer.readInt();
	}

}
