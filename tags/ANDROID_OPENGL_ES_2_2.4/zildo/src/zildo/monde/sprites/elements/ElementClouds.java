/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
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

import zildo.fwk.ZUtils;
import zildo.monde.sprites.elements.ElementImpact.ImpactKind;

/**
 * @author Tchegito
 *
 */
public class ElementClouds extends ElementChained {

	int nCloud=0;
	
	public ElementClouds(int p_x, int p_y) {
		super(p_x, p_y);
		setForeground(true);
	}
	
	@Override
	protected Element createOne(int p_x, int p_y) {
		int px = p_x + ZUtils.randomRange(14);
		int py = p_y + ZUtils.randomRange(14);
		delay = 2 + (int) Math.random() * 5;
		
		nCloud++;
		if (nCloud == 8) {	// Stop the animation about 8 sprites
			dying = true;
		}
		return new ElementImpact(px, py, ImpactKind.SMOKE, null);
	}

}
