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

public class Item {

	public ItemKind kind;
	public int level;
	
	public Item(ItemKind p_kind) {
		kind=p_kind;
		level=0;
	}

	public Item(ItemKind p_kind, int p_level) {
		this(p_kind);
		level=p_level;
	}
	
	public int hashCode() {
	    return kind.ordinal() * 15 + level;
	}
	
	public boolean equals(Object p_item) {
	    return p_item.hashCode() == hashCode();
	}
	
	public int getPrice() {
		return kind.price * (level+1);
	}
	
	public String toString() {
		String s=kind.name().toLowerCase();
		s=s.substring(0,1).toUpperCase() + s.substring(1);
		s+=" (";
		switch (level) {
		case 0:
			s+="courante";
			break;
		case 1:
			s+="arrangee";
			break;
		case 2:
			s+="bonne facture";
			break;
		case 3:
			s+="maitre d'arme";
			break;
		}
		s+=")\n"+getPrice()+" rupees";
		s=s.replaceAll("_", " ");
		return s;
	}
}
