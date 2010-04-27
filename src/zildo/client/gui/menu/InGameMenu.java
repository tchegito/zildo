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

package zildo.client.gui.menu;

import java.util.ArrayList;
import java.util.List;

import zildo.client.Client;
import zildo.client.ClientEngineZildo;
import zildo.fwk.ui.ItemMenu;
import zildo.fwk.ui.Menu;

/**
 * @author Tchegito
 *
 */
public class InGameMenu extends Menu {

	public InGameMenu() {
		
		final Client client = ClientEngineZildo.getClientForMenu();

		List<ItemMenu> items=new ArrayList<ItemMenu>();
		
		final Menu currentMenu=this;
		
        items.add(new ItemMenu("m7.continue", null) {
        	public void run() {
                client.handleMenu(null);
        	}
        });
        
        // If client is in singleplayer mode, he's allowed to save his game
        if (!client.isMultiplayer()) {
        	items.add(new ItemMenu("m7.save", null) {
        	public void run() {
                List<String> saves=SaveGameMenu.findSavegame();
                client.handleMenu(new SaveGameMenu(saves, false, currentMenu));
        	}
        });
        }
        
        items.add(new ItemMenu("m7.quit") {
        	public void run() {
        		client.stop();
        	}
        });
        
		setMenu(items);
		setTitle("m7.title");
	}
}
