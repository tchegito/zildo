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

package zildo.fwk.script.xml.element;

import java.util.List;

import org.xml.sax.Attributes;

import zildo.fwk.ZUtils;

public class AdventureElement extends AnyElement {

	List<SceneElement> scenes = ZUtils.arrayList();
	List<QuestElement> quests = ZUtils.arrayList();
	List<MapscriptElement> mapScripts = ZUtils.arrayList();
	List<ContextualActionElement> persoActions = ZUtils.arrayList();
	List<ContextualActionElement> tileActions = ZUtils.arrayList();
	
	
	@Override
	public void parse(Attributes p_elem) {
	}
	
	public void add(String node, AnyElement elem) {
		if ("scene".equals(node)) {
			scenes.add((SceneElement) elem);
		} else if ("quest".equals(node)) {
			quests.add((QuestElement) elem);
		} else if ("mapScript".equals(node)) {
			mapScripts.add((MapscriptElement) elem);
		} else if ("persoAction".equals(node)) {
			persoActions.add((ContextualActionElement) elem);
		} else if ("tileAction".equals(node)) {
			tileActions.add((ContextualActionElement) elem);
		}
	}

	/**
	 * Get the named scene, if it exists.
	 * @param p_name
	 * @return SceneElement
	 */
	public SceneElement getSceneNamed(String p_name) {
		for (SceneElement scene : scenes) {
			if (scene.id.equalsIgnoreCase(p_name)) {
				return scene;
			}
		}
		return null;
	}
	
	public List<QuestElement> getQuests() {
		return quests;
	}
	
	public List<SceneElement> getScenes() {
	    return scenes;
	}
	
	public List<MapscriptElement> getMapScripts() {
	    return mapScripts;
	}
	
	public List<ContextualActionElement> getPersoActions() {
	    return persoActions;
	}
	
	/**
	 * Get the named perso action, if it exists.
	 * @param p_name
	 * @return ContextualActionElement
	 */
	public ContextualActionElement getPersoActionNamed(String p_name) {
		return getContextualActionNamed(p_name, persoActions);
	}
	
	/**
	 * Get the named tile action, if it exists.
	 * @param p_name
	 * @return ContextualActionElement
	 */
	public ContextualActionElement getTileActionNamed(String p_name) {
		return getContextualActionNamed(p_name, tileActions);
	}
	
	private ContextualActionElement getContextualActionNamed(String p_name, List<ContextualActionElement> p_list) {
		for (ContextualActionElement pAction : p_list) {
			if (pAction.id.equalsIgnoreCase(p_name)) {
				return pAction;
			}
		}
		return null;
	}
	/**
	 * Manually add a quest to the adventure. (only for automatic behaviors like chest and doors)
	 * @param p_quest
	 */
	public void addQuest(QuestElement p_quest) {
		quests.add(p_quest);
	}
	
    @Override
	public void merge(AnyElement elem) {
    	if (elem != null && elem instanceof AdventureElement) {
    		// Merge 2 adventures
    		AdventureElement toMerge = (AdventureElement) elem;
    		mapScripts.addAll(toMerge.mapScripts);
    		quests.addAll(toMerge.quests);
    		scenes.addAll(toMerge.scenes);
    		persoActions.addAll(toMerge.persoActions);
    		tileActions.addAll(toMerge.tileActions);
    	}
    }
}
