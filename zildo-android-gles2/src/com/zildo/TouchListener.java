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

package com.zildo;

import zildo.Zildo;
import zildo.client.Client;
import zildo.client.ClientEngineZildo;
import zildo.fwk.ui.ItemMenu;
import zildo.fwk.ui.Menu;
import zildo.monde.util.Point;
import zildo.platform.input.AndroidInputInfos;
import zildo.platform.input.AndroidKeyboardHandler;
import zildo.platform.input.TouchPoints;
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
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int pointerCount = event.getPointerCount();
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
					item = tempItem;
					Log.d("touch", "item "+tempItem.getText());
					menu.activateItem(item);
					break;
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_MOVE:
					menu.selectItem(tempItem);
					break;
				}
			}
		} else {
			// No menu ==> player is in game
			// Deal with all points
			for (int p = 0; p < pointerCount; p++) {
	             float xx = event.getX(p); // * event.getXPrecision();
	             float yy = event.getY(p); // * event.getYPrecision();
	             interpretEvent(event.getActionMasked(), p, xx, yy);
		     }

		}
		return true;
	}
	
	private void interpretEvent(int actionMasked, int index, float xx, float yy) {
		int x = (int) (xx * ratioX);
		int y = (int) (yy * ratioY);

		Point p = new Point(x,y);
		switch (actionMasked) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_POINTER_DOWN:
			touchedPoints.set(index, p);
			break;
		case MotionEvent.ACTION_MOVE:
			touchedPoints.set(index, p);
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			touchedPoints.set(index, null);
			break;
		default:
			System.out.println("undetected action "+actionMasked);
		}
	}
	
	public void pressBackButton() {
		infos.backPressed = true;
	}
	
	public ItemMenu popItem() {
		ItemMenu i = item;
		item = null;
		return i;
	}
	
}
