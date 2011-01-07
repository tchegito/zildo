/**
 * Legend of Zildo
 * Copyright (C) 2006-2011 Evariste Boussaton
 * Based on original Zelda : link to the past (C) Nintendo 1992
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
	LEFT1, LEFT2, LEFT3, LEFT4, LEFT5, LEFT6, LEFT7,	// 27
	
	HANDSUP_UP_FIXED,
	HANDSUP_UP1, HANDSUP_UP2, HANDSUP_UP3, HANDSUP_UP4,
	HANDSUP_RIGHT_FIXED,
	HANDSUP_RIGHT1, HANDSUP_RIGHT2,
	HANDSUP_DOWN_FIXED,
	HANDSUP_DOWN1, HANDSUP_DOWN2, HANDSUP_DOWN3, HANDSUP_DOWN4,
	HANDSUP_LEFT_FIXED,
	HANDSUP_LEFT1, HANDSUP_LEFT2,	// 43
	
	PULL_UP1, PULL_UP2,
	PULL_RIGHT1, PULL_RIGHT2,
	PULL_DOWN1, PULL_DOWN2,
	PULL_LEFT1, PULL_LEFT2,	// 51
	
	LIFT_RIGHT, LIFT_LEFT,
	
	ATTACK_UP1, ATTACK_UP2, ATTACK_UP3, ATTACK_UP4, ATTACK_UP5, ATTACK_UP6,
	ATTACK_RIGHT1, ATTACK_RIGHT2, ATTACK_RIGHT3, ATTACK_RIGHT4, ATTACK_RIGHT5, ATTACK_RIGHT6,
	ATTACK_DOWN1, ATTACK_DOWN2, ATTACK_DOWN3, ATTACK_DOWN4, ATTACK_DOWN5, ATTACK_DOWN6,
	ATTACK_LEFT1, ATTACK_LEFT2, ATTACK_LEFT3, ATTACK_LEFT4, ATTACK_LEFT5, ATTACK_LEFT6,
	
	WOUND_UP, WOUND_RIGHT, WOUND_DOWN, WOUND_LEFT,	// 81
	
	PUSH_UP1, PUSH_UP2, PUSH_UP3, PUSH_UP4, PUSH_UP5,
	PUSH_RIGHT1, PUSH_RIGHT2, PUSH_RIGHT3,
	PUSH_DOWN1, PUSH_DOWN2, PUSH_DOWN3,
	PUSH_LEFT1, PUSH_LEFT2, PUSH_LEFT3,	// 95
	
	JUMP_UP, JUMP_RIGHT, JUMP_DOWN, JUMP_LEFT,
	
	WATFEET1, WATFEET2, WATFEET3, // 102 (Feet in the water)
	
	SHIELD_UP, SHIELD_RIGHT, SHIELD_DOWN, SHIELD_LEFT, // 106
	
	ARMSRAISED, // 107
	
	BOW_UP1, BOW_UP2, BOW_UP3,
	BOW_RIGHT1, BOW_RIGHT2, BOW_RIGHT3,
	BOW_DOWN1, BOW_DOWN2, BOW_DOWN3,
	BOW_LEFT1, BOW_LEFT2, BOW_LEFT3;
	
	
	
	
	
	
	
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
	
	public boolean isBlocking() {
		return false;
	}
}
