package zildo.monde.map;




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
		Angle angle=Angle.NORD;
		if ((px & 128)!=0 && startAngle.isHorizontal()) {
			if (x % 16 > 8) {
				angle=Angle.OUEST;
			} else {
				angle=Angle.EST;
			}
		} else if ((px & 128)==0 && startAngle.isVertical()) {
			if ((y % 16) > 8) {
				angle=Angle.NORD;
			} else {
				angle=Angle.SUD;
			}
		}
	
		// Return the 'computed' angle
		return angle;
	}
	
	public String toString() {
		StringBuffer sb=new StringBuffer();
		sb.append("Map="+mapname+"\npx="+px+"\npy="+py);
		return sb.toString();
	}
}