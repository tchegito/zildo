/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
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

package tools;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.stubbing.OngoingStubbing;

import zildo.Zildo;
import zildo.client.Client;
import zildo.client.ClientEngineZildo;
import zildo.client.ClientEventNature;
import zildo.client.MapDisplay;
import zildo.client.PlatformDependentPlugin;
import zildo.client.PlatformDependentPlugin.KnownPlugin;
import zildo.client.SpriteDisplay;
import zildo.client.gui.GUIDisplay;
import zildo.client.gui.ScreenConstant;
import zildo.client.gui.menu.RegisterChampionMenu;
import zildo.client.sound.SoundPlay;
import zildo.client.stage.GameStage;
import zildo.fwk.FilterCommand;
import zildo.fwk.ZUtils;
import zildo.fwk.bank.TileBank;
import zildo.fwk.db.Identified;
import zildo.fwk.gfx.Ortho;
import zildo.fwk.gfx.engine.SpriteEngine;
import zildo.fwk.gfx.engine.TileEngine;
import zildo.fwk.gfx.filter.CircleFilter;
import zildo.fwk.gfx.filter.CloudFilter;
import zildo.fwk.gfx.filter.ScreenFilter;
import zildo.fwk.input.CommonKeyboardHandler;
import zildo.fwk.input.KeyboardHandler;
import zildo.fwk.input.KeyboardHandler.Keys;
import zildo.fwk.input.KeyboardInstant;
import zildo.fwk.opengl.OpenGLGestion;
import zildo.fwk.opengl.SoundEngine;
import zildo.fwk.script.xml.ScriptReader;
import zildo.fwk.ui.Menu;
import zildo.monde.Game;
import zildo.monde.Hasard;
import zildo.monde.dialog.HistoryRecord;
import zildo.monde.items.Item;
import zildo.monde.items.ItemKind;
import zildo.monde.quest.actions.ScriptAction;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.desc.SpriteDescription;
import zildo.monde.sprites.desc.ZildoOutfit;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.sprites.persos.ia.PathFinder;
import zildo.monde.sprites.utils.SpriteSorter;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
import zildo.monde.util.Pointf;
import zildo.monde.util.Vector2f;
import zildo.platform.input.AndroidInputInfos;
import zildo.platform.input.AndroidKeyboardHandler;
import zildo.platform.input.LwjglKeyboardHandler;
import zildo.platform.input.TouchPoints;
import zildo.resource.Constantes;
import zildo.resource.KeysConfiguration;
import zildo.server.EngineZildo;
import zildo.server.MapManagement;
import zildo.server.SoundManagement;
import zildo.server.state.ClientState;
import zildo.server.state.ScriptManagement;

/**
 * @author Tchegito
 *
 */
@RunWith(ZildoJUnit.class)	// This annotation makes runner aware of our custom ones
public abstract class EngineUT {

	protected EngineZildo engine;
	protected ClientEngineZildo clientEngine;
	
	protected ClientState clientState;
	protected MapUtils mapUtils;	// To easily manipulate the map
	protected PersoUtils persoUtils;	// To easily manipulate the characters
	protected ScriptManagement scriptMgmt; // Just a shortcut
	
	protected KeyboardInstant instant;
	protected static KeyboardHandler fakedKbHandler;	// Will be reused all along
	
	FreezeMonitor freezeMonitor;

	boolean debugInfosPersos = false;
	/** A way to save memory: do not spy on Zildo, so Mockito doesn't create a lot of watchers. Default is TRUE. **/
	boolean spyHero = false;
	boolean spyMapManagement = false;
	boolean disableFreezeMonitor = false;
	boolean clientMainLoop = false;
	boolean soundEnabled = false;
	boolean disableGuiDisplay = false;
	
	boolean displayNFrame = false;	// To enable "frame: x" display ==> very disk consuming !
	
	public volatile int nFrame = 0;
	
	final static int MAX_WAIT_FRAMES = 1500;
	
	protected Perso spawnTypicalPerso(String name, int x, int y) {
		return spawnPerso(PersoDescription.BANDIT_CHAPEAU, name, x, y);
	}
	
	protected Perso spawnPerso(PersoDescription desc, String name, int x, int y) {
		Perso perso = EngineZildo.persoManagement.createPerso(desc, x, y, 0, name, Angle.NORD.value);
		EngineZildo.spriteManagement.spawnPerso(perso);
		return perso;
	}
	
