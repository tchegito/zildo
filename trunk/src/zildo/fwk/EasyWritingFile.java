package zildo.fwk;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class EasyWritingFile {

	ByteBuffer data;
	
	/**
	 * Creates an EasyWritingFile object for writing.
	 */
	public EasyWritingFile() {
        data = ByteBuffer.allocate(45000);
	}
	
	public void put(byte b) {
		data.put(b);
	}
	
	public void put(int i) {
		data.put((byte) (i >> 8));
		data.put((byte) (i & 255));
	}
	
	public void put(String p_str) {
		put(p_str, -1);
	}
	
	public void put(String p_str, int p_nCharacters) {
		int len=p_nCharacters;
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
	
    public void saveFile(String p_fileName) {
    	// Wrap the buffer
    	data.flip();
    	
        OutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream(new File(p_fileName));
            fileOut.write(data.array(), 0, data.limit());
        } catch (Exception e) {
            throw new RuntimeException("Unable to write " + p_fileName + " !");
        }
    }
    
    public ByteBuffer getAll() {
    	return data;
    }
}
