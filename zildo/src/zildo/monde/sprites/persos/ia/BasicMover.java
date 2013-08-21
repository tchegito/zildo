package zildo.monde.sprites.persos.ia;

import zildo.monde.sprites.SpriteEntity;
import zildo.monde.util.Point;

public class BasicMover extends Mover {

	public BasicMover(SpriteEntity mobile, int x, int y) {
		super(mobile, x, y);
	}
	
	Point delta = new Point(0,0);
	
	@Override
	protected Point move() {
		delta.x = (int) Math.signum( ( target.x - mobile.x));
		delta.y = (int) Math.signum( ( target.y - mobile.y));
		
		mobile.x+=delta.x;
		mobile.y+=delta.y;
	
		return delta;
	}

}
