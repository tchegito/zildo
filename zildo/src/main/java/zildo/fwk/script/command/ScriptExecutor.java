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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import zildo.Zildo;
import zildo.client.ClientEngineZildo;
import zildo.client.ClientEvent;
import zildo.client.ClientEventNature;
import zildo.fwk.script.context.IEvaluationContext;
import zildo.fwk.script.context.SpriteEntityContext;
import zildo.fwk.script.xml.element.action.ActionElement;
import zildo.fwk.script.xml.element.action.runtime.RuntimeAction;
import zildo.fwk.script.xml.element.action.runtime.RuntimeScene;
import zildo.fwk.script.xml.element.logic.VarElement;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.server.EngineZildo;

public class ScriptExecutor {

	// Stack of current scripts
	List<ScriptProcess> scripts = new ArrayList<ScriptProcess>();
	
	Set<Perso> involved=new HashSet<Perso>();	// All characters involved in script
	public boolean userEndedAction;				// TRUE if user has ended last action with ACTION keypressed
	
	boolean PROCESSING_SCRIPTS;	// TRUE if we're iterating over running scripts
	
	List<ScriptProcess> subScriptsEnded = new ArrayList<ScriptProcess>();
	Set<ScriptProcess> toTerminate = new HashSet<ScriptProcess>();
	List<ScriptProcess> toExecute = new ArrayList<ScriptProcess>();
	
	/**
	 * Ask for engine to execute the given script, with/without a NOEVENT signal at the end (bad for map scripts).
	 * @param p_script
	 * @param p_finalEvent
	 * @param p_topPriority TRUE=this script will be executed before all others
	 * @param p_context context (optional)
	 * @param p_caller process calling this new one
	 */    
	public void execute(RuntimeScene p_script, boolean p_finalEvent, boolean p_topPriority, IEvaluationContext p_context, ScriptProcess p_caller) {
		// TODO: attempt to duplicate context, need to refactor cleanly
		IEvaluationContext ctx = p_context;           
 		if (p_context != null) {
 			if (p_script.call != null && p_script.call.futureContext != null) {
 				ctx = p_script.call.futureContext;
 			} else {
 				ctx = ctx.clone();
 			}
			p_script.registerVariables(ctx, p_context);
		} else if (p_script.call != null) {
			ctx = p_script.call.futureContext;
			// We may have an NPE in the call right here, if a script is executed from 'mapscript' section
			p_script.registerVariables(ctx, null);
		}
		ScriptProcess sp = new ScriptProcess(p_script, this, p_finalEvent, p_topPriority, ctx, p_caller);
		if (p_caller != null) {
			p_caller.setSubProcess(sp);
		} else {
			if (!PROCESSING_SCRIPTS) {
				if (sp.topPriority) {	// Add priority scripts first
					toExecute.add(sp);
				} else {
					scripts.add(sp);
				}
			} else {
				// Scripts are actually processing, so keep it ready to be added during the #render method.
				toExecute.add(sp);
			}
		}
	}
	
	public String verbose() {
		StringBuilder sb = new StringBuilder();
		sb.append(scripts.size()).append(" scripts running {[");
		for (ScriptProcess process : scripts) {
			ScriptProcess s = process;
			while (s != null) {
				sb.append(s.scene.id).append(s).append(",");
				s = s.subProcess;
			}
		}
		sb.append("}");
		return sb.toString();
	}
	/**
	 * Execute a frame inside the current locking script, and all non locking ones.
	 */
	public void render() {
		if (!scripts.isEmpty()) {
			if (Zildo.infoDebugScriptVerbose) {
				System.out.println(verbose());
			}
			// 0) Terminate scripts asked by external methods (#stopFromContext for example)
			terminateIfNeeded();
			
			// 1) Render current scripts
			PROCESSING_SCRIPTS = true;
			//System.out.print("Frame:");
			for (ScriptProcess process : scripts) {

				boolean shouldGoOn = true;
				shouldGoOn = renderProcess(process);
				while (shouldGoOn) { // || process.subProcess != null) {
					shouldGoOn = renderProcess(process);
				}

				
				// Did the last action finished ? So we'll avoid GUI blinking with 1-frame long script. (issue 28)
				if (process.getCurrentNode() == null && process.currentActions.isEmpty()) {
					toTerminate.add(process);
				}
				
				// end condition: quit if this scene locks everything, unless it's a top priority one
				// In this case, there is surely another one top priority after it, and we must execute it
				if (process.scene.locked && !process.topPriority) {
					break;
				}
			}
			PROCESSING_SCRIPTS = false;
			
			// 2) Terminate those who are waiting
			terminateIfNeeded();
		}

		// 3) Create the awaiting one
		for (ScriptProcess process : toExecute) {
			int i=0;
			for (;i<scripts.size();i++) {
				if (!scripts.get(i).topPriority) {
					break;
				}
			}
			scripts.add(i, process);
		}
		toExecute.clear();
	}