	protected PersoPlayer spawnZildo(Vector2f v) {
		return spawnZildo((int) v.x, (int) v.y);
	}
	
	protected PersoPlayer spawnZildo(int x, int y) {
		PersoPlayer perso = new PersoPlayer(x, y, ZildoOutfit.Zildo);
		if (spyHero) {
			perso = spy(perso);
			// As we spy the object with Mockito, pathFinder's reference becomes wrong. So, we recreate it
			perso.setPathFinder(new PathFinder(perso));
		}
		
		perso.x = x;
		perso.y = y;
		perso.setDesc(PersoDescription.ZILDO);
		perso.setAngle(Angle.NORD);
		perso.setName("zildo");
		EngineZildo.spriteManagement.spawnPerso(perso);
		
		clientState.zildoId = perso.getId();
		clientState.zildo = perso;
		clientState.keys = instant;	// Simulate keypressed if we have a hero
		
		return perso;
	}
	
	protected PersoPlayer spawnZildoWithItem(int x, int y, ItemKind kind) {
		PersoPlayer hero = spawnZildo(x, y);
		Item item = new Item(kind);
		hero.getInventory().add(item);
		hero.setWeapon(item);
		return hero;
	}
	
	/**
	 * Check that given character is, or is not, at a specific location.<br/>
	 * The 'isAt' boolean determines if he should, or not be there.
	 * @param perso
	 * @param target
	 * @param isAt TRUE=he should be at / FALSE=he shouldn't be at
	 */
	protected void assertLocation(Perso perso, Pointf target, boolean isAt) {
		Assert.assertTrue("Perso's target should have been null !", perso.getTarget() == null);
		assertLocation((Element) perso, target, isAt);
	}
	
	/** Check an element location if it is/isn't at a given location, with 0.5 tolerance **/
	protected void assertLocation(Element elem, Pointf target, boolean isAt) {
		String entityType = elem.getEntityType().toString();
		String name = entityType + (elem.getName() != null ? (" " + elem.getName()) : "");
		String endMessage = target+" but is at ("+elem.x+","+elem.y+")";
		String message;
		if (isAt) {
			message = name+" should have been at " + endMessage;
			Assert.assertTrue(message, 
					Math.abs(elem.x - target.x) <= 0.5f && Math.abs(elem.y - target.y) <= 0.5f);
		} else {
			message = name+" shouldn't have been at " + endMessage;
			Assert.assertTrue(message, 
					Math.abs(elem.x - target.x) > 0.5f || Math.abs(elem.y - target.y) > 0.5f);
		}
	}
	
	protected void assertNotBlocked(Perso perso) {
		for (Angle a : Angle.values()) {
			Point coord = a.coords;
			Pointf loc = perso.tryMove(coord.x, coord.y);
			if (loc.x != perso.x || loc.y != perso.y) {
				return;
			}
		}
		Assert.assertTrue("Character is blocked !", false);
	}

	protected void renderFrames(int nbFrame) {
		renderFrames(nbFrame, true);
	}
	
	protected void renderFrames(int nbFrame, boolean debugInfos) {
		for (int i=0;i<nbFrame;i++) {
			if (clientState.zildo != null) {	// Simulate keypressed if we have a hero
				clientState.keys = instant;
			}
			updateGame();
			engine.renderFrame(Collections.singleton(clientState));
			
	        // Dialogs
	        if (ClientEngineZildo.guiDisplay.launchDialog(EngineZildo.dialogManagement.getQueue())) {
	        	EngineZildo.dialogManagement.stopDialog(clientState, false);
	        }
	        
	        if (clientMainLoop) {
	        	ClientEngineZildo.client.mainLoop();
	        }
	        
	        clientEngine.renderFrame(false);
			ClientEngineZildo.filterCommand.doFilter();
			if (debugInfos) {
				if (debugInfosPersos) {
					for (Perso perso : EngineZildo.persoManagement.tab_perso) {
						System.out.println(nFrame+": Perso: "+perso.getName()+" at "+perso.x+","+perso.y);
					}
				} else if (displayNFrame) {
					System.out.println("frame:"+nFrame);
				}
			}
			nFrame++;
		}		
	}
	
