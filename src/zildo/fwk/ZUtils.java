/**
 * Legend of Zildo
 * Copyright (C) 2006-2011 Evariste Boussaton
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

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;

public class ZUtils {

	static FloatBuffer floatBuffer = null;
	
	public static void sleep(long p_millis) {
		try {
			Thread.sleep(p_millis);
		} catch (InterruptedException e) {
			
		}
	}
	
	public static long getTime() {
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
	
	public static int randomRange(int p_range) {
		return (int) (p_range * Math.random()) - (p_range / 2);
	}
	
	/**
	 * Get the current color set with glColor4f
	 * @param p_info
	 * @param p_size
	 * @return float[]
	 */
	public static float[] getFloat(int p_info,int p_size) {
		if (floatBuffer == null) {
			floatBuffer=BufferUtils.createFloatBuffer(16);
		}
		GL11.glGetFloat(p_info, floatBuffer);
		
		float[] temp=new float[p_size];
		floatBuffer.get(temp);
		floatBuffer.position(0);
		return temp;
	}
	
	/**
	 * Set the current color from a float array.
	 * @param p_color
	 */
	public static void setCurrentColor(float[] p_color) {
		GL11.glColor4f(p_color[0], p_color[1], p_color[2], p_color[3]);		
	}
	
	/**
	 * Return an array of string, representing the text to be displayed in a combo, for a given enum type.
	 * @param <T>
	 * @param p_content
	 * @return String[]
	 */
	public static <T extends Enum<T>>  Object[] getValues(Class<T> p_content) {
		List<String> str=new ArrayList<String>();
		T[] enumConst=p_content.getEnumConstants();
		for (T mvt : enumConst) {
			str.add(mvt.name());
		}
		return str.toArray(new String[]{});
	}
	
	public static <T extends Enum<T>> T getField(String p_enumString, Class<T> p_clazz) {
		for (T e : p_clazz.getEnumConstants()) {
			if (e.toString().equals(p_enumString)) {
				return e;
			}
		}
		return null;
	}
}
