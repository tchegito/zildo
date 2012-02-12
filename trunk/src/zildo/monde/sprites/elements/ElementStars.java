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

import zildo.client.sound.BankSound;
import zildo.monde.sprites.elements.ElementImpact.ImpactKind;
import zildo.monde.util.Point;
import zildo.server.EngineZildo;

/**
 * Special element dealing with stars.
 * <p/>
 * 
 * It's just an element container, it does not render itself.
 * 
 * @author Tchegito
 * 
 */
public class ElementStars extends ElementChained {

	public enum StarKind {
		STATIC(4, 8), // Static star
		TRAIL(2, 3), // Trail turning around a zone
		CIRCLE(3, 0); // Stars drawing a circle (just one)

		int delay;
		int radius;

		/**
		 * @param p_delay
		 *            Delay until the next star
		 * @param p_radius
		 *            Range of the star focus
		 */
		private StarKind(int p_delay, int p_radius) {
			delay = p_delay;
			radius = p_radius;
		}
	}

	double iota = 0;
	StarKind kind;

	public ElementStars(StarKind p_kind, int p_x, int p_y) {
		super(p_x, p_y);

		kind = p_kind;
		visible = false;

		if (kind == StarKind.CIRCLE) {
			EngineZildo.soundManagement.broadcastSound(BankSound.Sort,
					new Point(p_x, p_y));
		}
	}

	@Override
	protected Element createOne(int px, int py) {
		// Find a spot inside a circle around the initial point
		double r = Math.random() * kind.radius;
		double theta = 2 * Math.PI * Math.random();
		Element star = new ElementImpact((int) (px + r * Math.cos(theta)),
				(int) (py + r * Math.sin(theta)), ImpactKind.STAR_YELLOW, null);
		star.setForeground(true);
		delay = (int) (Math.random() * 12 + kind.delay);
		return star;
	}

	@Override
	public void animate() {

		switch (kind) {
		case TRAIL:
			x += Math.cos(iota);
			y += Math.sin(iota);
			double beta = iota - Math.PI / 2;
			double gamma = iota + Math.PI / 2;
			boolean direction = true;
			for (Element e : linkeds) {
				double theta = beta;
				direction = !direction;
				if (!direction) {
					theta = gamma;
				}
				e.x += 0.5 * Math.cos(theta);
				e.y += 0.5 * Math.sin(theta);
			}
			iota += 0.01f;
			break;
		case CIRCLE:
			x = (float) (initialLocation.x + 8f * Math.cos(iota));
			y = (float) (initialLocation.y + 5f * Math.sin(iota));
			iota += 0.06f;
			if (iota > Math.PI * 2) {
				dying = true; // Just 1 turn
			}
			break;
		}
		super.animate();
	}

}
