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

package junit.script;

import org.junit.Assert;
import org.junit.Test;

import zildo.fwk.script.logic.ExprScanner;


/**
 * @author Tchegito
 *
 */
public class CheckScanner {

	@Test
	public void nominal() {
		ExprScanner scan = new ExprScanner("1 + 2 + 4");
		String[] values = new String[5];
		for (int i=0;i<5;i++) {
			values[i] = scan.next();
		}
		Assert.assertTrue("1".equals(values[0]));
		Assert.assertTrue("+".equals(values[1]));
		Assert.assertTrue("2".equals(values[2]));
		Assert.assertTrue("+".equals(values[3]));
		Assert.assertTrue("4".equals(values[4]));
	}
	
	@Test
	public void spaces() {
		ExprScanner scan = new ExprScanner("   1    +  2 + 4  ");
		String[] values = new String[5];
		for (int i=0;i<5;i++) {
			values[i] = scan.next();
		}
		Assert.assertEquals("1", values[0]);
		Assert.assertEquals("+", values[1]);
		Assert.assertEquals("2", values[2]);
		Assert.assertEquals("+", values[3]);
		Assert.assertEquals("4", values[4]);
		
		Assert.assertFalse(scan.hasNext());
	}
	
	@Test
	public void parenthese() {
		ExprScanner scan = new ExprScanner("1 + 4 * (2 - 3) + 6");
		String[] values = new String[11];
		for (int i=0;i<11;i++) {
			values[i] = scan.next();
		}
		Assert.assertTrue("1".equals(values[0]));
		Assert.assertTrue("+".equals(values[1]));
		Assert.assertTrue("4".equals(values[2]));
		Assert.assertTrue("*".equals(values[3]));
		Assert.assertTrue("(".equals(values[4]));
		Assert.assertTrue("2".equals(values[5]));
		Assert.assertTrue("-".equals(values[6]));
		Assert.assertTrue("3".equals(values[7]));
		Assert.assertTrue(")".equals(values[8]));
		Assert.assertTrue("+".equals(values[9]));
		Assert.assertTrue("6".equals(values[10]));
	}
	
	@Test
	public void minusFirst() {
		ExprScanner scan = new ExprScanner("-486");
		Assert.assertEquals("-", scan.next());
		Assert.assertEquals("486", scan.next());
	}
	
	@Test
	public void minMax() {
		ExprScanner scan = new ExprScanner("12 min 3");
		Assert.assertEquals("12", scan.next());
		Assert.assertEquals("min", scan.next());
		Assert.assertEquals("3", scan.next());
		
		scan = new ExprScanner("-12 min 3");
		Assert.assertEquals("-", scan.next());
		Assert.assertEquals("12", scan.next());
		Assert.assertEquals("min", scan.next());
		Assert.assertEquals("3", scan.next());
	}

}
