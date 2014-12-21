/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
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

package zildo.monde.sprites.persos;

import zildo.client.sound.BankSound;
import zildo.monde.Trigo;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.elements.ElementProjectile;
import zildo.monde.sprites.persos.ia.PathFinderBee;
import zildo.monde.util.Point;
import zildo.monde.util.Vector2f;
import zildo.server.EngineZildo;

/**
 * @author Tchegito
 *
 */
public class PersoFlyingSerpent extends PersoShadowed {

	float iota;
	float swingAmplitude;
	float swingBase;

	int count;
	public PersoFlyingSerpent() {
		pathFinder = new PathFinderBee(this);
		pv = 2;
		setForeground(true);
		flying = true;
		
		swingAmplitude = 3;
		swingBase = 8;
		count = 100;
	}
	
	@Override
	public void move() {
		super.move();
		
		// Swing the bird !
		iota += 0.07f;
		z = swingBase + (float) (swingAmplitude * Math.cos(iota));
		
		// Shoot
		if (count <= 0) {
			Perso zildo = EngineZildo.persoManagement.lookFor(this, 10, PersoInfo.ZILDO);
			// Target hero
			if (zildo != null) {
				double zDirection = Trigo.getAngleRadian(x, y-2, zildo.x, zildo.y);
				Vector2f speedVect = Trigo.vect(zDirection, 1.8f);
				Element redSphere = new ElementProjectile(ElementDescription.BROWNSPHERE1, x, y-2, z,
						speedVect.x, speedVect.y, this);
				EngineZildo.spriteManagement.spawnSprite(redSphere);
				EngineZildo.soundManagement.broadcastSound(BankSound.SerpentSpit, new Point(x, y));
				count = 200;
			} else {
				count = 50;
			}
		} else {
			count--;
		}
	}
}
