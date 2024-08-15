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


import zildo.Zildo;
import zildo.client.gui.GUIDisplay;
import zildo.client.gui.ScreenConstant;
import zildo.client.sound.Ambient;
import zildo.client.sound.SoundPlay;
import zildo.fwk.FilterCommand;
import zildo.fwk.gfx.Ortho;
import zildo.fwk.gfx.PixelShaders;
import zildo.fwk.gfx.engine.SpriteEngine;
import zildo.fwk.gfx.engine.TileEngine;
import zildo.fwk.gfx.filter.BilinearFilter;
import zildo.fwk.gfx.filter.CircleFilter;
import zildo.fwk.gfx.filter.CloudFilter;
import zildo.fwk.input.KeyboardInstant;
import zildo.fwk.opengl.OpenGLGestion;
import zildo.fwk.opengl.SoundEngine;
import zildo.monde.collision.Collision;
import zildo.monde.collision.Rectangle;
import zildo.monde.map.Area;
import zildo.monde.map.Case;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.Perso.PersoInfo;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.util.Point;
import zildo.monde.util.Vector3f;
import zildo.monde.util.Vector4f;
import zildo.resource.KeysConfiguration;
import zildo.server.EngineZildo;

public class ClientEngineZildo {

	// Link to directX object
	public static OpenGLGestion openGLGestion;
	public static Ortho ortho;
	public static FilterCommand filterCommand;

	public static SpriteDisplay spriteDisplay;
	public static MapDisplay mapDisplay;
	public static SoundEngine soundEngine;
	
	public static ScreenConstant screenConstant;
	public static GUIDisplay guiDisplay;

	public static SpriteEngine spriteEngine;
	public static TileEngine tileEngine;
	public static SoundPlay soundPlay;
	public static PixelShaders pixelShaders;

	public static Client client;

	private static ClientEvent askedEvent;
	

	
	public static boolean editing;
	
	// Time left to unblock player's moves
	private final int waitingScene;

	public static Ambient ambient=new Ambient();

	/**
	 * Should be called after {@link #initializeServer}
	 * @param p_awt TRUE=ZEditor / FALSE=game
	 */
	public void initializeClient(boolean p_awt) {

        editing = p_awt;
       
        Zildo.pdPlugin.init(editing);

        openGLGestion = Zildo.pdPlugin.openGLGestion;
        openGLGestion.init();
       
        screenConstant = new ScreenConstant(Zildo.viewPortX, Zildo.viewPortY);
        
        filterCommand = new FilterCommand();
		if (!p_awt) { // No sound in ZEditor
			soundEngine = Zildo.pdPlugin.soundEngine;
			soundPlay = new SoundPlay(soundEngine);
		}

		ortho = Zildo.pdPlugin.ortho;

		if (!p_awt) {
			Zildo.pdPlugin.initFilters();
			filterCommand.addDefaultFilters();
		}

		pixelShaders = Zildo.pdPlugin.pixelShaders;
		if (pixelShaders.canDoPixelShader()) {
			pixelShaders.preparePixelShader();
		}

		spriteEngine = Zildo.pdPlugin.spriteEngine;
		tileEngine = Zildo.pdPlugin.tileEngine;

		spriteDisplay = new SpriteDisplay(spriteEngine);
		mapDisplay = new MapDisplay(null);
		guiDisplay = new GUIDisplay();
		spriteEngine.init(spriteDisplay);

		// GUI
		guiDisplay.setToDisplay_generalGui(false);
		
		ortho.setOrthographicProjection(false);

	}

	/**
	 * Client intialization, with real network
	 * 
	 * @param p_openGLGestion
	 * @param p_selfInitialization TRUE=initialize at construction / FALSE=will be initialized later
	 * @param p_client
	 */
	public ClientEngineZildo(OpenGLGestion p_openGLGestion, boolean p_selfInitialization,
			Client p_client) {
		// Lien avec OpenGL
		ClientEngineZildo.openGLGestion = p_openGLGestion;

		if (p_selfInitialization) {
			initializeClient(false);
		}

		client = p_client;
		waitingScene = 0;
	}

