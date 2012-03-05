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

package zildo.monde.sprites.persos.action;

import zildo.client.sound.BankSound;
import zildo.monde.sprites.persos.Perso;
import zildo.server.EngineZildo;

/**
 * @author Tchegito
 *
 */
public class HealAction implements PersoAction {

	Perso perso;
	int numPv;
	
	public HealAction(Perso p_perso, int p_numPv) {
		perso = p_perso;
		numPv = p_numPv;
		perso.setAttente(1);
	}
	
	@Override
	public boolean launchAction() {
		int pv=perso.getPv();
		if (perso.getMaxpv() == pv || numPv == 0) {
			return true;
		} else {
			if (perso.getAttente() == 0) {
				perso.setPv(pv+1);
				numPv--;
				EngineZildo.soundManagement.broadcastSound(BankSound.ZildoRecupVie, perso);
				perso.setAttente(8 * 2);
			}
		}
		return false;
	}
}
