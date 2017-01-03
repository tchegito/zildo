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

package zildo.fwk.file;

import java.nio.ByteBuffer;

/**
 * Class which encapsulates the read/write operations into a ByteBuffer.<p/>
 * Here we use different type (int, byte, String).<br/>
 * String objects are treated as Turbo Pascal way :<ul>
 * <li>first byte: string's length</li>
 * <li>remaining: string content</li>
 * </ul>
 * @author tchegito
 *
 */
public class EasyBuffering {


	private static final String nullString="@nul#";
	
	protected ByteBuffer data;
	
	/**
	 * Creates an EasyWritingFile object for writing.
	 */
	public EasyBuffering() {
        data = ByteBuffer.allocate(45000);
	}
	
	public EasyBuffering(int size) {
		data = ByteBuffer.allocate(size);
	}
	
	public EasyBuffering(ByteBuffer b) {
		data = b;
	}
	
	/**
	 * Allocates a new one from an original one.
	 * @param original
	 * @param supp
	 */
	public EasyBuffering(EasyBuffering original, int supp) {
        data = ByteBuffer.allocate(original.getSize() + supp);
        ByteBuffer orig=original.getAll();
        int lim=orig.limit();
        orig.flip();
        data.put(orig);
        orig.limit(lim);
	}
	
	/*********************************************************************************
	 * WRITE operations
	 *********************************************************************************/

	public void put(byte b) {
		data.put(b);
	}

	public void put(float f) {
		data.putFloat(f);
	}

	public void put(int i) {
		data.put((byte) (i >> 8));
		data.put((byte) (i & 255));
	}
	
	// Not very efficient, but we store few booleans
	public void put(boolean b) {
		data.put(b ? (byte) 1 : (byte) 0);
	}
	
	/**
	 * Write a boolean array into one single byte. (more efficient than {@link #put(boolean b)})
	 * Takes one byte to store 6 booleans.
	 * @param p_number
	 * @return boolean[]
	 */
	public void putBooleans(boolean... p_value) {
		byte b=0;
		byte j=1;
		for (boolean val : p_value) {
			b=val ? (byte) (b | j) : b;
			j*=2;

			if (j == (byte)(2 << 7)) {
				data.put(b);
				j=1;
				b=0;
			}
		}
		if (j != 1) {
			data.put(b);
		}
	}
	
	public void put(String p_str) {
		put(p_str, -1);
	}
	
	public void put(String p_str, int p_nCharacters) {
		int len=p_nCharacters;
		if (p_str == null) {
			p_str=nullString;
		}
		if (p_nCharacters == -1) {
			len=p_str.length();
		}
		data.put((byte) p_str.length());
		byte[] bStr=p_str.getBytes();
		for (int i=0;i<p_str.length();i++) {
			data.put(bStr[i]);
		}
		for (int i=0;i<len-p_str.length()-1;i++) {
			data.put((byte) 0);
		}
	}
	
	public void put(ByteBuffer p_buffer) {
		data.put(p_buffer);
	}
	
	public void put(EasyBuffering p_buffer) {
		p_buffer.getAll().flip();
		data.put(p_buffer.getAll());
	}
	
	/*********************************************************************************
	 * READ operations
	 *********************************************************************************/
	
	public String readString(int nChars) {
		String result="";
		int lengthPertinent=readUnsignedByte();
		int length=lengthPertinent;
		if (nChars != -1) {
			length=nChars-1;
		}
		boolean reachEnd=false;
		for (;length>0;length--) {
			byte a=data.get();
			if (a==0) {
				reachEnd=true;
			}
			if (!reachEnd && result.length()!=lengthPertinent) {
				result+=(char) ((short)0xff & a);	// Remove sign bit
			}
		}
		if (nullString.equals(result)) {
			result=null;
		}
		return result;	
	}
	
	public String readString() {
		return readString(-1);
	}
	
	/**
	 * Read one byte, and returns it (-127..128)
	 * @return
	 */
	public byte readByte() {
		return data.get();
	}
	
	/**
	 * Read one byte, and returns it without sign(0..255)
	 * @return
	 */
	public short readUnsignedByte() {
		return (short) (0xFF & data.get()) ;
	}
	
	/**
	 * Read entire file.
	 * @return <short[]>
	 */
	public short[] readUnsignedBytes() {
		short[] temp=new short[this.getSize()];
		int posInitiale=data.position();
		for (int i=posInitiale;i<data.capacity();i++) {
			byte b=data.get(i);
			// See http://darksleep.com/player/JavaAndUnsignedTypes.html
			temp[i-posInitiale]=(short) (0xFF & b);	// Remove sign bit
		}
		return temp;
	}
	
	/**
	 * 
	 * @param sh tableau à remplir
	 * @param pos position dans sh à partir de laquelle on doit écrire
	 * @param size nombre d'octets à lire
	 */
	public void readUnsignedBytes(short[] sh, int pos, int size) {
		int posInitiale=data.position();
		for (int i=posInitiale;i<posInitiale+size;i++) {
			byte b=data.get();
			// See http://darksleep.com/player/JavaAndUnsignedTypes.html
			sh[i-posInitiale+pos]=(short) (0xFF & b);	// Remove sign bit
		}
	}

	public int readInt() {
		return (readUnsignedByte() << 8) + readUnsignedByte();
	}

	public boolean readBoolean() {
		short b=readUnsignedByte();
		return b==1;
	}
	
	/**
	 * Read a boolean array into one single byte. (more efficient than {@link #readBoolean()})
	 * @param p_number
	 * @return boolean[]
	 */
	public boolean[] readBooleans(int p_number) {
		short b=readUnsignedByte();
		boolean[] tab=new boolean[p_number];
		short j=1;
		for (int i=0;i<p_number;i++) {
			tab[i]=(b & j) != 0;
			j*=2;
			if (j == (byte)(2 << 7)) {
				j=1;
			}
		}
		return tab;
	}
	
	public float readFloat() {
		return data.getFloat();
	}

    public void clear() {
        data.clear();
    }

    public ByteBuffer getAll() {
    	return data;
    }
    	
	public boolean eof() {
		return !data.hasRemaining();
	}
	
	public int getSize() {
		return data.capacity();
	}
	
	/** Return a usable String in Java class. Only for purpose debug.**/ 
	public String getStringRepresentation() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		for (byte b : data.array()) {
			sb.append(b).append(", ");
		}
		sb.append("}");
		return sb.toString();
	}
}
