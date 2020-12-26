package zildo.fwk.script.logic;

import zildo.fwk.ZUtils;
import zildo.fwk.script.context.IEvaluationContext;
import zildo.fwk.script.logic.FloatVariable.NoContextException;
import zildo.fwk.script.model.point.PointEvaluator;
import zildo.monde.Trigo;
import zildo.monde.map.Area;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.util.Point;
import zildo.monde.util.Pointf;
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
		case "angle":	// Returns angle in radian between 2 named characters
			Perso npc1 = EngineZildo.persoManagement.getNamedPersoInContext(params[0].variable, context);
			Perso npc2 = EngineZildo.persoManagement.getNamedPersoInContext(params[1].variable, context);
			return (float) Trigo.getAngleRadian(npc1.x, npc1.y, npc2.x, npc2.y);
		case "project":	// Returns location with (String npc, float angle, int pixelRadius)
			Perso npc = EngineZildo.persoManagement.getNamedPersoInContext(params[0].variable, context);
			float alpha = params[1].evaluate(context);
			int radius = (int) params[2].evaluate(context);
			Pointf p = new Pointf(npc.x, npc.y);
			p.add(Trigo.vect(alpha, radius));
			// We can't return location (x,y) in just a float, so 2 solutions at least:
			// 1) return a bitwise float, like for example x on 10 first bits, and y on next ones
			// 2) set a named variable, instead of returning something. Name could be provided in input,
			//		"project(bandit, alpha, 64, 'returnVal')"
			return PointEvaluator.toSingleFloat(p);
		case "collide": // Returns TRUE is location (in pixel coordinate) is free from collision
			float loc = params[0].evaluate(context);
			Point pointLoc = PointEvaluator.fromFloat(loc);
			int result = EngineZildo.mapManagement.collide(pointLoc.x, pointLoc.y, (Element) context.getActor()) ? 1 : 0;
			System.out.println("collision at "+pointLoc+" gives "+result);
			return result;
		case "dist": // Returns distance between two float locations
			Point p1 = PointEvaluator.fromFloat(params[0].evaluate(context));
			Point p2 = PointEvaluator.fromFloat(params[1].evaluate(context));
			float dist = Point.distance(p1.x,  p1.y,  p2.x,  p2.y);;
			System.out.println("Distance between "+p1+" and "+p2+" is "+dist);
			return dist;
			default:
				throw new RuntimeException("Unable to find builtIn function "+funName);
		}
	}
	
	@Override
	public String toString() {
		return funName + "(" + ZUtils.arrayToString(params) + ")";
	}
}
