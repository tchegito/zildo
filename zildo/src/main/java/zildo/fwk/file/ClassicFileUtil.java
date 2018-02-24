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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.OutputStream;

import zildo.resource.Constantes;

/**
 * @author Tchegito
 *
 */
public class ClassicFileUtil implements FileUtil {

	@Override
	public EasyBuffering openFile(String path) {
		return new EasyReadingFile(path);
	}

	public OutputStream prepareSaveFile(String path) {
        try {
			return new FileOutputStream(new File(Constantes.DATA_PATH+path));
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Unable to write " + path + " !");
		}
	}
	
	@Override
	public File[] listFiles(String path, FilenameFilter filter) {
		File startPath = new File(Constantes.DATA_PATH + path);
		return startPath.listFiles(filter);
	}

	@Override
	public Object openFd(String file) {
		// This method is here only to implement correcly the interface.
		// But this is only intended for Android.
		throw new RuntimeException("Don't call me !");
	}

	@Override
	public EasyBuffering openPrivateFile(String path) {
		// Private file is nonsense for LWJGL platforms => same as openFile
		return openFile(path);
	}

	@Override
	public boolean openLink(String url) {
		// Never called on this device
		return true;
	}
}
