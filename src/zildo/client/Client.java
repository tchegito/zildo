/**
 * Legend of Zildo
 * Copyright (C) 2006-2012 Evariste Boussaton
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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import zildo.Zildo;
import zildo.client.gui.menu.InGameMenu;
import zildo.fwk.ZUtils;
import zildo.fwk.input.KeyboardHandler;
import zildo.fwk.input.KeyboardInstant;
import zildo.fwk.net.NetClient;
import zildo.fwk.net.TransferObject;
import zildo.fwk.net.www.InternetClient;
import zildo.fwk.opengl.OpenGLSound;
import zildo.fwk.opengl.OpenGLZildo;
import zildo.fwk.ui.ItemMenu;
import zildo.fwk.ui.Menu;
import zildo.server.EngineZildo;
import zildo.server.state.PlayerState;

/**
 * <h1>Client job:</h1><br/>
 * <ul>
 * <li>Read user movement</li>
 * <li>Display the scene</li>
 * <li>Store info about connected clients (to display score)</li>
 * </ul>
 * 
 * @author tchegito
 * 
 */
public class Client {

	ClientEngineZildo clientEngineZildo;
	OpenGLZildo glGestion;
	static boolean awt;
	boolean done = false;
	boolean serverLeft = false;
	boolean connected = false; // TRUE so as a connection with a server is
								// established
	boolean lan = false;
	Menu currentMenu;
	NetClient netClient;
	boolean multiplayer;
	boolean music = Zildo.soundEnabled;

	ItemMenu action = null;

	Map<Integer, PlayerState> states; // All player in the game (reduced info to
										// display scores)

	KeyboardInstant kbInstant = new KeyboardInstant();

	InGameMenu ingameMenu;

	public enum ClientType {
		SERVER_AND_CLIENT, CLIENT, ZEDITOR;
	}

	/**
	 * Create client with given parameter.
	 */
	public Client(boolean p_awt) {
		awt = p_awt;
		states = new HashMap<Integer, PlayerState>();
		// Video
		initializeDisplay();
	}

	void initializeDisplay() {
		if (awt) {
			glGestion = new OpenGLZildo();
		} else {
			glGestion = new OpenGLZildo(Zildo.fullScreen);
		}
		clientEngineZildo = new ClientEngineZildo(glGestion, awt, this);
		glGestion.setClientEngineZildo(clientEngineZildo);
		clientEngineZildo.setOpenGLGestion(glGestion);
	}

	/**
	 * Set up network things.
	 * 
	 * @param p_type
	 *            ClientType
	 * @param p_serverIp
	 * @param p_serverPort
	 */
	public void setUpNetwork(ClientType p_type, String p_serverIp, int p_serverPort, boolean p_multiplayer) {
		lan = p_serverIp == null;
		if (ClientType.CLIENT == p_type) {
			if (lan) {
				netClient = new NetClient(this);
			} else {
				netClient = new InternetClient(this, p_serverIp, p_serverPort);
			}
			connected = false;
		} else {
			connected = true; // We don't need to manage connection
		}
		multiplayer = p_multiplayer;
	}

	/**
	 * Initialize OpenGL in the ZEditor frame.
	 */
	public void initGL() {
		glGestion.init();
		clientEngineZildo.initializeClient(true);
		// Set up the map
		ClientEngineZildo.mapDisplay.setCurrentMap(EngineZildo.mapManagement.getCurrentMap());
		// And the sprites
		EngineZildo.spriteManagement.updateSprites(false);
		ClientEngineZildo.spriteDisplay.setEntities(EngineZildo.spriteManagement.getSpriteEntities(null));

	}

	long time;

	public boolean render() {
		if (!awt) {
			// Read keyboard
			KeyboardHandler.poll();

			// Music
			if (music) {
				long currentTime = ZUtils.getTime();
				OpenGLSound.pollMusic((int) (currentTime - time));
				time = currentTime;
			}
			done = glGestion.mainloop();

			if (KeyboardHandler.isKeyDown(KeyboardHandler.KEY_ESCAPE) && !ClientEngineZildo.filterCommand.isFading()) {
				// Escape is pressed and no fade is running
				if (connected) {
					if (ingameMenu == null) {
						ingameMenu = new InGameMenu();
					}
					handleMenu(ingameMenu);
				} else if (!isIngameMenu()) {
					done = true;
				}
			}

		}

		// Display scene
		glGestion.render(connected);

		if (action != null && !action.isLaunched()) {
			action.setLaunched(true);
			action.run();
			action = null;
		}
		return done;
	}

	public void serverLeft() {
		serverLeft = true;
	}

	public void stop() {
		done = true;
	}

	/**
	 * Two important things: -Do the network job -Render scene
	 */
	public void run() {

		while (!done && !serverLeft) {
			// Deals with network
			if (netClient != null) {
				netClient.run();
				connected = netClient.isConnected() && !isIngameMenu();

				if (connected) { // Read keyboard if player is in game
					kbInstant.update();

					// Send keyboard (a non-sending during certain time means
					// deconnection)
					netClient.sendKeyboard();
				}

			}
			render();

			ZUtils.sleep(5);
		}
	}

	public void handleMenu(Menu p_menu) {
		currentMenu = p_menu;
		if (p_menu == null) {
			connected = true;
		} else {
			currentMenu.refresh();
			connected = false;
		}
	}

	public void cleanUp() {
		if (netClient != null) {
			netClient.close();
		}
		if (glGestion != null) {
			glGestion.cleanUp();
			glGestion = null;
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

	public Collection<PlayerState> getPlayerStates() {
		return states.values();
	}

	/**
	 * Clients store the provided client in his registry.
	 * 
	 * @param p_playerName
	 */
	public void registerClient(PlayerState p_state) {
		states.put(p_state.zildoId, p_state);
	}

	/**
	 * Client is gone, so unregister him from the map.
	 * 
	 * @param p_zildoId
	 */
	public void unregisterClient(int p_zildoId) {
		states.remove(p_zildoId);
	}

	public KeyboardInstant getKbInstant() {
		return kbInstant;
	}

	public void setKbInstant(KeyboardInstant kbInstant) {
		this.kbInstant = kbInstant;
	}

	public boolean isMultiplayer() {
		return multiplayer;
	}

	public static boolean isZEditor() {
		return awt;
	}

	public boolean isIngameMenu() {
		return currentMenu != null && ingameMenu != null;
	}

	public boolean isMusic() {
		return music;
	}

	public void setMusic(boolean music) {
		this.music = music;
	}
}
