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

package zildo.fwk.input;

import zildo.monde.util.Vector2f;

/**
 * @author Tchegito
 *
 */
public interface KeyboardHandler {

	public boolean isKeyDown(int p_code);
	public boolean isKeyDown(Keys key);
	public boolean isKeyPressed(Keys key);
	
	// Called once per frame
	public void poll();
	
	/**
	 * @return true if a keyboard event was read, false otherwise
	 */
	public boolean next();
	
	public boolean getEventKeyState();
	
	public int getEventKey();
	
	public char getEventCharacter();
	
	public Vector2f getDirection();
	
	public enum Keys {Q, W, X, E, UP, DOWN, RIGHT, LEFT, TAB, RETURN, BACK, ESCAPE, LSHIFT,
		PAGEUP, PAGEDOWN,	// For PC Only
		// Specific for touch screen
		DIALOG_FRAME, TOUCH_MENU, TOUCH_BACK, COMPASS};
	public int getCode(Keys k);
	
}
