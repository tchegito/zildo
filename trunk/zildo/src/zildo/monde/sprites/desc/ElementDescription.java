/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
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
import zildo.fwk.ui.UIText;
import zildo.monde.items.ItemKind;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.elements.ElementGoodies;

public enum ElementDescription implements SpriteDescription {

	// Elem.spr
	// 0
	JAR, BUSHES, SHADOW, LEAF_GREEN, DROP_FLOOR, SMOKE, SMOKE_SMALL, TINY_ROCK1, TINY_ROCK2, BIG_KEY, HEART, STONE_HEAVY, STONE,
	// 13
	BLUE1, BLUE2, BLUE3, BLUE4, BLUE5, BLUE6, BLUE7, BLUE8, ANGLE1, ANGLE2, ANGLE3, ANGLE4,
	// 25
	BAR_UP, BAR_HORIZONTAL, BAR_VERTICAL, BARREL, GARDEN_ROCK, GARDEN_PAV_HORIZONTAL, GARDEN_PAV_VERTICAL, HEN,
	// 33
	DEATH_ANIM1, DEATH_ANIM2, DEATH_ANIM3, DEATH_ANIM4, DEATH_ANIM5, DEATH_ANIM6, DEATH_ANIM7,
	// 40
	DROP_SMALL, DROP_MEDIUM, ROCK_BALL, SHADOW_SMALL, SPARK_LEFT, SPARK_RIGHT, SPARK_UPLEFT, SPARK_UPRIGHT,
	// 48
	GOLDCOIN1, GOLDCOIN2, GOLDCOIN3, THREEGOLDCOINS1, THREEGOLDCOINS2, THREEGOLDCOINS3, GOLDPURSE1, GOLDPURSE2, GOLDPURSE3,
	// 57
	SMOKE_FEET1, SMOKE_FEET2, SMOKE_FEET3, SHADOW_MINUS,
	// 61
	WALL1, WALL2, WALL3, ROCK_BIG1, BAR_SIGN, SHADOW_LARGE, BLASON, WOOD_BAR, CUBE_BLUE, CUBE_ORANGE,
	// 71
	ARROW_UP, ARROW_RIGHT, ARROW_DOWN, ARROW_LEFT,
	// 75
	ARROW_LAND_UP1, ARROW_LAND_UP2, ARROW_LAND_UP3, ARROW_LAND_RIGHT1, ARROW_LAND_RIGHT2, ARROW_LAND_RIGHT3, ARROW_LAND_DOWN1, ARROW_LAND_DOWN2, ARROW_LAND_DOWN3, ARROW_LAND_LEFT1, ARROW_LAND_LEFT2, ARROW_LAND_LEFT3,
	// 87
	BOOMERANG1, BOOMERANG2, BOOMERANG3, BOOMERANG4,
	// 91
	ENEMYARC_UP1, ENEMYARC_UP2, ENEMYARC_RIGHT1, ENEMYARC_RIGHT2, ENEMYARC_DOWN1, ENEMYARC_DOWN2, ENEMYARC_LEFT1, ENEMYARC_LEFT2,
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
	KEY, SCROLL, BIG_HEART, HEART_FRAGMENT, COMPASS, CRYSTAL, LANTERN, FIRE_STAFF, ICE_STAFF, EXCALIBUR, BOOK, NECKLACE_RED, GLOVE, NECKLACE_GREEN, NECKLACE_BLUE, TINY_VIAL, VIAL, GRAPNEL, SUPER_GLOVE, FLIPPER, BOOTS, CUP, PURSE, FLASK, FLASK_RED, FLASK_GREEN, FLASK_BLUE,
	// 150
	SHIELD_RED, SHIELD_YELLOW, STAR1, STAR2, STAR3,

	// 155
	BOMBS3, SCEPTER, SPADE, BOOK_SIGN, LEAF, MILK,

	// 161
	WINDOW_WOOD, PORTRAIT_MARIO, STAFF_POUM, WATER_BRIDGE,

	// 165
	CASTLE_RED_FLAG, CASTLE_BLUE_FLAG, CASTLE_WINDOW, STATUE, DOOR_OPEN1, DOOR_OPEN2,
	// 171
	CEMETERY_DOOR, CARPET,
	// 173
	LAUNCHER1, LAUNCHER2,
	// 175
	PROJ_LAVA, PROJ_ICE, BIG_FIRE_BALL, SMALL_FIRE_BALL,
	PLATFORM,
	// 180
	BLUE_ENERGY,
	// 181
	HEART_FRAGMENT2, NECKLACE,
	// 183
	NOTE, PSYCHIC_SIGN, WITCH_SIGN, NOTE2,
	// 187
	ROCK_BAG, PEEBLE, ROCK_PILLAR,
	// 190
	LAVADROP1, LAVADROP2, LAVADROP3, LAVADROP4,
	// 194
	DUST1, DUST2, DUST3,
	// 197
	ZZZ1, ZZZ2,
	// 199
	CRATE, CRATE2, SMALL_TABLE,
	// 202
	PLATE, FORK, CANDLE1, CANDLE2, CANDLE3;
	
	Boolean damage;
	
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
		int i = ordinal();
		return (i >= GOLDCOIN1.ordinal() && i <= GOLDPURSE3.ordinal());
	}

	public boolean isWeapon() {
		return this == SWORD || this == ENEMYARC_RIGHT1 || this == BOMB
				|| this == BOOMERANG1 || this == ROCK_BAG;
	}

	/**
	 * Get the ItemKind associated with this description. Can return NULL if no
	 * one is associated.
	 * 
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
		case NECKLACE:
			return ItemKind.NECKLACE;
		case ROCK_BAG:
			return ItemKind.ROCK_BAG;
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
		case STATUE:
		case CRATE:
		case CRATE2:
			return true;
		default:
			return false;
		}
	}

	public Element createElement() {
		switch (this) {
		case DROP_FLOOR:
		case BOMBS3:
		case ARROW_UP:
		case HEART_FRAGMENT:
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

	public boolean isPushable() {
		switch (this) {
		case CUBE_BLUE:
		case CUBE_ORANGE:
		case BARREL:
		case CRATE:
		case STATUE:
			return true;
		}
		return false;
	}
	
	// Elements that Zildo can throw on enemies
	public boolean isDamageable() {
		if (damage == null) {
			damage = false;
			switch (this) {
				case JAR:
				case BUSHES:
				case SHADOW:		// ???
				case STONE:
				case STONE_HEAVY:
				case DEATH_ANIM6:	// ???
				case ROCK_BALL:
				case ARROW_UP:
				case ARROW_DOWN:
				case ARROW_RIGHT:
				case ARROW_LEFT:
				case BOMB:
				case STAFF_POUM:
				case BIG_FIRE_BALL:
				case PEEBLE:
					damage = true;
					break;
			}
		}
		return damage;
	}

	public boolean isOutmapAllowed() {
		return this == HEART_FRAGMENT || this == HEART_FRAGMENT2;
	}

	public String getFoundSentence(String add) {
    	String label=UIText.getGameText("automatic."+name()+add);
    	if (label.startsWith("automatic.")) {
    		return null;	// Label doesn't exist (security but this shouldn't occur)
    	}
    	return label;
	}
}
