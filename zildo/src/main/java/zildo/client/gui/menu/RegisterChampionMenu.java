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

package zildo.client.gui.menu;

import java.util.ArrayList;
import java.util.Date;

import zildo.fwk.net.www.WorldRegister;
import zildo.fwk.ui.ItemMenu;
import zildo.fwk.ui.Menu;
import zildo.fwk.ui.UIText;
import zildo.fwk.ui.UnselectableItemMenu;
import zildo.monde.Champion;
import zildo.monde.quest.actions.GameOverAction;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.resource.Constantes;
import zildo.server.EngineZildo;

/**
 * @author Tchegito
 *
 */
public class RegisterChampionMenu extends Menu {

	public RegisterChampionMenu() {
		super("m12.title");
		
		items = new ArrayList<ItemMenu>();
		// Try to register, when life was so tender ...
		if (tryRegister()) {
			// Success : 
			items.add(new UnselectableItemMenu("m12.ok.info") { });
			items.add(new ItemMenu("m12.butOk") {
				@Override
				public void run() {
           			// And finish
           			new GameOverAction().launchAction(null);

				}
			});
		} else {
			items.add(new UnselectableItemMenu("m12.ko.info") { });
			items.add(new ItemMenu("m12.ko.retry") {
				@Override
				public void run() {
					// Try again
					client.handleMenu(new RegisterChampionMenu());
				}
			});
			items.add(new ItemMenu("m12.ko.cancel") {
				@Override
				public void run() {
           			// And finish
           			new GameOverAction().launchAction(null);

				}
			});
		}
		setMenu(items);
	}
	
	/**
	 * Send registration to Zildo server for current player.
	 * @return boolean
	 */
	private boolean tryRegister() {
		PersoPlayer zildo = EngineZildo.persoManagement.getZildo();
		int timeSpent = EngineZildo.game.getTimeSpent();
		int moonHalf = zildo.getMaxpv() * 2; // + zildo.getMoonHalf();
		Champion ch = new Champion(UIText.getCharacterName(), moonHalf, Constantes.currentEpisode, new Date(), zildo.getMoney(), timeSpent);
		return EngineZildo.worldRegister.registerChampion(ch);
	}
}
