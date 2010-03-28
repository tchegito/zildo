/**
 * Legend of Zildo
 * Copyright (C) 2006-2010 Evariste Boussaton
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

	public byte[] getArray() {
        byte bytes[] = null;
        if(data.hasArray())
        {
            bytes = data.array();
        } else
        {
            bytes = new byte[data.capacity()];
            data.get(bytes);
        }
        return bytes;
	}
}
