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

package zildo.monde.map;

import zildo.fwk.file.EasyBuffering;
import zildo.fwk.file.EasySerializable;


/**
 * Represents a 16x16 region on a map. 
 * 
 * On each case, we can have 2 background tiles overlayed, and 1 foreground. 
 * Only 1 background tile is mandatory, other ones are optional.
 * 
 * @author Tchegito
 *
 */
public class Case implements EasySerializable {

	private Tile back;
	private Tile back2;
	private Tile fore;
	
	private Angle transition; // Back to fore ground	(n_banque | 64)
	
	private int z;	// Result of analysis
	
	
	public Case(Case p_original) {
		this.back = p_original.getBackTile().clone();
		if (p_original.getForeTile() != null) {
			this.fore = p_original.getForeTile().clone();
		}
		this.z = p_original.z;
	}

	public Tile getBackTile() {
		return back;
	}
	
	public void setBackTile(Tile p_tile) {
		this.back = p_tile;
	}
	
	public Tile getForeTile() {
		return fore;
	}
	
	public void setForeTile(Tile p_tile) {
		this.fore = p_tile;
	}
	
	public Case() {
		back = new Tile(0, 0, this);
		fore = null;
		z=0;
	}

	public int getAnimatedMotif(int compteur_animation)
	{
		int motif=back.index;
		switch (back.bank)
		{
		// On gère les sprites animés
			case 0:
			// L'eau
				if (motif>=108 && motif<=130 && motif !=129) {
					if (compteur_animation > 40)
						motif+=100;
					else if (compteur_animation > 20)
						motif+=100+23;
				} else if (motif==52 || motif==53) {
					//Les fleurs
					if (compteur_animation > 40)
						motif+=3;
					else if (compteur_animation > 20)
						motif+=111;
				}
				break;
	
	
			case 1:
				if (motif>=235 && motif<=237)
					motif+=(compteur_animation / 20)*3;
				break;
	
			case 2:
				switch (motif) 
				{
					case 174:    
						if (compteur_animation>=20)
							motif=175+(compteur_animation / 20);
						break;
					case 142:
						if (compteur_animation>=40)
							motif=178;
						else if (compteur_animation>=20)
							motif=194;
						break;
					case 144:
						if (compteur_animation>=40)
							motif=179;
						else if (compteur_animation>=20)
							motif=195;
						break;
				    case 235:
						motif=235+(compteur_animation / 20);
				}
				break;
			case 3:
			// L'eau dans les grottes/palais
				if (motif==78)
					motif=78+(compteur_animation / 20);
				break;
	
			case 5:
				// FORET3.DEC animation d'eau supplémentaire
				if (motif>=96 && motif<=104)
					motif+=(compteur_animation / 20)*3;
				break;
		}
	
		// Return computed motif
		return motif;
	}
	


	/**
	 * Serialize useful fields from this map case.
	 * @param p_buffer
	 */
	public void serialize(EasyBuffering p_buffer) {
		int isMasque = fore != null ? 128 : 0;
		int isTransition = transition != null ? 64 : 0;
		int isBack2 = back2 != null ? 32 : 0;
		p_buffer.put((byte) back.index);
		p_buffer.put((byte) (back.bank | isMasque | isTransition | isBack2));
		if (back2 != null) {
			p_buffer.put((byte) back2.index);
			p_buffer.put((byte) back2.bank);
		}
		if (fore != null) {
			p_buffer.put((byte) fore.index);
			p_buffer.put((byte) fore.bank);
		}
		if (transition != null) {
			p_buffer.put((byte) transition.value);
		}
	}
	
	/**
	 * Deserialize a byte buffer into a Case
	 * @param p_buffer
	 * @return SpriteEntity
	 */
	public static Case deserialize(EasyBuffering p_buffer) {
		Case mapCase=new Case();
		int index1 = p_buffer.readUnsignedByte();
		int bank1 = p_buffer.readUnsignedByte();
		if ((bank1 & 128) != 0) {
			int index2 = p_buffer.readUnsignedByte();
			int bank2 = p_buffer.readUnsignedByte();
			if ((bank1 & 128) != 0) { //bank2 != 0 || index2 != 0) {
				mapCase.setForeTile(new Tile(bank2, index2, mapCase));
			}
		}
		if ((bank1 & 64) != 0) {
			mapCase.setTransition(Angle.fromInt(p_buffer.readUnsignedByte()));
		}
		if ((bank1 & 32) != 0) {
			int indexB2 = p_buffer.readUnsignedByte();
			int bankB2 = p_buffer.readUnsignedByte();
			mapCase.setBackTile2(new Tile(bankB2, indexB2, mapCase));
		}
		bank1&=31;
		mapCase.setBackTile(new Tile(bank1, index1, mapCase));
		return mapCase;
	}
	
	/**
	 * Deserialize a byte buffer into a Case
	 * @param p_buffer
	 * @return SpriteEntity
	 */
	public static Case oldDeserialize(EasyBuffering p_buffer) {
		Case mapCase=new Case();
		int index1 = p_buffer.readUnsignedByte();
		int bank1 = p_buffer.readUnsignedByte();
		int index2 = p_buffer.readUnsignedByte();
		int bank2 = p_buffer.readUnsignedByte();
		mapCase.setBackTile(new Tile(bank1, index1, mapCase));
		if (bank1 > 63) {
			mapCase.setForeTile(new Tile(bank2, index2, mapCase));
		}
		return mapCase;
	}
	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public Angle getTransition() {
		return transition;
	}


	public void setTransition(Angle transition) {
		this.transition = transition;
	}
	
	public Tile getBackTile2() {
		return back2;
	}

	public void setBackTile2(Tile back2) {
		this.back2 = back2;
	}

	
}