	// TODO: don't modify input variable
	private boolean renderProcess(ScriptProcess process) {
		
		// Does this process have a sub process ? Check recursively
		ScriptProcess parent = null;
		while (process.subProcess != null) {
			if (subScriptsEnded.contains(process.subProcess)) {
				// This sub process has recently ended, so we cut the link from the parent
				process.subProcess.terminate();
				subScriptsEnded.remove(process.subProcess);
				process.subProcess = null;
				break;
			}
			parent = process;
			process = process.subProcess;
		}
		
		boolean shouldGoOn = false;
		RuntimeAction currentNode=process.getCurrentNode();
		if (currentNode == null) {
			if (process.currentActions.isEmpty()) { // Is there some actions waiting (those with attribute 'unblocked' at TRUE)
				// We reach the end of the script
				toTerminate.add(process);
			}
		} else {
			shouldGoOn = renderElement(process, currentNode, true);
		}

		// Render current actions too
		for (Iterator<RuntimeAction> it=process.currentActions.iterator();it.hasNext();) {
			RuntimeAction action=it.next();
			if (action.done) {	// It's done, so remove the action
				it.remove();
			} else {
				renderElement(process, action, false);
			}
		}
		
		if (process.getCurrentNode() == null) {
			if (parent != null) {
				// TODO: isn't parent.subProcess supposed to be equals to process ??? 
				if (parent.subProcess.currentActions.isEmpty() && parent.subProcess.subProcess == null) {
					parent.subProcess.terminate();
					parent.subProcess = null;
				}
			} else if (process.currentActions.isEmpty()){
				toTerminate.add(process);
			}
		}
		/*
		while (process.subProcess != null) {
			// TODO: pass only process to this sub function
			process = process.subProcess;
			//renderElement(process, process.getCurrentNode(), true);
			// Special case of process with just one line
			if (process.getCurrentNode() == null) {
				toTerminate.add(process);
			}
		} */
	
		return shouldGoOn;
	}
	
	private void terminateIfNeeded() {
		for (ScriptProcess process : toTerminate) {
			terminate(process);
		}
		toTerminate.clear();
	}
	
	/**
	 * Script just terminated.
	 * @param process
	 */
	private void terminate(ScriptProcess process) {
		boolean rootScript = scripts.remove(process);
		if (!rootScript) {
			// This script isn't in the global list, that means it's a subscript launched by another one
			subScriptsEnded.add(process);
		}
		process.terminate();
		if (!isScripting(false) && rootScript) {
			// Get back to life the involved characters
			for (Perso p : involved) {
				p.setGhost(p.getFollowing() != null);	// Cancel 'ghost' except if character is following someone
				if (p.isZildo()) {
					p.setOpen(true);
					if (p.getAttente() < 0) {
						p.setAttente(0);
					}
					PersoPlayer zildo = (PersoPlayer) p;
					zildo.setAppearance(null);
				}
				p.setUnstoppable(false);	// Reset this status
			}
			involved.clear();
			// Focus on Zildo
			SpriteEntity zildo=EngineZildo.persoManagement.getZildo();
			if (zildo != null) {
				ClientEngineZildo.mapDisplay.setFocusedEntity(zildo);
			}
			// Stop forced music
			EngineZildo.soundManagement.setForceMusic(false);

			if (process.finalEvent) {
				EngineZildo.askEvent(new ClientEvent(ClientEventNature.NOEVENT));
			}
		}
	}
	
