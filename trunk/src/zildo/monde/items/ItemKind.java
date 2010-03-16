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

package zildo.monde.items;

import zildo.monde.sprites.desc.ElementDescription;

public enum ItemKind {

	SWORD(ElementDescription.SWORD), 
	BOOMERANG(ElementDescription.BOOMERANG1), 
	WHIP(ElementDescription.BAR_HORIZONTAL), 
	BOW(ElementDescription.ARROW_UP),
	BOMB(ElementDescription.BOMB),
	FLUT(ElementDescription.FLUT),
	SHIELD(null);
	
	public ElementDescription representation;
	
	private ItemKind(ElementDescription p_itemRepresentation) {
		representation=p_itemRepresentation;
	}
	
	public static ItemKind fromString(String p_str) {
		for (ItemKind kind : values()) {
			if (kind.toString() .equalsIgnoreCase(p_str)) {
				return kind;
			}
		}
		throw new RuntimeException("Item "+p_str+" doesn't exists.");
	}
}
