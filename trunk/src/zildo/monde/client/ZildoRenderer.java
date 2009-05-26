package zildo.monde.client;

import zildo.monde.Game;
import zildo.network.Client;
import zildo.network.Server;

public class ZildoRenderer implements IRenderable {

	Server server;
	Client client;
	boolean initialized=false;
	Exception e=null;
	
	public ZildoRenderer() {
		Game game=new Game("polaky", true);
		server=new Server(game);
		client=new Client(server.getEngineZildo(), true);
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

	@Override
	public void setInitialized(boolean initialized) {
		this.initialized=initialized;
	}

}
