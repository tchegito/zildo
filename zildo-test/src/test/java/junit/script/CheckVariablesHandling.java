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

package junit.script;

import org.junit.Assert;
import org.junit.Test;

import tools.EngineScriptUT;
import zildo.fwk.file.EasyBuffering;
import zildo.fwk.script.context.IEvaluationContext;
import zildo.fwk.script.context.LocaleVarContext;
import zildo.fwk.script.context.SpriteEntityContext;
import zildo.fwk.script.logic.FloatExpression;
import zildo.fwk.script.logic.FloatVariable.NoContextException;
import zildo.fwk.script.model.ZSSwitch;
import zildo.monde.Game;
import zildo.monde.sprites.SpriteEntity;
import zildo.server.EngineZildo;
import zildo.server.state.ScriptManagement;

/**
 * Test about global variables.
 * 
 * @author Tchegito
 *
 */
public class CheckVariablesHandling extends EngineScriptUT {

	String basicXML="<adventure>"+
					" <scene id='test1'>"+
					"  <var name='stolenMoney' value='48'/>" +
					"  <var name='double' value='stolenMoney*2'/>" +
					" </scene>"+
					"</adventure>";
	
	String secondXML="<adventure>"+
					 " <scene id='test2'>"+
					 "  <var name='stolenMoney' value='12'/>" +
					 " </scene>"+
					 "</adventure>";
	
	String thirdXML="<adventure>"+
					" <scene id='test3'>"+
					"  <var name='fishWater' value='0'/>"+
					"  <var name='fishWater' value='fishWater+1'/>"+
					"  <var name='fishWater' value='fishWater+2'/>"+
					"  <if exp='fishWater=3'>" +
					"   <var name='result' value='1'/>"+
					"  </if>"+
					" </scene>"+
					"</adventure>";
	
	@Test
	public void assignation() throws Exception {
		
		loadXMLAsString(basicXML);
		
		executeScene("test1");
		while (scriptMgmt.isScripting()) {
			scriptMgmt.render();
		}
		
		Assert.assertEquals("48.0", scriptMgmt.getVariables().get("stolenMoney"));
		scriptMgmt.render();
		Assert.assertEquals("96.0", scriptMgmt.getVariables().get("double"));
	}
	
	@Test
	public void saveVariables() throws Exception {
		loadXMLAsString(basicXML);
		
		executeScene("test1");
		while (scriptMgmt.isScripting()) {
			scriptMgmt.render();
		}
		
		EasyBuffering buffer = new EasyBuffering();
		EngineZildo.game.heroName="tirou";	// Random
		EngineZildo.game.getTimeSpent();
		EngineZildo.game.serialize(buffer);
		
		EngineZildo.scriptManagement = new ScriptManagement();
		scriptMgmt = EngineZildo.scriptManagement;
		
		Game.deserialize(buffer, false);
		Assert.assertEquals("48.0", scriptMgmt.getVariables().get("stolenMoney"));
	}
	
	@Test
	public void overrideGlobal() throws Exception {
		loadXMLAsString(basicXML);
		loadXMLAsString(secondXML);
		
		executeScene("test1");
		while (scriptMgmt.isScripting()) {
			scriptMgmt.render();
		}
		
		executeScene("test2");
		while (scriptMgmt.isScripting()) {
			scriptMgmt.render();
		}
		
		EasyBuffering buffer = new EasyBuffering();
		EngineZildo.game.heroName="tirou";	// Random
		EngineZildo.game.getTimeSpent();
		EngineZildo.game.serialize(buffer);
		
		EngineZildo.scriptManagement = new ScriptManagement();
		scriptMgmt = EngineZildo.scriptManagement;
		
		Game.deserialize(buffer, false);
		// Check that saved game overrides global values
		Assert.assertEquals("12.0", scriptMgmt.getVariables().get("stolenMoney"));
	}
	
	@Test
	public void testIncrementAndTest() throws Exception {
		loadXMLAsString(thirdXML);
		
		executeScene("test3");
		while (scriptMgmt.isScripting()) {
			scriptMgmt.render();
		}
		
		Assert.assertEquals("3.0", scriptMgmt.getVariables().get("fishWater"));
		Assert.assertEquals("1.0", scriptMgmt.getVariables().get("result"));
	}
	
	
	@Test 
	public void compareSwitchAndFloatExpression() {
		// 1) first expression
		String expr = "dice10>5:128,0";
		FloatExpression fExpr = new FloatExpression(expr);
		ZSSwitch zsExpr = ZSSwitch.parseForDialog(expr);
		int result = zsExpr.evaluateInt();
		Assert.assertTrue(result == 0 || result == 128);
		boolean error = false;
		try {
			// "dice10" is forbidden with FloatExpression
			fExpr.evaluate(null);
		} catch (NoContextException e) {
			error = true;
		}
		Assert.assertTrue(error);
		
		// 2) Give it a try with another expression
		String varName = LocaleVarContext.VAR_IDENTIFIER + "val";
		expr = "(" + varName + ">2) *128";
		fExpr = new FloatExpression(expr);
		//zsExpr = ZSSwitch.parseForDialog(expr);
		IEvaluationContext ctx = new SpriteEntityContext(new SpriteEntity());
		String idxName = ctx.registerVariable(varName);
		scriptMgmt.getVariables().put(idxName, "3");
		Assert.assertEquals(128, (int) fExpr.evaluate(ctx));
		zsExpr.evaluateInt();
	}
}
