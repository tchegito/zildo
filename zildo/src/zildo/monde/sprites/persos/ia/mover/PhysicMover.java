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

package zildo.monde.sprites.persos.ia.mover;

import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.elements.Element;
import zildo.monde.util.Pointf;

/**
 * @author Tchegito
 *
 */
public class PhysicMover extends Mover {

	Element placeHolder;
	
	/**
	 * Build a physic mover with (x,y) as speed vector coordinates.
	 * @param mobile
	 * @param x
	 * @param y
	 */
	public PhysicMover(SpriteEntity mobile, int x, int y) {
		super(mobile, x, y);
		placeHolder = new Element();
		placeHolder.x = mobile.x;
		placeHolder.y = mobile.y;
		placeHolder.vx = x;
		placeHolder.vy = y;
		placeHolder.setDesc(mobile.getDesc());
		// Random friction vector
		placeHolder.fx = 0.1f;
		placeHolder.fy = 0.1f;
	}

	@Override
	protected Pointf move() {
		placeHolder.animate();

		Pointf p = new Pointf(placeHolder.x - mobile.x, placeHolder.y - mobile.y);
		mobile.x = placeHolder.x;
		mobile.y = placeHolder.y;
		
		if (Math.abs(placeHolder.vx) <= 0.01 && Math.abs(placeHolder.vy) <= 0.01 && Math.abs(placeHolder.vz) <= 0.01) {
			active = false;
		}
		return p;
	}
	
}
