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

import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.util.Angle;

/**
 * Projectile launched in a direction determined by (vx,vy).
 * 
 * Consists of one colliding sprite, and 2 others as a trail.
 * 
 * @author Tchegito
 *
 */
public class ElementProjectile extends ElementChained {
	
	int seq;	// Position in the sprite sequence (from 0 to 2, because we want 3 sprites)
	
	public ElementProjectile(ElementDescription desc, float x, float y, float z, float vx, float vy, Perso shooter) {
		super((int) x, (int) (y - z));
		
		this.desc = desc;
		this.vx = vx;
		this.vy = vy;
		this.seq = 0;
		
		this.linkedPerso = shooter;
	}
	
	@Override
	protected Element createOne(int p_x, int p_y) {
		Element e = new Element();
		e.setDesc(desc);
		e.vx = vx;
		e.vy = vy;
		e.x = x;
		e.y = y;
		e.addSpr = seq;
		e.linkedPerso = linkedPerso;
		//e.setForeground(true);
		e.flying = true;
		e.angle = Angle.NORD;	// Nonsense here
		delay = 2;
		
		seq++;
		if (seq == 3) {
			endOfChain = true;
		}
		return e;
	}
}
