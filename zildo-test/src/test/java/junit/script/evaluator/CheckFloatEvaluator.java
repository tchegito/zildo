/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package junit.script.evaluator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import zildo.fwk.script.context.IEvaluationContext;
import zildo.fwk.script.context.SpriteEntityContext;
import zildo.fwk.script.logic.FloatExpression;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoNJ;

/**
 * Check the float expression evaluator with variables handling.<br/>
 * 
 * For example : <li>
 * <ul>x+8</ul>
 * <ul>dice(10) > 4 ? a : b</ul>
 * </li>
 * @author Tchegito
 *
 */
public class CheckFloatEvaluator {
	
	IEvaluationContext context = null;
	
	@Test
	public void simpleWithSpaces() {
		FloatExpression expr;
		
		expr = new FloatExpression("x + 8");
		Assert.assertEquals("PLUS(x, 8.0)", expr.toString());

		expr = new FloatExpression("x + 8 * 3.5");
		Assert.assertEquals("PLUS(x, MULTIPLY(8.0, 3.5))", expr.toString());

		expr = new FloatExpression("(x + 8) * 3.5");
		Assert.assertEquals("MULTIPLY(PLUS(x, 8.0), 3.5)", expr.toString());
		
		expr = new FloatExpression("x + 1.2 * random");
		Assert.assertEquals("PLUS(x, MULTIPLY(1.2, random))", expr.toString());
	}
	
	@Test
	public void distributivity() {
		assertExpr("0+3.5*2+1", 8f);
		
		assertExpr("0+3.5*2-1", 6f);
		
		assertExpr("1-(2+3+4)+12", 4f);
	}
	
	@Test
	public void negativeValue() {
		assertExpr("-3.5*2+1", -6f);

		assertExpr("0+-3.5*2+1", -6f);

		assertExpr("0-3.5*2+1", -6f);
		
		assertExpr("1-(-2*3)-(5-3)+(-1+2)", 6f);
		
	}

	@Test
	public void negate() {
		// We'll check the NOT_EQUALS operator. Note that it does make sense only with EQUALS operator.
		// Conceptually, it should have been NOT operator and be usable in any kind of expression as a 1 operand combination.
		// But in this simplified engine, we only consider 2 operand combinations.
		assertExpr("1!=0", 1f);
		
		assertExpr("1=0", 0f);
		
		assertExpr("8!=0", 1f);
		assertExpr("8!=8", 0f);
		
		boolean error = false;
		try {
			new FloatExpression("!1=0");
		} catch (RuntimeException e) {
			error = true;
		}
		Assert.assertTrue("Error should have been detected in expression instead of ", error);
	}
	
	private void assertExpr(String stringExpression, float expected) {
		assertExpr(stringExpression, "Value should have been "+expected+" !", expected);
	}
	
	/** Assert that expression evaluates to expected value, given the class member {@link #context} **/
	private void assertExpr(String stringExpression, String errorMessage, float expected) {
		FloatExpression expr = new FloatExpression(stringExpression);
		float ret = expr.evaluate(context);
		
		Assert.assertTrue(errorMessage+" " + ret+" [expr: "+stringExpression+"] [AST: "+expr+"]", ret == expected);
	}
	
	@Test
	public void simpleWithoutSpaces() {
		FloatExpression expr;
		
		expr = new FloatExpression("x+8");
		Assert.assertEquals("PLUS(x, 8.0)", expr.toString());
		
		expr = new FloatExpression("x+8*3.5");
		Assert.assertEquals("PLUS(x, MULTIPLY(8.0, 3.5))", expr.toString());
		
	}
	
	@Test
	public void complex() {
		FloatExpression expr = new FloatExpression("x+(8*3.5*random+(2-1)/3)");
		Assert.assertEquals("PLUS(x, PLUS(MULTIPLY(MULTIPLY(8.0, 3.5), random), DIVIDE(MINUS(2.0, 1.0), 3.0)))", expr.toString());
		
	}
	
