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
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import zildo.fwk.file.EasyBuffering;
import zildo.fwk.file.FileUtil;

/**
 * @author Tchegito
 * 
 */
public class AndroidFileUtil implements FileUtil {

	@Override
	public EasyBuffering openFile(String path) {
		return new AndroidReadingFile(path);
	}

	@Override
	public File[] listFiles(String path, FilenameFilter filter) {
		List<File> files = new ArrayList<File>();
		try {
			String[] strFiles = AndroidReadingFile.assetManager.list("resources"+File.separator+"saves");
			for (String s : strFiles) {
				System.out.println(s);
				files.add(new File(s));
			}
		} catch (IOException e) {
			System.out.println("Error reading folder "+path);
		}
		return files.toArray(new File[] {});
	}

}
