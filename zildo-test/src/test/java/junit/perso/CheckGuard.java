package junit.perso;

import org.junit.Assert;
import org.junit.Test;

import tools.EngineUT;
import zildo.fwk.gfx.EngineFX;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.Perso.PersoInfo;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.sprites.utils.MouvementPerso;
import zildo.monde.util.Angle;
import zildo.monde.util.Zone;
import zildo.server.EngineZildo;


public class CheckGuard extends EngineUT {

	@Test
	public void detectAndShootHorizontal() {
		mapUtils.loadMap("prisonext");
		PersoPlayer hero = spawnZildo(360, 45);
		Perso gard = spawnPerso(PersoDescription.GARDE_CANARD, "b1", 450, 50);
		gard.setAngle(Angle.OUEST);
		gard.setQuel_deplacement(MouvementPerso.ZONEARC, true);
		gard.setInfo(PersoInfo.ENEMY);
		gard.setZone_deplacement(new Zone((int)gard.x, (int)gard.y, 10, 10)); 
		waitEndOfScripting();
		
		renderFrames(5);
		// Check if Zildo is detected
		Assert.assertTrue(gard.isAlerte());
		
		// Wait for an arrow to be shot
		waitForSpecificEntity(ElementDescription.ARROW_LEFT);
		
		while (!hero.isWounded()) {
			renderFrames(1);
		}
	}

	// Autre situation: garde en 490,69 target en 490,80 (il ne peut pas pas avancer à cause du mur
	// Zildo est en 397,80 et doit pouvoir être touché par une flèche
	
	@Test
	public void shootArrowThroughObstacle() {
		mapUtils.loadMap("prisonext");

		Assert.assertNull(alertAndShootHero(394, 285));
		Assert.assertNull(alertAndShootHero(401, 285));
		Assert.assertNotNull(alertAndShootHero(377, 285));
	}
	
	/** Spawn hero and black guard with a bow. Makes him shoot an arrow.
	 * Return an arrow if exists after a 300 frames waiting **/
	private SpriteEntity alertAndShootHero(int gardX, int gardY) {
		
		EngineZildo.spriteManagement.clearSprites(true);
		
		spawnZildo(gardX, 86);
		Perso gard = spawnPerso(PersoDescription.GARDE_CANARD, "noir", gardX, gardY);
		gard.setQuel_deplacement(MouvementPerso.ZONEARC, true);
		gard.setSpecialEffect(EngineFX.GUARD_BLACK);
		gard.setZone_deplacement(new Zone((int)gard.x, (int)gard.y, 10, 10)); 
		gard.setAngle(Angle.NORD);
		gard.setInfo(PersoInfo.ENEMY);
		waitEndOfScripting();
		gard.setAlerte(true);
		
		renderFrames(200);
		return findEntityByDesc(ElementDescription.ARROW_UP);
	}
}
