package zildo.monde.dialog;

import zildo.fwk.file.EasyBuffering;
import zildo.fwk.file.EasySerializable;

public class HistoryRecord implements EasySerializable {

	public final String key;
	public final String who;
	public final String mapName;
	
	
	public HistoryRecord(String key, String who, String mapName) {
		this.key = key;
		this.who = who;
		this.mapName = mapName;
	}

	@Override
	public void serialize(EasyBuffering p_buffer) {
		p_buffer.put(key);
		p_buffer.put(who);
		p_buffer.put(mapName);
	}

	public static HistoryRecord deserialize(EasyBuffering p_buffer) {
		return new HistoryRecord(p_buffer.readString(), 
				p_buffer.readString(), 
				p_buffer.readString());
	}
}
