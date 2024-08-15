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

package zildo.monde.items;

import zildo.fwk.ui.UIText;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.desc.SpriteDescription;

public enum ItemKind {

	SWORD(true, ElementDescription.SWORD, 20), 				// Do 1 damage
	MIDSWORD(true, ElementDescription.MIDDLE_SWORD, 100),	// Do 1+1 damage
	BOOMERANG(true, ElementDescription.BOOMERANG1, 40), 
	// Uncomment following line with appropriate description, when whip will be ready
	//WHIP(true, ElementDescription.BAR_HORIZONTAL, 50), 
	MOON(false, ElementDescription.HEART_FRAGMENT, 200),
	BOW(true, ElementDescription.ENEMYARC_RIGHT1, 40),
	DYNAMITE(true, ElementDescription.DYNAMITE, 10),
	HAMMER(true, ElementDescription.HAMMER, 10),
	SPADE_GROUND(true, ElementDescription.SPADE, 10),	// Fork sprite on the ground
	SPADE(true, ElementDescription.FORK_HIGH, 10),	// Fork sprite in inventory
	FLUT(false, ElementDescription.FLUT, 1),
	SHIELD(true, ElementDescription.SHIELD_BLUE, 60),
	SHIELD_MEDIUM(true, ElementDescription.SHIELD_YELLOW, 60),
	//SHIELD_LARGE(true, ElementDescription.SHIELD_YELLOW, 60),
	FLASK_RED(false, ElementDescription.FLASK_RED, 10),
	ITEM_SCEPTER(false, ElementDescription.ITEM_SCEPTER, 0),
	MILK(false, ElementDescription.MILK, 0),
	NECKLACE(false, ElementDescription.NECKLACE, 0),
	ROCK_BAG(true, ElementDescription.ROCK_BAG, 0),
	BLUEDROP(false, ElementDescription.DROP_FLOOR, 10),
	FLASK_YELLOW(false, ElementDescription.FLASK_YELLOW, 100),
	EMPTY_BAG(false, ElementDescription.EMPTY_BAG, 20),
	FULL_BAG(false, ElementDescription.FULL_BAG, 20),
	FIRE_RING(false, ElementDescription.FIRE_RING, 800, 5000),
	CUREPOTION(false, ElementDescription.FLASK_BLUE, 100),
	DRAGON_KEY(false, ElementDescription.DRAGON_KEY, 10),
	PORTAL_KEY(false, ElementDescription.PORTAL_KEY, 10);
	
	final public SpriteDescription representation;
	final public int price;
	final public int startLevel;
	final public boolean isWeapon;
	
	private ItemKind(boolean p_isWeapon, SpriteDescription p_itemRepresentation, int p_price) {
		this(p_isWeapon, p_itemRepresentation, p_price, 0);
	}
	
	private ItemKind(boolean p_isWeapon, SpriteDescription p_itemRepresentation, int p_price, int p_startLevel) {
		representation = p_itemRepresentation;
		price = p_price;
		startLevel = p_startLevel;
		isWeapon = p_isWeapon;
	}
	
	public static ItemKind fromString(String p_str) {
		for (ItemKind kind : values()) {
			if (kind.toString() .equalsIgnoreCase(p_str)) {
				return kind;
			}
		}
		throw new RuntimeException("Item "+p_str+" doesn't exists.");
	}
	
	public static ItemKind fromDesc(SpriteDescription desc) {
		if (desc != null) {
			for (ItemKind kind : values()) {
				if (kind.representation == desc) {
					return kind;
				}
			}
		}
		return null;
	}
	
	public String getName() {
		return UIText.getGameText("item."+name());
	}
	
	public String getFoundSentence(String add) {
    	String label=UIText.getGameText("automatic."+name()+add);
    	if (label.startsWith("automatic.")) {
    		return null;	// Label doesn't exist (security but this shouldn't occur)
    	}
    	return label;
	}
	
	/** Returns TRUE if we should call 'useItem()' when hero buys it **/
	public boolean canBeInInventory() {
		return this != SPADE_GROUND && (isWeapon() || (this != BLUEDROP && this != MOON));
	}
	
	/** Returns TRUE if item can be multiple in the inventory. Typically, dynamites aren't. **/
	public boolean canBeMultiple() {
		return this != DYNAMITE; // && this != FLASK_RED;
	}
	
	/** TRUE means that item can be counted in inventory, with a small digit below it. **/
	public boolean isStackable() {
		return this == FLASK_RED || this == FLASK_YELLOW || this == EMPTY_BAG || this == FULL_BAG;
	}

	public boolean isWeapon() {
		return isWeapon;
	}
}
