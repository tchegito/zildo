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
import zildo.server.EngineZildo;




public class ChainingPoint implements EasySerializable {
	
//////////////////////////////////////////////////////////////////////
// Construction/Destruction
//////////////////////////////////////////////////////////////////////

	public enum MapLink {
		REGULAR, STAIRS_STRAIGHT, STAIRS_CORNER_LEFT, STAIRS_CORNER_RIGHT;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String mapname;	// max length=8
	private short  px,py;

	private boolean vertical;
	private boolean border;
	
	// Extra infos (deduced from map's context) to locate points referring to the same map.
	private int orderX;
	private int orderY;
	private boolean done;	// Means that Zildo has been detected going through this. (useful for stairs)
	
	private Zone zone;
	
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
	
	public MapLink getLinkType() {
		int infomap=EngineZildo.mapManagement.getCurrentMap().readmap(px, py);
		switch (infomap) {
		case 183+768: case 184+768:
			return MapLink.STAIRS_CORNER_LEFT;
		case 187+768: case 188+768:
			return MapLink.STAIRS_CORNER_RIGHT;
		case 1024+249: case 1024+250:
			return MapLink.STAIRS_STRAIGHT;
		default:
			return MapLink.REGULAR;
		}
	}

	public void setOrderY(int orderY) {
		this.orderY = orderY;
	}

	public ChainingPoint()
	{
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// isCollide
	///////////////////////////////////////////////////////////////////////////////////////
	// IN : ax,ay (map coordinates in range 0..63,0..63)
	///////////////////////////////////////////////////////////////////////////////////////
	public boolean isCollide(int ax, int ay, boolean border) {
		if (!border) {
			if (vertical) {
				if (ay>=py && ax==px && ay<=(py+1)) {
					return true;
				}
			} else {
				if (ax>=px && ay==py && ax<=(px+1)) {
					return true;
				}
			}
			/*
	   if (tab_pe[i].py and 128)<>0 then begin
	    if tab_pe[i].px=oux then
	     temp:=tab_pe[i].mapname
	    else if (tab_pe[i].py and 127)=ouy then
	     temp:=tab_pe[i].mapname;
	   end;
			*/
		} else {
			// Map's border
			if ( py==ay || px==ax) {
				return true;
			}
		}
		// No collide
		return false;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// getAngle
	///////////////////////////////////////////////////////////////////////////////////////
	// IN : x,y (pixel coordinates in range 0..63*16,0..63*16
	///////////////////////////////////////////////////////////////////////////////////////
	public Angle getAngle(int x, int y, Angle startAngle) {
		Angle angle=startAngle;
		if (border && (px == 0 || px==EngineZildo.mapManagement.getCurrentMap().getDim_x()-1)) {
			// Vertical border
			if (x % 16 > 8) {
				angle=Angle.EST;
			} else {
				angle=Angle.OUEST;
			}
		} else if (border) {
			// Horizontal one
			if ((y % 16) > 8) {
				angle=Angle.SUD;
			} else {
				angle=Angle.NORD;
			}
		}
	
		// Return the 'computed' angle
		return angle;
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
	
	@Override
	public String toString() {
		return mapname;
	}
	
	/**
	 * Get the range, in pixel coordinates, taken for the point.
	 * @return Zone
	 */
	public Zone getZone(Area p_map) {
		if (zone == null) {
			Point p1=new Point(px, py);
			Point p2=new Point(2, 1);
			if (isBorder()) {
				if (p1.x == 0 || p1.x == p_map.getDim_x()-1) {
					p1.y=0;
					p2.y=p_map.getDim_y();
					p2.x=1;
				} else {
					p1.x=0;
					p2.x=p_map.getDim_x();
				}
			} else if (isVertical()) {
				p2.x=1;
				p2.y=2;
			}
			zone = new Zone(16*p1.x, 16*p1.y, 16*p2.x, 16*p2.y);
		}
		return zone;
	}
	
	/**
	 * Deserialize a chaining point from a given buffer.
	 * @param p_buffer
	 * @return ChainingPoint
	 */
	public static ChainingPoint deserialize(EasyBuffering p_buffer) {
		ChainingPoint pe = new ChainingPoint();
		pe.px = p_buffer.readUnsignedByte();
		pe.py = p_buffer.readUnsignedByte();
		String mapName = p_buffer.readString();
		pe.mapname = mapName;
		
		// Set the linked properties
		if (pe.px > 127) {
			pe.vertical = true;
			pe.px&=127;
		}
		if (pe.py > 127) {
			pe.border = true;
			pe.py&=127;
		}
		return pe;
	}
	
	/**
	 *  Serialize this chaining point.
	 */
	public void serialize(EasyBuffering p_buffer) {
		int saveX = px;
		int saveY = py;
		if (vertical) {
			saveX|=128;
		}
		if (border) {
			saveY|=128;
		}
		p_buffer.put((byte) saveX);
		p_buffer.put((byte) saveY);
		p_buffer.put(mapname);
	}
	
	/**
	 * Useful for test cases.
	 */
	public boolean equals(Object o) {
		if (!(o instanceof ChainingPoint)) {
			return false;
		}
		ChainingPoint other=(ChainingPoint) o;
		return (px == other.px && py == other.py && mapname.equals(other.mapname) && orderX == other.orderX && orderY == other.orderY);
	}

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}
}