package zildo.fwk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class EasyFile {

	ByteBuffer buf;
	int position;
	
	public EasyFile(String path) {
		FileInputStream stream;
		try {
			File file=new File(path);
			stream=new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Unable to find "+path);
		}
		FileChannel chIn=stream.getChannel();
		try {
			buf=ByteBuffer.allocate((int) chIn.size());
			chIn.read(buf);
			stream.close();
		} catch (IOException e) {
			throw new RuntimeException("Unable to read "+path);
		}
		buf.flip();
	}

	public String readString(int nChars) {
		String result="";
		int lengthPertinent=readUnsignedByte();
		int length=lengthPertinent;
		if (nChars != -1) {
			length=nChars-1;
		}
		boolean reachEnd=false;
		for (;length>0;length--) {
			byte a=buf.get();
			if (a==0) {
				reachEnd=true;
			}
			if (!reachEnd && result.length()!=lengthPertinent) {
				result+=(char) ((short)0xff & a);	// Remove sign bit
			}
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
		return (byte) buf.get();
	}
	
	/**
	 * Read one byte, and returns it without sign(0..255)
	 * @return
	 */
	public short readUnsignedByte() {
		return (short) (0xFF & buf.get()) ;
	}
	
	/**
	 * Read entire file.
	 * @return <short[]>
	 */
	public short[] readUnsignedBytes() {
		short[] temp=new short[this.getSize()];
		int posInitiale=buf.position();
		for (int i=posInitiale;i<buf.capacity();i++) {
			byte b=buf.get(i);
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
		int posInitiale=buf.position();
		for (int i=posInitiale;i<posInitiale+size;i++) {
			byte b=buf.get();
			// See http://darksleep.com/player/JavaAndUnsignedTypes.html
			sh[i-posInitiale+pos]=(short) (0xFF & b);	// Remove sign bit
		}
	}
	
	public boolean eof() {
		return buf.position() == buf.capacity();
	}
	
	public int getSize() {
		return buf.capacity();
	}
	
	public ByteBuffer getAll() {
		return buf;
	}
}
