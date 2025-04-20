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
import zildo.fwk.gfx.filter.FilterEffect;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
import zildo.monde.util.Zone;
import zildo.server.EngineZildo;

public class ChainingPoint implements EasySerializable {

	// ////////////////////////////////////////////////////////////////////
	// Construction/Destruction
	// ////////////////////////////////////////////////////////////////////

	/**
	 * Transition between map: 1 script for getting in , and 1 for getting out.
	 */
	public enum MapLink {
		REGULAR(null, null), 
		STAIRS_STRAIGHT("stairsUp", "stairsUpEnd"),
		STAIRS_ROOF_STRAIGHT("stairsStraightUp", "stairsStraightUpEnd"),
		STAIRS_CORNER_LEFT("stairsUpCornerLeft", "stairsUpCornerLeftEnd"), 
		STAIRS_CORNER_RIGHT("stairsUpCornerRight", "stairsUpCornerRightEnd"),
		STAIRS_CORNER_DOWN_LEFT("stairsDownCornerLeft", "stairsDownCornerLeftEnd"), 
		STAIRS_CORNER_DOWN_RIGHT("stairsDownCornerRight", "stairsUpCornerRightEnd"),
		// Stairs at the lower of the tile
		STAIRS_SHORT_CORNER_LEFT("stairsShortUpCornerLeft", "stairsShortUpCornerLeftEnd"), 
		STAIRS_SHORT_CORNER_RIGHT("stairsShortUpCornerRight", "stairsShortUpCornerRightEnd"),

		PIT("fallPit", ""),
		WOODSTAIRS_CORNER_LEFT("woodStairsUpCornerLeft", "woodStairsDownEnd"),
		WOODSTAIRS_END("woodStairsDown", "woodStairsUpEnd"),
		// Stairs in nature palace
		STAIRS_SINGLE_CORNER_LEFT("shortStairsDownLeft", "shortStairsDownRightEnd"),
		STAIRS_SINGLE_CORNER_RIGHT("shortStairsUpRight", "shortStairsUpRightEnd");
		
		public final String scriptIn, scriptOut;
		
		private MapLink(String p_scriptIn, String p_scriptOut) {
			scriptIn = p_scriptIn;
			scriptOut = p_scriptOut;
		}
	}
	
	private String mapname; // max length=8
	private short px, py;
	private short floor;

	private boolean vertical;
	private boolean border;
	private boolean single; // TRUE=chaining point is on only one tile
	
	// Extra infos (deduced from map's context) to locate points referring to
	// the same map.
	private int orderX;
	private int orderY;
	private boolean done; // Means that Zildo has been detected going through
							// this. (useful for stairs)

	private Zone zone;
	private Angle comingAngle;	// Angle which character is pointing when spawning from this point
	private FilterEffect transitionAnim;
	
	public FilterEffect getTransitionAnim() {
		return transitionAnim;
	}

	public void setTransitionAnim(FilterEffect transitionAnim) {
		this.transitionAnim = transitionAnim;
	}

	public Angle getComingAngle() {
		return comingAngle;
	}

	public void setComingAngle(Angle comingAngle) {
		this.comingAngle = comingAngle;
	}

	public String getMapname() {
		return mapname;
	}

	public void setMapname(String mapname) {
		this.mapname = mapname;
	}

	public short getPx() {
		return px;
	}

	public void setPx(short px) {
		this.px = px;
		zone = null;
	}

	public short getPy() {
		return py;
	}

	public void setPy(short py) {
		this.py = py;
		zone = null;
	}

	public int getOrderX() {
		return orderX;
	}

	public void setOrderX(int orderX) {
		this.orderX = orderX;
	}

	public int getOrderY() {
		return orderY;
	}

	/** Get transition kind based on graphic tile **/
	public MapLink getLinkType() {
		int infomap = EngineZildo.mapManagement.getCurrentMap().readmap(px/2, py/2 + (py % 2));
		switch (infomap) {
		case 183 + 768:
		case 184 + 768:
			return MapLink.STAIRS_CORNER_LEFT;
		case 187 + 768:
		case 188 + 768:
			return MapLink.STAIRS_CORNER_RIGHT;
		case 1024 + 249:
		case 1024 + 250:
			return MapLink.STAIRS_STRAIGHT;
		case 7*256 + 159:	// up
		case 7*256 + 155: case 7*256 + 156: // down
			return MapLink.STAIRS_ROOF_STRAIGHT;
		case 768 + 185:
		case 768 + 186:
			return MapLink.STAIRS_CORNER_DOWN_LEFT;
		case 1536 + 198:	// Hole in grass
		case 256*3 + 237:	// Border above abyss in a cave
		case 256*3 + 239:	// Same
			return MapLink.PIT;
		case 512 + 198:
			return MapLink.WOODSTAIRS_CORNER_LEFT;
		case 512 + 201:
			return MapLink.WOODSTAIRS_END;
		// Lavacave stairs
		case 10*256 + 125:
		case 10*256 + 126:
			return MapLink.STAIRS_SHORT_CORNER_LEFT;
		case 10*256 + 129:
		case 10*256 + 130:
			return MapLink.STAIRS_SHORT_CORNER_RIGHT;
		case 256*9 + 192:
			return MapLink.STAIRS_SINGLE_CORNER_LEFT;
		case 256*9 + 191:
			return MapLink.STAIRS_SINGLE_CORNER_RIGHT;
		default:
			return MapLink.REGULAR;
		}
	}

