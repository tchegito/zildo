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

import zildo.client.sound.BankSound;
import zildo.monde.collision.Collision;
import zildo.monde.collision.DamageType;
import zildo.monde.sprites.SpriteModel;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
import zildo.monde.util.Vector2f;
import zildo.server.EngineZildo;

/**
 * @author Tchegito
 *
 */
public class ElementSewerSmoke extends Element {

	int counter;
	Element[] volutes;
	Collision collision;
	
	final static Point[] volutesPos = new Point[] { new Point(-8, -1), new Point(-5, -4), new Point(-2, 1), new Point(4, -1)};
	
	public ElementSewerSmoke(int x, int y, Angle ang) {
		angle = ang;
		this.x = x;
		this.y = y;
		setDesc(ElementDescription.SEWER_SMOKE1);
		counter = 0;
		
		// Default angle: SUD
		setPos(new Vector2f(ang.coordf).mul(8));
		setFriction(new Vector2f(ang.coordf).mul(0.1f));
		setSpeed(new Vector2f(ang.coordf).mul(1.3f));
		
		ay = 0;
		
		EngineZildo.soundManagement.broadcastSound(BankSound.Gas, this);
	}
	
	@Override
	public void animate() {
		super.animate();
		if (counter<5) {
			addSpr = 0;
		} else if (counter < 20) {
			setDesc(ElementDescription.SEWER_SMOKE2);
			alphaA = -0.3f;
		}
		if (counter == 15) {
			// Spawn 4 voluts
			volutes = new Element[4];
			for (int i=0;i<4;i++) {
				volutes[i] = new Element();
				volutes[i].x = x + (float) (volutesPos[i].x * Math.random());
				volutes[i].y = y + (float) (volutesPos[i].y * Math.random());
				Vector2f speed = new Vector2f(-(x - volutes[i].x) / 8, -(y - volutes[i].y) / 8);
				speed.normalize(0.3f);
				volutes[i].vx = speed.x;
				volutes[i].vy = speed.y;
				volutes[i].fx = 0.045f;
				volutes[i].fy = 0.045f;
				volutes[i].alphaA = -0.2f;
				volutes[i].setDesc(ElementDescription.SEWER_VOLUT1);
				volutes[i].setAddSpr(i);
				EngineZildo.spriteManagement.spawnSprite(volutes[i]);
			}
			// Reduce alpha
			alphaA = -0.3f;
		}
		counter++;
		
		// Collisions
		Element e = this;
		SpriteModel model = e.getSprModel();
		collision = new Collision(e.getCenter(), new Point(model.getTaille_x(), model.getTaille_y()), 
				null, DamageType.POISON, null);

	}
	
	@Override
	public void manageCollision() {
		if (collision != null) {
			EngineZildo.collideManagement.addCollision(collision);
		}
	}
	
	@Override
	public Collision getCollision() {
		return collision;
	}
}
