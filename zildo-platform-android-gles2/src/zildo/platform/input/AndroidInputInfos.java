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

import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.util.Point;
import zildo.monde.util.Pointf;
import zildo.platform.input.AndroidKeyboardHandler.KeyLocation;
import zildo.server.EngineZildo;

/**
 * @author Tchegito
 *
 */
public class AndroidInputInfos {
	public TouchPoints liveTouchedPoints;
	public Pointf gamePadDirection;
	boolean[] gamePadButtons;
	public boolean backPressed;
	public boolean menuPressed;
	public Point movingCrossCenter;
	PersoPlayer zildo;
	Point zildoPos;
	int nGamePadButtonsPressed;
	
	static final int MAX_BUTTONS_PRESSED = KeyLocation.values().length;
	
	public AndroidInputInfos() {
		liveTouchedPoints = new TouchPoints();
		backPressed = false;
		menuPressed = false;
		zildo = null;
		zildoPos = new Point(0,0);
		gamePadDirection = new Pointf(0f, 0f);
		gamePadButtons = new boolean[MAX_BUTTONS_PRESSED];
		for (int i=0;i<MAX_BUTTONS_PRESSED;i++) {
			gamePadButtons[i] = false;
		}
	}
	
	public Point getZildoPos() {
		if (zildo == null || !zildo.visible) {
			if (EngineZildo.persoManagement != null) {
				zildo = EngineZildo.persoManagement.getZildo();
			}
		}
		if (zildo != null) {
			//SpriteModel model = zildo.getSprModel();
			zildoPos.x = zildo.getScrX()+8; // + model.getTaille_x() >> 1;
			zildoPos.y = zildo.getScrY()+8; // + model.getTaille_y() >> 1;
			return zildoPos;
		}
		return null;
	}
	
	public void pressButton(KeyLocation k) {
		gamePadButtons[k.ordinal()] = true;
	}
	
	public void releaseButton(KeyLocation k) {
		gamePadButtons[k.ordinal()] = false;
	}
}
