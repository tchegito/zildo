/**
 * Legend of Zildo
 * Copyright (C) 2006-2011 Evariste Boussaton
 * Based on original Zelda : link to the past (C) Nintendo 1992
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
import zildo.fwk.gfx.PixelShaders.EngineFX;
import zildo.monde.map.Point;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.SpriteModel;
import zildo.server.EngineZildo;

/**
 * @author Tchegito
 * 
 */
public class SpriteSelection<T extends SpriteEntity> extends Selection {

	final List<T> sprites;
	final List<EngineFX> initial;

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
		sprites.addAll(p_entities);
		for (SpriteEntity e : sprites) {
			initial.add(e.getSpecialEffect());
		}
		calculateOriginAndSize();
	}

	@Override
	public List<T> getElement() {
		return sprites;
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
		boolean isHorizontal = (entity.reverse & SpriteEntity.REVERSE_HORIZONTAL) != 0;
		boolean isVertical = (entity.reverse & SpriteEntity.REVERSE_VERTICAL) != 0;

		if (p_horizontal) {
			isHorizontal = !isHorizontal;
		} else {
			isVertical = !isVertical;
		}

		entity.reverse = isVertical ? SpriteEntity.REVERSE_VERTICAL : 0;
		entity.reverse |= isHorizontal ? SpriteEntity.REVERSE_HORIZONTAL : 0;
	}

	public void toggleForeground() {
		for (SpriteEntity e : sprites) {
			e.setForeground(!e.isForeground());
		}
	}

	public void addX(int p_value) {
		origin.x+=p_value;
		for (SpriteEntity e : sprites) {
			int diffx = (int) e.x;
			e.x = 16 * (int) (e.x / 16) + p_value;
			diffx -= e.x;
			e.setAjustedX(e.getAjustedX() - diffx);
		}
	}

	public void addY(int p_value) {
		origin.y+=p_value;
		for (SpriteEntity e : sprites) {
			int diffy = (int) e.y;
			e.y = 16 * (int) (e.y / 16) + p_value;
			diffy -= e.y;
			e.setAjustedY(e.getAjustedY() - diffy);
		}
	}

	public void place(Point p_point) {
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
        			elem.animate();
        		}
		}
		calculateOriginAndSize();
	}
	
	private void calculateOriginAndSize() {
		origin = new Point(500, 500);
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