	private void updateGame() {
		clientState.event = engine.renderEvent(clientState.event);
		clientState.event = clientEngine.renderEvent(clientState.event);
    	EngineZildo.dialogManagement.resetQueue();
	}
	
	public void initServer(Game game) {
		//game.editing = true;
		engine = new EngineZildo(game);
	}
	
	@Before
	public void setUp() {
		// Reset plugin state, because some tests change it
		PlatformDependentPlugin.currentPlugin = KnownPlugin.Lwjgl;
		Game game = new Game(null, "hero");
		initServer(game);
		
		// Create standard map
		//EngineZildo.soundManagement.setForceMusic(true);
		// Prepare mock for later
		MapManagement mm = new MapManagement();
		if (spyMapManagement) {
			mm = spy(mm);
		}
		EngineZildo.mapManagement = mm;
		
		initCounters();

		// Cheat to have a client
		Zildo.pdPlugin.kbHandler = null;
		Client fakeClient = new Client(true);
		ClientEngineZildo.client = fakeClient; 
		// Fake a client state list
        Assert.assertEquals(0, EngineZildo.persoManagement.tab_perso.size());
		clientState = new ClientState(null, 1);
		
		EngineZildo.setClientState(clientState);
		// Tile collision
		for (String bankName : TileEngine.tileBankNames) {
			TileBank motifBank = new TileBank();

			motifBank.charge_motifs(bankName);
		}
		
		// Mock certain screen filters
		@SuppressWarnings("unchecked")
		Class<ScreenFilter>[] filterClasses = new Class[] { CloudFilter.class, CircleFilter.class};
		for (Class<ScreenFilter> clazz : filterClasses) {
			ScreenFilter filter = (ScreenFilter) mock(clazz, withSettings().stubOnly());
			Zildo.pdPlugin.filters.put(clazz, filter);
		}

		// Fake client display
		if (clientEngine == null) {
			clientEngine = new ClientEngineZildo(null, false, fakeClient);
			//ClientEngineZildo.screenConstant = new ScreenConstant(Zildo.viewPortX, Zildo.viewPortY);
			//ClientEngineZildo.spriteDisplay = new SpriteDisplay();
			
			Zildo.soundEnabled = soundEnabled;
			if (soundEnabled) {
				EngineZildo.soundManagement = mock(SoundManagement.class);
			}
			ClientEngineZildo.soundPlay = mock(SoundPlay.class);
			ClientEngineZildo.soundEngine = mock(SoundEngine.class, withSettings().stubOnly());
			ClientEngineZildo.filterCommand = new FilterCommand();
			ClientEngineZildo.screenConstant = new ScreenConstant(Zildo.viewPortX, Zildo.viewPortY);
			ClientEngineZildo.openGLGestion = mock(OpenGLGestion.class, withSettings().stubOnly());
			ClientEngineZildo.spriteEngine = mock(SpriteEngine.class, withSettings().stubOnly());
			ClientEngineZildo.spriteDisplay = new SpriteDisplayMocked(ClientEngineZildo.spriteEngine);
			GUIDisplay gd = null;
			if (disableGuiDisplay) {
				gd = new GUIDisplay() {
					@SuppressWarnings("unused")
					void setToDisplay_generalGui() {	// Just to neutralize the methods
					}
				};
			} else {
				gd = spy(new GUIDisplay());
				when(gd.skipDialog()).thenReturn(true);
			}
			ClientEngineZildo.guiDisplay = gd;
			ClientEngineZildo.screenConstant = new ScreenConstant(Zildo.screenX, Zildo.screenY);
			
			fakeClient.setOpenGLGestion(ClientEngineZildo.openGLGestion);
			//fakeClient.askStage(stage);
			// Load default map and initialize map utils
			EngineZildo.mapManagement.loadMap("preintro", false);
			mapUtils = new MapUtils();
			persoUtils = new PersoUtils();

			ClientEngineZildo.mapDisplay = spy(new MapDisplay(mapUtils.area));
			//doNothing().when(ClientEngineZildo.mapDisplay).centerCamera();
			ClientEngineZildo.tileEngine = mock(TileEngine.class, withSettings().stubOnly());
			ClientEngineZildo.ortho = mock(Ortho.class, withSettings().stubOnly());
			
		}
		
		// Initialize keyboard to simulate input
		instant = new KeyboardInstant();
		// We have to neutralize pdPlugin.kbHandler, then recreate a handle. Because reset() is not satisfying
		fakedKbHandler = spy(new CommonKeyboardHandler() {
			
			LwjglKeyboardHandler justForCodes = new LwjglKeyboardHandler();
			
			@Override
			public void poll() {
				super.poll();
			}
			@Override
			public boolean next() {
				return false;
			}
			
			@Override
			public boolean isKeyDown(int p_code) {
				return false;
			}
			
			@Override
			public boolean getEventKeyState() {
				return false;
			}
			
			@Override
			public int getEventKey() {
				return 0;
			}
			
			@Override
			public char getEventCharacter() {
				return 0;
			}
			
			@Override
			public int getCode(Keys k) {
				return justForCodes.getCode(k);
			}
		});
		Zildo.pdPlugin.kbHandler = fakedKbHandler;
		
		if (clientMainLoop) {

        	doAnswer(invocation -> {
	    		for (GameStage stage : ClientEngineZildo.getClientForGame().getCurrentStages()) {
	    			stage.renderGame();
	    		}
	    		return null;
        	}).when(ClientEngineZildo.openGLGestion).render(anyBoolean());

		}
		// Create a thread wich monitors any freeze
		if (!disableFreezeMonitor) {
			freezeMonitor = new FreezeMonitor(this);
			freezeMonitor.start();
		}

		// To activate "frame: x" display => set DISPLAY_NFRAME to "true"
		if ("true".equals(System.getProperty("DISPLAY_NFRAME"))) {
			displayNFrame = true;
		}
		
		scriptMgmt = EngineZildo.scriptManagement;
	}
	
