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

package zildo.monde.quest.actions;

import zildo.SinglePlayer;
import zildo.client.ClientEngineZildo;
import zildo.client.gui.menu.StartMenu;
import zildo.monde.dialog.ActionDialog;
import zildo.server.EngineZildo;

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
		super("Game over ! Merci d'avoir joue a la legende de Zildo.");
	}
	
	@Override
	public void launchAction() {
		// Reset map / GUI
		EngineZildo.mapManagement.deleteCurrentMap();
		ClientEngineZildo.tileEngine.cleanUp();
		ClientEngineZildo.guiDisplay.setToDisplay_generalGui(false);
		ClientEngineZildo.soundPlay.stopMusic();
		// Stop this game
		SinglePlayer.getClientState().gameOver=true;
		// Return on the start menu
		ClientEngineZildo.getClientForMenu().handleMenu(new StartMenu());
	}

}
