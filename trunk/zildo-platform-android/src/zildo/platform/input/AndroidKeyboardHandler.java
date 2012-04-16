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

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import android.util.Log;

import zildo.Zildo;
import zildo.fwk.input.KeyboardHandler;
import zildo.monde.util.Point;

/**
 * @author Tchegito
 *
 */
public class AndroidKeyboardHandler implements KeyboardHandler {

	final static EnumMap<Keys, Integer> platformKeys = new EnumMap<Keys, Integer>(Keys.class);
	
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
	
	List<Point> liveTouchedPoints;
	List<Point> polledTouchedPoints;
	
	public AndroidKeyboardHandler() {
		liveTouchedPoints = new ArrayList<Point>();
		polledTouchedPoints = new ArrayList<Point>();
	}
	
	public void setTouchedPoints(List<Point> points) {
		this.liveTouchedPoints = points;
	}
	
	public boolean isKeyDown(int p_code) {
		if (!polledTouchedPoints.isEmpty()) {
			Point p = polledTouchedPoints.get(0);
			Log.d("keyboardhandler", "received one point");
			switch (p_code) {
			case KEY_Q:
				return p.x > (Zildo.viewPortX / 2);
			}
		}
		return false;
	}
	
	public void poll() {
		polledTouchedPoints.clear();
		if (!liveTouchedPoints.isEmpty()) {
			polledTouchedPoints.clear();
			polledTouchedPoints.addAll(liveTouchedPoints);
			liveTouchedPoints.clear();
		}
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
