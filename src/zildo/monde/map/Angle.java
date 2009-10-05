package zildo.monde.map;


public enum Angle {

	NORD(0, new Point(0,-1)),
	EST(1, new Point(1,0)),
	SUD(2, new Point(0,1)),
	OUEST(3, new Point(-1,0)),
	NORDEST(4, new Point(1,1)),
	SUDEST(5, new Point(1,1)),
	SUDOUEST(6, new Point(-1,1)),
	NORDOUEST(7, new Point(-1,-1));
	
	public int value;
	public Point coords;
	
	private Angle(int value, Point coords) {
		this.value=value;
		this.coords=coords;
	}
	
	public boolean isVertical() {
		return this==NORD || this==SUD;
	}
	
	public boolean isHorizontal() {
		return this==EST || this==OUEST;
	}

    public boolean isDiagonal() {
        return value > 3;
    }
    
	public static Angle rotate(Angle a, int quart) {
		return fromInt((a.value + quart) % 4);
	}
	static public Angle fromInt(int val) {
		for (Angle a : Angle.values()) {
			if (a.value == val) {
				return a;
			}
		}
		throw new RuntimeException(val+" n'est pas un angle reconnu.");
	}
	
	/**
	 * Returns a 0..8 ranged int based on this order (NORD, NORDEST, EST, SUDEST, SUD, SUDOUEST, OUEST, NORDOUEST)
	 * @return int
	 */
	private int getUsableValue() {
		if (isDiagonal()) {
			return (value - 4) *2 + 1;
		} else {
			return value*2;
		}
	}
	
	/**
	 * Returns TRUE if the given angle is this one's opposite, with/without tolerance of 1 scale.
	 * @param p_other
	 * @param p_tolerance
	 * @return boolean
	 */
	public boolean isOpposite(Angle p_other, boolean p_tolerance) {
		if (p_other == null) {
			return false;
		}
		int val1=getUsableValue();
		int val2=p_other.getUsableValue();
		int result=Math.abs(val2 - val1-4);
		if (p_tolerance) {
			return Math.abs(result - 1) == 0;
		} else {
			return result==0;
		}
	}
}
