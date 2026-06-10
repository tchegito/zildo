package junit.collision;

import org.junit.Test;

import org.junit.Assert;
import zildo.monde.collision.Line;
import zildo.monde.collision.Rectangle;
import zildo.monde.util.Pointf;
import zildo.monde.util.Zone;

public class TestGeometry {

	@Test
	public void doesItCross() {
		Line line = new Line(new Pointf(100, 90), new Pointf(200, 100));
		Assert.assertFalse(line.isCrossingCircle(new Pointf(160, 120), 10));
		Assert.assertFalse(line.isCrossingCircle(new Pointf(160, 120), 18));
		Assert.assertTrue(line.isCrossingCircle(new Pointf(160, 120), 60));

		// Horizontal line
		line = new Line(new Pointf(100, 100), new Pointf(200, 100));
		Assert.assertFalse(line.isCrossingCircle(new Pointf(160, 120), 10));
		Assert.assertTrue(line.isCrossingCircle(new Pointf(160, 120), 30));

	}
	
	
	@Test
	public void shiftRectangle() {
		Rectangle original = new Rectangle(new Pointf(382, 112.5), new Pointf(18, 15));
		Rectangle shifted = original.translate(0,  1);
		Assert.assertEquals(106d, shifted.getCornerTopLeft().y, 0d);

		original = new Rectangle(new Zone(373, 105, 18, 15));
		shifted = original.translate(0,  1);
		Assert.assertEquals(106d, shifted.getCornerTopLeft().y, 0d);
	}
	
	@Test
	public void shiftAndCrossingRectangle() {
		Rectangle r1 = new Rectangle(new Zone(373, 120, 18, 15));
		Rectangle r2 = new Rectangle(new Zone(373, 104.5f, 18, 15));
		Assert.assertFalse(r1.isCrossing(r2));
		Assert.assertFalse(r2.isCrossing(r1));
		
		r2 = r2.translate(0,  0.5f);
		Assert.assertTrue(r1.isCrossing(r2));
		Assert.assertTrue(r2.isCrossing(r1));
	}
}