	private void renderAction(ScriptProcess p_process, RuntimeAction p_action, boolean p_moveCursor) {
		boolean achieved=false;
		if (p_action.isMultiple()) {
			// Actions list
			achieved=true;
			for (RuntimeAction action : p_action.actions) {
				if (!action.done) {
					renderElement(p_process, action, false);
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
	
	private void renderVariable(ScriptProcess process, RuntimeAction p_elem, boolean moveCursor) {
		boolean achieved = process.varExec.render(p_elem);
		if (moveCursor && achieved) {
			process.cursor++;
		}
	}
	
	/** Render an element, wether it's an action or a variable execution. Both should make the cursor go forward **/
	private boolean renderElement(ScriptProcess process, RuntimeAction currentNode, boolean moveCursor) {
		// TODO: careful to 'actions' tag, where currentNode is not the process.currentNode
		RuntimeAction node = currentNode;
		boolean shouldGoOn = true;
		while (node != null) {
			boolean hadSubprocess = process.subProcess != null;
			boolean wasWaiting = node.waiting;
			if (!node.var) {
				//System.out.print("action => "+node);
				renderAction(process, node, moveCursor);
			} else {
				//System.out.print("var => "+node+", ");
				renderVariable(process, node, moveCursor);
			}
			
			if (node != null && node.var) {
				VarElement var = (VarElement)node.action;
				switch (var.kind) {
					case var:
						node = process.getCurrentNode();
						break;
					case _if:
						if (node.done) {
							node = process.getCurrentNode();
							shouldGoOn = wasWaiting;
							break;
						}
					default:
						shouldGoOn = false;
				}
			} else if (node != null && !node.var && node.action != null) {
				ActionElement act = (ActionElement)node.action;
				switch (act.kind) {
					case loop:
						if (node.done) {
							node = process.getCurrentNode();
							shouldGoOn = false;
							break;
						}
					case _for:
					case tile:
					case nameReplace:
						if (node.done) {
							node = process.getCurrentNode();
							break;
						}
					default:
						shouldGoOn = false;
				}
			} else {
				shouldGoOn = false;
			}
			if (!shouldGoOn) {
				if (!hadSubprocess && process.subProcess != null) {
					shouldGoOn = true;
				}
				break;
			}
		}
		return shouldGoOn;
	}
	
	public boolean isScripting() {
		return isScripting(false) || !toExecute.isEmpty();
	}
	
	/**
	 * Returns TRUE if any blocking script is going.
	 * @param onlyTopPriority TRUE=focus only on topPriority script (=mapScript)
	 * @return
	 */
	public boolean isScripting(boolean onlyTopPriority) {
		if (scripts.isEmpty()) {
			return false;
		}
		for (ScriptProcess process : scripts) {
			if (onlyTopPriority) {
				// Because topPriority is set to TRUE only for mapScript. These are the scripts triggered automatically on a new map.
				// See (<mapScript><condition>... in XML scripts)
				if (process.topPriority) {
					return true;
				}
			} else {
				// Is this script unblocking ?
				if (process.scene.locked) {
					return true;
				}
			}
		}
		return false;
	}
	
	// As we allow sometimes top priority scripts without considering game is blocked, this function is useful.
	public boolean isNonPriorityScripting() {
		if (scripts.isEmpty()) {
			return false;
		}
		for (ScriptProcess process : scripts) {
			// Is this script unblocking ?
			if (!process.topPriority && process.scene.locked) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns TRUE if given name is in the processing queue (i.e. the quest are unfinished)
	 * @param p_name
	 * @return boolean
	 */
	public boolean isProcessing(String p_name) {
		for (ScriptProcess process : scripts) {
			if (process.isNameProcessing(p_name)) {
				return true;
			}
		}
		return false;
	}
	
	public void clearUnlockingScripts() {
		for (Iterator<ScriptProcess> it = scripts.iterator();it.hasNext();) {
			ScriptProcess process = it.next();
			if (!process.scene.locked) {
				it.remove();
			}
		}
		if (!toExecute.isEmpty()) {
			toExecute.clear();
		}
	}
	
	/** Stop a running scene **/
	public void stopScene(String p_name) {
		for (ScriptProcess process : scripts) {
			if (process.isNameProcessing(p_name)) {
				toTerminate.add(process);
			}
		}
	}
	
	public void userEndAction() {
		userEndedAction=true;
	}
	
	/**
	 * Stop any scripts whose context is linked to this entity.
	 * @param entity
	 */
	public void stopFromContext(SpriteEntity entity) {
		for (ScriptProcess sp : scripts) {
			IEvaluationContext ctx = sp.actionExec.context;
			if (ctx != null && ctx instanceof SpriteEntityContext) {
				Perso perso = (Perso) ctx.getActor();
				if ( perso != null && perso.getId() == entity.getId()) {
					toTerminate.add(sp);
				}
			}
		}
	}
	
	/** Returns TRUE if this character has a running PersoAction **/
	public boolean isPersoActing(SpriteEntity entity) {
		for (ScriptProcess sp : scripts) {
			IEvaluationContext ctx = sp.actionExec.context;
			if (ctx != null && ctx instanceof SpriteEntityContext) {
				Perso perso = (Perso) ctx.getActor();
				if ( perso != null && perso.getId() == entity.getId()) {
					return true;
				}
			}
		}
		return false;
	}
}