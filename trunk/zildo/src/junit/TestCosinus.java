package junit;

import org.junit.Test;

public class TestCosinus {

	@Test
	public void testMathCosinus() {
		int x;
		float alpha = 0;
		float value = 0;
		float max =0;
		while (true) {
			alpha+=0.07f;
			value+=0.6f * Math.cos(alpha);
			if (value > max) {
				max = value;
			}
			System.out.println(max);
		}
	}
}
