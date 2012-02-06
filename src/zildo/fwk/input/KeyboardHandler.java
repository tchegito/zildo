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

package zildo.fwk.input;

import org.lwjgl.input.Keyboard;

/**
 * @author Tchegito
 *
 */
public class KeyboardHandler {

	public static final int KEY_ESCAPE          = 0x01;
	public static final int KEY_BACK            = 0x0E; /* backspace */
	public static final int KEY_TAB             = 0x0F;
	public static final int KEY_Q               = 0x10;
	public static final int KEY_W               = 0x11;
	public static final int KEY_E               = 0x12;
	public static final int KEY_RETURN          = 0x1C; /* Enter on main keyboard */
	public static final int KEY_LSHIFT          = 0x2A;
	public static final int KEY_X               = 0x2D;
	public static final int KEY_UP              = 0xC8; /* UpArrow on arrow keypad */
	public static final int KEY_LEFT            = 0xCB; /* LeftArrow on arrow keypad */
	public static final int KEY_RIGHT           = 0xCD; /* RightArrow on arrow keypad */
	public static final int KEY_DOWN            = 0xD0; /* DownArrow on arrow keypad */
	
	public static Boolean isKeyDown(int p_code) {
		return Keyboard.isKeyDown(p_code);		
	}
	
	public static void poll() {
		Keyboard.poll();
	}
	
	/**
	 * @return true if a keyboard event was read, false otherwise
	 */
	public static boolean next() {
		return Keyboard.next();
	}
	
	public static boolean getEventKeyState() {
		return Keyboard.getEventKeyState();
	}
	
	public static int getEventKey() {
		return Keyboard.getEventKey();
	}
	
	public static char getEventCharacter() {
		return Keyboard.getEventCharacter();
	}
}
