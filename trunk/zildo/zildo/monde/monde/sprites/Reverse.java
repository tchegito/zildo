package zildo.monde.sprites;

public enum Reverse {

	NOTHING(0), HORIZONTAL(128), VERTICAL(64), ALL(192);
	
	final int value;
	
	private Reverse(int p_value) {
		value = p_value;
	}
	
	public int getValue() {
		return value;
	}
	
	public Reverse succ() {
		return values()[(ordinal()+1) % 4];
	}
	
	public boolean isHorizontal() {
		return this == HORIZONTAL || this == ALL;
	}
	
	public boolean isVertical() {
		return this == VERTICAL || this == ALL;
	}
	
	public static Reverse fromBooleans(boolean h, boolean v) {
		int val = h ? HORIZONTAL.getValue() : 0;
		val|=v ? VERTICAL.getValue() : 0;
		return fromInt(val);
	}
	
	public static Reverse fromInt(int v) {
		for (Reverse r : values()) {
			if (r.value == v) {
				return r;
			}
		}
		throw new RuntimeException("Unable to get a valid Reverse value.");
	}
}
