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

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;

import zildo.client.Client;
import zildo.client.ClientEngineZildo;
import zildo.client.MapDisplay;
import zildo.client.gui.GUIDisplay;
import zildo.fwk.bank.TileBank;
import zildo.fwk.db.Identified;
import zildo.fwk.gfx.engine.TileEngine;
import zildo.monde.Game;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.desc.ZildoOutfit;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoNJ;
import zildo.monde.sprites.persos.PersoZildo;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
import zildo.monde.util.Pointf;
import zildo.server.EngineZildo;
import zildo.server.state.ClientState;

/**
 * @author Tchegito
 *
 */
public class EngineUT {

	protected EngineZildo engine;
	protected ClientEngineZildo clientEngine;
	
	protected List<ClientState> clients;
	protected MapUtils mapUtils;	// To easily manipulate the map
	
	protected Perso spawnTypicalPerso(String name, int x, int y) {
		Perso perso = new PersoNJ();
		perso.x = x;
		perso.y = y;
		perso.setDesc(PersoDescription.BANDIT_CHAPEAU);
		perso.setAngle(Angle.NORD);
		perso.setName(name);
		EngineZildo.spriteManagement.spawnPerso(perso);
		return perso;
	}
	
	protected Perso spawnZildo(int x, int y) {
		Perso perso = new PersoZildo(x, y, ZildoOutfit.Zildo);
		perso.x = x;
		perso.y = y;
		perso.setDesc(PersoDescription.BANDIT_CHAPEAU);
		perso.setAngle(Angle.NORD);
		perso.setName("zildo");
		EngineZildo.spriteManagement.spawnPerso(perso);
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
	
	protected void assertLocation(Element elem, Point target, boolean isAt) {
		String entityType = elem.getEntityType().toString();
		if (isAt) {
			Assert.assertTrue(entityType+" should have been at "+target, 
					(int) elem.x == target.x &&
					(int) elem.y == target.y);		
		} else {
			Assert.assertTrue(entityType+" shouldn't have been at "+target, 
					(int) elem.x != target.x || 
					(int) elem.y != target.y);
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
	
	protected void updateGame() {
		ClientState state = clients.get(0);
		engine.renderEvent(state.event);
		if (clientEngine == null) {
			clientEngine = new ClientEngineZildo(null, false, mock(Client.class));
			clientEngine.guiDisplay = mock(GUIDisplay.class);
			clientEngine.mapDisplay = spy(new MapDisplay(mapUtils.area));
			doNothing().when(clientEngine.mapDisplay).centerCamera();
			clientEngine.tileEngine = mock(TileEngine.class);
		}
		state.event = engine.renderEvent(state.event);
		state.event = clientEngine.renderEvent(state.event);
		renderFrames(1);
	}

	protected void renderFrames(int nbFrame) {
		renderFrames(nbFrame, true);
	}
	
	protected void renderFrames(int nbFrame, boolean debugInfos) {
		for (int i=0;i<nbFrame;i++) {
			engine.renderFrame(clients);
			if (debugInfos) {
				for (Perso perso : EngineZildo.persoManagement.tab_perso) {
					System.out.println("Perso: "+perso.getName()+" at "+perso.x+","+perso.y);
				}
			}
		}		
	}
	
	@Before
	public void setUp() {
		Game game = new Game(null, "hero");
		game.editing = true;
		engine = new EngineZildo(game);
		// Create standard map
		//EngineZildo.soundManagement.setForceMusic(true);
		EngineZildo.mapManagement.loadMap("preintro", false);
		
		// Cheat to have a client
		Client client =mock(Client.class);
		when(client.isIngameMenu()).thenReturn(false);
		ClientEngineZildo.client = client; 
		// Fake a client state list
		ClientState clState = new ClientState(null, 1);
		clients = Arrays.asList(clState);

		// Tile collision
		for (String bankName : TileEngine.tileBankNames) {
			TileBank motifBank = new TileBank();

			motifBank.charge_motifs(bankName);
		}
		
		mapUtils = new MapUtils();
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
