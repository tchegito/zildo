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

package zeditor.core.tiles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Vector4f;

import zeditor.core.selection.PersoSelection;
import zeditor.core.selection.SpriteSelection;
import zeditor.windows.managers.MasterFrameManager;
import zildo.fwk.bank.SpriteBank;
import zildo.fwk.gfx.GFXBasics;
import zildo.monde.map.Angle;
import zildo.monde.map.Zone;
import zildo.monde.sprites.SpriteModel;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.desc.SpriteDescription;
import zildo.monde.sprites.desc.ZPersoLibrary;
import zildo.monde.sprites.desc.ZSpriteLibrary;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.Perso.PersoInfo;
import zildo.server.EngineZildo;

/**
 * @author Tchegito
 *
 */
@SuppressWarnings("serial")
public class SpriteSet extends ImageSet {

	public final static int width = 320;
	public final static int height = 200;
	
    protected Map<Zone, SpriteDescription> objectsFromZone;

    boolean perso;
    
    final static ZPersoLibrary persoLibrary=new ZPersoLibrary();
    final static ZSpriteLibrary spriteLibrary=new ZSpriteLibrary();
    
    /**
     * Initialize the object
     * @param p_perso TRUE=Perso / FALSE=Element
     * @param p_manager
     */
    public SpriteSet(boolean p_perso, MasterFrameManager p_manager) {
    	super(null, p_manager);

    	perso=p_perso;
    	
    	objectsFromZone=new HashMap<Zone, SpriteDescription>();
    	initImage(p_perso ? persoLibrary : spriteLibrary);
	}
    
    public <T extends SpriteDescription> void initImage(List<SpriteDescription> p_bankDesc) {
        currentTile=new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    	currentTile.getGraphics();

    	
    	if (false) {	// for test, now
    		p_bankDesc=prepareListFromBank(4);
    	}

    	displayListSprites(p_bankDesc);
    }
    
    /**
     * Return a virtual list for displaying an entire bank
     * @param p_numBank
     * @return List<SpriteDescription>
     */
    private List<SpriteDescription> prepareListFromBank(final int p_numBank) {
    	// Display an entire bank
    	List<SpriteDescription> list=new ArrayList<SpriteDescription>();
    	SpriteBank bank=EngineZildo.spriteManagement.getSpriteBank(p_numBank);
    	for (int i=0;i<bank.getNSprite();i++) {
    		final int n=i;
    		list.add(new SpriteDescription() {
    			public int getBank() {
    				return p_numBank;
    			}
    			public int getNSpr() {
    				return n;
    			}
    		});
    	}
    	return list;
    }
    
    private void displayListSprites(List<SpriteDescription> p_list) {
    	
    	int posX=0;
    	int posY=0;
    	int maxY=0;
    	for (SpriteDescription perso : p_list) {
    		
        	SpriteBank bank=EngineZildo.spriteManagement.getSpriteBank(perso.getBank());
        	int nSpr=perso.getNSpr();
        	if (bank.getName().equals("PNJ2.SPR")) {
        		nSpr=nSpr % 128;
        	}
    		SpriteModel model=bank.get_sprite(nSpr);
    		
    		int sizeX=model.getTaille_x();
    		if (posX + sizeX > width) {
    			posX=0;
    			posY+=maxY;
    			maxY=0;
    		}
    		if (model.getTaille_y() > maxY) {
    			maxY=model.getTaille_y();
    		}
    		
    		drawPerso(posX, posY, bank, nSpr, false);

    		// Store this zone into the list
    		Zone z=new Zone(posX, posY, model.getTaille_x(), model.getTaille_y());
    		selectables.add(z);
    		objectsFromZone.put(z, perso);
    		posX+=sizeX;
    	}
    }
	final Vector4f colMasque=new Vector4f(192, 128, 240, 1);
	
    /**
     * Display sprite, with or without mask
     * @param i
     * @param j
     * @param nBank
     * @param nMotif
     * @param masque
     */
    private void drawPerso(int i, int j, SpriteBank pnjBank, int nSpr, boolean masque) {

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
			SpriteDescription desc=objectsFromZone.get(z);
			if (desc != null) {
				if (perso) {
					PersoDescription persoDesc=(PersoDescription) desc;
					// Initialize a virtual character
					Perso temp=EngineZildo.persoManagement.createPerso(persoDesc, 0, 0, 0, "new", Angle.NORD.value);
					temp.setInfo(PersoInfo.NEUTRAL);
					temp.initPersoFX();
			        persoLibrary.initialize(temp);
					
			        currentSelection = new PersoSelection(temp);
			        manager.setPersoSelection((PersoSelection) currentSelection);
				} else {
					Element temp=new Element();
					temp.setNBank(desc.getBank());
					temp.setNSpr(desc.getNSpr());
					currentSelection=new SpriteSelection(temp);
					manager.setSpriteSelection((SpriteSelection) currentSelection);
				}
			}
		}
	}

	@Override
	protected void specificPaint(Graphics2D p_g2d) {
		
	}

}
