/**
 * Legend of Zildo
 * Copyright (C) 2006-2011 Evariste Boussaton
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

package zildo.monde.sprites.persos.ia;

import zildo.monde.map.Point;
import zildo.monde.map.Pointf;
import zildo.monde.sprites.persos.Perso;
import zildo.server.EngineZildo;

/**
 * @author Tchegito
 *
 */
public class PathFinderStraightFlying extends PathFinder {

	float alpha;
	
	/**
	 * @param p_mobile
	 */
	public PathFinderStraightFlying(Perso p_mobile) {
		super(p_mobile);
		speed = 1.5f;
	}
	
	public void determineDestination() {
		// A simple bird flying straight toward the map's border.
		target=new Point(mobile.x, mobile.y);
		switch (mobile.getAngle()) {
			case EST:
			case SUD:
				target.x=-100;
				break;
			default:
				target.x = 16 * EngineZildo.mapManagement.getCurrentMap().getDim_x() + 100;
				break;
		}
		mobile.z=30f;	// Up in the sky
	}
	
	public Pointf reachDestination(float p_speed) {
		Pointf p =super.reachDestination(speed);
		
		// Swing the bird !
		alpha+=0.07f;
		mobile.z+= (float) (0.6f * Math.cos(alpha));
		return p;
	}

}
