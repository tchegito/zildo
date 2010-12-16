/**
 * Legend of Zildo
 * Copyright (C) 2006-2010 Evariste Boussaton
 * Based on original Zelda : link to the past (C) Nintendo 1992
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

import zildo.monde.sprites.elements.ElementImpact.ImpactKind;
import zildo.server.EngineZildo;

/**
 * Special element dealing with stars.<p/>
 * 
 * It's just an element container, it does not render itself.
 * @author Tchegito
 *
 */
public class ElementStars extends Element {

	int count=0;
	int delay;
	
	public ElementStars(int p_x, int p_y) {
		x=p_x;
		y=p_y;
		visible=false;
		spawnOne();
	}

	private void spawnOne() {
		// Find a spot inside a circle around the initial point
		double r=Math.random() * 8;
		double alpha=2*Math.PI * Math.random();
		Element i=new ElementImpact((int) (x + r * Math.cos(alpha)), 
								    (int) (y + r * Math.sin(alpha)), ImpactKind.STAR_YELLOW, null);
		EngineZildo.spriteManagement.spawnSprite(i);
		delay=(int) (Math.random() * 12 + 4);
	}

	public void animate() {
		count++;
		if (count == delay) {	// After the delay, create another one
			spawnOne();
			count=0;
		}
	}
}
