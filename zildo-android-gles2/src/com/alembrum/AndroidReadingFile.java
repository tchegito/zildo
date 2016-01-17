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

package com.alembrum;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import zildo.fwk.file.EasyBuffering;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

/**
 * @author Tchegito
 *
 */
public class AndroidReadingFile extends EasyBuffering {

	public static AssetManager assetManager;
	public static Context context;
	
	static byte[] buf = new byte[4000];	// One file at a time => static
	
	private AndroidReadingFile(String path, boolean asset) {
		super(null);	// Cancel first allocation
		Log.d("file", "open "+path);
		boolean finished = false;
		InputStream stream = null;
		try {
			
			if (asset) {
				stream = openAssetFile(path);
			} else {
				stream = openPrivateFile(path);
			}
			data = ByteBuffer.allocate(stream.available());
			while (!finished) {
		        int read = stream.read(buf);
		        data.put(buf, 0, read);
		        if (read == -1) {
		            throw new RuntimeException("Something went horribly wrong");
		        }
		        finished = (read < buf.length);

		    }
		} catch (Exception e) {
			throw new RuntimeException("Unable to read "+path);
		} finally {
			if (stream != null)
				try {
					stream.close();
				} catch (IOException e) {
					throw new RuntimeException("Something went horribly wrong : unable to close stream");
				}
		}
		data.flip();
	}

	public static AndroidReadingFile open(String path) {
		return new AndroidReadingFile(path, true);
	}
	
	public static AndroidReadingFile openPrivate(String path) {
		return new AndroidReadingFile(path, false);
	}
	
	private InputStream openAssetFile(String path) {
		String completeFilename="resources/"+path;
		try {
			return assetManager.open(completeFilename);
		} catch (IOException e) {
			throw new RuntimeException("Unable to find "+path);
		}
		
	}
	private InputStream openPrivateFile(String path) {
		try {
			return context.openFileInput(path);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Unable to find "+path);
		}
	}
	
	
}
