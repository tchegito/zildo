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

package zildo.fwk.script.logic;

import zildo.monde.sprites.SpriteEntity;

/**
 * @author Tchegito
 *
 */
public class SpriteEntityContext implements IEvaluationContext {

	SpriteEntity entity;
	
	public SpriteEntityContext(SpriteEntity p_entity) {
		entity = p_entity;
	}
	@Override
	public float getValue(String key) {
		if (key.length() == 1) {	// Filter length to avoid too much comparisons
			if ("x".equals(key)) {
				return entity.x;
			} else if ("y".equals(key)) {
				return entity.y;
			} else if ("z".equals(key)) {
				return entity.z;
			}
		}
		// Don't crash ! But result could be weird
		return 0;
	}

}
