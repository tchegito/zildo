/**
 * Legend of Zildo
 * Copyright (C) 2006-2011 Evariste Boussaton
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

import zildo.client.ClientEngineZildo;
import zildo.fwk.file.EasyBuffering;
import zildo.fwk.file.EasyReadingFile;
import zildo.fwk.file.EasyWritingFile;
import zildo.fwk.net.ServerInfo;
import zildo.fwk.net.TransferObject;
import zildo.fwk.ui.EditableItemMenu;
import zildo.fwk.ui.InfoMenu;
import zildo.fwk.ui.ItemMenu;
import zildo.fwk.ui.Menu;
import zildo.fwk.ui.UIText;
import zildo.resource.Constantes;

/**
 * A menu providing creation of a new server.
 * 
 * At the moment of adding it to the server file configuration, controls that it's a valid one.
 * 
 * After that, we go back the previous menu, assuming it was a "Join game" menu, reloading the server configuration.
 * @author tchegito
 */
@Deprecated
public class AddServerMenu extends Menu {

    StringBuilder name = new StringBuilder(UIText.getMenuText("m3.defaultName"));
    StringBuilder ip = new StringBuilder("<IP>");
    StringBuilder port = new StringBuilder("<port>");

    public AddServerMenu(Menu p_previousMenu) {
        super("m3.title");

        previousMenu = p_previousMenu;

        ItemMenu[] itms = new ItemMenu[5];
        itms[0] = new EditableItemMenu(name) {
            @Override
            public void run() {
                client.handleMenu(currentMenu);
            }
        };
        itms[1] = new EditableItemMenu(ip) {
            @Override
            public void run() {
            	client.handleMenu(currentMenu);
            }
        };
        itms[2] = new EditableItemMenu(port) {
            @Override
            public void run() {
            	client.handleMenu(currentMenu);
            }
        };
        itms[3] = new ItemMenu("m3.add") {
            @Override
            public void run() {
            	addServer(name.toString(), ip.toString(), port.toString());
            }
        };
        itms[4] = new ItemMenu("global.back") {
            @Override
            public void run() {
                ClientEngineZildo.getClientForMenu().handleMenu(previousMenu);
            }
        };

        setMenu(itms);
    }
    
    /**
     * Add a new server, if given parameters are corrects.
     * @param p_name
     * @param p_ip
     * @param p_port
     */
    private void addServer(String p_name, String p_ip, String p_port) {
    	// 1: Controls that everything is correct
    	int error=2;
    	try {
    		if (p_name.equals(UIText.getMenuText("m3.defaultName"))) {
    			throw new Exception();
    		}
    		error=0;
    		int intPort=Integer.valueOf(p_port);
    		error=1;
    		new TransferObject(p_ip, intPort);
    		
    		// Everything's ok if we got here
    		saveServerInfos(new ServerInfo(p_name, p_ip, intPort));
    		
    		// And go back to the menu
    		client.handleMenu(new JoinGameMenu(loadServersInfos(), previousMenu.previousMenu));
    		
    	} catch (Exception e) {
    		String message;
    		switch (error) {
    		case 0:
    			message="m3.error.port";
    			break;
    		case 2:
    			message="m3.error.name";
    			break;
    		default:
    		case 1:
    			message="m3.error.unreachable";
    			break;
    		}
    		
    		message=UIText.getMenuText(message);
    		client.handleMenu(new InfoMenu("Impossible. "+message, currentMenu));
    	}
    }
    
    /**
     * Add given server to the server file {@link Constantes#SERVER_FILE}
     * @param p_serverInfo
     */
    private void saveServerInfos(ServerInfo p_serverInfo) {
    	// Read the file
    	List<ServerInfo> infos=loadServersInfos();
    	// Replace server with same name, if needed.
    	int idx=infos.indexOf(p_serverInfo);
    	if (idx != -1) {
    		infos.remove(idx);
    	}
    	infos.add(p_serverInfo);
    	// And save the file
    	EasyBuffering buffer=new EasyBuffering();
    	for (ServerInfo info : infos) {
    		buffer.put(info.name);
    		buffer.put(info.ip);
    		buffer.put(info.port);
    	}
    	EasyWritingFile file=new EasyWritingFile(buffer);
    	file.saveFile(Constantes.SERVER_FILE);
    }
    
    /**
     * Read the servers configuration file to return a list of {@link ServerInfo}.<br/>
     * In any error case, return an empty list.
     * @return List<ServerInfo>
     */
    static public List<ServerInfo> loadServersInfos() {
		List<ServerInfo> infos=new ArrayList<ServerInfo>();
    	try {
        	EasyReadingFile file=new EasyReadingFile(Constantes.SERVER_FILE);
        	while (!file.eof()) {
        		String name=file.readString();
        		String ip=file.readString();
        		int port=file.readInt();
        		infos.add(new ServerInfo(name, ip, port));
        	}
    		return infos;
    	} catch (Exception e) {
    		return infos;
    	}
    }
}
