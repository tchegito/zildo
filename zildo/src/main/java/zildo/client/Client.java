/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
 * 
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zildo.Zildo;
import zildo.client.gui.MenuTransitionProgress;
import zildo.client.gui.GUIDisplay.DialogMode;
import zildo.client.gui.menu.CompassMenu;
import zildo.client.gui.menu.InGameMenu;
import zildo.client.stage.GameStage;
import zildo.client.stage.MenuStage;
import zildo.client.stage.SinglePlayer;
import zildo.fwk.ZUtils;
import zildo.fwk.gfx.filter.CloudFilter;
import zildo.fwk.gfx.filter.EarthQuakeFilter;
import zildo.fwk.gfx.filter.FitToScreenFilter;
import zildo.fwk.gfx.filter.LightningFilter;
import zildo.fwk.gfx.filter.RedFilter;
import zildo.fwk.input.KeyboardHandler;
import zildo.fwk.input.KeyboardHandler.Keys;
import zildo.fwk.input.KeyboardInstant;
import zildo.fwk.net.NetClient;
import zildo.fwk.net.TransferObject;
import zildo.fwk.net.www.InternetClient;
import zildo.fwk.opengl.OpenGLGestion;
import zildo.fwk.ui.DefaultMenuListener;
import zildo.fwk.ui.ItemMenu;
import zildo.fwk.ui.Menu;
import zildo.fwk.ui.MenuListener;
import zildo.monde.util.Point;
import zildo.monde.util.Vector3f;
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
	OpenGLGestion glGestion;
	static boolean selfInitialization;
	boolean done = false;
	boolean serverLeft = false;
	boolean connected = false; // TRUE so as a connection with a server is
								// established
	boolean lan = false;
	NetClient netClient;
	boolean multiplayer;
	boolean music = Zildo.soundEnabled;
	boolean fullscreen = Zildo.fullScreen;
	// touchpad parameters
	boolean leftHanded;	// For touch screen
	boolean movingCross; // For touch screen
	boolean androidUI = true;	// For touch screen (hide UI after a long time idle)
	Point crossCenter;	// When player first touch the right zone
	Point draggingTouch;	// Player's dragging touch
	
	ItemMenu action = null;

	Map<Integer, PlayerState> states; // All player in the game (reduced info to
										// display scores)

	KeyboardInstant kbInstant = new KeyboardInstant();

	final InGameMenu ingameMenu;

	KeyboardHandler kbHandler = Zildo.pdPlugin.kbHandler;

	MenuListener menuListener;
	MenuTransitionProgress menuTransition = new MenuTransitionProgress(this);
	
	List<GameStage> stages;
	List<GameStage> ongoingStages;

	public enum ClientType {
		SERVER_AND_CLIENT, CLIENT, ZEDITOR;
	}
	
	ItemMenu END_MENU_ITEM = new ItemMenu() {
		@Override
		public void run() {
			handleMenu(null);
		}
	};	
	
	/**
	 * Create client with given parameter.
	 * @param p_delegateInitialization TRUE=initialization will be done later / FALSE=complete initialization now
	 */
	public Client(boolean p_delegateInitialization) {
		selfInitialization = !p_delegateInitialization;
		states = new HashMap<Integer, PlayerState>();
		// Video
		initializeDisplay();
		
		stages = new ArrayList<GameStage>();
		ongoingStages = new ArrayList<GameStage>();
		menuListener = new DefaultMenuListener();
		
		ingameMenu = new InGameMenu();
		
		connected = true;
	}

	void initializeDisplay() {
        clientEngineZildo = new ClientEngineZildo(glGestion, selfInitialization, this);
        //glGestion.setClientEngineZildo(clientEngineZildo);
        glGestion = Zildo.pdPlugin.openGLGestion;
        if (glGestion != null) {
            glGestion.setClientEngineZildo(clientEngineZildo);
        }
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
        clientEngineZildo.initializeClient(true);
        glGestion = Zildo.pdPlugin.openGLGestion;
        glGestion.setClientEngineZildo(clientEngineZildo);
        clientEngineZildo.setOpenGLGestion(glGestion);
        // Set up the map
        ClientEngineZildo.mapDisplay.setCurrentMap(EngineZildo.mapManagement.getCurrentMap());
        // And the sprites
        EngineZildo.spriteManagement.updateSprites(false);
        ClientEngineZildo.spriteDisplay.setEntities(EngineZildo.spriteManagement.getSpriteEntities(null));

    }

	long time;

	public boolean mainLoop() {
		// Render current stage and remove those which are finished
		//long t1 = ZUtils.getTime();
		List<GameStage> toRemove = new ArrayList<GameStage>();
		for (GameStage stage : stages) {
			if (stage.isDone()) {
				toRemove.add(stage);
			} else {
				stage.updateGame();
			}
		}
		// Is there a new stage being requested during the updating ?
		if (ongoingStages.size() > 0) {	
			for (GameStage stage : ongoingStages) {
				// Issue 120: double security to avoid this kind of hazardous case
				if (stage instanceof SinglePlayer && !stages.isEmpty())	{
					for (GameStage ss : stages) {
						if (ss instanceof SinglePlayer && !ss.isDone())
							throw new RuntimeException("Impossible to have 2 unfinished singleplayer stages !");
					}
					stage.launchGame();
				}
				stages.add(stage);
				
 			}
			ongoingStages.clear();
		}
		// Is there still  menu stage ?
		boolean stillMenu = false;
		for (GameStage stage : stages) {
			if (stage instanceof MenuStage) {
				stillMenu = true;
				break;
			}
		}
		// Display walls around the screen during ingame menu
		ClientEngineZildo.filterCommand.active(FitToScreenFilter.class, stillMenu, null);
		if (!stillMenu) {
			menuTransition.forceMenu(null);
		}
		
		// Is there some deletion asked ?
		if (!toRemove.isEmpty()) {
			for (GameStage stage : toRemove) {
				stages.remove(stage);
			}
		}
		
		//long t2 = ZUtils.getTime();
		
		render();
		
		//long t3 = ZUtils.getTime();
		
		//System.out.println("time update : "+(t2-t1)+"ms / time render : "+(t3-t2)+"ms / number of stage : "+stages.size());
		if (action != null) {
			action.activate();
			if (action != null && !action.isLaunched()) {
				action.setLaunched(true);
				action.run();
				
				action = null;
			}
		}
		
		return done;
	}
	
	public void render() {
        if (kbHandler == null) {
            kbHandler = Zildo.pdPlugin.kbHandler;
        }

        if (!ClientEngineZildo.editing) {
			// Read keyboard
			Zildo.pdPlugin.kbHandler.poll();

			// Music
			if (isMusic()) {
				long currentTime = ZUtils.getTime();
				ClientEngineZildo.soundEngine.pollMusic((int) (currentTime - time));
				time = currentTime;
			}
			done |= glGestion.mainloop();

			handleKeys();

		}

        menuTransition.mainLoop();
        
		// Display scene
        connected = !isIngameMenu();
		glGestion.render(connected);
	}

	/**
	 * Handle system keys : ESCAPE for computer, BACK/MENU for touchscreen devices
	 */
	private void handleKeys() {
		if (menuTransition.isReadyToInteract()) {
			Menu currentMenu = menuTransition.getCurrentMenu();
			boolean compassMenu = currentMenu instanceof CompassMenu;
			DialogMode dm = ClientEngineZildo.guiDisplay.getToDisplay_dialogMode();
			if (kbHandler.isKeyPressed(Keys.ESCAPE)) {
				// Escape is pressed and no fade is running
				if (connected) {
					if (!EngineZildo.spriteManagement.isBlockedNonHero() && dm != DialogMode.INFO) {
						handleMenu(ingameMenu);
					}
				} else if (compassMenu) {
					askForItemMenu(END_MENU_ITEM);
				} else if (currentMenu != ingameMenu) {
					done = true;
				} else {
					askForItemMenu(ingameMenu, "m7.continue");
				}
			}
			
			if (kbHandler.isKeyPressed(Keys.TOUCH_MENU)) {
				// Don't allow in game menu when player is in texter (it would make too much overlapping text on screen)
				if (connected && !isIngameMenu() && dm != DialogMode.TEXTER && dm != DialogMode.INFO) {
					handleMenu(ingameMenu);
				}
			}
			
			if (kbHandler.isKeyPressed(Keys.TOUCH_BACK)) {
				if (!connected) {
					if (currentMenu == ingameMenu) {
						askForItemMenu(ingameMenu, "m7.quit");
					} else if (compassMenu) {
						askForItemMenu(END_MENU_ITEM);
					} else if (currentMenu != null) {
						if ("m7.quitConfirm".equals(currentMenu.getKey())) {
							askForItemMenu(currentMenu, "global.yes");
						} else if ("m1.title".equals(currentMenu.getKey())) {
							done = true;
						}
					} else {
						askForItemMenu(ingameMenu, "m7.quit");
					}
				} else if (dm != DialogMode.INFO && dm != DialogMode.CREDITS) {
					handleMenu(ingameMenu);
					askForItemMenu(ingameMenu, "m7.quit");
				}
			}
			if (kbHandler.isKeyPressed(Keys.COMPASS) && 
					connected &&	// Inside the game, not in main menu
					ClientEngineZildo.guiDisplay.isToDisplay_generalGui() && 
					dm != DialogMode.TEXTER &&
					dm != DialogMode.INFO &&
					// Forbid access when dialoguing => too much characters on screen (issue 109)
					ClientEngineZildo.guiDisplay.isToDisplay_compassItem()
					) {
				if (currentMenu instanceof CompassMenu) {
					askForItemMenu(END_MENU_ITEM);
				} else {
					handleMenu(new CompassMenu());
				}
			}
		}
	}

	/**
	 * Simulate an item selection. Assume that a menu is displayed.
	 * @param item
	 */
	private void askForItemMenu(Menu menu, String itemKey) {
		ItemMenu item = menu.getItemNamed(itemKey);
		askForItemMenu(item);
	}
	private void askForItemMenu(ItemMenu anonymItem) {
		for (GameStage stage : stages) {
			if (stage instanceof MenuStage) {
				((MenuStage)stage).askForItemMenu(anonymItem);
			}
		}
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
			mainLoop();
			
			ZUtils.sleep(5);
		}
	}

	/**
	 * A new menu is asked. So we take time to remove the current one, during when a fade out is processed. Then the new one comes with a fade in
	 * @param p_menu
	 */
	public void handleMenu(Menu p_menu) {
		menuTransition.askForMenu(p_menu);
	}

	public MenuTransitionProgress getMenuTransition() {
		return menuTransition;
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

	public boolean isIngameMenu() {
		return (menuTransition.getCurrentMenu() != null && ingameMenu != null) ||
				(isMenuDisplayed());
	}

	public boolean isMusic() {
		return music && Zildo.soundEnabled;
	}
	
	public boolean isFullscreen() {
		return fullscreen;
	}

	public void setFullscreen(boolean value) {
		fullscreen = value;
	}
	public void setMusic(boolean music) {
		this.music = music;
		if (!music) {
			ClientEngineZildo.soundPlay.disableMusic();
		} else {
			ClientEngineZildo.soundPlay.enableMusic();
		}
	}
	
	public void setLeftHanded(boolean lh) {
		leftHanded = lh;
	}
	
	public boolean isLeftHanded() {
		return leftHanded;
	}
	
	public boolean isMovingCross() {
		return movingCross;
	}

	public void showAndroidUI(boolean show) {
		androidUI = show;
	}
	
	public boolean isDisplayedAndroidUI() {
		return androidUI;
	}
	
	public void setMovingCross(boolean movingCross) {
		this.movingCross = movingCross;
	}
	
	public Point getCrossCenter() {
		return crossCenter;
	}

	public void setCrossCenter(Point crossCenter) {
		this.crossCenter = crossCenter;
	}

	public Point getDraggingTouch() {
		return draggingTouch;
	}

	public void setDraggingTouch(Point draggingTouch) {
		this.draggingTouch = draggingTouch;
	}
	
	public void setOpenGLGestion(OpenGLGestion glGestion) {
		this.glGestion = glGestion;
	}
	
	public Menu getCurrentMenu() {
		return menuTransition.getCurrentMenu();
	}
	
	public boolean isReady() {
		return glGestion != null;
	}
	
	public void setAction(ItemMenu action) {
		if (isMenuDisplayed()) {	// Issue 120: Disallow action if no menu is displayed
			this.action = action;
		}
	}
	
	private boolean isMenuDisplayed() {
		if (menuTransition.getCurrentMenu() != null) {
			return true;
		}
		// Check if a menu is still active, iterating over stages
		// Because with Android, we use threads and this introduces different behavior than PC
		return isStageMenuDisplayed();
	}
	
	// TODO: see if this method and the previous could be merged in one
	private boolean isStageMenuDisplayed() {
		for (GameStage stage : stages) {
			if (stage instanceof MenuStage && !stage.isDone()) {
				return true;
			}
		}
		return false;
	}
	
	public void setMenuListener(MenuListener p_menuListener) {
		menuListener = p_menuListener;
	}
	
	public MenuListener getMenuListener() {
		return menuListener;
	}
	
	public List<GameStage> getCurrentStages() {
		return stages;
	}
	
	public void askStage(GameStage stage) {
		if (isStageMenuDisplayed()) {
			menuTransition.askForStage(stage);
		} else {
			ongoingStages.add(stage);
		}
	}
	
	/**
	 * Player quit game and return to main menu.
	 */
	public void quitGame() {
		for (GameStage stage : stages) {
			stage.endGame();
		}
		
		ClientEngineZildo.guiDisplay.clearGui();
		ClientEngineZildo.spriteDisplay.clearSprites();
		ClientEngineZildo.soundPlay.stopLooping();
		ClientEngineZildo.ortho.setFilteredColor(new Vector3f(1, 1, 1));
		ClientEngineZildo.ortho.setAmbientColor(new Vector3f(1, 1, 1));
		ClientEngineZildo.filterCommand.restoreFilters();
		ClientEngineZildo.filterCommand.active(RedFilter.class, false, null);
		ClientEngineZildo.filterCommand.active(LightningFilter.class, false, null);
		ClientEngineZildo.filterCommand.active(CloudFilter.class, false, null);
		ClientEngineZildo.filterCommand.active(EarthQuakeFilter.class, false, null);
		ClientEngineZildo.mapDisplay.reset();

		connected = false;
	}
	
	public boolean isDone() {
		return done;
	}
}
