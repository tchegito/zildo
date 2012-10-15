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

package zildo.monde.collision;

import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;

public class Collision {

    public int cx, cy;	// Center
    public int cr;		// Radius
    public final Angle cangle; // Shooter's angle
    public final Point size; // Exact object's size (if not null, radius will be ignored)
    public final Perso perso; // Shooter
    public final DamageType damageType;
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
}