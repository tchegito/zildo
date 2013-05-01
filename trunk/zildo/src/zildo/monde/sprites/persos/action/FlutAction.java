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

package zildo.monde.sprites.persos.action;

import zildo.client.sound.BankSound;
import zildo.monde.Hasard;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.persos.PersoZildo;
import zildo.monde.sprites.utils.MouvementZildo;
import zildo.monde.util.Angle;
import zildo.server.EngineZildo;

/**
 * @author Tchegito
 *
 */
public class FlutAction implements PersoAction {

	PersoZildo perso;
	
	public FlutAction(PersoZildo zildo) {
		perso = zildo;
		EngineZildo.soundManagement.playSound(BankSound.Flut, perso);
		perso.setAttente(110);
		perso.setAngle(Angle.SUD);
		perso.setMouvement(MouvementZildo.PLAYING_FLUT);
	}
	
	@Override
	public boolean launchAction() {
		if (perso.getAttente() < 2) {
			perso.setMouvement(MouvementZildo.VIDE);
			return true;
		} else if (perso.getAttente() % 40 == 0) {
			// Drop a note
			Element element = new Element();
			element.x = perso.x-6;
			element.y = perso.y;
			element.z = 4;
			element.vx = 0.2f+0.1f*(float) Math.random();
			element.ax = -0.01f;
			element.az = 0.015f; // + rnd()*0.005f);
			element.fx = 0.04f * (float) Math.random();
			
			element.alphaA = -0.06f;
			
			ElementDescription desc = ElementDescription.NOTE;
			if (Hasard.lanceDes(5)) {
				desc = ElementDescription.NOTE2;
			}
			element.setSprModel(desc);

			element.setScrX((int) element.x);
			element.setScrY((int) element.y);

			element.setForeground(true);
			
			EngineZildo.spriteManagement.spawnSprite(element);
		}
		return false;
	}
}
