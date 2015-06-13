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

package zildo.fwk;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
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

	public static <T extends Enum<T>>  Object[] getValues(Class<T> p_content) {
		return getValues(p_content, null);
	}
	
	public static abstract class Conditioner<T> {
		public abstract boolean accept(T obj);
	}
	
	/**
	 * Return an array of string, representing the text to be displayed in a combo, for a given enum type.
	 * @param <T>
	 * @param p_content
	 * @return String[]
	 */
	public static <T extends Enum<T>>  Object[] getValues(Class<T> p_content, Conditioner<T> p_cond) {
		List<String> str=new ArrayList<String>();
		T[] enumConst=p_content.getEnumConstants();
		for (T mvt : enumConst) {
			if (p_cond == null || p_cond.accept(mvt)) {
				str.add(mvt.name());
			}
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
	
	static long totalsize = 0;
	
    public static ByteBuffer createByteBuffer(int size)
    {
    	totalsize+=size;
    	//System.out.println("allocate "+size+" / total ="+totalsize);
        return ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
    }
    
    public static IntBuffer createIntBuffer(int size)
    {
        return createByteBuffer(size << 2).asIntBuffer();
    }
    
    public static FloatBuffer createFloatBuffer(int size)
    {
        return createByteBuffer(size << 2).asFloatBuffer();
    }

    public static ShortBuffer createShortBuffer(int size)
    {
        return createByteBuffer(size << 1).asShortBuffer();
    }
    
    
    /**
     * OpenGL likes "adjusted" size for texture. We take multiple of 256.
     * @param n Initial size
     * @return Adjusted size
     */
    static public int adjustTexSize(int n) {
        if (n % 256 == 0) {
            return n;
        }
        return (n & 0xff00) + 256;
    }

    public static IntBuffer getBufferWithId(int id) {
        IntBuffer buf = createIntBuffer(1);
        buf.put(id);
        buf.rewind();
        return buf;
    }
    
    public static String capitalize(String s) {
    	if (s == null || s.length() == 0) {
    		return s;
    	}
    	return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
    
    public static <T> String listToString(List<T> elements) {
		StringBuilder sb = new StringBuilder(16);
		for (T elem : elements) {
			sb.append(elem.toString());
			sb.append(",");
		}
		if (sb.length() > 1) {
			sb.setLength(sb.length()-1);
		}
		return sb.toString();
    }
    
    static byte[] buffer = new byte[10];
    
    /** Returns an array containing decomposition of POSITIVE value in base 10. **/
    public static byte[] decomposeBase10(int value) {
    	int n = value;
    	int numDecimal = 0;
    	while ( n > 0 || numDecimal == 0) {
    		buffer[ numDecimal++ ] = (byte) (n % 10);
    		n = n/10;
    	}
    	byte[] result = new byte[numDecimal];
    	for (int i=0;i<numDecimal;i++) {
    		result[i] = buffer[numDecimal - i - 1];
    	}
    	return result;
    }
    
    public static <T> List<T> arrayList() {
    	return new ArrayList<T>();
    }
    
    public static boolean isEmpty(String s) {
    	return s == null || "".equals(s);
    }
}
