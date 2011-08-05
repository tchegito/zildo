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

package zildo.monde.sprites.persos;

import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.persos.ia.PathFinderSquirrel;

public class PersoSquirrel extends PersoNJ {

    public PersoSquirrel() {
	    pathFinder = new PathFinderSquirrel(this);
	   
	    Element ombre=new Element();
	    ombre.setX(x);
	    ombre.setY(y-12);
	    ombre.setSprModel(ElementDescription.SHADOW);
	    addPersoSprites(ombre);
    }

    @Override
    public void finaliseComportement(int p_compteur_animation) {
	    // Move character's shadow
	    if (persoSprites.size() >0) {
	        Element ombre=persoSprites.get(0);
	        ombre.setX(x);
	        ombre.setY(y-1);
	        ombre.setZ(-7);
	        ombre.setVisible(z>=0);
	    }
	    super.finaliseComportement(p_compteur_animation);
    }
   
    @Override
    public void animate(int p_compteur_animation) {
        super.animate(p_compteur_animation);
   
        z=z+vz;
	    if (z<0) {
	        z=0;vz=0;az=0;
	    }
	    vz=vz+az;
    }
}
