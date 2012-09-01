/**
 * Legend of Zildo
 * Copyright (C) 2006-2012 Evariste Boussaton
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
package zildo.client.gui.menu;

import java.util.ArrayList;
import java.util.List;

import zildo.fwk.net.www.WorldRegister;
import zildo.fwk.ui.ItemMenu;
import zildo.fwk.ui.Menu;
import zildo.fwk.ui.PageableMenu;
import zildo.fwk.ui.UnselectableItemMenu;
import zildo.monde.Champion;

/**
 * @author Tchegito
 * 
 */
public class HallOfFameMenu extends PageableMenu {

	
	public HallOfFameMenu(Menu p_previousMenu) {
		super("m10.title");

		this.previousMenu = p_previousMenu;
		items = new ArrayList<ItemMenu>();
		
		// Ask internet server about the champions
		final List<Champion> champions = WorldRegister.getChampions();
		if (champions == null) {
			items.add(new UnselectableItemMenu("m10.internet.out") { });
			setTitle("");
		} else {
			// Display all champions
			for (Champion ch : champions) {
				items.add(new UnselectableItemMenu(ch.toString()) { });
			}
		}
		items.add(new ItemMenu("global.back") {
			@Override
			public void run() {
				client.handleMenu(previousMenu);
			}
		});
		setMenu(items.toArray(new ItemMenu[] {}));
	}
}
