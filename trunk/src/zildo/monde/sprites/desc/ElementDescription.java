/**
 * Legend of Zildo
 * Copyright (C) 2006-2010 Evariste Boussaton
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

public enum ElementDescription  implements SpriteDescription {

	// Elem.spr
	// 0
	JAR, BUSHES, SHADOW, LEAF1, LEAF2, SMOKE, SMOKE_SMALL, TINY_ROCK1, TINY_ROCK2, BIG_KEY, HEART, STONE_HEAVY, STONE,
	// 13
	BLUE1, BLUE2, BLUE3, BLUE4, BLUE5, BLUE6, BLUE7, BLUE8, ANGLE1, ANGLE2, ANGLE3, ANGLE4,
	// 25
	BAR_UP, BAR_HORIZONTAL, BAR_VERTICAL, BARREL, GARDEN_ROCK, GARDEN_PAV_HORIZONTAL, GARDEN_PAV_VERTICAL, HEN,
	// 33
	DEATH_ANIM1, DEATH_ANIM2, DEATH_ANIM3, DEATH_ANIM4, DEATH_ANIM5, DEATH_ANIM6, DEATH_ANIM7,
	// 40
	HEART_LEFT, HEART_RIGHT, ROCK_BALL, SHADOW_SMALL, SPARK_LEFT, SPARK_RIGHT, SPARK_UPLEFT, SPARK_UPRIGHT,
	// 48
	GREENMONEY1, GREENMONEY2, GREENMONEY3, BLUEMONEY1, BLUEMONEY2, BLUEMONEY3, REDMONEY1, REDMONEY2, REDMONEY3,
	// 57
	SMOKE_FEET1, SMOKE_FEET2, SMOKE_FEET3, SHADOW_MINUS,
	// 61
	WALL1, WALL2, WALL3, ROCK_BIG1, ROCK_BIG2, SHADOW_LARGE, BLASON, WOOD_BAR, CUBE_BLUE, CUBE_ORANGE,
	// 71
	ARROW_UP, ARROW_RIGHT, ARROW_DOWN, ARROW_LEFT,
	// 75
	ARROW_LAND_UP1, ARROW_LAND_UP2, ARROW_LAND_UP3,
	ARROW_LAND_RIGHT1, ARROW_LAND_RIGHT2, ARROW_LAND_RIGHT3,
	ARROW_LAND_DOWN1, ARROW_LAND_DOWN2, ARROW_LAND_DOWN3,
	ARROW_LAND_LEFT1, ARROW_LAND_LEFT2, ARROW_LAND_LEFT3,
	// 87
	BOOMERANG1, BOOMERANG2, BOOMERANG3, BOOMERANG4,
	// 91
	ENEMYARC_UP1, ENEMYARC_UP2,
	ENEMYARC_RIGHT1, ENEMYARC_RIGHT2, 
	ENEMYARC_DOWN1, ENEMYARC_DOWN2, 
	ENEMYARC_LEFT1, ENEMYARC_LEFT2,
	// 99
	IMPACT1, IMPACT2, IMPACT3, IMPACT4,
	// 103
	REDBALL1, REDBALL2, REDBALL3,
	// 106
	BOMB, EXPLO1, EXPLO2, EXPLO3, EXPLOSMOKE1, EXPLOSMOKE2, EXPLOSMOKE3,
	
	// 113
	FLUT,
	
	// 114
	QUAD1, QUAD2, QUAD3, QUAD4, QUAD5, QUAD6, QUAD7, QUAD8;
	
	public int getBank() {
		return SpriteBank.BANK_ELEMENTS;
	}
	
	public static ElementDescription fromInt(int p_value) {
		return ElementDescription.values()[p_value];
	}
	
	public int getNSpr() {
		return this.ordinal();
	}
	
	public boolean isMoney() {
		int i=ordinal();
		return (i >=GREENMONEY1.ordinal() && i<=REDMONEY3.ordinal());		
	}
}
