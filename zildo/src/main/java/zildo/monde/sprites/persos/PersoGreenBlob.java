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

package zildo.monde.sprites.persos;

import zildo.client.sound.BankSound;
import static zildo.server.EngineZildo.hasard;
import zildo.monde.Trigo;
import zildo.monde.sprites.Reverse;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.desc.SpriteAnimation;
import zildo.monde.sprites.elements.ElementPoison;
import zildo.monde.sprites.persos.ia.PathFinderGreenBlob;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
import zildo.monde.util.Pointf;
import zildo.server.EngineZildo;

/**
 * @author Tchegito
 *
 */
public class PersoGreenBlob extends PersoShadowed {

	int delay;
	int cloudDelay;	// Number of frame to wait before launching a poison cloud
	boolean enemyFound;	// TRUE when he found Zildo and wants to spit his poison cloud
	boolean approaching;	// TRUE when he found Zildo and is getting closer
	Perso enemy;
	double correctAngle;
	
	public PersoGreenBlob() {
		super(ElementDescription.SHADOW, 1);
		setPathFinder(new PathFinderGreenBlob(this));
		setPv(2);
		setNSpr(PersoDescription.GREEN_BLOB.first());
		setDesc(PersoDescription.GREEN_BLOB);
		
		cloudDelay = 0;
	}
	
	final int[] seqStill = { 0, 1, 2, 1 }; // another 3 sprites
	final int[] seqGreen = {0, 3, 4, 3 };

	@Override
	public void finaliseComportement(int compteur_animation) {
		setAjustedX((int) x);
		setAjustedY((int) y);
		
		reverse = angle == Angle.OUEST ? Reverse.HORIZONTAL : Reverse.NOTHING;
		int posSequence = (compteur_animation / 20) % 4;
		int add_spr;
		if (deltaMoveX != 0) {
			add_spr = seqGreen[posSequence];
			if (posSequence == 2 && delay == 0) {
				EngineZildo.soundManagement.broadcastSound(BankSound.Blob, this);
				delay = 20;
			}
			delay = Math.max(0, --delay);

		} else {
			// Persos à 3 sprite et 1 angle
			add_spr = seqStill[posSequence];
		}
		// TODO: enhance that
		// Ugly trick : reduce 'add_spr' because it's readded in SpriteManagement#updateSprites
		// Problem is some use of 'add_spr' non-orthodox, let's see CompositeElement#followShape
		setNSpr(PersoDescription.GREEN_BLOB.nth(add_spr));
		
	}
	
	@Override
	public void animate(int compteur_animation) {
		super.animate(compteur_animation);
		
		if (pathFinder.getTarget() == null) {
			if (enemyFound) {
				// Makes him getting closer to Zildo
				double gamma = Trigo.getAngleRadian(x, y, enemy.x, enemy.y);
				gamma += hasard.intervalle((float) (Math.PI / 4f));
				gamma += correctAngle;
				Pointf p = new Pointf(x + 12 * Math.cos(gamma),
					     			 y + 12 * Math.sin(gamma) );
				pathFinder.setTarget(p);
				approaching = true;
			} else if (approaching ) {
				int xx = (int) (x + angle.coords.x * 16);
				int yy = (int) (y + angle.coords.y * 16);
				cloudDelay = ElementPoison.CLOUD_DURATION;
				EngineZildo.spriteManagement.spawnSpriteGeneric(SpriteAnimation.POISONCLOUD, xx, yy, floor, 0, this, null);
				EngineZildo.soundManagement.broadcastSound(BankSound.PoisonCloud, new Point(x,y));
				approaching = false;
				correctAngle = 0;
			}
			enemyFound = false;
		}
		if (!approaching && !enemyFound && cloudDelay == 0) {
			Perso zildo = EngineZildo.persoManagement.lookForOne(this, 5, PersoInfo.ZILDO, true);
			if (zildo != null) {
				enemyFound = true;
				enemy = zildo;
			}
				
		}
		if (cloudDelay > 0) {
			cloudDelay--;
		}
	}
}