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
