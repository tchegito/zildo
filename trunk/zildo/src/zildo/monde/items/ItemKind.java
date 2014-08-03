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

package zildo.monde.items;

import zildo.fwk.ui.UIText;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.desc.SpriteDescription;
import zildo.monde.sprites.desc.ZildoDescription;

public enum ItemKind {

	SWORD(ElementDescription.SWORD, 20), 
	BOOMERANG(ElementDescription.BOOMERANG1, 40), 
	WHIP(ElementDescription.BAR_HORIZONTAL, 50), 
	BOW(ElementDescription.ENEMYARC_RIGHT1, 40),
	DYNAMITE(ElementDescription.DYNAMITE, 10),
	HAMMER(ElementDescription.SPADE, 10),	// TODO: replace by 'HAMMER' description
	FLUT(ElementDescription.FLUT, 1),
	GLOVE(ElementDescription.GLOVE, 20),
	GLOVE_IRON(ElementDescription.SUPER_GLOVE, 40),
	SHIELD(ZildoDescription.SHIELD_DOWN, 60),
	SHIELD_MEDIUM(ElementDescription.SHIELD_RED, 60),
	SHIELD_LARGE(ElementDescription.SHIELD_YELLOW, 60),
	FLASK_RED(ElementDescription.FLASK_RED, 10),
	SCEPTER(ElementDescription.SCEPTER, 0),
	MILK(ElementDescription.MILK, 0),
	NECKLACE(ElementDescription.NECKLACE, 0),
	ROCK_BAG(ElementDescription.ROCK_BAG, 0),
	BLUEDROP(ElementDescription.DROP_FLOOR, 10),
	FLASK_YELLOW(ElementDescription.FLASK_YELLOW, 100),
	EMPTY_BAG(ElementDescription.EMPTY_BAG, 20),
	FULL_BAG(ElementDescription.FULL_BAG, 20),
	FIRE_RING(ElementDescription.FIRE_RING, 800, 5000);
	
	final public SpriteDescription representation;
	final public int price;
	final public int startLevel;
	
	private ItemKind(SpriteDescription p_itemRepresentation, int p_price) {
		this(p_itemRepresentation, p_price, 0);
	}
	
	private ItemKind(SpriteDescription p_itemRepresentation, int p_price, int p_startLevel) {
		representation = p_itemRepresentation;
		price = p_price;
		startLevel = p_startLevel;
	}
	
	public static ItemKind fromString(String p_str) {
		for (ItemKind kind : values()) {
			if (kind.toString() .equalsIgnoreCase(p_str)) {
				return kind;
			}
		}
		throw new RuntimeException("Item "+p_str+" doesn't exists.");
	}
	
	public static ItemKind fromElemDesc(ElementDescription desc) {
		for (ItemKind kind : values()) {
			if (kind.representation == desc) {
				return kind;
			}
		}
		return null;
	}
	
	public String getName() {
		return UIText.getGameText("item."+name());
	}
	
	public String getFoundSentence() {
    	String label=UIText.getGameText("automatic."+name());
    	if (label.startsWith("automatic.")) {
    		return null;	// Label doesn't exist (security but this shouldn't occur)
    	}
    	return label;
	}
	
	/** Returns TRUE if we should call 'useItem()' when hero buys it **/
	public boolean canBeInInventory() {
		return this != BLUEDROP;
	}
	
	/** Returns TRUE if item can be multiple in the inventory. Typically, dynamites aren't. **/
	public boolean canBeMultiple() {
		return this != DYNAMITE; // && this != FLASK_RED;
	}
}
