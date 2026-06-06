package junit.collision;

import org.junit.Test;

import org.junit.Assert;
import zildo.monde.collision.Line;
import zildo.monde.util.Pointf;

public class TestLine {

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
}
