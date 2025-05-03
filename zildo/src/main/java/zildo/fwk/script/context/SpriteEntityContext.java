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

package zildo.fwk.script.context;

import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.persos.Perso;

/**
 * @author Tchegito
 *
 */
public class SpriteEntityContext extends LocaleVarContext {

	SpriteEntity entity;
	Perso perso;
	
	public SpriteEntityContext(SpriteEntity p_entity) {
		entity = p_entity;
		if (entity != null && Perso.class.isAssignableFrom(entity.getClass())){
			perso = (Perso) entity;
		}
	}
	
	// TODO: refactor this more smoothly
	public SpriteEntityContext(SpriteEntity p_entity, IEvaluationContext ctx) {
		this(p_entity);
		if (ctx != null && LocaleVarContext.class.isAssignableFrom(ctx.getClass())) {
			cloneLocales((LocaleVarContext) ctx);
		}
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
		} else if (key.length() == 10) {
			if ("deltaMoveX".equals(key)) {
				return ((Perso)entity).deltaMoveX;
			} else if ("deltaMoveY".equals(key)) {
				return ((Perso)entity).deltaMoveY;
			}
		} else {
			if ("attente".equals(key)) {
				return perso.getAttente();
			} else if ("angle".equals(key)) {
				return (float) perso.getAngle().value;
			} else if ("floor".equals(key)) {
				return perso.floor;
			}
		}
		// Don't crash ! But result could be weird
		return 0;
	}
	
	public SpriteEntity getActor() {
		return perso;
	}
}
