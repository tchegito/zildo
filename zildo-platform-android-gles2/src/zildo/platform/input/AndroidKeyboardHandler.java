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

import zildo.Zildo;
import zildo.client.ClientEngineZildo;
import zildo.fwk.input.CommonKeyboardHandler;
import zildo.fwk.input.DPadMovement;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
import zildo.monde.util.Vector2f;
import zildo.monde.util.Zone;

/**
 * @author Tchegito
 *
 */
public class AndroidKeyboardHandler extends CommonKeyboardHandler {

	Point repereCrossCenter = new Point(10 + (80/2), Zildo.viewPortY - (80/2));
	
	// Relations between given key and its location on screen, inside virtual pad
	public enum KeyLocation {
		VP_UP(29, -10, 22, 39, true, KEY_UP),	// Real zone : 29, 11, 23, 19
		VP_LEFT(-10, 29, 39, 23, true, KEY_LEFT),	// Real zone : 11, 29, 19, 23
		VP_RIGHT(51, 29, 39, 23, true, KEY_RIGHT),	// Real zone : 51, 29, 19, 23
		VP_DOWN(29, 52, 22, 38, true, KEY_DOWN),	// Real zone : 29, 52, 23, 19
		// diagonals
		VP_UP_LEFT(-10, -10, 39, 39, true, KEY_UP, KEY_LEFT),
		VP_UP_RIGHT(51, -10, 39, 39, true, KEY_UP, KEY_RIGHT),
		VP_DOWN_LEFT(-10, 52, 39, 38, true, KEY_DOWN, KEY_LEFT),
		VP_DOWN_RIGHT(51, 52, 39, 38, true, KEY_DOWN, KEY_RIGHT),
		// inventory
		VP_INVENTORY(0, 0, 40, 31, false, KEY_X),
		VP_BUTTON_X(274-16-8, 200, 24+16, 24+16, false, KEY_Q),
		VP_BUTTON_Y(296-16-8, 170-8-3, 24+16, 24+16, false, KEY_W),
		VP_FRAME(0, 180, 320, 240, false, KEY_DIALOG_FRAME),
		
		VP_DPAD(0-30, 0-30, 80+60, 80+60, true, KEY_DIALOG_FRAME),	// KEy has no meaning here
		VP_COMPASS(48, 0, 32, 32, false, KEY_COMPASS),
		VP_GEAR(290, 0, 28, 28, false, KEY_TOUCH_GEAR);
		/*
		VP_BUTTON_A(4, 33, 26, 26, false, KEY_Q),
		VP_BUTTON_B(36, 58, 26, 26, false, KEY_W),
		VP_BUTTON_C(36, 8, 26, 26, false, KEY_X);
		*/
		
		public final Zone z;
		public final Zone zLeftHanded;
		public final int keyCode;
		public final int keyCode2;
		public boolean direction;
		
