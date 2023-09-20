/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
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

package zildo.monde.sprites.utils;

import java.util.ArrayList;
import java.util.List;

public enum MouvementPerso {

	// Characters movements
	ZONE(0),
	HEN(1),	// Poule
	OBSERVE(2),
	IMMOBILE(3),	// Not alertable
	VOLESPECTRE(4),
	RAT(5),
	ZONELANCE(6),
	ZONEARC(7),
	ELECTRIC(8),
	BEE(9),
	BIRD(10),
	SQUIRREL(11),
	WAKEUP(12),
	WAITING(13),	// like IMMOBILE, but alertable
	CAT(14),
	SLEEPING(15),
	FOLLOW(16),
	MOBILE_WAIT(17),	// Character will wait if something is on his way (contrary to default mode, where target becomes NULL when he's blocked)
						// Plus: he ask blocking people to leave, except if it's the hero
	//IMMATERIAL(18),	// No collision and untouchable (removed in favor of FlagPerso.F_IMMATERIAL)
	SPITFIRE(19),	// For dragon
	RETRACTED(20),	// For dragon too, when he's diving into lava
	FREEFLY(21),	// For vulture, with z as target
	CHAIN_FOLLOW(22),	// Get target from the followed one 
	MOLE(23),
	HOODED_ATTACK(24);
	
	public int valeur;
	
	private MouvementPerso(int val) {
		this.valeur=val;
	}
	
	public static MouvementPerso fromInt(int a) {
		for (MouvementPerso mvt : MouvementPerso.values()) {
			if (mvt.valeur == a) {
				return mvt;
			}
		}
		throw new RuntimeException("Le script de mouvement "+a+" n'existe pas.");
	}
	
	/**
	 * Does this script make the character collide obstacles ?
	 * @return TRUE = no collision / FALSE = collision with background
	 */
	public boolean isFlying() {
		return this == VOLESPECTRE || this == BEE || this == BIRD || this == FREEFLY;
	}
	
	public static String[] getValues() {
		List<String> str=new ArrayList<String>();
		for (MouvementPerso mvt : values()) {
			str.add(mvt.name());
		}
		return str.toArray(new String[]{});
	}
	
	/**
	 * Does this script make the character focus on his target, or out of his way ? 
	 * @return TRUE = character runs away / FALSE = character runs on his target
	 */
	public boolean isAfraid() {
		return this == HEN || this == CAT;
	}
	/**
	 * Does this script make the character move diagonally ?
	 * @return TRUE = diagonal move / FALSE = lateral move
	 */
	public boolean isDiagonal() {
		return this == HEN ||
		this == VOLESPECTRE ||
		this == ELECTRIC ||
		this == SQUIRREL;
	}
	
	public boolean isOnlyHorizontal() {
		return this == CAT;
	}
	
	/**
	 * Does this script make the character move inside a zone?
	 * @return TRUE = he can move / FALSE = immobile
	 */
	public boolean isMobileZone() {
		return this != IMMOBILE &&
		this != WAITING &&
		this != OBSERVE &&
		this != WAKEUP &&
		this != MOBILE_WAIT &&
		this != SPITFIRE &&
		this != RETRACTED &&
		this != FREEFLY &&
		this != MOLE &&
		this != HOODED_ATTACK;
	}
	
	/**
	 * Does the character looking for Zildo ?
	 * @return TRUE = he look for / FALSE = he can't be in alert
	 */
	public boolean isAlertable() {
		return this != RAT && this != ELECTRIC && this != BEE && this != IMMOBILE && 
				this != SPITFIRE && this != RETRACTED && this != MOLE && this != CHAIN_FOLLOW && this != MOBILE_WAIT &&
				this != HOODED_ATTACK;
	}
}
