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

package zildo.monde.sprites.persos.ia;

import zildo.monde.map.Angle;
import zildo.monde.map.Pointf;
import zildo.monde.sprites.persos.Perso;

/**
 * @author Tchegito
 *
 */
public class PathFinderFlying extends PathFinder {

	
	/**
	 * @param p_mobile
	 */
	public PathFinderFlying(Perso p_mobile) {
		super(p_mobile);
	}

	/**
	 * Determine destination for SCRIPT_CORBEAU
	 */
	@Override
	public void determineDestination() {
        float x=mobile.x;
        float y=mobile.y;
		super.determineDestination();
		target.x=(int)((target.x+Math.random()*20.0f-10.0f-x)/2);
		target.y=(int)((target.y+Math.random()*20.0f-10.0f-y)/2);
	}
	
    
	@Override
    public Pointf reachDestination(float p_speed) {
    	if (target == null) {
    		return null;
    	}
		double alpha=Math.PI*(mobile.getCptMouvement()/100.0f)-Math.PI/2.0f;
		mobile.z=(float) (2.0f+10.0f*Math.sin(alpha+Math.PI/2.0f));
		alpha=(Math.PI/100.0f)*Math.cos(alpha);
		mobile.x+=target.x*alpha;
		mobile.y+=target.y*alpha;
		if (target.x<0) {
			mobile.setAngle(Angle.EST);
		} else {
			mobile.setAngle(Angle.NORD);    	
		}
		return new Pointf(mobile.x, mobile.y);
    }
    
}
