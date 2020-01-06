package zildo.fwk.script.logic;

import zildo.fwk.ZUtils;
import zildo.fwk.script.context.IEvaluationContext;
import zildo.fwk.script.logic.FloatVariable.NoContextException;
import zildo.monde.map.Area;
import zildo.server.EngineZildo;

public class FloatBuiltIn implements FloatASTNode {

	String funName;
	FloatVariable[] params;
	
	/** Called with "fun:<name>" as first parameter and "<val1>,<val2>..." as second **/
	public FloatBuiltIn(String funName, String strParams) {
		this.funName = funName.substring(FloatExpression.RESERVED_WORD_FUN.length());
		String[] values = strParams.split(",");
		params = new FloatVariable[values.length];
		int i=0;
		for (String s : values) {
			params[i++] = new FloatVariable(s.trim());
		}
	}

	@Override
	public float evaluate(IEvaluationContext context) {
		if (context == null) {
			throw new NoContextException();
		}
		switch (funName) {
		case "mapFloor":
			// Evaluate parameters and call functions
			Area map = EngineZildo.mapManagement.getCurrentMap();
			int xx = (int) params[0].evaluate(context) / 16;
			int yy = (int) params[1].evaluate(context) / 16;
			return Math.max(map.getHighestCaseFloor(xx, yy), map.getHighestCaseFloor(xx, yy -1));
			default:
				throw new RuntimeException("Unable to find builtIn function "+funName);
		}
	}
	
	@Override
	public String toString() {
		return funName + "(" + ZUtils.arrayToString(params) + ")";
	}
}
