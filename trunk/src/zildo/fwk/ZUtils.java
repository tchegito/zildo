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

import java.util.ArrayList;
import java.util.List;

public class ZUtils {

	public static void sleep(long p_millis) {
		try {
			Thread.sleep(p_millis);
		} catch (InterruptedException e) {
			
		}
	}
	
	public static long getTime() {
		return System.nanoTime() / 1000000;	// Equivalent to followoing line but not LWJGL dependent
		//return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
	
	public static int randomRange(int p_range) {
		return (int) (p_range * Math.random()) - (p_range / 2);
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
	
	public static <T> T getField(String p_enumString, List<T> p_elems) {
		for (T e : p_elems) {
			if (e.toString().equals(p_enumString)) {
				return e;
			}
		}
		return null;
	}
}
