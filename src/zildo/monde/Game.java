package zildo.monde;

import zildo.monde.items.ItemKind;
import zildo.monde.quest.Quest;
import zildo.monde.quest.QuestDiary;
import zildo.monde.quest.QuestEvent;
import zildo.monde.quest.MapReplacement;
import zildo.monde.sprites.persos.PersoZildo;

/**
 * Modelizes a saved game, or start game. For now, it describes:<br/>
 * -simple game in a given map <br/>
 * -minimum management for map editing (ZEditor) <br/>
 * -deathmatch/cooperative nature<br/>
 * -current quest diary<br/>
 * @author tchegito
 */
public class Game {

    public boolean editing;
    public boolean multiPlayer;
    public boolean deathmatch; // Defines the game rules
    public String mapName;
    public QuestDiary questDiary;

    public Game(String p_mapName, boolean p_editing) {
        mapName = p_mapName;
        editing = p_editing;
        multiPlayer = false;
        questDiary = createTempQuestDiary();
    }

    /**
     * Create the "historic" zildo's adventure. Very simple.
     * @return QuestDiary
     */
    private static QuestDiary createTempQuestDiary() {
        QuestDiary diary = new QuestDiary();

        // Create the flut quest
        MapReplacement replaces = new MapReplacement();
        replaces.put("d4", "d4bis");
        replaces.put("d5m1", "d5m1bis");
        replaces.put("polaky", "polakbis");
        Quest q = new Quest("flute", replaces) {
            public boolean checkTrigger(QuestEvent p_event, PersoZildo p_zildo) {
                if (QuestEvent.INVENTORY == p_event) {
                    // Check if Gerard has given the flut to Zildo
                    return p_zildo.hasItem(ItemKind.FLUT);
                }
                return false;
            }
        };
        diary.addQuest(q);

        return diary;
    }
}
