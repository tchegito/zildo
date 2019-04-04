package zildo.server.state;

import zildo.fwk.script.context.IEvaluationContext;
import zildo.fwk.script.context.LocaleVarContext;
import zildo.fwk.script.logic.FloatExpression;
import zildo.server.EngineZildo;

/**
 * Class modelizing a script call. It could be a tileAction, persoAction or classic scene.
 * 
 * We extract arguments passed to this script, and store a future context, once we get ready to evaluate arguments
 * from the right context. 
 */
public class ScriptCall {

	public final String name;	// Origin name = <scene name>(<arguments separated by comma>)
	public final String actionName;
	public final String[] args;
	// A script could be a "scene", "persoAction" or "tileAction". Last ones are focused on Perso and Tile
	// So we store here the future context for the called script, pre-created around the right object (Perso or Tile)
	public final IEvaluationContext futureContext;	
	
	public ScriptCall(String p_call, IEvaluationContext p_futureContext) {
		name = p_call;	// Origin name will be kept, to be able to stop on demand

		// Parse action name for arguments
		int posParenthese = name.indexOf('(');
		if (posParenthese != -1) {
			actionName = name.substring(0, posParenthese);
			String argStr= name.substring(posParenthese+1, name.indexOf(')'));
			args = argStr.split(",");
		} else {
			// No parameters
			actionName = name;
			args = null;
		}
		futureContext = p_futureContext;
	}
	
	/** Register arguments passed to this call. Can be optionally resolved from a caller context. **/
	public void registerVariables(IEvaluationContext context, IEvaluationContext callerContext) {
		if (args != null) {
			int nVar = 0;
			for (String arg : args) { 
				String varName = context.registerVariable(LocaleVarContext.VAR_IDENTIFIER + "arg"+nVar);
				String value = arg.trim();
				// If variable was local to the caller context, resolve it
				if (value.startsWith(LocaleVarContext.VAR_IDENTIFIER) && callerContext != null && callerContext.hasVariables()) {
					// If variable was local to the caller context, resolve it
					value = "" + new FloatExpression(value).evaluate(callerContext);
				} else 
				
				// 1) alphanumeric => store as string
				if (value.startsWith("'") && value.endsWith("'")) {
					value = value.substring(1, value.length()-1);
				} else if (value.startsWith("*")) {
					value = context.getString(value.substring(1));
				} else {
					value = "" + new FloatExpression(arg).evaluate(context);
				}
				EngineZildo.scriptManagement.putVarValue(varName, value);
				nVar++;
			}
		}		
	}
}
