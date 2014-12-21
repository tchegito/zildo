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

package zildo.monde.map;

import java.util.HashMap;
import java.util.Map;

import zildo.monde.util.Angle;

/**
 * Class used by the collision engine.<br/>
 * 
 * It provides informations about blockable characteristics of a tile.
 * 
 * @author tchegito
 */
public class TileInfo {

	public enum Template {
		WALKABLE, // Entire tile is walkable
		FULL, // Entire tile is blocking
		CORNER, // One quarter of the tile is walkable.
		CORNER_DIAGONAL, // One quarter + one half quarter (diagonal) is walkable
		HALF, // One half of the tile is walkable.
		HALF_CORNER, // One half-quarter is walkable (diagonally)
		QUARTER,	// 4 pixel-sized bar
	}

	public Template template;

	public boolean inverse; // Inverse walkable and blocking part (in conjunction with 'corner' and 'corner diagonal')
	public Angle blockAngle; // Indicates the collider angle. Can be diagonal

	private static final Map<Integer, TileInfo> tileInfoMap = new HashMap<Integer, TileInfo>();

	private int hash = -1; // Keep it for optimizing

	public TileInfo() {
		template = Template.WALKABLE;
		inverse = false;
		blockAngle = null;
	}

	/**
	 * Return TRUE/FALSE depending on the given position (p_posX, p_posY) in (0..15,0..15)
	 * 
	 * @param p_posX
	 * @param p_posY
	 * @return boolean
	 */
	public boolean collide(int p_posX, int p_posY) {

		boolean result;

		switch (template) {
		case WALKABLE:
			return false;

		case HALF:
			switch (blockAngle) {
			case NORD:
				return (p_posY < 8);
			case NORDEST:
				return (p_posY < p_posX);
			case EST:
				return (p_posX > 7);
			case SUDEST:
				return (p_posY > (15 - p_posX));
			case SUD:
				return (p_posY > 7);
			case SUDOUEST:
				return (p_posY > p_posX);
			case OUEST:
				return (p_posX < 8);
			case NORDOUEST:
				return (p_posY < (16 - p_posX));
			}

		case CORNER: // Only diagonal angles
			result = collideCorner(blockAngle, p_posX, p_posY);
			if (inverse) {
				result = !result;
			}
			return result;

		case CORNER_DIAGONAL: // Only diagonal angles
			result = collideCornerDiagonal(blockAngle, p_posX, p_posY);
			if (inverse) {
				result = !result;
			}
			return result;

		case HALF_CORNER:
			result = collideHalfCorner(blockAngle, p_posX, p_posY);
			if (inverse) {
				result = !result;
			}
			return result;

		case QUARTER:
			return collideQuarter(blockAngle, p_posX, p_posY);
			
		case FULL:
		default:
			return true;
		}
	}

	private boolean collideCorner(Angle p_angle, int p_posX, int p_posY) {
		switch (p_angle) {
		case NORDEST:
			return (p_posX > 7 && p_posY < 8);
		case SUDEST:
			return (p_posX > 7 && p_posY > 7);
		case SUDOUEST:
			return (p_posX < 8 && p_posY > 7);
		case NORDOUEST:
			return (p_posX < 8 && p_posY < 8);
		default:
			throw new RuntimeException("No way to determine the tile collision !");
		}
	}

  /**
   * XXXX CornerDiagonal with angle=Angle.NORDEST
   * 0XXX
   * 0000
   * 0000
	 * @param p_angle
	 * @param p_posX
	 * @param p_posY
	 * @return
	 */
	private boolean collideCornerDiagonal(Angle p_angle, int p_posX, int p_posY) {
		switch (p_angle) {
		case NORDEST:
			return (p_posX < 8 && p_posY < p_posX) || (p_posX > 7 && p_posY < 8); // Never used for now
		case SUDEST:
			return (p_posX < 8 && p_posY >= 16 - p_posX) || (p_posX > 7 && p_posY > 7);
		case SUDOUEST:
			return (p_posX > 7 && p_posY > p_posX) || (p_posX < 8 && p_posY > 7);
		case NORDOUEST:
			return (p_posX > 7 && p_posY < 16 - p_posX) || (p_posX < 8 && p_posY < 8);
		default:
			throw new RuntimeException("No way to determine the tile collision !");
		}
	}

	/**
	 * HalfCorner collision (only diagonal angles)
	 * 
	 * @param p_angle
	 * @param p_posX
	 * @param p_posY
	 * @return boolean
	 */
	private boolean collideHalfCorner(Angle p_angle, int p_posX, int p_posY) {
		switch (p_angle) {
		case NORDEST:
			return p_posY <= (p_posX - 8); 
		case SUDEST:
			return p_posY > (24 - p_posX);
		case SUDOUEST:
			return p_posY > (p_posX + 8);
		case NORDOUEST:
			return p_posY < (8 - p_posX);
		default:
			throw new RuntimeException("No way to determine the tile collision !");
		}
	}

	private boolean collideQuarter(Angle p_angle, int p_posX, int p_posY) {
		switch (p_angle) {
		case NORD:
			return p_posY < 4;
		case SUD:
			return p_posY > 11;
		case EST:
			return p_posX > 11;
		case OUEST:
			return p_posX < 4;
		default:
			throw new RuntimeException("No way to determine the tile collision !");
		}
	}
	
	/**
	 * Returns TRUE if given TileInfo is equals to the current one.
	 */
	@Override
	public boolean equals(Object p_object) {
		if (p_object == null || p_object.getClass() != TileInfo.class) {
			return false;
		}
		TileInfo t2 = (TileInfo) p_object;
		return hashCode() == t2.hashCode();
	}

	@Override
	public int hashCode() {
		if (hash == -1) {
			// Just for pleasure ! We could just returned 1, in order to discard the default hashCode behavior.
			hash = template.ordinal(); // 3 bits
			hash += inverse ? 8 : 0; // 1 bit
			hash += blockAngle == null ? 0 : (blockAngle.value << 4); // 3 bits
		}

		return hash;
	}

	/** Returns the TileInfo corresponding to an int value. (=reverse hashCode) */
	public static TileInfo fromInt(int p_value) {
		TileInfo t = tileInfoMap.get(p_value);
		if (t == null) {
			try {
				// Not in the map yet, so we create it and put it into.
				// According to valid value
				if (p_value > ((7 << 4) + 15)) {
					return null;
				}
				if ((p_value & 7) > 6 && (p_value & 7) <= 7) {
					return null;
				}
				if ((p_value & 7) == 0 && p_value > 0) {
					return null;
				}
				if ((p_value & 7) == 1 && p_value > 1) {
					return null;
				}
				t = new TileInfo();
				t.template = Template.values()[p_value & 7];
				t.inverse = (p_value & 8) != 0 ? true : false;
				t.blockAngle = Angle.fromInt(p_value >> 4);
				if (t.template == Template.QUARTER && (t.inverse || t.blockAngle.value > 3)) {
					return null;
				}
				if ((t.template == Template.CORNER || t.template == Template.CORNER_DIAGONAL) && !t.blockAngle.isDiagonal()) {
					return null;
				}
				if (t.template == Template.HALF && t.inverse) {
					return null;
				}
			} catch (Exception e) {
				t = TileInfo.fromInt(0);
			}
			tileInfoMap.put(p_value, t);
		}
		return t;
	}
}