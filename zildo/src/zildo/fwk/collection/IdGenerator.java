/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
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

/**
 * Class able to generate an ID between 0 and a maximum given to the constructor.<br/>
 * ID are recycled in a cycle buffer.
 * 
 * @author Tchegito
 *
 */
public class IdGenerator {

	boolean[] buffer;
	int cursor;
	
	public IdGenerator(int maxId) {
		buffer = new boolean[maxId];
		for (int i=0;i<maxId;i++) {
			buffer[i] = false;
		}
		cursor = 0;
	}
	
	public int pop() {
		boolean ind = true;
		int startCursor = cursor;
		while (ind == true) {
			ind = buffer[cursor];
			if (!ind) {
				buffer[cursor] = true;	// Place is taken
				break;
			}
			cursor = (cursor+1) % buffer.length;
			if (startCursor == cursor) {	// One turn => no room left !
				throw new RuntimeException("Can't allow another ID !");
			}
		}
		return cursor;
	}
	
	public void remove(int id) {
		buffer[id] = false;
	}
}
