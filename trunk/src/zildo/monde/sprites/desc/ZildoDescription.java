package zildo.monde.sprites.desc;

import zildo.fwk.bank.SpriteBank;
import zildo.monde.map.Angle;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.utils.Sprite;

public enum ZildoDescription implements SpriteDescription {

	UP_FIXED,
	UP1, UP2, UP3, UP4, UP5, UP6,
	RIGHT_FIXED,
	RIGHT1, RIGHT2, RIGHT3, RIGHT4, RIGHT5, RIGHT6, RIGHT7,
	DOWN_FIXED,
	DOWN1, DOWN2, DOWN3, DOWN4,
	LEFT_FIXED,
	LEFT1, LEFT2, LEFT3, LEFT4, LEFT5, LEFT6, LEFT7;
	
	public int getBank() {
		return SpriteBank.BANK_ZILDO;
	}
	
	static final int[][] seq_zildoDeplacement={
			{1,2,3,4,-1,5, 6,-2} ,{0,1,2,3,7,6,4,5},
			{1,2,3,4,-1,2,-3,-4},{0,1,2,3,7,6,4,5}};
	
	public static Sprite getMoving(Angle p_angle, int p_seq) {
		// 1) Fixed position
		ZildoDescription desc=ZildoDescription.UP_FIXED;
		switch (p_angle) {
		case EST:
			desc=RIGHT_FIXED; break;
		case SUD:
			desc=DOWN_FIXED; break;
		case OUEST:
			desc=LEFT_FIXED; break;
		}
		// 2) Moving
		int n=0;
		int reverse=0;
		if (p_seq != 0) {
			n=seq_zildoDeplacement[p_angle.value][p_seq];
			reverse=n<0 ? SpriteEntity.REVERSE_HORIZONTAL : 0;
		}
		
		return new Sprite(desc.ordinal() + Math.abs(n), desc.getBank(), reverse);
	}
	
	public static ZildoDescription fromInt(int p_value) {
		return ZildoDescription.values()[p_value];
	}
	
	public int getNSpr() {
		return this.ordinal();
	}
}
