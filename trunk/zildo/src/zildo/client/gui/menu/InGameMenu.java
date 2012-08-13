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

import zildo.fwk.ui.InfoMenu;
import zildo.fwk.ui.ItemMenu;
import zildo.fwk.ui.Menu;
import zildo.server.EngineZildo;

/**
 * @author Tchegito
 * 
 */
public class InGameMenu extends Menu {

	public InGameMenu() {
		
		items = new ArrayList<ItemMenu>();
		items.add(new ItemMenu("m7.continue", null) {
			@Override
			public void run() {
				client.handleMenu(null);
			}
		});

		// If client is in singleplayer mode, he's allowed to save his game
		if (!client.isMultiplayer()) {
			items.add(new ItemMenu("m7.save", null) {
				@Override
				public void run() {
					Menu menu;
					if (EngineZildo.scriptManagement.isScripting()) {
						menu = new InfoMenu("m8.impossible", currentMenu);
					} else {
						menu = new SaveGameMenu(false, currentMenu);
					}
					client.handleMenu(menu);
				}
			});
		}

		items.add(new ItemMenu("m7.options", null) {
			@Override
			public void run() {
				client.handleMenu(new OptionsMenu(currentMenu));
			}
		});

		items.add(new ItemMenu("m7.quit") {
			@Override
			public void run() {
				client.quitGame();
			}
		});

		setMenu(items);
		setTitle("m7.title");
	}
}
