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
		
		switch (actionMasked) {
		case MotionEvent.ACTION_DOWN:
			activePointerId = event.getPointerId(0);
			touchedPoints.set(activePointerId, extractPoint(event, 0));
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			index = event.getActionIndex();
			activePointerId = event.getPointerId(index);
			touchedPoints.set(activePointerId, extractPoint(event, index));
			break;
		case MotionEvent.ACTION_MOVE:
			int pointerCount = event.getPointerCount();
			// Move all recorded point currently on screen
	        for(int i = 0; i < pointerCount; ++i) {
	            int pointerId = event.getPointerId(i);
	            index = event.findPointerIndex(pointerId);
	            touchedPoints.set(pointerId, extractPoint(event, index));
	        }
			break;
		case MotionEvent.ACTION_UP: // Only 1 index
			touchedPoints.set(activePointerId, null);
			activePointerId = INACTIVE_POINTER;
			break;
		case MotionEvent.ACTION_POINTER_UP:	// when there's more than 1 index
			index = event.getActionIndex();
			int pointerId = event.getPointerId(index);
			touchedPoints.set(pointerId, null);
	        if (pointerId == activePointerId) {
	            // This was our active pointer going up. Choose a new
	            // active pointer and adjust accordingly.
	            final int newIndex = index == 0 ? 1 : 0;
	            activePointerId = event.getPointerId(newIndex);
	        }
			break;
		case MotionEvent.ACTION_CANCEL:
			touchedPoints.clear();	// VERY important ! Otherwise, moves would have continue
			activePointerId = INACTIVE_POINTER;
			break;
		default:
		}
	}
	
	private Point extractPoint(MotionEvent event, int index) {
		if (index >= 0 && index < event.getPointerCount()) {
			float xx = event.getX(index);
			float yy = event.getY(index);
			int x = (int) (xx * ratioX);
			int y = (int) (yy * ratioY);
	
			return new Point(x,y);
		}		
		return null;
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
		if (infos != null) {
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
	}
	
	public void setGamePadDirection(float x, float y) {
		if (infos != null) {
			infos.gamePadDirection.x = x;
			infos.gamePadDirection.y = y;
		}
	}
	
	public ItemMenu popItem() {
		ItemMenu i = item;
		item = null;
		return i;
	}
	
}
