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

package zildo.monde.sprites.desc;

import zildo.fwk.bank.SpriteBank;

/**
 * @author Tchegito
 *
 */
public enum GearDescription implements SpriteDescription {
	
	GREEN_DOOR(true), GREEN_DOOR_OPENING(false),
	GREEN_SIMPLEDOOR(true), GREEN_SIMPLEDOOR_OPENING(false),
	
	PRISON_GRATE, PRISON_GRATESIDE,
	
	BOULDER,
	
	CAVE_SIMPLEDOOR, CAVE_MASTERDOOR(true), CAVE_KEYDOOR(true),
	CAVE_KEYDOOR_OPENING(false),

	BOULDER2,
	
	BIG_BLUE_DOOR,
	
	CRACK1, CRACK2,	// Broken walls
	
	GRATE(true), GRATE_OPENING(false),
	
	HIDDENDOOR(true), HIDDENDOOR_OPENING(false),
	LAVASPIKE, LAVA1, LAVA2, LAVA3,
	PALACE_DOOR(true), PALACE_DOOR_OPENING(false);

	// Desc used only on doors (initialized with a boolean)
	private GearDescription openingDoor = null;
	private GearDescription closedDoor = null;
	
	static {
		// We can call this only when all enum members are initialized (and just once)
		for (GearDescription d : GearDescription.values()) {
			if (d.closedDoor != null) {
				d.openingDoor = GearDescription.values()[d.ordinal() + 1];
			} else if (d.openingDoor != null) {
				d.closedDoor = GearDescription.values()[d.ordinal() - 1];
			}
		}
		CAVE_MASTERDOOR.openingDoor = CAVE_KEYDOOR_OPENING;
	}
	
	private GearDescription() {
	}
	private GearDescription(boolean p_door) {
		if (p_door) {
			closedDoor = this;
		} else {
			openingDoor = this;
		}
	}
	
	public int getBank() {
		return SpriteBank.BANK_GEAR;
	}
		
	public int getNSpr() {
		return ordinal();
	}
	
	/**
	 * Return gear's identity from given integer value.
	 * @param nSpr
	 * @return GearDescription
	 */
	public static GearDescription fromNSpr(int nSpr) {
		return values()[nSpr];
	}

	@Override
	public boolean isBlocking() {
		switch (this) {
			case GREEN_DOOR:
			case GREEN_DOOR_OPENING:
			case CAVE_SIMPLEDOOR:
			case CAVE_KEYDOOR:
			case BOULDER:
			case CAVE_MASTERDOOR:
			case GREEN_SIMPLEDOOR:
			case GREEN_SIMPLEDOOR_OPENING:
			case GRATE:
			case BIG_BLUE_DOOR:
			case HIDDENDOOR:
			case PALACE_DOOR:
				return true;
			default:
				return false;
		}

	}
	
	@Override
	public boolean isOnGround() {
		switch (this) {
		case LAVA1:
		case LAVA2:
		case LAVA3:
		case LAVASPIKE:
			return true;
		default:
			return false;
		}
	}
	
	public boolean isExplodable() {
		switch (this) {
		case CRACK1:
		case CRACK2:
		case BOULDER:
			return true;
		default:
			return false;
		}
	}

	@Override
	public boolean isDamageable() {
		return false;
	}
	
	@Override
	public boolean isPushable() {
		return false;
	}
	
	public boolean isNotFixe() {
		return false;
	}
	
	@Override
	public boolean isSliping() {
		return false;
	}

	public GearDescription getOpeningDesc() {
		return openingDoor;
	}
	
	public GearDescription getClosedDesc() {
		return closedDoor;
	}
	
	public int getRadius() {
		return 7;
	}
	
	@Override
	public boolean doesImpact() {
		return false;
	}
}
