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

package zildo.monde.items;

import zildo.fwk.ui.UIText;
import zildo.monde.sprites.desc.ElementDescription;

public enum ItemKind {

	SWORD(ElementDescription.SWORD, 20), 
	BOOMERANG(ElementDescription.BOOMERANG1, 40), 
	WHIP(ElementDescription.BAR_HORIZONTAL, 50), 
	BOW(ElementDescription.ENEMYARC_RIGHT1, 40),
	BOMB(ElementDescription.BOMB, 10),
	FLUT(ElementDescription.FLUT, 1),
	SHIELD(null, 60),
	FLASK_RED(ElementDescription.FLASK_RED, 10);
	
	public ElementDescription representation;
	public int price;
	
	private ItemKind(ElementDescription p_itemRepresentation, int p_price) {
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
}
