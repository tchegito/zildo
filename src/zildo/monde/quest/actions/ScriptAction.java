package zildo.monde.quest.actions;

import zildo.monde.dialog.ActionDialog;
import zildo.server.EngineZildo;

public class ScriptAction extends ActionDialog {

	public ScriptAction(String p_text) {
		super(p_text);
	}
	
	@Override
	public void launchAction() {
		EngineZildo.scriptManagement.userEndAction();
	}
}
