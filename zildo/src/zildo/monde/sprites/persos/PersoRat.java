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

package zildo.monde.sprites.persos;

import zildo.monde.Hasard;
import zildo.monde.sprites.Reverse;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.util.Point;
import zildo.resource.Constantes;
import zildo.server.EngineZildo;

/**
 * Used for both RAT and BIG_RAT.
 * 
 * @author Tchegito
 *
 */
public class PersoRat extends PersoNJ {

	int waitingCount;
	boolean bigRat;
	
	final int BITE_DISTANCE = 12;
	
	public PersoRat(PersoDescription p_desc) {
		super();
		pathFinder.speed = 1.5f;
		bigRat = p_desc == PersoDescription.BIG_RAT;

		pv = bigRat ? 3 : 2;
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
			if (bigRat) { // Big rat crawls randomly, but run on hero if he sees him
				Perso zildo = EngineZildo.persoManagement.lookFor(this, 5, PersoInfo.ZILDO);
				if (zildo != null) {
					float dx = x - zildo.x;
					float dy = y - zildo.y;
					if (dy > dx || dx < BITE_DISTANCE) {	// Rat runs quickly on Zildo
						pathFinder.setTarget(new Point(zildo.x, zildo.y));
					} else {
						// Runs just in front of him to bite
						Point front = new Point(zildo.x + BITE_DISTANCE * Math.signum(dx), zildo.y);
						pathFinder.setTarget(front);
					}
				}
			} else { // Small rat has a variation sprite, and crawls randomly
				int varying = (attente % (4 * Constantes.speed)) / (2 * Constantes.speed);
				if (angle.isHorizontal()) {
					nSpr = PersoDescription.RAT.nth(6) + varying;
				} else {
					nSpr += 1;
					if (varying == 1) {
						reverse = (reverse == Reverse.HORIZONTAL) ? Reverse.NOTHING : Reverse.HORIZONTAL;
					}
				}
			}
		}
		
	}
}
