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

import android.os.Message;
import zildo.client.ClientEngineZildo;
import zildo.fwk.ui.DefaultMenuListener;
import zildo.fwk.ui.EditableItemMenu;
import zildo.fwk.ui.ItemMenu;
import zildo.fwk.ui.Menu;

/**
 * @author Tchegito
 *
 */
public class AndroidMenuListener extends DefaultMenuListener {

	TouchListener touchListener;
	
	public AndroidMenuListener(TouchListener touchListener) {
		this.touchListener = touchListener;
	}
	
	EditableItemMenu editable = null;
	
	@Override
	public ItemMenu act(Menu menu) {
		if (editable != null) {
			// Player is editing an item => wait for he finished
			if (!editable.getText().isEmpty()) {
				return editable;
			}
		}
		ItemMenu item = touchListener.popItem();
		if (item != null && item instanceof EditableItemMenu) {
			// For now, only editable item is "player name", so it's ugly, but works
			// TODO: need to be refactored later
			Message msg = new Message();
			msg.what = ZildoActivity.PLAYERNAME_DIALOG;
			msg.obj = item;
			editable = (EditableItemMenu) item;
			ZildoActivity.handler.sendMessage(msg);
			ClientEngineZildo.getClientForMenu().handleMenu(null);
			item = null;
		}
		return item;
	}
}
