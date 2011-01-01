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

package zeditor.tools.tiles;

import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import zeditor.tools.sprites.BankEdit;
import zildo.fwk.bank.MotifBank;
import zildo.fwk.file.EasyBuffering;
import zildo.fwk.file.EasyWritingFile;
import zildo.fwk.gfx.GFXBasics;

/**
 * @author Tchegito
 *
 */
public class MotifBankEdit extends MotifBank {

	BankEdit bankEdit;
	Banque bank;
	
	/**
	 * Constructor designed for building .DEC with class deriving from {@link Banque}.
	 * @param p_bank
	 */
	public MotifBankEdit(Banque p_bank) {
		super();
		
		bankEdit=new BankEdit();
		bank=p_bank;
		
		String name=p_bank.getClass().getSimpleName().toUpperCase();
		setName(name);
	}
	
	/**
	 * Constructor designed for generating image in ZEditor background panel.
	 * @param p_bank
	 */
	public MotifBankEdit(MotifBank p_motifBank, Banque p_bank) {
		this(p_bank);
		
		motifs_map=p_motifBank.getMotifs_map();
		nb_motifs=p_motifBank.getNb_motifs();
	}
	
    public void addSpr(int p_position, short[] p_gfx) {
        bankEdit.gfxs.add(p_position, p_gfx);
        nb_motifs++;
    }
   
    public void removeSpr(int p_position) {
        bankEdit.gfxs.remove(p_position);
        nb_motifs--;
    }
   
    public void addSprFromImage(int p_position, int p_startX, int p_startY) {
		// Extract sprite from image
		short[] sprite = bankEdit.getRectFromImage(p_startX, p_startY, 16, 16);
		addSpr(p_position, sprite);
	}
    
    public void loadImage(String p_filename, int p_transparentColor) {
    	String imageName=Banque.PKM_PATH + p_filename + ".png";
		bankEdit.loadImage(imageName, p_transparentColor);
	}
    
    public void saveBank() {
        EasyBuffering buffer=new EasyBuffering(bankEdit.gfxs.size() * 16 *16);
        for (int i=0;i<nb_motifs;i++) {
            for (short s : bankEdit.gfxs.get(i)) {
                buffer.put((byte) s);
            }
        }
        EasyWritingFile file=new EasyWritingFile(buffer);
        file.saveFile(getName()+".DEC");
    }
    
    
    public Image generateImg() {
    	// Determine dimension
    	int maxX=0;
    	int maxY=0;
    	for (Point p : bank.coords) {
    		maxX=Math.max(p.x+16, maxX);
    		maxY=Math.max(p.y+16, maxY);
    	}
    	Image img=new BufferedImage(maxX, maxY, BufferedImage.TYPE_INT_RGB);
    	
    	// Draw the tiles as they are predefined
    	int tile=0;
    	for (Point p : bank.coords) {
    		drawImage(img, p.x, p.y, get_motif(tile++));
    	}
    	    	
    	img.flush();
    	
    	return img;
    }
    
    private void drawImage(Image img, int x, int y, short[] data) {

    	int[] intData=new int[data.length];
    	for (int i=0;i<data.length;i++) {
    		intData[i] = GFXBasics.getIntColor(data[i]);
    	}
    	
    	DataBuffer buffer = new DataBufferInt(intData, data.length);
    	BufferedImage tempImg=new BufferedImage(16,16, BufferedImage.TYPE_INT_RGB);
    	WritableRaster raster = Raster.createWritableRaster(tempImg.getSampleModel(), buffer, null);
    	tempImg.setData(raster);

    	img.getGraphics().drawImage(tempImg, x, y, null);
    	  	
    }
}
