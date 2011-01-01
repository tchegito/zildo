/**
 * Legend of Zildo
 * Copyright (C) 2006-2011 Evariste Boussaton
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

package zeditor.tools.sprites;

import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import zildo.fwk.gfx.GFXBasics;
import zildo.prefs.Constantes;

/**
 * Tool class for modifying SpriteBank and MotifBank.<br/>
 * 
 * Deals with an image, and provides rectangular captures from it.
 * @author Tchegito
 *
 */
public class BankEdit {


    public List<short[]> gfxs;
    BufferedImage img;
    int[] imgPixels;    // Parallel image which is a source for inserting new sprites into this bank
   
    public BankEdit() {
        gfxs=new ArrayList<short[]>();
    }
    
    public void loadImage(String p_filename, int p_transparentColor) {
		img = null;
		String fileName=p_filename;
		if (fileName.indexOf("\\") == -1) {
			fileName=Constantes.DATA_PATH + p_filename;
		}
		try {
			img = ImageIO.read(new File(fileName));
		} catch (IOException e) {
		}
		imgPixels = new int[img.getWidth() * img.getHeight()];
		PixelGrabber pixelGrabber = new PixelGrabber(img, 0, 0, img.getWidth(),
				img.getHeight(), imgPixels, 0, img.getWidth());
		try {
			pixelGrabber.grabPixels();
		} catch (InterruptedException e) {

		}
		for (int j = 0; j < img.getHeight(); j++) {
			for (int i = 0; i < img.getWidth(); i++) {
				int offset = i + (j * img.getWidth());
				if ((imgPixels[offset] & 0xffffff) == p_transparentColor) {
					imgPixels[offset] = 255;
				} else {
					imgPixels[offset] = GFXBasics
							.getPalIndex(imgPixels[offset]);
				}
			}
		}
	}
    
    public short[] getRectFromImage(int p_startX, int p_startY,
			int p_tailleX, int p_tailleY) {
		// Extract sprite from image
		short[] sprite = new short[p_tailleX * p_tailleY];
		for (int j = 0; j < p_tailleY; j++) {
			for (int i = 0; i < p_tailleX; i++) {
				int offsetImg = (p_startY + j) * img.getWidth() + p_startX + i;
				sprite[j * p_tailleX + i] = (short) imgPixels[offsetImg];
			}
		}
		
		return sprite;
	}
}
