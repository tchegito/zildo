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

package zildo.monde.sprites.persos.ia;

import zildo.monde.Trigo;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.util.Pointf;
import zildo.server.EngineZildo;

/**
 * Jumping squirrel, in random direction.
 * 
 * @author Tchegito
 *
 */
public class PathFinderSquirrel extends PathFinder {

    final static int jumpDistance = 20;
    float alpha;
    int nbJump = 0;
    
    public PathFinderSquirrel(Perso p_mobile) {
    	super(p_mobile);
    	speed=1f;
    }
   
    @Override
    public void determineDestination() {
	    if (mobile.z == 0 && mobile.getAttente() == 0) {
	    	if (nbJump == 0) {
	    		alpha+=Math.random() - 0.5f;
	    		correctAlpha();
	        	nbJump = (int) (1 + Math.random() * 4);
	    	}
	        Pointf tempTarget = new Pointf(mobile.x + Math.cos(alpha) * jumpDistance,
	                    mobile.y + Math.sin(alpha) * jumpDistance);
	        nbJump--;
	        if (EngineZildo.mapManagement.collide(tempTarget.x, tempTarget.y, mobile) ||
	        		EngineZildo.persoManagement.collidePerso(Math.round(tempTarget.x), Math.round(tempTarget.y), mobile, 50) != null) {
	        	tempTarget = null;
	        	alpha+= Math.PI/2;
	    		correctAlpha();
	        } else {
	        	setTarget(tempTarget);
	        	//mobile.setAttente(20);
	        }
	    }
    }

    @Override
    public void setTarget(Pointf p_target) {
    	if (p_target != null) {
    		mobile.az = -0.1f;
    		mobile.vz = 1.2f;
    	}
    	super.setTarget(p_target);
    }
    
    /**
     * Adjust the moving angle, in order to avoid angles too much vertical.
     */
    private void correctAlpha() {
    	while (Math.abs(Math.cos(alpha)) < Trigo.cosPISur4) {
    		alpha+=Trigo.PI_SUR_4;
    	}
    }
    
    
    @Override
    public Pointf reachDestination(float p_speed) {
    	return reachLine(p_speed, true);
    }
}
