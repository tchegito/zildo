package junit.fwk;

import org.junit.Assert;
import org.junit.Test;

import zildo.monde.collision.Rectangle;
import zildo.monde.util.Point;
import zildo.monde.util.Zone;

public class RectangleTest {

	@Test
	public void circleBox() {
		Zone size = new Zone(453, 560, 28, 16);
		// Place a point inside the rectangle
		Point p = new Point(470, 573);
		int radius = 7;
		
		Rectangle r = new Rectangle(size);
		Assert.assertTrue(r.isInside(p));
		Assert.assertTrue(r.isCrossingCircle(p, radius));
		
		// Now move the point outside, but still in the radius
		p = new Point(470, 578);
		Assert.assertFalse(r.isInside(p));
		Assert.assertTrue(r.isCrossingCircle(p, radius));
		
		// Now move the point too far away to be inside the radius
		p = new Point(470, 585);
		Assert.assertFalse(r.isInside(p));
		Assert.assertFalse(r.isCrossingCircle(p, radius));
	}
}
