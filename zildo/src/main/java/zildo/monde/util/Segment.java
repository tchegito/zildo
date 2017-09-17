package zildo.monde.util;

/** Represents a [ab] segment **/
public class Segment {

	Pointf a;
	Pointf b;
	
	public Segment(Pointf a, Pointf b) {
		this.a = a;
		this.b = b;
	}
	
	/** Returns an intersection point if 2 segments crossed (NULL otherwise).
	 * Inspired from there:
	 * https://stackoverflow.com/questions/563198/how-do-you-detect-where-two-line-segments-intersect **/
	public Pointf cross(Segment other) {
		
		float p0_x = a.x;
		float p0_y = a.y;
		float p1_x = b.x;
		float p1_y = b.y;
		
		float p2_x = other.a.x;
		float p2_y = other.a.y;
		float p3_x = other.b.x;
		float p3_y = other.b.y;
		
	    float s1_x, s1_y, s2_x, s2_y;
	    s1_x = p1_x - p0_x;     s1_y = p1_y - p0_y;
	    s2_x = p3_x - p2_x;     s2_y = p3_y - p2_y;

	    float s, t;
	    s = (-s1_y * (p0_x - p2_x) + s1_x * (p0_y - p2_y)) / (-s2_x * s1_y + s1_x * s2_y);
	    t = ( s2_x * (p0_y - p2_y) - s2_y * (p0_x - p2_x)) / (-s2_x * s1_y + s1_x * s2_y);

	    if (s >= 0 && s <= 1 && t >= 0 && t <= 1) {
	        // Collision detected
	    	return new Pointf(p0_x + (t * s1_x),
	    					  p0_y + (t * s1_y));
	    }

	    return null; // No collision
	}
}