		private KeyLocation(int x, int y, int wx, int wy, boolean isDirection, int... keys) {
			int addX = 0;
			int addY = 0;
			if (isDirection) {
				addY = Zildo.viewPortY - 80;
				addX = 10;
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
			direction = isDirection;
		}
		
		/** Returns TRUE if given point is inside this key zone, considering leftHanded parameter.
		 */
		protected boolean isInto(Point p, boolean leftHanded) {
			if (!leftHanded) {
				return z.isInto(p.x, p.y);
			} else {
				return zLeftHanded.isInto(p.x, p.y);
			}
		}
		
		static boolean isInCrossArea(Point p) {
			return p.x < (Zildo.viewPortX / 2) && 
				   p.y > (Zildo.viewPortY / 3);
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
	private static final int KEY_TOUCH_MENU	= 0xD2;	/* Random */
	private static final int KEY_TOUCH_BACK	= 0xD3;	/* Random */
	private static final int KEY_COMPASS = 40; //253;
	private static final int KEY_TOUCH_GEAR = 41;
	
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
		platformKeys.put(Keys.TOUCH_MENU, KEY_TOUCH_MENU);
		platformKeys.put(Keys.TOUCH_BACK, KEY_TOUCH_BACK);
		platformKeys.put(Keys.COMPASS, KEY_COMPASS);
		platformKeys.put(Keys.PAGEUP, 254);	// No pageup/pagedown on touchscreen
		platformKeys.put(Keys.PAGEDOWN, 253);
		platformKeys.put(Keys.GEAR, KEY_TOUCH_GEAR);
	}
	
	TouchPoints polledTouchedPoints;
	//TouchMovement tm;
	AndroidInputInfos infos;
	
	public AndroidKeyboardHandler() {
		polledTouchedPoints = new TouchPoints();
		//infos = new AndroidInputInfos();
	}
	
	public void setAndroidInputInfos(AndroidInputInfos infos) {
		this.infos = infos;
	}
	
	static final int middleX = Zildo.viewPortX / 2;
	static final int middleY = Zildo.viewPortY / 2;
	
	public boolean isKeyDown(int p_code) {
		switch (p_code) {
		case KEY_TOUCH_BACK:
			return infos.backPressed;
		case KEY_TOUCH_MENU:
			return infos.menuPressed;
		default:
			return keyStates[p_code];
		}
	}
	
	Angle previous;
	Vector2f direction;
	int simulatedKeyCode;
	int previousSimulatedKeyCode;
	int countInactivity;
	
	public void poll() {
		// Clear all keys state
		for (int i : platformKeys.values()) {
			keyStates[i] = false;
		}
		super.poll(); //[19] = keyNext[19];
		
		simulatedKeyCode = -1;
		
		polledTouchedPoints.clear();
		direction = null;
		boolean atLeastOneInDpadArea = false;

		if (!infos.liveTouchedPoints.isEmpty()) {
			synchronized (infos.liveTouchedPoints) {
				polledTouchedPoints.copy(infos.liveTouchedPoints);
			}
			countInactivity = 0;
			ClientEngineZildo.client.showAndroidUI(true);

			
			// Update all keys state
			
			boolean movingCross = ClientEngineZildo.client.isMovingCross();
			boolean leftHanded = !movingCross && ClientEngineZildo.client.isLeftHanded();
			int idx = 0;
			for (Point p : polledTouchedPoints.getAll()) {
				if (p != null) {	// Don't know why, but it can be NULL
					// 1) fix moving cross center, on first touch
					Point translated = new Point(p);
					if (movingCross &&
							// Allow first touch only half of the screen, but drag everywhere
							((infos.movingCrossCenter != null && idx == infos.idxPointCrossCenter) || KeyLocation.isInCrossArea(p))) {
						atLeastOneInDpadArea = true;
						if (infos.movingCrossCenter == null) {
							System.out.println("Place cross at "+p+" on index "+idx);
							infos.movingCrossCenter = p;
							infos.idxPointCrossCenter = idx;	// Store finger index on the cross center
							ClientEngineZildo.client.setCrossCenter(p);
							//Log.d("TOUCH", "Place cross center at "+p);
						}
						Point shift = new Point(infos.movingCrossCenter)
							.translate(-repereCrossCenter.x, -repereCrossCenter.y);
						translated.add(-shift.x, -shift.y);
						ClientEngineZildo.client.setDraggingTouch(p);
						
						// Moving center feature: when touch is too far of the cross, cross follow
						if (idx == infos.idxPointCrossCenter) {
							Point newCenter = DPadMovement.moveCenter(infos.movingCrossCenter, p);
							if (!newCenter.equals(infos.movingCrossCenter)) {
								infos.movingCrossCenter = newCenter;
								ClientEngineZildo.client.setCrossCenter(newCenter);
							}
						}
						//Log.d("TOUCH", "apply shift of "+shift+" which means p="+translated);
					}
					
					for (KeyLocation kLoc : KeyLocation.values()) {
						if (kLoc.isInto(translated, leftHanded) || 
								// For DPAD on moving mode, allow full screen
								(kLoc == KeyLocation.VP_DPAD && movingCross && infos.movingCrossCenter != null)) {
							if (kLoc == KeyLocation.VP_DPAD && (!movingCross || idx == infos.idxPointCrossCenter)) {	// Special : d-pad zone
								int shiftX = leftHanded ? kLoc.zLeftHanded.x1 : 0;	// For left handed, pad is on the right
								direction = DPadMovement.compute(translated.x - 50 - shiftX, translated.y - 200);
							} else {
								keyStates[kLoc.keyCode] = true;
								if (kLoc.keyCode2 != -1) {
									keyStates[kLoc.keyCode2] = true;
								}
							}
						}
					}
				}
				idx++;
			}
			
		} else {
			countInactivity++;
			if (countInactivity == 100) {
				ClientEngineZildo.client.showAndroidUI(false);
			}
		}
		// Interpret gamepad events: direction + buttons
		if (!infos.gamePadDirection.isEmpty()) {
			direction = new Vector2f(infos.gamePadDirection);
			direction.normalize(1);

			Angle angleDirection = Angle.fromDelta(direction.x, direction.y);
			switch (angleDirection) {
			case NORD:
				if (previousSimulatedKeyCode != KEY_UP) {
					keyStates[KEY_UP] = true;
					simulatedKeyCode = KEY_UP;
				}
				break;
			case SUD:
				if (previousSimulatedKeyCode != KEY_DOWN) {
					keyStates[KEY_DOWN] = true;
					simulatedKeyCode = KEY_DOWN;
				}
				break;
			case OUEST:
				if (previousSimulatedKeyCode != KEY_LEFT) {
					keyStates[KEY_LEFT] = true;
					simulatedKeyCode = KEY_LEFT;
				}
				break;
			case EST:
				if (previousSimulatedKeyCode != KEY_RIGHT) {
					keyStates[KEY_RIGHT] = true;
					simulatedKeyCode = KEY_RIGHT;
				}
			default:
				break;
			}

			if (simulatedKeyCode != -1) {
				previousSimulatedKeyCode = simulatedKeyCode;
			}
		} else {
			previousSimulatedKeyCode = -1;
		}
		
		for (KeyLocation k : KeyLocation.values()) {
			if (infos.gamePadButtons[k.ordinal()]) {
				keyStates[k.keyCode] = true;
			}
		}
		
		if (!atLeastOneInDpadArea && infos.movingCrossCenter != null) {
			//Log.d("TOUCH", "Remove cross center");
			infos.movingCrossCenter = null;
			infos.idxPointCrossCenter = -1;
			ClientEngineZildo.client.setCrossCenter(null);
			ClientEngineZildo.client.setDraggingTouch(null);
		}
	}
	
	@Override
	public Vector2f getDirection() {
		return direction;
	}
	
	/**
	 * @return true if a keyboard event was read, false otherwise
	 */
	public boolean next() {
		return simulatedKeyCode != -1; //Keyboard.next();
	}
	
	public boolean getEventKeyState() {
		return true; //Keyboard.getEventKeyState();
	}
	
	public int getEventKey() {
		try {
			return simulatedKeyCode; //Keyboard.getEventKey();
		} finally {
			simulatedKeyCode = -1;
		}
	}
	
	public char getEventCharacter() {
		return 0; //Keyboard.getEventCharacter();
	}
	
	public int getCode(Keys k) {
		return platformKeys.get(k);
	}
}
