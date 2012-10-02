package zildo.monde.sprites.persos.ia;

import zildo.monde.sprites.elements.Element;

public class BasicMover extends Mover {

	public BasicMover(Element mobile, int x, int y) {
		super(mobile, x, y);
	}
	
	@Override
	protected void move() {
		int px = (int) Math.signum( ((int) target.x - mobile.x));
		int py = (int) Math.signum( ((int) target.y - mobile.y));
		
		mobile.x+=px;
		mobile.y+=py;
	}

}