	public void waitEndOfScripting() {
		waitEndOfScripting(null);
	}
	
	public void waitChangingMap() {
		int safetyCheckFrame = 0;
		PersoPlayer hero = EngineZildo.persoManagement.getZildo();
		while (!EngineZildo.mapManagement.isChangingMap(hero)) {
			renderFrames(1);
			if (safetyCheckFrame++ > MAX_WAIT_FRAMES) {
				throw new RuntimeException("Test seems blocked there ! After " + MAX_WAIT_FRAMES + " frames, nothing happened !");
			}
		}
	}
	
	public void waitEndOfScroll() {
		while (clientState.event.nature == ClientEventNature.CHANGINGMAP_SCROLL ||
				clientState.event.nature == ClientEventNature.CHANGINGMAP_WAITSCRIPT ||
				clientState.event.nature == ClientEventNature.CHANGINGMAP_SCROLL_WAIT_MAP) {
			renderFrames(1);
		}
		renderFrames(1);
	}
	
	/** Wait until initialization scripts are over 
	 * (at least 1 frame ==> in order to init things like PersoCollision#initFrame). **/
	public void waitEndOfScripting(ScriptAction action) {
		// Wait end of scripts
		int safetyCheckFrame = 0;
		while (true) {
			renderFrames(1);
			if (!EngineZildo.scriptManagement.isScripting()) {
				break;
			}
			Menu menu = ClientEngineZildo.client.getCurrentMenu();
			if (menu != null && menu.getClass() == RegisterChampionMenu.class) {
				// In any way, we cut here if test reaches end of game
				break;
			}
			if (action != null) {
				action.launchAction(clientState);
			}
			safetyCheckFrame++;
			if (safetyCheckFrame > MAX_WAIT_FRAMES) {
				throw new RuntimeException("Test seems blocked there ! After " + MAX_WAIT_FRAMES + " frames, nothing happened !");
			}
		}
	}

	/** Useful to pass a cutscene **/
	public void waitEndOfScriptingPassingDialog() {
		waitEndOfScripting(new PassingDialogScriptAction(null));
		// Release buttons
		simulateKeyPressed();
		renderFrames(1);
	}

