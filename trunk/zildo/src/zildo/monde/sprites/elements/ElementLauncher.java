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

package zildo.monde.sprites.elements;

import zildo.monde.sprites.desc.SpriteAnimation;
import zildo.monde.sprites.persos.PersoZildo;
import zildo.server.EngineZildo;

/**
 * @author Tchegito
 *
 */
public class ElementLauncher extends Element {

	private static final int RANGEX = 100;
	private static final int RANGEY = 20;
	private static final int DELAY = 5;
	int count = 0;
	int firing = 0;
	
	public ElementLauncher(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public void animate() {
		if (firing != 0) {
			prepareFire();
		} else if (count == 0) {
			// Look for Zildo
			PersoZildo zildo = EngineZildo.persoManagement.getZildo();
			if (zildo != null) {
				if (zildo.x > x && zildo.x <= x+RANGEX &&
					zildo.y >= (y-RANGEY / 2) && zildo.y <= (y+RANGEY)) {
					firing = DELAY;
				}
			}
		} else {
			count--;
		}
	}
	
	private void prepareFire() {
		if (firing != 0) {
			switch (firing) {
			case DELAY:
				addSpr = 1;
				setAjustedY((int)y+1);
				break;
			case 1:
				addSpr = 0;
				setAjustedY((int)y);
				fire();
				break;
			}
			firing--;
		}
	}
	
	private void fire() {
		int xx = (int) x + 4;
		int yy = (int) y + 12;
		EngineZildo.spriteManagement.spawnSpriteGeneric(SpriteAnimation.ROCKBALL, xx, yy, floor, 0, null, null);
		count = 30;
	}
}
