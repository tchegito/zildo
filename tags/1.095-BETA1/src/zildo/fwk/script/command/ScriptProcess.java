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

package zildo.fwk.script.command;

import java.util.ArrayList;
import java.util.List;

import zildo.fwk.script.xml.element.ActionElement;
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
	public ActionExecutor actionExec;					// Delegate object designed for rendering actions
	public SceneElement scene;
	public boolean finalEvent;	// TRUE=send NOEVENT at the end of the script execution / FALSE=nothing
	
	PersoZildo duplicateZildo;
	
	List<ActionElement> currentActions=new ArrayList<ActionElement>();

	public ScriptProcess(SceneElement p_scene, ScriptExecutor p_scriptExecutor, boolean p_finalEvent) {
		scene=p_scene;
		cursor=0;
		actionExec=new ActionExecutor(p_scriptExecutor);
		
		if (scene.restoreZildo) {
			PersoZildo zildo = EngineZildo.persoManagement.getZildo();
			if (zildo != null) {
				duplicateZildo=(PersoZildo) EngineZildo.persoManagement.getZildo().clone();
			}
		}
	}
	
	/**
	 * Return the current node, based on the cursor value.
	 * @return AnyElement
	 */
	public ActionElement getCurrentNode() {
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
}
