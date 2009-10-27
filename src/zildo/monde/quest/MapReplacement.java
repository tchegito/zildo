package zildo.monde.quest;

import java.util.HashMap;

/**
 * Just a simple class to encapsulate a <String, String> map.
 * It provides map (=area) replacement according to a quest status. (See {@link QuestDiary})
 * @author tchegito
 *
 */
public class MapReplacement extends HashMap<String, String> {

	private static final long serialVersionUID = 1L;

	public MapReplacement() {
        super();
    }

    public MapReplacement(MapReplacement p_replaces) {
        putAll(p_replaces);
    }
}
