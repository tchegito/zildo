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

package zildo.monde.map;

import zildo.server.EngineZildo;




public class ChainingPoint {
	
//////////////////////////////////////////////////////////////////////
// Construction/Destruction
//////////////////////////////////////////////////////////////////////

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String mapname;	// max length=8
	private short  px,py;

	// Extra infos (deduced from map's context) to locate points referring to the same map.
	private int orderX;
	private int orderY;

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
	}

	public short getPy() {
		return py;
	}

	public void setPy(short py) {
		this.py = py;
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

	public void setOrderY(int orderY) {
		this.orderY = orderY;
	}

	public ChainingPoint()
	{
	}
	
	// Assignment operator
	public ChainingPoint(ChainingPoint original) {
		this.mapname=original.mapname;
		this.px=original.px;
		this.py=original.py;
		this.orderX=0;
		this.orderY=0;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// isCollide
	///////////////////////////////////////////////////////////////////////////////////////
	// IN : ax,ay (map coordinates in range 0..63,0..63)
	///////////////////////////////////////////////////////////////////////////////////////
	public boolean isCollide(int ax, int ay, boolean border) {
		if (py<128) {
			if ((px & 128)!=0) {
				if (ay>=py && ax==(px & 127) && ay<=(py+1)) {
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
		} else if (border) {
			// Map's border
			if ( (py & 127)==ay || (px & 127)==ax) {
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
		if ((py & 128)!=0 && (px==0 || px==EngineZildo.mapManagement.getCurrentMap().getDim_x()-1)) {
			// Vertical border
			if (x % 16 > 8) {
				angle=Angle.EST;
			} else {
				angle=Angle.OUEST;
			}
		} else if ((py & 128)!=0) {
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
		return py > 127;
	}
		
	public void setVertical(boolean verti) {
		px=(short) (px & 127);
		if (verti) {
			px=(short) (px+128);
		}
	}
	
	public boolean isVertical() {
		return px > 127;
	}

	public void setBorder(boolean border) {
		py=(short) (py & 127);
		if (border) {
			py=(short) (py+128);
		}
	}
	
	@Override
	public String toString() {
		return mapname;
	}
	
	/**
	 * Get the range in pixel coordiantes taken for the point.
	 * @return
	 */
	public Zone getZone() {
		Area map=EngineZildo.mapManagement.getCurrentMap();
		Point p1=new Point(px & 63, py & 63);
		Point p2=new Point(32, 16);
		if (isBorder()) {
			if (p1.x == 0 || p1.x == map.getDim_x()-1) {
				p1.y=0;
				p2.y=map.getDim_y()*16;
				p2.x=16;
			} else {
				p1.x=0;
				p2.x=map.getDim_x()*16;
			}
		} else if (isVertical()) {
			p2.x=16;
			p2.y=32;
		}
		return new Zone(p1.x, p1.y, p2.x, p2.y);		
	}
}