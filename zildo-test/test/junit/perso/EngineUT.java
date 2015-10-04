/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
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

package junit.perso;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import zildo.Zildo;
import zildo.client.Client;
import zildo.client.ClientEngineZildo;
import zildo.client.MapDisplay;
import zildo.client.gui.DialogContext;
import zildo.client.gui.DialogDisplay;
import zildo.client.gui.GUIDisplay;
import zildo.client.gui.ScreenConstant;
import zildo.client.sound.SoundPlay;
import zildo.fwk.FilterCommand;
import zildo.fwk.bank.TileBank;
import zildo.fwk.db.Identified;
import zildo.fwk.gfx.Ortho;
import zildo.fwk.gfx.engine.TileEngine;
import zildo.fwk.gfx.filter.CircleFilter;
import zildo.fwk.gfx.filter.CloudFilter;
import zildo.fwk.gfx.filter.ScreenFilter;
import zildo.fwk.input.CommonKeyboardHandler;
import zildo.fwk.input.KeyboardHandler;
import zildo.fwk.input.KeyboardInstant;
import zildo.monde.Game;
import zildo.monde.dialog.WaitingDialog;
import zildo.monde.quest.actions.ScriptAction;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.desc.ZildoOutfit;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoNJ;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
import zildo.monde.util.Pointf;
import zildo.monde.util.Vector2f;
import zildo.server.EngineZildo;
import zildo.server.MapManagement;
import zildo.server.state.ClientState;

/**
 * @author Tchegito
 *
 */
public class EngineUT {

	protected EngineZildo engine;
	protected ClientEngineZildo clientEngine;
	
	protected ClientState clientState;
	protected MapUtils mapUtils;	// To easily manipulate the map
	
	protected KeyboardInstant instant = new KeyboardInstant();
	
	int nFrame = 0;
	
	protected Perso spawnTypicalPerso(String name, int x, int y) {
		return spawnPerso(PersoDescription.BANDIT_CHAPEAU, name, x, y);
	}
	
	protected Perso spawnPerso(PersoDescription desc, String name, int x, int y) {
		Perso perso = new PersoNJ();
		perso.x = x;
		perso.y = y;
		perso.setDesc(desc);
		perso.setAngle(Angle.NORD);
		perso.setName(name);
		EngineZildo.spriteManagement.spawnPerso(perso);
		return perso;
	}
	
	protected PersoPlayer spawnZildo(int x, int y) {
		PersoPlayer perso = spy(new PersoPlayer(x, y, ZildoOutfit.Zildo));
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
		Assert.assertTrue(perso.getTarget() == null);
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
			Pointf loc = perso.tryMove(perso.x + coord.x, perso.y + coord.y);
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
	        
			ClientEngineZildo.filterCommand.doFilter();
			if (debugInfos) {
				for (Perso perso : EngineZildo.persoManagement.tab_perso) {
					System.out.println(nFrame+": Perso: "+perso.getName()+" at "+perso.x+","+perso.y);
				}
			}
			nFrame++;
		}		
	}
	
	private void updateGame() {
		engine.renderEvent(clientState.event);
		clientState.event = engine.renderEvent(clientState.event);
		clientState.event = clientEngine.renderEvent(clientState.event);
    	EngineZildo.dialogManagement.resetQueue();
	}
	
	@Before
	public void setUp() {
		Game game = new Game(null, "hero");
		game.editing = true;
		engine = new EngineZildo(game);
		// Create standard map
		//EngineZildo.soundManagement.setForceMusic(true);
		// Prepare mock for later
		EngineZildo.mapManagement = spy(new MapManagement());
		EngineZildo.mapManagement.loadMap("preintro", false);
		
		// Cheat to have a client
		Client client =mock(Client.class);
		when(client.isIngameMenu()).thenReturn(false);
		ClientEngineZildo.client = client; 
		// Fake a client state list
		clientState = new ClientState(null, 1);
		
		EngineZildo.setClientState(clientState);
		// Tile collision
		for (String bankName : TileEngine.tileBankNames) {
			TileBank motifBank = new TileBank();

			motifBank.charge_motifs(bankName);
		}
		
		mapUtils = new MapUtils();
		
		// Mock certain screen filters
		@SuppressWarnings("unchecked")
		Class<ScreenFilter>[] filterClasses = new Class[] { CloudFilter.class, CircleFilter.class};
		for (Class<ScreenFilter> clazz : filterClasses) {
			ScreenFilter cloudFilter = (ScreenFilter) mock(clazz);
			Zildo.pdPlugin.filters.put(clazz, cloudFilter);
		}

		// Fake client display
		if (clientEngine == null) {
			clientEngine = new ClientEngineZildo(null, false, mock(Client.class));
			//ClientEngineZildo.screenConstant = new ScreenConstant(Zildo.viewPortX, Zildo.viewPortY);
			//ClientEngineZildo.spriteDisplay = new SpriteDisplay();
			ClientEngineZildo.soundPlay = mock(SoundPlay.class);
			ClientEngineZildo.filterCommand = new FilterCommand();
			ClientEngineZildo.guiDisplay = mock(GUIDisplay.class);
			ClientEngineZildo.screenConstant = new ScreenConstant(Zildo.screenX, Zildo.screenY);
			DialogDisplay dialogDisplay = new DialogDisplay(new DialogContext(), 0);
			when(ClientEngineZildo.guiDisplay.launchDialog(any())).then(new Answer<Boolean>() {
				@SuppressWarnings("unchecked")
				@Override
				public Boolean answer(InvocationOnMock invocation) throws Throwable {
					List<WaitingDialog> dials = (List<WaitingDialog>) invocation.getArguments()[0];
					return dialogDisplay.launchDialog(dials);
				}
			});
			when(ClientEngineZildo.guiDisplay.skipDialog()).thenReturn(true);
			
			ClientEngineZildo.mapDisplay = spy(new MapDisplay(mapUtils.area));
			doNothing().when(ClientEngineZildo.mapDisplay).centerCamera();
			ClientEngineZildo.tileEngine = mock(TileEngine.class);
			ClientEngineZildo.ortho = mock(Ortho.class);
			
		}
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

	/** Simulates player holding a direction with the d-pad **/
	public void simulateDirection(Vector2f dir) {
		KeyboardHandler fakedKbHandler = org.mockito.Mockito.mock(CommonKeyboardHandler.class);
		Zildo.pdPlugin.kbHandler = fakedKbHandler;
		when(fakedKbHandler.getDirection()).thenReturn(dir);

		instant.update();
	}

	@After
	public void tearDown() {
		for (Perso perso : EngineZildo.persoManagement.tab_perso) {
			Identified.remove(SpriteEntity.class, perso.getId());
			for (Element elem : perso.getPersoSprites()) {
				Identified.remove(SpriteEntity.class, elem.getId());
			}
		}
		Identified.resetCounter(SpriteEntity.class);
		Identified.clearAll();
	}
}
