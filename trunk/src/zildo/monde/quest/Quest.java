package zildo.monde.quest;

import zildo.monde.sprites.persos.PersoZildo;

/**
 * @author eboussaton
 */
public abstract class Quest {

    // TODO: multi quest

    String name;
    boolean done;
    MapReplacement transformations;

    public Quest(String p_name, MapReplacement p_transfMap) {
        name = p_name;
        done = false;
        transformations = new MapReplacement(p_transfMap);
    }

    public boolean isAccomplished() {
        return done;
    }

    public void accomplish() {
        done = true;
    }

    public MapReplacement getTransformMap() {
        return transformations;
    }
    
    public abstract boolean checkTrigger(QuestEvent p_event, PersoZildo p_zildo);
}