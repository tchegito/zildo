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

// Interface for all sprite description enums

public interface SpriteDescription {

	public int getBank();
	
	public int getNSpr();
	
	public boolean isBlocking();

	public boolean isPushable();
	
	public boolean isDamageable();
	
	default boolean isBurnable() {
		return false;
	}
	
	class Locator {
		public static SpriteDescription findSpr(int nBank, int nSpr) {
			switch (nBank) {
			case SpriteBank.BANK_ZILDO:
				return ZildoDescription.fromInt(nSpr);
			case SpriteBank.BANK_ELEMENTS:
				return ElementDescription.fromInt(nSpr);
			case SpriteBank.BANK_FONTES:
				return null;
			case SpriteBank.BANK_PNJ:
				return PersoDescription.fromNSpr(nSpr);
			case SpriteBank.BANK_PNJ2:
				return PersoDescription.fromNSpr(nSpr > 127 ? nSpr : nSpr + 128);
			case SpriteBank.BANK_PNJ3:
				return PersoDescription.fromNSpr(nSpr + 259);
			case SpriteBank.BANK_PNJ4:
				return PersoDescription.fromNSpr(nSpr + 259 + 109);
			case SpriteBank.BANK_PNJ5:
				return PersoDescription.fromNSpr(nSpr + 259 + 109 + 96);
			case SpriteBank.BANK_GEAR:
				return GearDescription.fromNSpr(nSpr);
			default:
			    // Is this bank Zildo with another outfit ?
			    if (nBank >= SpriteBank.BANK_ZILDOOUTFIT) {
				return ZildoDescription.fromInt(nSpr);
			    }
			}
			throw new RuntimeException("Can't find sprite for bank "+nBank+" and sprite "+nSpr);
		}
		
		/**
		 * Return a SpriteDescription from all enums extending this interface, except Zildo.
		 * Try to avoid this method in runtime (prefer prerender phase).
		 * @param val
		 * @return SpriteDescription
		 */
		public static SpriteDescription findNamedSpr(String val) {
			SpriteDescription desc = null;
			try {
				desc = ElementDescription.valueOf(val);
			} catch (IllegalArgumentException e) {
				try {
					desc = GearDescription.valueOf(val);
				} catch (IllegalArgumentException e2) {
					try {
						desc = PersoDescription.valueOf(val);
					} catch (IllegalArgumentException e3) {
						desc =FontDescription.valueOf(val);
					}
				}
			}
			return desc;
		}
	}
	
	public int ordinal();
	
	public boolean isNotFixe();
	
	/**
	 * Is this sprite supposed to slip on the ground/water ? Basically, an element stops as soon as it
	 * collides. But some of them (water leaf for example), may slip on the water, along the coast borders.
	 * @return
	 */
	public boolean isSliping();
	
	/** Returns TRUE if element must be displayed as an entity => non blocking and drawn before elements.
	 * Useful for lava patch **/
	public boolean isOnGround();
	
	/** Returns TRUE if we should spawn an impact sprite when collided **/
	public boolean doesImpact();
	
	public int getRadius();
}
