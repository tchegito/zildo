/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
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
public abstract class CommonKeyboardHandler implements KeyboardHandler{
	
	boolean[] keyPressed = new boolean[256];
	
	boolean[] keyNext = new boolean[256];
	
	@Override
	public void poll() {
		System.arraycopy(keyNext, 0, keyPressed, 0 , 256);
	}
	
	public boolean isKeyDown(Keys key) {
		return isKeyDown(getCode(key));
	}
	
	public Vector2f getDirection() {
		return null;
	}
	
	/**
	 * Returns TRUE if given key is just being pressed. Meaning that if this method returns TRUE, calling it
	 * on next frame should return FALSE (unless player is not human ;) )
	 */
	public final boolean isKeyPressed(Keys key) {
		boolean down = isKeyDown(key);
		int value = key.ordinal();
		boolean pressed = down && !keyPressed[value];
		keyNext[value] = down;
		/*
		if (pressed) {
			System.out.println("key pressed:"+key+" at frame "+EngineZildo.nFrame);
		}
		*/
		return pressed;
	}
}
