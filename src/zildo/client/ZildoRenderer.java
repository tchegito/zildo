package zildo.client;

import zildo.monde.Game;
import zildo.server.EngineZildo;
import zildo.server.Server;

public class ZildoRenderer implements IRenderable {

	Server server;
	Client client;
	boolean initialized=false;
	Exception e=null;
	
	public ZildoRenderer(String mapName) {
		Game game=new Game(mapName, true);
		server=new Server(game, true);
		client=new Client(true);
	}
	
	public void initRenderer() {
    	client.initGL();
	}

	public void initScene() {
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void preRenderScene() {
		// TODO Auto-generated method stub

	}

	public void renderScene() {
		if (e == null) {
			try {
				client.render();
			} catch (Exception e) {
				this.e=e;
				e.printStackTrace();
			}
		}
	}

	public void cleanUp() {
		client.cleanUp();
	}
	
	public void setInitialized(boolean initialized) {
		this.initialized=initialized;
	}
	
    public EngineZildo getEngineZildo() {
        return server.getEngineZildo();
    }
}
