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

import java.util.ArrayList;
import java.util.List;

import zildo.Zildo;
import zildo.client.ClientEngineZildo;
import zildo.client.PlatformDependentPlugin;
import zildo.client.PlatformDependentPlugin.KnownPlugin;
import zildo.client.sound.BankMusic;
import zildo.client.sound.BankSound;
import zildo.client.stage.CreditStage;
import zildo.client.stage.MultiPlayer;
import zildo.fwk.gfx.filter.FilterEffect;
import zildo.fwk.net.ServerInfo;
import zildo.fwk.net.www.WorldRegister;
import zildo.fwk.ui.ConfirmMenu;
import zildo.fwk.ui.InfoMenu;
import zildo.fwk.ui.ItemMenu;
import zildo.fwk.ui.Menu;
import zildo.fwk.ui.UIText;
import zildo.monde.Game;
import zildo.resource.Constantes;

/**
 * Class handling all actions user can access from the start menu.
 * 
 * @author tchegito
 *
 */
public class StartMenu extends Menu {
	
	public StartMenu() {
        
		// Play menu music
		ClientEngineZildo.soundPlay.playMusic(BankMusic.Triste, null);
        ClientEngineZildo.filterCommand.fadeIn(FilterEffect.FADE);

        // Display version
		ClientEngineZildo.guiDisplay.displayMessage("v" + Constantes.CURRENT_VERSION_DISPLAYED);

		List<ItemMenu> theItems = new ArrayList<ItemMenu>();
		theItems.add(new ItemMenu("m1.single") {
            @Override
            public void run() {
    			client.handleMenu(new SinglePlayerMenu(currentMenu));
            }
        });

    	// TODO: Dirty way to check Android platform : need to be cleaned with
    	// a better injection mechanism.
    	if (PlatformDependentPlugin.currentPlugin == KnownPlugin.Android) {
    		theItems.add(new ItemMenu("m1.website") {
    			@Override
    			public void run() {
    				openLink("http://www.alembrume.fr");
    			};
    		});
    		theItems.add(new ItemMenu("m1.donate") {
    			@Override
    			public void run() {
    				client.handleMenu(new ConfirmMenu("info.donate", new ItemMenu("global.yes") {
    					@Override
    					public void run() {
    	    				openLink("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=UJ4DYC583FMRY");
    					}
    				}, new ItemMenu("global.no") {
    					@Override
    					public void run() {
    						client.handleMenu(currentMenu);
    					}
    				}));

    			};    			
    		});
    	} else {
	    	theItems.add(new ItemMenu("m1.multi") {
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
	                	@Override
						public void run() {
	                		if (lan) {
	                			new MultiPlayer();
	                		} else {
	                			// Internet
	                    		List<ServerInfo> serversReady=WorldRegister.getStartedServers();
	                    		if (serversReady.isEmpty()) {
	                    			client.handleMenu(new InfoMenu("mess.noservers", currentMenu));
	                    		} else {
	                    			client.handleMenu(new JoinGameMenu(serversReady, multiMenu));
	                    		}
	                		}
	                	}
	                };
	                final ItemMenu itemPlayerName = new ItemMenu(getPlayerNameString()) {
	                    @Override
	                    public void run() {
	                        client.handleMenu(new PlayerNameMenu(playerName, multiMenu, null));
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
	                        client.handleMenu(currentMenu);
	                    }
	                };
	
	                multiMenu = new Menu("m2.title", itemCreate, itemJoin, itemPlayerName, itemToggleNetwork, itemBack) {
	                    @Override
						public void refresh() {
	                    	itemPlayerName.setText(getPlayerNameString());
	                    }
	                };
	                client.handleMenu(multiMenu);
	        	}
	        	
	        	String getNetTypeString() {
	        		return UIText.getMenuText("m2.currentNet", lan ? "LAN" : "www");
	        	}
	            String getPlayerNameString() {
	                return UIText.getMenuText("m2.playerName", playerName.toString());
	            }
	
	        });
    	}
    	
    	theItems.add(new ItemMenu("m1.hof") {
        	@Override
			public void run() {
        		client.handleMenu(new HallOfFameMenu(currentMenu));
        	}
        });
        
    	theItems.add(new ItemMenu("m1.credits") {
        	@Override
			public void run() {
        		client.askStage(new CreditStage(false));
        	}
        });
        
    	theItems.add(new ItemMenu("m1.quit", BankSound.MenuSelectGame) {
        	@Override
			public void run() {
        		client.stop();
        	}
        });
        
        setMenu(theItems);
        setTitle("m1.title");
	}
	
	void openLink(String url) {
		if (Zildo.pdPlugin.openLink(url)) {
			client.handleMenu(currentMenu);
		} else {
			client.handleMenu(new InfoMenu("mess.nobrowser", currentMenu));
		}

	}
}
