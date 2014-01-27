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

package zeditor.tools.builder;

import zildo.monde.map.Area;
import zildo.monde.map.Case;
import zildo.monde.map.Tile;
import zildo.monde.sprites.Rotation;
import zildo.monde.sprites.SpriteEntity;
import zildo.server.EngineZildo;
import zildo.server.MapManagement;

/**
 * Adjust rotations state, because enum was wrongly designed.
 * COUNTERCLOCKWISE and UPSIDEDOWN value have to be swapped to get a correct sequence order.
 * 
 * NOTE:can cause damage => just run once !
 * 
 * @author Tchegito
 *
 */
public class AdjustRotations extends AllMapProcessor {

	boolean isReplacements = false;
	
	@Override
	public boolean run() {
		MapManagement mapManagement = EngineZildo.mapManagement;
		Area area = mapManagement.getCurrentMap();

		isReplacements = false;
		for (int y = 0 ; y < area.getDim_y() ; y++) {
			for (int x = 0 ; x < area.getDim_x() ; x++) {
				Case c = area.get_mapcase(x, y);
				
				if (c != null) {
					adjustTile(c.getBackTile());
					adjustTile(c.getBackTile2());
					adjustTile(c.getForeTile());
				}
			}
		}
		
		for (SpriteEntity entity : EngineZildo.spriteManagement.getSpriteEntities(null)) {
			if (entity.rotation == Rotation.COUNTERCLOCKWISE) {
				entity.rotation = Rotation.UPSIDEDOWN;
				markReplacement();
			} else if (entity.rotation == Rotation.UPSIDEDOWN) {
				entity.rotation = Rotation.COUNTERCLOCKWISE;
				markReplacement();
			}
		}
		if (isReplacements) {
			System.out.println("replacements !");
		}
		return isReplacements;
	}
	
	private void adjustTile(Tile t) {
		if (t == null) {
			return;
		}
		if (t.rotation == Rotation.COUNTERCLOCKWISE) {
			t.rotation = Rotation.UPSIDEDOWN;
			markReplacement();
		} else if (t.rotation == Rotation.UPSIDEDOWN) {
			t.rotation = Rotation.COUNTERCLOCKWISE;
			markReplacement();
		}
	}
	
	private void markReplacement() {
		isReplacements = true;
		System.out.println("got it !");
	}
}
