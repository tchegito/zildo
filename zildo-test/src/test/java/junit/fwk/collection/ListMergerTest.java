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

package junit.fwk.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import org.junit.Assert;
import zildo.fwk.collection.ListMerger;

/**
 * @author Tchegito
 *
 */
public class ListMergerTest {

	@Test
	public void basic() {
		List<String> l1 = Arrays.asList("a", "b", "c", "d");
		List<String> l2 = Arrays.asList("e", "f");
		for (String s : new ListMerger<String>(l1, l2)) {
			System.out.println(s);
		}
	}
	
	@Test
	public void removal() {
		List<String> l1 = new ArrayList<String>();
		l1.addAll(Arrays.asList("a", "b", "c", "d"));
		List<String> l2 = new ArrayList<String>();
		l2.addAll(Arrays.asList("e", "f"));
		ListMerger<String> merged = new ListMerger<String>(l1, l2);
		l1.remove("a");
		Assert.assertEquals(l1.indexOf("a"), -1);
		Assert.assertEquals(merged.indexOf("a"), -1);
	}
}
