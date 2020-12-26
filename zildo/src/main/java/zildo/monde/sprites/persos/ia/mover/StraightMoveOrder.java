package zildo.monde.sprites.persos.ia.mover;

import zildo.monde.sprites.elements.Element;

public class StraightMoveOrder extends BasicMoveOrder {

	float distance;
	
	public StraightMoveOrder(float x, float y, float speed) {
		super(x, y, speed);
	}
	
	@Override
	void init(Mover p_wrapper) {
		super.init(p_wrapper);
		distance = target.distance(mobile.x,  mobile.y);
		
		// Neutralize speed because that could be used once this mover has finish
		((Element)mobile).vx = 0;
		((Element)mobile).vy = 0;
		if (distance < speed) {
			active = false;
		}
		// Calculate delta just once because we suppose our target is fixed
		delta.x = speed * (target.x - mobile.x) / distance;
		delta.y = speed * (target.y - mobile.y) / distance;
	}
	
	protected void calculateDelta() {

	}
}
