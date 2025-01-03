/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
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

import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.util.Point;
import zildo.server.EngineZildo;

/**
 * Abstract class designed for chained elements : when an animation needs
 * several elements created on a time period at a given place.
 * 
 * Note that created elements will be named "<initialName>_<n>" where n will start from 0.
 * 
 * @author Tchegito
 * 
 */
public abstract class ElementChained extends Element {

	protected boolean endOfChain = false;	// To allow subclasses to stop the chain creation
	
	private int count = 0;
	protected int delay = 0;
	protected boolean follow = false;	// True means the whole chain follow the leader (first one created)
	
	List<Point> leadLocations;	// For following (trail effect)
	List<Element> linkeds;

	public ElementChained(int p_x, int p_y) {
		x = p_x;
		y = p_y;

		linkeds = new ArrayList<Element>();
		visible = false;
	}

	@Override
	public void animate() {
		for (Element e : linkeds) {
			e.setAlpha(e.getAlpha() + alphaV);
		}
		
		if (!endOfChain) {
			if (count >= delay) { // After the delay, create another one
				Element elem = createOne((int) x, (int) y);

				elem.setName(name+"_"+linkeds.size());
				
				if (elem.getDesc() == ElementDescription.FIRE_SPIRIT1) {
					elem.flying = true;
				}
				
				linkeds.add(elem);
				EngineZildo.spriteManagement.spawnSprite(elem);
				
				count = 0;
			}
			count++;
		} else if (!follow) {
			// Detect if chained element are over
			if (linkeds.get(0).dying) {
				linkeds.remove(0);
			}
			if (linkeds.isEmpty()) {
				dying = true;
			}
		}
		
		if (follow && !linkeds.isEmpty()) {
			if (linkeds.stream().allMatch(e -> e.dying)) {
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
			} else if (!linkeds.isEmpty()) {
				burningFire.x = linkeds.get(0).x;
				burningFire.y = linkeds.get(0).y;
			}
		}
		
		if (follow && linkeds.size() > 0) {
			if (leadLocations == null) leadLocations = new ArrayList<Point>();
			leadLocations.add(new Point(x, y));
			int index = leadLocations.size() - delay;
			if (index >= 0) {
				for (Element e : linkeds) {
					if (leadLocations.size() <= index || index < 0) {
						break;
					}
					Point ref = leadLocations.get(index);
					e.x = ref.x;
					e.y = ref.y;
					e.zoom = zoom;
					e.reverse = reverse;
					index -= delay;
				}
			}
			if (endOfChain) {
				// Limit locations list
				int max = delay * linkeds.size();
				if (leadLocations.size() > max) {
					leadLocations.remove(0);
				}
			}
		}
	}
	
	public boolean contains(Element toSearch) {
		return !linkeds.isEmpty() && linkeds.stream().anyMatch(e -> e == toSearch);
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
		if (!linkeds.isEmpty()) {
			linkeds.get(0).fall();
		}
		return true;
	}
}
