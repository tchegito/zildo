package zildo.fwk.script.command;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import zildo.client.ClientEngineZildo;
import zildo.fwk.script.xml.ActionElement;
import zildo.fwk.script.xml.ActionsElement;
import zildo.fwk.script.xml.AnyElement;
import zildo.fwk.script.xml.SceneElement;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.persos.Perso;

public class ScriptExecutor {

	SceneElement script = null;
	int cursor;
	boolean userEndedAction;
	Set<Perso> involved=new HashSet<Perso>();
	ActionExecutor actionExec;
	
	List<ActionElement> currentActions=new ArrayList<ActionElement>();
	
	/**
	 * Ask for engine to execute the given script.
	 * @param p_script
	 */
	public void execute(SceneElement p_script) {
		script=p_script;
		cursor=0;
		actionExec=new ActionExecutor(this);
	}
	
	public void render() {
		if (isScripting()) {
			AnyElement currentNode=getCurrent();
			if (currentNode == null) {
				// We reach the end of the script
				terminate();
			} else {
				Class<? extends AnyElement> clazz=currentNode.getClass();
				if (ActionElement.class.isAssignableFrom(clazz)) {
					renderAction((ActionElement) currentNode, true);
				}
				
				// Render current actions too
				for (Iterator<ActionElement> it=currentActions.iterator();it.hasNext();) {
					ActionElement action=it.next();
					if (action.done) {	// It's done, so remove the action
						it.remove();
					} else {
						renderAction(action, false);
					}
				}
			}
		}
	}

	private void terminate() {
		// Get back to life the involved characters
		for (Perso p : involved) {
			p.setGhost(false);
		}
		involved.clear();
		currentActions.clear();
		script=null;
		actionExec=null;
		// Focus on Zildo
		SpriteEntity zildo=ClientEngineZildo.spriteDisplay.getZildo();
		ClientEngineZildo.mapDisplay.setFocusedEntity(zildo);
	}
	
	private void renderAction(ActionElement p_action, boolean p_moveCursor) {
		boolean achieved=false;
		if (p_action.getClass() == ActionsElement.class) {
			// Actions list
			ActionsElement actions=(ActionsElement) p_action;
			achieved=true;
			for (ActionElement action : actions.actions) {
				if (!action.done) {
					renderAction(action, false);
					achieved=achieved & action.done;
				}
			}
		} else {
			achieved=actionExec.render(p_action);
		}
		if (p_moveCursor) {
			if (p_action.unblock) {	// Action is unblocking, so go next, but keep it in a list
				currentActions.add(p_action);
				achieved=true;
			}
			if (achieved) {
				cursor++;
			}
		}
	}
	
	public boolean isScripting() {
		return script != null;
	}
	
	/**
	 * Return the current node, based on the cursor value.
	 * @return AnyElement
	 */
	private AnyElement getCurrent() {
		if (cursor >= script.actions.size()) {	// End of the list
			return null;
		} else {
			return script.actions.get(cursor);
		}
	}
	
	public void userEndAction() {
		userEndedAction=true;
	}
}