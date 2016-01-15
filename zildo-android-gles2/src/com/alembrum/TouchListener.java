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

package com.alembrum;

import zildo.Zildo;
import zildo.client.Client;
import zildo.client.ClientEngineZildo;
import zildo.fwk.ui.ItemMenu;
import zildo.fwk.ui.Menu;
import zildo.monde.util.Point;
import zildo.platform.input.AndroidInputInfos;
import zildo.platform.input.AndroidKeyboardHandler;
import zildo.platform.input.AndroidKeyboardHandler.KeyLocation;
import zildo.platform.input.TouchPoints;
import android.annotation.SuppressLint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * @author Tchegito
 *
 */
public class TouchListener implements OnTouchListener {

	private Client client;
	
	private float ratioX;
	private float ratioY;
	
	public TouchListener(Client client) {
		this.client = client;
		touchedPoints = new TouchPoints();
	}
	
	/**
	 * Share touched points with the "keyboard" handler, so as to detect which button is pressed.
	 */
	public void init() {
		AndroidKeyboardHandler kbHandler = (AndroidKeyboardHandler) Zildo.pdPlugin.kbHandler;
		infos = new AndroidInputInfos();
		infos.liveTouchedPoints = touchedPoints;
		kbHandler.setAndroidInputInfos(infos);
		
		ratioX = (float) Zildo.viewPortX / (float) Zildo.screenX;
		ratioY = (float) Zildo.viewPortY / (float) Zildo.screenY;
	}
	
	ItemMenu item;
	
	AndroidInputInfos infos;
	
	final TouchPoints touchedPoints;
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int index = event.getActionIndex();

		// Deal with current point for menu
		int x = (int) (event.getX(index) * ratioX);
		int y = (int) (event.getY(index) * ratioY);
		
		Menu menu = client.getCurrentMenu();
		if (menu != null) {
			ItemMenu tempItem = ClientEngineZildo.guiDisplay.getItemOnLocation(x, y);
			if (tempItem != null) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_POINTER_UP:
					item = tempItem;
					break;
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_POINTER_DOWN:
				case MotionEvent.ACTION_MOVE:
					menu.selectItem(tempItem);
					break;
				}
			}
		}
        interpretEvent(event);
		return true;
	}
	
	final static int INACTIVE_POINTER = -1;
	int activePointerId;
	
	private void interpretEvent(MotionEvent event) {
		int actionMasked = event.getActionMasked();
		
		int index;
		if (actionMasked == MotionEvent.ACTION_MOVE && activePointerId != INACTIVE_POINTER) {
			index = event.findPointerIndex(activePointerId);
		} else {
			index = event.getActionIndex();
		}
		float xx = event.getX(index);
		float yy = event.getY(index);
		int pointerId = event.getPointerId(index);
		int x = (int) (xx * ratioX);
		int y = (int) (yy * ratioY);

		Point p = new Point(x,y);
		switch (actionMasked) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_POINTER_DOWN:
			//Log.d("TOUCH", "down ("+actionMasked+") at index "+index+" id="+pointerId+" pos="+x+","+y);
			touchedPoints.add(index, p);
			activePointerId = event.getPointerId(index);
			break;
		case MotionEvent.ACTION_MOVE:
			//Log.d("TOUCH", "move ("+actionMasked+") at index "+index+" id="+pointerId+" pos="+x+","+y);
			touchedPoints.set(index, p);
			break;
		case MotionEvent.ACTION_UP:
			activePointerId = INACTIVE_POINTER;
		case MotionEvent.ACTION_POINTER_UP:
	        if (pointerId == activePointerId) {
	            // This was our active pointer going up. Choose a new
	            // active pointer and adjust accordingly.
	            final int newIndex = index == 0 ? 1 : 0;
	            activePointerId = event.getPointerId(newIndex);
	        }
			//Log.d("TOUCH", "up ("+actionMasked+") at index "+index+" id="+pointerId+" pos="+x+","+y);
			touchedPoints.set(index, null);

			break;
		case MotionEvent.ACTION_CANCEL:
			Log.d("touch", "on clear => CANCEL");
			touchedPoints.clear();	// VERY important ! Otherwise, moves would have continue
			activePointerId = INACTIVE_POINTER;
			//Log.d("TOUCH", "ends up gesture");
			break;
		default:
			//Log.d("TOUCH", "undetected action "+actionMasked);
		}
	}
	
	public void pressBackButton(boolean pressed) {
		if (infos != null) {
			infos.backPressed = pressed;
		}
	}
	
	public void pressMenuButton(boolean pressed) {
		if (infos != null) {
			infos.menuPressed = pressed;
		}
	}
	
	public void pressGameButton(KeyLocation k, boolean press) {
		if (press) {
			infos.pressButton(k);
			Menu menu = client.getCurrentMenu();
			if (menu != null) {
				// Inside a menu, activate current item
				item = menu.items.get(menu.getSelected());
			}
		} else {
			infos.releaseButton(k);
		}
	}
	
	public void setGamePadDirection(float x, float y) {
		infos.gamePadDirection.x = x;
		infos.gamePadDirection.y = y;
	}
	
	public ItemMenu popItem() {
		ItemMenu i = item;
		item = null;
		return i;
	}
	
}
