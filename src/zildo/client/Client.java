package zildo.client;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;

import zildo.Zildo;
import zildo.client.gui.menu.ItemMenu;
import zildo.client.gui.menu.Menu;
import zildo.fwk.ZUtils;
import zildo.fwk.net.InternetClient;
import zildo.fwk.net.NetClient;
import zildo.fwk.net.TransferObject;
import zildo.fwk.opengl.OpenGLZildo;
import zildo.server.EngineZildo;

/**
 * Client job:
 * -----------
 * -Read user movement
 * -Display the scene
 * 
 * @author tchegito
 *
 */
public class Client {

	ClientEngineZildo clientEngineZildo;
	OpenGLZildo glGestion;
	boolean awt;
	boolean done=false;
	boolean serverLeft=false;
	boolean connected=false;	// TRUE so as a connection with a server is established
	boolean lan=false;
	Menu currentMenu;
	NetClient netClient;
	
	ItemMenu action=null;
	
	public enum ClientType {
		SERVER_AND_CLIENT, CLIENT, ZEDITOR;
	}

	/**
	 * Create client with given parameter.
	 */
	public Client(boolean p_awt) {
		awt=p_awt;
		initializeDisplay();

	}

	void initializeDisplay() {
		if (awt) {
			glGestion=new OpenGLZildo();
		} else {
			glGestion=new OpenGLZildo(Zildo.fullScreen);
		}
		clientEngineZildo=new ClientEngineZildo(glGestion, awt, this);
		glGestion.setClientEngineZildo(clientEngineZildo);
		clientEngineZildo.setOpenGLGestion(glGestion);
	}
	
	/**
	 * Set up network things.
	 * @param p_type ClientType
	 * @param p_serverIp 
	 * @param p_serverPort
	 */
	public void setUpNetwork(ClientType p_type, String p_serverIp, int p_serverPort) {
		lan=p_serverIp == null;
		if (ClientType.CLIENT == p_type) {
			if (lan) {
				netClient=new NetClient(this);
			} else {
				netClient=new InternetClient(this, p_serverIp, p_serverPort);
			}
			connected=false;
		} else {
			connected=true;	// We don't need to manage connection
		}
	}
	
	public void readKeyboard() {
		Keyboard.poll();
	}
	
	public void initGL() {
		try {
			glGestion.init();
		} catch (LWJGLException e) {
			throw new RuntimeException("Problem initializing ZildoRenderer !");
		}
		clientEngineZildo.initializeClient(true);
        ClientEngineZildo.mapDisplay.setCurrentMap(EngineZildo.mapManagement.getCurrentMap());
    }
	
	public boolean render()
	{
		if (!awt) {
	        // Read keyboard
	        Keyboard.poll();
	
	        done=glGestion.mainloop();
		}
		
        // Display scene
		glGestion.render(connected);
		
        return done;
	}
	
	public void serverLeft() {
		serverLeft=true;
	}
	
	public void stop() {
		done=true;
	}
	
	/**
	 * Two important things:
	 * -Do the network job
	 * -Render scene
	 */
	public void run() {
		
        while (!done && !serverLeft) {
			// Deals with network
            if (netClient != null) {
                netClient.run();
                connected = netClient.isConnected();
                if (connected) {
                    netClient.sendKeyboard();
                }
            }
       		render();

       		if (action != null) {
       			action.run();
       			action=null;
       		}
        	ZUtils.sleep(5);
        }
	}
	
	public void handleMenu(Menu p_menu) {
		currentMenu=p_menu;
	}
	
	public void cleanUp() {
        if (netClient != null) {
        	netClient.close();
        }
        if (glGestion != null) {
        	glGestion.cleanUp();
        	glGestion=null;
        }
	}

	public ClientEngineZildo getEngineZildo() {
		return clientEngineZildo;
	}
	
	public TransferObject getNetClient() {
		return netClient;
	}
	
	public boolean isLAN() {
		return lan;
	}
}
