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
		server=new Server(game);
		client=new Client(true);
	}
	
	@Override
	public void initRenderer() {
    	client.initGL();
	}

	@Override
	public void initScene() {
	}

	@Override
	public boolean isInitialized() {
		return initialized;
	}

	@Override
	public void preRenderScene() {
		// TODO Auto-generated method stub

	}

	@Override
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
	
	@Override
	public void setInitialized(boolean initialized) {
		this.initialized=initialized;
	}
	
    public EngineZildo getEngineZildo() {
        return server.getEngineZildo();
    }
}
