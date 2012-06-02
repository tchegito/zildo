/**
 * Legend of Zildo
 * Copyright (C) 2006-2012 Evariste Boussaton
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

package com.zildo;

import java.io.InputStream;
import java.nio.ByteBuffer;

import zildo.fwk.file.EasyBuffering;
import android.content.res.AssetManager;
import android.util.Log;

/**
 * @author Tchegito
 *
 */
public class AndroidReadingFile extends EasyBuffering {

	public static AssetManager assetManager;
	
	static byte[] buf = new byte[4000];	// One file at a time => static
	
	public AndroidReadingFile(String path) {
		super(null);	// Cancel first allocation
		String completeFilename="resources/"+path;
		Log.d("file", "open "+path);
		int done = 0;
		boolean finished = false;
		try {
			InputStream stream = assetManager.open(completeFilename);
			data = ByteBuffer.allocate(stream.available());
			while (!finished) {
		        int read = stream.read(buf);
		        data.put(buf, 0, read);
		        if (read == -1) {
		            throw new RuntimeException("Something went horribly wrong");
		        }
		        done += read;
		        finished = (read < buf.length);

		    }
		} catch (Exception e) {
			throw new RuntimeException("Unable to read "+path);
		}
		data.flip();
	}

	
	
}
