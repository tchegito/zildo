package zeditor.tools.sprites;

import zildo.monde.util.Zone;

public class ZoneO extends Zone {

	int offXLeft, offXRight;
	int offY;
	
	public ZoneO(int x1, int y1, int x2, int y2, int offXLeft, int offXRight, int offY) {
		super(x1, y1, x2, y2);
		this.offXLeft = offXLeft;
		this.offXRight = offXRight;
		this.offY = offY;
	}
	
	Zone getOffsetZone() {
		return new Zone(offXLeft, offY, offXRight, 0); 
	}
}
