package zildo.monde.dialog;

import static zildo.client.gui.GUIDisplay.TXT_CHANGE_COLOR;

import java.util.List;

import zildo.fwk.ZUtils;
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
		StringBuilder sb = new StringBuilder("\n");
		Region currentRegion = null;
		if (records.isEmpty()) {
			return UIText.getMenuText("dialogRecords.empty");
		}
		
		for (HistoryRecord record : ZUtils.Reversed.reversed(records)) {
			Region region = Region.fromMapName(record.mapName);
			if (currentRegion != region) {
				currentRegion = region;
				sb.append(TXT_CHANGE_COLOR).append(region.getName()).append(TXT_CHANGE_COLOR+"\n\n");
			}
			if (!ZUtils.isEmpty(record.who)) {	// Eliminate name for text without speaker (sign, scenario)
				sb.append(TXT_CHANGE_COLOR).append(record.who).append(TXT_CHANGE_COLOR).append(": ");
			}
			String sentence = UIText.getGameText(record.key);
			sentence = sentence.replaceAll("(.*)\\#[0-9]", "$1");
			sentence = sentence.replaceAll("(.*)\\$sell.*", "$1");
			sentence = sentence.replaceAll("(.*)\\@.*", "$1");
			sb.append(sentence);
			sb.append("\n\n");
		}
		return sb.toString();
	}
	
	public boolean equals(Object o) {
		if (o == null || !(o instanceof HistoryRecord)) {
			return false;
		}
		HistoryRecord r = (HistoryRecord) o;
		return r.key.equals(key) &&
			   r.who.equals(who) &&
			   r.mapName.equals(mapName);
	}
	
	@Override
	public String toString() {
		return key;
	}
}
