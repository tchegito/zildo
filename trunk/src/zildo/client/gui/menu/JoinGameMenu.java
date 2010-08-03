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
import zildo.fwk.ui.UIText;

public class JoinGameMenu extends Menu {

    Menu currentMenu = this;
    
    Client client=ClientEngineZildo.getClientForMenu();
    
    public JoinGameMenu(List<ServerInfo> serversReady, Menu p_previous) {
        super("m4.title");
        
        previousMenu=p_previous;
        
        List<ItemMenu> items = new ArrayList<ItemMenu>();
        for (final ServerInfo srv : serversReady) {
            ItemMenu item = new ItemMenu(srv.name) {
                @Override
                public void run() {
                	if (InternetClient.isResponding(srv)) {
                		new MultiPlayer(srv);
                	} else {
    	                client.handleMenu(new InfoMenu(UIText.getText("m4.error", srv.name), currentMenu));
                	}
                }
            };
            items.add(item);
        }

        items.add(new ItemMenu("m4.add") {
            @Override
            public void run() {
            	client.handleMenu(new AddServerMenu(currentMenu));
            }
        });
        
        items.add(new ItemMenu("global.back") {
        	@Override
			public void run() {
        		client.handleMenu(previousMenu);
        	}
        });
        setMenu(items);
    }
    
    
}