	public void setOrderY(int orderY) {
		this.orderY = orderY;
	}

	public ChainingPoint() {
		done = false;
	}

	/**
	 * Returns TRUE if given coordinates collide with current chaining point.
	 * NOTE: Chaining points are 8x8 scaled, instead of 16x16 for tiles.
	 * @param ax,ay : doubled map coordinates in range 0..127,0..127
	 */
	public boolean isCollide(int ax, int ay, boolean p_border) {
		if (single) {
			return (ax == px || ax == px+1) && (ay == py || ay == py+1);
		}
		if (!border) {
			if (vertical) {
				if (ay >= py && (ax == px || ax == px+1) && ay <= (py + 3)) {
					return true;
				}
			} else {
				if (ax >= px && (ay == py || ay == py+1) && ax <= (px + 3)) {
					return true;
				}
			}
		} else if (p_border) {
			// Map's border
			if ( vertical && (ax == px || ax == px+1)) {
				return true;
			} else if (!vertical && (ay == py || ay == py+1) ) {
				return true;
			}
		}
		// No collide
		return false;
	}

	public boolean isBorder() {
		return border;
	}

	public void setVertical(boolean p_verti) {
		vertical = p_verti;
		zone = null;
	}

	public boolean isVertical() {
		return vertical;
	}

	public void setBorder(boolean p_border) {
		border = p_border;
		zone = null;
	}

	public boolean isSingle() {
		return single;
	}

	public void setSingle(boolean single) {
		this.single = single;
		zone = null;
	}

	@Override
	public String toString() {
		return mapname;
	}

	/**
	 * Get the range, in pixel coordinates, taken for the point.
	 * 
	 * @return Zone
	 */
	public Zone getZone(Area p_map) {
		if (zone == null) {
			Point p1 = new Point(px, py);
			Point p2 = new Point(4, 2);
			if (isBorder()) {
				if (p1.x == 0 || p1.x == 2*p_map.getDim_x() - 2) {
					p1.y = 0;
					p2.y = p_map.getDim_y() * 2;
					p2.x = 2;
				} else {
					p1.x = 0;
					p2.x = p_map.getDim_x() * 2;
				}
			} else if (isVertical()) {
				p2.x = 2;
				p2.y = 4;
			} else if (isSingle()) {
				p2.x = 2;
				p2.y = 2;
			}
			zone = new Zone(8 * p1.x, 8 * p1.y, 8 * p2.x, 8 * p2.y);
		}
		return zone;
	}

	/**
	 * Deserialize a chaining point from a given buffer.
	 * 
	 * @param p_buffer
	 * @return ChainingPoint
	 */
	public static ChainingPoint deserialize(EasyBuffering p_buffer) {
		ChainingPoint pe = new ChainingPoint();
		pe.px = p_buffer.readUnsignedByte();
		pe.py = p_buffer.readUnsignedByte();
		pe.floor = p_buffer.readUnsignedByte();
		pe.comingAngle = Angle.fromInt(p_buffer.readUnsignedByte());
		int transitionAnimPlusFlag = p_buffer.readUnsignedByte();
		pe.transitionAnim = FilterEffect.values()[transitionAnimPlusFlag & 31];
		String mapName = p_buffer.readString();
		pe.mapname = mapName;

		// Set the linked properties
		if ((transitionAnimPlusFlag & 32) != 0) {
			pe.single = true;
		}
		if ((transitionAnimPlusFlag & 64) != 0) {
			pe.vertical = true;
		}
		if ((transitionAnimPlusFlag & 128) != 0) {
			pe.border = true;
		}
		return pe;
	}

	/**
	 * Serialize this chaining point.
	 */
	@Override
	public void serialize(EasyBuffering p_buffer) {
		int saveX = px;
		int saveY = py;
		int transAnim = transitionAnim.ordinal();
		if (single) {
			transAnim |= 32;
		}
		if (vertical) {
			transAnim |= 64;
		}
		if (border) {
			transAnim |= 128;
		}
		p_buffer.put((byte) saveX);
		p_buffer.put((byte) saveY);
		p_buffer.put((byte) floor);
		p_buffer.put((byte) comingAngle.value);
		p_buffer.put((byte) transAnim);
		p_buffer.put(mapname);
	}

	/**
	 * Useful for test cases.
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ChainingPoint)) {
			return false;
		}
		ChainingPoint other = (ChainingPoint) o;
		return (px == other.px && py == other.py && mapname.equals(other.mapname) && orderX == other.orderX && orderY == other.orderY);
	}

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}

	public short getFloor() {
		return floor;
	}

	public void setFloor(short floor) {
		this.floor = floor;
	}
	
	
}