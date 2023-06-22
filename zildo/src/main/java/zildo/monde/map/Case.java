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

package zildo.monde.map;

import zildo.fwk.file.EasyBuffering;
import zildo.fwk.file.EasySerializable;
import zildo.monde.sprites.Reverse;
import zildo.monde.sprites.Rotation;


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

	public enum TileLevel {
		BACK, BACK2, FORE;
	}
	
	private Tile back;
	private Tile back2;
	private Tile fore;
	
	private int z;	// Result of analysis
	
	private boolean modified;
	private int back2RemovedBank;	// Set when back2 tile just been removed, to keep previous bank
	
	public Case(Case p_original) {
		this.back = p_original.getBackTile().clone();
		if (p_original.getBackTile2() != null) {
			this.back2 = p_original.getBackTile2().clone();
		}
		if (p_original.getForeTile() != null) {
			this.fore = p_original.getForeTile().clone();
		}
		this.z = p_original.z;
	}

	public Tile getBackTile() {
		return back;
	}
	
	public void setBackTile(Tile p_tile) {
		p_tile.previousBank = back.bank;
		this.back = p_tile;
		modified = true;
	}
	
	public Tile getForeTile() {
		return fore;
	}
	
	public void setForeTile(Tile p_tile) {
		if (fore != null && p_tile != null) {
			p_tile.previousBank = fore.bank;
		}
		this.fore = p_tile;
		modified = true;
	}
	
	public Case() {
		back = new Tile(0, 0, this);
		fore = null;
		z=0;
		modified = true;
	}

	public boolean isModified() {
		return modified;
	}
	
	public boolean isBack2Removed() {
		return back2RemovedBank != -1;
	}
	
	public int getBack2Removed() {
		return back2RemovedBank;
	}
	
	public void clearBack2Removed() {
		back2RemovedBank = -1;
	}
	
	public void setModified(boolean value) {
		modified = value;
	}
	
	private static int[] seqWater={0, 100, 123, 100};
	private static int[] seqFlower={0, 111, 3};
	private static int[] seqForge={142, 194, 178};
	private static int[] seqForge2={144, 195, 179};
	private static int[] seqRotate={0, 1, 2, 1};
	private static int[] seqForward={0, 1, 2};
	
	public static int getAnimatedMotif(TileLevel level, Tile t, int compteur_animation) {
		int motif = t.index;
		int bank = t.bank;
		int comptModulo3 = (compteur_animation / 20) % 3;
		int comptModulo4 = (compteur_animation / 20) % 4;
		switch (level) {	// Some animation works only on back2 (because of mask color, indeed)
			case BACK:
			switch (bank) {
			// Animated tiles
				case 0:
				// Water
					if (motif>=108 && motif<=130 && motif !=129) {
						motif += seqWater[comptModulo4];
					} else if (motif==52 || motif==53) {
						motif += seqFlower[comptModulo3];
					}
					break;
		
		
				case 1:
					if (motif>=235 && motif<=237)
						motif+=seqForward[comptModulo3] * 3;
					break;
		
				case 2:
					switch (motif) 
					{
						case 174:    
							if (comptModulo3>=20)
								motif=175+(comptModulo3 / 20);
							break;
						case 142:	// Forge
							motif = seqForge[comptModulo3];
							break;
						case 144:
							motif = seqForge2[comptModulo3];
							break;
					    case 235:
							motif=235+seqRotate[comptModulo4];
					}
					break;
				case 3:
				// L'eau dans les grottes/palais
					if (motif==78)
						motif=78+comptModulo3;
					else if (motif == 217) {	// Lava
						motif = 217 + seqForward[comptModulo3];
					} else if( motif == 230) {
						motif = 230 + seqForward[comptModulo3];
					}
					break;
					
		
				case 5:
					// FORET3.DEC animation d'eau supplémentaire
					if (motif == 211) {
						motif+=seqRotate[comptModulo4];
					}
					break;
				case 9: // Nature palace
					// 78 , 92 , 106 (+14)
					if (motif >= 78 && motif <= 91) {
						motif += 14 * ((compteur_animation / 5) % 6);
					}
					if (motif >=162 && motif <= 171) {
						motif += 2 * ((compteur_animation / 6) % 6);
					}
					
					break;
			}
			break;
			case BACK2:
				switch (bank) {
					case 6:	// FORET4.DEC
						if (motif == 215 || motif == 218 || motif == 221 ||
							(motif >= 0 && motif <= 2) ) {
							motif += seqForward[2 - comptModulo3];
						}
						break;
				}
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
		int isSpecial = (back.reverse != Reverse.NOTHING || back.rotation != Rotation.NOTHING) ? 64 : 0;
		int isBack2 = back2 != null ? 32 : 0;
		p_buffer.put((byte) back.index);
		p_buffer.put((byte) (back.bank | isMasque | isSpecial | isBack2));
		if (isSpecial > 0) {
			int val = back.reverse.ordinal();
			val |= back.rotation.ordinal() << 2;
			p_buffer.put((byte) val);
		}
		if (back2 != null) {
			serializeOneTile(back2, p_buffer);
		}
		if (fore != null) {
			serializeOneTile(fore, p_buffer);
		}

	}
	
	/** Serialize null case : (-1,-1) **/
	public static void serializeNull(EasyBuffering p_buffer) {
		p_buffer.put((byte)-1);
		p_buffer.put((byte)-1);
	}
	
	/**
	 * Serialize just one tile (2 or 3 bytes)
	 * @param p_tile
	 * @param p_buffer
	 */
	private void serializeOneTile(Tile p_tile, EasyBuffering p_buffer) {
		int bank = p_tile.bank;
		p_buffer.put((byte) p_tile.index);
		if (p_tile.reverse != Reverse.NOTHING || p_tile.rotation != Rotation.NOTHING) {
			bank|=64;	// Need extra bit;
		}
		p_buffer.put((byte) bank);
		if ((bank & 64) != 0) {
			p_buffer.put((byte) (p_tile.reverse.ordinal() | (p_tile.rotation.ordinal() << 2)));
		}
	}
	
	/**
	 * Deserialize a byte buffer into a Case
	 * @param p_buffer
	 * @return SpriteEntity
	 */
	public static Case deserialize(EasyBuffering p_buffer) {
		int index1 = p_buffer.readUnsignedByte();
		int bank1 = p_buffer.readUnsignedByte();
		if (index1 == 255 && bank1 == 255) {
			return null;
		}
		Case mapCase=new Case();
		Tile back = new Tile(bank1&31, index1, mapCase);
		if ((bank1 & 64) != 0) {
			int value = p_buffer.readUnsignedByte();
			if ((value & 3) != 0) {
				back.reverse = Reverse.values()[value & 3];
			}
			if ((value & 15) > 3) {
				back.rotation = Rotation.values()[(value>>2) & 3];
			}// Note: Value >15 are not used (it was designed for 'transitions' before, between back and
			 //       fore ground. This is done now by floors.
		}
		if ((bank1 & 32) != 0) {
			Tile t = deserializeOneTile(mapCase, p_buffer);
			mapCase.setBackTile2(t);
		}
		if ((bank1 & 128) != 0) {
			Tile t = deserializeOneTile(mapCase, p_buffer);
			mapCase.setForeTile(t);
		}
		mapCase.setBackTile(back);


		return mapCase;
	}
	
	/**
	 * Serialize just one tile (2 or 3 bytes)
	 * @param p_case
	 * @param p_buffer
	 * @return Tile
	 */
	private static Tile deserializeOneTile(Case p_case, EasyBuffering p_buffer) {
		int index = p_buffer.readUnsignedByte();
		int bank = p_buffer.readUnsignedByte();
		Tile t = new Tile(bank & 63, index, p_case);
		if ((bank & 64) != 0) {
			int val = p_buffer.readUnsignedByte();
			t.reverse = Reverse.values()[val & 3];
			t.rotation = Rotation.values()[(val>>2) & 3];
		}
		return t;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}
	
	public Tile getBackTile2() {
		return back2;
	}

	public void setBackTile2(Tile p_tile) {
		if (back2 != null && p_tile != null) {
			p_tile.previousBank = back2.bank;
		}
		if (p_tile == null && back2 != null) {
			back2RemovedBank = back2.bank;
		}
		this.back2 = p_tile;
		modified = true;
	}

	/**
	 * Get one of the tile from back,back2 and fore, which is valued by the given integer.
	 * Returns NULL if none has this value.
	 * @param val
	 * @return Tile
	 */
	public Tile getOneValued(int... vals) {
		for (int val : vals) {
			if (back.getValue() == val) {
				return back;
			}
			if (back2 != null && back2.getValue() == val) {
				return back2;
			}
			if (fore != null && fore.getValue() == val) {
				return fore;
			}
		}
		return null;
	}
	
	public Tile getLevel(TileLevel level) {
		switch (level) {
		case BACK:
			default:
			return back;
		case BACK2:
			return back2;
		case FORE:
			return fore;
		}
	}
	
}