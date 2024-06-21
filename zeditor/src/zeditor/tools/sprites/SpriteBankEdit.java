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

import java.util.Iterator;

import zeditor.tools.builder.Modifier;
import zeditor.tools.builder.texture.TileTexture;
import zeditor.tools.tiles.Banque;
import zeditor.tools.tiles.GraphChange;
import zildo.client.gui.GUIDisplay;
import zildo.fwk.bank.SpriteBank;
import zildo.fwk.db.Identified;
import zildo.fwk.file.EasyBuffering;
import zildo.fwk.file.EasyWritingFile;
import zildo.monde.sprites.SpriteModel;
import zildo.monde.util.Zone;

/**
 * Designed for modifying sprite bank.<br/>
 * 
 * Features:<ul>
 * <li>uses an external image to pick sprites (see {@link #loadImage} and {@link #addSprFromImage})</li>
 * <li>remove sprite</li>
 * <li>add new one</li>
 * </ul>
 * @author Tchegito
 *
 */
public class SpriteBankEdit extends SpriteBank {

	protected BankEdit bankEdit;
	
	protected SpriteBank bank;
	
    public SpriteBankEdit() {
    	this(null);
    }
    public SpriteBankEdit(SpriteBank p_bank) {
    	bank = p_bank;
    	if (p_bank != null) {
	        models=p_bank.getModels();
	        name=p_bank.getName();
	        nSprite=p_bank.getNSprite();
    	}
    	
        bankEdit = new BankEdit();
        
        // Build all graphics into a single list
        for (int i=0;i<nSprite;i++) {
            bankEdit.gfxs.add(new int[] {});
        }
        Identified.resetCounter(SpriteModel.class);
    }
    
    public void addSpr(int p_position, int p_tailleX, int p_tailleY, Zone borders, int[] p_gfx) {
        SpriteModel model=new SpriteModel(p_tailleX, p_tailleY, borders);
        bankEdit.gfxs.add(p_position, p_gfx);
        models.add(p_position, model);
        nSprite++;
    }
    
    public void setSpr(int p_position, int p_tailleX, int p_tailleY, Zone borders, int[] p_gfx) {
        SpriteModel model=new SpriteModel(p_tailleX, p_tailleY, borders);
        bankEdit.gfxs.set(p_position, p_gfx);
        models.set(p_position, model);
        nSprite++;
    }
    
    public void removeSpr(int p_position) {
        models.remove(p_position);
        bankEdit.gfxs.remove(p_position);
        nSprite--;
    }
   
    public void clear() {
    	while (nSprite != 0 && bankEdit.gfxs.size() > 0) {
    		removeSpr(0);
    	}
    }
    
    public void fillNSprite(int number) {
    	for (int i=0;i<number;i++) {
	    	bankEdit.gfxs.add(new int[] {});
	    	models.add(new SpriteModel(0, 0));
    	}
    }
    
    public void addSprFromImage(int p_position, Zone z) {
		// Extract sprite from image
		int[] sprite = bankEdit.getRectFromImage(z.x1, z.y1, z.x2, z.y2);
		addSpr(p_position, z.x2, z.y2, zoneBorders(z), sprite);
	}
   
    public void setSprFromImage(int p_position, Zone z) {
		// Extract sprite from image
		int[] sprite = bankEdit.getRectFromImage(z.x1, z.y1, z.x2, z.y2);
		setSpr(p_position, z.x2, z.y2, zoneBorders(z), sprite);
	}
    
    private Zone zoneBorders(Zone z) {
    	if (z instanceof ZoneO) {
    		ZoneO zo = (ZoneO) z;
    		return zo.getOffsetZone();
    	}
    	return null;
    }
    
    public void loadImage(String p_filename, int p_transparentColor) {
		String imageName=Banque.PKM_PATH;
    	// New engine with free tiles
    	// 1) Try with folder containing free tiles
    	String completeName = imageName + "../FreeGraph/" + p_filename + ".png";
    	try {
    		bankEdit.loadImage(completeName, p_transparentColor);
    	} catch (Exception e) {
        	completeName = imageName + p_filename + ".png";
    		bankEdit.loadImage(completeName, p_transparentColor);
    	}
	}
    
