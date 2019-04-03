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

package zildo.fwk.script.command;

import java.util.ArrayList;
import java.util.List;

import zildo.fwk.script.context.IEvaluationContext;
import zildo.fwk.script.xml.element.action.runtime.RuntimeAction;
import zildo.fwk.script.xml.element.action.runtime.RuntimeScene;
import zildo.monde.sprites.persos.PersoPlayer;
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
	public RuntimeScene scene;
	public boolean finalEvent;	// TRUE=send NOEVENT at the end of the script execution / FALSE=nothing
	boolean topPriority;
	
	ScriptProcess subProcess;	// When this script executes another one
	
	PersoPlayer duplicateZildo;
	
	List<RuntimeAction> currentActions=new ArrayList<RuntimeAction>();

	public ScriptProcess(RuntimeScene p_scene, ScriptExecutor p_scriptExecutor, 
			boolean p_finalEvent, boolean p_topPriority, IEvaluationContext p_context, ScriptProcess p_caller) {
		scene=p_scene;
		cursor=0;
		topPriority = p_topPriority;
		actionExec=new ActionExecutor(p_scriptExecutor, p_scene.locked, p_context, scene.actions.size() == 1, this);
		varExec = new VariableExecutor(p_scene.locked, p_context, this);
		finalEvent = p_finalEvent;
		
		if (scene.restoreZildo) {
			PersoPlayer zildo = EngineZildo.persoManagement.getZildo();
			if (zildo != null) {
				duplicateZildo=(PersoPlayer) EngineZildo.persoManagement.getZildo().clone();
			}
		}
	}
	
	public void setSubProcess(ScriptProcess subProcess) {
		this.subProcess = subProcess;
	}
	/**
	 * Return the current node, based on the cursor value.
	 * @return AnyElement
	 */
	public RuntimeAction getCurrentNode() {
		if (cursor >= scene.actions.size()) {	// End of the list
			return null;
		} else {
			return scene.actions.get(cursor);
		}		
	}
	
	public void terminate() {
		if (scene.restoreZildo) {
			PersoPlayer zildo=EngineZildo.persoManagement.getZildo();
			// Reset current zildo
			zildo.x=duplicateZildo.x;
			zildo.y=duplicateZildo.y;
			zildo.z=duplicateZildo.z;
		}
		actionExec.terminate();
	}
	
	/** Returns TRUE if the given name match this process, or its subprocess **/
	public boolean isNameProcessing(String p_name) {
		if (scene.call != null) {	// Try with original name, if parameters exist
			return p_name.equals(scene.call.name);
		} else if (p_name.equals(scene.id)) {
			return true;
		} else if ((RuntimeScene.MARQUER_SCENE + p_name).equals(scene.id)) {
			return true;
		}
		if (subProcess != null) {
			return subProcess.isNameProcessing(p_name);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return (topPriority ? "priority" : "") + "["+cursor+" on "+scene.toString()+"]";
	}
}
