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

package zildo.fwk;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

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
    	
	public static ByteBuffer duplicateBuffer(ByteBuffer buf) {
		ByteBuffer copied = ByteBuffer.allocateDirect(buf.limit());
		copied.put(buf);
		return copied;
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
    
    public static <T> T listTail(List<T> elements) {
    	return elements.get(elements.size() - 1);
    }

    public static <T> List<T> arrayList() {
    	return new ArrayList<T>();
    }
    
    public static <T> Set<T> hashSet() {
    	return new HashSet<T>();
    }
    
    public static <K, V> Map<K, V> hashMap() {
    	return new HashMap<K, V>();
    }
    
    public static <T> List<T> addOrCreate(List<T> list, T elem) {
    	List<T> output = list;
    	if (list == null) {
    		output = arrayList();
    	}
    	output.add(elem);
    	return output;
    }
    public static boolean isEmpty(String s) {
    	return s == null || "".equals(s);
    }
    
    /** Transforms string into integer, and returns -1 if something goes wrong **/
    public static int safeValueOf(String s) {
    	try {
    		return Integer.valueOf(s);
    	} catch (NumberFormatException e) {
    		return -1;
    	}
    }
    
    public static class Reversed<T> implements Iterable<T> {
        private final List<T> original;

        public Reversed(List<T> original) {
            this.original = original;
        }

        public Iterator<T> iterator() {
            final ListIterator<T> i = original.listIterator(original.size());

            return new Iterator<T>() {
                public boolean hasNext() { return i.hasPrevious(); }
                public T next() { return i.previous(); }
                public void remove() { i.remove(); }
            };
        }

        public static <T> Reversed<T> reversed(List<T> original) {
            return new Reversed<T>(original);
        }
    }
    
    public static String hexa(long v) {
    	return String.format("0x%08X", v);
    }
    
    @SafeVarargs
	public static <T> String arrayToString(T... values) {
    	StringBuilder sb = new StringBuilder();
    	for (T v : values) {
    		if (sb.length() != 0) sb.append(", ");
    		sb.append(v);
    	}
    	return sb.toString();
    }
    	
    static final Pattern p = Pattern.compile("[0-9|\\.|\\-| ]*");
    
    public static boolean isEmpty(int[] seq) {
    	return seq == null || seq.length > 0; 
    }
    
    public static boolean isNumeric(String p_text) {
    	return p.matcher(p_text).matches();
    }
    
    public static int[] listToArray(List<Integer> l) {
    	int[] array = new int[l.size()];
    	int ind = 0;
    	for (Integer i : l) {
    		array[ind++] = i;
    	}
    	return array;
    }
}
