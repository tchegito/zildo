package zildo.client.gui.menu;

import java.util.List;

import zildo.MultiPlayer;
import zildo.SinglePlayer;
import zildo.client.Client;
import zildo.client.ClientEngineZildo;
import zildo.client.SoundPlay.BankSound;
import zildo.fwk.net.InternetClient;
import zildo.fwk.net.ServerInfo;
import zildo.monde.Game;

/**
 * Class handling all actions user can access from the start menu.
 * 
 * @author tchegito
 *
 */
public class StartMenu extends Menu {
	
	public StartMenu() {
        
		final Client client=ClientEngineZildo.getClientForMenu();
		
        final Game game = new Game("polaky", false);
        final Menu startMenu=this;
        
        ItemMenu itemSinglePlayer=new ItemMenu("Single Player", BankSound.MenuSelectGame) {
        	public void run() {
        		new SinglePlayer(game);
        	}
        };
        
        ItemMenu itemMultiPlayer=new ItemMenu("Multi Player") {
        	Menu multiMenu;
        	boolean lan=true;
        	
        	public void run() {
                ItemMenu itemCreate=new ItemMenu("Create game", BankSound.MenuSelectGame) {
                	public void run() {
                		new MultiPlayer(game, lan);
                	}
                };
                ItemMenu itemJoin=new ItemMenu("Join game", BankSound.MenuSelectGame) {
                	public void run() {
                		if (lan) {
                			new MultiPlayer();
                		} else {
                			// Internet
                    		List<ServerInfo> serversReady=InternetClient.scanExistingServers();
                    		if (serversReady.isEmpty()) {
                    			client.handleMenu(new InfoMenu("No servers found.", multiMenu));
                    		} else {
                    			client.handleMenu(new JoinGameMenu(serversReady));
                    		}
                		}
                	}
                };
                ItemMenu itemToggleNetwork=new ItemMenu(getNetTypeString()) {
                	public void run() {
                		lan=!lan;
                		this.text=getNetTypeString();
                		client.handleMenu(multiMenu);
                	}
                };
                ItemMenu itemBack=new ItemMenu("Back") {
                	public void run() {
                		client.handleMenu(startMenu);
                	}
                };
                
                multiMenu=new Menu("Multiplayer", itemCreate, itemJoin, itemToggleNetwork, itemBack);
                client.handleMenu(multiMenu);
        	}
        	
        	String getNetTypeString() {
        		return "Current network - "+ (lan ? "LAN" : "www");
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
