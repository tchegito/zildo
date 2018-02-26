package junit.input;

import static zildo.fwk.input.DPadMovement.compute;
import static zildo.fwk.input.DPadMovement.moveCenter;
import static zildo.fwk.input.DPadMovement.forces;

import org.junit.Assert;
import org.junit.Test;

import zildo.fwk.input.DPadMovement;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
import zildo.monde.util.Vector2f;

public class TestTouchPad {

	final float FACTOR_MIDDLE = forces[1];
	final float FACTOR_SMALL = forces[0];
	
	@Test
	public void basic() {
		Vector2f NORTH = new Vector2f(Angle.NORD.coordf);
		Vector2f SOUTH = new Vector2f(Angle.SUD.coordf);
		Vector2f EAST = new Vector2f(Angle.EST.coordf);
		Vector2f WEST = new Vector2f(Angle.OUEST.coordf);
		// No movement
		Assert.assertNull( compute(0, 0));
		// Plain movement
		Assert.assertEquals(EAST, compute(40,0));
		Assert.assertEquals(WEST, compute(-40,0));
		Assert.assertEquals(NORTH, compute(0,-40));
		Assert.assertEquals(SOUTH, compute(0,40));
		// Half movement
		Assert.assertEquals(EAST.mul(FACTOR_MIDDLE), compute(20,0));
		Assert.assertEquals(WEST.mul(FACTOR_MIDDLE), compute(-20,0));
		Assert.assertEquals(NORTH.mul(FACTOR_MIDDLE), compute(0,-20));
		Assert.assertEquals(SOUTH.mul(FACTOR_MIDDLE), compute(0,20));
		// Smallest movement
		Assert.assertEquals(EAST.mul(FACTOR_SMALL), compute(5,0));
		Assert.assertEquals(WEST.mul(FACTOR_SMALL), compute(-5,0));
		Assert.assertEquals(NORTH.mul(FACTOR_SMALL), compute(0,-5));
		Assert.assertEquals(SOUTH.mul(FACTOR_SMALL), compute(0,5));
		
		// Too big movement
		Assert.assertEquals(EAST, compute(400,0));
	}
	
	@Test
	public void movingCross() {
		Point cross = new Point(160, 100);
		// Movement on cross
		Assert.assertEquals(cross, moveCenter(cross, new Point(180, 110)));
		
		// Movement too far
		Point touch = new Point(290, 110);
		Point newCross = moveCenter(cross, touch);
		Assert.assertNotEquals(cross, newCross);
		Assert.assertEquals(DPadMovement.DISTANCE_MAX, (int) newCross.distance(touch));
		Assert.assertTrue(newCross.x < touch.x);
		Assert.assertTrue(newCross.y < touch.y);
	}
}
