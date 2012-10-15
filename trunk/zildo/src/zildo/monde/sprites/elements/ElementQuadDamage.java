/**
 * Legend of Zildo
 * Copyright (C) 2006-2012 Evariste Boussaton
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

import zildo.monde.sprites.desc.ElementDescription;

public class ElementQuadDamage extends ElementGoodies {

	int anim=0;
	
	public ElementQuadDamage(int p_x, int p_y) {
		super();
		x=p_x;
		y=p_y;
		z=3;
		nSpr=ElementDescription.QUAD1.ordinal();
		volatil=false;
		
        // Add a shadow
		addShadow(ElementDescription.SHADOW_SMALL);
	}
	
	@Override
	public void animate() {
		super.animate();
		
		anim++;
		addSpr= (anim / 8) % 8;
		
	}
}
