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

package zildo.monde.sprites.magic;

import zildo.client.sound.BankSound;
import zildo.monde.sprites.persos.Perso;
import zildo.server.EngineZildo;
import zildo.server.MultiplayerManagement;

/**
 * An affection is any kind of magic cast on a character : invincibility, poison, quad damage ...
 * Here, we deals with duration, sound, and the rest.
 * 
 * @author Tchegito
 *
 */
public class Affection {

	public enum AffectionKind {
		QUAD_DAMAGE(MultiplayerManagement.QUAD_TIME_DURATION),
		INVINCIBILITY(500);

		int duration;

		private AffectionKind(int p_duration) {
			duration = p_duration;
		}
	}
	
	int duration;
	final AffectionKind kind;
	Perso perso;
	
	public Affection(Perso p_perso, AffectionKind p_kind) {
		perso = p_perso;
		kind = p_kind;
		duration = p_kind.duration;
	}
	
	public boolean render() {
		
		// Specific behavior
		switch (kind) {
		case QUAD_DAMAGE:
			if (duration == 160) {
				EngineZildo.soundManagement.broadcastSound(BankSound.QuadDamageLeaving, perso);
			}		
			break;
		case INVINCIBILITY:
			if ( (kind.duration - duration) % 100 == 0) {
				EngineZildo.soundManagement.broadcastSound(BankSound.Invincible, perso);
			}
		}
		duration--;

		return duration <=0;
	}
}
