package zildo.monde;

import zildo.monde.util.Point;

public class Trigo {

	public static double getAngleRadian(float ax, float ay, float bx, float by) {
		double cosAngle = (bx - ax) / Point.distance(ax, ay, bx, by);
		double result = Math.acos(cosAngle);
		if (by < ay) {
			result = -result;
		}
		return result;
	}
}
