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

package zildo.platform.opengl.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

/**
 * @author Tchegito
 *
 */
public class ShortBufferizer {

	ShortBuffer fBuffer;
	
	public ShortBufferizer(int length) {
		ByteBuffer bBuffer;
		bBuffer = ByteBuffer.allocateDirect(length * 4);
		bBuffer.order(ByteOrder.nativeOrder());
		fBuffer = bBuffer.asShortBuffer();
	}
	
	public void store(int[] p_ints) {
		for (int i : p_ints) {
			fBuffer.put((short) i);
		}
	}
	
	public ShortBuffer storeAndFlip(int[] p_ints) {
		store(p_ints);
		return rewind();
	}
	
	public ShortBuffer rewind() {
		fBuffer.position(0);
		return fBuffer;
	}
	
	public void addInt(int... f) {
		for (int ff : f) {
			fBuffer.put((short) ff);
		}
	}
	
	public int getCount() {
		return fBuffer.position();
	}
}
