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

package zildo.monde.sprites.persos;

import zildo.monde.Hasard;
import zildo.monde.sprites.Reverse;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.resource.Constantes;

/**
 * @author Tchegito
 *
 */
public class PersoRat extends PersoShadowed {

	int waitingCount;
	
	public PersoRat() {
		super(ElementDescription.SHADOW_SMALL, 2);
		pathFinder.speed = 0;
		pv = 2;
	}

	@Override
	public void destinationReached() {
		attente = 10+Hasard.rand(15);
	}
	
	@Override
	public void animate(int compteur_animation) {
		super.animate(compteur_animation);
	}
	
	@Override
	public void finaliseComportement(int compteur_animation) {
		super.finaliseComportement(compteur_animation);
		
		if (attente != 0) {
			int varying = (attente % (4 * Constantes.speed)) / (2 * Constantes.speed);
			if (angle.isHorizontal()) {
				nSpr = (PersoDescription.RAT.nth(6) + varying) % 128;
			} else {
				nSpr += 1;
				if (varying == 1) {
					reverse = (reverse == Reverse.HORIZONTAL) ? Reverse.NOTHING : Reverse.HORIZONTAL;
				}
			}
		}
		
	}
}
