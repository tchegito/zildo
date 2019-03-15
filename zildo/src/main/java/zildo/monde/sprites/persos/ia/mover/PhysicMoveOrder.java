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

package zildo.monde.sprites.persos.ia.mover;

import zildo.monde.sprites.elements.Element;
import zildo.monde.util.Pointf;
import zildo.monde.util.Vector2f;

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
	public PhysicMoveOrder(float x, float y) {
		super(x, y);
	}

	@Override
	protected Pointf move() {
		Element placeHolder = wrapper.elemPlaceHolder;
		placeHolder.animate();

		Pointf p = new Pointf(placeHolder.x - mobile.x, placeHolder.y - mobile.y);
		boolean stopped = (p.x == 0 && p.y == 0);
		mobile.x = placeHolder.x;
		mobile.y = placeHolder.y;
		
		if (stopped || (Math.abs(placeHolder.vx) <= 0.1 && 
			Math.abs(placeHolder.vy) <= 0.1 && 
			Math.abs(placeHolder.vz) <= 0.1) ) {
			placeHolder.vx = 0; placeHolder.vy = 0; placeHolder.vz = 0;
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
		Vector2f newSpeed = new Vector2f(placeHolder.vx + target.x, placeHolder.vy + target.y);
		Vector2f speed = new Vector2f(placeHolder.vx, placeHolder.vy);
		speed.add(newSpeed).normalize(2.5f);
		placeHolder.vx = speed.x;
		placeHolder.vy = speed.y;
		placeHolder.setDesc(mobile.getDesc());
		// Random friction vector
		placeHolder.fx = 0.03f;
		placeHolder.fy = 0.03f;
	}
	
}
