package zildo.fwk.script.command;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import zildo.client.ClientEngineZildo;
import zildo.fwk.script.xml.ActionElement;
import zildo.fwk.script.xml.ActionsElement;
import zildo.fwk.script.xml.AnyElement;
import zildo.fwk.script.xml.SceneElement;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.persos.Perso;

public class ScriptExecutor {

	Stack<ScriptProcess> scripts = new Stack<ScriptProcess>();	// Stack of current scripts
	Set<Perso> involved=new HashSet<Perso>();	// All characters involved in script
	public boolean userEndedAction;				// TRUE if user has ended last action with ACTION keypressed

	/**
	 * Ask for engine to execute the given script.
	 * @param p_script
	 */
	public void execute(SceneElement p_script) {
		scripts.push(new ScriptProcess(p_script, this));
	}
	
	public void render() {
		if (isScripting()) {
			
			ScriptProcess process=getCurrent();
			
			AnyElement currentNode=process.getCurrentNode();
			if (currentNode == null) {
				// We reach the end of the script
				terminate();
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
			}
		}
	}

	private void terminate() {
		ScriptProcess process=scripts.pop();
		process.terminate();
		if (scripts.empty()) {
			// Get back to life the involved characters
			for (Perso p : involved) {
				p.setGhost(false);
			}
			involved.clear();
			// Focus on Zildo
			SpriteEntity zildo=ClientEngineZildo.spriteDisplay.getZildo();
			ClientEngineZildo.mapDisplay.setFocusedEntity(zildo);
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
		return !scripts.empty();
	}
	
	public ScriptProcess getCurrent() {
		return scripts.lastElement();
	}
	
	public void userEndAction() {
		userEndedAction=true;
	}
}