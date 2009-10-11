package zildo.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import zildo.monde.collision.Collision;
import zildo.monde.collision.DamageType;
import zildo.monde.collision.Rectangle;
import zildo.monde.map.Point;
import zildo.monde.map.Angle;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoZildo;

public class CollideManagement {

    private List<Collision> tab_colli; // Zones d'aggression des monstres

    // ////////////////////////////////////////////////////////////////////
    // Construction/Destruction
    // ////////////////////////////////////////////////////////////////////

    public CollideManagement() {
        tab_colli = new ArrayList<Collision>();
    }

    // /////////////////////////////////////////////////////////////////////////////////////
    // initFrame
    // /////////////////////////////////////////////////////////////////////////////////////
    // Initializes collision counters.
    // /////////////////////////////////////////////////////////////////////////////////////
    public void initFrame() {
        tab_colli.clear();
    }

    // /////////////////////////////////////////////////////////////////////////////////////
    // addCollision
    // /////////////////////////////////////////////////////////////////////////////////////
    // IN:zildoAttack (TRUE if this collision is FROM Zildo, FALSE otherwise)
    // x,y,rayon,angle : collision parameters
    // perso : perso who create this collision
    // /////////////////////////////////////////////////////////////////////////////////////
    public void addCollision(int x, int y, int rayon, Point size, Angle angle, Perso perso, DamageType damageType) {
        Collision colli = new Collision(x, y, rayon, angle, perso, damageType, null);
        addCollision(colli);
    }
    
    public void addCollision(Collision p_colli) {
    	tab_colli.add(p_colli);
    }

    // /////////////////////////////////////////////////////////////////////////////////////
    // manageCollisions
    // /////////////////////////////////////////////////////////////////////////////////////
    // Here we detect which character is touched and call 'beingWounded' on Perso objects.
    // Zildo or/and PersoNJ can be wounded
    // /////////////////////////////////////////////////////////////////////////////////////
    public void manageCollisions(Collection<ClientState> p_states) {
        for (int i = 0; i < tab_colli.size(); i++) {
            // 1) For each collision, check wether Zildo gets wounded
            Collision collider = tab_colli.get(i);

            Perso damager = collider.perso;
            int infoDamager = damager == null ? 2 : damager.getInfo(); // If no one, consider it's from a Zildo

            if (infoDamager == 1) { // PNJ -> they attack zildo
                checkAllZildoWound(p_states, collider);
            } else if (infoDamager == 2) { // ZILDO -> he attacks PNJ or another Zildo
                // 2) For each collision, check wether a monster/zildo gets wounded
                for (int j = 0; j < tab_colli.size(); j++) {
                    Collision collided = tab_colli.get(j);
                    Perso damaged = collided.perso;
                    if (damaged != null) { // No one to damage : it's a bushes or rock
                        int infoDamaged = damaged.getInfo();

                        if (j != i && !damaged.equals(damager)) {
                            if (infoDamaged == 1) { // Zildo hit an enemy
                                checkEnemyWound(collider, collided);
                            } else if (infoDamaged == 2) {
                                checkZildoWound((PersoZildo) damaged, collider);
                            }
                        }
                    }
                }
                // Check if any Zildo is hurt
                checkAllZildoWound(p_states, collider);
            }
        	// Check if any tile is damaged (only with cutting/exploding damage)
            DamageType dmgType=collider.damageType;
            if (dmgType != null && dmgType.isCutting()) {
            	Set<Point> tilesCollided=getTilesCollided(collider);
    			// And ask 'map' object to react
            	for (Point location : tilesCollided) {
            		EngineZildo.mapManagement.getCurrentMap().attackTile(location);
            	}
            }
        }
    }

    private void checkAllZildoWound(Collection<ClientState> p_states, Collision p_colli) {
        for (ClientState state : p_states) {
            PersoZildo zildo = state.zildo;
            Perso damager = p_colli.perso;
            if (damager == null || !damager.equals(zildo)) {
                checkZildoWound(state.zildo, p_colli);
            }
        }
    }

