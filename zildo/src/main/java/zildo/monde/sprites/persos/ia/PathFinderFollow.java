/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
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

import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.util.Point;
import zildo.monde.util.Pointf;

/**
 * @author Tchegito
 *
 */
public class PathFinderFollow extends PathFinder {

	Element followed;
	
	public PathFinderFollow(Perso p_mobile, Element p_followed) {
		super(p_mobile);
		followed = p_followed;
	}
	
	@Override
	public void determineDestination() {
		// Detect if followed element has moved
		Pointf prev = followed.getDelta();
		if (prev.x != 0 || prev.y != 0) {
			target = new Point(followed.x, followed.y);
		}
	}
	
    @Override
    public Pointf reachDestination(float p_speed) {
    	return reachLine(p_speed, false);
    }
}
