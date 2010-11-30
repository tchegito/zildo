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

package zildo.fwk.bank;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import zildo.fwk.file.EasyReadingFile;
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
	
	protected Logger logger=Logger.getLogger("SpriteBank");
	
	public static final int MAX_BANQUES = 8;

	public static final int BANK_ZILDO = 0;
	public static final int BANK_ELEMENTS = 1;
	public static final int BANK_PNJ = 2;
	public static final int BANK_FONTES = 3;
	public static final int BANK_PNJ2 = 4;
	public static final int BANK_COPYSCREEN = 5;	// Just one sprite : screen sized
	public static final int BANK_FONTES2 = 6;
	
		// Class variables
	private long spr_size;
	private List<SpriteModel> tab_sprite;

	private short[] sprites_buf;
	private int nSprite;
	private	String name;
	
	public SpriteBank()
	{
		this.nSprite=0;
		this.spr_size=0;
		tab_sprite=new ArrayList<SpriteModel>();
		this.sprites_buf=null;
	
		logger.log(Level.INFO, "Creating SpriteBank");
	}

	public SpriteBank(short[] sprites_buf_, long spr_size, List<SpriteModel> tab_sprite)
	{
		this.nSprite=tab_sprite.size();
		this.sprites_buf=sprites_buf_;
		this.spr_size=spr_size;
		this.tab_sprite=tab_sprite;
	
		logger.log(Level.INFO, "Creating SpriteBank");
	}
	
	// Assignment operator
	public SpriteBank(SpriteBank original) {
		this.sprites_buf=original.sprites_buf;
		this.spr_size=original.spr_size;
		this.tab_sprite=original.tab_sprite;
		this.name=original.name;
		this.nSprite=original.nSprite;
	}
	
	@Override
	public void finalize()
	{
		logger.log(Level.INFO, "Deleting SpriteBank");
	
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
		tab_sprite.clear();
	}
	
	// Load a sprites bank into memory
	public void charge_sprites(String filename)
	{
		EasyReadingFile file=new EasyReadingFile(filename);
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
			tab_sprite.add(spr);
	
			int taille=b*a;
			file.readUnsignedBytes(sprites_buf, k, taille);
	
			k+=taille;
			nSprite++;
		}
	}
	
	public SpriteModel get_sprite(int nspr)
	{
		// Get the right sprite
		return tab_sprite.get(nspr);
	}
	
	public long modifyPixel(int nSpr, int color)
	{
		long toaff=-1;
		String banqueGarde="PNJ.SPR";
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
		tab_sprite.add(spr);
	
		nSprite++;
	}

	public long getSpr_size() {
		return spr_size;
	}

	public void setSpr_size(long spr_size) {
		this.spr_size = spr_size;
	}

	public List<SpriteModel> getTab_sprite() {
		return tab_sprite;
	}

	public void setTab_sprite(List<SpriteModel> tab_sprite) {
		this.tab_sprite = tab_sprite;
	}

	public short[] getSprites_buf() {
		return sprites_buf;
	}
	
	public short getSprites_buf(long pos) {
		return sprites_buf[(int) pos];
	}

	public void setSprites_buf(short[] sprites_buf) {
		this.sprites_buf = sprites_buf;
	}

	public int getNSprite() {
		return nSprite;
	}

	public void setNSprite(int sprite) {
		nSprite = sprite;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
