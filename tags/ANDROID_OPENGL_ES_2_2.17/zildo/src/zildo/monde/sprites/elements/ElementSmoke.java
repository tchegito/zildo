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
import zildo.server.EngineZildo;

/**
 * Chimney smoke.
 * 
 * @author Tchegito
 * 
 */
public class ElementSmoke extends Element {

	int startX;
	int startY;
	boolean vanishing = false;

	public ElementSmoke(int p_x, int p_y) {
		super();
		startX = p_x;
		startY = p_y;
		x = p_x + 16.0f;
		y = p_y + 34.0f;

	}

	@Override
	public void animate() {
		physicMoveWithCollision();
		if (z > 28 && nSpr == 6) {
			nSpr = 5; // Smoke
		} else if (z > 48 && nSpr == 5 && !vanishing) {
			if (isInsideView()) {
				EngineZildo.spriteManagement.spawnSpriteGeneric(SpriteAnimation.CHIMNEY_SMOKE, 
						startX, startY, floor, 0,	null, null);
				vanishing = true; // Sprite is vanishing (with alpha channel)
			}
		} else if (z > 18) {
			alpha -= 3;
		}

		if (alpha < 10) {
			dying = true;
		}

		setAjustedX((int) x);
		setAjustedY((int) y);
	}
}
