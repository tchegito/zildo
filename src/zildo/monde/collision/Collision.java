package zildo.monde.collision;

import zildo.monde.map.Angle;
import zildo.monde.map.Point;
import zildo.monde.sprites.persos.Perso;

public class Collision {

    public int cx, cy;	// Center
    public int cr;		// Radius
    public Angle cangle; // Shooter's angle
    public Point size; // Exact object's size (if not null, radius will be ignored)
    public Perso perso; // Shooter
    public DamageType damageType;
    
    public Collision() {
    }

    public int getCx() {
        return cx;
    }

    public void setCx(int cx) {
        this.cx = cx;
    }

    public int getCy() {
        return cy;
    }

    public void setCy(int cy) {
        this.cy = cy;
    }

    public int getCr() {
        return cr;
    }

    public void setCr(int cr) {
        this.cr = cr;
    }

    public Angle getCangle() {
        return cangle;
    }

    public void setCangle(Angle cangle) {
        this.cangle = cangle;
    }

    public Perso getPerso() {
        return perso;
    }

    public void setPerso(Perso perso) {
        this.perso = perso;
    }

    public Collision(Point p_center, Point p_size, Perso p_shooter, DamageType p_type) {
    	this.cx=p_center.x;
    	this.cy=p_center.y;
    	this.size=p_size;
    	this.cangle = Angle.NORD;	// Default
    	this.perso=p_shooter;
    	this.damageType=p_type;
    }
    
    public Collision(int x, int y, int cr, Point size, Angle angle, Perso perso, DamageType p_type) {
        this.cx = x;
        this.cy = y;
        this.cr = cr;
        this.size = size;
        this.cangle = angle;
        this.perso = perso;
    	this.damageType=p_type;
    }
}