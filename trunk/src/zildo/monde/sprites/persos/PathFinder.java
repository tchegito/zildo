/**
 * Legend of Zildo
 * Copyright (C) 2006-2010 Evariste Boussaton
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

import zildo.monde.map.Angle;
import zildo.monde.map.Point;
import zildo.monde.map.Pointf;
import zildo.monde.map.Zone;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.utils.MouvementPerso;

/**
 * Deals with AI for characters.<p/>
 * 
 * It's really basic. We set target and speed, then character moves to it.<p/>
 * 
 * Features:<ul>
 * <li>zone moves (random location in a rectangle region)</li>
 * <li>circular moves</li>
 * </ul>
 * 
 * @author Tchegito
 *
 */
public class PathFinder {

	Perso mobile;
	Point target;
	float speed;	// Should be used if different of 0
	boolean backward;	// Default FALSE. TRUE means character is steping back
	boolean open;	// Default FALSE. TRUE means character can open doors.
	
	public PathFinder(Perso p_mobile) {
		mobile=p_mobile;
		backward=false;
		open=p_mobile.isZildo();
	}
	
    /**
     * Shouldn't modify heros location !
     * @param p_speed
     * @return int
     */
    public Pointf reachDestination(float p_speed) {

        float x=mobile.x;
        float y=mobile.y;
        Pointf pos = new Pointf(x, y);
        if (target==null) {
            return pos;
        }
        
    	float velocity=speed == 0 ? p_speed : speed;
        int immo = 0;
        Angle a=mobile.getAngle();
        
        if (x < target.x - 0.5f) {
            pos.x += velocity;
            if (pos.x > target.x) {
                pos.x = target.x;
            }
            a=Angle.EST;
        } else if (x > target.x + 0.5f) {
            pos.x -= velocity;
            if (pos.x < target.x) {
                pos.x = target.x;
            }
            a=Angle.OUEST;
        } else {
            immo++;
        }
        if (y < target.y - 0.5f) {
            pos.y += velocity;
            if (pos.y > target.y + 0.5f) {
                pos.y = target.y;
            }
            a=Angle.SUD;
        } else if (y > target.y + 0.5f) {
            pos.y -= velocity;
            if (pos.y < target.y - 0.5f) {
                pos.y = target.y;
            }
            a=Angle.NORD;
        } else {
            immo++;
        }

        // If there's no movement, stop the target
        if (immo == 2) {
            target=null;
        }

        if (backward && a!= null) {
        	a=Angle.rotate(a, 2);
        }
        mobile.setAngle(a);
        
        return pos;
    }
    
    public void reachDestinationFlying() {
    	if (target == null) {
    		return;
    	}
		double alpha=Math.PI*(mobile.getCptMouvement()/100.0f)-Math.PI/2.0f;
		mobile.z=(float) (2.0f+10.0f*Math.sin(alpha+Math.PI/2.0f));
		alpha=(Math.PI/100.0f)*Math.cos(alpha);
		mobile.x+=target.x*alpha;
		mobile.y+=target.y*alpha;
		if (target.x<0) {
			mobile.setAngle(Angle.EST);
		} else mobile.setAngle(Angle.NORD);    	
    }
	
	///////////////////////////////////////////////////////////////////////////////////////
	// determineDestination (SCRIPT_ZONE)
	///////////////////////////////////////////////////////////////////////////////////////
	// Set a location target(x,y) in the current perso, inside the movement area (zone_deplacement)
	// This is where we assign a new position, horizontally and/or vertically depending on the
	// character's script.
	///////////////////////////////////////////////////////////////////////////////////////
	void determineDestination() {
		int j=13+3;
        float x=mobile.x;
        float y=mobile.y;
        
        if (SpriteEntity.ENTITYTYPE_PERSO != mobile.getEntityType()) {
        	return;
        }
        
        MouvementPerso mvt=mobile.getQuel_deplacement();
        Zone zone=mobile.getZone_deplacement();
        
		while (true) {
			target=new Point(x, y);
	
			// On déplace le perso soit horizontalement, soit verticalement,
			// ou les 2 si c'est une poule. Car les poules ont la bougeotte.
			if (j%2==0 || MouvementPerso.persoDiagonales.contains(mvt) )
				target.x+= (16*Math.random()*j) - 8*j;
	
			if (j%2==1 || MouvementPerso.persoDiagonales.contains(mvt) )
				target.y+= (16*Math.random()*j) - 8*j;
	
			j--; // On diminue le rayon jusqu'à être dans la zone
	
			if ((target.x>=zone.getX1() && target.y>=zone.getY1() &&
				 target.x<=zone.getX2() && target.y<=zone.getY2()) ||
				(j==-1) )
				break;
		}
	
	    if (j==-1) {  // En cas de pépin
			target.x=zone.getX1();
			target.y=zone.getY1();
	    }
	}
	
	/**
	 * Determine destination for SCRIPT_ABEILLE.
	 */
	void determineDestinationBee() {
        float x=mobile.x;
        float y=mobile.y;
		target=new Point();
		target.x=(int)(x+(5.0f+Math.random()*10.0f)*Math.cos(2.0f*Math.PI*Math.random()));
		target.y=(int)(y+(5.0f+Math.random()*10.0f)*Math.sin(2.0f*Math.PI*Math.random()));
	}
	
	/**
	 * Determine destination for SCRIPT_CORBEAU
	 */
	void determineDestinationFlying() {
        float x=mobile.x;
        float y=mobile.y;
		determineDestination();
		target.x=(int)((target.x+Math.random()*20.0f-10.0f-x)/2);
		target.y=(int)((target.y+Math.random()*20.0f-10.0f-y)/2);
	}

	public boolean hasReachedTarget() {
		float x=mobile.x;
		float y=mobile.y;
		if (x == target.x && y == target.y) {
			return true;
		}
		return (x >= target.x - 0.5f && x <= target.x + 0.5f && 
			    y >= target.y - 0.5f && y <= target.y + 0.5f);
	}
}
