/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
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

package zildo.client.gui;

import java.util.ArrayList;

import zildo.client.ClientEngineZildo;
import zildo.client.SpriteDisplay;
import zildo.fwk.gfx.EngineFX;
import zildo.monde.sprites.Reverse;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.desc.SpriteDescription;

public class GUISpriteSequence extends ArrayList<SpriteEntity> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean sequenceDrawn;

	// ////////////////////////////////////////////////////////////////////
	// Construction/Destruction
	// ////////////////////////////////////////////////////////////////////

	public GUISpriteSequence() {
		sequenceDrawn = false;
		this.clear();
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// isDrawn
	// /////////////////////////////////////////////////////////////////////////////////////
	public boolean isDrawn() {
		return sequenceDrawn;
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// addSprite
	// /////////////////////////////////////////////////////////////////////////////////////
	// -ask sprite management to add sprite in the engine with given parameters
	// /////////////////////////////////////////////////////////////////////////////////////
	public SpriteEntity addSprite(int p_nBank, int p_nSpr, int x, int y,
			boolean visible, int alpha) {
		SpriteDisplay spriteDisplay = ClientEngineZildo.spriteDisplay;
		SpriteEntity entity = spriteDisplay.spawnFont(p_nBank, p_nSpr, x, y,
				visible);
		entity.setAlpha(alpha);
		entity.setSpecialEffect(EngineFX.FOCUSED);
		
		this.add(entity);
		sequenceDrawn = true;

		return entity;
	}

	public SpriteEntity addSprite(SpriteDescription p_desc, int x, int y) {
		return addSprite(p_desc.getBank(), p_desc.getNSpr(), x, y, true, 255);
	}
	
	public SpriteEntity addSprite(SpriteDescription p_desc, int x, int y, int alpha) {
		return addSprite(p_desc.getBank(), p_desc.getNSpr(), x, y, true, alpha);
	}
	
	public SpriteEntity addSprite(SpriteDescription p_desc, int x, int y, Reverse rev) {
		SpriteEntity entity = addSprite(p_desc.getBank(), p_desc.getNSpr(), x, y, true, 255);
		entity.reverse = rev;
		return entity;
	}

	public SpriteEntity addSprite(SpriteDescription p_desc, int x, int y, Reverse rev, int alpha) {
		SpriteEntity entity = addSprite(p_desc.getBank(), p_desc.getNSpr(), x, y, true, alpha);
		entity.reverse = rev;
		return entity;
	}
	
	// /////////////////////////////////////////////////////////////////////////////////////
	// clear
	// /////////////////////////////////////////////////////////////////////////////////////
	// -ask sprite management to remove each sprites added in this sequence
	// /////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void clear() {
		SpriteDisplay spriteDisplay = ClientEngineZildo.spriteDisplay;
		for (SpriteEntity entity : this) {
			spriteDisplay.deleteSprite(entity);
		}
		super.clear();

		sequenceDrawn = false;
	}

}