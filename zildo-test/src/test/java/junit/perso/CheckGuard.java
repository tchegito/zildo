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
import zildo.monde.util.Point;
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
	
	PersoPlayer zildo;
	
	@Test
	public void shootArrowThroughObstacle() {
		mapUtils.loadMap("prisonext");

		Assert.assertFalse(alertAndShootHero(394, 285, null));
		Assert.assertFalse(alertAndShootHero(401, 285, null));
		Assert.assertTrue(alertAndShootHero(377, 285, null));
	}
	
	@Test
	public void shouldnShootArrow() {
		mapUtils.loadMap("chateaucoucou3");
		
		// Another case with a flowerpot between hero and shooter
		Assert.assertFalse(alertAndShootHero(609, 260, new Point(700, 261)));
	}
	
	@Test
	public void shouldntShootOnDifferentFloor() {
		mapUtils.loadMap("prisonext");
		PersoPlayer hero = spawnZildo(374, 141);
		hero.setFloor(2);
		Assert.assertFalse("Guard shouldn't have shot an arrow !", alertAndShootHero(310, 141, new Point(374, 141)));
	}
	
	/** Spawn hero and black guard with a bow. Makes him shoot an arrow.
	 * Return an arrow if exists after a 300 frames waiting **/
	private boolean alertAndShootHero(int gardX, int gardY, Point zildoLoc) {
		Point loc = zildoLoc;
		if (zildoLoc == null) loc =	new Point(gardX, 86);
 
		if (zildo == null) {
			zildo = spawnZildo(loc.x, loc.y);
		} else {
			zildo.x = loc.x;
			zildo.y = loc.y;
		}
		
		Perso gard = spawnPerso(PersoDescription.GARDE_CANARD, "noir", gardX, gardY);
		gard.setQuel_deplacement(MouvementPerso.ZONEARC, true);
		gard.setSpecialEffect(EngineFX.GUARD_BLACK);
		gard.setZone_deplacement(new Zone((int)gard.x, (int)gard.y, 10, 10)); 
		gard.setAngle(Angle.NORD);
		gard.setInfo(PersoInfo.ENEMY);
		waitEndOfScripting();
		gard.setAlerte(true);
		
		boolean foundArrow = false;
		int nbFrames = 200;
		while (nbFrames-- > 0) {
			foundArrow = findEntityByDesc(ElementDescription.ARROW_UP) != null || 
					findEntityByDesc(ElementDescription.ARROW_RIGHT) != null;
			renderFrames(1);
			if (foundArrow) break;
		}

		return foundArrow;
	}
}
