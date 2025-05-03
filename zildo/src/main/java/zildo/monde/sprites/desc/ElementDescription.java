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
import zildo.fwk.ui.UIText;
import zildo.monde.items.ItemKind;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.elements.ElementGoodies;

public enum ElementDescription implements SpriteDescription {

	// Elem.spr
	// 0
	JAR, BUSHES, SHADOW, LEAF_GREEN, DROP_FLOOR, SMOKE, SMOKE_SMALL, TINY_ROCK, NETTLE_LEAF, FORK_HIGH, HEART, STONE_HEAVY, STONE,
	// 13
	BLUE1, BLUE2, BLUE3, BLUE4, BLUE5, BLUE6, BLUE7, BLUE8, ANGLE1, ANGLE2, ANGLE3, ANGLE4,
	// 25
	BAR_UP, BAR_HORIZONTAL, BAR_VERTICAL, BARREL, GARDEN_ROCK, GARDEN_PAV_HORIZONTAL, GARDEN_PAV_VERTICAL, FORESTAY,
	// 33
	DEATH_ANIM1, DEATH_ANIM2, DEATH_ANIM3, DEATH_ANIM4, DEATH_ANIM5, DEATH_ANIM6, DEATH_ANIM7,
	// 40
	DROP_SMALL, DROP_MEDIUM, ROCK_BALL, SHADOW_SMALL, POISONBALL, POISONGOOP, SPARK_UPLEFT, SPARK_UPRIGHT,
	// 48
	GOLDCOIN1, GOLDCOIN2, GOLDCOIN3, THREEGOLDCOINS1, THREEGOLDCOINS2, THREEGOLDCOINS3, GOLDPURSE1, GOLDPURSE2, GOLDPURSE3,
	// 57
	SMOKE_FEET1, SMOKE_FEET2, SMOKE_FEET3, SHADOW_MINUS,
	// 61
	WALL1, WALL2, FORESTAY_PILLAR, ROCK_BIG1, BAR_SIGN, SHADOW_LARGE, BLASON, WOOD_BAR, CUBE_BLUE, CUBE_ORANGE,
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
	DYNAMITE, EXPLO1, EXPLO2, EXPLO3, EXPLOSMOKE1, EXPLOSMOKE2, EXPLOSMOKE3,

	// 113, 114
	FLUT, SWORD,

	// 115
	STONEWALL1, STONEWALL2, DYNAMITE2, FIRE_SPIRIT1, FIRE_SPIRIT2, FIRE_SPIRIT3, QUAD5, QUAD6,

	// 123
	KEY, SCROLL, BIG_HEART, HEART_FRAGMENT, HAMMER, CRYSTAL, LANTERN, FIRE_STAFF, ITEM_SCEPTER, EXCALIBUR, BOOK, NECKLACE_RED, CREEPER4, NECKLACE_GREEN, NECKLACE_BLUE, TINY_VIAL, VIAL, GRAPNEL, SUPER_GLOVE, FLIPPER, BOOTS, CUP, PURSE, FLASK, FLASK_RED, FLASK_YELLOW, FLASK_BLUE,
	// 150
	SHIELD_BLUE, SHIELD_YELLOW, STAR1, STAR2, STAR3,

	// 155
	BOMBS3, SCEPTER, SPADE, BOOK_SIGN, LEAF, MILK,

	// 161
	WINDOW_WOOD, PORTRAIT_MARIO, STAFF_POUM, WATER_BRIDGE,