	// Declare here to be overridable in tests, for example to add a check on something
	protected class PassingDialogScriptAction extends ScriptAction {
		public PassingDialogScriptAction(String p_key) {
			super(p_key);
		}
		int timeToWait = 0;
		@Override
		public void launchAction(ClientState p_clientState) {
			if (p_clientState.dialogState.isDialoguing()) {
				if (timeToWait == 0) {
					simulateKeyPressed(KeysConfiguration.PLAYERKEY_ACTION.code);
					timeToWait = 1;
				} else {
					timeToWait--;
					if (timeToWait == 0) {
						//simulateKeyPressed(KeysConfiguration.PLAYERKEY_ACTION.code);
						simulateKeyPressed();
					}
				}
			}
		}
	};
	
	/** Useful to pass a cutscene **/
	public void waitEndOfScriptingRandomlyPressingButtons() {
		waitEndOfScripting(new ScriptAction(null) {
			int timeToWait = 0;
			@Override
			public void launchAction(ClientState p_clientState) {
				//if (p_clientState.dialogState.isDialoguing()) {
					if (timeToWait == 0) {
						int a = new Hasard().de6();
						if (a > 4) {
							simulateKeyPressed(KeysConfiguration.PLAYERKEY_ACTION.code, KeysConfiguration.PLAYERKEY_ATTACK.code);
						} else if (a > 2) {
							simulateKeyPressed(KeysConfiguration.PLAYERKEY_ACTION.code);
						} else {
							simulateKeyPressed(KeysConfiguration.PLAYERKEY_ATTACK.code);
						}
						simulateKeyPressed(KeysConfiguration.PLAYERKEY_DIALOG.code, KeysConfiguration.PLAYERKEY_ACTION.code);
						//simulateKeyPressed(KeysConfiguration.PLAYERKEY_ATTACK.code);
						timeToWait = 4;
					} else {
						timeToWait--;
						if (timeToWait == 0) {
							//simulateKeyPressed(KeysConfiguration.PLAYERKEY_ACTION.code);
							simulateKeyPressed();
						}
					}
				//}
			}
		});
	}

	/** Wait for map switch, then check that new map is the expected one **/
	protected void assertMapIsChangingToward(String mapName) {
		String current = EngineZildo.mapManagement.getCurrentMap().getName();
		int safetyCheckFrame = 0;
		while (!EngineZildo.mapManagement.isChangingMap(clientState.zildo) && safetyCheckFrame <= MAX_WAIT_FRAMES) {
			renderFrames(1);
			safetyCheckFrame++;

		}
		while (current.equals(EngineZildo.mapManagement.getCurrentMap().getName())  && safetyCheckFrame < MAX_WAIT_FRAMES) {
			renderFrames(1);
			safetyCheckFrame++;
		}
		if (safetyCheckFrame > MAX_WAIT_FRAMES) {
			throw new RuntimeException("Test seems blocked there ! After " + MAX_WAIT_FRAMES + " frames, nothing happened !");
		}
		Assert.assertEquals("Map has changed but to the wrong one !", mapName, EngineZildo.mapManagement.getCurrentMap().getName());
	}

	protected void loadXMLAsString(String string) throws Exception {
		InputStream stream = new ByteArrayInputStream(string.getBytes());
		EngineZildo.scriptManagement.getAdventure().merge(ScriptReader.loadStream(stream));
	}
	
	public void simulateDirection(int x, int y) {
		simulateDirection(new Vector2f(x, y));
	}
	
	/** Simulates player holding a direction with the d-pad **/
	public void simulateDirection(Vector2f dir) {
		when(fakedKbHandler.getDirection()).thenReturn(dir);
		instant.update();
	}

