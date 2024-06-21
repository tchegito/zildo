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
import zildo.monde.map.Area;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.magic.Affection.AffectionKind;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.Perso.PersoInfo;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.sprites.utils.FlagPerso;
import zildo.monde.util.Point;
import zildo.server.state.ClientState;

public class CollideManagement {

    private List<Collision> tab_colli; // Zones d'aggression des monstres
    private List<Collision> tab_floorColli;	// Collision on the ground (issued from SpriteEntity)
    
    // ////////////////////////////////////////////////////////////////////
    // Construction/Destruction
    // ////////////////////////////////////////////////////////////////////

    public CollideManagement() {
        tab_colli = new ArrayList<Collision>();
        tab_floorColli = new ArrayList<Collision>();
    }

    // /////////////////////////////////////////////////////////////////////////////////////
    // initFrame
    // /////////////////////////////////////////////////////////////////////////////////////
    // Initializes collision counters.
    // /////////////////////////////////////////////////////////////////////////////////////
    public void initFrame() {
        tab_colli.clear();
        tab_floorColli.clear();
    }
    
    /**
     * Add collision in the way: from a Perso or Element.
     * @param p_colli
     */
    public void addCollision(Collision p_colli) {
    	tab_colli.add(p_colli);
    }

    public void addFloorCollision(Collision p_colli) {
    	tab_floorColli.add(p_colli);
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
                	if (j != i) { 
	                    Collision collided = tab_colli.get(j);
	                    Perso damaged = collided.perso;
	                    if (damaged != null) { // No one to damage : it's a bushes or rock
	                    	PersoInfo infoDamaged = damaged.getInfo();
	
	                        if (!damaged.equals(damager)) {
	                            if (infoDamaged == PersoInfo.ENEMY || infoDamaged == PersoInfo.SHOOTABLE_NEUTRAL) { // Zildo hit an enemy
	                                checkEnemyWound(collider, collided);
	                            } else if (infoDamaged == PersoInfo.ZILDO) {
	                                checkZildoWound(damaged, collider);
	                            }
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
            	Area area = EngineZildo.mapManagement.getCurrentMap();
            	Set<Point> tilesCollided=getTilesCollided(collider);

            	// TODO: maybe merge all 'for' tilesCollided
            	switch (dmgType) {
	            	case CUTTING_FRONT:
	            	case EXPLOSION:
		    			// And ask 'map' object to react
		            	int floor = area.getHighestFloor();
		        		if (collider.perso != null) {
		        			floor = collider.perso.floor;
		        		}
		            	for (Point location : tilesCollided) {
		            		area.attackTile(floor, location, null);
		            	}
		            	break;
	            	case SMASH:
	            		for (Point location : tilesCollided) {
	            			area.smashTile(location);
	            		}
	            		break;
	            	case FORKING:
		            	for (Point location : tilesCollided) {
		            		if (damager.isZildo() && ((PersoPlayer)damager).canFork()) {
		            			area.forkTile(damager, location);
		            		}	
		            	}
            	}
            }
        }
        
        // Break with the client/server paradigm: we deal with a single hero, as multiplayer isn't supported anymore
        PersoPlayer hero = p_states.iterator().next().zildo;
        if (hero != null) {
	        Collision zildoFeetCollision = new Collision(new Point(hero.x, hero.y), new Point(6,4), hero, null, null);	
	
	        
	        for (int i = 0; i < tab_floorColli.size(); i++) {
	            // We know that we only deal with collision on the ground damaging the hero
	            Collision collider = tab_floorColli.get(i);
	
	            if ((collider.perso == null || !collider.perso.isWounded()) && checkColli(collider, zildoFeetCollision)) {
	            	if (collider.perso == null || (collider.isMultifloor() || hero.floor == collider.perso.floor)) {
	            		// Zildo gets wounded
	            		hit(collider, zildoFeetCollision);
	            	}
	            }
	        }
        }
    }

    private void checkAllZildoWound(Collection<ClientState> p_states, Collision p_colli) {
        Perso damager = p_colli.perso;
        for (ClientState state : p_states) {
            PersoPlayer zildo = state.zildo;
            // Zildo can't damage himself, excepted with explosion
            if (zildo != null && (damager == null || !damager.equals(zildo) || p_colli.damageType == DamageType.EXPLOSION)) {
                checkZildoWound(state.zildo, p_colli);
            }
        }
        // Check collision for characters woundable by enemies
        // Same side than hero, but not him (exemple: coal)
        for (Perso p : EngineZildo.persoManagement.tab_perso) {
        	if (p.getInfo() == PersoInfo.ZILDO && !p.isZildo()) {
                checkZildoWound(p, p_colli);
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
	    	
	    	rect.multiply(1/16f);	// Adapt tile coordinate (one tile is 16x16 sized)
	    	Point cornerTopLeft=rect.getCornerTopLeft();
	    	Point cornerBottomRight=rect.getCornerBottomRight();
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
    public void checkZildoWound(Perso p_zildo, Collision p_colli) {
        Collision zildoCollision = p_zildo.getCollision();

        if ((p_colli.perso == null || !p_colli.perso.isWounded()) && checkColli(p_colli, zildoCollision)) {
        	if (p_colli.perso == null || (p_colli.isMultifloor() || p_zildo.floor == p_colli.perso.floor)) {
        		// Zildo gets wounded
        		hit(p_colli, zildoCollision);
        	}
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
        		if (p_collided.isMultifloor() || (p_collider.perso != null && p_collider.perso.floor == p_collided.perso.floor))
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
		Perso attacker=p_collider.perso;
    	Element weapon=p_collided.weapon;
    	// Hurt perso if isn't already wounded, is visible, is not immaterial (attacker too), and if he hasn't -1 as HP.
    	// Note that an immaterial character can throw REAL projectile and hurt (see condition about collider.weapon)
        if (perso != null && !perso.isWounded() && (perso.getFlagBehavior() & FlagPerso.F_INVULNERABLE ) == 0
        		&& perso.isVisible() && (perso.getFlagBehavior() & FlagPerso.F_IMMATERIAL) == 0
        		&& (attacker == null || (attacker.getFlagBehavior() & FlagPerso.F_IMMATERIAL) == 0 || p_collider.weapon != null)) {
        	boolean persoResisting = perso.getDesc() == null ? false : perso.getDesc().resistToDamageType(p_collider.damageType);
        	
        	// Does this an affection ?
        	if (!persoResisting && p_collider.damageType == DamageType.SLOWNESS) {
        		p_collided.perso.affect(AffectionKind.SLOWNESS);
        	} else if (p_collider.weapon != null && p_collider.weapon.getDesc() == ElementDescription.POISONBALL) {
        		p_collided.perso.affect(AffectionKind.SLOWNESS);
        	} else if (weapon != null) {
        		// Only parry with another weapon
       			perso.parry(p_collider.cx, p_collider.cy, perso);
        	} else {
        		// How much damage ?
        		int dmg = p_collider.damageType == null ? 1 : p_collider.damageType.getHP(perso);
        		
        		// Does this character resist to this kind of damage ?
        		// And is the collided immaterial ? (ex: poison => we can't hurt poison !)
        		boolean material = p_collided.damageType == null ? true : !p_collided.damageType.isImmaterial();
        		if (!persoResisting && material && dmg > 0) {

	        		if (attacker != null && p_collider.perso.isZildo()) {
	        			PersoPlayer zildo=(PersoPlayer) attacker;
	        			if (zildo.isAffectedBy(AffectionKind.QUAD_DAMAGE)) {
	        				EngineZildo.soundManagement.broadcastSound(BankSound.QuadDamaging, zildo);
	        				dmg *= 4;
	        			}
	        			// Check if collider is something else than current hero's weapon
	        			if (p_collider.weapon == null && zildo.getWeapon() != null && zildo.getWeapon().kind == ItemKind.MIDSWORD) {
	        				dmg += 1;
	        			}
	        			// If collider is damageable (thrown pots for example), break it
		        		if (p_collider.weapon != null && p_collider.weapon.getDesc().isDamageable()) {
		        			if (p_collider.weapon.fall()) {
		        				p_collider.weapon.die();
		        			}
		        		}
	        		}
	        		// Calculate crossing point overlapping 2 areas
	        		Point hitPoint = new Point(p_collider.cx, p_collider.cy);
	        		if (p_collider.damageType == DamageType.PEEBLE) {
	        			// Use peeble direction for future projection
	        			hitPoint = new Point(perso.x - p_collider.weapon.getVx(),
	        					perso.y - p_collider.weapon.getVy());
	        		} else if (p_collider.size == null && p_collided.size == null) {	// Between 2 circles (only for now)
	        			hitPoint = Collision.hitPointOnCircles(p_collider.cx, p_collider.cy, p_collided.cx, p_collided.cy, p_collider.cr, p_collided.cr);
		        		// Then unapply the collision adjustment to have visual center
		        		hitPoint.add((int) perso.x - p_collided.cx,
		        					 (int) perso.y - p_collided.cy);
	        		}
	        		perso.beingWounded(hitPoint.x, hitPoint.y, p_collider.perso, dmg);
        		}
        	}
        	
            if (p_collider.weapon != null && !persoResisting) {
            	p_collider.weapon.beingCollided(perso);
            }
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
            return Collision.checkCollisionCircles(x1, y1, x2, y2, radius1, radius2);
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