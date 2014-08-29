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
import zildo.monde.items.Item;
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
		INVINCIBILITY(500),
		FIRE_DAMAGE_REDUCED(5000);

		int duration;

		private AffectionKind(int p_duration) {
			duration = p_duration;
		}
	}
	
	int duration;
	final AffectionKind kind;
	final Perso perso;
	Item item;
	
	/** Create an effection with an absolut duration **/
	public Affection(Perso p_perso, AffectionKind p_kind) {
		perso = p_perso;
		kind = p_kind;
		duration = p_kind.duration;
	}
	
	/** Create an affection linked to an item (magic effect with limited duration) **/
	public Affection(Perso p_perso, AffectionKind p_kind, Item p_item) {
		this(p_perso, p_kind);
		if (p_item != null) {
			item = p_item;
			duration = p_item.level;
		}
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

		if (item != null) {	// Synchronize this duration with item's one (mapped on 'level' field)
			item.level = duration;
		}
		return duration <=0;
	}
	
	@Override
	public boolean equals(Object obj) {
		return ((Affection) obj).kind == kind;
	}
}
