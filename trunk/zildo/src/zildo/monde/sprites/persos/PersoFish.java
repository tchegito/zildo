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

import zildo.fwk.collection.IntSet;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.util.Point;
import zildo.resource.Constantes;
import zildo.server.EngineZildo;

/**
 * @author Tchegito
 *
 */
public class PersoFish extends PersoShadowed {

	double gamma;
	double moveAngle;
	int swingSize = 10;
	
	public PersoFish() {
		super(ElementDescription.SHADOW, 2);
		gamma = Math.random() * Constantes.mathPi;
	}
	
	@Override
	public void move() {
		super.move();
		if (!flying) {
			if (linkedPerso != null) {	// In hero's arms
				if (Math.random() > 0.7) {	// Change sprite slowly
					addSpr = (int) (3 * Math.random());
				}				
				z = 17;
			} else {
				int scale = (int) (swingSize * Math.abs(Math.cos(gamma)));
				addSpr = Math.min(scale / 3, 3);
				z = (float) (scale * (0.8 + 0.3 * Math.random()));
				
				pathFinder.speed *= 0.95f;
				if (z < 1 && pathFinder != null && (pathFinder.getTarget() == null || pathFinder.speed < 0.1)) {
					double attackSpeed = 0.5f + Math.random() * 0.1f;
					moveAngle += Math.PI/4;
					Point targetPoint = new Point((int) (x + 20 * attackSpeed * Math.cos(moveAngle)),
						     					  (int) (y + 20 * attackSpeed * Math.sin(moveAngle)) );
					pathFinder.setTarget(targetPoint);
					pathFinder.speed = (float) (0.6 + Math.random() * 0.3);
				}
				gamma += 0.1d;
			}
		}
	}
	
	IntSet waterBank = new IntSet(188, 189, 190, 255);
	
    @Override
	public void fall() {
		flying = false;
		linkedPerso = null;
		// Fish was flying because someone threw it : detect if it's on a water tile
		int value = EngineZildo.mapManagement.getCurrentMap().readmap((int) x / 16, (int) y / 16);
		if (waterBank.contains(value - 256 * 2)) { // Water inside bank
			swingSize = 2;
		} else {
			swingSize = 10;
		}
    }
}
