package junit.collision;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

import zildo.monde.collision.Collision;
import zildo.monde.collision.PersoCollision;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoNJ;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
import zildo.server.EngineZildo;
import zildo.server.SpriteManagement;

public class TestPersoCollision {

	@Test
	public void addAndRemove() {
		PersoCollision colli = new PersoCollision();
		EngineZildo.spriteManagement = new SpriteManagement();
		EngineZildo.spriteManagement.persoColli = colli;
		
		Element elem = new Element();
		// Will do twice: one for background, one for foreground
		for (int i=0;i<2;i++) {
			// No one in the field
			assertNull(colli.checkCollision(160,  100, elem, 10));
			// Add one then recheck
			Perso clampin = new PersoNJ();
			clampin.setPv(6);
			clampin.x = 160;
			clampin.y = 100;
			clampin.initializeId(SpriteEntity.class);
			if (i == 1) {
				clampin.setForeground(true);
				elem.setForeground(true);
			}
			colli.initFrame(Collections.singletonList(clampin));
			assertEquals(clampin, colli.checkCollision(160,  100, elem, 10));
			
			// Remove then recheck
			colli.notifyDeletion(clampin);
			assertNull(colli.checkCollision(160,  100, elem, 10));
		}
	}

	@Test
	public void computeHitLocation() {
		Collision p1 = new Collision(100, 100, 10, Angle.NORD, null, null, null);
		Collision p2 = new Collision(136, 100, 30, Angle.NORD, null, null, null);
		Assert.assertTrue(Collision.checkCollisionCircles(p1.cx, p1.cy, p2.cx, p2.cy, p1.cr, p2.cr));
		// With old methods, we get a hit point on (100, 100)
		Point hitPoint = Collision.hitPointOnCircles(p1.cx, p1.cy, p2.cx, p2.cy, p1.cr, p2.cr);
		Assert.assertEquals(new Point(108, 100), hitPoint);
		hitPoint = Collision.hitPointOnCircles(p2.cx, p2.cy, p1.cx, p1.cy, p2.cr, p1.cr);
		Assert.assertEquals(new Point(108, 100), hitPoint);

		// Small radius
		p1 = new Collision(185, 108, 4, Angle.NORD, null, null, null);
		p2 = new Collision(175, 101, 10, Angle.NORD, null, null, null);
		Assert.assertTrue(Collision.checkCollisionCircles(p1.cx, p1.cy, p2.cx, p2.cy, p1.cr, p2.cr));
		hitPoint = Collision.hitPointOnCircles(p1.cx, p1.cy, p2.cx, p2.cy, p1.cr, p2.cr);
		Assert.assertEquals(new Point(182, 106), hitPoint);
		hitPoint = Collision.hitPointOnCircles(p2.cx, p2.cy, p1.cx, p1.cy, p2.cr, p1.cr);
		Assert.assertEquals(new Point(182, 106), hitPoint);
		
	}
}
