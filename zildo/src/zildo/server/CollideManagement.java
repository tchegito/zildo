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

package zildo.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import zildo.client.sound.BankSound;
import zildo.monde.collision.Collision;
import zildo.monde.collision.DamageType;
import zildo.monde.collision.Rectangle;
import zildo.monde.items.ItemKind;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.magic.Affection.AffectionKind;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.Perso.PersoInfo;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.sprites.utils.MouvementPerso;
import zildo.monde.util.Point;
import zildo.server.state.ClientState;

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
    
    /**
     * Add collision in the way: from a Perso or Element.
     * @param p_colli
     */
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
            PersoInfo infoDamager = damager == null ? PersoInfo.ZILDO : damager.getInfo(); // If no one, consider it's from a Zildo

            if (infoDamager == PersoInfo.ENEMY) { // PNJ -> they attack zildo
                checkAllZildoWound(p_states, collider);
            } else if (infoDamager == PersoInfo.ZILDO) { // ZILDO -> he attacks PNJ or another Zildo
                // 2) For each collision, check wether a monster/zildo gets wounded
                for (int j = 0; j < tab_colli.size(); j++) {
                    Collision collided = tab_colli.get(j);
                    Perso damaged = collided.perso;
                    if (damaged != null) { // No one to damage : it's a bushes or rock
                    	PersoInfo infoDamaged = damaged.getInfo();

                        if (j != i && !damaged.equals(damager)) {
                            if (infoDamaged == PersoInfo.ENEMY || infoDamaged == PersoInfo.SHOOTABLE_NEUTRAL) { // Zildo hit an enemy
                                checkEnemyWound(collider, collided);
                            } else if (infoDamaged == PersoInfo.ZILDO) {
                                checkZildoWound((PersoPlayer) damaged, collider);
                            }
                        }
                    }
                }
                // Check if any Zildo is hurt
                checkAllZildoWound(p_states, collider);
            }
        	// Check if any tile is damaged (only with cutting/exploding damage)
            DamageType dmgType=collider.damageType;
            if (dmgType != null) {
            	if (dmgType.isCutting()) {
	            	Set<Point> tilesCollided=getTilesCollided(collider);
	    			// And ask 'map' object to react
	            	for (Point location : tilesCollided) {
	            		EngineZildo.mapManagement.getCurrentMap().attackTile(collider.perso.floor, location, null);
	            	}
            	} else if (dmgType == DamageType.SMASH) {
	            	Set<Point> tilesCollided=getTilesCollided(collider);
            		for (Point location : tilesCollided) {
            			EngineZildo.mapManagement.getCurrentMap().smashTile(location);
            		}
            	}
            }
        }
    }

    private void checkAllZildoWound(Collection<ClientState> p_states, Collision p_colli) {
        for (ClientState state : p_states) {
            PersoPlayer zildo = state.zildo;
            Perso damager = p_colli.perso;
            // Zildo can't damage himself, excepted with explosion
            if (zildo != null && (damager == null || !damager.equals(zildo) || p_colli.damageType == DamageType.EXPLOSION)) {
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
    public void checkZildoWound(PersoPlayer p_zildo, Collision p_colli) {
        Collision zildoCollision = p_zildo.getCollision();

        if ((p_colli.perso == null || !p_colli.perso.isWounded()) && checkColli(p_colli, zildoCollision)) {
            // Zildo gets wounded
        	hit(p_colli, zildoCollision);
        }
    }

    /**
     * Check wether the given collision hit the another one. Wound if needed.
     * @param p_collider
     * @param p_collided
     */
    public void checkEnemyWound(Collision p_collider, Collision p_collided) {
        if (checkColli(p_collided, p_collider)) {
        	// Check that collision doesn't happen on a projectile (not inside persoSprites)
        	List<Element> persoSprites = p_collided.perso.getPersoSprites();
        	if (p_collided.weapon == null || persoSprites == null || persoSprites.contains(p_collided.weapon)) {
        		hit(p_collider, p_collided);
        	}
        }
    }

    /**
     * A collision hit a character. Calls two methods:<br/>
     * <ul>
     * <li>{@link Perso#beingWounded()} on the collided character</li>
     * <li>{@link Element#beingCollided()} on the collider element</li>
     * </ul>
     * @param p_collider
     * @param p_collided
     */
    public void hit(Collision p_collider, Collision p_collided) {
        // Character gets wounded, if he isn't yet
    	Perso perso=p_collided.perso;
    	Element weapon=p_collided.weapon;
    	// Hurt perso if isn't already wounded, is visible, is not immaterial, and if he hasn't -1 as HP.
        if (perso != null && !perso.isWounded() && perso.getPv() != -1 
        		&& perso.isVisible() && perso.getQuel_deplacement() != MouvementPerso.IMMATERIAL) {
        	boolean persoResisting = perso.getDesc() == null ? false : perso.getDesc().resistToDamageType(p_collider.damageType);
        	
        	if (weapon != null) {
        		// Only parry with another weapon
       			perso.parry(p_collider.cx, p_collider.cy, perso);
        	} else {
        		// How much damage ?
        		int dmg = p_collider.damageType == null ? 1 : p_collider.damageType.getHP(perso);
        		
        		// Does this character resist to this kind of damage ?
        		// And is the collided immaterial ? (ex: poison => we can't hurt poison !)
        		boolean material = p_collided.damageType == null ? true : !p_collided.damageType.isImmaterial();
        		if (!persoResisting && material && dmg > 0) {
	        		Perso attacker=p_collider.perso;
	        		if (attacker != null && p_collider.perso.isZildo()) {
	        			PersoPlayer zildo=(PersoPlayer) attacker;
	        			if (zildo.isAffectedBy(AffectionKind.QUAD_DAMAGE)) {
	        				EngineZildo.soundManagement.broadcastSound(BankSound.QuadDamaging, zildo);
	        				dmg *= 4;
	        			}
	        			if (zildo.getWeapon() != null && zildo.getWeapon().kind == ItemKind.MIDSWORD) {
	        				dmg += 1;
	        			}
	        		}
	        		perso.beingWounded(p_collider.cx, p_collider.cy, p_collider.perso, dmg);
        		}
        	}
        	
            if (p_collider.weapon != null && !persoResisting) {
            	p_collider.weapon.beingCollided(perso);
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

    // Returns TRUE if both collision collapses
    public boolean checkColli(Collision p_collider, Collision p_collided) {
		int x1=p_collider.cx;
		int y1=p_collider.cy;
		int x2=p_collided.cx;
		int y2=p_collided.cy;
		int radius1=p_collider.cr;
		int radius2=p_collided.cr;
		Point size1=p_collider.size;
		Point size2=p_collided.size;
		
		// Check for same layer (except for flying ones)
		if (p_collider.perso != null && p_collided.perso != null) {
			if (p_collider.perso.isForeground() != p_collided.perso.isForeground() &&
					!p_collider.perso.flying) {
				return false;
			}
		}
		
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