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
import zildo.monde.sprites.SpriteEntity;

/**
 * @author Tchegito
 * 
 */
public class SpriteSelection<T extends SpriteEntity> extends Selection {

	final List<T> sprites;
	final List<EngineFX> initial;
	boolean multi = false;

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
		for (SpriteEntity e : p_entities) {
			initial.add(e.getSpecialEffect());
		}
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

	public void reverse(boolean p_horizontal) {
		for (SpriteEntity e : sprites) {
			reverseEntity(e, p_horizontal);
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
		for (SpriteEntity e : sprites) {
			int diffx = (int) e.x;
			e.x = 16 * (int) (e.x / 16) + p_value;
			diffx -= e.x;
			e.setAjustedX(e.getAjustedX() - diffx);
		}
	}

	public void addY(int p_value) {
		for (SpriteEntity e : sprites) {
			int diffy = (int) e.y;
			e.y = 16 * (int) (e.y / 16) + p_value;
			diffy -= e.y;
			e.setAjustedY(e.getAjustedY() - diffy);
		}
	}

}
