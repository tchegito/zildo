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

package zildo.monde.collision;

import zildo.monde.Hasard;
import zildo.monde.sprites.magic.Affection.AffectionKind;
import zildo.monde.sprites.persos.Perso;

public enum DamageType {

	// NOTE: All causes a damage from 0 to 1, except FIRE, which causes 2.
	// Interesting to have another scale of damage depending on the weapon. We could add or multiply such damages scores.
	
	HARMLESS,	// Useful for peebles
	BLUNT, // Boomerang
	PEEBLE, // From peeble
	PIERCING, // Arrow
	CUTTING, // Sword
	CUTTING_FRONT, // Sword just in front of character
	EXPLOSION, // Bomb
	SMASH, // Hammer
	FIRE,	// Fire (big damage)
	POISON;
	
	public boolean isCutting() {
		return CUTTING==this || CUTTING_FRONT==this || EXPLOSION==this; 
	}
	
	// Calculate the right HP loss, considering character's affections
	public int getHP(Perso damagedPerso) {
		switch (this) {
		case FIRE:
		case EXPLOSION:
			int val = 2;
			if (damagedPerso.isAffectedBy(AffectionKind.FIRE_DAMAGE_REDUCED)) {
				val >>= 1;
			}
			return val;
		case HARMLESS:
			return 0;
		case PEEBLE:
			return Math.max(0, Hasard.rangeInt(-1, 1));
		default:
			return 1;
		}
	}
}
