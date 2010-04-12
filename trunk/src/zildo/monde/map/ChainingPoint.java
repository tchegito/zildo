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
	
	public String toString() {
		StringBuffer sb=new StringBuffer();
		sb.append("Map="+mapname+"\npx="+px+"\npy="+py);
		return sb.toString();
	}
}