package zildo.monde.dialog;

public abstract class ActionDialog {

	String text;
	
	public ActionDialog(String p_text) {
		text=p_text;
	}
	
	public abstract void launchAction();
}
