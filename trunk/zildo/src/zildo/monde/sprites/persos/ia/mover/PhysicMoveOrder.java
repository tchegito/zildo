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

import zildo.monde.sprites.elements.Element;
import zildo.monde.util.Pointf;

/**
 * @author Tchegito
 *
 */
public class PhysicMoveOrder extends MoveOrder {

	/**
	 * Build a physic mover with (x,y) as speed vector coordinates.
	 * @param mobile
	 * @param x
	 * @param y
	 */
	public PhysicMoveOrder(int x, int y) {
		super(x, y);
		
	}

	@Override
	protected Pointf move() {
		Element placeHolder = wrapper.elemPlaceHolder;
		placeHolder.animate();

		Pointf p = new Pointf(placeHolder.x - mobile.x, placeHolder.y - mobile.y);
		mobile.x = placeHolder.x;
		mobile.y = placeHolder.y;
		
		if (Math.abs(placeHolder.vx) <= 0.01 && 
			Math.abs(placeHolder.vy) <= 0.01 && 
			Math.abs(placeHolder.vz) <= 0.01) {
			active = false;
		}
		return p;
	}
	
	@Override
	void init(Mover p_wrapper) {
		super.init(p_wrapper);
		Element placeHolder = p_wrapper.getPlaceHolder();
		placeHolder.x = mobile.x;
		placeHolder.y = mobile.y;
		placeHolder.vx += target.x;
		placeHolder.vy += target.y;
		placeHolder.setDesc(mobile.getDesc());
		// Random friction vector
		placeHolder.fx = 0.1f;
		placeHolder.fy = 0.1f;
	}
	
}
