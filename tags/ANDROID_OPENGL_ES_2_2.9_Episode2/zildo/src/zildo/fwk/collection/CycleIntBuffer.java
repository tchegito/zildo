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
 * @author Tchegito
 *
 */
public class CycleIntBuffer {

	int[] buffer;
	int cursor;
	
	/**
	 * @param size should be a power of 2.
	 */
	public CycleIntBuffer(int size) {
		buffer = new int[size];
	}
	
	public void init(int value) {
		for (int i=0;i<buffer.length;i++) {
			buffer[i] = value;
		}
	}
	
	/**
	 * Return the first 
	 * @return
	 */
	public int pop() {
		int ind = -1;
		int startCursor = cursor;
		while (ind == -1) {
			ind = buffer[cursor];
			if (ind != -1) {
				buffer[cursor] = -1;	// Place is taken
			}
			cursor = (cursor+1) % buffer.length;
			if (startCursor == cursor) {
				return -1;	// No values !!!
			}
		}
		return ind;
	}
	
	/**
	 * Look for a specific value. WARNING: if buffer is too small => inifinite loop
	 * @param value
	 */
	public void lookForEmpty() {
		int ind = buffer[cursor];
		while (ind != -1) {
			cursor = (cursor+1) % buffer.length;
			ind = buffer[cursor];
		}
	}

	public void push(int value) {
		if (cursor == buffer.length) {
			cursor = 0;
		}
		set(cursor, value);
		cursor=(cursor+1) % buffer.length;
	}
	
	public void set(int index, int value) {
		buffer[index] = value;
	}
	
	public int get(int index) {
		return buffer[index];
	}
	
	public int length() {
		return buffer.length;
	}
	
	public void rewind() {
		cursor = 0;
	}
}