	// 165
	CASTLE_RED_FLAG, CASTLE_BLUE_FLAG, CASTLE_WINDOW, STATUE, DOOR_OPEN1, DOOR_OPEN2,
	// 171
	STONEWALL, CARPET,
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
	PLATE, FORK, CANDLE1, CANDLE2, CANDLE3,
	// 207
	POISON1, POISON2, CARPET2,
	// 210
	WATER_LILY,
	BOTTLE_EMPTY, BOTTLE_RED,
	// 213
	WATER_LEAF,
	// 214
	BROWNSPHERE1, BROWNSPHERE2, BROWNSPHERE3,
	REDSPHERE1, REDSPHERE2, REDSPHERE3,
	// 220
	ANCHOR_BAN, EATEN_FISH, PRIEST_SIGN,
	// 223
	WATER_ANIM1, WATER_ANIM2, WATER_ANIM3, WATER_ANIM4,
	// 227
	INVENTOR_SIGN,
	// 228
	EMPTY_BAG, FULL_BAG,
	// 230
	SEWER_BARH, SEWER_BARV, SEWER_BARUP,
	// 233
	SEWER_SMOKE1, SEWER_SMOKE2, SEWER_VOLUT1, SEWER_VOLUT2, SEWER_VOLUT3, SEWER_VOLUT4,
	// 239
	WATERWAVE1, WATERWAVE2, WATERWAVE3,
	// 242
	FIRE_RING, 
	// 243
	PURPLE_FIREFLY, BLUE_FIREFLY,
	// 245
	FIRE_BALL,	// Fireball for dragon
	// 246
	WILL_O_WIST, WILL_O_REFLECT,
	// 248
	BUNCH_LEAVES, HOLE_STUMP,
	// 250
	MIDDLE_SWORD,
	// 251
	BULLET, //,	PRIEST_SIGN;
	// 252
	CREEPER1, CREEPER2, CREEPER3A, CREEPER3B,
	// 256
	HEARTH1, HEARTH2, HEARTH3, HEARTH4, HEARTH5, HEARTH6,
	// 262	==> we store a byte so 262 means 5=SMOKE
	CAULDRON1, CAULDRON2, CAULDRON3, GREEN_BUBBLE,
	// Wind sucking from fire elemental
	FIREWIND1, FIREWIND2, FIREWIND3,
	DRAGON_KEY, PORTAL_KEY,
	// Bitey's GNAP (when the plant shut her tooth)
	GNAP1, GNAP2, GNAP3, GNAP4, GNAP5,
	// Catapult
	CATAPULT_CRATE, CATAPULT_BRANCH,
	// Straw on the fork, and flying
	STRAW, STRAWF1, STRAWF2, STRAWF3,
	// Sand projection
	SAND1, SAND2, SAND3, SAND4, SAND5,
	AMPHORA,
	// Bag of sand
	BAGSAND, FLOWERVASE,
	CHAIR,
	// Hook of the butcher
	HOOKCHAIN, HOOK, 
	// Symbols on slabs
	SLABS1, SLABS2, SLABS3, SLABS4, SLABS5, SLABS6, SLABS7, SLABS8, SLABS9, SLABS10,
	BUNCH_LEAVESFORK,
	WOODEN_BAR;
	
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

	/**
	 * Get the ItemKind associated with this description. Can return NULL if no
	 * one is associated.
	 * 
	 * @return ItemKind
	 */
	public ItemKind getItem() {
		return ItemKind.fromDesc(this);
	}

	@Override
	public boolean isBlocking() {
		switch (this) {
		case BAR_UP: case SEWER_BARUP:
		case BAR_HORIZONTAL: case SEWER_BARH:
		case BAR_VERTICAL: case SEWER_BARV:
		case BARREL:
		case STONEWALL1: case STONEWALL2: case STONEWALL:
		case WOOD_BAR:
		case CUBE_BLUE:
		case CUBE_ORANGE:
		case SPADE:
		case STATUE:
		case CRATE:
		case CRATE2:
		case SMALL_TABLE:
		case STONE:
		case STONE_HEAVY:
		case ROCK_PILLAR:
		case FORESTAY_PILLAR:
		case BAGSAND:
		case FLOWERVASE:
		case CHAIR:
		case WOODEN_BAR:
			return true;
		default:
			return false;
		}
	}

	public Element createElement() {
		Element elem = null;
		switch (this) {
		case DROP_FLOOR:
		case BOMBS3:
		case ARROW_UP:
		case HEART_FRAGMENT:
			elem =  new ElementGoodies();
			break;
		default:
			elem =new Element();
		}
		elem.setDesc(this);
		return elem;
	}

	public boolean isShadow() {
		switch (this) {
		case SHADOW:
		case SHADOW_LARGE:
		case SHADOW_MINUS:
		case SHADOW_SMALL:
			return true;
		default:
				return false;
		}
	}

	/** Note: a sprite will be considered as {@link Element} if this method returns TRUE. Otherwise, it will be a {@link SpriteEntity}. **/
	public boolean isPushable() {
		switch (this) {
		case CUBE_BLUE:
		case CUBE_ORANGE:
		case BARREL:
		case CRATE:
		case CRATE2:
		case STATUE:
		case CANDLE1:
			return true;
		default:
			return false;
		}
	}
	
	// Elements that Zildo can throw on enemies
	public boolean isDamageable() {
		if (damage == null) {
			damage = false;
			switch (this) {
				case JAR:
				case BUSHES:
				case AMPHORA:
				//case SHADOW:		// ???
				case STONE:
				case STONE_HEAVY:
				case DEATH_ANIM6:	// ???
				case ROCK_BALL:
				case ARROW_UP:
				case ARROW_DOWN:
				case ARROW_RIGHT:
				case ARROW_LEFT:
				case DYNAMITE:
				case DYNAMITE2:
				case STAFF_POUM:
				case BIG_FIRE_BALL:
				case PEEBLE:
				case POISON1: case POISON2:
				case REDSPHERE1: case REDSPHERE2: case REDSPHERE3:
				case BROWNSPHERE1: case BROWNSPHERE2: case BROWNSPHERE3:
				case FIRE_BALL:
				case EXPLO1: case EXPLO2: case EXPLO3:
				case SEWER_SMOKE1: case SEWER_SMOKE2:
				case SEWER_VOLUT1: case SEWER_VOLUT2: case SEWER_VOLUT3: case SEWER_VOLUT4:
				case BULLET:
				case POISONBALL:
				case POISONGOOP:
				case HOOKCHAIN: case HOOK:
				case FIRE_SPIRIT1:
				case CANDLE1: case BUNCH_LEAVES: case BUNCH_LEAVESFORK:	// To be eligible for CATCHING_FIRE and LIGHTING_FIRE
				case CREEPER1: case CREEPER2: case CREEPER3A: case CREEPER3B: case CREEPER4:
					damage = true;
				default:
					break;
			}
		}
		return damage;
	}

