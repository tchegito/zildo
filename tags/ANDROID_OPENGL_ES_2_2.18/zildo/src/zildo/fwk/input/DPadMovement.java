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

package zildo.fwk.input;

import zildo.monde.Trigo;
import zildo.monde.util.Pointf;
import zildo.monde.util.Vector2f;

/**
 * @author Tchegito
 *
 */
public class DPadMovement {
	final static float[] forces = {0.2f, 0.6f, 1}; 
	
	float direction;
	
	public static Vector2f compute(int px, int py) {
		Vector2f v = new Vector2f(0,0);
		// 0 <= px <= 80 
		// 0 <= py <= 80
		double distance = Pointf.pythagore(px, py);
		if (distance == 0) {
			return null;
		}
		
		// Get angle and round it
		double angle = Trigo.getAngleRadianWithDistance(0, 0, px, py, distance);
		double rounded = Math.round(angle / Trigo.PI_SUR_4) * Trigo.PI_SUR_4;
		
		int ppx = (int) (distance * Math.cos(rounded));
		int ppy = (int) (distance * Math.sin(rounded));
		float ratio = (float) distance / 40;
		float clamped = 0;
		for (float force : forces) {
			clamped = force;
			if (ratio <= force){
				break;
			}
		}
		v.set(clamped * (ppx / distance), clamped * (ppy / distance));
		return v; 
	}
}
