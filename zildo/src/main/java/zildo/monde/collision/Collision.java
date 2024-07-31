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

package zildo.monde.collision;

import zildo.monde.Trigo;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
import zildo.monde.util.Pointf;

public class Collision {

    public int cx, cy;	// Center
    public int cr;		// Radius
    public final Angle cangle; // Shooter's angle
    public final Point size; // Exact object's size (if not null, radius will be ignored)
    public final Perso perso; // Shooter
    public DamageType damageType;
    public final Element weapon;	// Element which causes direct collision (boomerang, projectile...)

    /**
     * Create a bounding box collision
     * @param p_center
     * @param p_size
     * @param p_shooter
     * @param p_type
     * @param p_weapon TODO
     */
    public Collision(Point p_center, Point p_size, Perso p_shooter, DamageType p_type, Element p_weapon) {
    	this.cx=p_center.x;
    	this.cy=p_center.y;
    	this.size=p_size;
    	this.cangle = Angle.NORD;	// Default
    	this.perso=p_shooter;
    	this.damageType=p_type;
    	this.weapon=p_weapon;
    }
    
    /**
     * Create a circular collision
     * @param x
     * @param y
     * @param cr
     * @param angle
     * @param perso
     * @param p_type
     * @param p_weapon TODO
     */
    public Collision(int x, int y, int cr, Angle angle, Perso perso, DamageType p_type, Element p_weapon) {
        this.cx = x;
        this.cy = y;
        this.cr = cr;
        this.size = null;
        this.cangle = angle;
        this.perso = perso;
    	this.damageType=p_type;
    	this.weapon=p_weapon;
    }
    
    // Special case: dragon cause damage whatever the floor is
    public boolean isMultifloor() {
    	if (perso != null && perso.getDesc() != null) {
	    	switch (perso.getDesc()) {
	    	case CHAUVESOURIS:
	    	case DRAGON:
	    		return true;
	    	}
    	}
    	return false;
    }
    
    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	sb.append("HitBox(x="+cx+",y="+cy);
    	if (size != null) {
    		sb.append(" - size="+size);
    	} else {
    		sb.append(" - radius="+cr);
    	}
    	sb.append(" type="+damageType);
    	sb.append(")");
    	return sb.toString();
    }
    
    ///////////////////////////////////////////////////////////////////////////////
    // check_colli
    // /////////////////////////////////////////////////////////////////////////////////////
    // IN:(x,y) coordinates of the first character
    // (a,b) coordinates of the second character
    // r : radius of the first character
    // rayon: radius of the second character
    // /////////////////////////////////////////////////////////////////////////////////////
    // Return true if two characters are colliding.
    // It's called with potential location in a move. Usually, if this method returns true,
    // previous coordinates will be kept.
    // /////////////////////////////////////////////////////////////////////////////////////
    public static boolean checkCollisionCircles(int x, int y, int a, int b, int r, int rayon) {
        // Juste des maths...
        int c = Math.abs(x - a);
        int d = Math.abs(y - b);
        if (c < 50 && d < 50) {
            c = c * c;
            c += d * d;
            c = (int) Math.sqrt(c);
            return (c < (r + rayon));
        } else {
            return false;
        }
    }
    
    /** Calculate the middle of intersecting zones between 2 circles **/
    public static Point hitPointOnCircles(int x, int y, int a, int b, int r, int rayon) {
    	double angle = Trigo.getAngleRadian(a-x, b-y);
    	Point e1 = new Pointf(x,y).translate(Trigo.vect(angle, r)).toPoint();
    	Point e2 = new Pointf(a,b).translate(Trigo.vect(angle, -rayon)).toPoint();
    	return Point.middle(e1, e2);
    }
    
    @Override
    public int hashCode() {
    	int n = cx << 20;
    	n |= cy << 10;
    	if (size != null) {
    		n |= size.hashCode();
    	} else {
    		n |= cr;
    	}
    	return n;
    }
}