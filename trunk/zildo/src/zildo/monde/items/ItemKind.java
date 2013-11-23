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
	BAG(ElementDescription.ROCK_BAG, 50),	// TODO: add a real description
	BLUEDROP(ElementDescription.DROP_FLOOR, 10);
	
	public SpriteDescription representation;
	public int price;
	
	private ItemKind(SpriteDescription p_itemRepresentation, int p_price) {
		representation = p_itemRepresentation;
		price = p_price;
	}
	
	public static ItemKind fromString(String p_str) {
		for (ItemKind kind : values()) {
			if (kind.toString() .equalsIgnoreCase(p_str)) {
				return kind;
			}
		}
		throw new RuntimeException("Item "+p_str+" doesn't exists.");
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
}