	public void renderFrame(boolean p_editor) {
		if (waitingScene != 0) {
			return;
		}

		//long t1 = ZUtils.getTime();
		
		// Focus camera on player
		if (!p_editor && client.connected) {
			if (mapDisplay.getCurrentMap() != null) {
				mapDisplay.centerCamera();
			}

			// Is Zildo talking with somebody ?
			guiDisplay.manageDialog();
		}

		//long t2 = ZUtils.getTime();

		// Tile engine
		Area[] maps=new Area[]{ mapDisplay.getCurrentMap(), mapDisplay.getPreviousMap()};
		tileEngine.updateTiles(mapDisplay.getCamera(), maps, mapDisplay.getCompteur_animation());
		
		//long t3 = ZUtils.getTime();

		guiDisplay.prepareDraw(!client.connected);
		
		spriteDisplay.updateSpritesClient(mapDisplay.getCamera());

		//long t4 = ZUtils.getTime();

		ClientEngineZildo.openGLGestion.beginScene();

		// // DISPLAY ////

		spriteEngine.initRendering();
		// Determine min floor for single or double map displaying (in case of map scroll, we have 2 maps)
		int lowestFloor = 1;
		int highestFloor = 1;
		for (Area area : maps) {
			if (area != null) {
				lowestFloor = Math.min(lowestFloor, area.getLowestFloor());
				highestFloor = Math.max(highestFloor, area.getHighestFloor());
			}
		}
		for (int floor = lowestFloor ; floor <= 2; floor++) {
			boolean lastTurn = floor == 2;
			
			// Display BACKGROUND tiles
			if (mapDisplay.foreBackController.isDisplayBackground()) {
			    tileEngine.render(floor, true);
			}
	
			//long t5 = ZUtils.getTime();
	
			// Display BACKGROUND sprites
			if (spriteDisplay.foreBackController.isDisplayBackground()) {
			    spriteEngine.render(floor, true);
			}
			
			//long t6 = ZUtils.getTime();
	
			// Display FOREGROUND tiles
			if (mapDisplay.foreBackController.isDisplayForeground()) {
			    tileEngine.render(floor, false);
			}

			if (!p_editor && lastTurn) {	// Display only once !
				// We have to split GuiDisplay into 2 different methods
				// 1) one for preparing GUISpriteSequences 
				// (to be called before SpriteDisplay#updateSpritesClient)
				// 2) one for drawing with Ortho#box etc
				// (to be called once in this loop after back and fore ground
				guiDisplay.draw();
			}
			// Display FOREGROUND sprites
			if (spriteDisplay.foreBackController.isDisplayForeground()) {
			    spriteEngine.render(floor, false);
			}
		}

		//long t7 = ZUtils.getTime();

		
		//long t8 = ZUtils.getTime();

		if (Zildo.infoDebug && !p_editor) {
			this.debug();
		}

		KeyboardInstant kbInstant = client.getKbInstant();
		if (client.isMultiplayer()) {
			// Does player want to see the scores ? (tab key pressed)
			boolean tabPressed = false;
			if (kbInstant != null) {
				tabPressed = kbInstant.isKeyDown(KeysConfiguration.PLAYERKEY_TAB);
			}
			guiDisplay.setToDisplay_scores(tabPressed);
		}
		
		//long t9 = ZUtils.getTime();

		//System.out.println("t2 = "+(t2-t1)+"ms t3="+(t3-t2)+"ms t4="+(t4-t3)+"ms t5="+(t5-t4)+"ms t6="+(t6-t5)+"ms t7="+(t7-t6)+"ms t8="+(t8-t7)+"ms "+(t9-t8));
		
		openGLGestion.endScene();
	}

	public ClientEvent renderEvent(ClientEvent p_event) {

		ClientEvent retEvent = p_event;

		boolean displayGUI = !p_event.script && !p_event.mapChange;

		if (p_event.wait != 0) {
			p_event.wait--;
		} else {
			switch (p_event.nature) {
				case CHANGINGMAP_ASKED :
					// Changing map : 1/3 we launch the fade out
					retEvent.effect = p_event.effect; //FilterEffect.BLEND;
				case FADE_OUT :
					retEvent.nature = ClientEventNature.FADING_OUT;
					filterCommand.fadeOut(retEvent.effect);
					break;
				case FADING_OUT :
					if (filterCommand.isFadeOver()) {
						retEvent.nature = ClientEventNature.FADEOUT_OVER;
					} else {
						circleOnZildo();
					}
					break;
				case CLEAR:
					// Reset fade, hide Zildo and kill the current map
					filterCommand.fadeEnd();
            		EngineZildo.mapManagement.deleteCurrentMap();
            		EngineZildo.persoManagement.getZildo().setX(-100);
            		ClientEngineZildo.tileEngine.cleanUp();
            		//retEvent.nature = ClientEventNature.NOEVENT;
					break;
				case CHANGINGMAP_LOADED :
					// Changing map : 2/3 we load the new map and launch the
					// fade in

				case FADE_IN :
					retEvent.nature = ClientEventNature.FADING_IN;
					filterCommand.fadeIn(retEvent.effect);

					break;
				case FADING_IN :
					if (filterCommand.isFadeOver()) {
						// Changing map : 3/3 we unblock the player
						filterCommand.active(retEvent.effect.getFilterClass()[0], false, null);
						filterCommand.active(BilinearFilter.class, true, null);
						retEvent.nature = ClientEventNature.NOEVENT;
						retEvent.mapChange = false;
					} else {
						// Call Circle filter to focus on Zildo
						circleOnZildo();
					}
					break;
				case CHANGINGMAP_SCROLL_ASKED :
					retEvent.nature = ClientEventNature.CHANGINGMAP_SCROLL_WAIT_MAP;
					displayGUI = false;
					break;
				case CHANGINGMAP_SCROLL_START :
					if (mapDisplay.getTargetCamera() == null) {
						// Calculate camera-delta and apply it to the cloud filter
						Point deltaCam = new Point(mapDisplay.getCamera());
						mapDisplay.centerCamera();
						mapDisplay.shiftForMapScroll(p_event.angle);
						deltaCam.sub( mapDisplay.getCamera());
						Zildo.pdPlugin.getFilter(CloudFilter.class).addOffset(deltaCam.x, deltaCam.y);

						retEvent.nature = ClientEventNature.CHANGINGMAP_WAITSCRIPT;
					}
					displayGUI = false;
					break;
				case CHANGINGMAP_SCROLL :
					displayGUI = false;
					if (!mapDisplay.isScrolling()) {
						retEvent.nature = ClientEventNature.CHANGINGMAP_SCROLLOVER;
						// Show GUI sprites back
						displayGUI = true;
						//mapDisplay.setPreviousMap(null);
					}
					break;
				case NOEVENT:
				    if (askedEvent != null) {
					retEvent.nature = askedEvent.nature;
					askedEvent = null;
				    }
				default:
				    break;
			}
		}
		// Remove GUI when scripting
		guiDisplay.setToDisplay_generalGui(displayGUI);

		return retEvent;
	}

