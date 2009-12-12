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
import zildo.prefs.Constantes;

/**
 * A menu providing creation of a new server.
 * 
 * At the moment of adding it to the server file configuration, controls that it's a valid one.
 * 
 * After that, we go back the previous menu, assuming it was a "Join game" menu, reloading the server configuration.
 * @author tchegito
 */
public class AddServerMenu extends Menu {

    StringBuilder name = new StringBuilder("<name>");
    StringBuilder ip = new StringBuilder("<IP>");
    StringBuilder port = new StringBuilder("<port>");

    Menu currentMenu = this;

    public AddServerMenu(Menu p_previousMenu) {
        super("Enter the server IP and port");

        previousMenu = p_previousMenu;

        ItemMenu[] items = new ItemMenu[5];
        items[0] = new EditableItemMenu(name) {
            @Override
            public void run() {
                ClientEngineZildo.getClientForMenu().handleMenu(currentMenu);
            }
        };
        items[1] = new EditableItemMenu(ip) {
            @Override
            public void run() {
                ClientEngineZildo.getClientForMenu().handleMenu(currentMenu);
            }
        };
        items[2] = new EditableItemMenu(port) {
            @Override
            public void run() {
                ClientEngineZildo.getClientForMenu().handleMenu(currentMenu);
            }
        };
        items[3] = new ItemMenu("Add this server") {
            @Override
            public void run() {
            	addServer(name.toString(), ip.toString(), port.toString());
            }
        };
        items[4] = new ItemMenu("Back") {
            @Override
            public void run() {
                ClientEngineZildo.getClientForMenu().handleMenu(previousMenu);
            }
        };

        setMenu(items);
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
    		if (p_name.equals("<name>")) {
    			throw new Exception();
    		}
    		error=0;
    		int port=Integer.valueOf(p_port);
    		error=1;
    		new TransferObject(p_ip, port);
    		
    		// Everything's ok if we got here
    		saveServerInfos(new ServerInfo(p_name, p_ip, port));
    		
    		// And go back to the menu
        	ClientEngineZildo.getClientForMenu().handleMenu(new JoinGameMenu(loadServersInfos(), previousMenu.previousMenu));
    		
    	} catch (Exception e) {
    		String message;
    		switch (error) {
    		case 0:
    			message="Port should be a number.";
    			break;
    		case 2:
    			message="Enter a valid name";
    			break;
    		default:
    		case 1:
    			message="Unreachable !";
    			break;
    		}
    		
        	ClientEngineZildo.getClientForMenu().handleMenu(new InfoMenu("Impossible. "+message, currentMenu));
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
