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
 * Movement which goes from initial location to given one.
 * 
 * To do so, we determine an angle between PI/4 and PI/2
 * @author Tchegito
 *
 */
public class CircularMoveOrder extends MoveOrder {

	Pointf center;
	Pointf radius;
	double factor;
	double iota;
	int pasX, pasY;
	
	Element mobileElement;
	
	final int nbFrames = 100;
	
	/**
	 * Build a circular mover with (x,y) as target coordinate.
	 * @param mobile
	 * @param x
	 * @param y
	 */
	public CircularMoveOrder(int x, int y) {
		super(x, y);
	}

	@Override
	protected Pointf move() {
		double angle = factor * iota;
		mobileElement.vx = (float) ( radius.x * Math.cos(angle) * pasX * factor);
		mobileElement.vy = (float) (-radius.y * Math.sin(angle) * pasY * factor);
		mobileElement.physicMoveWithCollision();
		//mobile.animate();

		/*
		Pointf p = new Pointf(placeHolder.x - mobile.x, placeHolder.y - mobile.y);
		mobile.x = placeHolder.x;
		mobile.y = placeHolder.y;
*/
		if (iota == nbFrames) {
			target = null;
			active = false;
			mobileElement.vx = 0;
			mobileElement.vy = 0;
		} else {
			iota++;
		}

		return new Pointf(mobile.x, mobile.y);
	}
	
	@Override
	void init(Mover p_wrapper) {
		super.init(p_wrapper);
		
		if (mobile.getEntityType().isElement()) {
			mobileElement = (Element) mobile;
		}
		center = new Pointf(target.x, mobile.y);
		radius = new Pointf(Math.abs(target.x - mobile.x), 
							Math.abs(target.y - mobile.y));
		pasX = target.x > mobile.x ? 1 : -1;
		pasY = target.y > mobile.y ? -1 : 1;
		factor = Math.PI/2 / nbFrames;
		iota = 0;
	}
}
