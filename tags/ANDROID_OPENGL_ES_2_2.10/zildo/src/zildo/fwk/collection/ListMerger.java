/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
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

import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Tchegito
 * 
 */
public class ListMerger<E> extends AbstractList<E> {

	private final List<E> list1;
	private final List<E> list2;

	public ListMerger(List<E> list1, List<E> list2) {
		this.list1 = list1;
		this.list2 = list2;
	}

	@Override
	public E get(int index) {
		if (index < list1.size()) {
			return list1.get(index);
		}
		return list2.get(index - list1.size());
	}

	@Override
	public Iterator<E> iterator() {
		
		return new Iterator<E>() {
			Iterator<E> it = list1.iterator();
			boolean first = true;
			
			@Override
			public boolean hasNext() {
				if (first) {
					if (!it.hasNext()) {
						first = false;
						it = list2.iterator();
						return it.hasNext();
					} else {
						return true;
					}
				} else {
					return it.hasNext();
				}
			}

			@Override
			public E next() {
				return it.next();
			}

			@Override
			public void remove() {
				it.remove();
			}
		};
	}
	
	@Override
	public int size() {
		return list1.size() + list2.size();
	}

}
