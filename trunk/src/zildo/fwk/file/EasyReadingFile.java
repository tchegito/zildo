package zildo.fwk.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class EasyReadingFile extends EasyBuffering {

	int position;
	
	/**
	 * Creates an EasyFile object for reading.
	 * @param path file name with complete path.
	 */
	public EasyReadingFile(String path) {
		FileInputStream stream;
		try {
			File file=new File(path);
			stream=new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Unable to find "+path);
		}
		FileChannel chIn=stream.getChannel();
		try {
			data=ByteBuffer.allocate((int) chIn.size());
			chIn.read(data);
			stream.close();
		} catch (IOException e) {
			throw new RuntimeException("Unable to read "+path);
		}
		data.flip();
	}

}
