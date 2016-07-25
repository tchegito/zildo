/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
 * 
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

package zildo.fwk.collection;

import java.util.ArrayList;
import java.util.List;

public class IntSet {

	List<Integer> backed;

	public IntSet(int... startSet) {
		backed = new ArrayList<Integer>(startSet.length);
		for (int a : startSet) {
			backed.add(a);
		}
	}
	
	public IntSet addRange(int first, int last) {
		for (int i = first; i<=last; i++) {
			backed.add(i);
		}
		return this;
	}
	
	// Wrapped method
	public boolean contains(Integer i) {
		return backed.contains(i);
	}
	
	public int indexOf(Integer i) {
		return backed.indexOf(i);
	}
	
	public Integer get(int nth) {
		return backed.get(nth);
	}
	
}
