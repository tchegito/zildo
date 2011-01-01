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

import zildo.fwk.bank.SpriteBank;
import zildo.fwk.file.EasyBuffering;
import zildo.fwk.file.EasyWritingFile;
import zildo.fwk.gfx.GFXBasics;
import zildo.monde.sprites.SpriteModel;
import zildo.prefs.Constantes;

/**
 * @author Tchegito
 *
 */
public class SpriteBankEdit extends SpriteBank {

    List<short[]> gfxs;
    BufferedImage img;
    int[] imgPixels;    // Parallel image which is a source for inserting new sprites into this bank
   
    public SpriteBankEdit(SpriteBank p_spr) {
        models=p_spr.getModels();
        name=p_spr.getName();
        nSprite=p_spr.getNSprite();
        gfxs=new ArrayList<short[]>();
        sprites_buf=p_spr.getSprites_buf();
       
        // Build all graphics into a single list
        for (int i=0;i<nSprite;i++) {
            short[] gfx=p_spr.getSpriteGfx(i);
            gfxs.add(gfx);
        }
    }
    public void addSpr(int p_position, int p_tailleX, int p_tailleY, short[] p_gfx) {
        SpriteModel model=new SpriteModel(p_tailleX, p_tailleY, 0);    // don't care about 'offset'
        gfxs.add(p_position, p_gfx);
        models.add(p_position, model);
        nSprite++;
    }
   
    public void removeSpr(int p_position) {
        models.remove(p_position);
        gfxs.remove(p_position);
        nSprite--;
    }
   
    public void addSprFromImage(int p_position, int p_startX, int p_startY, int p_tailleX, int p_tailleY) {
        // Extract sprite from image
        short[] sprite=new short[p_tailleX * p_tailleY];
        for (int j=0;j<p_tailleY;j++) {
        for (int i=0;i<p_tailleX;i++) {
            int offsetImg=(p_startY + j) * img.getWidth() + p_startX + i;
            sprite[j*p_tailleX+i]=(short) imgPixels[offsetImg];
        }
        }
        addSpr(p_position, p_tailleX, p_tailleY, sprite);
    }
   
    public void loadImage(String p_filename, int p_transparentColor) {
       img = null;
        try {
            img = ImageIO.read(new File(Constantes.DATA_PATH+p_filename));
        } catch (IOException e) {
        }
        imgPixels=new int[img.getWidth() * img.getHeight()];
        PixelGrabber pixelGrabber=new PixelGrabber(img, 0, 0, img.getWidth(), img.getHeight(), imgPixels, 0, img.getWidth());
        try {
        	if (pixelGrabber.grabPixels()){
	            System.out.println("Ok");
	        }
        } catch (InterruptedException e) {
       
        }
        for (int j=0;j<img.getHeight();j++) {
	        for (int i=0;i<img.getWidth();i++) {
	            int offset=i + (j*img.getWidth());
	            if ((imgPixels[offset] & 0xffffff) == p_transparentColor) {
	            	imgPixels[offset]=255;
	            } else {
	            	imgPixels[offset]=GFXBasics.getPalIndex(imgPixels[offset]);
	            }
	        }
        }
    }
    
    public void saveBank() {
        EasyBuffering buffer=new EasyBuffering();
        for (int i=0;i<nSprite;i++) {
            SpriteModel model=models.get(i);
            buffer.put((byte) model.getTaille_x());
            buffer.put((byte) model.getTaille_y());
            for (short s : gfxs.get(i)) {
                buffer.put((byte) s);
            }
        }
        EasyWritingFile file=new EasyWritingFile(buffer);
        file.saveFile(getName());
    }
}
