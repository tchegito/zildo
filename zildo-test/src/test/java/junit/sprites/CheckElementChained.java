package junit.sprites;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import tools.EngineUT;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.elements.CustomizableElementChained;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.persos.ia.mover.StraightMoveOrder;
import zildo.monde.util.Angle;
import zildo.server.EngineZildo;

public class CheckElementChained extends EngineUT {

	@Test
	public void moveWillOWist() {
		move(ElementDescription.WILL_O_WIST);
	}

	@Test
	public void moveFireBall() {
		move(ElementDescription.SMALL_FIRE_BALL);
		// Check that sprite are disappeared now, after being smashed to the wall
		List<SpriteEntity> entities = findEntitiesByDesc(ElementDescription.PROJ_LAVA);
		Assert.assertEquals(0, entities.size());
	}

	private void move(ElementDescription desc) {
		waitEndOfScripting();
		Element matrix = EngineZildo.spriteManagement.createElement(desc, 160, 100, 0, null,  null, null);
		Element element = new CustomizableElementChained(matrix, 3, 4);
		if (!desc.isNotFixe()) {
			element.setMover(new StraightMoveOrder(160 + 100, 100, 4));
		} else {
			element.beingThrown(160, 100, Angle.EST, null);
		}
		EngineZildo.spriteManagement.spawnSprite(element);
		
		// 26 corresponds to the number of frames during sprite is moving
		// for the fireball, that may be adjusted if something changes (speed for example)
		for (int i=0;i<26;i++) {
			renderFrames(1);
			/*findEntitiesByDesc(desc).forEach(e ->
				System.out.println("x:" + e.x+" , y:" + e.y+" , z:"+e.z)
			);*/
		}

		Assert.assertTrue(findEntityByDesc(desc).x > 200);
	}
}
