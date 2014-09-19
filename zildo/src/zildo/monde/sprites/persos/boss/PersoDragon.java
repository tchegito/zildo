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

package zildo.monde.sprites.persos.boss;

import zildo.monde.sprites.Reverse;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.persos.PersoNJ;
import zildo.monde.sprites.utils.CompositeElement;

/**
 * @author Tchegito
 *
 */
public class PersoDragon extends PersoNJ {

	CompositeElement neck;
	
	double gamma;
	
	int[] seq = {3, 3, 2, 2, 1, 0, 4, 4};
	
	public PersoDragon(int x, int y) {
		//this.x = x;
		//this.y = y;
		pv = 5;
		setInfo(PersoInfo.ENEMY);
		
		desc = PersoDescription.DRAGON;
		neck = new CompositeElement(this);
		neck.lineShape(seq.length);

	}
	
	@Override
	public void animate(int compteur_animation) {
		super.animate(compteur_animation);
		
		int nth=0;
		double beta = gamma;
		double iota = 0;
		float xx=x, yy=y, zz=z; 
		for (int i=0;i<neck.elems.size()-1;i++) {
			Element e = neck.elems.get(i+1);
			if (i >= 6) {	// Wings
				e.x = neck.elems.get(3).x - 40;
				e.y = neck.elems.get(3).y+100;// + 100;
				e.z = neck.elems.get(3).z + 60;// + 100; // - 40;
				if (i == 7) {
					e.x = e.x +80;
					e.reverse = Reverse.HORIZONTAL;
				}
			} else {
				e.x = xx + (float) (3 * Math.cos(beta * 0.7) + 12 * Math.sin(iota));
				e.z = zz + 20 - (float) (2 * Math.sin(beta) + 2 * Math.cos(iota));
				if (i == 5) {	// Head
					e.z -= 30;
					e.x -= 10;
					e.setForeground(true);
				}
				e.y = yy;
				//e.z = zz + 6;
			}
			e.setAddSpr(seq[i]);
			xx = e.x;
			yy = e.y;
			zz = e.z;
			beta += 0.01;
			iota += 0.001;
			nth++;
		}
		//refElement.setAddSpr(1);
		visible = false;
		
		//visible = true;
		gamma += 0.08;
	}
}
