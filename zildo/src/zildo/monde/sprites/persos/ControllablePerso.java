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

package zildo.monde.sprites.persos;

/**
 * @author Tchegito
 *
 */

public enum ControllablePerso {
	ZILDO(true, false, true, true, true),	// Inventory - Attack - Pickup - Talk
	PRINCESS_BUNNY(false, true, false, false, true);	// Freejump - Talk

	public static String QUEST_DETERMINING_APPEARANCE = "hero_princess";
	
	public boolean canInventory;	// TRUE=character has an inventory
	public boolean canFreeJump;	// TRUE=character can jump, if player press a button
	public boolean canAttack;
	public boolean canPickup;
	public boolean canTalk;
	
	private ControllablePerso(boolean inventory, boolean freeJump, boolean attack, boolean canPickup, boolean canTalk) {
		this.canInventory = inventory;
		this.canFreeJump = freeJump;
		this.canAttack = attack;
		this.canPickup = canPickup;
		this.canTalk = canTalk;
	}
}
