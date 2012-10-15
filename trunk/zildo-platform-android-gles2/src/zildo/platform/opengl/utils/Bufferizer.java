/**
 * Legend of Zildo
 * Copyright (C) 2006-2012 Evariste Boussaton
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
import java.nio.FloatBuffer;

/**
 * @author Tchegito
 *
 */
public class Bufferizer {

	FloatBuffer fBuffer;
	
	public Bufferizer(int length) {
		ByteBuffer bBuffer;
		bBuffer = ByteBuffer.allocateDirect(length * 4);
		bBuffer.order(ByteOrder.nativeOrder());
		fBuffer = bBuffer.asFloatBuffer();
	}
	
	public void store(float[] p_floats) {
		fBuffer.put(p_floats, 0, p_floats.length);
	}
	
	public FloatBuffer storeAndFlip(float[] p_floats) {
		store(p_floats);
		return rewind();
	}
	
	public FloatBuffer rewind() {
		fBuffer.position(0);
		return fBuffer;
	}
	
	public void addFloat(float... f) {
		for (float ff : f) {
			fBuffer.put(ff);
		}
	}
	
	public int getCount() {
		return fBuffer.position();
	}
}
