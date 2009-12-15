package zildo.fwk.script.command;

import zildo.SinglePlayer;
import zildo.client.ClientEngineZildo;
import zildo.fwk.script.xml.ActionElement;
import zildo.fwk.script.xml.ActionsElement;
import zildo.fwk.script.xml.AnyElement;
import zildo.fwk.script.xml.ScriptElement;
import zildo.fwk.script.xml.StartElement;
import zildo.monde.map.Point;
import zildo.monde.quest.actions.ScriptAction;
import zildo.monde.sprites.persos.Perso;
import zildo.server.EngineZildo;

public class ScriptExecutor {

	ScriptElement script = null;
	int cursor;
	boolean userEndedAction;
	
	/**
	 * Ask for engine to execute the given script.
	 * @param p_script
	 */
	public void execute(ScriptElement p_script) {
		script=p_script;
		cursor=0;
	}
	
	public void render() {
		if (isScripting()) {
			AnyElement currentNode=getCurrent();
			if (currentNode == null) {
				// We reach the end of the script
				script=null;
			} else {
				Class<? extends AnyElement> clazz=currentNode.getClass();
				if (StartElement.class == clazz) {
					renderStart((StartElement) currentNode);
				} else if (ActionElement.class.isAssignableFrom(clazz)) {
					renderAction((ActionElement) currentNode, true);
				}
			}
		}
	}
	
	private void renderStart(StartElement p_start) {
		if (p_start.mapName != null) {
			EngineZildo.mapManagement.charge_map(p_start.mapName);
			ClientEngineZildo.mapDisplay.setCurrentMap(EngineZildo.mapManagement.getCurrentMap());
		}
		for (ActionElement action : p_start.startActions) {
			renderAction(action, false);
		}
		cursor++;
	}
	
	private void renderAction(ActionElement p_action, boolean p_moveCursor) {
		boolean achieved=false;
		if (p_action.getClass() == ActionsElement.class) {
			// Actions list
			ActionsElement actions=(ActionsElement) p_action;
			achieved=true;
			for (ActionElement action : actions.actions) {
				if (!action.done) {
					renderAction(action, p_moveCursor);
					achieved=achieved & action.done;
				}
			}
		} else {
			if (p_action.waiting) {
				waitForEndAction(p_action);
			} else {
				String who=p_action.who;
				Perso perso=EngineZildo.persoManagement.getNamedPerso(who);
				Point location=p_action.location;
				String text=p_action.text;
				switch (p_action.kind) {
				case pos:
					perso.x=location.x;
					perso.y=location.y;
					achieved=true;
					break;
				case moveTo:
		            perso.setGhost(true);
		            perso.setDx(location.x);
		            perso.setDy(location.y);
		            break;
				case speak:
					EngineZildo.dialogManagement.launchDialog(SinglePlayer.getClientState(), null, new ScriptAction(text));
					userEndedAction=false;
					break;
				}
				p_action.done=achieved;
				p_action.waiting=!achieved;
			}
		}

		if (p_moveCursor && achieved) {
			cursor++;
		}
	}
	
	private void waitForEndAction(ActionElement p_action) {
		String who=p_action.who;
		Perso perso=EngineZildo.persoManagement.getNamedPerso(who);
		Point location=p_action.location;
		boolean achieved=false;
		switch (p_action.kind) {
		case moveTo:
	        if (perso.x == location.x && perso.y == location.y) {
	        	perso.setGhost(false);
	        	achieved=true;
	        }
	        break;
		case speak:
			break;
		}
		p_action.waiting=!achieved;
		p_action.done=achieved;
	}
	
	public boolean isScripting() {
		return script != null;
	}
	
	/**
	 * Return the current node, based on the cursor value.
	 * @return AnyElement
	 */
	private AnyElement getCurrent() {
		if (cursor == 0) {
			return script.start;
		} else {
			if (cursor-1 >= script.actions.size()) {	// End of the list
				return null;
			} else {
				return script.actions.get(cursor - 1);
			}
		}
	}
	
	public void userEndAction() {
		userEndedAction=true;
	}
}