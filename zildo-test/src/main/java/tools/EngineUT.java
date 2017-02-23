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

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;

import zildo.Zildo;
import zildo.client.Client;
import zildo.client.ClientEngineZildo;
import zildo.client.MapDisplay;
import zildo.client.PlatformDependentPlugin;
import zildo.client.PlatformDependentPlugin.KnownPlugin;
import zildo.client.SpriteDisplay;
import zildo.client.gui.GUIDisplay;
import zildo.client.gui.ScreenConstant;
import zildo.client.sound.SoundPlay;
import zildo.fwk.FilterCommand;
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
import zildo.monde.Game;
import zildo.monde.Hasard;
import zildo.monde.dialog.HistoryRecord;
import zildo.monde.quest.actions.ScriptAction;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.desc.PersoDescription;
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
import zildo.resource.Constantes;
import zildo.resource.KeysConfiguration;
import zildo.server.EngineZildo;
import zildo.server.MapManagement;
import zildo.server.state.ClientState;

/**
 * @author Tchegito
 *
 */
@RunWith(ZildoJUnit.class)	// This annotation makes runner aware of our custom ones
public class EngineUT {

	protected EngineZildo engine;
	protected ClientEngineZildo clientEngine;
	
	protected ClientState clientState;
	protected MapUtils mapUtils;	// To easily manipulate the map
	
	protected KeyboardInstant instant;
	static KeyboardHandler fakedKbHandler;	// Will be reused all along
	
	FreezeMonitor freezeMonitor;

	boolean debugInfosPersos = false;
	/** A way to save memory: do not spy on Zildo, so Mockito doesn't create a lot of watchers. Default is TRUE. **/
	boolean spyHero = false;
	boolean spyMapManagement = false;
	boolean disableFreezeMonitor = false;
	
	public volatile int nFrame = 0;
	
	protected Perso spawnTypicalPerso(String name, int x, int y) {
		return spawnPerso(PersoDescription.BANDIT_CHAPEAU, name, x, y);
	}
	
