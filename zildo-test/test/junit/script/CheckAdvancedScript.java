/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
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

package junit.script;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import zildo.fwk.script.logic.FloatExpression;
import zildo.fwk.script.logic.IEvaluationContext;
import zildo.fwk.script.logic.SpriteEntityContext;
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
public class CheckAdvancedScript {
	
	
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
		FloatExpression expr;
		float ret;
		
		expr = new FloatExpression("0+3.5*2+1");
		ret = expr.evaluate(null);
		Assert.assertTrue("Value should have been 8f instead of "+ret, ret == 8f);
		
		expr = new FloatExpression("0+3.5*2-1");
		ret = expr.evaluate(null);
		Assert.assertTrue("Value should have been 6f instead of "+ret, ret == 6f);
		
		expr = new FloatExpression("1-(2+3+4)+12");
		ret = expr.evaluate(null);
		Assert.assertTrue("Value should have been 4f instead of "+ret, ret == 4f);
	}
	
	@Test
	public void negativeValue() {
		FloatExpression expr;

		expr = new FloatExpression("-3.5*2+1");
		float ret = expr.evaluate(null);
		Assert.assertTrue("Value should have been -6f instead of "+ret, ret == -6f);

		expr = new FloatExpression("0+-3.5*2+1");
		ret = expr.evaluate(null);
		Assert.assertTrue("Value should have been -6f instead of "+ret, ret == -6f);

		expr = new FloatExpression("0-3.5*2+1");
		ret = expr.evaluate(null);
		Assert.assertTrue("Value should have been -6f instead of "+ret, ret == -6f);
		
		expr = new FloatExpression("1-(-2*3)-(5-3)+(-1+2)");
		ret = expr.evaluate(null);
		Assert.assertTrue("Value should have been 6f instead of "+ret, ret == 6f);
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
		IEvaluationContext context = new SpriteEntityContext(entity);
		FloatExpression expr;
		
		expr = new FloatExpression("x+8");
		float ret = expr.evaluate(context);
		Assert.assertTrue(ret == 168f);

		expr = new FloatExpression("y-4");
		ret = expr.evaluate(context);
		Assert.assertTrue(ret == 96f);

		expr = new FloatExpression("2*(x+y)-40/2");
		ret = expr.evaluate(context);
		Assert.assertTrue("Value should have been 500 instead of "+ret, ret == 500f);

		long t1 = System.nanoTime();
		expr = new FloatExpression("158+y/3-(x+4)*(y+3)");
		ret = expr.evaluate(context);
		Assert.assertTrue("Value should have been -16700.666f instead of "+ret, ret == -16700.666f);
		long t2 = System.nanoTime();
		
		System.out.println("time = " + (t2-t1) / 1000 + " microsecond for evaluating "+expr);
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
		IEvaluationContext context = new SpriteEntityContext(perso);
		FloatExpression expr;
		float ret;
		
		expr = new FloatExpression("attente=2");
		ret = expr.evaluate(context);
		Assert.assertTrue(ret == 0);
		
		perso.setAttente(2);
		ret = expr.evaluate(context);
		Assert.assertTrue(ret == 1);
		
		expr = new FloatExpression("attente<8");
		ret = expr.evaluate(context);
		Assert.assertTrue(ret == 1);

		expr = new FloatExpression("attente>15");
		ret = expr.evaluate(context);
		Assert.assertTrue(ret == 0);

		expr = new FloatExpression("attente>0");
		ret = expr.evaluate(context);
		Assert.assertTrue(ret == 1);
	}
	
	@Test
	public void optimized() {
		FloatExpression expr;
		
		expr = new FloatExpression(3.5f);
		Assert.assertTrue(expr.isImmediate());

		expr = new FloatExpression("3.5f");
		Assert.assertTrue(expr.isImmediate());
		
		expr = new FloatExpression("3.5+1.2");
		Assert.assertTrue(expr.isImmediate());
		
		expr = new FloatExpression("1+8*random");
		Assert.assertTrue(!expr.isImmediate());
	}
	
	@Before
	public void setup() {
		FloatExpression.OPTIMIZE = true;		
	}
}
