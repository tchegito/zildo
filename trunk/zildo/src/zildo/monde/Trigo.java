package zildo.monde;

import zildo.monde.util.Point;
import zildo.monde.util.Vector2f;

public class Trigo {

	public static final double PI_SUR_4 = Math.PI / 4d;
	
	public static double getAngleRadian(float ax, float ay) {
		return getAngleRadian(1, 0, ax, ay);
	}
	
	public static double getAngleRadian(float ax, float ay, float bx, float by) {
		double cosAngle = (bx - ax) / Point.distance(ax, ay, bx, by);
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
