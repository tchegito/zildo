/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
 * 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package zildo.monde.sprites.desc;

import zildo.fwk.bank.SpriteBank;
import zildo.monde.sprites.Reverse;
import zildo.monde.sprites.utils.Sprite;
import zildo.monde.util.Angle;
import zildo.resource.Constantes;

public enum ZildoDescription implements SpriteDescription {

	UP_FIXED,
	UP1, UP2, UP3, UP4, UP5, UP6, UP7, UP8,
	RIGHT_FIXED,
	RIGHT1, RIGHT2, RIGHT3, RIGHT4, RIGHT5, RIGHT6, RIGHT7,
	DOWN_FIXED,
	DOWN1, DOWN2, DOWN3, DOWN4, DOWN5, DOWN6, DOWN7,

	HANDSUP_UP_FIXED,	//20
	HANDSUP_UP1, HANDSUP_UP2, HANDSUP_UP3, HANDSUP_UP4,
	HANDSUP_RIGHT_FIXED,
	HANDSUP_RIGHT1, HANDSUP_RIGHT2,
	HANDSUP_DOWN_FIXED,
	HANDSUP_DOWN1, HANDSUP_DOWN2, HANDSUP_DOWN3, HANDSUP_DOWN4,

	PULL_UP1, PULL_UP2,	//33
	PULL_RIGHT1, PULL_RIGHT2,
	PULL_DOWN1, PULL_DOWN2,

	LIFT_RIGHT, LIFT_LEFT,	//39
	
	ATTACK_UP1, ATTACK_UP2, ATTACK_UP3, ATTACK_UP4, ATTACK_UP5, //ATTACK_UP6,
	ATTACK_RIGHT1, ATTACK_RIGHT2, ATTACK_RIGHT3, ATTACK_RIGHT4, ATTACK_RIGHT5, //ATTACK_RIGHT6,
	ATTACK_DOWN1, ATTACK_DOWN2, ATTACK_DOWN3, ATTACK_DOWN4, ATTACK_DOWN5, //ATTACK_DOWN6,

	WOUND_UP, WOUND_RIGHT, WOUND_DOWN, WOUND_LEFT,	// 56
	
	PUSH_UP1, PUSH_UP2, PUSH_UP3, PUSH_UP4, PUSH_UP5,
	PUSH_RIGHT1, PUSH_RIGHT2, PUSH_RIGHT3,
	PUSH_DOWN1, PUSH_DOWN2, PUSH_DOWN3,

	JUMP_UP, JUMP_RIGHT, JUMP_DOWN, JUMP_LEFT,	//71
	
	WATFEET1, WATFEET2, WATFEET3, // 75 (Feet in the water)
	
	SHIELD_UP, SHIELD_RIGHT, SHIELD_DOWN, SHIELD_LEFT, // 78
	
	ARMSRAISED, // 82
	
	BOW_UP1, BOW_UP2, BOW_UP3,
	BOW_RIGHT1, BOW_RIGHT2, BOW_RIGHT3,
	BOW_DOWN1, BOW_DOWN2, BOW_DOWN3,

	// 92
	DIRT1, DIRT2, DIRT3,	// feet in dirt
	
	LAYDOWN, FALLING,
	
	SWORD0, SWORD1, SWORD2, SWORD3,	// 97
	
	ZILDO_FLUT,
	
	SLEEPING, WAKINGUP, WAKEUP, TURNRIGHT,
	
	MSWORD0, MSWORD1, MSWORD2, MSWORD3,
	
	FORK0, ARM, FORK1, HOLDINGFORK1, HOLDINGFORK2, HOLDINGFORK3;
	
	
	public int getBank() {
		return SpriteBank.BANK_ZILDO;
	}
	
	static final int[][] seq_zildoDeplacement={
		{1,2,3,4,7,5, 6, 8} ,{0,1,2,3,7,6,4,5},
		{1,2,3,4, 5, 2,6,7},{0,1,2,3,7,6,4,5}};
	
	static final int[][] seq_zildoDeplacement_slow={
			{1,1,2,2,3,3, 4, 4} ,{0,0,1,1,7,7,6,6},
			{0,0,1,1, 2, 2,1,1},{0,0,1,1,7,7,6,6}};
	
	static int seq_1[] = { 0, 1, 2, 1 };
	static int seq_2[] = { 0, 1, 2, 1, 0, 3, 4, 3 };
	
	static final int[] seq_zildoBow = { 0, 1, 2, 1 };

	public static Sprite getMoving(Angle p_angle, int p_seq, boolean slow) {
		// 1) Fixed position
		ZildoDescription desc=ZildoDescription.UP_FIXED;
		Reverse reverse=Reverse.NOTHING;
		switch (p_angle) {
		case EST:
			desc=RIGHT_FIXED; break;
		case SUD:
			desc=DOWN_FIXED; break;
		case OUEST:
			desc=RIGHT_FIXED;	// Right is left with reverse attribute
			reverse = Reverse.HORIZONTAL;
			break;
		case NORD:
			reverse = reverse.flipHorizontal();
		}
		// 2) Moving
		int n=0;
		if (p_seq != -1) {
			if (slow) {
				if (p_angle == Angle.SUD) {
					desc = ZildoDescription.HOLDINGFORK1;
				}
				n=seq_zildoDeplacement_slow[p_angle.value][p_seq];
			} else {
				n=seq_zildoDeplacement[p_angle.value][p_seq];
			}
		}
		return fillSprite(desc, Math.abs(n), reverse);
	}
	
