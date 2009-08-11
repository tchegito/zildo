package zildo.fwk.file;

import java.nio.ByteBuffer;

/**
 * Class which encapsulates the read/write operations into a ByteBuffer.
 * Here we use different type (int, byte, String).
 * String objects are treated as Turbo Pascal way :
 * -first byte: string's length
 * -remaining: string content
 * 
 * @author tchegito
 *
 */
public class EasyBuffering {


	private static final String nullString="@nul#";
	
	ByteBuffer data;
	
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
			data.put((byte) bStr[i]);
		}
		for (int i=0;i<len-p_str.length()-1;i++) {
			data.put((byte) 0);
		}
	}
	
	public void put(ByteBuffer p_buffer) {
		data.put(p_buffer);
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
		return (byte) data.get();
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
		return ((int) readUnsignedByte() << 8) + readUnsignedByte();
	}

	public boolean readBoolean() {
		short b=readUnsignedByte();
		return b==1;
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
}
