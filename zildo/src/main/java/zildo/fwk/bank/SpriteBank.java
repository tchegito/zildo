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

package zildo.fwk.bank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import zildo.Zildo;
import zildo.fwk.file.EasyBuffering;
import zildo.monde.sprites.SpriteModel;
import zildo.monde.sprites.SpriteStore;
import zildo.monde.util.Zone;

/**
//////////////////////////////////////////////////////////////////////
//SpriteBank
//////////////////////////////////////////////////////////////////////
//Class defining a list of stored sprites, called 'sprite bank'
//
//////////////////////////////////////////////////////////////////////
*/

public class SpriteBank {
	
	public static final int BANK_ZILDO = 0;
	public static final int BANK_ELEMENTS = 1;
	public static final int BANK_PNJ = 2;
	public static final int BANK_FONTES = 3;
	public static final int BANK_PNJ2 = 4;
	public static final int BANK_GEAR = 5;
	public static final int BANK_PNJ3 = 6;
	public static final int BANK_PNJ4 = 7;
	public static final int BANK_PNJ5 = 8;
	public static final int BANK_ZILDOOUTFIT = 9;
	
	private static final int COLOR_GARD1 = 178 << 16 | 146 << 8 | 251;
	private static final int COLOR_GARD2 = 81 << 16 | 113 << 8 | 203;

	private static final int COLOR_THIEF1 = 81 << 16 | 105 << 8 | 170;
	private static final int COLOR_THIEF2 = 146 << 16 | 170 << 8 | 235;

	
		// Class variables
	protected List<SpriteModel> models;

	protected int nSprite;
	protected String name;
	
	private boolean toModif = true;	// For optimization reason : pnj.spr will be modified
	
	public SpriteBank()
	{
		this.nSprite=0;
		models=new ArrayList<SpriteModel>();
	}
	
	// Load a sprites bank into memory
	public void charge_sprites(String filename)
	{
		EasyBuffering file=Zildo.pdPlugin.openFile(filename);
		short a,b;
		int texX=0, texY=0;
		
		// Theoretically, max size will be 256x256 -1 = 65535
		name=filename;
	
		while (!file.eof()) {
			a=file.readUnsignedByte();
			b=file.readUnsignedByte();
			texX = file.readUnsignedByte();
			texY = file.readUnsignedByte();
			
			int offY=file.readUnsignedByte();
			Zone emptyBorders = null;
			// If bit 7 is on, so we have 2 offset available for this sprite
			
			if ((offY & 128) != 0) {
				int offXLeft=file.readUnsignedByte();
				int offXRight=file.readUnsignedByte();
				int valOffY = offY & 127;
				if (valOffY > 64) valOffY = valOffY-128;	// Consider negative offset if > 64
				emptyBorders = new Zone(offXLeft, valOffY, offXRight, 0);
			}
			
			// Still theory : 256 won't fit in a byte, so 0 means probably 256
			if (a == 0) a = 256;
			if (b == 0) b = 256;
			// Build a temporary sprite and add it to the list
			SpriteModel spr=new SpriteModel(a, b, emptyBorders);
			spr.setTexPos_x(texX);
			spr.setTexPos_y(texY);
			models.add(spr);
			nSprite++;
		}
		
		toModif = name.equals("pnj.spr") || name.equals("pnj2.spr");
	}
	
	public SpriteModel get_sprite(int nspr)
	{
		// Get the right sprite
		return models.get(nspr);
	}
	
	public int modifyPixel(int nSpr, int color)
	{
		int toaff=color;
		if (toModif) {
			int col = color & 0xffffff;
			if (name.equals("pnj.spr")) {
				if (nSpr>=20 && nSpr<=34) {
					if (col == COLOR_GARD1) {
						toaff = 0xff + (127 << 24);
					} else if (col == COLOR_GARD2) {
						toaff = 0xff00 + (127 << 24);
					}
				}
			} else { // name = pnj2.spr
				if ((nSpr>=(244-128) && nSpr<=(249-128)) || (nSpr >= 256-128 && nSpr <= 258-128)) {
					if (col == COLOR_THIEF2) {
						toaff = 0xff + (127 << 24);
					} else if (col == COLOR_THIEF1) {
						toaff = 0xff00 + (127 << 24);
					}
				}
			}
		}
		return toaff;
	}

	public List<SpriteModel> getModels() {
		return models;
	}

	public int getNSprite() {
		return nSprite;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public int getIndex() {
		return Arrays.asList(SpriteStore.sprBankName).indexOf(name);
	}
}
