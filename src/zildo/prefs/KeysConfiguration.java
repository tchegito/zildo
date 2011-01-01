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

package zildo.prefs;

import org.lwjgl.input.Keyboard;

// All these keys should be send to server.
// Its lead to player's movement/action.

public enum KeysConfiguration {

	PLAYERKEY_ACTION(Keyboard.KEY_Q),
	PLAYERKEY_ATTACK(Keyboard.KEY_W),
	PLAYERKEY_INVENTORY(Keyboard.KEY_X),
	PLAYERKEY_UP(Keyboard.KEY_UP),
	PLAYERKEY_DOWN(Keyboard.KEY_DOWN),
	PLAYERKEY_RIGHT(Keyboard.KEY_RIGHT),
	PLAYERKEY_LEFT(Keyboard.KEY_LEFT),
	PLAYERKEY_TOPIC(Keyboard.KEY_E),
	PLAYERKEY_TAB(Keyboard.KEY_TAB);
	
	public int code;
	
	private KeysConfiguration(int p_code) {
		this.code=p_code;
	}
}
