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

package zildo.monde.sprites.persos;

import zildo.monde.sprites.Reverse;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.persos.ia.PathFinder;
import zildo.monde.util.Point;
import zildo.monde.util.Pointf;

/**
 * @author Tchegito
 *
 */
public class PersoSpider extends PersoShadowed {

	double attackAngle, attackSpeed;
	boolean opposite;
	
	public PersoSpider(int p_x, int p_y) {
		super(ElementDescription.SHADOW_SMALL, 3);
		x = p_x;
		y = p_y;
		pathFinder = new PathFinder(this);
		pv = 2;
	}
	
	@Override
	public void move() {
		if (pathFinder.getTarget() == null) {
			// Look for a random point
			attackSpeed = 3f + Math.random() * 2.2f;
			if (opposite) {
				attackAngle = attackAngle + Math.PI;
				attackSpeed += 1f;
				opposite = false;
			} else {
				// Look for Zildo
				attackAngle = Math.random() * 2 * Math.PI;
			}
			Point target = new Point((int) (x + attackSpeed * Math.cos(15 * attackAngle)),
								     (int) (y + attackSpeed * Math.sin(15 * attackAngle)) );
			pathFinder.setTarget(target);
		} else {
			Pointf pos = pathFinder.reachDestination((float) attackSpeed);
			if (pos.x == x && pos.y == y) {
				opposite = true;	// Collision, so go in the opposite next time
				pathFinder.setTarget(null);
			} else {
				if (pos.x != Float.NaN && pos.y != Float.NaN) { 
					x = pos.x;
					y = pos.y;
				}
				attackSpeed *= 0.95f;
				if (attackSpeed <= 0.1f) {
					pathFinder.setTarget(null);
				}
			}
		}
	}
	
	@Override
	public void finaliseComportement(int compteur_animation) {
		addSpr = 0;
		if (deltaMoveX != 0) {
			addSpr = 1;
			reverse = deltaMoveX < 0 ? Reverse.NOTHING : Reverse.HORIZONTAL;
		}
		super.finaliseComportement(compteur_animation);
	}
}
