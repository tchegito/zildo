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
import zildo.monde.map.Point;
import zildo.monde.map.Pointf;
import zildo.monde.map.Zone;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.persos.Perso;
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
	protected Point target;
	public float speed;	// Should be used if different of 0
	public boolean backward;	// Default FALSE. TRUE means character is steping back
	public boolean open;	// Default FALSE. TRUE means character can open doors.
	public boolean unstoppable;	// TRUE = no collision for this character
    protected int nbShock;				// Number of times character hit something going to his target

	public PathFinder(Perso p_mobile) {
		mobile=p_mobile;
		backward=false;
		open=p_mobile.isZildo();
		speed = 0.5f;
		unstoppable=false;
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
    
    /**
     * Set a location target(x,y) in the current perso, inside the movement area (zone_deplacement). 
     * This is where we assign a new position, horizontally and/or vertically depending on the 
     * character's script.
     */
    public void determineDestination() {
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
			if (j%2==0 || mvt.isDiagonal() )
				target.x+= (16*Math.random()*j) - 8*j;
	
			if (j%2==1 || mvt.isDiagonal() )
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

	public boolean hasReachedTarget() {
		float x=mobile.x;
		float y=mobile.y;
		if (x == target.x && y == target.y) {
			return true;
		}
		return (x >= target.x - 0.5f && x <= target.x + 0.5f && 
			    y >= target.y - 0.5f && y <= target.y + 0.5f);
	}
	
	public void collide() {
		if (mobile.getQuel_deplacement() == MouvementPerso.HEN ||
				mobile.getQuel_deplacement() == MouvementPerso.BEE) {
			target=null;
		} else {
			if (nbShock++ >= 3 && !mobile.isGhost()) {
				target=null;
				mobile.setAlerte(false);
				nbShock=0;
			}
			mobile.setAttente(10 + (int) (Math.random()*20));
		}
	}
	
	/**
	 * Common method for reaching a point by the shortest distance : a line.<br/>
	 * Set the angle accordingly to the direction.
	 */
	final protected Pointf reachLine(float p_speed, boolean p_twoAngles) {
		Pointf p =new Pointf(mobile.x, mobile.y);
		if (target != null) {
			float hypothenuse = target.distance(new Point(mobile.x, mobile.y));
			double cosAngle = (target.x - mobile.x) / hypothenuse;
			double sinAngle = (target.y - mobile.y) / hypothenuse;
			p.x+=cosAngle * p_speed;
			p.y+=sinAngle * p_speed;
			if (hypothenuse < p_speed) {
				target=null;
			}
			// Set the angle
			if (p_twoAngles) {
				if (cosAngle < 0) {
					mobile.setAngle(Angle.OUEST);
				} else {
					mobile.setAngle(Angle.EST);
				}
			}
		}
		return p;
	}

	public Point getTarget() {
		return target;
	}

	/**
	 * Overridable method, in order to do something special when the character gets a target.
	 * @param target
	 */
	public void setTarget(Point target) {
		this.target = target;
	}
}
