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

package zildo.monde.sprites.persos.ia;

import zildo.monde.sprites.persos.Perso;
import zildo.monde.util.Angle;
import zildo.monde.util.Pointf;

/**
 * @author Tchegito
 *
 */
public class PathFinderGreenBlob extends PathFinder {
	
	/**
	 * @param p_mobile
	 */
	public PathFinderGreenBlob(Perso p_mobile) {
		super(p_mobile);
		speed = 0f;
	}
	
	private static final float[] coeff={0.1f, 0.1f, 0.1f, 0.2f,0.5f};
	
	@Override
	public Pointf reachDestination(float p_speed) {
		int nspr = mobile.getAddSpr();
		float mulSpeed = 1f * coeff[nspr];
		Pointf p = super.reachDestination(mulSpeed);
		if (p.x < mobile.x) {
			mobile.setAngle(Angle.OUEST);
		}
		return p;
	}
}
