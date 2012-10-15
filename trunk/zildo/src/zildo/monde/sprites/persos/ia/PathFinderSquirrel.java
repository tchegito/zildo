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

package zildo.monde.sprites.persos.ia;

import zildo.monde.sprites.persos.Perso;
import zildo.monde.util.Point;
import zildo.monde.util.Pointf;
import zildo.server.EngineZildo;

public class PathFinderSquirrel extends PathFinder {

    final static int jumpDistance = 20;
    float alpha;
    int nbJump = 0;
    
    private final double cosPISur4 = Math.cos(Math.PI / 4);	// Remember this result
    
    public PathFinderSquirrel(Perso p_mobile) {
    	super(p_mobile);
    	speed=1f;
    }
   
    @Override
    public void determineDestination() {
	    if (mobile.z == 0) {
	    	if (nbJump == 0) {
	    		alpha+=Math.random() - 0.5f;
	    		correctAlpha();
	        	nbJump = (int) (1 + Math.random() * 4);
	    	}
	        Point tempTarget = new Point((int) (mobile.x + Math.cos(alpha) * jumpDistance),
	                    (int) (mobile.y + Math.sin(alpha) * jumpDistance));
	        nbJump--;
	        if (EngineZildo.mapManagement.collide(tempTarget.x, tempTarget.y, mobile) ||
	        		EngineZildo.persoManagement.collidePerso(tempTarget.x, tempTarget.y, mobile, 50) != null) {
	        	tempTarget = null;
	        	alpha+= Math.PI/2;
	    		correctAlpha();
	        } else {
	        	setTarget(tempTarget);
	        }
	    }
    }

    @Override
    public void setTarget(Point p_target) {
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
    	while (Math.abs(Math.cos(alpha)) < cosPISur4) {
    		alpha+=Math.PI / 4;
    	}
    }
    
    
    @Override
    public Pointf reachDestination(float p_speed) {
    	return reachLine(p_speed, true);
    }
}
