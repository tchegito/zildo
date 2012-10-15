/**
 * Legend of Zildo
 * Copyright (C) 2006-2012 Evariste Boussaton
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

import zildo.fwk.bank.SpriteBank;
import zildo.monde.sprites.Reverse;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.desc.SpriteDescription;
import zildo.monde.sprites.desc.ZildoDescription;

public class Sprite {

	public SpriteDescription spr;	// Identify the sprite (Bank + sprite number)
	public Reverse reverse;				// Reverse (horizontal and/or vertical)
	
	public Sprite(int p_nSpr, int p_nBank, Reverse p_reverse) {
		switch (p_nBank) {
		case SpriteBank.BANK_ELEMENTS:
			spr=ElementDescription.fromInt(p_nSpr);
			break;
		case SpriteBank.BANK_ZILDO:
			spr=ZildoDescription.fromInt(p_nSpr);
			break;
		}
		reverse=p_reverse;
	}
}
