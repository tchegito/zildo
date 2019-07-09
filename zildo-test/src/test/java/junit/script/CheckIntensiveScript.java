package junit.script;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import tools.EngineUT;
import tools.annotations.DisableFreezeMonitor;
import zildo.fwk.script.context.LocaleVarContext;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.util.Vector2f;
import zildo.server.EngineZildo;

public class CheckIntensiveScript extends EngineUT {

	PersoPlayer hero;
	
	// We had a bug when turtles were kept after a map has scrolled if turtle were currently moving.
	// Because we choose to keep entity with 'ghost' attribute at TRUE.
	@Test @DisableFreezeMonitor
	public void switchMapAndCountTurtles() {
		mapUtils.loadMap("sousbois5");
		hero = spawnZildo(374, 37);
		countTurtles();
		
		// Make turtle awake
		simulateDirection(0, 1);
		waitForScriptRunning("turtleAwake");
		
		// Wait for its move
		waitForScriptRunning("tortueSousbois5_2");
		
		renderFrames(50);
		
		// Then change map
		hero.setPos(new Vector2f(224,17));
		simulateDirection(0,-1);
		while (!EngineZildo.mapManagement.isChangingMap(hero)) {
			renderFrames(1);
		}
		System.out.println(getLocVariableKeys());
		while (hero.y < 50) {	// Wait until scroll is over
			renderFrames(1);
		}
		System.out.println(getLocVariableKeys());
		waitEndOfScripting();
		
		// Come back
		simulateDirection(0,1);
		while (!EngineZildo.mapManagement.isChangingMap(hero)) {
			renderFrames(1);
		}
		System.out.println(getLocVariableKeys());
		while (hero.y > 50 || hero.isGhost()) {	// Wait until scroll is over
			renderFrames(1);
		}
		
		countTurtles();
	}
	
	// Check a cutscene where a character (Maltus) follows hero during a map scroll
	// We ensure that he isn't removed during the scroll
	@Test @DisableFreezeMonitor
	public void sceneWithSwitchingMap() {
		mapUtils.loadMap("foretg2");
		spawnZildo(463,510);
		//EngineZildo.scriptManagement.accomplishQuest("suite_visit1_weapon", false);
		// Get out of the cave and wait for cutscene to start
		simulateDirection(0, 1);
		waitForScriptRunning("maltus_foretg");
		// Maltus will be spawned by the script, so wait until it is
		renderFrames(5);
		Assert.assertNotNull(persoUtils.persoByName("maltus"));
		mapUtils.assertCurrent("foret");
		waitEndOfScriptingPassingDialog();
		Assert.assertNotNull(persoUtils.persoByName("maltus"));
		//maltus_foretg
	}
	
	@Test
	public void lotOfTurtles() {
		mapUtils.loadMap("sousbois5");
		hero = spawnZildo(374, 37);
		// Check that we haven't any local variables
		Assert.assertEquals(0, getLocVariableKeys().size());
		waitEndOfScripting();
		
		countTurtles();
		makeTurtlesMove();
		
		// Change map
		hero.setPos(new Vector2f(224,17));
		simulateDirection(0,-1);
		while (!EngineZildo.mapManagement.isChangingMap(hero)) {
			renderFrames(1);
		}
		System.out.println(getLocVariableKeys());
		while (hero.y < 50) {	// Wait until scroll is over
			renderFrames(1);
		}
		System.out.println(getLocVariableKeys());
		waitEndOfScripting();
		
		// Come back
		simulateDirection(0,1);
		while (!EngineZildo.mapManagement.isChangingMap(hero)) {
			renderFrames(1);
		}
		System.out.println(getLocVariableKeys());
		while (hero.y > 50 || hero.isGhost()) {	// Wait until scroll is over
			renderFrames(1);
		}

		countTurtles();

		makeTurtlesMove();
		System.out.println(getLocVariableKeys());
		
	}

	private void countTurtles() {
		int nbTurtles = 0;
		for (SpriteEntity entity : EngineZildo.spriteManagement.getSpriteEntities(null)) {
			if (entity.getDesc() == PersoDescription.TURTLE) {
				nbTurtles++;
			}
		}
		Assert.assertEquals("We should have only 2 turtles !", 2, nbTurtles);
	}
	
	private void makeTurtlesMove() {
		Perso rightTurtle = EngineZildo.persoManagement.getNamedPerso("tortue2");
		Perso leftTurtle = EngineZildo.persoManagement.getNamedPerso("tortue1");
		// Ensure that turtle isn't moving
		for (int i=0;i<1;i++) {
			Assert.assertNull(rightTurtle.getTarget());
			hero.setPos(new Vector2f(374,37));

			simulateDirection(0, 1);
			// Wait until turtle is moving, because she detected hero
			int n=0;
			while (rightTurtle.getTarget() == null) {
				renderFrames(1);
			}
			hero.setPos(new Vector2f(213, 160));
			simulateDirection(1,0);
			System.out.println(getLocVariableKeys());
			// Wait until turtle has finished its move
			waitForScriptRunning("turtleSleep");
			for (int a=0;a<1;a++) {
				//EngineZildo.scriptManagement.runTileAction(new Point(22, 8), "getMoney", false);
			}
			
			waitForScriptFinish("turtleSleep");

			System.out.println(getLocVariableKeys());
			simulateDirection(0,0);
			renderFrames(5);
			hero.setPos(new Vector2f(168,344));
			waitForScriptRunning("turtleSleep");
			//EngineZildo.scriptManagement.runTileAction(new Point(22, 8), "getMoney", false);
			waitForScriptFinish("turtleSleep");
		}
	}
	
	private List<String> getLocVariableKeys() {
		List<String> keys = new ArrayList<>();
		for (String key : EngineZildo.scriptManagement.getVariables().keySet()) {
			if (key.startsWith(LocaleVarContext.VAR_IDENTIFIER)) {
				keys.add(key);
			}
		}
		return keys;
	}
}