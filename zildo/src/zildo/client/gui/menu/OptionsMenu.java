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

package zildo.client.gui.menu;

import java.util.ArrayList;

import zildo.Zildo;
import zildo.client.ClientEngineZildo;
import zildo.client.PlatformDependentPlugin;
import zildo.client.PlatformDependentPlugin.KnownPlugin;
import zildo.fwk.ui.ItemMenu;
import zildo.fwk.ui.Menu;
import zildo.fwk.ui.UIText;

/**
 * @author Tchegito
 * 
 */
public class OptionsMenu extends Menu {

	boolean music = client.isMusic();
	boolean fullScreen = Zildo.fullScreen;
	boolean leftHanded = client.isLeftHanded();
	boolean movingCross = client.isMovingCross();
	
	public OptionsMenu(final Menu p_previous) {

		items = new ArrayList<ItemMenu>();

		items.add(new ItemMenu(getMusicString()) {

			@Override
			public void run() {
				if (Zildo.soundEnabled) {
					music = !music;
					client.setMusic(music);
					setText(getMusicString());
				}
				client.handleMenu(currentMenu);
			}

		});

    	// TODO: Dirty way to check Android platform : need to be cleaned with
    	// a better injection mechanism.
    	if (PlatformDependentPlugin.currentPlugin != KnownPlugin.Android) {
			items.add(new ItemMenu(getFullscreenString()) {
				@Override
				public void run() {
					fullScreen = !fullScreen;
					ClientEngineZildo.openGLGestion.switchFullscreen(fullScreen);
					setText(getFullscreenString());
					client.handleMenu(currentMenu);
				}
			});
    	} else {
    		items.add(new ItemMenu(getLeftHandedString()) {
    			@Override
    			public void run() {
    				leftHanded = !leftHanded;
    				setText(getLeftHandedString());
    				client.setLeftHanded(leftHanded);
					client.handleMenu(currentMenu);
    			}
    		});
    		items.add(new ItemMenu(getMovingCrossString()) {
    			@Override
    			public void run() {
    				movingCross = !movingCross;
    				setText(getMovingCrossString());
    				client.setMovingCross(movingCross);
					client.handleMenu(currentMenu);
    			}
    		});
    		
    	}
		
		items.add(new ItemMenu("global.back") {
			@Override
			public void run() {
				client.handleMenu(p_previous);
			}
		});
		setMenu(items.toArray(new ItemMenu[] {}));
		setTitle("m7.options");
	}

	String getMusicString() {
		return UIText.getMenuText("m9.musicPref", music ? "On" : "Off");
	}
	
	String getFullscreenString() {
		return UIText.getMenuText("m9.fullScreenPref", fullScreen ? "On" : "Off");
	}
	
	String getLeftHandedString() {
		return UIText.getMenuText("m9.leftHanded", leftHanded ? "On" : "Off");
	}
	
	String getMovingCrossString() {
		return UIText.getMenuText(movingCross ? "m9.movingCross.mobile" : "m9.movingCross.locked");
	}
}
