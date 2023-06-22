package zildo.monde.map;

import zildo.monde.sprites.Rotation;

public class TileLight {

	public int north(int level, float x, float y) {
		if (level == 0) {
			return (int)y % 16;
		} else if (level == 1) {
			return 8 + Math.min((int) y % 16, 7);
		} else {
			return ((int) y % 16) / 2;
		}
	}
	
	public int south(int level, float x, float y) {
		if (level == 1) {
			return 15 - Math.max((int)y % 16 - 8, 0);
		} else {
			return 8 - ((int) y % 16) / 2;
		}
	}
	
	 public int left(int level, float x, float y) {
		 switch (level) {
		 case 0:
			 return (int)x % 16;
		 case 1:
			 return 8 + Math.min((int) x % 16, 7);
		 default:
			 return (int) (x % 16) / 2;
		 }
	 }
	 
	 public int right(int level, float x, float y) {
		 switch (level) {
			 case 0:
				 return 15 - (int)x % 16;
			 case 1:
				 return Math.min(8 + 16 - (int) x % 16, 15);
			 default:
				 return 8 - ((int) x % 16) / 2;
		 }
	 }
	 
	 public int forRotatedTile(int level, float x, float y, Rotation rot) {
		switch (rot) {
		case NOTHING:
			default:
			return north(level, x, y);
		case CLOCKWISE:
			return right(level, x, y);
		case UPSIDEDOWN:
			return south(level, x, y);
		case COUNTERCLOCKWISE:
			return left(level, x, y);
		}
	 }

}
