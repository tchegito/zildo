/**
 * Legend of Zildo
 * Copyright (C) 2006-2012 Evariste Boussaton
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
	public static final int BANK_COPYSCREEN = 6;	// Just one sprite : screen sized
	public static final int BANK_FONTES2 = 7;
	public static final int BANK_ZILDOOUTFIT = 8;
	
		// Class variables
	protected List<SpriteModel> models;

	protected short[] sprites_buf;
	protected int nSprite;
	protected String name;
	
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
		models.clear();
	}
	
	// Load a sprites bank into memory
	public void charge_sprites(String filename)
	{
		EasyBuffering file=Zildo.pdPlugin.openFile(filename);
		short a,b;
	    int k;
	
		k=0;
	
		// Pour les MFC, on fait plutôt du new
		sprites_buf=new short[64000];
		name=filename;
	
		while (!file.eof()) {
			a=file.readUnsignedByte();
			b=file.readUnsignedByte();
	
			// Build a temporary sprite and add it to the list
			SpriteModel spr=new SpriteModel(a, b, k);
			models.add(spr);
	
			int taille=b*a;
			file.readUnsignedBytes(sprites_buf, k, taille);
	
			k+=taille;
			nSprite++;
		}
	}
	
	public SpriteModel get_sprite(int nspr)
	{
		// Get the right sprite
		return models.get(nspr);
	}
	
	public long modifyPixel(int nSpr, int color)
	{
		long toaff=-1;
		String banqueGarde="pnj.spr";
		if (name.equals(banqueGarde) && nSpr>=20 && nSpr<=34) {
			if (color == 198) {
				toaff=0xff + (127 << 24);
			} else if (color == 199) {
				toaff=0xff00 + (127 << 24);
			}
		}
		return toaff;
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
