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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zildo.fwk.script.command.ScriptExecutor;
import zildo.fwk.script.xml.ActionElement;
import zildo.fwk.script.xml.AdventureElement;
import zildo.fwk.script.xml.QuestElement;
import zildo.fwk.script.xml.SceneElement;
import zildo.fwk.script.xml.ScriptReader;
import zildo.fwk.script.xml.TriggerElement;
import zildo.monde.items.ItemKind;
import zildo.monde.map.ChainingPoint;
import zildo.monde.map.Point;
import zildo.monde.quest.MapReplacement;
import zildo.monde.quest.QuestEvent;
import zildo.monde.quest.actions.ScriptAction;
import zildo.monde.sprites.persos.PersoZildo;
import zildo.server.EngineZildo;
import zildo.server.Server;

/**
 * Delegate class which deals with script.<p/>
 * 
 * It provides two basic functions :<ul>
 * <li>render script (via {@link ScriptExecutor})</li>
 * <li>trigger an event</li>
 * </ul>
 * and more high-level functions :<ul>
 * <li>remember opened chest</li>
 * <li>remember opened doors</li>
 * </ul>
 * 
 * @author Tchegito
 *
 */
public class ScriptManagement {

    ScriptExecutor scriptExecutor;
    AdventureElement adventure=null;
    MapReplacement replaces;
    
    final Map<String, QuestElement> questsByName;
    
    // 'LOCATION' trigs for specific location on the current map
    final List<TriggerElement> locationTriggerOnMap;	
    
    public ScriptManagement() {
        // Load adventure
        adventure=(AdventureElement) ScriptReader.loadScript("quests.xml");

        scriptExecutor=new ScriptExecutor();
        
        replaces=new MapReplacement();
        
        questsByName = new HashMap<String, QuestElement>();
        for (QuestElement quest : adventure.getQuests()) {
        	questsByName.put(quest.name, quest);
        }
        
    	locationTriggerOnMap = new ArrayList<TriggerElement>();
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
    	
    	// 0: optimization, check LOCATION trigger on a restricted list
    	if (p_triggerElement.isLocationSpecific()) {
    		boolean atLeastOne=false;
    		for (TriggerElement trig : locationTriggerOnMap) {
    			if (trig.match(p_triggerElement)) {
    				trig.done=true;
    				atLeastOne=true;
    			}
    		}
    		if (!atLeastOne) {	// No one ? So get out !
    			return;
    		}
    	}
    	
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
     * Method designed for optimization : we have to chek at each character's movement, if he activates
     * a trigger. So, this must be a fast operation.
     * @param p_mapName
     */
    public void prepareMapSubTriggers(String p_mapName) {
    	locationTriggerOnMap.clear();
    	for (QuestElement quest : adventure.getQuests()) {
    		if (!quest.done) {
    			// For each quest undone yet :
    			for (TriggerElement trig : quest.getTriggers()) {
    				if (trig.isLocationSpecific() && p_mapName.equals(trig.getName())) {
    					locationTriggerOnMap.add(trig);
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
    	boolean found=false;
    	QuestElement quest = questsByName.get(p_questName);
    	if (quest != null) {
   			accomplishQuest(quest, p_trigger);
    	} else {
	    	if (!found) { // Given quest hasn't been found, so create it (useful for automatic behaviors like chest and doors)
	    		quest=new QuestElement();
	    		quest.name=p_questName;
	    		quest.done=true;	// Set it done
	    		questsByName.put(p_questName, quest);
	    		adventure.addQuest(quest);
	    	}
    	}
    }
    
    /**
     * Update quest status, and launch the associated actions.
     * @param p_quest
     * @param p_trigger TRUE=we have to launch targeted action / FALSE=just set quest to 'done' state
     */
    private void accomplishQuest(QuestElement p_quest, boolean p_trigger) {
    	System.out.println("Accomplish "+p_quest.name);
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
		
	public void automaticBehavior(PersoZildo p_zildo, ItemKind p_kind) {
        switch (p_kind) {
        case BOW:
        	p_zildo.setCountArrow(p_zildo.getCountArrow() + 5);	// Bow comes with 5 arrows
        case BOMB:
        	p_zildo.setCountBomb(p_zildo.getCountBomb() + 5);	// 5 bombs at start
        default:
        	break;
        }
    	String label=p_kind.getFoundSentence();
    	if (label != null) {
    		EngineZildo.dialogManagement.launchDialog(Server.getClientFromZildo(p_zildo), null, new ScriptAction(label));
    	}
	}
	
	/**
	 * Returns TRUE if the given quest is marked as done.
	 * @param p_questName
	 * @return boolean
	 */
	public boolean isQuestDone(String p_questName) {
		QuestElement quest = questsByName.get(p_questName);
		if (quest == null) {
			return false;
		}
		return quest.done;
	}
	
	public boolean isOpenedChest(String p_mapName, Point p_location) {
		String questName=p_mapName+p_location.toString();
		return isQuestDone(questName);
	}
	
	public void openChest(String p_mapName, Point p_location) {
		String questName=p_mapName+p_location.toString();
		accomplishQuest(questName, false);
	}
	
	/**
	 * Build a quest's keyname about a chaining point between 2 maps.
	 * @param p_mapName
	 * @param p_ch
	 * @return String
	 */
	private String buildKeyDoor(String p_mapName, ChainingPoint p_ch) {
		String map2=p_ch.getMapname();
		String key=p_mapName.compareTo(map2) < 0 ? p_mapName+map2 : map2+p_mapName;
		key+=p_ch.getOrderX()+p_ch.getOrderY();

		return key;
	}
	
	public boolean isOpenedDoor(String p_mapName, ChainingPoint p_ch) {
		return isQuestDone(buildKeyDoor(p_mapName, p_ch));
	}
	
	public void openDoor(String p_mapName, ChainingPoint p_ch) {
		accomplishQuest(buildKeyDoor(p_mapName, p_ch), false);
	}
}