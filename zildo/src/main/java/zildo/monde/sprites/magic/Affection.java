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

package zildo.monde.sprites.magic;

import zildo.client.sound.BankSound;
import zildo.monde.items.Item;
import zildo.monde.sprites.persos.Perso;
import zildo.resource.Constantes;
import zildo.server.EngineZildo;

/**
 * An affection is any kind of magic cast on a character : invincibility, poison, quad damage ...
 * Here, we deals with duration, sound, and the rest.
 * 
 * @author Tchegito
 *
 */
public class Affection {

	public enum AffectionKind {
		INVINCIBILITY(500),
		FIRE_DAMAGE_REDUCED(5000),
		SLOWNESS(100);

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
		case INVINCIBILITY:
			if ( (kind.duration - duration) % 100 == 0) {
				EngineZildo.soundManagement.broadcastSound(BankSound.Invincible, perso);
			}
			break;
		case SLOWNESS:
			if (duration == 1) {
				perso.walkTile(false);	// Restore the expected light
				perso.setSpeed(Constantes.ZILDO_SPEED);
			} else {
				int[] poisonColor = {0x30, 0xd5, 0xc8}; // {0x44, 0xff, 0x33};
				int color = (poisonColor[0] << 16) + (poisonColor[1] << 8) + poisonColor[2];
				float factor = 1;
				// Manual easing-in-out
				if (duration > kind.duration - 16) {
					factor = (kind.duration - duration) / 16f;
				} else if (duration < 16) {
					factor = duration / 16f;
				}
				
				if (factor != 1) {
					color =   ((int) (255-(255-poisonColor[0]) * factor) << 16)
							+ ((int) (255-(255-poisonColor[1]) * factor) << 8)
							+ ((int) (255-(255-poisonColor[2]) * factor));

					//System.out.println("Factor="+factor + ZUtils.hexa(color));
				}
				perso.setLight(color);
				perso.setSpeed(0.5f);
			}
			default:
			break;
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
