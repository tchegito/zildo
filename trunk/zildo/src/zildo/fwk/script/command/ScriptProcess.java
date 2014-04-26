/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
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

package zildo.fwk.script.command;

import java.util.ArrayList;
import java.util.List;

import zildo.fwk.script.logic.IEvaluationContext;
import zildo.fwk.script.xml.element.LanguageElement;
import zildo.fwk.script.xml.element.SceneElement;
import zildo.monde.sprites.persos.PersoZildo;
import zildo.server.EngineZildo;

/**
 * Object describing a script being processed by the script engine.
 * @author Tchegito
 *
 */
public class ScriptProcess {

	public int cursor;
	public ActionExecutor actionExec;	// Delegate object designed for rendering actions
	public VariableExecutor varExec;	// Delegate object designed for rendering variables operations
	public SceneElement scene;
	public boolean finalEvent;	// TRUE=send NOEVENT at the end of the script execution / FALSE=nothing
	boolean topPriority;
	
	PersoZildo duplicateZildo;
	
	List<LanguageElement> currentActions=new ArrayList<LanguageElement>();

	public ScriptProcess(SceneElement p_scene, ScriptExecutor p_scriptExecutor, 
			boolean p_finalEvent, boolean p_topPriority,
			IEvaluationContext context) {
		scene=p_scene;
		cursor=0;
		topPriority = p_topPriority;
		actionExec=new ActionExecutor(p_scriptExecutor, p_scene.locked, context);
		varExec = new VariableExecutor(p_scene.locked, context);
		finalEvent = p_finalEvent;
		
		if (scene.restoreZildo) {
			PersoZildo zildo = EngineZildo.persoManagement.getZildo();
			if (zildo != null) {
				duplicateZildo=(PersoZildo) EngineZildo.persoManagement.getZildo().clone();
			}
		}
		
		// Initialize all actions state
		p_scene.reset();
	}
	
	/**
	 * Return the current node, based on the cursor value.
	 * @return AnyElement
	 */
	public LanguageElement getCurrentNode() {
		if (cursor >= scene.actions.size()) {	// End of the list
			return null;
		} else {
			return scene.actions.get(cursor);
		}		
	}
	
	public void terminate() {
		if (scene.restoreZildo) {
			PersoZildo zildo=EngineZildo.persoManagement.getZildo();
			// Reset current zildo
			zildo.x=duplicateZildo.x;
			zildo.y=duplicateZildo.y;
			zildo.z=duplicateZildo.z;
		}
	}
	
	@Override
	public String toString() {
		return "["+cursor+" on "+scene.toString()+"]";
	}
}
