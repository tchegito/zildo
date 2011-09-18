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
import zildo.monde.items.ItemKind;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.elements.ElementGoodies;

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
	
	// 113, 114
	FLUT, SWORD,
	
	// 115
	QUAD1, QUAD2, QUAD3, QUAD4, QUAD5, QUAD6, QUAD7, QUAD8,
	
	// 123
	KEY, SCROLL, BIG_HEART, HEART_FRAGMENT, COMPASS, CRYSTAL, LANTERN, FIRE_STAFF, ICE_STAFF,
	EXCALIBUR, BOOK, NECKLACE_RED, GLOVE, NECKLACE_GREEN, NECKLACE_BLUE, TINY_VIAL, VIAL, 
	GRAPNEL, SUPER_GLOVE, FLIPPER, BOOTS, CUP, PURSE, FLASK, FLASK_RED, FLASK_GREEN, FLASK_BLUE,
	// 150
	SHIELD_RED, SHIELD_YELLOW, STAR1, STAR2, STAR3,
	
	// 155
	BOMBS3, SCEPTER, SPADE, BOOK_SIGN, LEAF, MILK,
	
	// 161
	WINDOW_WOOD, PORTRAIT_MARIO, STAFF_POUM, WATER_BRIDGE,
	
	// 165
	CASTLE_RED_FLAG, CASTLE_BLUE_FLAG, CASTLE_WINDOW, STATUE, DOOR_OPEN1, DOOR_OPEN2;

	
	public int getBank() {
		return SpriteBank.BANK_ELEMENTS;
	}
	
	public static ElementDescription fromInt(int p_value) {
		return ElementDescription.values()[p_value];
	}
	
	public int getNSpr() {
		int n=this.ordinal();
		if (name().startsWith("GEAR")) {
			n-=156;
		}
		return n;
	}
	
	public boolean isMoney() {
		int i=ordinal();
		return (i >=GREENMONEY1.ordinal() && i<=REDMONEY3.ordinal());		
	}
	
	public boolean isWeapon() {
		return this==SWORD || this==ENEMYARC_RIGHT1 || this==BOMB || this==BOOMERANG1;
	}
	
	/**
	 * Get the ItemKind associated with this description. Can return NULL if no one is associated.
	 * @return ItemKind
	 */
	public ItemKind getItem() {
		switch (this) {
		case SWORD:
			return ItemKind.SWORD;
		case ENEMYARC_RIGHT1:
			return ItemKind.BOW;
		case BOMB:
			return ItemKind.BOMB;
		case BOOMERANG1:
			return ItemKind.BOOMERANG;
		default:
			return null;
		}
	}

	@Override
	public boolean isBlocking() {
		switch (this) {
		case BAR_UP:
		case BAR_HORIZONTAL:
		case BAR_VERTICAL: 
		case BARREL:
		case WOOD_BAR:
		case CUBE_BLUE: 
		case CUBE_ORANGE:
		case SPADE:
			return true;
		default:
			return false;
		}
	}
	
	public Element createElement() {
		switch (this) {
		case HEART: case BOMBS3: case ARROW_UP:
			return new ElementGoodies();
		default:
			return new Element();
		}
	}
	
	public boolean isShadow() {
		switch (this) {
			case SHADOW:
			case SHADOW_LARGE:
			case SHADOW_MINUS:
			case SHADOW_SMALL:
				return true;
		}
		return false;
	}

}
