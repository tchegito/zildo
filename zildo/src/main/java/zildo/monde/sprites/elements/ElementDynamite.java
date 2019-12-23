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

package zildo.monde.sprites.elements;

import zildo.client.sound.BankSound;
import zildo.fwk.gfx.EngineFX;
import zildo.monde.collision.Collision;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.elements.ElementImpact.ImpactKind;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
import zildo.server.EngineZildo;

public class ElementDynamite extends Element {

	Perso shooter;
	int counter;

	/**
	 * New dynamite. NOTE: p_shooter <b>CAN'T</b> be null.
	 */
	public ElementDynamite(int p_startX, int p_startY, int p_startZ, Perso p_shooter) {
		x = p_startX;
		y = p_startY;
		z = p_startZ;
		shooter = p_shooter;
		setSprModel(ElementDescription.DYNAMITE);
		counter = 120;

		// Add a shadow
		addShadow(ElementDescription.SHADOW_SMALL);

		if (p_shooter != null) {	// shooter may be initialized later
			floor = p_shooter.getFloor();
			
			EngineZildo.soundManagement.broadcastSound(BankSound.PlanteBombe, this);
		}
	}

	@Override
	public void animate() {
		counter--;
		if (counter == 0) {
			dying = true;
			shadow.dying = true;
			ElementImpact explosion = new ElementImpact((int) x, (int) y, ImpactKind.EXPLOSION, shooter);
			explosion.z = z;
			EngineZildo.spriteManagement.spawnSprite(explosion);
			// Detection of explodable walls
			EngineZildo.mapManagement.getCurrentMap().explodeTile(new Point(x, y), true, null);
			
			// Detection of explodable boulders
			Element boulder = EngineZildo.spriteManagement.collideElement((int) x, (int) y, null, 12, 
					ElementDescription.STONE_HEAVY);
			if (boulder != null) {
				boulder.angle = Angle.fromDelta(boulder.x - x, boulder.y - y);
				boulder.vx = boulder.angle.coordf.x * 1.4f;
				boulder.vy = boulder.angle.coordf.y * 1.1f;
				if (boulder.angle.isHorizontal()) {
					boulder.vy = 1.1f;
				}
				boulder.az = -0.08f;
				boulder.vz = 2.8f;
				boulder.z = 2;
				boulder.flying = true;	// Flying object need angle
			}
		} else if (counter < 30) {
			setSpecialEffect(EngineFX.PERSO_HURT);
		}

		super.animate();
	}

	@Override
	public Collision getCollision() {
		return null;
	}

	@Override
	public boolean beingCollided(Perso p_perso) {
		return true;
	}
}
