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

        ItemMenu itemSinglePlayer = new ItemMenu("Single Player", BankSound.MenuSelectGame) {
            @Override
            public void run() {
                new SinglePlayer(new Game("d4", false));
            }
        };

        ItemMenu itemMultiPlayer = new ItemMenu("Multi Player") {
            Menu multiMenu;
            boolean lan = true;
            StringBuilder playerName = new StringBuilder(PlayerNameMenu.loadPlayerName());

            @Override
            public void run() {
                ItemMenu itemCreate = new ItemMenu("Create game", BankSound.MenuSelectGame) {
                    @Override
                    public void run() {
                        new MultiPlayer(new Game("polakym", false), lan);
                	}
                };
                ItemMenu itemJoin=new ItemMenu("Join game", BankSound.MenuSelectGame) {
                	public void run() {
                		if (lan) {
                			new MultiPlayer();
                		} else {
                			// Internet
                    		List<ServerInfo> serversReady=AddServerMenu.loadServersInfos();
                    		if (serversReady.isEmpty()) {
                    			client.handleMenu(new InfoMenu("No servers found.", "Add one", new AddServerMenu(multiMenu)));
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
                ItemMenu itemBack = new ItemMenu("Back") {
                    @Override
                    public void run() {
                        client.handleMenu(startMenu);
                    }
                };

                multiMenu = new Menu("Multiplayer", itemCreate, itemJoin, itemPlayerName, itemToggleNetwork, itemBack) {
                    public void refresh() {
                    	itemPlayerName.setText(getPlayerNameString());
                    }
                };
                client.handleMenu(multiMenu);
        	}
        	
        	String getNetTypeString() {
        		return "Current network - "+ (lan ? "LAN" : "www");
        	}
            String getPlayerNameString() {
                return "Player name - " + playerName.toString();
            }

        };
        
        ItemMenu itemQuit=new ItemMenu("Quit", BankSound.MenuSelectGame) {
        	public void run() {
        		client.stop();
        	}
        };
        
        setMenu(itemSinglePlayer, itemMultiPlayer, itemQuit);
        setTitle("Welcome to Zildo");
	}
}
