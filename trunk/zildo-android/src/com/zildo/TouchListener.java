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

import java.util.ArrayList;
import java.util.List;

import zildo.Zildo;
import zildo.client.Client;
import zildo.client.ClientEngineZildo;
import zildo.fwk.ui.ItemMenu;
import zildo.fwk.ui.Menu;
import zildo.monde.util.Point;
import zildo.platform.input.AndroidInputInfos;
import zildo.platform.input.AndroidKeyboardHandler;
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
	
	public TouchListener(Client client) {
		this.client = client;
		touchedPoints = new ArrayList<Point>();
	}
	
	/**
	 * Share touched points with the "keyboard" handler, so as to detect which button is pressed.
	 */
	public void init() {
		AndroidKeyboardHandler kbHandler = (AndroidKeyboardHandler) Zildo.pdPlugin.kbHandler;
		infos = new AndroidInputInfos();
		infos.liveTouchedPoints = touchedPoints;
		kbHandler.setAndroidInputInfos(infos);
	}
	
	ItemMenu item;
	
	AndroidInputInfos infos;
	final List<Point> touchedPoints;
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int x = (int) (event.getX() * event.getXPrecision());
		int y = (int) (event.getY() * event.getYPrecision());
		//Log.d("touch", "pos = "+x+", "+y+" action = "+event.getAction());
		
		Menu menu = client.getCurrentMenu();
		if (menu != null) {
			ItemMenu tempItem = ClientEngineZildo.guiDisplay.getItemOnLocation(x, y);
			if (tempItem != null) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_UP:
					item = tempItem;
					Log.d("touch", "item "+item.getText());
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
			Point p = new Point(x,y);
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				//Log.d("touch", "add points");
				touchedPoints.add(p);
				break;
			case MotionEvent.ACTION_MOVE:
				if (touchedPoints.size() != 1) {
					System.out.println("very strange !");
					touchedPoints.clear();
					touchedPoints.add(p);
				}
				touchedPoints.set(0, p);
				//Log.d("touch", "maintained");
				break;
			case MotionEvent.ACTION_UP:
				touchedPoints.clear();
				//Log.d("touch", "remove point");
				break;
			}
		}
		return true;
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
