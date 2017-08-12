/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
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

import java.util.ArrayList;
import java.util.List;

import static zildo.server.EngineZildo.hasard;
import zildo.monde.collision.Collision;
import zildo.monde.collision.DamageType;
import zildo.monde.sprites.Reverse;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.util.Point;
import zildo.monde.util.Zone;
import zildo.server.EngineZildo;

/**
 * @author Tchegito
 *
 */
public class ElementPoison extends Element {

	List<Element> clouds;
	double beta;
	double duration;
	Collision collision;
	
	public static int CLOUD_DURATION = 1000;
	
	public ElementPoison(int x, int y, Perso p_shooter) {
		clouds = new ArrayList<Element>();
		int des12 = 0;
		double gamma = 0;
		Zone zc = new Zone(1024, 1024, -1024, -1024);
		// Place 5 small clouds
		int size = 0;
		for (int i=0;i<6;i++) {
			Element cloud = new Element();
			double rayon = 8 + Math.random() * 4;
			cloud.x = x;
			cloud.y = y;
			if (i != 0) {
				cloud.x += (float) (rayon * Math.cos(gamma));
				cloud.y += (float) (rayon * Math.sin(gamma));
			}
			gamma += 2*Math.PI / 5 + Math.random() * Math.PI/16;
			ElementDescription cloudDesc = ElementDescription.POISON1;
			des12 = (des12 + 1) % 3;
			if (des12 == 0) {
				cloud.reverse = Reverse.HORIZONTAL;
			} else if (des12 == 1) {
				cloud.reverse = Reverse.ALL;
			}
			if (hasard.lanceDes(5)) {
				cloudDesc = ElementDescription.POISON2;
			}
			cloud.setDesc(cloudDesc);
			cloud.setForeground(true);
			EngineZildo.spriteManagement.spawnSprite(cloud);
			clouds.add(cloud);
			
			// Determining collision zone
			size = cloud.getSprModel().getTaille_x() / 2;	// Assume that width is the same than height
			zc.x1 = (int) Math.min(zc.x1, cloud.x - size);
			zc.y1 = (int) Math.min(zc.y1, cloud.y - size);
			zc.x2 = (int) Math.max(zc.x2, cloud.x + size);
			zc.y2 = (int) Math.max(zc.y2, cloud.y + size);
		}
		duration = CLOUD_DURATION;
		
		zc.x2 = zc.x2 - zc.x1;
		zc.y2 = zc.y2 - zc.y1;
		zc.y2 -= size / 2;
		collision = new Collision(zc.getCenter(), new Point(zc.x2, zc.y2), p_shooter, DamageType.POISON, null);
	}
	
	@Override
	public void animate() {
		double add = 0;
		double durationFactor = 1;
		if (duration < 300) {
			durationFactor = duration  / 300;
		}
		for (Element cloud : clouds) {
			boolean bigCloud = add == 0; // First one
			int zoomAmplitude = bigCloud ? 15 : 35;
			int zoomF = (int) (180 + zoomAmplitude * Math.cos(beta + add));
			int alphaF = (int) (200 + 55 * Math.sin(3*beta + add*8));
			if (bigCloud) {
				zoomF += 100;
				alphaF = (int) Math.min(alpha + 50, 255);
			} else {
				cloud.x += 0.01 * Math.cos(beta + add);
				cloud.y += 0.02 * Math.sin(beta - add);
			}
			cloud.zoom = zoomF;
			cloud.setAlpha((int) (alphaF * durationFactor));
			add+=Math.PI / 8;
		}
		beta += 0.06;

		duration--;
		if (duration == -1) {
			for (Element cloud : clouds) {
				cloud.dying = true;
			}
			dying = true;
		}

		// Make poison damage hero
		manageCollision();
		super.animate();
	}
	
	@Override
	public void manageCollision() {
		EngineZildo.collideManagement.addCollision(collision);
	}
	
    @Override
	public Collision getCollision() {
    	if (duration > 100) {
    		return collision;
    	} else {	// Cloud is going to disappear
    		return null;
    	}
    }
}
