package zildo.monde.sprites.persos.ia;

import zildo.monde.sprites.elements.Element;
import zildo.monde.util.Point;

public abstract class Mover {

	Element mobile;
	Point target;
	
	public Mover(Element mobile, int x, int y) {
		this.mobile = mobile;
		this.target = new Point(x, y);
	}
	
	protected abstract void move();
	
	public boolean reachTarget() {
		move();
		if (mobile.x == target.x && mobile.y == target.y) {
			return true;
		}
		return false;
	}
}
