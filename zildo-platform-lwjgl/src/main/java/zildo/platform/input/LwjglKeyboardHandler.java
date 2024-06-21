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

package zildo.platform.input;

import java.util.EnumMap;

import org.lwjgl.input.Keyboard;

import zildo.Zildo;
import zildo.client.ClientEngineZildo;
import zildo.fwk.input.CommonKeyboardHandler;
import zildo.monde.util.Vector2f;
import zildo.resource.KeysConfiguration;

/**
 * @author Tchegito
 *
 */
public class LwjglKeyboardHandler extends CommonKeyboardHandler {

	final static EnumMap<Keys, Integer> platformKeys = new EnumMap<Keys, Integer>(Keys.class);
	
	private static final int KEY_ESCAPE          = 0x01;
	private static final int KEY_BACK            = 0x0E; /* backspace */
	private static final int KEY_TAB             = 0x0F;
	private static final int KEY_G				 = 0x22;
	private static final int KEY_Q               = 0x10;
	private static final int KEY_W               = 0x11;
	private static final int KEY_E               = 0x12;
	private static final int KEY_RETURN          = 0x1C; /* Enter on main keyboard */
	public static final int KEY_LSHIFT          = 0x2A;
	private static final int KEY_X               = 0x2D;
	private static final int KEY_UP              = 0xC8; /* UpArrow on arrow keypad */
	private static final int KEY_LEFT            = 0xCB; /* LeftArrow on arrow keypad */
	private static final int KEY_RIGHT           = 0xCD; /* RightArrow on arrow keypad */
	private static final int KEY_DOWN            = 0xD0; /* DownArrow on arrow keypad */
	private static final int KEY_R               = 0x13;
	private static final int KEY_T               = 0x14;
	private static final int KEY_2				= 40; //253;
	private static final int KEY_PAGEUP			= 201;
	private static final int KEY_PAGEDOWN		= 209;
		
	Vector2f v = new Vector2f(0, 0);	// Just to avoid declaring a new one at each time
	
	static {
		platformKeys.put(Keys.BACK, KEY_BACK);
		platformKeys.put(Keys.ESCAPE, KEY_ESCAPE);
		platformKeys.put(Keys.TAB, KEY_TAB);
		platformKeys.put(Keys.Q, KEY_Q);
		platformKeys.put(Keys.W, KEY_W);
		platformKeys.put(Keys.E, KEY_E);
		platformKeys.put(Keys.RETURN, KEY_RETURN);
		platformKeys.put(Keys.LSHIFT, KEY_LSHIFT);
		platformKeys.put(Keys.X, KEY_X);
		platformKeys.put(Keys.UP, KEY_UP);
		platformKeys.put(Keys.LEFT, KEY_LEFT);
		platformKeys.put(Keys.RIGHT, KEY_RIGHT);
		platformKeys.put(Keys.DOWN, KEY_DOWN);
		platformKeys.put(Keys.DIALOG_FRAME, KEY_E);	// Nonsense without touch screen
		platformKeys.put(Keys.TOUCH_MENU, KEY_R);	// Nonsense without touch screen
		platformKeys.put(Keys.TOUCH_BACK, KEY_T);	// Nonsense without touch screen
		platformKeys.put(Keys.COMPASS, KEY_2);
		platformKeys.put(Keys.PAGEUP, KEY_PAGEUP);
		platformKeys.put(Keys.PAGEDOWN, KEY_PAGEDOWN);
		platformKeys.put(Keys.GEAR, KEY_G);	// Irrelevant on LWJGL
	}

	public boolean isKeyDown(int p_code) {
		if (Zildo.replayMovements) {
			KeysConfiguration keyCode = null;
			switch (p_code) {
			case KEY_LEFT: keyCode = KeysConfiguration.PLAYERKEY_LEFT; break;
			case KEY_RIGHT: keyCode = KeysConfiguration.PLAYERKEY_RIGHT; break;
			case KEY_UP: keyCode = KeysConfiguration.PLAYERKEY_UP; break;
			case KEY_DOWN: keyCode = KeysConfiguration.PLAYERKEY_DOWN; break;
			
			}
			if (keyCode == null) return false;
			return ClientEngineZildo.client.getKbInstant().isKeyDown(keyCode);
		} else {
			return Keyboard.isKeyDown(p_code);
		}
	}
	
	public void poll() {
		super.poll();
		Keyboard.poll();
	}
	
	@Override
	public Vector2f getDirection() {
		float deltaX=0, deltaY=0;
		if (isKeyDown(Keys.LEFT)) {
			deltaX-=1;
		} else if (isKeyDown(Keys.RIGHT)) {
			deltaX+=1;
		}

		// UP/DOWN key
		if (isKeyDown(Keys.UP)) {
			deltaY-=1;
		} else if (isKeyDown(Keys.DOWN)) {
			deltaY+=1;
		}
		
		if (deltaX ==0 && deltaY == 0) {
			return null;
		}
		v.set(deltaX, deltaY);
		v.normalize(1);
		return v;	
	}
	
	/**
	 * @return true if a keyboard event was read, false otherwise
	 */
	public boolean next() {
		return Keyboard.next();
	}
	
	public boolean getEventKeyState() {
		return Keyboard.getEventKeyState();
	}
	
	public int getEventKey() {
		return Keyboard.getEventKey();
	}
	
	public char getEventCharacter() {
		return Keyboard.getEventCharacter();
	}
	
	public int getCode(Keys k) {
		return platformKeys.get(k);
	}
}
