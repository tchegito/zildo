/**
 * Legend of Zildo
 * Copyright (C) 2006-2012 Evariste Boussaton
 * Based on original Zelda : link to the past (C) Nintendo 1992
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

package zildo.fwk;

/**
 * @author Tchegito
 *
 */
public class CycleIntBuffer {

	int[] buffer;
	int cursor;
	int nbValues;
	
	/**
	 * @param size should be a power of 2.
	 */
	public CycleIntBuffer(int size) {
		buffer = new int[size];
		nbValues = 0;
	}
	
	public void init(int value) {
		for (int i=0;i<buffer.length;i++) {
			buffer[i] = value;
		}
		if (value == -1) {
			nbValues = 0;
		} else {
			nbValues = buffer.length;
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
				nbValues--;
			}
			cursor = (cursor+1) % buffer.length;
			if (startCursor == cursor) {
				return -1;	// No values !!!
			}
		}
		return ind;
	}
	
	/**
	 * Look for a specific value.
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
		if (buffer[index] == -1 && value != -1) {
			nbValues++;
		} else if (buffer[index] != -1 && value == -1) {
			nbValues--;
		}
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
	
	/**
	 * Return non-null (!= -1) values.
	 * @return
	 */
	public int getNbValues() {
		return nbValues;
	}
}
