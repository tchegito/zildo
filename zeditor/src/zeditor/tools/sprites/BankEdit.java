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

package zeditor.tools.sprites;

import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import zeditor.tools.Transparency;
import zildo.monde.util.Zone;
import zildo.resource.Constantes;

/**
 * Tool class for modifying SpriteBank and MotifBank.<br/>
 * 
 * Deals with an image, and provides rectangular captures from it.
 * @author Tchegito
 *
 */
public class BankEdit {

    public List<int[]> gfxs;
    
    public BufferedImage img;
    public int[] imgPixels;    // Parallel image which is a source for inserting new sprites into this bank
   
    public BankEdit() {
        gfxs=new ArrayList<int[]>();
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
				int v = imgPixels[offset];
				if (p_transparentColor == -1) {
					// Nothing : keep transparency
				} else if ((v & 0xffffff) == Transparency.TRANSPARENCY_COLOR) {
					imgPixels[offset] = 0 << 24;
				} else {
					// No transparency at all => 0xff as ALPHA value
					imgPixels[offset] = v & 0xffffff | 0xff000000;
				}
			}
		}
	}
    
    public int[] getRectFromImage(int p_startX, int p_startY,
			int p_tailleX, int p_tailleY) {
		// Extract sprite from image
		int[] sprite = new int[p_tailleX * p_tailleY];
		for (int j = 0; j < p_tailleY; j++) {
			for (int i = 0; i < p_tailleX; i++) {
				int offsetImg = (p_startY + j) * img.getWidth() + p_startX + i;
				sprite[j * p_tailleX + i] = imgPixels[offsetImg];
			}
		}
		
		return sprite;
	}
    
    public void setRectFromImage(Zone location, int[] sprite) {
		for (int j = 0; j < location.y2; j++) {
			for (int i = 0; i < location.x2; i++) {
				int offsetImg = (location.y1 + j) * img.getWidth() + location.x1 + i;
				imgPixels[offsetImg] = sprite[j * location.x2 + i];
			}
		}
    }
    
    /**
     * Returns TRUE if there's something on a vertical line (useful for fonts).
     * @param p_startX
     * @param p_startY
     * @param p_height
     * @return boolean
     */
    protected boolean isLineFilled(int p_startX, int p_startY, int p_height) {
    	for (int i=0;i<p_height;i++) {
			int offsetImg = (p_startY + i) * img.getWidth() + p_startX;
    		if (imgPixels[offsetImg] != 0) {
    			return true;
    		}
    	}
    	return false;
    }
    
    public int getImageWidth() {
    	return img.getWidth();
    }
    
    public int getImageHeight() {
    	return img.getHeight();
    }
}