	public boolean isOutsidemapAllowed() {
		return this == HEART_FRAGMENT || this == HEART_FRAGMENT2 || isSliping();
	}

	public String getFoundSentence(String add) {
    	String label=UIText.getGameText("automatic."+name()+add);
    	if (label.startsWith("automatic.")) {
    		return null;	// Label doesn't exist (security but this shouldn't occur)
    	}
    	return label;
	}
	
	/**
	 * If this methods returns TRUE, then element is submitted to physics.
	 */
	public boolean isNotFixe() {
		switch (this) {
		case JAR: case AMPHORA: case BUSHES: case LEAF: case STRAWF1: case STRAWF2: case STRAWF3:
		//case BUNCH_LEAVES:
		case LEAF_GREEN: case DROP_FLOOR: case SMOKE:
		case SMOKE_SMALL: case TINY_ROCK: case NETTLE_LEAF:
		case HEART: case STONE_HEAVY: case STONE:
		case BARREL:
		case DROP_SMALL: case DROP_MEDIUM: case ROCK_BALL:
		case POISONBALL: case SPARK_UPLEFT: case SPARK_UPRIGHT:
		case GOLDCOIN1: case GOLDCOIN2: case GOLDCOIN3:
		case THREEGOLDCOINS1: case THREEGOLDCOINS2: case THREEGOLDCOINS3:
		case GOLDPURSE1: case GOLDPURSE2: case GOLDPURSE3:
		case CUBE_BLUE: case CUBE_ORANGE:
		case ARROW_UP: case ARROW_RIGHT: case ARROW_DOWN: case ARROW_LEFT:
		case BOOMERANG1: case BOOMERANG2: case BOOMERANG3: case BOOMERANG4:
		case DYNAMITE: case DYNAMITE2: case BOMBS3:
		case KEY: case STAFF_POUM: case BIG_FIRE_BALL:
		case HEART_FRAGMENT: case NOTE: case NOTE2:
		case PEEBLE: case ZZZ1: case ZZZ2: case STAR1: case STAR2: case STAR3:
		case CRATE: case WATER_LEAF:
		case REDSPHERE1: case REDSPHERE2: case REDSPHERE3:
		case BROWNSPHERE1: case BROWNSPHERE2: case BROWNSPHERE3:
		case FIRE_BALL:
		case SEWER_SMOKE1: case SEWER_SMOKE2:
		case SEWER_VOLUT1: case SEWER_VOLUT2: case SEWER_VOLUT3: case SEWER_VOLUT4:
		case WATERWAVE1: case WATERWAVE2: case WATERWAVE3:
		case SWORD: case FIRE_RING:
		case BLUE_FIREFLY: case FIRE_SPIRIT1: case FIRE_SPIRIT2: case FIRE_SPIRIT3:
		case BULLET: case GREEN_BUBBLE:
		case FIREWIND1: case FIREWIND2: case FIREWIND3:
		case DRAGON_KEY: case PORTAL_KEY:
		case HOOKCHAIN: case HOOK:
		case SLABS1: case SLABS2: case SLABS3: case SLABS4: case SLABS5: case SLABS6: case SLABS7: case SLABS8: case SLABS9: case SLABS10:
			return true;
		default:
			return false;
		}		
	}
	
	@Override
	public boolean isSliping() {
		return this == WATER_LEAF;
	}
	
	static public boolean isPlatform(SpriteDescription desc) {
		return desc == WATER_LEAF || desc == PLATFORM || desc == PersoDescription.TURTLE;
	}
	
	public int getRadius() {
		return 7;
	}
	
	@Override
	public boolean isOnGround() {
		return false;
	}
	
	@Override
	public boolean doesImpact() {
		return this == PEEBLE || this == HOOK;
	}
	
	/** Allow to find a desc even if it's a string containing NULL, for scripting purpose. **/
	static public ElementDescription safeValueOf(String s) {
		if (s == null || "NULL".equals(s.toUpperCase())) {
			return null;
		} else {
			return valueOf(s);
		}
	}

	public boolean hasShadow() {
		switch(this) {
		case NOTE:
		case NOTE2:
		case ZZZ1:
		case ZZZ2:
		case DROP_SMALL:
		case EXPLO1:
		case STAR1:
			return false;
		default:
			return true;
		}
	}
	
	public boolean isBurnable() {
		switch (this) {
		case BUNCH_LEAVES:
		case BUNCH_LEAVESFORK:
		case CREEPER1:
		case CREEPER2:
		case CREEPER3A:
		case CREEPER3B:
		case CREEPER4:
			return true;
		default:
			return false;
		}
	}
	
}
