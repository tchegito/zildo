/**
 * Legend of Zildo
 * Copyright (C) 2006-2012 Evariste Boussaton
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

package zildo.platform.input;

import java.util.EnumMap;

import zildo.Zildo;
import zildo.fwk.input.KeyboardHandler;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
import zildo.monde.util.Zone;

/**
 * @author Tchegito
 *
 */
public class AndroidKeyboardHandler implements KeyboardHandler {

	// Relations between given key and its location on screen, inside virtual pad
	enum KeyLocation {
		VP_UP(29, 11, 23, 19, true, KEY_UP),
		VP_LEFT(11, 29, 19, 23, true, KEY_LEFT),
		VP_RIGHT(51, 29, 19, 23, true, KEY_RIGHT),
		VP_DOWN(29, 52, 23, 19, true, KEY_DOWN),
		// diagonals
		VP_UP_LEFT(11, 11, 18, 18, true, KEY_UP, KEY_LEFT),
		VP_UP_RIGHT(51, 11, 18, 18, true, KEY_UP, KEY_RIGHT),
		VP_DOWN_LEFT(11, 52, 18, 18, true, KEY_DOWN, KEY_LEFT),
		VP_DOWN_RIGHT(51, 52, 18, 18, true, KEY_DOWN, KEY_RIGHT),
		VP_BUTTON_A(4, 33, 26, 26, false, KEY_Q),
		VP_BUTTON_B(36, 58, 26, 26, false, KEY_W),
		VP_BUTTON_C(36, 8, 26, 26, false, KEY_X);
		
		public final Zone z;
		public final int keyCode;
		public final int keyCode2;
		
		private KeyLocation(int x, int y, int wx, int wy, boolean isDirection, int... keys) {
			int addX = 0;
			int addY = Zildo.viewPortY - 80;
			if (!isDirection) {
				addX = Zildo.viewPortX - 98;
				addY = Zildo.viewPortY - 87;
			}
			z = new Zone(x + addX, y + addY, wx, wy);
			keyCode = keys[0];
			if (keys.length > 1) {
				keyCode2 = keys[1];
			} else {
				keyCode2 = -1;
			}
		}
	}
	
	final static EnumMap<Keys, Integer> platformKeys = new EnumMap<Keys, Integer>(Keys.class);
	boolean[] keyStates = new boolean[255];
	
	private static final int KEY_ESCAPE          = 0x01;
	private static final int KEY_BACK            = 0x0E; /* backspace */
	private static final int KEY_TAB             = 0x0F;
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
	}
	
	TouchPoints polledTouchedPoints;
	//TouchMovement tm;
	boolean resetBack;
	AndroidInputInfos infos;
	
	public AndroidKeyboardHandler() {
		polledTouchedPoints = new TouchPoints();
		infos = new AndroidInputInfos();
	}
	
	public void setAndroidInputInfos(AndroidInputInfos infos) {
		this.infos = infos;
	}
	
	static final int middleX = Zildo.viewPortX / 2;
	static final int middleY = Zildo.viewPortY / 2;
	
	public boolean isKeyDown(int p_code) {
		if (p_code == KEY_ESCAPE) {
			if (infos.backPressed) {
				resetBack = true;
			}
			return infos.backPressed;
		}
		
		return keyStates[p_code];
	}
	
	Angle previous;
	
	public void poll() {
		// Clear all keys state
		for (int i : platformKeys.values()) {
			keyStates[i] = false;
		}
		
		if (resetBack) {
			infos.backPressed = false;	// Reinitialize back button press
			resetBack = false;
		}
		polledTouchedPoints.clear();
		if (infos.liveTouchedPoints.size() != 0) {
			polledTouchedPoints.clear();
			synchronized (infos.liveTouchedPoints) {
				polledTouchedPoints.putAll(infos.liveTouchedPoints);
			}

			// Update all keys state
			for (Point p : polledTouchedPoints.getAll()) {
				for (KeyLocation kLoc : KeyLocation.values()) {
					if (kLoc.z.isInto(p.x, p.y)) {
						keyStates[kLoc.keyCode] = true;
						if (kLoc.keyCode2 != -1) {
							keyStates[kLoc.keyCode2] = true;
						}
					}
				}
			}
		}
		//tm.render();
		//previous = tm.getCurrent();
		

	}
	
	/**
	 * @return true if a keyboard event was read, false otherwise
	 */
	public boolean next() {
		return false; //Keyboard.next();
	}
	
	public boolean getEventKeyState() {
		return false; //Keyboard.getEventKeyState();
	}
	
	public int getEventKey() {
		return 0; //Keyboard.getEventKey();
	}
	
	public char getEventCharacter() {
		return 0; //Keyboard.getEventCharacter();
	}
	
	public int getCode(Keys k) {
		return platformKeys.get(k);
	}
}
