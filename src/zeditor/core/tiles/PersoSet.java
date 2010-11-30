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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import zeditor.windows.managers.MasterFrameManager;
import zildo.fwk.bank.SpriteBank;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.server.EngineZildo;

/**
 * @author Tchegito
 *
 */
@SuppressWarnings("serial")
public class PersoSet extends ImageSet {

	private final int width = 320;
	private final int height = 512;
	
    public PersoSet(String p_tileName, MasterFrameManager p_manager) {
    	super(p_tileName, p_manager);
    	
    	initImage();
	}
    
    public void initImage() {
        currentTile=new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    	currentTile.getGraphics();
    	
    }
    
    /**
     * Display tile, with or without mask
     * @param i
     * @param j
     * @param nBank
     * @param nMotif
     * @param masque
     */
    private void drawPerso(int i, int j, int nBank, int nMotif, boolean masque) {
    	
    	SpriteBank pnjBank=EngineZildo.spriteManagement.getSpriteBank(SpriteBank.BANK_PNJ);
    	short[] data=pnjBank.getSpriteGfx(PersoDescription.BANDIT.getNSpr());
    }
    
	@Override
	protected void buildSelection() {

	}

	@Override
	protected void specificPaint(Graphics2D p_g2d) {
		
	}

}
