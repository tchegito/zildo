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

package zildo.monde.sprites.persos.ia;

import zildo.monde.Hasard;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.util.Point;
import zildo.monde.util.Pointf;

/**
 * @author Tchegito
 * 
 */
public class PathFinderBee extends PathFinder {

	/**
	 * @param p_mobile
	 */
	public PathFinderBee(Perso p_mobile) {
		super(p_mobile);
	}

	/**
	 * Determine destination for SCRIPT_ABEILLE.
	 */
	@Override
	public void determineDestination() {
		float x = mobile.x;
		float y = mobile.y;
		target = new Point();
		target.x = (int) (x + (5.0f + Math.random() * 10.0f)
				* Math.cos(2.0f * Math.PI * Math.random()));
		target.y = (int) (y + (5.0f + Math.random() * 10.0f)
				* Math.sin(2.0f * Math.PI * Math.random()));
	}

	@Override
	public Pointf reachDestination(float p_speed) {
		Pointf pos = super.reachDestination(p_speed);

		// Introduce noise
		double alpha = Math.random() * 2 * Math.PI;
		double dx = Math.cos(alpha) * p_speed;
		double dy = Math.sin(alpha) * p_speed;

		pos.add((float) dx, (float) dy);

		mobile.z = 10 + Hasard.intervalle(2);

		return pos;
	}
}
