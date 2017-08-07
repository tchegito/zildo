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

package zildo.monde.sprites.persos.ia;

import static zildo.server.EngineZildo.hasard;
import zildo.fwk.script.context.SpriteEntityContext;
import zildo.monde.sprites.desc.EntityType;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoNJ;
import zildo.monde.sprites.utils.MouvementPerso;
import zildo.monde.sprites.utils.MouvementZildo;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
import zildo.monde.util.Pointf;
import zildo.monde.util.Zone;
import zildo.resource.Constantes;
import zildo.server.EngineZildo;

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
	public boolean backward;	// Default FALSE. TRUE means character is stepping back
	public boolean open;	// Default FALSE. TRUE means character can open doors.
	protected boolean unstoppable;	// TRUE = no collision for this character
	public boolean alwaysReach;	// TRUE = character will wait forever if something is on his way (example:turtle)
    protected int nbShock;				// Number of times character hit something going to his target

	public PathFinder(Perso p_mobile) {
		mobile=p_mobile;
		backward=false;
		open=p_mobile.isZildo();
		speed = p_mobile.isZildo() ? Constantes.ZILDO_SPEED : 0.5f;
		unstoppable = false;
		alwaysReach = false;
	}
	
    /**
     * Shouldn't modify mobile location ! But update his angle.
     * @param p_speed 0 means we take default pathfinder's speed
     * @return Pointf
     */
    public Pointf reachDestination(float p_speed) {

        float x=mobile.x;
        float y=mobile.y;
        Pointf pos = new Pointf(x, y);
        Pointf delta = new Pointf(0, 0);
        if (target==null) {
            return pos;
        }
        
    	float velocity=speed == 0f ? p_speed : speed;
        int immo = 0;
        int move = 0;
        Angle a=mobile.getAngle();
        
        if (x < target.x - 0.5f) {
            delta.x = velocity;
        	move++;
            if (pos.x + delta.x >= target.x + 0.5f) {
                delta.x = target.x - mobile.x;
            }
            a=Angle.EST;
        } else if (x > target.x + 0.5f) {
            delta.x = -velocity;
            move++;
            if (pos.x + delta.x <= target.x - 0.5f) {
                delta.x = target.x - mobile.x;
            }
            a=Angle.OUEST;
        } else if (x != target.x) {
        	delta.x = target.x - x;
        } else {
            immo++;
        }
        if (y < target.y - 0.5f) {
            delta.y = velocity;
            move++;
            if (pos.y + delta.y > target.y + 0.5f) {
                delta.y = target.y - mobile.y;
            }
            a=Angle.SUD;
        } else if (y > target.y + 0.5f) {
            delta.y = -velocity;
            move++;
            if (pos.y + delta.y < target.y - 0.5f) {
                delta.y = target.y - mobile.y;
            }
            a=Angle.NORD;
        } else if (y != target.y) {
        	delta.y = target.y - y;
        } else {
            immo++;
        }

        if (move == 2) {
        	// diagonal move ==> adjust with coeff
            float coeff = Constantes.cosPiSur4;
            delta.x = delta.x * coeff;
            delta.y = delta.y * coeff;
        }
        
        // If there's no movement, stop the target
        if (immo == 2) {
       		target=null;
        } else if (mobile.getMouvement() != MouvementZildo.SAUTE && mobile.getQuel_deplacement() != MouvementPerso.VOLESPECTRE) {
        	pos = mobile.tryMove(delta.x, delta.y);
        	// Recalculate angle
        	delta.x = pos.x - mobile.x;
        	delta.y = pos.y - mobile.y;
        	if (delta.x !=0 || delta.y != 0) {
        		a = Angle.fromDelta(delta.x, delta.y);
        	}
        }

        if (backward && a!= null) {
        	a = a.rotate(2);
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
		int j=6+hasard.rand(6);
        float x=mobile.x;
        float y=mobile.y;
        
        if (EntityType.PERSO != mobile.getEntityType()) {
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
	
			if (!mvt.isOnlyHorizontal() && (j%2==1 || mvt.isDiagonal()) )
				target.y+= (16*Math.random()*j) - 8*j;
	
			j--; // On diminue le rayon jusqu'à être dans la zone
	
			if ((target.x>=zone.x1 && target.y>=zone.y1 &&
				 target.x<=zone.x2 && target.y<=zone.y2) ||
				(j==-1) )
				break;
		}
	
	    if (j==-1) {  // En cas de pépin
			target.x=zone.x1;
			target.y=zone.y1;
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
	
	/** Character collides into another one. We'll see if we must stop his movement, and determine a new target. Or we may
	 * ask the blocker one to move, if we're into a scripting scene.
	 */
	public void collide() {
		switch (mobile.getQuel_deplacement()) {
			case HEN:
			case BEE:
			case SQUIRREL:
				target=null;
				break;
			case CAT:
				((PersoNJ)mobile).destinationReached();
				break;
			default:
				mobile.setAttente(10 + (int) (Math.random()*20));
				if (mobile.isGhost()) {
					mobile.tryJump(new Pointf(mobile.x, mobile.y));
				}
				if (!alwaysReach) {
					nbShock++;
					if (!mobile.isGhost()) {
						if (nbShock >= 3) {
							target=null;
							mobile.setAlerte(false);
							nbShock=0;
						}
					} else if (mobile.getQuel_deplacement() != MouvementPerso.FOLLOW){
						// Freeze during a scene (because moving character is 'ghost')
						nbShock=0;
						
                		Perso collidingPerso = EngineZildo.persoManagement.lookForOne(mobile, 1, null, false);
                		if (collidingPerso != null) {
							Angle a = mobile.getAngle();
							// First : lateral
							Point nonBlockingPos = null;
							Angle[] angles = new Angle[] {a.rotate(1), Angle.rotate(a,-1), a, a.opposite()};
							int dist = 12;
							for (Angle chkAngle : angles) {
								Point testPos = new Point();
								testPos.x = (int) (collidingPerso.x + chkAngle.coordf.x * dist);
								testPos.y = (int) (collidingPerso.y + chkAngle.coordf.y * dist);
								
								if (!EngineZildo.mapManagement.collide(testPos.x, testPos.y, collidingPerso)) {
									nonBlockingPos = testPos;
									break;
								}
							}
							// Ask blocking character to move with a script (with priority on running scene)
							if (nonBlockingPos != null) {
								SpriteEntityContext context = new SpriteEntityContext(collidingPerso);
								EngineZildo.scriptManagement.runPersoAction(collidingPerso, "moveCharacter("+nonBlockingPos.x+","+nonBlockingPos.y+")", context, true);
							}
							mobile.setAttente(10);
                		}
					}
				}
		}
	}
	
	/**
	 * Common method for reaching a point by the shortest distance : a line.<br/>
	 * Set the angle accordingly to the direction.
	 */
	final protected Pointf reachLine(float p_speed, boolean p_twoAngles) {
		Pointf p =new Pointf(mobile.x, mobile.y);
		if (target != null) {
	    	float velocity=speed == 0f ? p_speed : speed;
			float hypothenuse = target.distance(new Point(Math.round(mobile.x), Math.round(mobile.y)));
			if (hypothenuse == 0f || hypothenuse < p_speed) {
				target = null;
			} else {
				double cosAngle = (target.x - mobile.x) / hypothenuse;
				double sinAngle = (target.y - mobile.y) / hypothenuse;
				float dx = (float) (cosAngle * velocity);
				float dy = (float) (sinAngle * velocity);
				
	        	p = mobile.tryMove(dx, dy);
	        	// Recalculate angle
	        	dx = p.x - mobile.x;
	        	dy = p.y - mobile.y;
	        	
				// Set the angle
				if (p_twoAngles) {
					if (cosAngle < 0) {
						mobile.setAngle(Angle.OUEST);
					} else {
						mobile.setAngle(Angle.EST);
					}
				} else if (dx != 0 || dy != 0) {
					mobile.setAngle(Angle.fromDelta(dx, dy));
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
	
	/**
	 * Move the mobile, with given algorithm.
	 * @param p_speed
	 * @return TRUE if mobile has moved / FALSE if collision (means that target has been set to NULL)
	 */
	public boolean move(float p_speed, MoveAlgo p_algo) {
		Pointf loc = null;
		switch (p_algo) {
		case APPROACH:
			loc = reachDestination(p_speed);
			break;
		case STRAIGHT:
			loc = reachLine(p_speed, true);
			break;
		}
		if (target != null && loc.x == mobile.x && loc.y == mobile.y) {
			target = null;
			return false;
		}
		if (loc.x != Float.NaN && loc.y != Float.NaN) { 
			mobile.x = loc.x;
			mobile.y = loc.y;
		}
		return true;
	}

	// Here we use accessor, because this property will not be always modifiable
	public boolean isUnstoppable() {
		return unstoppable;
	}

	public void setUnstoppable(boolean unstoppable) {
		this.unstoppable = unstoppable;
	}
	
}