	@Test
	public void relativeValues() {
		SpriteEntity entity = new SpriteEntity(160, 100, false);
		context = new SpriteEntityContext(entity);
		
		assertExpr("x+8", 168f);

		assertExpr("y-4", 96f);

		assertExpr("2*(x+y)-40/2", 500f);

		long t1 = System.nanoTime();
		assertExpr("158+y/3-(x+4)*(y+3)", -16700.666f);
		long t2 = System.nanoTime();
		
		System.out.println("time = " + (t2-t1) / 1000 + " microsecond for evaluating expression");
	}
	
	@Test
	public void trickyExpressions() {
		FloatExpression expr;
		FloatExpression.OPTIMIZE = false;
		
		expr = new FloatExpression("1+(2*3)+4");
		Assert.assertEquals("PLUS(PLUS(1.0, MULTIPLY(2.0, 3.0)), 4.0)", expr.toString());

		expr = new FloatExpression("1*(2+3)+4");
		Assert.assertEquals("PLUS(MULTIPLY(1.0, PLUS(2.0, 3.0)), 4.0)", expr.toString());

		expr = new FloatExpression("1-2*3+4");
		Assert.assertEquals("PLUS(MINUS(1.0, MULTIPLY(2.0, 3.0)), 4.0)", expr.toString());
		
		expr = new FloatExpression("1-(2*3)+4");
		Assert.assertEquals("PLUS(MINUS(1.0, MULTIPLY(2.0, 3.0)), 4.0)", expr.toString());
		
		expr = new FloatExpression("0+3.5*2-1");
		Assert.assertEquals("PLUS(0.0, MINUS(MULTIPLY(3.5, 2.0), 1.0))", expr.toString());
		
		expr = new FloatExpression("2*(x+y)-40/2");
		Assert.assertEquals("MINUS(MULTIPLY(2.0, PLUS(x, y)), DIVIDE(40.0, 2.0))", expr.toString());
	}

	@Test
	public void builtInFunction() {
		SpriteEntity entity = new SpriteEntity(160, 100, false);
		IEvaluationContext context = new SpriteEntityContext(entity);
		FloatExpression expr;
		float ret;
		
		expr = new FloatExpression("x+random");
		ret = expr.evaluate(context);
		Assert.assertTrue(ret >= 160 && ret <= 161);
		
		expr = new FloatExpression("dice10 + 3");
		ret = expr.evaluate(context);
		Assert.assertTrue(ret >= 3 && ret <= 13);
	}
	
	@Test
	public void comparison() {
		Perso perso = new PersoNJ();
		perso.setAttente(100);
		context = new SpriteEntityContext(perso);
		
		assertExpr("attente=2", 0f);
		
		perso.setAttente(2);
		assertExpr("attente=2", 1f);
		
		assertExpr("attente<8", 1f);

		assertExpr("attente>15", 0f);

		assertExpr("attente>0", 1f);
	}
	
	@Test
	public void minMax() {
		assertExpr("4 min 3", 3f);
		assertExpr("-12 min 3", -12f);

		assertExpr("4 max 3", 4f);
		assertExpr("-12 max 3", 3);
	}

	@Test
	public void optimized() {
		FloatExpression expr;
		
		expr = new FloatExpression(3.5f);
		Assert.assertTrue(expr.isImmediate());

		expr = new FloatExpression("3.5");
		Assert.assertTrue(expr.isImmediate());
		
		expr = new FloatExpression("3.5+1.2");
		Assert.assertTrue(expr.isImmediate());
		
		expr = new FloatExpression("1+8*random");
		Assert.assertTrue(!expr.isImmediate());
	}

	@Test
	public void builtInFunctions() {
		FloatExpression exp = new FloatExpression("fun:mapFloor(15,36)=2");
		
		Assert.assertEquals("EQUALS(mapFloor(15.0, 36.0), 2.0)", exp.toString());
	}
	
	@Test
	public void builtInWithMultiply() {
		FloatExpression exp = new FloatExpression("fun:project(bandit, 2, 3*0.5)");
		
		Assert.assertEquals("project(bandit, 2.0, MULTIPLY(3.0, 0.5))", exp.toString());
	}

	@Before
	public void setup() {
		FloatExpression.OPTIMIZE = true;		
	}
}
