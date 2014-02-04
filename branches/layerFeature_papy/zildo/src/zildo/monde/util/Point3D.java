/**
 * 
 */
package zildo.monde.util;

/**
 * @author fcastagn
 * 
 */
public class Point3D extends Point {

	private int l;

	public Point3D() {
		super();
	}

	public Point3D(int x, int y, int l) {
		super(x, y);
		this.l = l;
	}

	public Point3D(float x, float y, int l) {
		super(x, y);
		this.l = l;
	}

	// Copy constructor
	public Point3D(Point3D p) {
		this(p.getX(), p.getY(), p.getL());
	}

	public int getL() {
		return l;
	}

	public void setL(int l) {
		this.l = l;
	}

	@Override
	public String toString() {
		return "(" + getX() + ", " + getY() + ", " + l + ")";
	}

	/**
	 * Returns TRUE if given point have same coordinates as current one.
	 */
	@Override
	public boolean equals(Object p_other) {
		if (!p_other.getClass().equals(Point3D.class)) {
			return false;
		}
		Point3D p = (Point3D) p_other;
		return super.equals(p_other) && p.l == l;
	}

	@Override
	public int hashCode() {
		int hash = super.hashCode();
		hash = hash * 31 + l;
		return hash;
	}

	public static Point3D fromString(String p_text) {
		String[] coords = p_text.split(",");
		return new Point3D(Integer.valueOf(coords[0]), Integer.valueOf(coords[1]), Integer.valueOf(coords[2]));
	}

	public Point3D multiply(float factor) {
		return new Point3D(x * factor, y * factor, l);
	}

}
