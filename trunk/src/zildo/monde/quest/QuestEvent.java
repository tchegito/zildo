package zildo.monde.quest;

public enum QuestEvent {

    LOCATION, DIALOG, INVENTORY, QUESTDONE;
    
    
    public static QuestEvent fromString(String p_name) {
    	for (QuestEvent kind : values()) {
    		if (kind.toString().equalsIgnoreCase(p_name)) {
    			return kind;
    		}
    	}
    	return null;
    }
}
