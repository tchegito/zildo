package zeditor.tools.checker;

public class ErrorDescription {

	static public abstract class Action {
		public abstract void run();
	}
	
	public final CheckError kind;
	public final String message;
	public final Action fixAction;	// Optional : if user confirms, this action will be launched to fix detected errors
	public final boolean autofix;
	
	public ErrorDescription(CheckError p_kind, String p_message) {
		kind = p_kind;
		message = p_message;
		fixAction = null;
		autofix = false;
	}

	public ErrorDescription(CheckError p_kind, String p_message, Action p_action) {
		kind = p_kind;
		message = p_message;
		fixAction = p_action;
		autofix = false;
	}

	public ErrorDescription(CheckError p_kind, String p_message, boolean p_autofix) {
		kind = p_kind;
		message = p_message;
		fixAction = null;
		autofix = p_autofix;
	}
}