	/**
	 * Call Circle filter to focus on Zildo.
	 */
	private void circleOnZildo() {
		PersoPlayer zildo = EngineZildo.persoManagement.getZildo();
		Point zildoPos=zildo.getCenteredScreenPosition();
		Zildo.pdPlugin.getFilter(CircleFilter.class).setCenter(zildoPos.x, zildoPos.y);		
	}
	
	public static void cleanUp() {
		filterCommand.cleanUp();
		pixelShaders.cleanUp();
		tileEngine.cleanUp();
		spriteEngine.cleanUp();
		soundPlay.cleanUp();
	}

	void debug() {
		int fps = (int) openGLGestion.getFPS();

		ortho.drawText(1, 50, "fps=" + fps, new Vector3f(0.0f, 0.0f, 1.0f));

		SpriteEntity zildo = spriteDisplay.getZildo();
		if (zildo == null) {
			return;
		}
		ortho.drawText(1, 80, "zildo: " + zildo.x, new Vector3f(1.0f, 0.0f,
				1.0f));
		ortho.drawText(43, 86, "" + zildo.y, new Vector3f(1.0f, 0.0f, 1.0f));

		ortho.enableBlend();

		// Debug collision
		Point camera = mapDisplay.getCamera();
		if (EngineZildo.collideManagement != null && Zildo.infoDebugCollision) {
			for (Collision c : EngineZildo.collideManagement.getTabColli()) {
				if (c != null) {
					int rayon = c.cr;
					int color = 15;
					Perso damager = c.perso;
					Vector4f alphaColor = new Vector4f(0.2f, 0.4f, 0.9f, 0.1f*8);
					if (damager != null && damager.getInfo() == PersoInfo.ENEMY) {
						color = 20;
					}
					if (c.size == null) {
						ortho.box(c.cx - rayon - camera.x, c.cy - rayon
										- camera.y, rayon * 2, rayon * 2, 0,
										alphaColor);
					} else {
						Point center = new Point(c.cx - camera.x, c.cy
								- camera.y);
						Rectangle rect = new Rectangle(center, c.size);
						ortho.box(rect, color, alphaColor);
					}
				}
			}
			// -7, -10
			int x = (int) zildo.x; // - 4;
			int y = (int) zildo.y; // - 10;
			ortho.box(x - 7 -camera.x, y - 17 - camera.y, 14, 14, 12, new Vector4f(0.2f, 0.4f, 0.9f, 0.1f));

			// Feet
			ortho.box(x - 4 -camera.x, y - 2 - camera.y, 8, 4, 12, new Vector4f(0.6f, 0.4f, 0.3f, 0.1f));

		}

		if (Zildo.infoDebugCase) {
			for (int y = 0; y < 20; y++) {
				for (int x = 0; x < 20; x++) {
					int cx = x + camera.x / 16;
					int cy = y + camera.y / 16;
					int px = x * 16 - camera.x % 16;
					int py = y * 16 - camera.y % 16;
					if (cy < 64 && cx < 64) {
						Case c = EngineZildo.mapManagement.getCurrentMap()
								.get_mapcase(cx, cy);
						int onmap = EngineZildo.mapManagement.getCurrentMap()
								.readmap(cx, cy);
						ortho.drawText(px, py + 4, "" + c.getZ(), new Vector3f(
								0, 1, 0));
						ortho.drawText(px, py, "" + onmap,
								new Vector3f(1, 0, 0));
					}
				}
			}
		}
	}

	/**
	 * Return a Client object for launching game. Only one instance of this
	 * object is allowed in an execution.
	 * 
	 * @return Client
	 */
	public static Client getClientForGame() {
		guiDisplay.setToDisplay_generalGui(true);
		return client;
	}

	public final static Client getClientForMenu() {
		return client;
	}

	public void setOpenGLGestion(OpenGLGestion p_openGLGestion) {
		openGLGestion = p_openGLGestion;
	}
	
	public static void askEvent(ClientEventNature p_nature) {
	    askedEvent = new ClientEvent(p_nature);
	}

}
