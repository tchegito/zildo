package zildo.monde;

import zildo.monde.util.Point;
import zildo.monde.util.Vector2f;

public class Trigo {

	public static final double PI_SUR_2 = Math.PI / 2d;
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
		return getAngleRadianWithDistance(ax, ay, bx, by, distance);
	}
	
	/** Small optimization for case where we already have calculated distance **/
	public static double getAngleRadianWithDistance(float ax, float ay, float bx, float by, double distance) {
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
	
	public static Function easeInOut(int distance, float speed) {
		float fixedSpeed = speed * EASE_SPEED_FACTOR;
		final int totalTime = (int) (distance / fixedSpeed);
		Function func = new Function() {
			
			double a = Math.PI / totalTime;
			double b = Math.PI * 0.5f;
			@Override
			public float apply(float x) {
				return (float) (Math.sin(a * x - b) + 1) /2;
			}
		}; 
		return func;
	}
	
	public static Function easePolynomial(int distance, float speed) {
		final int totalTime = (int) (distance / speed);
		return new Function() {
			public float apply(float x) {
				float ts = (x/=totalTime) * x;
				float tc = (ts * x);
				return -1.5975f*tc*ts + 3.9975f*ts*ts + -4.8f*tc + 3.2f*ts + 0.2f*x;
			}
		};
	}
	
	// With 0.66f as a value, we ensure to have a maximal speed as the one provided
	// But global time to go from point A to point B is way too long. So for now, w
	public static final float EASE_SPEED_FACTOR = 1; //0.66f;
	
	public static Function easeInOutDerivee(int distance, float speed) {
		float fixedSpeed = speed * EASE_SPEED_FACTOR;
		final int totalTime = (int) (distance / fixedSpeed);
		Function func = new Function() {
			
			double a = Math.PI / totalTime;
			double b = Math.PI * 0.5f;
			@Override
			public float apply(float x) {
				return (float) ((a/2) * Math.cos(a * x - b ));
			}
		}; 
		return func;
	}

}
