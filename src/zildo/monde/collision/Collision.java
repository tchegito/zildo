package zildo.monde.collision;

import zildo.monde.map.Angle;
import zildo.monde.map.Point;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.persos.Perso;

public class Collision {

    public int cx, cy;	// Center
    public int cr;		// Radius
    public Angle cangle; // Shooter's angle
    public Point size; // Exact object's size (if not null, radius will be ignored)
    public Perso perso; // Shooter
    public DamageType damageType;
    public Element weapon;	// Element which causes direct collision (boomerang, projectile...)
    
    public Collision() {
    }

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