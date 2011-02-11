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

// Interface for all sprite description enums

public interface SpriteDescription {

	public int getBank();
	
	public int getNSpr();
	
	public boolean isBlocking();
	
	class Locator {
		public static SpriteDescription findSpr(int nBank, int nSpr) {
			switch (nBank) {
			case SpriteBank.BANK_ZILDO:
				return ZildoDescription.fromInt(nSpr);
			case SpriteBank.BANK_ELEMENTS:
				return ElementDescription.fromInt(nSpr);
			case SpriteBank.BANK_PNJ:
			case SpriteBank.BANK_PNJ2:
				return PersoDescription.fromNSpr(nSpr);
			case SpriteBank.BANK_GEAR:
				return GearDescription.fromNSpr(nSpr);
			default:
			    // Is this bank Zildo with another outfit ?
			    if (nBank >= SpriteBank.BANK_COPYSCREEN) {
				return ZildoDescription.fromInt(nSpr);
			    }
			}
			throw new RuntimeException("Can't find sprite for bank "+nBank);
		}
	}
}
