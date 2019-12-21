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

package zildo.monde.sprites.persos.action;

import zildo.client.sound.BankSound;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.elements.ElementArrow;
import zildo.monde.sprites.persos.Perso;
import zildo.server.EngineZildo;

/**
 * A character shot an arrow in front of him.
 * @author Tchegito
 *
 */
public class ShotArrowAction implements PersoAction {

	Perso perso;

	public ShotArrowAction(Perso p_perso) {
		perso = p_perso;
		perso.setAttente(60 + EngineZildo.hasard.rand(30));
	}
	
	@Override
	public boolean launchAction() {
		if (perso.getAttente() <= 2*8) {	// Sometimes 'attente' field is decremented by 2 instead of 1, so we never reach 2*8
			Element weapon=perso.getEn_bras();	// Should NOT be nul !
			int xx, yy;
			if (weapon != null) {
				xx = (int) weapon.getX();
				yy = (int) weapon.getY();
			} else {	// Not tested yet
				xx = (int) perso.x;
				yy = (int) perso.y;
			}
			EngineZildo.soundManagement.broadcastSound(BankSound.FlecheTir, perso);
			Element arrow=new ElementArrow(perso.getAngle(), xx, yy, 0, perso);
			EngineZildo.spriteManagement.spawnSprite(arrow);
			return true;	// End action
		}
		return false;
	}

}
