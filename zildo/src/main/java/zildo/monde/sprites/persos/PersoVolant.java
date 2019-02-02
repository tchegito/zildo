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

package zildo.monde.sprites.persos;

import java.util.HashMap;
import java.util.Map;

import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.desc.SpriteDescription;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.persos.ia.PathFinderFlying;
import zildo.monde.sprites.persos.ia.PathFinderStraightFlying;
import zildo.monde.util.Point;
import zildo.monde.util.Zone;

public class PersoVolant extends PersoShadowed {

	final static Map<PersoDescription, Point> grabPoint = new HashMap<PersoDescription, Point>();

	static {
		grabPoint.put(PersoDescription.VAUTOUR, new Point(3, 10));
	}

	public PersoVolant(PersoDescription p_desc) {
		super();

		setCptMouvement(100);
		setForeground(true);
		setSpeed(2.0f);

		shadow.setX(x);
		shadow.setY(y - 12);
		shadow.setSprModel(ElementDescription.SHADOW_SMALL);

		switch (p_desc) {
		case OISEAU_VERT:
			pathFinder = new PathFinderStraightFlying(this, 35f, 8.3f);
			break;
		default:
			pathFinder = new PathFinderFlying(this);
			break;
		}
		pv = 2;
		desc = p_desc;
	}

	@Override
	public void finaliseComportement(int compteur_animation) {
		// Move character's shadow
		if (shadow != null) {
			shadow.setX(x);
			shadow.setY(y + 6);
			shadow.setVisible(z > 0);
		}
		super.finaliseComportement(compteur_animation);
	}

	@Override
	public void animate(int compteur_animation) {
		for (Element e : persoSprites) {
			SpriteDescription d = e.getDesc();
			if (!(d instanceof ElementDescription) || !((ElementDescription)d).isShadow()) {
				Point grabber = grabPoint.get(getDesc());
				if (grabber == null) {
					Zone sprZone = getZone();
					grabber = new Point((sprZone.x2 + sprZone.x1) / 2,
							(sprZone.y2 + sprZone.y1) / 2);
				}
				e.x = this.x + grabber.x;
				e.y = this.y + grabber.y;
				e.z = this.z;
			}
		}
		super.animate(compteur_animation);
	}
}
