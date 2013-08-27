/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
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

package zildo.monde.quest.actions;

import zildo.client.Client;
import zildo.client.ClientEngineZildo;
import zildo.client.stage.CreditStage;
import zildo.client.stage.SinglePlayer;
import zildo.fwk.gfx.filter.FilterEffect;
import zildo.fwk.gfx.filter.LightningFilter;
import zildo.fwk.gfx.filter.RedFilter;
import zildo.fwk.ui.UIText;
import zildo.monde.dialog.ActionDialog;
import zildo.server.EngineZildo;
import zildo.server.state.ClientState;

/**
 * When Zildo dies in single player.
 * <p/>
 * Displays a message and shut down the single player.<br/>
 * Back to the start menu.
 * 
 * @author tchegito
 *
 */
public class GameOverAction extends ActionDialog {

	public GameOverAction() {
		super(UIText.getGameText("game.over"));
	}
	
	/**
	 * @param p_clientState if NULL, it means that player has won the game.
	 */
	@Override
	public void launchAction(ClientState p_clientState) {
		ClientEngineZildo.filterCommand.restoreFilters();
		ClientEngineZildo.filterCommand.active(RedFilter.class, false, null);
		ClientEngineZildo.filterCommand.active(LightningFilter.class, false, null);
		ClientEngineZildo.mapDisplay.foreBackController.setDisplaySpecific(true, true);

		
		if (p_clientState == null) {
			// Keep music and launch credits
			finishGame();
			Client client = ClientEngineZildo.getClientForMenu();
			client.quitGame();
            client.askStage(new CreditStage(true));
		} else {
			// Get player back at his entry point in the current room
			EngineZildo.scriptManagement.userEndAction();
			EngineZildo.soundManagement.setForceMusic(false);
			String mapName = EngineZildo.mapManagement.getCurrentMap().getName();
			EngineZildo.mapManagement.loadMap(mapName, false);
			// Just to reinitialize the tile engine optimizations
			ClientEngineZildo.mapDisplay.setCurrentMap(EngineZildo.mapManagement.getCurrentMap());
        	EngineZildo.persoManagement.getZildo().setPv(1);
        	EngineZildo.mapManagement.respawn(0);
        	ClientEngineZildo.filterCommand.fadeIn(FilterEffect.CIRCLE);
    		ClientEngineZildo.guiDisplay.setToDisplay_generalGui(true);

			//ClientEngineZildo.soundPlay.stopMusic();
			//client.handleMenu(new StartMenu());
		}
	}

	private void finishGame() {
		// Reset map / GUI
		EngineZildo.mapManagement.deleteCurrentMap();
		ClientEngineZildo.tileEngine.cleanUp();
		ClientEngineZildo.guiDisplay.setToDisplay_generalGui(false);
		ClientEngineZildo.filterCommand.fadeIn(FilterEffect.SEMIFADE);
		
		// Stop this game
		SinglePlayer.getClientState().gameOver=true;
		// Return on the start menu


	}
}
