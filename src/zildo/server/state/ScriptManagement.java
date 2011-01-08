/**
 * Legend of Zildo
 * Copyright (C) 2006-2011 Evariste Boussaton
 * Based on original Zelda : link to the past (C) Nintendo 1992
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package zildo.server.state;

import java.util.List;

import zildo.fwk.script.command.ScriptExecutor;
import zildo.fwk.script.xml.ActionElement;
import zildo.fwk.script.xml.AdventureElement;
import zildo.fwk.script.xml.QuestElement;
import zildo.fwk.script.xml.SceneElement;
import zildo.fwk.script.xml.ScriptReader;
import zildo.fwk.script.xml.TriggerElement;
import zildo.monde.quest.MapReplacement;
import zildo.monde.quest.QuestEvent;

/**
 * Delegate class which deals with script.<p/>
 * 
 * It provides two functions:<ul>
 * <li>render script (via {@link ScriptExecutor})</li>
 * <li>trigger an event</li>
 * </ul>
 * @author Tchegito
 *
 */
public class ScriptManagement {

    ScriptExecutor scriptExecutor;
    AdventureElement adventure=null;
    MapReplacement replaces;
    
    public ScriptManagement() {
        // Load adventure
        adventure=(AdventureElement) ScriptReader.loadScript("quests.xml");

        scriptExecutor=new ScriptExecutor();
        
        replaces=new MapReplacement();
    }
    
    public void render() {
    	scriptExecutor.render();
    }
    
    public boolean isScripting() {
    	return scriptExecutor.isScripting();
    }
    
    public void userEndAction() {
    	scriptExecutor.userEndAction();
    }
    
    /**
     * Execute the given named script, if it exists.
     * @param p_name
     */
    public void execute(String p_name) {
    	SceneElement scene=adventure.getSceneNamed(p_name);
    	if (scene != null) {
    		scriptExecutor.execute(scene);
    	} else {
    		throw new RuntimeException("Scene "+p_name+" doesn't exist !");
    	}
    }

    private void execute(List<ActionElement> p_actions) {
    	// Create a SceneElement from the given actions
		SceneElement scene=SceneElement.createScene(p_actions);
		// And execute this list
		scriptExecutor.execute(scene);
    }
    
    /**
     * Entry point for all identifiable action, which could target a trigger.<p/>
     * Trigger could be any kind of QuestEvent.
     * @param p_triggerElement element created by a static method from TriggerElement
     */
    public void trigger(TriggerElement p_triggerElement) {
    	// 1: check the existing triggers to potentially enable them
    	for (QuestElement quest : adventure.getQuests()) {
    		if (!quest.done) {
    			// For each quest undone yet :
    			for (TriggerElement trig : quest.getTriggers()) {
    				if (!trig.done && trig.match(p_triggerElement)) {
    					trig.done=true;
    				}
    			}
    		}
    	}
    	// 2: recheck all triggers to potentially accomplish a quest
    	for (QuestElement quest : adventure.getQuests()) {
    		if (!quest.done) {
    			// For each quest undone yet :
    			boolean achieved=true;
    			for (TriggerElement trig : quest.getTriggers()) {
    				achieved&=trig.done;
    			}
    			if (achieved) {
    				accomplishQuest(quest, true);
    			} else if (quest.isTriggersBoth()) {
    				// All trigger are not activated at the same time ==> then we reset them to 'undone'
    				for (TriggerElement trig : quest.getTriggers()) {
    					trig.done=false;
    				}
    			} else {
    				// Reset only the 'location' trigger to 'undone' (because they have to be immediate)
    				for (TriggerElement trig : quest.getTriggers()) {
    					if (QuestEvent.LOCATION == trig.kind) {
    						trig.done=false;
    					}
    				}    				
    			}
    		}
    	}
    }


    /**
     * Update quest status, and launch the associated actions.
     * @param p_questName
     * @param p_trigger
     */
    public void accomplishQuest(String p_questName, boolean p_trigger) {
    	for (QuestElement quest : adventure.getQuests()) {
    		if (quest.name.equals(p_questName)) {
    			accomplishQuest(quest, p_trigger);
    		}
    	}
    }
    
    /**
     * Update quest status, and launch the associated actions.
     * @param p_quest
     * @param p_trigger TRUE=we have to launch targeted action / FALSE=just set quest to 'done' state
     */
    private void accomplishQuest(QuestElement p_quest, boolean p_trigger) {
    	p_quest.done=true;
    	
    	// 1) note the history events (mapReplace ...)
    	List<ActionElement> history=p_quest.getHistory();
		if (history != null) {
			execute(history);
		}
		
		// 2) execute the immediate actions (only in-game)
    	if (p_trigger) {
	    	// Target potentials triggers
	    	TriggerElement trig=TriggerElement.createQuestDoneTrigger(p_quest.name);
	    	trigger(trig);
			// Execute the corresponding actions
			execute(p_quest.getActions());
    	}

    	
    }
    
    public String getReplacedMapName(String p_mapName) {
        String name = replaces.get(p_mapName);
        if (name == null) {
            return p_mapName;
        } else {
            return name;
        }
    }
    
    public void addReplacedMapName(String p_ancient, String p_new) {
    	replaces.put(p_ancient, p_new);
    }

	public AdventureElement getAdventure() {
		return adventure;
	}
}