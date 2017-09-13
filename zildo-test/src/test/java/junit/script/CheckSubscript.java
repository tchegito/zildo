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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import tools.EngineScriptUT;
import zildo.fwk.script.xml.ScriptReader;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.action.ScriptedPersoAction;
import zildo.server.EngineZildo;
import zildo.server.PersoManagement;

/**
 * @author Tchegito
 *
 */
public class CheckSubscript extends EngineScriptUT {
	
	/** Check that each subscript is fully executed before returning to caller **/
	@Test
	public void oneSubCall() {
		scriptMgmt.getAdventure().merge(ScriptReader.loadScript("junit/script/subscript"));

		waitEndOfScripting();
		executeScene("caller");
		waitEndOfScripting();
		
		Assert.assertEquals("3.0", scriptMgmt.getVariables().get("state"));
	}
	
	@Test
	public void loop() {
		scriptMgmt.getAdventure().merge(ScriptReader.loadScript("junit/script/loops"));
		
		waitEndOfScripting();
		int nbSprites = countSprites();
		executeScene("handMadeFor");
		waitEndOfScripting();

		// Check that "spawn" has been called 16 times
		Assert.assertEquals(16,  countSprites() - nbSprites);
	}

	@Test
	public void checkFor() {
		checkForScript("builtInFor");
	}

	@Test
	public void checkNestedFor() {
		checkForScript("forInActions");
		// Make sure following actions after 'for' have been executed
		Assert.assertEquals("1.0", EngineZildo.scriptManagement.getVarValue("goodToGo"));
	}

	// Try to find a bug occuring with turret boss, when 'for' loops variable exceeds its limit. Consequence was
	// turret always shooting bullets on hero, without waiting between a burst.
	@Test
	public void checkForInLoop() {
		checkNumberVariablesWithScene("forInLoop", 2);
	}
	
	private void checkNumberVariablesWithScene(String sceneName,int nbAuthorized) {
		
		scriptMgmt.getAdventure().merge(ScriptReader.loadScript("junit/script/loops"));
		
		waitEndOfScripting();
		executeScene(sceneName);
		Map<String, String> vars = scriptMgmt.getVariables();
		int startLength = vars.size();
		boolean waitForResetI = false;
		int nbCycle = 0;
		while (nFrame < 5000) {
			renderFrames(1);

			vars = scriptMgmt.getVariables();
			Assert.assertTrue("Variables size shouldn't expect to exceed "+nbAuthorized+" !", vars.size() <= startLength + nbAuthorized);
			String s = vars.get("loc:0");
			if (s != null) {
				int i = (int) Float.parseFloat(s);
				if (waitForResetI) {
					if (i == 0) {
						System.out.println("Cycle over: "+nbCycle);
						waitForResetI = false;
						nbCycle++;
					}
				}
			}
			String forOver = scriptMgmt.getVarValue("forOver");
			if ("1.0".equals(forOver)) {
				waitForResetI = true;
			}
		}
	}
	
	@Test
	public void checkPersoActionForInLoop() {
		scriptMgmt.getAdventure().merge(ScriptReader.loadScript("junit/script/loops"));
		final int NB_PERSOS = 10;
		
		waitEndOfScripting();
		
		List<Perso> persos = new ArrayList<>();
		for (int i=0;i<NB_PERSOS;i++) {
			Perso perso = spawnPerso(PersoDescription.TURRET, "turret"+i, 16 + 20 * (i%8), 16 + 20 * (i / 8));
			perso.setAction(new ScriptedPersoAction(perso, "actionForInLoop"));
			persos.add(perso);
		}

		
		Map<String, String> vars = scriptMgmt.getVariables();
		// We have 2 local variables per character. So calculate the max and we'll check it's never exceeded
		final int initialSize = vars.size();
		final int maxVariables = initialSize + (2 * NB_PERSOS);
		
		while (nFrame < 5000) {
			renderFrames(1);
			vars = scriptMgmt.getVariables();
			System.out.println(initialSize+" ==> "+vars.size());
			
			Assert.assertTrue("We should not have more than "+maxVariables+" !", vars.size() <= maxVariables);
			
		}
	}
	
	@Test
	public void checkSceneWithParameters() {
		checkNumberVariablesWithScene("loopExecParameters", 20);
	}
	
	@Test
	public void checkStopScene() {
		scriptMgmt.getAdventure().merge(ScriptReader.loadScript("junit/script/subscript"));
		
		waitEndOfScripting();
		
		executeSceneNonBlocking("monScript");
		
		PersoManagement pm = EngineZildo.persoManagement;
		Assert.assertEquals(1,  pm.tab_perso.size());	// hero
		renderFrames(20);
		Assert.assertNotNull(pm.getNamedPerso("bandit"));
		Assert.assertNotNull(pm.getNamedPerso("hector"));
		Assert.assertEquals(4,  pm.tab_perso.size());	// 3 + hero
		
		executeScene("terminator");	// This scene will kill previous one
		renderFrames(3+3);
		// Check script are correctly stopped, and expected characters removed
		Assert.assertFalse(EngineZildo.scriptManagement.isScripting());
		Assert.assertNotNull(pm.getNamedPerso("bandit"));
		Assert.assertNotNull(pm.getNamedPerso("hector"));
		Assert.assertEquals(3,  pm.tab_perso.size());
	}
	
	private void checkForScript(String scriptName) {
		scriptMgmt.getAdventure().merge(ScriptReader.loadScript("junit/script/loops"));
		
		waitEndOfScripting();
		int nbSprites = countSprites();
		executeScene(scriptName);
		waitEndOfScripting();

		// Check that "spawn" has been called 16 times
		Assert.assertEquals(16,  countSprites() - nbSprites);
		
		// Check that spawned entities are at the right place, depending on local variable
		int firstY = 100;
		for (SpriteEntity entity : EngineZildo.spriteManagement.getSpriteEntities(null)) {
			if (entity.getDesc() == ElementDescription.BUSHES) {
				Assert.assertEquals(firstY, (int) entity.y);
				firstY += 16;
			}
		}
	}

	/** Particular bug noticed 09/01/2017:
	 * -we have a character, with a persoAction executed
	 * -in this persoAction, we call a script
	 * -in this script, we create a local variable, and we have an 'if' clause
	 * -after this latter script, the local variable wasn't removed from context
	 * NOTE: if no 'if' clause is in the script, this works
	 */
	@Test
	public void checkVariablesRemoved() {
		scriptMgmt.getAdventure().merge(ScriptReader.loadScript("junit/script/subscript"));
		waitEndOfScripting();
		
		int numVariables = scriptMgmt.getVariables().size();

		EngineZildo.scriptManagement.execute("checkVariablesRemovedLaunch", false);
		
		renderFrames(5);
		Perso perso = EngineZildo.persoManagement.getNamedPerso("elemental");
		while (perso.visible) {
			renderFrames(1);
		}
		
		// Check that all local variables have been removed
		Assert.assertEquals(numVariables, scriptMgmt.getVariables().size());
	}
	
	private int countSprites() {
		return EngineZildo.spriteManagement.getSpriteEntities(null).size();
	}
}
