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

import java.util.List;

import zildo.MultiPlayer;
import zildo.SinglePlayer;
import zildo.client.Client;
import zildo.client.ClientEngineZildo;
import zildo.client.SoundPlay.BankSound;
import zildo.fwk.net.ServerInfo;
import zildo.fwk.ui.InfoMenu;
import zildo.fwk.ui.ItemMenu;
import zildo.fwk.ui.Menu;
import zildo.fwk.ui.UIText;
import zildo.monde.Game;

/**
 * Class handling all actions user can access from the start menu.
 * 
 * @author tchegito
 *
 */
public class StartMenu extends Menu {
	
	public StartMenu() {
        
		final Client client = ClientEngineZildo.getClientForMenu();

        final Menu startMenu = this;

        ItemMenu itemSinglePlayer = new ItemMenu("m1.single", BankSound.MenuSelectGame) {
            @Override
            public void run() {
                new SinglePlayer(new Game("d5", false));
            }
        };

        ItemMenu itemMultiPlayer = new ItemMenu("m1.multi") {
            Menu multiMenu;
            boolean lan = true;
            StringBuilder playerName = new StringBuilder(PlayerNameMenu.loadPlayerName());

            @Override
            public void run() {
                ItemMenu itemCreate = new ItemMenu("m2.create", BankSound.MenuSelectGame) {
                    @Override
                    public void run() {
                        new MultiPlayer(new Game("polakym", false), lan);
                	}
                };
                ItemMenu itemJoin=new ItemMenu("m2.join", BankSound.MenuSelectGame) {
                	public void run() {
                		if (lan) {
                			new MultiPlayer();
                		} else {
                			// Internet
                    		List<ServerInfo> serversReady=AddServerMenu.loadServersInfos();
                    		if (serversReady.isEmpty()) {
                    			client.handleMenu(new InfoMenu("mess.noservers", "mess.noservers.add", new AddServerMenu(multiMenu)));
                    		} else {
                    			client.handleMenu(new JoinGameMenu(serversReady, multiMenu));
                    		}
                		}
                	}
                };
                final ItemMenu itemPlayerName = new ItemMenu(getPlayerNameString()) {
                    @Override
                    public void run() {
                        client.handleMenu(new PlayerNameMenu(playerName, multiMenu));
                    }
                };
                ItemMenu itemToggleNetwork = new ItemMenu(getNetTypeString()) {
                    @Override
                    public void run() {
                        lan = !lan;
                        this.setText(getNetTypeString());
                        client.handleMenu(multiMenu);
                    }
                };
                ItemMenu itemBack = new ItemMenu("global.back") {
                    @Override
                    public void run() {
                        client.handleMenu(startMenu);
                    }
                };

                multiMenu = new Menu("m2.title", itemCreate, itemJoin, itemPlayerName, itemToggleNetwork, itemBack) {
                    public void refresh() {
                    	itemPlayerName.setText(getPlayerNameString());
                    }
                };
                client.handleMenu(multiMenu);
        	}
        	
        	String getNetTypeString() {
        		return UIText.getText("m2.currentNet", lan ? "LAN" : "www");
        	}
            String getPlayerNameString() {
                return UIText.getText("m2.playerName", playerName.toString());
            }

        };
        
        ItemMenu itemQuit=new ItemMenu("m1.quit", BankSound.MenuSelectGame) {
        	public void run() {
        		client.stop();
        	}
        };
        
        setMenu(itemSinglePlayer, itemMultiPlayer, itemQuit);
        setTitle("m1.title");
	}
}
