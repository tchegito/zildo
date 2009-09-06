package zildo.client.gui.menu;

import zildo.MultiPlayer;
import zildo.SinglePlayer;
import zildo.client.Client;
import zildo.client.SoundPlay.BankSound;
import zildo.monde.Game;

public class StartMenu extends Menu {

	public StartMenu(final Client client) {
        
        final Game game = new Game("polaky", false);

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
                		new MultiPlayer(game, true);
                	}
                };
                ItemMenu itemJoin=new ItemMenu("Join game", BankSound.MenuSelectGame) {
                	public void run() {
                		new MultiPlayer();
                	}
                };
                ItemMenu itemToggleNetwork=new ItemMenu(getNetTypeString()) {
                	public void run() {
                		lan=!lan;
                		this.text=getNetTypeString();
                		client.handleMenu(multiMenu);
                	}
                };
                multiMenu=new Menu(itemCreate, itemJoin, itemToggleNetwork);
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
	}
}
