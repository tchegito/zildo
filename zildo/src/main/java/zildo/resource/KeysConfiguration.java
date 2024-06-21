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

package zildo.resource;

import zildo.fwk.input.KeyboardHandler.Keys;

// All these keys should be send to server.
// Its lead to player's movement/action.

public enum KeysConfiguration {

	PLAYERKEY_ACTION(Keys.Q),
	PLAYERKEY_ATTACK(Keys.W),
	PLAYERKEY_INVENTORY(Keys.X),
	PLAYERKEY_UP(Keys.UP),
	PLAYERKEY_DOWN(Keys.DOWN),
	PLAYERKEY_RIGHT(Keys.RIGHT),
	PLAYERKEY_LEFT(Keys.LEFT),
	PLAYERKEY_TOPIC(Keys.E),
	PLAYERKEY_TAB(Keys.TAB),
	PLAYERKEY_ADVENTUREMENU(Keys.COMPASS),
	PLAYERKEY_DIALOG(Keys.DIALOG_FRAME),
	PLAYERKEY_BACK(Keys.TOUCH_BACK);
	
	public Keys code;
	
	KeysConfiguration(Keys p_code) {
		this.code=p_code;
	}
}
