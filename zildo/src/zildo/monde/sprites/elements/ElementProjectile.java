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

import zildo.monde.Trigo;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.util.Angle;
import zildo.monde.util.Vector2f;

/**
 * Projectile launched in a direction determined either by:<ul>
 * <li>provided speed (vx,vy)</li>
 * <li>provided target {@link Element} from a given source {@link Element}. Note it may move during the launch</li>
 * </ul>
 * 
 * Consists of one colliding sprite, and 2 others as a trail.
 * 
 * @author Tchegito
 *
 */
public class ElementProjectile extends ElementChained {
	
	int seq;	// Position in the sprite sequence (from 0 to 2, because we want 3 sprites)
	
	public enum ProjectileKind {
		THREE_TRAIL(3, true, 2),	// Three reducing and close sprites as a trail
		FIREBALLS(5, false, 8);
		
		final int numberOfElements, delay;
		boolean increaseSpriteModel;
		
		private ProjectileKind(int numberOfElements, boolean increaseSpriteModel, int delay) {
			this.numberOfElements = numberOfElements;
			this.increaseSpriteModel = increaseSpriteModel;
			this.delay = delay;
		}
	};
	
	final ProjectileKind kind;
	Element shootingSpot;
	Element shootedSpot;
	
	public ElementProjectile(ElementDescription desc, ProjectileKind kind, float x, float y, float z, float vx, float vy, Perso shooter) {
		super((int) x, (int) (y - z));
		
		this.desc = desc;
		this.vx = vx;
		this.vy = vy;
		this.seq = 0;
		
		this.kind = kind;
		this.linkedPerso = shooter;
	}
	
	public ElementProjectile(ElementDescription desc, ProjectileKind kind, Element shootingOne, Element shootedOne, Perso shooter) {
		this(desc, kind, shootingOne.x, shootingOne.y, shootingOne.z, 0, 0, shooter);
		
		shootingSpot = shootingOne;
		shootedSpot = shootedOne;
	}
	
	@Override
	protected Element createOne(int p_x, int p_y) {
		
		if (shootedSpot != null && shootingSpot != null) {
			double zDirection = Trigo.getAngleRadian(shootingSpot.x, shootingSpot.y-70, shootedSpot.x, shootedSpot.y);
			Vector2f speedVect = Trigo.vect(zDirection, 1.8f);
			vx = speedVect.x;
			vy = speedVect.y;
			x = shootingSpot.x;
			y = shootingSpot.y-70;
			z = shootingSpot.z;
		}
		Element e = new Element();
		e.setDesc(desc);
		e.vx = vx;
		e.vy = vy;
		e.x = x;
		e.y = y;
		e.addSpr = kind.increaseSpriteModel ? seq : 0;
		e.linkedPerso = linkedPerso;
		e.setForeground(true);
		e.flying = true;
		e.angle = Angle.NORD;	// Nonsense here
		e.reverse = reverse;
		e.zoom = zoom;
		delay = kind.delay;
		
		seq++;
		if (seq == kind.numberOfElements) {
			endOfChain = true;
		}
		return e;
	}
}
