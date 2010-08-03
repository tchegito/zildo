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

import zildo.SinglePlayer;
import zildo.client.Client;
import zildo.client.ClientEngineZildo;
import zildo.client.sound.BankSound;
import zildo.fwk.ui.ItemMenu;
import zildo.fwk.ui.Menu;
import zildo.monde.Game;

/**
 * @author Tchegito
 *
 */
public class SinglePlayerMenu extends Menu {

    Client client=ClientEngineZildo.getClientForMenu();
    Menu currentMenu = this;
    
	public SinglePlayerMenu(Menu p_previous) {
		previousMenu=p_previous;
		
        ItemMenu itemNew=new ItemMenu("m6.new", BankSound.MenuSelectGame) {
        	@Override
			public void run() {
                new SinglePlayer(new Game(null, false)).launchGame();
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
