package zildo.fwk.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class EasyWritingFile extends EasyBuffering {
    
	public EasyWritingFile(EasyBuffering p_buffer) {
		data=p_buffer.data;
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

}
