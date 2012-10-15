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

package zildo.monde.sprites.persos;

import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.persos.ia.PathFinderSquirrel;

public class PersoSquirrel extends PersoShadowed {

    public PersoSquirrel(PersoDescription p_desc) {
    	super();
    	
    	// This perso could be from two descriptions
    	switch (p_desc) {
    	case LAPIN:
    		shadow.setDesc(ElementDescription.SHADOW);
    		break;
    	case PRINCESS_BUNNY:
    		shadow.setDesc(ElementDescription.SHADOW_SMALL);
    		break;
    	}
	    pathFinder = new PathFinderSquirrel(this);
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
