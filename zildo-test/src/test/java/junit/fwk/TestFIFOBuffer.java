package junit.fwk;

import org.junit.Assert;
import org.junit.Test;

import zildo.fwk.collection.FIFOBuffer;
import zildo.monde.util.Point;

public class TestFIFOBuffer {

	@Test
	public void small() {
		FIFOBuffer buf3 = new FIFOBuffer(3);
		Point p1 = new Point(1,2);
		Assert.assertNull(buf3.pushAndPop(new Point(1,2)));
		Assert.assertNull(buf3.pushAndPop(new Point(3,4)));
		Assert.assertNull(buf3.pushAndPop(new Point(5,6)));
		
		Assert.assertEquals(p1, buf3.pushAndPop(new Point(7, 8)));
	}
	
	@Test
	public void repeatedConsecutiveValues() {
		FIFOBuffer buf5 = new FIFOBuffer(5);
		// 10 times the same value must lead to only 1 value stored in the buffer
		Point start = new Point(1, 8);
		for (int i=0;i<10;i++) {
			System.out.println(i);
			Point p = new Point(start);
			Assert.assertNull(buf5.pushAndPop(p));
		}
		// Fill the buffer to 5 elements
		for (int i=0;i<4;i++) {
			Point p = new Point(i,i+1);
			Assert.assertNull(buf5.pushAndPop(p));
		}
		// Ensure that we get the first entered
		Assert.assertEquals(start, buf5.pushAndPop(new Point(137, 158)));
	}
}
