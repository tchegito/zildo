package zildo.monde.sprites.persos;

import zildo.monde.map.Angle;
import zildo.monde.map.Point;
import zildo.monde.map.Pointf;
import zildo.monde.map.Zone;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.elements.Element;
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

	Element mobile;
	Point target;
	float speed;	// Should be used if different of 0
	
	public PathFinder(Element p_mobile) {
		mobile=p_mobile;
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
        
        if (x < target.x - 0.5f) {
            pos.x += velocity;
            if (pos.x > target.x) {
                pos.x = target.x;
            }
            mobile.setAngle(Angle.EST);
        } else if (x > target.x + 0.5f) {
            pos.x -= velocity;
            if (pos.x < target.x) {
                pos.x = target.x;
            }
            mobile.setAngle(Angle.OUEST);
        } else {
            immo++;
        }
        if (y < target.y - 0.5f) {
            pos.y += velocity;
            if (pos.y > target.y + 0.5f) {
                pos.y = target.y;
            }
            mobile.setAngle(Angle.SUD);
        } else if (y > target.y + 0.5f) {
            pos.y -= velocity;
            if (pos.y < target.y - 0.5f) {
                pos.y = target.y;
            }
            mobile.setAngle(Angle.NORD);
        } else {
            immo++;
        }

        if (immo == 2) {
            target=null;
        }

        return pos;
    }
    
	
	///////////////////////////////////////////////////////////////////////////////////////
	// determineDestination
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
        
        Perso perso=(Perso) mobile;
        MouvementPerso mvt=perso.getQuel_deplacement();
        Zone zone=perso.getZone_deplacement();
        
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
	
	void determineDestinationBee() {
        float x=mobile.x;
        float y=mobile.y;
		target=new Point();
		target.x=(int)(x+(5.0f+Math.random()*10.0f)*Math.cos(2.0f*Math.PI*Math.random()));
		target.y=(int)(y+(5.0f+Math.random()*10.0f)*Math.sin(2.0f*Math.PI*Math.random()));
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
