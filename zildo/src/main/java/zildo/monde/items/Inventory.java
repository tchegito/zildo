/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tchegito
 *
 */
public class Inventory {

	public List<StoredItem> items;

	public Inventory(List<StoredItem> p_items) {
		items = p_items;
	}
	
	private Inventory() {
		items = new ArrayList<StoredItem>();
	}
	
	public static Inventory fromItems(List<Item> p_items) {
		Inventory i = new Inventory();
		for (Item item : p_items) {
			i.add(item, 1);
		}
		return i;
	}

	private void add(Item item, int quantity) {
		int index = items.indexOf(item);
		if (index == -1) {
			items.add(new StoredItem(item, item.getPrice(), 1));
		} else {
			items.get(index).quantity +=quantity ;
		}
	}
	
	public void add(Item it) {
		add(it, 1);
	}
	
	public void remove(Item item) {
		int index = items.indexOf(item);
		if (index != -1) {
			items.get(index).quantity--;
		}
	}
	
	public int indexOf(Item it) {
		return items.indexOf(new StoredItem(it, 1,1));
	}
}
