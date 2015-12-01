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

package zildo.monde.sprites.elements;

import zildo.monde.collision.Collision;
import zildo.monde.collision.DamageType;
import zildo.monde.sprites.Reverse;
import zildo.monde.sprites.Rotation;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.SpriteModel;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.elements.ElementImpact.ImpactKind;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
import zildo.monde.util.Pointf;
import zildo.server.EngineZildo;

/**
 * @author Tchegito
 *
 */
public class ElementPlaceHolder extends Element {

	SpriteEntity mobile;
	double ancSpeed = 0f;
	int count;
	
	public ElementPlaceHolder(SpriteEntity p_mobile) {
		mobile = p_mobile;
	}
	
	@Override
	public void animate() {
		super.animate();
		
		switch ((ElementDescription)desc) {
		case WATER_LEAF:
			double distance = Pointf.pythagore(vx, vy);
			if (distance > 1f) {
				if (distance > ancSpeed) {
					ancSpeed = distance;
					count = 0;
					spawnWave();
				} else  {
					count += 1;
					if ((count % 10) == 0) {
						spawnWave();
					}
				}
			} else {
				ancSpeed = 0f;
			}
			default:
			break;
		}
	}
	
	private void spawnWave() {
		Angle ang = Angle.fromDelta(vx, vy);
		// Spawn a wave
		int finalX = (int) x - 18*ang.coords.x;
		int finalY = (int) y - 15*ang.coords.y - (getSprModel().getTaille_y() >> 1) + 4;
		
		if (ang.isVertical()) {
			finalY -= 8;
		}
		
		Element wave = new ElementImpact(finalX, finalY, ImpactKind.WAVE, null);
		wave.setDesc(ElementDescription.WATERWAVE3);
		wave.vx = -vx / 5f;
		wave.vy = -vy / 5f;
		wave.rotation = Rotation.fromAngle(ang).succ();
		wave.reverse = Reverse.HORIZONTAL;
		EngineZildo.spriteManagement.spawnSprite(wave);
	}
	
	@Override
	public Collision getCollision() {
		SpriteModel spr = mobile.getSprModel();
		Point pCenter = new Point(mobile.x, mobile.y); // + spr.getTaille_y() / 2);
		Point size = new Point(spr.getTaille_x(), spr.getTaille_y()).multiply(0.7f); 
		Collision c = new Collision(pCenter, size, null, DamageType.HARMLESS, null);
		return c;
	}

}
