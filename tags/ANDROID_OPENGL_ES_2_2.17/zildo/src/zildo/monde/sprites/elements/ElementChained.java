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

package zildo.monde.sprites.elements;

import java.util.ArrayList;
import java.util.List;

import zildo.monde.util.Point;
import zildo.server.EngineZildo;

/**
 * Abstract class designed for chained elements : when an animation needs
 * several elements created on a time period at a given place.
 * 
 * @author Tchegito
 * 
 */
public abstract class ElementChained extends Element {

	protected boolean endOfChain = false;	// To allow subclasses to stop the chain creation
	
	int count = 0;
	int delay = 0;
	Point initialLocation;

	List<Element> linkeds;

	public ElementChained(int p_x, int p_y) {
		x = p_x;
		y = p_y;
		initialLocation = new Point(p_x, p_y);

		linkeds = new ArrayList<Element>();
		visible = false;
	}

	@Override
	public void animate() {

		if (!endOfChain) {
			if (count >= delay) { // After the delay, create another one
				Element elem = createOne((int) x, (int) y);
	
				linkeds.add(elem);
				EngineZildo.spriteManagement.spawnSprite(elem);
				
				count = 0;
			}
			count++;
		} else {
			// Detect if chained element are over
			if (linkeds.get(0).dying) {
				linkeds.remove(0);
			}
			if (linkeds.isEmpty()) {
				dying = true;
			}
		}
		
		if (mover != null && mover.isActive()) {
			// Moving is delegated to another object
			mover.reachTarget();
		}
			
		// Move the burning fire sprite along with this one
		if (burningFire != null) {
			if (dying) {
				burningFire.dying = true;
			} else {
				burningFire.x = linkeds.get(0).x;
				burningFire.y = linkeds.get(0).y;
			}
		}
	}

	/**
	 * Delegate method designed for creating an element.<br/>
	 * It needs to set a delay too.<br/>
	 * 
	 * @param p_x
	 * @param p_y
	 * @return Element
	 */
	protected abstract Element createOne(int p_x, int p_y);

	@Override
	public boolean fall() {
		return true;
	}
}
