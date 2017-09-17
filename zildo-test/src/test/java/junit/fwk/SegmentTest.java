package junit.fwk;

import org.junit.Assert;

import org.junit.Test;

import zildo.monde.util.Pointf;
import zildo.monde.util.Segment;

/** Checks that segment class is working well. **/
public class SegmentTest {

	@Test
	public void crossEasy() {
		Segment a = new Segment(new Pointf(15, 10), new Pointf(45, 10));
		Segment b = new Segment(new Pointf(30, 4), new Pointf(30, 23));
		
		Segment c = new Segment(new Pointf(15, 68), new Pointf(150, 69));
		
		Pointf expectedIntersect = new Pointf(30, 10);
		Assert.assertEquals(expectedIntersect, a.cross(b));
		Assert.assertEquals(expectedIntersect, b.cross(a));

		Assert.assertNull( a.cross(c));
}
}
