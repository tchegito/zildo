package zildo.client.gui.menu;

import java.util.ArrayList;
import java.util.List;

import zildo.MultiPlayer;
import zildo.client.Client;
import zildo.client.ClientEngineZildo;
import zildo.fwk.net.InternetClient;
import zildo.fwk.net.ServerInfo;
import zildo.fwk.ui.InfoMenu;
import zildo.fwk.ui.ItemMenu;
import zildo.fwk.ui.Menu;

public class JoinGameMenu extends Menu {

    Menu currentMenu = this;
    
    Client client=ClientEngineZildo.getClientForMenu();
    
    public JoinGameMenu(List<ServerInfo> serversReady, Menu p_previous) {
        super("Choose a server");
        
        previousMenu=p_previous;
        
        List<ItemMenu> items = new ArrayList<ItemMenu>();
        for (final ServerInfo srv : serversReady) {
            ItemMenu item = new ItemMenu(srv.name) {
                @Override
                public void run() {
                	if (InternetClient.isResponding(srv)) {
                		new MultiPlayer(srv);
                	} else {
    	                client.handleMenu(new InfoMenu(srv.name+" is not responding !", currentMenu));
                	}
                }
            };
            items.add(item);
        }

        items.add(new ItemMenu("Add server") {
            @Override
            public void run() {
            	client.handleMenu(new AddServerMenu(currentMenu));
            }
        });
        
        items.add(new ItemMenu("Back") {
        	public void run() {
        		client.handleMenu(previousMenu);
        	}
        });
        setMenu(items.toArray(new ItemMenu[] {}));
    }
    
    
}