	/** Simulate 1 or many key pressed **/
	public void simulateKeyPressed(final Keys... keys) {
		reset(fakedKbHandler);
		if (keys == null || keys.length == 0) {
			doReturn(false).when(fakedKbHandler).isKeyDown(anyInt());
			doReturn(false).when(fakedKbHandler).next();
		} else {
			if (keys.length == 1) {
				doReturn(true).when(fakedKbHandler).isKeyDown(keys[0]);
				// Mock for Menu class
				when(fakedKbHandler.next()).thenReturn(true).thenReturn(false);
				doReturn(true).when(fakedKbHandler).getEventKeyState();
				doReturn(fakedKbHandler.getCode(keys[0])).when(fakedKbHandler).getEventKey();
			} else {
				// Multiple value matching
				doReturn(true).when(fakedKbHandler).isKeyDown(argThat(new ArgumentMatcher<Keys>() {
					@Override
					public boolean matches(Keys argument) {
						for (Keys k : keys) {
							if (argument == k) {
								return true;
							}
						}
						return false;
					}
				}));
				// Mock for menu class (mandatory to loop twice, because Mockito got mixed up otherwise)
				OngoingStubbing<Boolean> osNext = null;
				doReturn(true).when(fakedKbHandler).getEventKeyState();
				for (Keys k : keys) {
					if (osNext == null) {
						osNext = when(fakedKbHandler.next());
					}
					osNext = osNext.thenReturn(true);
				}
				osNext.thenReturn(false);
				OngoingStubbing<Integer> osEventKey = null;
				for (Keys k : keys) {
					int code = fakedKbHandler.getCode(k);
					if (osEventKey == null) {
						osEventKey = when(fakedKbHandler.getEventKey());
					}
					
					osEventKey = osEventKey.thenReturn(code);
				}
				osEventKey.thenReturn(0);
			}
		}
		instant.update();
	}
	
	/** Press a key, wait, then hold it, and wait again given time (by frame number) **/
	public void simulatePressButton(Keys key, int time) {
		simulateKeyPressed(key);
		renderFrames(1);
		simulateKeyPressed();
		renderFrames(time);		
	}
	
	/** Check that a given quest name is running **/
	public void checkScriptRunning(String name) {
		Assert.assertTrue(EngineZildo.scriptManagement.isScripting());
		Assert.assertTrue(EngineZildo.scriptManagement.isQuestProcessing(name));
	}

	public void waitForScriptRunning(String name) {
		waitForScriptRunning(name, null);
	}
	
	public void waitForQuestDone(String name) {
		while (!EngineZildo.scriptManagement.isQuestDone(name)) {
			renderFrames(1);
		}
	}
	
	/** Wait for a given quest name to run **/
	public void waitForScriptRunning(String name, Runnable run) {
		while (true) {
			renderFrames(1);
			if (run != null) run.run();
			if (EngineZildo.scriptManagement.isQuestProcessing(name)) {
				break;
			}
		}
	}
	
	/** Wait for a given quest to finish **/
	public void waitForScriptFinish(String name) {
		Assert.assertTrue(EngineZildo.scriptManagement.isQuestProcessing(name));
		while (true) {
			renderFrames(1);
			if (!EngineZildo.scriptManagement.isQuestProcessing(name)) {
				break;
			}
		}
	}
	
	public void talkAndCheck(String key) {
		talkAndCheck(key, true);
	}
	/** Make hero talkin by pressing action button, and check that given dialog has been said. Then go on dialog, if given paramter is TRUE. **/
	public void talkAndCheck(String key, boolean goOn) {
		PersoPlayer hero = EngineZildo.persoManagement.getZildo();
		Assert.assertTrue(hero.getDialoguingWith() == null);
		simulatePressButton(Keys.Q, 2);
		Assert.assertTrue("Hero should have been talking with someone !", hero.getDialoguingWith() != null);
		checkNextDialog(key);
		if (goOn) {
			goOnDialog();
		}
	}

	// Specific for dialogs
	public void goOnDialog() {
		simulatePressButton(Keys.Q, 2);	// Skip
		simulatePressButton(Keys.Q, 2);	// Go on next
	}

	public List<HistoryRecord> dials() {
		return EngineZildo.game.getLastDialog();
	}
	
	/** Check that dialog history records has an expected size, and last one was with expected key **/
	public void checkNextDialog(int number, String key) {
		Assert.assertEquals(number, dials().size());
		checkNextDialog(key);
	}

	public void checkNextDialog(String key) {
		Assert.assertEquals(key, ZUtils.listTail(dials()).key);
	}
	
	@After
	public void tearDown() {
		EngineZildo.spriteManagement.clearSprites(true);

		reset(fakedKbHandler);
		// Reset render platform to LWJGL
		PlatformDependentPlugin.currentPlugin = KnownPlugin.Lwjgl;
		// Reset input
		simulateDirection(new Vector2f(0, 0));
		if (freezeMonitor != null) {
			freezeMonitor.cutItOut();
	/*
			try {
				//freezeMonitor.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			*/
			//freezeMonitor.stop();
		}
	}
	
