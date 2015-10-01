package zildo.monde.dialog;

import java.util.List;

import zildo.fwk.file.EasyBuffering;
import zildo.fwk.file.EasySerializable;
import zildo.fwk.ui.UIText;
import zildo.monde.map.Region;

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
	
	public static String getDisplayString(List<HistoryRecord> records) {
		StringBuilder sb = new StringBuilder();
		Region currentRegion = null;
		for (HistoryRecord record : records) {
			Region region = Region.fromMapName(record.mapName);
			if (currentRegion != region) {
				currentRegion = region;
				sb.append(region.getName()).append("\n");
			}
			sb.append((char)-4).append(record.who).append((char)-3).append(": ");
			sb.append(UIText.getGameText(record.key));
			sb.append("\n\n");
		}
		return sb.toString();
	}
}
