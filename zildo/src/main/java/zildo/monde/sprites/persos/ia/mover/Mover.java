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

import java.util.HashMap;
import java.util.Map;

import zildo.fwk.script.xml.element.TriggerElement;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.SpriteModel;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.elements.ElementPlaceHolder;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.util.Point;
import zildo.monde.util.Pointf;
import zildo.monde.util.Zone;
import zildo.server.EngineZildo;

/**
 * @author Tchegito
 *
 */
public class Mover {


	MoveOrder order;
	Map<Integer, SpriteEntity> linkedEntities;	// All entities carried by the mover
	SpriteEntity mobile;
	Element elemPlaceHolder;	// For physic mover
	int flatZ = 0;	// z-coordinate at which people can meet this mover (useful for character's able to take someone on his back)
	
	/**
	 * @param mobile
	 * @param x
	 * @param y
	 */
	public Mover(SpriteEntity mobile) {
		this.mobile = mobile;
		this.linkedEntities = new HashMap<Integer, SpriteEntity>();
	}

	public Mover(SpriteEntity mobile, int z) {
		this(mobile);
		flatZ = z;
	}
	
	public void reachTarget() {
		float ancX = mobile.x;
		float ancY = mobile.y;
		Pointf delta = order.move();
		if (mobile instanceof Element) {
			((Element)mobile).setAnc(ancX, ancY);
		}
		takePeople(delta);
	}
	
	/** Take people with moving entity (platform, turtle ...) **/
	public void takePeople(Pointf delta) {
		// Move the linked entities
		for (SpriteEntity entity : linkedEntities.values()) {
			// Handle collision for Perso (but is it really necessary ? Maybe for later !)
			// Now we disable this because when Zildo is on the leaf, it should move regardless of
			// Zildo's location
			if (false && entity.getEntityType().isPerso()) {
				Perso p = (Perso) entity;
				Pointf result = p.tryMove(delta.x, delta.y);
				p.x = result.x;
				p.y = result.y;
			} else {
				// Move entity on platform according to the integer part of the platform delta
				int feltX = (int) (Math.floor(mobile.x) - Math.floor(mobile.x-delta.x));
				int feltY = (int) (Math.floor(mobile.y) - Math.floor(mobile.y-delta.y));
				entity.x += feltX;
				entity.y += feltY;
				entity.setAjustedX(entity.getAjustedX() + feltX);
				entity.setAjustedY(entity.getAjustedY() + feltY);
				/* For now, this is not needed. No quest triggers on a character move on a platform.
				if (entity.isZildo()) {
					String mapName = EngineZildo.mapManagement.getCurrentMap().getName();
					TriggerElement trig = TriggerElement.createLocationTrigger(mapName, new Point(entity.x, entity.y), mobile.getName(), -1, entity.floor);
					EngineZildo.scriptManagement.trigger(trig);
				} */
			}
		}
	}
	
	public boolean isActive() {
		return order == null ? false : order.active;
	}
	
	/**
	 * Link an entity to this mover, when entity just gets above this mover.
	 * @param e
	 * @return TRUE if entity is just newly associated (=it wasn't before)
	 */
	public boolean linkEntity(SpriteEntity e) {
		if (elemPlaceHolder != null && !e.getEntityType().isEntity()) {
			elemPlaceHolder.setLinkedPerso((Element) e);
		}
		return linkedEntities.put(e.getId(), e) == null;
	}
	
	/**
	 * Unlink an entity : it isn't on the moving entity anymore.
	 * @param e
	 */
	public void unlinkEntity(SpriteEntity e) {
		linkedEntities.remove(e.getId());
	}
	
	/**
	 * Returns TRUE if the given entity is on the mover.
	 * @param e entity
	 * @return boolean
	 */
	public boolean isOnIt(SpriteEntity e) {
		return linkedEntities.containsKey(e.getId());
	}
	
	public Element getPlaceHolder() {
		if (elemPlaceHolder == null) {
			elemPlaceHolder = new ElementPlaceHolder(mobile);
		}
		return elemPlaceHolder;
	}
	
	public void merge(MoveOrder m) {
		order = m;
		order.init(this);
	}
	
	public int getFlatZ() {
		return flatZ;
	}
	
	public Zone getZone() {
		Point middle = mobile.getCenter();
		SpriteModel model = mobile.getSprModel();
		Zone zz = new Zone(middle.x, middle.y + getFlatZ(), model.getTaille_x(), model.getTaille_y());
		if (mobile.getDesc() == PersoDescription.TURTLE) {
			// Particular case: turtle => only the shell is walkable
			zz.y1 += 2;
			zz.y2 -= 5;
		}
		return zz;
	}
}
