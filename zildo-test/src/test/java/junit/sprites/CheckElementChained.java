package junit.sprites;

import org.junit.Assert;
import org.junit.Test;

import tools.EngineUT;
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
		
		for (int i=0;i<50;i++) {
			renderFrames(1);
			findEntitiesByDesc(desc).forEach(e ->
				System.out.println("x:" + e.x+" , y:" + e.y)
			);
		}

		Assert.assertTrue(findEntityByDesc(desc).x > 200);
	}
}
