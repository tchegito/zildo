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

import zildo.monde.sprites.persos.Perso;
import zildo.monde.util.Point;
import zildo.monde.util.Pointf;
import zildo.server.EngineZildo;

/**
 * This path finder is always unstoppable.
 * 
 * @author Tchegito
 * 
 */
public class PathFinderStraightFlying extends PathFinder {

	float alpha;
	float swingAmplitude;
	float swingBase;
	
	/**
	 * @param p_mobile
	 */
	public PathFinderStraightFlying(Perso p_mobile, float p_swingBase, float p_swingAmplitude) {
		super(p_mobile);
		speed = 1.5f;
		swingAmplitude = p_swingAmplitude;
		swingBase = p_swingBase;
		unstoppable=true;
	}

	@Override
	public void determineDestination() {
		// A simple bird flying straight toward the map's border.
		target = new Point(mobile.x, mobile.y);
		switch (mobile.getAngle()) {
		case EST:
		case SUD:
			target.x = -100;
			break;
		default:
			target.x = 16 * EngineZildo.mapManagement.getCurrentMap().getDim_x() + 100;
			break;
		}
		mobile.z = swingBase; // Up in the sky
	}

	@Override
	public Pointf reachDestination(float p_speed) {
		Pointf p = reachLine(p_speed, true);

		// Swing the bird !
		alpha += 0.07f;
		mobile.z = swingBase + (float) (swingAmplitude * Math.cos(alpha));
		return p;
	}

	@Override
	public void setUnstoppable(boolean unstoppable) {
		// Nothing because this 'PathFinder' is always unstoppable
	}
}