	/**
	 * Return correct sprite for Zildo raising his hands.
	 * @param p_angle
	 * @param p_seq 0..7
	 * @return Sprite
	 */
	public static Sprite getArmraisedMoving(Angle p_angle, int p_seq) {
		// 1) Fixed position
		ZildoDescription desc=ZildoDescription.HANDSUP_UP_FIXED;
		Reverse reverse=Reverse.NOTHING;
		switch (p_angle) {
		case EST:
			desc=HANDSUP_RIGHT_FIXED; break;
		case SUD:
			desc=HANDSUP_DOWN_FIXED; break;
		case OUEST:
			desc=HANDSUP_RIGHT_FIXED;	// Right is left with reverse attribute
			reverse = Reverse.HORIZONTAL;
			break;
		}
		// 2) Moving
		int n=0;
		if (p_seq != 0) {
			if (p_angle.isVertical()) {
				n = seq_2[p_seq];
			} else {
				n = seq_1[p_seq % 4];
			}
		}
		
		return fillSprite(desc, n, reverse);
	}
	
	public static Sprite getBowAttacking(Angle p_angle, int p_seq) {
		ZildoDescription desc = ZildoDescription.BOW_UP1;
		Reverse reverse=Reverse.NOTHING;
		switch (p_angle) {
		case OUEST:
			reverse=Reverse.HORIZONTAL;
		case EST:
			desc=BOW_RIGHT1;break;
		case SUD:
			desc=BOW_DOWN1;break;
		}
		int n = seq_zildoBow[(((4 * 8 - p_seq - 1) % (4 * 8)) / 8)];
		return fillSprite(desc, n, reverse);
	}
	
	public static Sprite getSwordAttacking(Angle p_angle, int p_seq) {
		ZildoDescription desc=ZildoDescription.ATTACK_UP1;
		Reverse reverse=Reverse.NOTHING;
		switch (p_angle) {
		case OUEST:
			reverse=Reverse.HORIZONTAL;
		case EST:
			desc=ATTACK_RIGHT1;break;
		case SUD:
			desc=ATTACK_DOWN1;break;
		}

		if (p_seq == 5) {
			p_seq = 4;
		}
		return fillSprite(desc, p_seq, reverse);
	}
	
	/** Hero is walking, holding his fork **/
	public static Sprite getMovingFork(Angle p_angle, int p_seq) {
		switch (p_angle) {
			case EST:
			case OUEST:
			default:
				return getMoving(p_angle, p_seq, true);
		case NORD:
			if (p_seq == -1) {
				return fillSprite(ZildoDescription.ATTACK_UP1, 0, Reverse.NOTHING);
			} else {
				Sprite s = getMoving(p_angle, p_seq, true);
				s.reverse = Reverse.NOTHING;
				return s;
			}
		case SUD:
			if (p_seq == -1) {
				return fillSprite(ZildoDescription.ATTACK_DOWN1, 0, Reverse.NOTHING);
			} else {
				Sprite s = getMoving(p_angle, p_seq, true);
				s.reverse = Reverse.NOTHING;
				return s;
			}
		}
	}

	public static Sprite getForkAttacking(Angle p_angle, int p_seq) {
		int[] sequence = new int[] {0, 0, 0};
		Reverse r = Reverse.NOTHING;
		ZildoDescription desc = ZildoDescription.ATTACK_RIGHT1;
		switch (p_angle) {
			case OUEST:
				r = Reverse.HORIZONTAL;
			default:
				break;
			case NORD:
				desc = ZildoDescription.ATTACK_UP1;
				break;
			case SUD:
				desc = ZildoDescription.ATTACK_DOWN1;
				break;
		}
		return fillSprite(desc, sequence[p_seq], r);
	}
	
	public static Sprite getPushing(Angle p_angle, int p_seq) {
		ZildoDescription desc = ZildoDescription.PUSH_UP1;
		Reverse reverse = Reverse.NOTHING;
		int add = 0;
		switch (p_angle) {
		case NORD:
			add = seq_2[(p_seq / 2 % (8 * Constantes.speed)) / Constantes.speed];
			break;
		case OUEST:
			reverse = Reverse.HORIZONTAL;
		case EST:
			if (p_angle == Angle.OUEST){ 
				add -= 6;
			}
		default:
			add -= 3;
			desc = ZildoDescription.PUSH_RIGHT1;
			add+= p_angle.value * 3 + seq_1[(p_seq / 2 % (4 * Constantes.speed)) / Constantes.speed];
		}
		
		return fillSprite(desc, add, reverse);
	}
	
	public static Sprite getPulling(Angle p_angle, int p_seq) {
		ZildoDescription desc = ZildoDescription.PULL_UP1;
		Reverse reverse = Reverse.NOTHING;
		int add = p_angle.value * 2 + p_seq;
		switch (p_angle) {
		case OUEST:
			reverse = Reverse.HORIZONTAL;
			add-= 4;
		default:
		}
		return fillSprite(desc, add, reverse);
	}
	
	final static Sprite sprReturned = new Sprite(0,  0, Reverse.NOTHING);
	
	private static Sprite fillSprite(SpriteDescription desc, int add, Reverse rev) {
		sprReturned.reverse = rev;
		sprReturned.nSpr = desc.getNSpr() + add;
		sprReturned.spr = desc;
		return sprReturned;
	}
	
	public int getRadius() {
		return 7;
	}
	
	public static ZildoDescription fromInt(int p_value) {
		return ZildoDescription.values()[p_value];
	}
	
	public int getNSpr() {
		return this.ordinal();
	}
	
	public boolean isBlocking() {
		return false;
	}

	public boolean isDamageable() {
		return false;
	}
	
	@Override
	public boolean isPushable() {
		return false;
	}
	
	public boolean isNotFixe() {
		return false;
	}
	
	@Override
	public boolean isOnGround() {
		return false;
	}
	
	@Override
	public boolean isSliping() {
		return true;
	}
	
	@Override
	public boolean doesImpact() {
		return false;
	}
}
