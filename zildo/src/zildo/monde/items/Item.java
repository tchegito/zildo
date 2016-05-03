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


public class Item {

	public ItemKind kind;
	public int level;

	public Item(ItemKind p_kind) {
		kind = p_kind;
		level = p_kind.startLevel;
	}

	public Item(ItemKind p_kind, int p_level) {
		this(p_kind);
		level = p_level;
	}

	@Override
	public int hashCode() {
		return kind.ordinal() * 15 + level;
	}

	@Override
	public boolean equals(Object p_item) {
		return p_item.hashCode() == hashCode();
	}

	public int getPrice() {
		return kind.price * (level + 1);
	}
	
	@Override
	public String toString() {
		return "["+kind.toString()+","+level+"]";
	}
	
	public static Item fromStrings(String entry1, String entry2) {
		Item item = new Item(
				ItemKind.fromString(entry1)
				,Integer.valueOf(entry2)
		);
		return item;
	}
}