    /**
     * Returns a set containing all tiles hit by provided collision.
     * @param p_colli
     * @return List<Point>
     */
    private Set<Point> getTilesCollided(Collision p_colli) {
    	Set<Point> tilesLocation=new HashSet<Point>();
		Perso perso=p_colli.perso;
    	if (p_colli.damageType==DamageType.CUTTING_FRONT && perso != null) {
    		Point loc=new Point(perso.x, perso.y);
    		loc=loc.multiply(1/16f);
    		loc.add(perso.getAngle().coords);
    		tilesLocation.add(loc);
    	} else {
	    	Point center=new Point(p_colli.cx, p_colli.cy);
	    	Point size=p_colli.size;
	    	if (size == null) {	// If collision is circular, consider it as a square
	    		size=new Point(p_colli.cr, p_colli.cr);
	    	}
	    	Rectangle rect=new Rectangle(center, size);
	    	
	    	//rect.scale(1-(16f / size.x), 1-(16f / size.y));
	    	rect.multiply(1/16f);	// Adapt tile coordinate (one tile is 16x16 sized)
	    	Point cornerTopLeft=rect.getCornerTopLeft();
	    	Point cornerBottomRight=cornerTopLeft.translate(rect.getSize());
	    	for (int j=cornerTopLeft.y;j<=cornerBottomRight.y;j++) {
	    		for (int i=cornerTopLeft.x;i<=cornerBottomRight.x;i++) {
	    			tilesLocation.add(new Point(i,j));
	    		}
	    	}
    	}
    	return tilesLocation;
    }
    
    /**
     * Check wether the given collision hit the given Zildo. Wound if needed.
     * @param p_zildo
     * @param p_colli
     */
    public void checkZildoWound(PersoZildo p_zildo, Collision p_colli) {
        float zildoX = p_zildo.getX() - 4;
        float zildoY = p_zildo.getY() - 10;
        // If he's already wounded, don't check
        Collision zildoCollision = new Collision((int) zildoX, (int) zildoY, 8, null, p_zildo, null, null);

        if (checkColli(p_colli, zildoCollision)) {
            // Zildo gets wounded
        	wound(p_colli, p_zildo);
        }
    }

    /**
     * Check wether the given collision hit the another one. Wound if needed.
     * @param p_collider
     * @param p_collided
     */
    public void checkEnemyWound(Collision p_collider, Collision p_collided) {
        if (checkColli(p_collided, p_collider)) {
            wound(p_collider, p_collided.perso);
        }
    }

    /**
     * A collision hit a character. Calls two methods:
     * -{@link Perso#beingWounded()} on the collided character
     * -{@link Element#beingCollided()} on the collider element
     * @param p_collider
     * @param p_collided
     */
    public void wound(Collision p_collider, Perso p_collided) {
        // Character gets wounded, if he isn't yet
        if (p_collided != null && !p_collided.isWounded()) {
        	p_collided.beingWounded(p_collider.cx, p_collider.cy, p_collider.perso);
            
            if (p_collider.weapon != null) {
            	p_collider.weapon.beingCollided();
            }
        }
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
    public boolean checkCollisionCircles(int x, int y, int a, int b, int r, int rayon) {
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

    public boolean checkColli(Collision p_collider, Collision p_collided) {
		int x1=p_collider.cx;
		int y1=p_collider.cy;
		int x2=p_collided.cx;
		int y2=p_collided.cy;
		int radius1=p_collider.cr;
		int radius2=p_collided.cr;
		Point size1=p_collider.size;
		Point size2=p_collided.size;
        // Check for each
        if (size1 == null && size2 == null) {
            // Collision between 2 circles
            return checkCollisionCircles(x1, y1, x2, y2, radius1, radius2);
        } else if (size2 == null) {
            // Collision between 1 rectangle and 1 circle
            return new Rectangle(new Point(x1, y1), size1).isCrossingCircle(new Point(x2, y2), radius2);
        } else if (size1 == null && size2 != null) {
            // Idem
            return new Rectangle(new Point(x2, y2), size2).isCrossingCircle(new Point(x1, y1), radius1);
        } else {
            // Collision between 2 rectangles
            return new Rectangle(new Point(x1, y1), size1).isCrossing(new Rectangle(new Point(x2, y2), size2));
        }
    }

    public List<Collision> getTabColli() {
        return tab_colli;
    }
}