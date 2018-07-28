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

package zeditor.tools.tiles;

import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;

import zeditor.tools.Transparency;
import zeditor.tools.builder.texture.TileTexture;
import zeditor.tools.sprites.BankEdit;
import zildo.fwk.bank.TileBank;
import zildo.fwk.file.EasyBuffering;
import zildo.fwk.file.EasyWritingFile;
import zildo.fwk.gfx.GFXBasics;
import zildo.fwk.gfx.engine.TileEngine;
import zildo.monde.map.TileCollision;
import zildo.monde.map.TileInfo;

/**
 * @author Tchegito
 *
 */
public class TileBankEdit extends TileBank {

	BankEdit bankEdit;
	Banque bank;

	private int bankOrder;
	final static TileCollision infosCollision = TileCollision.getInstance();
	
	/**
	 * Constructor designed for building .DEC with class deriving from {@link Banque}.
	 * @param p_bank
	 */
	public TileBankEdit(Banque p_bank) {
		super();
		
		bankEdit=new BankEdit();
		bank=p_bank;
		
		if (p_bank != null) {
			String name=p_bank.getClass().getSimpleName().toUpperCase();
			setName(name);
			bankOrder = TileEngine.getBankFromName(getName());
		}
		
	}
	
	/**
	 * Constructor designed for generating image in ZEditor background panel.
	 * @param p_bank
	 */
	public TileBankEdit(TileBank p_motifBank, Banque p_bank) {
		this(p_bank);
		
		setName(p_motifBank.getName());
		
		bankOrder = TileEngine.getBankFromName(getName());

		nb_motifs = p_motifBank.nb_motifs;
	}
	
    public void addSpr(int p_position, int[] p_gfx) {
        bankEdit.gfxs.add(p_position, p_gfx);
        nb_motifs++;
    }
   
    public void removeSpr(int p_position) {
        bankEdit.gfxs.remove(p_position);
        nb_motifs--;
    }
   
    public void addSprFromImage(int p_position, int p_startX, int p_startY) {
		// Extract sprite from image
		int[] sprite = bankEdit.getRectFromImage(p_startX, p_startY, 16, 16);
		addSpr(p_position, sprite);
	}
    
    public void loadImage(String p_filename, int p_transparentColor) {
    	String imageName=Banque.PKM_PATH;
    	// New engine with free tiles
    	// 1) Try with folder containing free tiles
    	String completeName = imageName + "../FreeGraph/notAlembrume/" + p_filename + ".png";
    	try {
    		bankEdit.loadImage(completeName, p_transparentColor);
    	} catch (Exception e) {
        	completeName = imageName + p_filename + ".png";
    		bankEdit.loadImage(completeName, p_transparentColor);
    	}
	}
    
    /** This has to save GFX into texture, and only collision data into DEC file **/
    public void saveBank() {
        EasyBuffering buffer=new EasyBuffering(nb_motifs * TileBank.motifSize);
        for (int i=0;i<nb_motifs;i++) {
        	// Put the collision info
            TileInfo info = infosCollision.getTileInfo(256 * bankOrder + i);
            byte hash = (byte) TileInfo.Template.FULL.hashCode();
            if (info != null) {
            	hash = (byte) info.hashCode();
            }
        	buffer.put(hash);
        }
        EasyWritingFile file=new EasyWritingFile(buffer);
        file.saveFile(getName()+".DEC");
        
        // Create texture
        new TileTexture(bankEdit.gfxs).createTextureFromMotifBank(getName(), nb_motifs);
    }
    
    
    public Image generateImg(ByteBuffer texture) {
    	// Determine dimension
    	int maxX=0;
    	int maxY=0;
    	for (Point p : bank.coords) {
    		maxX=Math.max(p.x+16, maxX);
    		maxY=Math.max(p.y+16, maxY);
    	}
    	Image img=new BufferedImage(maxX, maxY, BufferedImage.TYPE_INT_RGB);
    	
    	// Draw the tiles as they are predefined
    	Point texPos = new Point(0, 0);
    	int idxTile = 0;
    	for (Point p : bank.coords) {
    		texPos.x = (idxTile % 16) * 16;
    		texPos.y = (idxTile / 16) * 16;
    		drawImage(img, p.x, p.y, texture, texPos);
    		idxTile++;
    	}
    	    	
    	img.flush();
    	
    	return img;
    }

    private void drawImage(Image img, int x, int y, ByteBuffer texture, Point texPos) {
    	
    	int size = 16 * 16;
    	int[] intData=new int[size];
    	int offset = texPos.y * (256*3) + texPos.x*3;
    	for (int i=0;i<size;i++) {
			int pixel = GFXBasics.readColor(texture,  i*3 + offset);
    		if (pixel == 0) {
    			pixel = Transparency.TRANSPARENCY_COLOR;
    		}
    		intData[i] = pixel;
    		if (i % 16 ==15) {
    			offset += 256*3 - 16*3;
    		}
    	}
    	
    	DataBuffer buffer = new DataBufferInt(intData, intData.length);
    	BufferedImage tempImg=new BufferedImage(16,16, BufferedImage.TYPE_INT_RGB);
    	WritableRaster raster = Raster.createWritableRaster(tempImg.getSampleModel(), buffer, null);
    	tempImg.setData(raster);

    	img.getGraphics().drawImage(tempImg, x, y, null);
    	  	
    }
}