	protected Perso spawnPerso(PersoDescription desc, String name, int x, int y) {
		Perso perso = EngineZildo.persoManagement.createPerso(desc, x, y, 0, name, Angle.NORD.value);
		EngineZildo.spriteManagement.spawnPerso(perso);
		return perso;
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
	
	/**
	 * Check that given character is, or is not, at a specific location.<br/>
	 * The 'isAt' boolean determines if he should, or not be there.
	 * @param perso
	 * @param target
	 * @param isAt TRUE=he should be at / FALSE=he shouldn't be at
	 */
	protected void assertLocation(Perso perso, Point target, boolean isAt) {
		Assert.assertTrue("Perso's target should have been null !", perso.getTarget() == null);
		assertLocation((Element) perso, target, isAt);
	}
	
	/** Check an element location if it is/isn't at a given location, with 0.5 tolerance **/
	protected void assertLocation(Element elem, Point target, boolean isAt) {
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
	        
	        clientEngine.renderFrame(false);
			ClientEngineZildo.filterCommand.doFilter();
			if (debugInfos) {
				if (!debugInfosPersos) {
					System.out.println("frame:"+nFrame);
				} else {
					for (Perso perso : EngineZildo.persoManagement.tab_perso) {
						System.out.println(nFrame+": Perso: "+perso.getName()+" at "+perso.x+","+perso.y);
					}
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
		Client fakeClient = spy(new Client(true));
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
			ScreenFilter cloudFilter = (ScreenFilter) mock(clazz);
			Zildo.pdPlugin.filters.put(clazz, cloudFilter);
		}

		// Fake client display
		if (clientEngine == null) {
			clientEngine = new ClientEngineZildo(null, false, fakeClient);
			//ClientEngineZildo.screenConstant = new ScreenConstant(Zildo.viewPortX, Zildo.viewPortY);
			//ClientEngineZildo.spriteDisplay = new SpriteDisplay();
			
			ClientEngineZildo.soundPlay = mock(SoundPlay.class);
			ClientEngineZildo.filterCommand = new FilterCommand();
			ClientEngineZildo.screenConstant = new ScreenConstant(Zildo.viewPortX, Zildo.viewPortY);
			ClientEngineZildo.openGLGestion = mock(OpenGLGestion.class);
			ClientEngineZildo.spriteEngine = mock(SpriteEngine.class);
			ClientEngineZildo.spriteDisplay = new SpriteDisplayMocked(ClientEngineZildo.spriteEngine);
			ClientEngineZildo.guiDisplay = spy(new GUIDisplay());
			ClientEngineZildo.screenConstant = new ScreenConstant(Zildo.screenX, Zildo.screenY);
			when(ClientEngineZildo.guiDisplay.skipDialog()).thenReturn(true);

			// Load default map and initialize map utils
			EngineZildo.mapManagement.loadMap("preintro", false);
			mapUtils = new MapUtils();

			ClientEngineZildo.mapDisplay = spy(new MapDisplay(mapUtils.area));
			//doNothing().when(ClientEngineZildo.mapDisplay).centerCamera();
			ClientEngineZildo.tileEngine = mock(TileEngine.class);
			ClientEngineZildo.ortho = mock(Ortho.class);
			
		}

		// Tells client that we're done with menu and inside the game
		fakeClient.handleMenu(null);
		/*
		new CloudFilter(null) {
			
			@Override
			public boolean renderFilter() {
				// TODO Auto-generated method stub
				return false;
			}
			@Override
			public void addOffset(int x, int y) {
				offsetU += x;
				offsetV += y;
			}
		});
		*/
		
		// Initialize keyboard to simulate input
		instant = new KeyboardInstant();
		if (fakedKbHandler == null) {
			fakedKbHandler = org.mockito.Mockito.mock(CommonKeyboardHandler.class);
		}
		Zildo.pdPlugin.kbHandler = fakedKbHandler;
		
		// Create a thread wich monitors any freeze
		if (!disableFreezeMonitor) {
			freezeMonitor = new FreezeMonitor(this);
			freezeMonitor.start();
		}

	}
	
	public void waitEndOfScripting() {
		waitEndOfScripting(null);
	}
	/** Wait until initialization scripts are over. **/
	public void waitEndOfScripting(ScriptAction action) {
		// Wait end of scripts
		while (EngineZildo.scriptManagement.isScripting()) {
			renderFrames(1);
			if (action != null) {
				action.launchAction(clientState);
			}
		}
	}

	/** Useful to pass a cutscene **/
	public void waitEndOfScriptingPassingDialog() {
		waitEndOfScripting(new ScriptAction(null) {
			int timeToWait = 0;
			@Override
			public void launchAction(ClientState p_clientState) {
				//if (p_clientState.dialogState.isDialoguing()) {
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
				//}
			}
		});
	}

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
		} else {
			if (keys.length == 1) {
				doReturn(true).when(fakedKbHandler).isKeyDown(keys[0]);
			} else {
				// Multiple value matching
				doReturn(true).when(fakedKbHandler).isKeyDown(Matchers.argThat(new ArgumentMatcher<Keys>() {
					@Override
					public boolean matches(Object argument) {
						for (Keys k : keys) {
							if (argument == k) {
								return true;
							}
						}
						return false;
					}
				}));
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
	
	// Specific for dialogs
	public void goOnDialog() {
		simulatePressButton(Keys.Q, 2);	// Skip
		simulatePressButton(Keys.Q, 2);	// Go on next
	}

	public List<HistoryRecord> dials() {
		return EngineZildo.game.getLastDialog();
	}
	
	public void checkNextDialog(int number, String key) {
		Assert.assertEquals(number, dials().size());
		Assert.assertEquals(key, dials().get(number-1).key);
	}
	
	@SuppressWarnings("deprecation")
	@After
	public void tearDown() {
		reset(fakedKbHandler);
		// Reset render platform to LWJGL
		PlatformDependentPlugin.currentPlugin = KnownPlugin.Lwjgl;
		// Reset input
		simulateDirection(new Vector2f(0, 0));
		if (freezeMonitor != null) {
			freezeMonitor.cutItOut();
	
			try {
				freezeMonitor.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			freezeMonitor.stop();
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
	
	protected class SpriteDisplayMocked extends SpriteDisplay {
		public SpriteDisplayMocked(SpriteEngine spriteEngine) {
			super(spriteEngine);
			
			spriteSorter=new SpriteSorterMocked(Constantes.SORTY_MAX, Constantes.SORTY_REALMAX);
		}

		@Override
		public void setEntities(List<SpriteEntity> p_entities) {
			spriteEntities.clear();
			spriteEntities.addAll(p_entities);
		}
		
		public SpriteEntity[][] getTabTri() {
			return ((SpriteSorterMocked)spriteSorter).getTabTri();
		}
	}
}