    public void saveBank() {
    	// Calculate texture position for each sprite
        TileTexture tt = new TileTexture(bankEdit.gfxs);
		tt.createModelsFromSpriteBank(this);
		
        EasyBuffering buffer=new EasyBuffering(80000);
        for (int i=0;i<nSprite;i++) {
            SpriteModel model=models.get(i);
            buffer.put((byte) model.getTaille_x());
            buffer.put((byte) model.getTaille_y());
            buffer.put((byte) model.getTexPos_x());
            buffer.put((byte) model.getTexPos_y());
            Zone offsets = model.getEmptyBorders();
            if (offsets == null) {
            	buffer.put((byte) 0);
            } else {
                buffer.put((byte) (offsets.y1 | 128));
                buffer.put((byte) offsets.x1);
                buffer.put((byte) offsets.x2);
            }
        }
        EasyWritingFile file=new EasyWritingFile(buffer);
        file.saveFile(getName());
        
        // Save texture
        tt.createTextureFromSpriteBank(this);
    }
    
    /**
     * Returns the width of an element starting at given coordinates.<br/>
     * We assume that element is ending when there's an entire line made with transparent color at his right.
     * @param p_startX
     * @param p_startY
     * @param p_height
     * @return int
     */
	public int getWidth(int p_startX, int p_startY, int p_height) {
		int width = 0;
		while (p_startX < bankEdit.getImageWidth() && bankEdit.isLineFilled(p_startX + width, p_startY, p_height)) {
			width++;
			if (width == 420) {
				System.out.println("rate");
			}
		}
		return width;
	}
	
	public void addSpritesFromBank(SpriteBanque p_bank) {
   	 Zone[] elements=p_bank.getZones();
   	 Iterator<GraphChange> itChanges = p_bank.getPkmChanges().iterator();
   	 GraphChange current = null;
	 int startSpr=getNSprite();
	 int i=0;
     for (Zone z : elements) {
    	 if (current == null && itChanges.hasNext()) {
    		 current = itChanges.next();
    	 }
    	 if (current != null) {
    		 if (current.nTile == i) {
    			 loadImage(current.imageName, current.transparency ? -1 : Modifier.COLOR_BLUE);
    			 current = null;
    		 }
    	 }
    	 try {
    		 addSprFromImage(startSpr + i, z);
    	 } catch (Exception e) {
    		 throw new RuntimeException("Unable to insert sprite "+i+"/"+elements.length+" on bank "+p_bank, e);
    	 }
    		 i++;
      }
	}
	
	public void captureFonts(int posY, int fontHeight, String chars, int constantWidth, int heightSpace) {
		// Capture the fonts
		int startX = 0;
		int startY = posY;
		int nTentativ = 0;
		final int imgWidth = bankEdit.getImageWidth();
		final int offsetnSprite = nSprite;
		int width;
		for (int i = 0; i < GUIDisplay.transcoChar.length(); i++) {
			// Get size
			if (constantWidth != 0) {
				width = constantWidth;
			} else {
				width = getWidth(startX, startY, fontHeight);
			}
			int offsetFont = i;	// Default i-nth font
			if (chars != null) {
				offsetFont = GUIDisplay.transcoChar.indexOf(chars.charAt(i));
			}
			if (width > 1) {
				//System.out.println(c);
				if (chars == null) {
					addSprFromImage(offsetnSprite + offsetFont, new Zone(startX, startY, width, fontHeight));
				} else {
					setSprFromImage(offsetnSprite + offsetFont, new Zone(startX, startY, width, fontHeight));
				}

				//System.out.println(startX + " , " + startY + " size=" + width);

				startX += width + 1;
				if (constantWidth > 0) {
					startX--;	// No need to space fonts, if width is constant
				}
				nTentativ = 0;
			} else {
				if (nTentativ == 5 || startX >= imgWidth) {
					startX = 0;
					startY += fontHeight + heightSpace;
					i--;
					nTentativ = 0;
				} else {
					nTentativ++;
					startX++;
					i--;
				}
			}
		}
	}
}
