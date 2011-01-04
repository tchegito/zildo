/**
 * Legend of Zildo
 * Copyright (C) 2006-2011 Evariste Boussaton
 * Based on original Zelda : link to the past (C) Nintendo 1992
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
	
	GEAR_GREENDOOR, GEAR_GREENDOOR_OPENING;
	
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
}
