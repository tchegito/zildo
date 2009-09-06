package zildo.client.gui.menu;

import zildo.MultiPlayer;
import zildo.SinglePlayer;
import zildo.client.Client;
import zildo.client.SoundPlay.BankSound;
import zildo.fwk.net.NetSend;
import zildo.monde.Game;

public class StartMenu extends Menu {
	
	public StartMenu(final Client client) {
        
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
                			new MultiPlayer(NetSend.NET_PORT_IP, NetSend.NET_PORT_SERVER);
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
