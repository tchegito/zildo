package junit.collision;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import zildo.monde.collision.CollBuffer;
import zildo.monde.collision.Collision;
import zildo.monde.collision.PersoCollision;
import zildo.monde.collision.Rectangle;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoNJ;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
import zildo.monde.util.Pointf;
import zildo.monde.util.Vector2f;
import zildo.monde.util.Zone;
import zildo.server.EngineZildo;
import zildo.server.SpriteManagement;

public class TestPersoCollision {

	PersoCollision colli;
	
	@Before
	public void init() {
		colli = new PersoCollision();
		EngineZildo.spriteManagement = new SpriteManagement();
		EngineZildo.spriteManagement.persoColli = colli;
	}
	@Test
	public void addAndRemove() {
	
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
		Pointf hitPoint = Collision.hitPointOnCircles(p1.cx, p1.cy, p2.cx, p2.cy, p1.cr, p2.cr);
		Assert.assertEquals(new Pointf(108f, 100f), hitPoint);
		hitPoint = Collision.hitPointOnCircles(p2.cx, p2.cy, p1.cx, p1.cy, p2.cr, p1.cr);
		Assert.assertEquals(new Pointf(108f, 100f), hitPoint);

		// Small radius
		p1 = new Collision(185, 108, 4, Angle.NORD, null, null, null);
		p2 = new Collision(175, 101, 10, Angle.NORD, null, null, null);
		Assert.assertTrue(Collision.checkCollisionCircles(p1.cx, p1.cy, p2.cx, p2.cy, p1.cr, p2.cr));
		hitPoint = Collision.hitPointOnCircles(p1.cx, p1.cy, p2.cx, p2.cy, p1.cr, p2.cr);
		Assert.assertEquals(new Point(182, 106), hitPoint.toPoint());
		hitPoint = Collision.hitPointOnCircles(p2.cx, p2.cy, p1.cx, p1.cy, p2.cr, p1.cr);
		Assert.assertEquals(new Point(182, 106), hitPoint.toPoint());
		
	}
	
	@Test
	public void rectangleAndCircles() {
		Rectangle rect = new Rectangle(new Zone(371, 120, 18, 15));
		Assert.assertTrue(rect.isCrossingCircle(new Pointf(381, 140), 6));
	}
	
	@Test
	public void heroAndTurtles() {
		PersoPlayer hero = new PersoPlayer(1);
		hero.setPos(new Vector2f(381, 140));
		PersoNJ turtle1 = spawnTurtle(382f, 112.75f);
		PersoNJ turtle2 = spawnTurtle(382f, 128.25f);
		
		List<Perso> persos = Arrays.asList(hero, turtle1, turtle2);
		persos.forEach(p -> {
			p.initializeId(SpriteEntity.class);
			p.setPv(1);
		});
		// 1) check howManyAround
		colli.initFrame(persos);
		Assert.assertEquals(3,  CollBuffer.howManyAround(23, 8));
		
		Assert.assertEquals(turtle2, colli.checkCollision(382f, 113.25f, turtle1, 7));
	}
	
	PersoNJ spawnTurtle(float x, float y) {
		PersoNJ turtle = new PersoNJ();
		turtle.setPos(new Vector2f(x, y));
		turtle.setDesc(PersoDescription.TURTLE);
		turtle.setSprModel(PersoDescription.TURTLE, 0);
		turtle.setAngle(Angle.SUD);
		turtle.initMover();
		return turtle;
	}
	
	@Test
	public void multiTurtles() {
		PersoPlayer hero = new PersoPlayer(1);
		hero.setPos(new Vector2f(392.83, 140.66f));
		hero.setZ(5f);
		PersoNJ turtle1 = spawnTurtle(375.25f, 142f);
		PersoNJ turtle2 = spawnTurtle(357.5f, 98f);
		PersoNJ turtle3 = spawnTurtle(382f, 110.75f);
		PersoNJ turtle4 = spawnTurtle(382f, 130.5f);
		List<Perso> persos = Arrays.asList(hero, turtle1, turtle2, turtle3, turtle4);
		persos.forEach(p -> {
			p.initializeId(SpriteEntity.class);
			p.setPv(1);
		});
		colli.initFrame(persos);
		Assert.assertEquals(3,  CollBuffer.howManyAround(23, 8));
		Assert.assertEquals(turtle4, colli.checkCollision(382, 118f, turtle3, 7));

		for (int i=0;i<30+5;i++) {
			turtle3.setY(turtle3.getY() + 0.25f);
			colli.initFrame(persos);
		}
		Assert.assertEquals(4,  CollBuffer.howManyAround(23, 8));
		Assert.assertEquals(turtle4, colli.checkCollision(382, turtle3.y + 0.25f, turtle3, 7));
	}
}
