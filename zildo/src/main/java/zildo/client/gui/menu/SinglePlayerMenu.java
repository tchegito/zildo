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

package zildo.client.gui.menu;

import zildo.client.sound.BankSound;
import zildo.client.stage.SinglePlayer;
import zildo.fwk.ui.ItemMenu;
import zildo.fwk.ui.Menu;
import zildo.fwk.ui.UIText;
import zildo.monde.Game;

/**
 * @author Tchegito
 *
 */
public class SinglePlayerMenu extends Menu {

	public SinglePlayerMenu(Menu p_previous) {
		previousMenu=p_previous;
		final StringBuilder sb = new StringBuilder();

		// Create the hidden item, just here to launch the game, once player has set his name
		final ItemMenu runSingle = new ItemMenu("hidden", BankSound.MenuSelectGame) {
			@Override
			public void run() {
				// Create single player game, with setting player name
				String playerName = sb.toString();
				UIText.setCharacterName(playerName);
				client.askStage(new SinglePlayer(new Game(null, playerName)));
			}
		};

		ItemMenu itemNew=new ItemMenu("m6.new", BankSound.MenuSelectGame) {
        	@Override
			public void run() {
        		client.handleMenu(new PlayerNameMenu(sb, currentMenu, runSingle));
        	}
        };
        
        ItemMenu itemLoad=new ItemMenu("m6.load", BankSound.MenuSelectGame) {
        	@Override
			public void run() {
                client.handleMenu(new SaveGameMenu(true, currentMenu));
        	}
        };
        
        ItemMenu itemBack=new ItemMenu("global.back") {
        	@Override
			public void run() {
        		client.handleMenu(previousMenu);
        	}
        };
        
		setMenu(itemNew, itemLoad, itemBack);
		setTitle("m6.title");
	}
	
}
