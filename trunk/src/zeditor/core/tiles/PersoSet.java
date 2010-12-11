/**
 * Legend of Zildo
 * Copyright (C) 2006-2010 Evariste Boussaton
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

package zeditor.core.tiles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.util.vector.Vector4f;

import zeditor.core.selection.PersoSelection;
import zeditor.windows.managers.MasterFrameManager;
import zildo.fwk.bank.SpriteBank;
import zildo.fwk.gfx.GFXBasics;
import zildo.monde.map.Angle;
import zildo.monde.map.Zone;
import zildo.monde.sprites.SpriteModel;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoNJ;
import zildo.monde.sprites.persos.Perso.PersoInfo;
import zildo.server.EngineZildo;

/**
 * @author Tchegito
 *
 */
@SuppressWarnings("serial")
public class PersoSet extends ImageSet {

	public final static int width = 320;
	public final static int height = 320;
	
    protected Map<Zone, Object> objectsFromZone;

    public PersoSet(String p_tileName, MasterFrameManager p_manager) {
    	super(p_tileName, p_manager);
    	
    	objectsFromZone=new HashMap<Zone, Object>();
    	initImage();
	}
    
    public void initImage() {
        currentTile=new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    	currentTile.getGraphics();
    	
    	int posX=0;
    	int posY=0;
    	int maxY=0;
    	for (PersoDescription perso : PersoDescription.values()) {
    		
        	SpriteBank pnjBank=EngineZildo.spriteManagement.getSpriteBank(perso.getBank());
        	int nSpr=perso.first() % 128;
    		SpriteModel model=pnjBank.get_sprite(nSpr);
    		drawPerso(posX, posY, pnjBank, perso, false);
    		
    		// Store this zone into the list
    		Zone z=new Zone(posX, posY, model.getTaille_x(), model.getTaille_y());
    		selectables.add(z);
    		objectsFromZone.put(z, nSpr);
    		
    		posX+=model.getTaille_x();
    		if (posX > width) {
    			posX=0;
    			posY+=maxY;
    		}
    		if (model.getTaille_y() > maxY) {
    			maxY=model.getTaille_y();
    		}
    	}
    }
    
	final Vector4f colMasque=new Vector4f(192, 128, 240, 1);
	
    /**
     * Display tile, with or without mask
     * @param i
     * @param j
     * @param nBank
     * @param nMotif
     * @param masque
     */
    private void drawPerso(int i, int j, SpriteBank pnjBank, PersoDescription nMotif, boolean masque) {

    	int nSpr=nMotif.first() % 128;
    	SpriteModel model=pnjBank.get_sprite(nSpr);
    	short[] data=pnjBank.getSpriteGfx(nSpr);
    	
    	Graphics2D gfx2d=(Graphics2D) currentTile.getGraphics();
    	
    	for (int y=0;y<model.getTaille_y();y++) {
        	for (int x=0;x<model.getTaille_x();x++) {
        		int offset=y*model.getTaille_x()+x;
        		int a=data[offset];
        		if (a != 255) {
            		Vector4f col=GFXBasics.getColor(a);
        			gfx2d.setColor(new Color(col.x / 256, col.y / 256, col.z / 256));
        			gfx2d.drawLine(x+i,y+j,x+i,y+j);
        		}
        	}
    	}
    	
    }
    
	@Override
	protected void buildSelection() {
		Zone z=getObjectOnClick(startPoint.x+1, startPoint.y+1);
		if (z != null) {
			int nSpr=(Integer) objectsFromZone.get(z);
			PersoDescription desc=PersoDescription.fromNSpr(nSpr);
			if (desc != null) {
				// Initialize a virtual character
				Perso temp=EngineZildo.persoManagement.createPerso(desc, 0, 0, 0, "new", Angle.NORD.value);
				temp.setInfo(PersoInfo.NEUTRAL);
				temp.initPersoFX();
				
		        currentSelection = new PersoSelection(temp);
		        manager.setPersoSelection((PersoSelection) currentSelection);
			}
		}
	}

	@Override
	protected void specificPaint(Graphics2D p_g2d) {
		
	}

}
