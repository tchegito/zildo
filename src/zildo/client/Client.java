package zildo.client;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;

import zildo.Zildo;
import zildo.fwk.ZUtils;
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
	boolean connected=false;	// TRUE so as a connection with a server is established
	
	NetClient netClient;
	
	/**
	 * Client and server are on two differents PC.
	 */
	public Client() {
		// On crée les objets OpenGL
		glGestion=new OpenGLZildo(Zildo.fullScreen);
		clientEngineZildo=new ClientEngineZildo(glGestion, false);
		glGestion.setEngineZildo(clientEngineZildo);
		
		awt=false;
		connected=false;
		
		netClient=new NetClient(this);
		
	}
	
	/**
	 * Same PC for client and server.
	 * @param p_engine
	 */
	public Client(boolean p_awt) {
		awt=p_awt;
		if (p_awt) {
			glGestion=new OpenGLZildo();
		} else {
			glGestion=new OpenGLZildo(Zildo.fullScreen);
		}
		clientEngineZildo=new ClientEngineZildo(glGestion, p_awt);
		glGestion.setEngineZildo(clientEngineZildo);
		clientEngineZildo.setOpenGLGestion(glGestion);

		connected=true;	// We don't need to manage connection
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
	
	public void updateEngine() {
		
	}
	
	/**
	 * Two important things:
	 * -Do the network job
	 * -Render scene
	 */
	public void run() {
		
        while (!done) {
			// Deals with network
            if (netClient != null) {
                netClient.run();
                connected = netClient.isConnected();
                if (connected) {
                    netClient.sendKeyboard();
                }
            }
        	if (true || connected) {
        		render();
        	}

        	ZUtils.sleep(5);
        }

        if (netClient != null) {
        	netClient.close();
        }
        
        cleanUp();
	}

	public void cleanUp() {
		glGestion.cleanUp();				
	}

	public ClientEngineZildo getEngineZildo() {
		return clientEngineZildo;
	}
	
	public TransferObject getNetClient() {
		return netClient;
	}
}
