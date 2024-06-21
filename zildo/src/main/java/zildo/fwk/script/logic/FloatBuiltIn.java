package zildo.fwk.script.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
	FloatASTNode[] params;
	
	/** Called with "fun:<name>" as first parameter and "<val1>,<val2>..." as second **/
	public FloatBuiltIn(String funName, FloatASTNode fparams) {
		this.funName = funName.substring(FloatExpression.RESERVED_WORD_FUN.length());

		List<FloatASTNode> res = decompose(fparams);
		params = res.toArray(new FloatASTNode[res.size()]);
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
			Perso npc1 = EngineZildo.persoManagement.getNamedPersoInContext(variable(0), context);
			Perso npc2 = EngineZildo.persoManagement.getNamedPersoInContext(variable(1), context);
			return (float) Trigo.getAngleRadian(npc1.x, npc1.y, npc2.x, npc2.y);
		case "project":	// Returns location with (String npc, float angle, int pixelRadius)
			Perso npc = EngineZildo.persoManagement.getNamedPersoInContext(variable(0), context);
			float alpha = params[1].evaluate(context);
			int radius = (int) params[2].evaluate(context);
			Pointf p = new Pointf(npc.x, npc.y);
			p.add(Trigo.vect(alpha, radius));
			// We can't return location (x,y) in just a float, so 2 solutions at least:
			// 1) return a bitwise float, like for example x on 10 first bits, and y on next ones
			// 2) set a named variable, instead of returning something. Name could be provided in input,
			//		"project(bandit, alpha, 64, 'returnVal')"
			return PointEvaluator.toSingleFloat(p);
		case "collide": // Returns TRUE is location (in pixel coordinate) is free from collision args=(loc)
			float loc = params[0].evaluate(context);
			Point pointLoc = PointEvaluator.fromFloat(loc);
			int result = EngineZildo.mapManagement.collide(pointLoc.x, pointLoc.y, (Element) context.getActor()) ? 1 : 0;
			//System.out.println("collision at "+pointLoc+" gives "+result);
			return result;
		case "persoloc":
			npc = EngineZildo.persoManagement.getNamedPersoInContext(variable(0), context);
			
			return PointEvaluator.toSingleFloat(new Pointf(npc.x, npc.y));
		case "dist": // Returns distance between two float locations args=(loc1, loc2)
			Point p1 = PointEvaluator.fromFloat(params[0].evaluate(context));
			Point p2 = PointEvaluator.fromFloat(params[1].evaluate(context));
			return distance(p1, p2);
		case "loc":
			float lx = params[0].evaluate(context);
			float ly = params[1].evaluate(context);
			return PointEvaluator.toSingleFloat(new Pointf(lx, ly));
		case "lineCollide": // Returns TRUE if there's no obstacle between two locations args=(a.x, a.y, b.x, b.y)
							// Also exists with (loc1, loc2)
			if (params.length == 4) {
				p1 = new Point(params[0].evaluate(context), params[1].evaluate(context));
				p2 = new Point(params[2].evaluate(context), params[3].evaluate(context));
			} else {
				p1 = PointEvaluator.fromFloat(params[0].evaluate(context));
				p2 = PointEvaluator.fromFloat(params[1].evaluate(context));
			}
			final int nbIterations = 10;
			Pointf vec = new Pointf(p2.x - p1.x, p2.y - p1.y);
			vec.mul(1 / (float)nbIterations);
			Pointf point = new Pointf(p1);
			for (int i=0;i<nbIterations;i++) {
				if (EngineZildo.mapManagement.collide(point.x, point.y, null)) {
					return 1;
				}
				point.add(vec);
			}
			return 0;
			default:
				throw new RuntimeException("Unable to find builtIn function "+funName);
		}
	}
	
	private List<FloatASTNode> decompose(FloatASTNode node) {
		List<FloatASTNode> result = new ArrayList<>();
		if (node instanceof FloatOperator && ((FloatOperator)node).op == Operator.SEPARATOR) {
			FloatOperator fo = (FloatOperator) node;
			result.addAll(decompose(fo.operand1));
			result.addAll(decompose(fo.operand2));
		} else {
			return Collections.singletonList(node);
		}
		return result;
	}
	
	private String variable(int i) {
		FloatASTNode v = params[i];
		if (!(v instanceof FloatVariable)) {
			throw new RuntimeException("Variable "+i+" from "+funName+" should be a FloatVariable !");
		}
		return ((FloatVariable) v).variable;
	}
	
	private float distance(Point p1, Point p2) {
		float dist = Point.distance(p1.x,  p1.y,  p2.x,  p2.y);;
		return dist;
	}
	
	@Override
	public String toString() {
		return funName + "(" + ZUtils.arrayToString(params) + ")";
	}
}
