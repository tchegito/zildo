package zildo.monde.quest;

import static zildo.client.gui.GUIDisplay.TXT_CHANGE_COLOR;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Properties;

import zildo.fwk.script.model.ZSSwitch;
import zildo.fwk.ui.UIText;
import zildo.monde.sprites.persos.ControllablePerso;
import zildo.server.EngineZildo;

public class QuestLog {

	static Properties questLog = null;
	
	public QuestLog() {
		if (questLog == null) {
	        String filename = "zildo/resource/script/questLog.properties";
	        questLog = new Properties();
	        InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(filename);
	        if (stream == null) {
	        	stream = QuestLog.class.getClassLoader().getResourceAsStream(filename);
	        }
	        try { 
	        	questLog.load(stream);
	        } catch (IOException e) {
	        	
	        }
		}
	}
	
	public String getDisplayString() {
		boolean isSquirrel = EngineZildo.scriptManagement.isQuestDone(ControllablePerso.QUEST_DETERMINING_APPEARANCE);
		
		String key = "questLog";
		if (isSquirrel) key+=".roxy";
		String txt = UIText.getGameText(key);
		
		StringBuilder sb = new StringBuilder(TXT_CHANGE_COLOR+txt+TXT_CHANGE_COLOR+"\n\n");
		StringBuilder sbSecondary = new StringBuilder();
		
		
		for (Entry<Object, Object> s : questLog.entrySet()) {
			key = (String) s.getKey();
			String value = (String) s.getValue();
			boolean roxyQuest = key.startsWith("roxy");
			// Roxy and hero has their own quests
			if ((isSquirrel && !roxyQuest) || (!isSquirrel && roxyQuest)) {
				continue;
			}
			if (ZSSwitch.parseForScript(value).evaluateInt() != 0) {
				String label = UIText.getGameText(key)+"\n";
				if (key.startsWith("ql_s")) {
					sbSecondary.append(label);
				} else {
					sb.append(label);
				}
			}
		}
		
		if (sbSecondary.length() > 0) {
			sb.append("\n"+TXT_CHANGE_COLOR+UIText.getGameText("questLog.secondary")+TXT_CHANGE_COLOR+"\n\n");
			sb.append(sbSecondary);
		}
		
		return sb.toString();
	}
}
