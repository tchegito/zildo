package junit.perso.bosses;

import org.junit.Assert;
import org.junit.Test;

import tools.EngineUT;
import zildo.monde.items.ItemKind;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.util.Angle;

public class TestTurretBoss extends EngineUT {

	@Test
	public void meetAndFight() {
		mapUtils.loadMap("prison13");
		PersoPlayer hero = spawnZildoWithItem(160, 187, ItemKind.SWORD);
		waitEndOfScripting();
		
		// Leave the room and go in the turret's one
		simulateDirection(0, 1);
		renderFrames(50);
		waitEndOfScroll();
		mapUtils.assertCurrent("prison14");
		
		waitForScriptRunning("boss_turret");
		waitForScriptRunning("spawnBossTurret1");
		
		renderFrames(50);
		
		Perso turret1 = persoUtils.persoByName("turret1");
		// 1) Turret causes damage when popped out of the ground
		while (turret1.getAlpha() != 255) {
			renderFrames(1);
		}
		hero.x = turret1.x + 9;
		hero.y = turret1.y - 10;
		hero.walkTile(false);
		renderFrames(1);
		Assert.assertTrue(hero.isWounded());
		
		// 2) All turrets should stay in the room
		for (int i=1;i<5;i++) {
			Perso turret = persoUtils.persoByName("turret"+i);
			Assert.assertTrue(turret.x > 32 && turret.y > 32 && turret.x < 300 && turret.y < 180);
		}
		
		// 3) Heart should be damageable
		Perso turretH = persoUtils.persoByName("turretH");
		while (turretH.getAlpha() != 255) {
			renderFrames(1);
		}
		hero.x = 159;
		hero.y = 153;
		hero.setAngle(Angle.NORD);
		hero.attack();
		renderFrames(5);
		Assert.assertTrue(turretH.isWounded());
	}
}
