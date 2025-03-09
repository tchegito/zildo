package junit.script;

import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import tools.SimpleEngineScript;
import zildo.fwk.script.context.LocaleVarContext;
import zildo.fwk.script.context.SpriteEntityContext;
import zildo.fwk.script.logic.FloatExpression;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoNJ;
import zildo.server.EngineZildo;

public class CheckScriptVariables extends SimpleEngineScript {

	/** Check combination test operator "|" and "&" respectively "OR" and "AND" operators. **/ 
	@Test
	public void conditionalIf() {
		FloatExpression expr = new FloatExpression("a=7 | a=14");
		Assert.assertEquals("OR(EQUALS(a, 7.0), EQUALS(a, 14.0))", expr.toString());
		
		Map<String, String> globalVariables = scriptMgmt.getVariables();

		// 1.1: a=0
		globalVariables.put("a", "0");
		Assert.assertEquals(0, (int) expr.evaluate(null));
		// 1.2: a=7
		globalVariables.put("a", "7");
		Assert.assertEquals(1, (int) expr.evaluate(null));
		// 1.3: a=14
		globalVariables.put("a", "14");
		Assert.assertEquals(1, (int) expr.evaluate(null));
		// 1.4: a=15
		globalVariables.put("a", "15");
		Assert.assertEquals(0, (int) expr.evaluate(null));
		
		// 2.1: a=15, b=4
		globalVariables.put("b", "4");
		expr = new FloatExpression("a=15 & b=3");
		Assert.assertEquals("AND(EQUALS(a, 15.0), EQUALS(b, 3.0))", expr.toString());
		Assert.assertEquals(0, (int) expr.evaluate(null));
		// 2.2: a=15, b=3
		globalVariables.put("b", "3");
		Assert.assertEquals(1, (int) expr.evaluate(null));
		// 2.3: a=9, b=3
		globalVariables.put("a", "9");
		Assert.assertEquals(0, (int) expr.evaluate(null));
		
		// 3.1: a=9, b=6
		globalVariables.put("b", "6");
		expr = new FloatExpression("a=9 | b=6");
		Assert.assertEquals(1, (int) expr.evaluate(null));
		
		// 4.1: a!=8
		expr = new FloatExpression("a!=8");
		Assert.assertEquals(1, (int) expr.evaluate(null));

		// 4.2: a!=9
		expr = new FloatExpression("a!=9");
		Assert.assertEquals("NOT_EQUALS(a, 9.0)", expr.toString());
		Assert.assertEquals(0, (int) expr.evaluate(null));

		// 5: minus first
		expr = new FloatExpression("-a");
		Assert.assertEquals("MINUS(0.0, a)", expr.toString());
		Assert.assertEquals(-9, (int) expr.evaluate(null));
	}
		
	@Test
	public void localVariables() {
		FloatExpression expr = new FloatExpression("loc:arg0");
		Perso p = new PersoNJ();
		LocaleVarContext c = new SpriteEntityContext(p);
		String key = c.registerVariable("loc:arg0");
		scriptMgmt.putVarValue(key, "138");
		Assert.assertEquals(138f, expr.evaluate(c), 0);
		
		// Same with minus sign before
		expr = new FloatExpression("-loc:arg0");
		Assert.assertEquals(-138f, expr.evaluate(c), 0);
		
	}
	
	@Override
	@After
	public void tearDown() {
		EngineZildo.persoManagement = null;
		EngineZildo.scriptManagement = null;
	}
}
