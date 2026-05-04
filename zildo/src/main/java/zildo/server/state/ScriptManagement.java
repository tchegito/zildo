/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
 * 
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

import zildo.Zildo;
import zildo.client.sound.Ambient.Atmosphere;
import zildo.fwk.ZUtils;
import zildo.fwk.script.command.ScriptExecutor;
import zildo.fwk.script.command.ScriptProcess;
import zildo.fwk.script.context.IEvaluationContext;
import zildo.fwk.script.context.LocaleVarContext;
import zildo.fwk.script.context.SceneContext;
import zildo.fwk.script.context.SpriteEntityContext;
import zildo.fwk.script.context.TileLocationContext;
import zildo.fwk.script.xml.ScriptReader;
import zildo.fwk.script.xml.element.AdventureElement;
import zildo.fwk.script.xml.element.ConditionElement;
import zildo.fwk.script.xml.element.ContextualActionElement;
import zildo.fwk.script.xml.element.LanguageElement;
import zildo.fwk.script.xml.element.MapscriptElement;
import zildo.fwk.script.xml.element.QuestElement;
import zildo.fwk.script.xml.element.SceneElement;
import zildo.fwk.script.xml.element.TriggerElement;
import zildo.fwk.script.xml.element.action.runtime.RuntimeScene;
import zildo.monde.items.Item;
import zildo.monde.items.ItemKind;
import zildo.monde.items.StoredItem;
import zildo.monde.map.ChainingPoint;
import zildo.monde.quest.QuestEvent;
import zildo.monde.quest.StringReplacement;
import zildo.monde.quest.actions.TakingItemAction;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.util.Point;
import zildo.server.EngineZildo;
import zildo.server.Server;

 /** 
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
    StringReplacement areaReplaces;
    StringReplacement zikReplaces;
    StringReplacement nameReplaces;
    
    public final KeyQuest keyQuest;
    
    final Map<String, QuestElement> questsByName;
    
    final Map<String, String> variables;
    
    // 'LOCATION' trigs for specific location on the current map
    final List<TriggerElement> locationTriggerOnMap;	
    // Triggers used to accept/reject a chaining point
    final List<QuestElement> chainingTriggerQuestOnMap;	

    boolean planComputeTriggers;

    /**
     * Build a script management object.<br/>
     * Note that each time player loads/restarts a game, a new instance of this object is created.<br/>
     * So we have not to reinitialize all quest 'done' states, because previous object is garbage collected.
     */
    public ScriptManagement() {
        // Load adventure
        adventure=(AdventureElement) ScriptReader.loadScript("common", "global", "episode1", "episode2", "episode3", "episode4");

        scriptExecutor=new ScriptExecutor();
        
        areaReplaces=new StringReplacement();
        zikReplaces=new StringReplacement();
        nameReplaces = new StringReplacement();
        
        questsByName = new HashMap<String, QuestElement>();
        for (QuestElement quest : adventure.getQuests()) {
        	questsByName.put(quest.name, quest);
        }
        
        variables = new HashMap<String, String>();
        
    	locationTriggerOnMap = new ArrayList<TriggerElement>();
    	chainingTriggerQuestOnMap = new ArrayList<QuestElement>();
    	
    	execute("setup", true);
    	
    	planComputeTriggers = false;
    	
    	keyQuest = new KeyQuest();
    }
    
    /** Returns an instant photography of running scripts, with cursor positions, for CrashReporter **/
    public String verbose() {
    	return scriptExecutor.verbose();
    }
    
    Boolean cached_isScripting;
    
    public void render() {
    	cached_isScripting = null;
    	scriptExecutor.render();
    	
    	if (planComputeTriggers) {
    		computeTriggers();
			cached_isScripting = null;	// News scripts could have been added
    		planComputeTriggers = false;
    	}
    }
    
    public boolean isScripting() {
    	if (cached_isScripting == null) {
    		cached_isScripting = scriptExecutor.isScripting();
    	}
    	return cached_isScripting;
    }
    
    /** Is any script blocking (except top priority ones) **/
    public boolean isBlockedScripting() {
    	return scriptExecutor.isScripting(false);
    }
    
    public boolean isPriorityScripting() {
    	return scriptExecutor.isScripting(true);
    }
    
    public void userEndAction() {
    	scriptExecutor.userEndAction();
    }
    
    /**
     * Execute the given named script, if it exists, and it's not already running.
     * @param p_name
     * @param p_locked TRUE=default:blocking scene / FALSE=non blocking scene
     */
    public void execute(String p_name, boolean p_locked) {
    	execute(p_name, p_locked, new SceneContext(), null);
    }
    
    public void execute(String p_name, boolean p_locked, IEvaluationContext p_context, ScriptProcess p_caller) {
    	if (Zildo.infoDebugScript) {
    		System.out.println("Execute scene "+p_name);
    	}
    	ScriptCall call = new ScriptCall(p_name, null);
    	SceneElement scene=adventure.getSceneNamed(call.actionName);
    	if (scene != null) {
    		// Allow a script with arguments to be launched even if same name is already processing
    		 if (call.args != null || !scriptExecutor.isProcessing(call.actionName)) {
				scriptExecutor.execute(new RuntimeScene(scene, p_locked, call), true, false, p_context, p_caller);
			 	cached_isScripting = null;
			 }
    	} else {
    		throw new RuntimeException("Scene "+call.actionName+" doesn't exist !");
    	}
    }

    /**
     * Execute a sequence of actions with given attributes.
     * @param p_actions
     * @param p_finalEvent TRUE=get back event nature to NOEVENT
     * @param p_quest (optional)
     * @param p_topPriority
     * @param p_context (optional)
     * @param p_locked used if no quest is provided
     */
    public void execute(List<LanguageElement> p_actions, boolean p_finalEvent, QuestElement p_quest, boolean p_topPriority, 
    		IEvaluationContext p_context, boolean p_locked) {
    	execute(p_actions, p_finalEvent, p_quest, p_topPriority, p_context, p_locked, null);
    }
   
    public void execute(List<LanguageElement> p_actions, boolean p_finalEvent, QuestElement p_quest, boolean p_topPriority, 
    		IEvaluationContext p_context, boolean p_locked, ScriptProcess p_caller) {
    	// Create a RuntimeScene from the given actions
    	boolean locked = p_locked;
		if (p_caller != null) {	// Sub process locking should match with main one
			locked = p_caller.scene.locked;
		}
		RuntimeScene scene=new RuntimeScene(p_actions, p_quest, locked, null);
		// And execute this list
		scriptExecutor.execute(scene, p_finalEvent, p_topPriority, p_context, p_caller);
		cached_isScripting = null;
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
    			if (!trig.done && trig.match(p_triggerElement)) {
    				trig.done=true;
    				atLeastOne=true;
    			}
    		}
    		if (!atLeastOne) {	// No one ? So get out !
    			return;
    		}
    	}
    	
    	// Check the existing triggers to potentially enable them
    	for (QuestElement quest : adventure.getQuests()) {
    		if (!quest.done) {
    			// For each quest undone yet :
    			for (TriggerElement trig : quest.getTriggers()) {
    				if (!trig.done && trig.match(p_triggerElement)) {
    					trig.done = true;
    				}
    			}
    		}
    	}
    	
    	// Plan to check all triggers on next frame beginning
    	planComputeTriggers = true;
    }
    
    /**
     * Called just after a frame where triggers have been activated. We'll check here if all triggers
     * inside a quest are accomplished, and reset those who need to be.
     */
    public void computeTriggers() {
    	// Recheck all triggers to potentially accomplish a quest
    	for (QuestElement quest : adventure.getQuests()) {
    		if (!quest.done) {
    			// For each quest undone yet :
    			boolean achieved = quest.getTriggers().size() != 0;
    			for (TriggerElement trig : quest.getTriggers()) {
    				boolean trigDone = trig.isDone();
    				achieved&=trigDone;
    				if (!trigDone) break;	// No need to continue on current quest's triggers
    			}
    			// Particular case : no triggers at all IS NOT considered as achieved
    			if (achieved) {
    				accomplishQuest(quest, true);
    			} else if (quest.isTriggersBoth()) {
    				// All trigger are not activated at the same time ==> then we reset them to 'undone'
    				for (TriggerElement trig : quest.getTriggers()) {
    					trig.done=false;
    				}
    			} else {
    				// Reset only the 'location','dialog', 'use' and 'fall' trigger to 'undone' 
    				// (because they have to be immediate)
    				for (TriggerElement trig : quest.getTriggers()) {
    					if (trig.done && trig.isImmediate()){
    						switch (trig.kind) {
    						case LOCATION:
	    						TriggerElement currentMapTrigger = EngineZildo.mapManagement.getCurrentMapTrigger();
	    						if (trig.isLocationSpecific() || !trig.match(currentMapTrigger)) {
	    							trig.done = false;
	    						}
	    						break;
    						case DIALOG:
    						case USE:
    						case FALL:
    							trig.done = false;
    						default:
    							break;
    						}
    					}
    				}    				
    			}
    		}
    	}
		//System.out.println(cntIsDone+" passages dans trig.isDone");
    }

    /**
     * Method designed for optimization : we have to check at each character's movement, if he activates
     * a trigger. So, this must be a fast operation.
     * Here we isolate all triggers eligible to a specific location on the current map.
     * @param p_mapName
     */
    public void prepareMapSubTriggers(String p_mapName) {
    	locationTriggerOnMap.clear();
    	chainingTriggerQuestOnMap.clear();
    	for (QuestElement quest : adventure.getQuests()) {
			// For each quest undone yet :
    		if (quest.getTriggers() != null) {
				for (TriggerElement trig : quest.getTriggers()) {
					if (trig.isChainingPointAcceptance()) {
						quest.done = false;
						chainingTriggerQuestOnMap.add(quest);
					}
					// Add trigger whatever quest status (done or not)
					if (/*!quest.done &&*/ trig.isLocationSpecific() && trig.mapNameMatch(p_mapName)) {
						locationTriggerOnMap.add(trig);
						//break;
					}
				}
    		}
    	}
    }

    /** Returns TRUE if the chaining point is accepted, or rejected.
     * That means that we can cancel player changing map according to quest's triggers. **/
    public boolean acceptChainingPoint() {
    	for (QuestElement quest : chainingTriggerQuestOnMap) {
    		if (!quest.done) {
    			boolean match = true;
    			for (TriggerElement trig : quest.getTriggers()) {
    				match &= trig.isDone() || trig.isChainingPointAcceptance();
    			}
    			if (match) {
    				// Execute quest
    				quest.done = true;
    				execute(quest.getActions(), false, quest, false, null, quest.locked);
        			return false;
    			}
    		}
    	}
    	return true;
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
    		if (!quest.done) { // Don't accomplish an already done quest !
    			accomplishQuest(quest, p_trigger);
    		}
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
    	if (Zildo.infoDebugScript) {
    		System.out.println("Accomplish "+p_quest.name);
    	}
   		p_quest.done=true;
    	

		
		// 1) execute the immediate actions (only in-game)
    	if (p_trigger) {
	    	// Target potentials triggers
	    	TriggerElement trig=TriggerElement.createQuestDoneTrigger(p_quest.name);
	    	trigger(trig);
			// Execute the corresponding actions
			execute(p_quest.getActions(), p_quest.locked, p_quest, false, new SceneContext(), true);
    	}

    	// 2) note the history events (mapReplace ...)
    	// It will be executed AFTER the entire script in 'actions' tag
    	List<LanguageElement> history=p_quest.getHistory();
		if (history != null) {
			execute(history, true, null, false, null, true);
		}
    	
    }
    
    /**
     * Declare a quest "undone". (used for quests with attribute "repeat")
     * @param p_questName
     */
    public void resetQuest(String p_questName) {
    	QuestElement quest = questsByName.get(p_questName);
    	if( quest != null) {
    		quest.done = false;
    		for (TriggerElement trigger : quest.getTriggers()) {
    			trigger.reset();
    		}
    	}
    }
    
    public String getReplacedMapName(String p_mapName) {
        return areaReplaces.getValue(p_mapName);
    }
    
    public String getReplacedZikName(String p_zikName) {
        return zikReplaces.getValue(p_zikName);
    }
    
    public String getReplacedPersoName(String p_persoName) {
    	return nameReplaces.getValue(p_persoName);
    }
    
    public void addReplacedMapName(String p_ancient, String p_new) {
    	areaReplaces.put(p_ancient, p_new);
    }

    public void addReplacedZikName(String p_ancient, String p_new) {
    	zikReplaces.put(p_ancient, p_new);
    }
    
    public void addReplacedPersoName(String p_ancient, String p_new) {
    	String[] names = p_ancient.split(",");
    	for (String s : names) {
    		nameReplaces.put(s, p_new);
    	}
    }
    
	public AdventureElement getAdventure() {
		return adventure;
	}
		
	/**
	 * Executes some automatic script when Zildo picks an item up.
	 * @param p_zildo
	 * @param p_kind
	 */
	public void automaticBehavior(PersoPlayer p_zildo, Element p_element) { //ItemKind p_kind, ElementDescription p_desc) {
		if (isScripting()) {
			return;	// Go away, because a script has already taken the lead
		}
		ElementDescription p_desc = (ElementDescription) p_element.getDesc();
		ItemKind p_kind = p_desc.getItem();
		String label;
		String add="";
		if (p_kind == null) {
			label = p_desc.getFoundSentence(add);
		} else {
			switch (p_kind) {
				// TODO:check if heart_fragment2 is a possibility
		    	//if (p_desc == ElementDescription.HEART_FRAGMENT || p_desc == ElementDescription.HEART_FRAGMENT2) {
				case MOON:
		    		// Specific for moon fragment
		    		if (!p_zildo.hasItem(ItemKind.NECKLACE)) {
		    			add="0";
		    		} else if (p_zildo.getMoonHalf() >= 2) {
		    			add="3";
		    		} else if (p_zildo.getMoonHalf() == 1) {
		    			add="2";
		    		}
		    		break;
		        case BOW:
		        	p_zildo.setCountArrow(p_zildo.getCountArrow() + 5);	// Bow comes with 5 arrows
		        case DYNAMITE:
		        	p_zildo.setCountBomb(p_zildo.getCountBomb() + 5);	// 5 bombs at start
		        default:
		        	break;
		    }
	    	label=p_kind.getFoundSentence(add);
		}
    	if (label != null) {
    		ClientState client = Server.getClientFromZildo(p_zildo);
    		boolean infoDialog = true;
    		if (p_desc == ElementDescription.KEY) {
    			String questName = "found" + p_desc;
    			if (!isQuestDone(questName)) {
    				accomplishQuest(questName, false);
    			} else {
    				infoDialog = false;
    			}
    		}
    		if (infoDialog) {
    			EngineZildo.dialogManagement.launchDialog(client, null, new TakingItemAction(label, p_element));
    		}
    	}
	}
	
	public void runPersoAction(Perso perso, String name, IEvaluationContext p_callerContext, boolean p_topPriority) {
		ScriptCall call = new ScriptCall(name, new SpriteEntityContext(perso));
		
		ContextualActionElement action = adventure.getPersoActionNamed(call.actionName);
		if (action != null) {
			// Register action argument(s)
			RuntimeScene scene=new RuntimeScene(action.actions, null, false, call);
			scriptExecutor.execute(scene, true, p_topPriority, p_callerContext, null);
			perso.setAttente(action.duration);
		}
	}
	
	public void runTileAction(Point loc, String name, boolean locked) {
		ContextualActionElement action = adventure.getTileActionNamed(name);
		TileLocationContext context = new TileLocationContext(loc);
		// Tile action are default topPriority. But this can be canceled by "unblocked" attribute
		execute(action.actions, false, null, locked, context, false);
	}
	
	public void stopPersoAction(Perso perso) {
		scriptExecutor.stopFromContext(perso);
	}
	
	public boolean isPersoActing(Perso perso) {
		return scriptExecutor.isPersoActing(perso);
	}
	public void stopScene(String name) {
		scriptExecutor.stopScene(name);
	}
	
	/**
	 * Clear all scripts that are non blocking (example: PersoAction, or moving platforms)
	 */
	public void clearUnlockingScripts() {
		scriptExecutor.clearUnlockingScripts();
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
	
	/**
	 * Returns TRUE if the given quest is done and its actions are achieved.
	 * @param p_questName
	 * @return boolean
	 */
	public boolean isQuestOver(String p_questName) {
		boolean done = isQuestDone(p_questName);
		if (done && isScripting()) {
			// Check if this quest is currently active
			return !scriptExecutor.isProcessing(p_questName);
		}
		return done;
	}
	
	public boolean isQuestProcessing(String p_questName) {
		return scriptExecutor.isProcessing(p_questName);
	}
	
	/** Is an action already done on this tile ? **/
	public boolean isTileDone(String p_mapName, Point p_location) {
		return isQuestDone(keyQuest.buildTileLoc(p_mapName, p_location));
	}
	
	/** Do an action on a tile: opens a chest, or take nettle **/
	public void actOnTile(String p_mapName, Point p_location) {
		accomplishQuest(keyQuest.buildTileLoc(p_mapName, p_location), true);
	}
	
	public boolean isOpenedDoor(String p_mapName, ChainingPoint p_ch) {
		return isQuestDone(keyQuest.buildKeyDoor(p_mapName, p_ch));
	}
	
	public void openDoor(String p_mapName, ChainingPoint p_ch) {
		accomplishQuest(keyQuest.buildKeyDoor(p_mapName, p_ch), true);
	}
	
	public boolean isTakenItem(String p_mapName, Point p_location, String p_persoName, ElementDescription p_desc) {
		if (p_location == null && p_persoName == null) {
			throw new RuntimeException("Location or character's name should be provided on map "+p_mapName+" for element "+p_desc+" !");
		}
		String questName;
		if (p_location != null) {
			questName = keyQuest.buildKeyItem(p_mapName, p_location, null, p_desc);
		} else {
			questName = keyQuest.buildKeyItem(p_mapName, null, p_persoName, p_desc);
		}
		return isQuestDone(questName);
	}
	
	public void explodeWall(String p_mapName, Point p_location) {
		accomplishQuest(keyQuest.buildExplosion(p_mapName,  p_location), true);
	}
	
	public boolean isExplodedWall(String p_mapName, Point p_location) {
		return isQuestDone(keyQuest.buildExplosion(p_mapName, p_location));
	}
	
	/**
	 * Take an item : accomplish a corresponding quest name.
	 * @param p_mapName
	 * @param p_x
	 * @param p_y
	 * @param p_desc
	 */
	public void takeItem(String p_mapName, Point p_location, String p_persoName, ElementDescription p_desc) {
		accomplishQuest(keyQuest.buildKeyItem(p_mapName, p_location, p_persoName, p_desc), false);
	}

	public void execMapScript(String p_mapName, Atmosphere p_atmo, boolean p_scroll) {
		for (MapscriptElement mapScript : adventure.getMapScripts()) {
			for (ConditionElement condi : mapScript.getConditions()) {
				if (condi.match(p_mapName, p_scroll)) {
			    	if (Zildo.infoDebugScript) {
			    		System.out.println(condi);
			    	}
					// Execute the 'mapscript' before all, with topPriority=TRUE
					execute(condi.getActions(), false, null, true, null, true);
				}
			}
		}
	}
	
	/**
	 * Returns TRUE if we can propose blue drop to the player.<br/>
	 * It's allowed only when player got the necklace.<br/>
	 * @return boolean
	 */
	public boolean isBlueDropDisplayable() {
		PersoPlayer zildo = EngineZildo.persoManagement.getZildo();
		return (zildo != null && zildo.hasItem(ItemKind.NECKLACE));
	}
	
	public Map<String, String> getVariables() {
		return variables;
	}
	
	public String getVarValue(String p_varName) {
		if (variables == null || variables.size() == 0) {
			return null;
		}
		return variables.get(p_varName);
	}
	
	/** Semantic method, to differentiate get/set into variables map. **/
	public void putVarValue(String p_varName, String p_value) {
		variables.put(p_varName, p_value);
	}
	
	/**
	 * Returns TRUE if player can save his game.<br/>
	 * This is forbidden in two cases : when a script is running, and when hero is on a platform.
	 * An extra case has been added: when hero doesn't exist yet.
	 * @return boolean
	 */
	public boolean isAllowedToSave() {
		PersoPlayer zildo = EngineZildo.persoManagement.getZildo();
		if (zildo == null) return false;
		boolean onPlatform = zildo != null && zildo.isOnPlatform();
		boolean isBossFighting = "1.0".equals(EngineZildo.scriptManagement.getVarValue("bossFighting"));
		return !scriptExecutor.isScripting(false) && !onPlatform && !isBossFighting;
	}
	
	public void sellItem(String storeName, Item item) {
		String itemsAsString = getVarValue(storeName);
		List<StoredItem> sellingItems = StoredItem.fromString(itemsAsString);
		for (StoredItem sItem : sellingItems) {
			if (sItem.item.kind == item.kind){
				sItem.decrements();
				break;
			}
		}
		getVariables().put(storeName, ZUtils.listToString(sellingItems));
	}
	
	public void initForNewMap() {
		// Reinit counters
		LocaleVarContext.clean();
		// Reinit all trigger with 'dead' because it only counts on the current map
    	for (QuestElement quest : adventure.getQuests()) {
    		if (!quest.done) {
    			for (TriggerElement trig : quest.getTriggers()) {
    				if (trig.kind == QuestEvent.DEAD) {
    					trig.done = false;
    				}
    			}
    		} else if (quest.isRepeatable()) {
            	// Reinit all repeatable quests as undone
    			quest.done = false;
    		}
    	}
	}
	
	public List<String> getAccomplishedQuests() {
		List<String> quests = new ArrayList<String>();
		for (QuestElement quest : adventure.getQuests()) {
			if (quest.done) {
				quests.add(quest.name);
			}
		}
		return quests;
	}
}