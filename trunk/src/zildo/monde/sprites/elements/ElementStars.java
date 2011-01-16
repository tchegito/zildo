/**
 * Legend of Zildo
 * Copyright (C) 2006-2011 Evariste Boussaton
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

import java.util.ArrayList;
import java.util.List;

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

	public enum StarKind {
		STATIC(4, 8), TRAIL(2, 3);
		
		int delay;
		int radius;
		
		/**
		 * @param p_delay Delay until the next star
		 * @param p_radius Range of the star focus
		 */
		private StarKind(int p_delay, int p_radius) {
			delay=p_delay;
			radius=p_radius;
		}
	}
	
	int count=0;
	int delay=0;
	double alpha=0;
	StarKind kind;
	
	List<Element> linkeds;
	
	public ElementStars(StarKind p_kind, int p_x, int p_y) {
		x=p_x;
		y=p_y;
		kind=p_kind;
		visible=false;
		
		linkeds=new ArrayList<Element>();
		
	}

	private void spawnOne(int px, int py) {
		// Find a spot inside a circle around the initial point
		double r=Math.random() * kind.radius;
		double theta=2*Math.PI * Math.random();
		Element star=new ElementImpact((int) (px + r * Math.cos(theta)), 
								    (int) (py + r * Math.sin(theta)), ImpactKind.STAR_YELLOW, null);
		linkeds.add(star);
		EngineZildo.spriteManagement.spawnSprite(star);
		delay=(int) (Math.random() * 12 + kind.delay);
	}

	public void animate() {
		
		switch (kind) {
		case TRAIL:
			x+=Math.cos(alpha);
			y+=Math.sin(alpha);
			double beta=alpha-Math.PI/2;
			double gamma=alpha+Math.PI/2;
			boolean direction=true;
			for (Element e : linkeds) {
				double angle=beta;
				direction=!direction;
				if (!direction) {
					angle=gamma;
				}
				e.x+=0.5*Math.cos(angle);
				e.y+=0.5*Math.sin(angle);
			}
			alpha+=0.01f;
			break;
		}
		if (count == delay) {	// After the delay, create another one
			spawnOne((int) x, (int) y);
			count=0;
		}
		count++;
	}
	
	public void fall() {}
}
