package zildo.fwk.input;

import zildo.fwk.file.EasyBuffering;
import zildo.fwk.file.EasySerializable;
import zildo.monde.util.Vector2f;
import zildo.resource.KeysConfiguration;

public class MovementRecord implements EasySerializable {
	
	public long frame;
	final public byte key;
	final public Vector2f angle;
		
	public MovementRecord(long nFrame, byte key, Vector2f angle) {
		this.frame = nFrame;
		this.key = key;
		this.angle = angle;
	}
	
	@Override
	public void serialize(EasyBuffering p_buffer) {
		p_buffer.put((int) frame);
		p_buffer.put(key);
		if (angle == null) {
			p_buffer.put(-1f);
			p_buffer.put(-1f);
		} else {
			p_buffer.put(angle.x);
			p_buffer.put(angle.y);
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(frame).append("[");
		if (angle != null) {
			sb.append(angle);
		}
		if (key != -1) {
			sb.append(KeysConfiguration.values()[key]);
		}
		sb.append("]");
		return sb.toString();
	}
	
	public static MovementRecord deserialize(EasyBuffering p_buffer) {
		int frame = p_buffer.readInt();
		byte key = p_buffer.readByte();
		float x = p_buffer.readFloat();
		float y = p_buffer.readFloat();
		Vector2f dir = (x==-1f && y==-1f) ? null : new Vector2f(x, y);
		return new MovementRecord(frame, key, dir);
	}
}
