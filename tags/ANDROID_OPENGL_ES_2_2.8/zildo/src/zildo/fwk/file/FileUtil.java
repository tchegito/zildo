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

package zildo.fwk.file;

import java.io.File;
import java.io.FilenameFilter;
import java.io.OutputStream;

/**
 * @author Tchegito
 *
 */
public interface FileUtil {

	EasyBuffering openFile(String path);
	EasyBuffering openPrivateFile(String path);
	OutputStream prepareSaveFile(String path);
	
	File[] listFiles(String path, FilenameFilter filter);
	
	// Method used only in Android to get an AssetFileDescriptor
	Object openFd(String path);
	
}
