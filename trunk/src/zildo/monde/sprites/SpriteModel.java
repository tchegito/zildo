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

package zildo.monde.sprites;

import zildo.Zildo;
import zildo.fwk.Identified;





////////////////////////////////////////////////////////////////////////////////////////
//
// S p r i t e
//
////////////////////////////////////////////////////////////////////////////////////////
// Model class for a graphic sprite from a given bank.
////////////////////////////////////////////////////////////////////////////////////////
//
// Technically:
//-------------
//* Contains informations about sprite location on the bank's texture.

public class SpriteModel extends Identified {
	static public final int TEXTER_BORDGAUCHE = 50;
	static public final int TEXTER_BORDDROIT = 270;

	private	int taille_x,taille_y;
	private int texPos_x,texPos_y;			// Position sur la texture de sprite

	private	int offset;
	
	private static SpriteModel screenSized=null;
	
	static final SpriteModel getScreenSized() {
		if (screenSized == null) {
			screenSized=new SpriteModel();
			screenSized.taille_x=320; //Zildo.viewPortX*2;
			screenSized.taille_y=-Zildo.viewPortY;
			screenSized.texPos_x=0;
			screenSized.texPos_y=0;
		}
		return screenSized;
	}
	
	public SpriteModel() {
		
	}
	
	public SpriteModel(int taille_x, int taille_y, int offset) {
		this.taille_x=taille_x;
		this.taille_y=taille_y;
		this.offset=offset;
		initializeId();
	}
	
	public SpriteModel(int taille_x, int taille_y, int texPos_x, int texPos_y) {
		this.taille_x=taille_x;
		this.taille_y=taille_y;
		this.texPos_x=texPos_x;
		this.texPos_y=texPos_y;
		initializeId();
	}
	
	public int getTaille_x() {
		return taille_x;
	}

	public void setTaille_x(int taille_x) {
		this.taille_x = taille_x;
	}

	public int getTaille_y() {
		return taille_y;
	}

	public void setTaille_y(int taille_y) {
		this.taille_y = taille_y;
	}

	public int getTexPos_x() {
		return texPos_x;
	}

	public void setTexPos_x(int texPos_x) {
		this.texPos_x = texPos_x;
	}

	public int getTexPos_y() {
		return texPos_y;
	}

	public void setTexPos_y(int texPos_y) {
		this.texPos_y = texPos_y;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}
}