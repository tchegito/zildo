/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
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

import zildo.Zildo;
import zildo.client.ClientEngineZildo;
import zildo.fwk.input.CommonKeyboardHandler;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
import zildo.monde.util.Zone;

/**
 * @author Tchegito
 *
 */
public class AndroidKeyboardHandler extends CommonKeyboardHandler {

	// Relations between given key and its location on screen, inside virtual pad
	enum KeyLocation {
		VP_UP(29, 0, 22, 29, true, KEY_UP),	// Real zone : 29, 11, 23, 19
		VP_LEFT(0, 29, 29, 23, true, KEY_LEFT),	// Real zone : 11, 29, 19, 23
		VP_RIGHT(51, 29, 29, 23, true, KEY_RIGHT),	// Real zone : 51, 29, 19, 23
		VP_DOWN(29, 52, 22, 28, true, KEY_DOWN),	// Real zone : 29, 52, 23, 19
		// diagonals
		VP_UP_LEFT(0, 0, 29, 29, true, KEY_UP, KEY_LEFT),
		VP_UP_RIGHT(51, 0, 29, 29, true, KEY_UP, KEY_RIGHT),
		VP_DOWN_LEFT(0, 52, 29, 28, true, KEY_DOWN, KEY_LEFT),
		VP_DOWN_RIGHT(51, 52, 29, 28, true, KEY_DOWN, KEY_RIGHT),
		// inventory
		VP_INVENTORY(0, 0, 40, 31, false, KEY_X),
		VP_BUTTON_X(286, 200, 25, 24, false, KEY_Q),
		VP_BUTTON_Y(294, 170, 25, 24, false, KEY_W),
		VP_FRAME(0, 180, 320, 240, false, KEY_DIALOG_FRAME);
		/*
		VP_BUTTON_A(4, 33, 26, 26, false, KEY_Q),
		VP_BUTTON_B(36, 58, 26, 26, false, KEY_W),
		VP_BUTTON_C(36, 8, 26, 26, false, KEY_X);
		*/
		
		public final Zone z;
		public final Zone zLeftHanded;
		public final int keyCode;
		public final int keyCode2;
		
		private KeyLocation(int x, int y, int wx, int wy, boolean isDirection, int... keys) {
			int addX = 0;
			int addY = 0;
			if (isDirection) {
				addY = Zildo.viewPortY - 80;
			}
			z = new Zone(x + addX, y + addY, wx, wy);
			keyCode = keys[0];
			if (keys.length > 1) {
				keyCode2 = keys[1];
			} else {
				keyCode2 = -1;
			}
			// Calculate zone for left handed
			if (keys[0] == KEY_X) {	// Inventory stay at the same place
				zLeftHanded = z;
			} else {
				int symetricX = Zildo.viewPortX - x - addX - wx;
				if (isDirection) {
					symetricX = Zildo.viewPortX - 80 + x + addX; 
				}
				zLeftHanded = new Zone(symetricX, y + addY, wx, wy);
			}
		}
		
		protected boolean isInto(Point p, boolean leftHanded) {
			if (!leftHanded) {
				return z.isInto(p.x, p.y);
			} else {
				return zLeftHanded.isInto(p.x, p.y);
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
	private static final int KEY_DIALOG_FRAME	= 0xD1;	/* Random */
	
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
		platformKeys.put(Keys.DIALOG_FRAME, KEY_DIALOG_FRAME);
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
		/*else if (p_code == KEY_Q || p_code == KEY_W) {
			if (polledTouchedPoints.size() != 0) {
				for (Point p : polledTouchedPoints.getAll()) {
					switch (p_code) {
					case KEY_Q:
						if (p.x >= middleX && p.y < middleY) {
							return true;
						}
						break;
					case KEY_W:
						if (p.x >= middleX && p.y >= middleY) {
							return true;
						}
						break;
						/*
					case KEY_X:
						Point zildoPos = infos.getZildoPos();
						// Inventory only if player isn't moving (tm.getCurrent) and close enough
						// to zildo location
						if (zildoPos != null //&& tm.getCurrent() == null 
								&& zildoPos.distance(p) < 24) {
							 return true;
						}
						break;
					*//*
					}
				}
			}			
		} */else {
			return keyStates[p_code];
		}
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
			boolean leftHanded = ClientEngineZildo.client.isLeftHanded();
			for (Point p : polledTouchedPoints.getAll()) {
				for (KeyLocation kLoc : KeyLocation.values()) {
					if (kLoc.isInto(p, leftHanded)) {
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
