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

import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.server.EngineZildo;

/**
 * Simply replace hearts by "drop on the floor" on all maps.
 * @author Tchegito
 *
 */
public class ReplaceSpritesModel extends AllMapProcessor {

	/* (non-Javadoc)
	 * @see zeditor.tools.builder.AllMapProcessor#run()
	 */
	@Override
	protected boolean run() {
		boolean adjustments = false;
		for (SpriteEntity entity : EngineZildo.spriteManagement.getSpriteEntities(null)) {
			if (entity.getDesc() == ElementDescription.HEART) {
				entity.setDesc(ElementDescription.DROP_FLOOR);
				adjustments = true;
			}
		}
		
		return adjustments;
	}

}
