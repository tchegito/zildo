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

package zildo.client.stage;

import zildo.client.Client;
import zildo.client.ClientEngineZildo;
import zildo.client.gui.GUIDisplay;
import zildo.client.gui.GUISequence;
import zildo.fwk.ui.ItemMenu;
import zildo.fwk.ui.Menu;
import zildo.fwk.ui.MenuListener;

/**
 * @author Tchegito
 *
 */
public class MenuStage extends GameStage {

	ItemMenu item;
	MenuListener menuListener;
	GUIDisplay guiDisplay;
	Client client;
	Menu menu;
	
	public MenuStage(Menu menu) {
		this.menu = menu;
        client = ClientEngineZildo.getClientForGame();
		this.menuListener = client.getMenuListener();
		guiDisplay = ClientEngineZildo.guiDisplay;
		launchGame();
	}
	
	@Override
	public void launchGame() {
        menu.refresh();
	}
	
	@Override
	public void updateGame() {
		item = menuListener.act(menu);
		if (item == null && client.getMenuTransition().isReadyToInteract()) {	// No move during a fade
			item = menuListener.actSpe(menu);
		}
		if (item != null && item.isSelectable()) {
			// Terminates menu and schedule selected action to execute
			client.setAction(item);
		}
		
		if (client.getMenuTransition().isCurrentOver()) {
			endGame();
		}
	}

	 /**
	  *  Render menu. Keys are managed directly from Menu object.
	  */
	@Override
	public void renderGame() {
		if (!done && item == null) {
			guiDisplay.displayMenu(menu, client.getMenuTransition().getFadeLevel());
		}
	}

	@Override
	public void endGame() {
		guiDisplay.endMenu();
		done = true;
		guiDisplay.clearSequences(GUISequence.TEXT_MENU);
	}

	public void askForItemMenu(ItemMenu it) {
		menuListener.askFor(it);
	}
}
