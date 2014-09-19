/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
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

package zeditor.tools.palette;

import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import zeditor.tools.tiles.Banque;
import zildo.fwk.file.EasyBuffering;
import zildo.fwk.file.EasyWritingFile;
import zildo.monde.util.Vector3f;

/**
 * @author Tchegito
 * 
 */
public class PaletteExtractor {

	Vector3f[] palette;

	public PaletteExtractor(String filename) {
		palette = loadPalette(Banque.PNG_PATH + filename);
	}

	public void save(String outPalFileName) {
		EasyBuffering buf = new EasyBuffering(768);
		for (int i = 0; i < 256; i++) {
			buf.put((byte) palette[i].x);
			buf.put((byte) palette[i].y);
			buf.put((byte) palette[i].z);
		}

		new EasyWritingFile(buf).saveFile(outPalFileName);
	}

	private Vector3f[] loadPalette(String p_filename) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File(p_filename));
		} catch (IOException e) {
		}
		IndexColorModel colors = (IndexColorModel) img.getColorModel();
		Vector3f[] ret = new Vector3f[256];
		for (int i = 0; i < 256; i++) {
			Vector3f col = new Vector3f(colors.getRed(i), colors.getGreen(i), colors.getBlue(i));
			/*
			 * System.out.println("col "+i+ " R" + col.x+ " G"+col.y+
			 * " B"+col.z);
			 */
			ret[i] = col;
		}
		return ret;
	}
}
