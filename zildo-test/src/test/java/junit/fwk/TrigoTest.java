/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package junit.fwk;

import static java.lang.Math.abs;
import static zildo.monde.Trigo.getAngleRadian;

import org.junit.Assert;
import org.junit.Test;

import zildo.monde.Function;
import zildo.monde.Trigo;

/**
 * @author Tchegito
 *
 */
public class TrigoTest {

	@Test
	public void angleRadian() {
		// No dependent of size
		Assert.assertEquals(getAngleRadian(0, -1), getAngleRadian(0, -8), 0);
		// PI / 2
		Assert.assertEquals(Math.PI / 2d, abs(getAngleRadian(0f, -1.5f)), 0.0001d);
		// 0
		Assert.assertEquals(0, abs(getAngleRadian(3, 0)), 0.0001d);
		// PI / 4
		Assert.assertEquals(abs(getAngleRadian(5, 5)), abs(getAngleRadian(1, 1)), 0.0001d);
		Assert.assertEquals(Math.PI / 4d, abs(getAngleRadian(1, 1)), 0.0001d);
		Assert.assertEquals(Math.PI, getAngleRadian(-38, 1), 0.1d);
	}
	
	// Easing functions
	@Test
	public void ease() {
		int distance = 60;
		Function func = Trigo.easeInOut(distance, 1);
		int[] values = new int[distance];
		for (int i=0;i<distance;i++) {
			int pos = Math.round(func.apply(i) * distance);
			values[i] = pos;
			System.out.println(pos);
		}
		Assert.assertEquals(0, values[0]);
		Assert.assertEquals(distance, values[distance-1]);
	}
	
	@Test
	public void easePolynomial() {
		int distance = 60;
		Function func = Trigo.easePolynomial(distance, 1);
		int[] values = new int[distance];
		for (int i=0;i<distance;i++) {
			int pos = Math.round(func.apply(i) * distance);
			values[i] = pos;
			System.out.println(pos);
		}
		Assert.assertEquals(0, values[0]);
		Assert.assertEquals(distance, values[distance-1]);
	}
	
	@Test
	public void easeDerivee() {
		checkEaseDerivee(140, 1);
	}
	
	@Test
	public void easeDeriveeSpeed() {
		checkEaseDerivee(2200, 2f);
	}
	
	// Check that easeInOutDerivee is really the derivee of easeInOut
	@Test
	public void compareFunctionAndDerivee() {
		int distance = 140;
		Function func = Trigo.easeInOut(distance, 1);
		Function derivee = Trigo.easeInOutDerivee(distance, 1);
		float val = 0;
		float funcVal = 0;
		for (int i=0;i<distance;i++) {
			val += derivee.apply(i) * distance;
			funcVal = func.apply(i)*distance;
			System.out.println(funcVal+"\t\t"+val);
		}
		Assert.assertEquals((int) val, (int) funcVal);
	}
	
	@Test
	public void compareSpeed() {
		// Compare number of frames needed to reach one point, with each movement: linear and easing.
		// We try to get gap as close as possible (less than 5%).
		int distance = 140;
		Function func = Trigo.easeInOut(distance, 1);
		int i=0;
		// Go forward until function value reach the target
		while (Math.round(func.apply(i) * distance) < distance) {
			i++;
		}
		float ratio = ((float) distance - i) / i * 100;
		System.out.println(i+" frames with easing");
		System.out.println(distance+" frames with constant speed ("+(int) ratio+"%)");
		Assert.assertTrue("Ratio should have been less than 5 ! Value Trigo.EASE_SPEED_FACTOR should be ajusted to 1.", (int) ratio < 5);
	}
	
	
	private void checkEaseDerivee(int distance, float speed) {
		int iterations = (int) (distance / speed / Trigo.EASE_SPEED_FACTOR);
		Function funcDerivee = Trigo.easeInOutDerivee(distance, speed);
		int[] values = new int[iterations];
		float val = 0;
		for (int i=0;i<iterations;i++) {
			float funcVal = funcDerivee.apply(i) * distance;
			float delta = funcVal * Math.signum(1f);
			val += delta; //funcVal;
			values[i] = Math.round(val);
			System.out.println(val+"\t\t"+funcVal);
		}
		Assert.assertEquals(0, values[0]);
		Assert.assertEquals(distance, values[iterations-1]);
		System.out.println("max="+distance*Function.FunctionUtils.max(funcDerivee, distance));		
	}
}
