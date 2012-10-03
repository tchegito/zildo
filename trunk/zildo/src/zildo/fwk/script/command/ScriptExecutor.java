/**
 * Legend of Zildo
 * Copyright (C) 2006-2012 Evariste Boussaton
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import zildo.client.ClientEngineZildo;
import zildo.client.ClientEvent;
import zildo.client.ClientEventNature;
import zildo.fwk.script.xml.element.ActionElement;
import zildo.fwk.script.xml.element.ActionsElement;
import zildo.fwk.script.xml.element.AnyElement;
import zildo.fwk.script.xml.element.SceneElement;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.persos.Perso;
import zildo.server.EngineZildo;
import zildo.server.state.ScriptManagement;

public class ScriptExecutor {

	// Stack of current scripts
	List<ScriptProcess> scripts = new ArrayList<ScriptProcess>();
	
	Set<Perso> involved=new HashSet<Perso>();	// All characters involved in script
	public boolean userEndedAction;				// TRUE if user has ended last action with ACTION keypressed
	
	/**
	 *  Ask for engine to execute the given script, with/without a NOEVENT signal at the end (bad for map scripts).
	 * @param p_script
	 * @param p_finalEvent
	 * @param p_topPriority TRUE=this script will be executed before all others
	 */
	public void execute(SceneElement p_script, boolean p_finalEvent, boolean p_topPriority) {
		ScriptProcess sp = new ScriptProcess(p_script, this, p_finalEvent, p_topPriority);
		int i;
		for (i=0;i<scripts.size();i++) {
			if (!scripts.get(i).topPriority) {
				break;
			}
		}
		scripts.add(i, sp);
	}
	
	public void render() {
		if (!scripts.isEmpty()) {
			
			ScriptProcess process=getCurrent();
			
			AnyElement currentNode=process.getCurrentNode();
			if (currentNode == null) {
				// We reach the end of the script
				terminate(process);
			} else {
				Class<? extends AnyElement> clazz=currentNode.getClass();
				if (ActionElement.class.isAssignableFrom(clazz)) {
					renderAction(process, (ActionElement) currentNode, true);
				}
				
				// Render current actions too
				for (Iterator<ActionElement> it=process.currentActions.iterator();it.hasNext();) {
					ActionElement action=it.next();
					if (action.done) {	// It's done, so remove the action
						it.remove();
					} else {
						renderAction(process, action, false);
					}
				}
				
				// Did the last action finished ? So we'll avoid GUI blinking with 1-frame long script. (issue 28)
				if (process.getCurrentNode() == null) {
					terminate(process);
				}
			}
		}
	}

	/**
	 * Script just terminated.
	 * @param process TODO
	 */
	private void terminate(ScriptProcess process) {
		scripts.remove(process);
		process.terminate();
		if (!isScripting()) {
			// Get back to life the involved characters
			for (Perso p : involved) {
				p.setGhost(false);
				if (p.isZildo()) {
					p.setOpen(true);
				}
				p.setUnstoppable(false);	// Reset this status
			}
			involved.clear();
			// Focus on Zildo
			SpriteEntity zildo=EngineZildo.persoManagement.getZildo();
			ClientEngineZildo.mapDisplay.setFocusedEntity(zildo);
			// Stop forced music
			EngineZildo.soundManagement.setForceMusic(false);

			if (process.finalEvent) {
				EngineZildo.askEvent(new ClientEvent(ClientEventNature.NOEVENT));
			}
		}
	}
	
	private void renderAction(ScriptProcess p_process, ActionElement p_action, boolean p_moveCursor) {
		boolean achieved=false;
		if (p_action.getClass() == ActionsElement.class) {
			// Actions list
			ActionsElement actions=(ActionsElement) p_action;
			achieved=true;
			for (ActionElement action : actions.actions) {
				if (!action.done) {
					renderAction(p_process, action, false);
					achieved=achieved & action.done;
				}
			}
		} else {
			achieved=p_process.actionExec.render(p_action);
		}
		if (p_moveCursor) {
			if (p_action.unblock) {	// Action is unblocking, so go next, but keep it in a list
				p_process.currentActions.add(p_action);
				achieved=true;
			}
			if (achieved) {
				p_process.cursor++;
			}
		}
	}
	
	public boolean isScripting() {
		if (scripts.isEmpty()) {
			return false;
		}
		for (ScriptProcess process : scripts) {
			// Is this script unblocking ?
			if (!process.scene.unblock) {
				return true;
			}
		}
		return false;
	}
	
	public ScriptProcess getCurrent() {
		return scripts.size() == 0 ? null : scripts.get(0);
	}
	
	/**
	 * Returns TRUE if given name is in the processing queue (i.e. the quest are unfinished)
	 * @param p_name
	 * @return boolean
	 */
	public boolean isProcessing(String p_name) {
		for (ScriptProcess process : scripts) {
			if (p_name.equals(process.scene.id)) {
				return true;
			} else if ((ScriptManagement.MARQUER_SCENE + p_name).equals(process.scene.id)) {
				return true;
			}
		}
		return false;
	}
	
	public void userEndAction() {
		userEndedAction=true;
	}
}