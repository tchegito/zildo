package zildo.monde;

import zildo.monde.util.Point;
import zildo.monde.util.Vector2f;

public class Trigo {

	public static final double PI_SUR_4 = Math.PI / 4d;
	public static final double SQUARE_2 = Math.sqrt(2);
	public static final double cosPISur4 = Math.cos(Math.PI / 4);	// Remember this result
	
	public static double getAngleRadian(float ax, float ay) {
		return getAngleRadian(0, 0, ax, ay);
	}
	
	public static double getAngleRadian(float ax, float ay, float bx, float by) {
		double distance = Point.distance(ax, ay, bx, by);
		if (distance == 0) {
			return 0;
		}
		double cosAngle = (bx - ax) / distance;
		double result = Math.acos(cosAngle);
		if (by < ay) {
			result = -result;
		}
		return result;
	}
	
	public static Vector2f vect(double angle, double speed) {
		return new Vector2f(
				speed * Math.cos(angle),
			    speed * Math.sin(angle) );
	}
}
