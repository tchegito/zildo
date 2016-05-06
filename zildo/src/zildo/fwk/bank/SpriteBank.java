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
import java.util.List;

import zildo.Zildo;
import zildo.fwk.file.EasyBuffering;
import zildo.monde.sprites.SpriteModel;

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
	public static final int BANK_ZILDOOUTFIT = 8;
	
		// Class variables
	protected List<SpriteModel> models;

	protected short[] sprites_buf;
	protected int nSprite;
	protected String name;
	
	private boolean toModif = false;	// For optimization reason : pnj.spr will be modified
	
	public SpriteBank()
	{
		this.nSprite=0;
		models=new ArrayList<SpriteModel>();
		this.sprites_buf=null;
	}
	
	@Override
	public void finalize()
	{
		// No need to do that in Java
		
		// sprites_buf is already deleted in SpriteManagement
		//delete sprites_buf;
	/*
		std::list<Sprite*>::iterator  it=tab_sprite.begin();
		for (it;it!=tab_sprite.end();)
		{
			Sprite* sprite=*it;
			delete sprite;
			nSprite--;
			it++;
		}
	*/
		//models.clear();
	}
	
	// Load a sprites bank into memory
	public void charge_sprites(String filename)
	{
		EasyBuffering file=Zildo.pdPlugin.openFile(filename);
		short a,b;
	    int k;
	
		k=0;
	
		// Theoretically, max size will be 256x256 = 65535
		sprites_buf=new short[65535];
		name=filename;
	
		while (!file.eof()) {
			a=file.readUnsignedByte();
			b=file.readUnsignedByte();
	
			// Still theory : 256 won't fit in a byte, so 0 means probably 256
			if (a == 0) a = 256;
			if (b == 0) b = 256;
			// Build a temporary sprite and add it to the list
			SpriteModel spr=new SpriteModel(a, b, k);
			models.add(spr);
	
			int taille=b*a;
			file.readUnsignedBytes(sprites_buf, k, taille);
	
			k+=taille;
			nSprite++;
		}
		
		toModif = name.equals("pnj.spr") || name.equals("pnj2.spr");
	}
	
	public SpriteModel get_sprite(int nspr)
	{
		// Get the right sprite
		return models.get(nspr);
	}
	
	public long modifyPixel(int nSpr, int color)
	{
		long toaff=-1;
		if (toModif) {
			if (name.equals("pnj.spr")) {
				if (nSpr>=20 && nSpr<=34) {
					if (color == 198) {
						toaff = 0xff + (127 << 24);
					} else if (color == 199) {
						toaff = 0xff00 + (127 << 24);
					}
				}
			} else { // name = pnj2.spr
				if ((nSpr>=(244-128) && nSpr<=(249-128)) || (nSpr >= 256-128 && nSpr <= 258-128)) {
					if (color == 172) {
						toaff = 0xff + (127 << 24);
					} else if (color == 171) {
						toaff = 0xff00 + (127 << 24);
					}
				}
			}
		}
		return toaff;
	}
	
	public int whichPalette(int nSpr) {
		if (name.equals("pnj3.spr") && ((nSpr >= 66))) {	// Dragon => Decroded palette
			return 2;
		} else if (name.equals("elem.spr") && (nSpr == 245 || nSpr == 246 || nSpr == 247)) {
			return 2;
		} else {
			return 1;
		}
	}
	
	/**
	 * Return a short[] representing the sprite bitmap
	 * @param nSpr nth sprite in the bank
	 * @return short[]
	 */
	public short[] getSpriteGfx(int nSpr) {
		SpriteModel spr=get_sprite(nSpr);
		
		int size=spr.getTaille_x() * spr.getTaille_y();
		short[] coupe=new short[size];
		int a=spr.getOffset();
		System.arraycopy(sprites_buf, a, coupe, 0, size);
		return coupe;
	}
	
	public void addSpriteReference(int texPosX, int texPosY, int sizeX, int sizeY)
	{
		SpriteModel spr=new SpriteModel(sizeX, sizeY, texPosX, texPosY);
		models.add(spr);
	
		nSprite++;
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

	public short[] getSprites_buf() {
		return sprites_buf;
	}
	
	public void freeTempBuffer() {
		//sprites_buf = null;		// Optim for android but wrong for zeditor
	}
}
