package zildo.monde;


public enum Angle {

	NORD(0),
	EST(1),
	SUD(2),
	OUEST(3);
	
	public int value;
	
	private Angle(int value) {
		this.value=value;
	}
	
	public boolean isVertical() {
		return this==NORD || this==SUD;
	}
	
	public boolean isHorizontal() {
		return this==EST || this==OUEST;
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
	
}
