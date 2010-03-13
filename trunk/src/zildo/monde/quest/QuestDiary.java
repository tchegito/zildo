package zildo.monde.quest;

import java.util.HashMap;
import java.util.Map;

import zildo.monde.sprites.persos.PersoZildo;

/**
 * @author eboussaton
 */
public class QuestDiary {

    Map<String, Quest> quests;


    public QuestDiary() {
        quests = new HashMap<String, Quest>();
    }

    private Quest getQuest(String p_questName) {
        Quest q = quests.get(p_questName);
        if (q == null) {
            throw new RuntimeException("Quest \"" + p_questName + "\" doesn't exist !");
        }
        return q;
    }

    public boolean isAccomplished(String p_questName) {
        return getQuest(p_questName).isAccomplished();
    }

    public void addQuest(Quest p_quest) {
        quests.put(p_quest.name, p_quest);
    }

    /**
     * Update quest status and map replacements.
     * @param p_questName
     */
    public void accomplish(String p_questName) {
        Quest q = getQuest(p_questName);
        q.accomplish();
    }

    
    /**
     * Check every quest to trigger with the given event.
     * @param p_event
     * @param p_zildo
     */
    public void trigger(QuestEvent p_event, PersoZildo p_zildo) {
        for (Quest q : quests.values()) {
            if (q.checkTrigger(p_event, p_zildo)) {
                accomplish(q.name);
            }
        }
    }
}