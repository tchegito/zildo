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

package zeditor.core.selection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import zeditor.windows.subpanels.SelectionKind;
import zildo.fwk.gfx.EngineFX;
import zildo.monde.sprites.Reverse;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.SpriteModel;
import zildo.monde.util.Point;
import zildo.server.EngineZildo;

/**
 * @author Tchegito
 * 
 */
public class SpriteSelection<T extends SpriteEntity> extends Selection {

	final List<T> sprites;
	final List<EngineFX> initial;
	final List<Point> initialLocations;	// Useful with list of entities, when user's playing with the addX/addY feature
	
	Point origin;
	Point corner;
	
	@Override
	public SelectionKind getKind() {
		return SelectionKind.SPRITES;
	}

	public SpriteSelection(T p_entity) {
		this(Collections.singletonList(p_entity));
	}

	public SpriteSelection(List<T> p_entities) {
		sprites = new ArrayList<T>();
		initial = new ArrayList<EngineFX>();
		initialLocations = new ArrayList<Point>();
		sprites.addAll(p_entities);
		for (SpriteEntity e : sprites) {
			initial.add(e.getSpecialEffect());
			initialLocations.add(new Point(e.x, e.y));
		}
		calculateOriginAndSize();
	}

	@Override
	public List<T> getElement() {
		return sprites;
	}

	@Override
	public void focus() {
		for (SpriteEntity e : sprites) {
			e.setSpecialEffect(EngineFX.SHINY);
		}
	}
	
	@Override
	public void unfocus() {
		Iterator<EngineFX> it = initial.iterator();
		for (SpriteEntity e : sprites) {
			e.setSpecialEffect(it.next());
		}
	}

	/** Reverse all entities, horizontally or vertically. */
	public void reverse(boolean p_horizontal) {
		for (SpriteEntity e : sprites) {
			reverseEntity(e, p_horizontal);
			// reverse location
			if (p_horizontal) {
				e.x = 2*origin.x + corner.x - e.x;
			} else {
				// Axial symetry
				int diffY = 2*origin.y + corner.y - (int) (2*e.y);
				// Add size because gravity center was at the bottom of the element => so at the top of the symetric one
				diffY+=e.getSprModel().getTaille_y();
				e.y+= diffY;
				e.setAjustedY(e.getAjustedY() + diffY);
			}
		}
	}

	private void reverseEntity(SpriteEntity entity, boolean p_horizontal) {
		boolean isHorizontal = entity.reverse.isHorizontal();
		boolean isVertical = entity.reverse.isVertical();

		if (p_horizontal) {
			isHorizontal = !isHorizontal;
		} else {
			isVertical = !isVertical;
		}

		entity.reverse = Reverse.fromBooleans(isHorizontal, isVertical);
	}

	public void toggleForeground() {
		for (SpriteEntity e : sprites) {
			e.setForeground(!e.isForeground());
		}
	}

	public void addX(int p_value) {
		origin.x+=p_value;
		if (sprites.size() == 1) {
			SpriteEntity e = sprites.get(0);
			int diffx = (int) e.x;
			e.x = 16 * (int) (e.x / 16) + p_value;
			diffx -= e.x;
			e.setAjustedX(e.getAjustedX() - diffx);
		} else {
			// List of entities
			for (int i=0;i<sprites.size();i++) {
				SpriteEntity e = sprites.get(i);
				Point p = initialLocations.get(i);
				int diffx = (int) e.x;
				e.x = p.x + p_value;
				diffx -= e.x;
				e.setAjustedX(e.getAjustedX() - diffx);
			}
		}
	}

	public void addY(int p_value) {
		origin.y+=p_value;
		if (sprites.size() == 1) {
			SpriteEntity e = sprites.get(0);
			int diffy = (int) e.y;
			e.y = 16 * (int) (e.y / 16) + p_value;
			diffy -= e.y;
			e.setAjustedY(e.getAjustedY() - diffy);
		} else {
			// List of entities
			for (int i=0;i<sprites.size();i++) {
				SpriteEntity e = sprites.get(i);
				Point p = initialLocations.get(i);
				int diffy = (int) e.y;
				e.y = p.y + p_value;
				diffy -= e.y;
				e.setAjustedY(e.getAjustedY() - diffy);
			}
		}
	}
	
	public void setRepeatX(int p_value) {
		for (SpriteEntity e : sprites) {
			e.repeatX = (byte) p_value;
		}
	}

	public void setRepeatY(int p_value) {
		for (SpriteEntity e : sprites) {
			e.repeatY = (byte) p_value;
		}
	}
	
	public void setFloor(int floor) {
		sprites.forEach(s -> s.setFloor(floor));
	}
	
	/** Put the whole current sprite selection on the map, taking selected floor into account **/
	public void place(Point p_point, int floor) {
		boolean first=true;
		Point delta = new Point(0,0);

		for (SpriteEntity elem : sprites) {
        		// Note the delta
        		if (first) {
        		    delta.x = (int) (p_point.x - elem.x);
        		    delta.y = (int) (p_point.y - elem.y);
        		    elem.x=p_point.x;
        		    elem.y=p_point.y;
        		    first = false;
        		} else {
        		    elem.x+=delta.x;
        		    elem.y+=delta.y;
        		}

        		elem.setAjustedX(elem.getAjustedX() + delta.x);
        		elem.setAjustedY(elem.getAjustedY() + delta.y);
        		if (!EngineZildo.spriteManagement.isSpawned(elem)) {
        			EngineZildo.spriteManagement.spawnSprite(elem);
        			if (elem.getEntityType().isElement()) {
        				elem.animate();
        			} else {
        				SpriteEntity entity = elem;
        				SpriteModel sprModel = entity.getSprModel();
        				entity.setAjustedX( (int) entity.x - (sprModel.getTaille_x() >> 1) );
        				entity.setAjustedY( (int) entity.y - (sprModel.getTaille_y() >> 1) );
        			}
        		}
        		elem.setFloor(floor);
		}
		calculateOriginAndSize();
	}
	
	private void calculateOriginAndSize() {
		origin = new Point(1200, 1200);
		corner = new Point(0, 0);
		SpriteModel model;
		for (SpriteEntity e : sprites) {
			model = e.getSprModel();
			int refX = e.getAjustedX();
			int refY = e.getAjustedY();
			origin.x = Math.min(origin.x, refX - model.getTaille_x() / 2);
			origin.y = Math.min(origin.y, refY - model.getTaille_y());
			corner.x = Math.max(corner.x, refX + model.getTaille_x() / 2);
			corner.y = Math.max(corner.y, refY);
		}
		corner.x-=origin.x;
		corner.y-=origin.y;
	}
}
