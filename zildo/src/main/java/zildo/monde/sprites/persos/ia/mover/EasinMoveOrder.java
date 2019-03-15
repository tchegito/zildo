package zildo.monde.sprites.persos.ia.mover;

import zildo.monde.Function;
import zildo.monde.Trigo;
import zildo.monde.util.Point;
import zildo.monde.util.Pointf;

/** Move including ease-in and ease-out. 
 * 
 * With a given speed, the mover will reach 1.3 * speed at higher value. 
 * But movement is guaranteed to be nearly as fast as linear one (see TrigoTest#compareSpeed to be sure). **/
public class EasinMoveOrder extends BasicMoveOrder {

	int timeCounter;
	
	float totalDistance;

	Function easeFunc;
	
	public EasinMoveOrder(float x, float y, float speed) {
		super(x, y, speed);
		timeCounter = 0;
	}
	
	@Override
	void init(Mover p_wrapper) {
		super.init(p_wrapper);
		totalDistance = Point.distance(mobile.x, mobile.y, target.x, target.y);
		easeFunc = Trigo.easeInOutDerivee((int) totalDistance, speed);
	}

	@Override
	protected Pointf move() {
		// Calculate speed along the timeCounter
		speed = easeFunc.apply(timeCounter) * totalDistance;
		timeCounter += 1;
		return super.move();
	}

}