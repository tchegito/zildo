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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import zildo.fwk.file.EasyBuffering;
import zildo.fwk.file.FileUtil;
import zildo.resource.Constantes;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.util.Log;

/**
 * Android has some particular ways to manage files, contrary to LWJGL targeted platforms.<p/>
 * 
 * Basically two major differences :<ol>
 * <li>assets : impossible to get a {@link File} object from this directory. So we have to use
 * {@link AssetFileDescriptor}.</li>
 * <li>private files : application can have its own file within its context, but without folders.
 * So we have to remove path separator from the name, and folders indeed.</li>
 * </ol>
 * @author Tchegito
 * 
 */
public class AndroidFileUtil implements FileUtil {

	@Override
	public EasyBuffering openFile(String path) {
		return AndroidReadingFile.open(path);
	}

	@Override
	public EasyBuffering openPrivateFile(String path) {
		return AndroidReadingFile.openPrivate(removePaths(path));
	}
	
	@Override
	public File[] listFiles(String path, FilenameFilter filter) {
		List<File> files = new ArrayList<File>();
		String[] strFiles = AndroidReadingFile.context.fileList();

		for (String s : strFiles) {
			File saveFile = AndroidReadingFile.context.getFileStreamPath(s);
			files.add(saveFile);
		}
		return files.toArray(new File[] {});
	}

	@Override
	public Object openFd(String file) {
		String completeFilename="resources/"+file;
		try {
			return AndroidReadingFile.assetManager.openFd(completeFilename);
		} catch (IOException e) {
			Log.e("sound", "can't load "+file);
			return null;
		}
	}
	
	public OutputStream prepareSaveFile(String path) {
		try {
			return AndroidReadingFile.context.openFileOutput(removePaths(path), Context.MODE_PRIVATE);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Unable to write " + path + " !");
		}
	}
	
	
	private String removePaths(String s) {
		return s.replaceAll(Constantes.SAVEGAME_DIR, "")
				.replaceAll(Constantes.INI_DIR, "");
	}
}