	private void initCounters() {
		for (Perso perso : EngineZildo.persoManagement.tab_perso) {
			Identified.remove(SpriteEntity.class, perso.getId());
			for (Element elem : perso.getPersoSprites()) {
				Identified.remove(SpriteEntity.class, elem.getId());
			}
		}
		Identified.resetCounter(SpriteEntity.class);
		Identified.clearAll();
	}
	
	// All the following is just to avoid to transform a "private" method into a "public"
	private class SpriteSorterMocked extends SpriteSorter {
		public SpriteSorterMocked(int p_sortYMax, int p_sortYRealMax) {
			super(p_sortYMax, p_sortYRealMax);
		}

		public SpriteEntity[][] getTabTri() {
			return tab_tri;
		}
	}
	
	public class SpriteDisplayMocked extends SpriteDisplay {
		public SpriteDisplayMocked(SpriteEngine spriteEngine) {
			super(spriteEngine);
			
			spriteSorter=new SpriteSorterMocked(Constantes.SORTY_MAX, Constantes.SORTY_REALMAX);
		}

		@Override
		public void setEntities(List<SpriteEntity> p_entities) {
			super.setEntities(p_entities);
			//spriteEntities.clear();
			//spriteEntities.addAll(p_entities);
		}
		
		public SpriteEntity[][] getTabTri() {
			return ((SpriteSorterMocked)spriteSorter).getTabTri();
		}
		public int[][] getQuadOrder() {
			return ((SpriteSorterMocked)spriteSorter).getQuadOrder();
		}
		public List<SpriteEntity> getEntities() {
			return spriteEntities;
		}
		 
	}
	
	// TODO: check if annotation wouldn't be more appropriate (it would avoid to create twice fakedKbHandler)
	protected TouchPoints enableAndroidTouch() {
		TouchPoints touchedPoints = new TouchPoints();

		PlatformDependentPlugin.currentPlugin = KnownPlugin.Android;
		
		// Paste from TouchListener class
		AndroidKeyboardHandler kbHandler = spy(new AndroidKeyboardHandler()); // 'Spy' to avoid crash in tearDown
		AndroidInputInfos infos = new AndroidInputInfos();
		infos.liveTouchedPoints = touchedPoints;
		kbHandler.setAndroidInputInfos(infos);
		
		fakedKbHandler = (KeyboardHandler) kbHandler;
		Zildo.pdPlugin.kbHandler = fakedKbHandler;
		
		return touchedPoints;
	}
	
	/** Convenience method to reproduce quests achievements from a crash report **/
	protected void accomplishQuest(String questsFromReport) {
		String[] quests = questsFromReport.split(", ");
		System.out.println(quests.length+" quests");
		for (String quest : quests) {
			EngineZildo.scriptManagement.accomplishQuest(quest,  false);
		}
	}

	/** Return all entities matching given description **/
	protected List<SpriteEntity> findEntitiesByDesc(SpriteDescription desc) {
		List<SpriteEntity> found = ZUtils.arrayList();
		for (SpriteEntity e : EngineZildo.spriteManagement.getSpriteEntities(null)) {
			if (e.getDesc() == desc) {
				found.add(e);
			}
		}
		return found;
	}
	
	/** Return first entity matching description **/
	protected SpriteEntity findEntityByDesc(SpriteDescription desc) {
		List<SpriteEntity> entities = findEntitiesByDesc(desc);
		return entities.isEmpty() ? null : entities.get(0);
	}
	
	
	/** Wait for a specific projectile to be thrown. Be careful, because wait is unlimited. **/
	protected SpriteEntity waitForSpecificEntity(ElementDescription... desc) {
		SpriteEntity found = null;
		int safetyCheckFrame=0;
		while (found == null) {
			List<SpriteEntity> entities = EngineZildo.spriteManagement.getSpriteEntities(null);
			for (SpriteEntity entity : entities) {
				for (ElementDescription d : desc) {
					if (entity.getDesc() == d && entity.isVisible()) {
						found = entity;
						break;
					}
				}
				if (found != null) break;
			}
			renderFrames(1);
			safetyCheckFrame++;
			if (safetyCheckFrame > MAX_WAIT_FRAMES) {
				throw new RuntimeException("Test seems blocked there ! After " + MAX_WAIT_FRAMES + " frames, nothing happened !");
			}
		}
		return found;
	}
}