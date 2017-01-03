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

package zildo.monde.sprites.elements;

import zildo.client.sound.BankSound;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.persos.Perso;
import zildo.server.EngineZildo;

public class ElementAnimMort extends Element {

	int count;
	
	/**
	 * Crée un sprite de mort lié au personnage
	 * @param perso
	 */
    public ElementAnimMort(Perso perso) {
        super();
        z = 8.0f;
        count=0;
        nSpr = 33;
        setLinkedPerso(perso);
    }

    @Override
	public void animate() {

        count++;

        super.animate();
        byte seq_mort[] = { 33, 35, 34, 36, 37, 38, 39, 0 };

        // Animation de la mort d'un perso
        x = x - vx;
        y = y - vy;
        SpriteEntity link = getLinkedPerso();
        if (getLinkedPerso() != null) {
            Perso perso = (Perso) link;
            x = perso.getX();
            y = perso.getY();
            if (nSpr == 36) {
                perso.hide();
            } else if (nSpr == 38) {
                link.dying=true;
                setLinkedPerso(null);
            }
        }
        if (count >= 6 && count < 7) {
            EngineZildo.soundManagement.broadcastSound(BankSound.MonstreMeurt, this);
        }
        int nextSpr=seq_mort[count/6];
        if (nextSpr == 0) {
            dying=true;
        } else {
        	nSpr=nextSpr;
        }
    }
}