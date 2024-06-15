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

package zildo.monde.sprites.elements;

import static zildo.server.EngineZildo.hasard;

import zildo.client.sound.BankSound;
import zildo.monde.collision.Collision;
import zildo.monde.collision.DamageType;
import zildo.monde.sprites.Reverse;
import zildo.monde.sprites.Rotation;
import zildo.monde.sprites.SpriteModel;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.utils.CompositeElement;
import zildo.server.EngineZildo;

/** Sequence of animating sprites. 
 * 
 * Sprite dies at the end of the sequence, except if 'seqLong' is negative. **/
public class ElementImpact extends Element {

	public enum ImpactKind {
		SIMPLEHIT(ElementDescription.IMPACT1, 4,1), 
		EXPLOSION(ElementDescription.EXPLO1, new int[] {0,0,0,1,1,2,2,1,1,2,2,3}, 2), 
		EXPLOSION_UNDAMAGE(EXPLOSION),	// Same as explosion but without damage (used when boss are killed)
		FIRESMOKE(ElementDescription.EXPLOSMOKE1, 3,8),
		SMOKE(ElementDescription.SMOKE, new int[] {0,1,52,53,54}, 8),
		STAR_YELLOW(ElementDescription.STAR1, new int[] {0,1,2,1,0}, 8),
		DROP_ENERGY(ElementDescription.BLUE_ENERGY, 1,1),
		LAVA_DROP(ElementDescription.LAVADROP1, 4, 2),
		DUST(ElementDescription.DUST1, 3, 3),
		WATER_SPLASH(ElementDescription.WATER_ANIM1, 4, 3),
		WAVE(ElementDescription.WATERWAVE1, new int[] {2,2,2,1,1,1,1,0,0,0,0,0}, 3),
		STAFF_TURNING(ElementDescription.STAFF_POUM, 8, 4),	// Staff falling on the floor, or hitting a wall
		HEARTH(ElementDescription.HEARTH1, -6, 6),	// Fire in nature palace's hearth
		CAULDRON(ElementDescription.CAULDRON1, -3, 6),
		GNAP(ElementDescription.GNAP1, 5, 4),
		SAND_POPOUT(ElementDescription.SAND1, 5, 4);	// A sand projection for buried monsters
		
		final ElementDescription desc;
		final int seqLong;	// Size of the sequence of the sprite's life (negative value means infinite loop)
		final int speed;	// Number of frame during each sprite animation
		final int[] seq;
		
		/**
		 * Default constructor : create a linear sprite sequence (1,2,3,...)
		 */
		private ImpactKind(ElementDescription p_desc, int p_seqLong, int p_speed) {
			desc=p_desc;
			seqLong=p_seqLong;
			speed=p_speed;
			seq=new int[Math.abs(seqLong)];
			for (int i=0;i<seq.length;i++) {
				seq[i]=i;
			}
		}
		
		private ImpactKind(ImpactKind k) {	// Copy constructor
			this(k.desc, k.seq, k.speed);
		}
		
		/**
		 * Constructor with custom sprite sequence
		 */
		private ImpactKind(ElementDescription p_desc, int[] p_seq, int p_speed) {
			desc=p_desc;
			seqLong=p_seq.length;
			seq=p_seq;
			speed=p_speed;
		}
	}
	
	int counter;
	int startX, startY;
	ImpactKind kind;

	CompositeElement composite;

    public ElementImpact(int p_startX, int p_startY, ImpactKind p_kind, Perso p_shooter) {
    	super();
		x=p_startX;
		y=p_startY;
		z=4;
		counter=0;
		kind=p_kind;
		switch (p_kind) {
			case DUST:
				if (hasard.lanceDes(5)) 
					reverse = Reverse.HORIZONTAL;
			case SIMPLEHIT:
			case FIRESMOKE:
			case STAR_YELLOW:
			case LAVA_DROP:
			case WATER_SPLASH:
			case WAVE:
			case STAFF_TURNING:
			case HEARTH:
			case CAULDRON:
				setSprModel(kind.desc);
				break;
			case EXPLOSION:
			case EXPLOSION_UNDAMAGE:
				setSprModel(ElementDescription.EXPLO1);
				y+=getSprModel().getTaille_y()/2;
				composite=new CompositeElement(this);
				EngineZildo.soundManagement.broadcastSound(BankSound.Explosion, this);
				break;
			case DROP_ENERGY:
				setSprModel(ElementDescription.BLUE_ENERGY);
				y+=getSprModel().getTaille_y()/2;
				zoom = 1;
				setForeground(true);
				composite=new CompositeElement(this);
				composite.squareShape(0,0);
				alpha = 255;
				break;
			case SAND_POPOUT:
				alphaA = -0.5f;
				break;
			default:
				break;
		}
    	if (kind == ImpactKind.STAFF_TURNING) {
    		az=-0.01f;
    		vz=0.35f;
    	}

		addSpr=0;
        setLinkedPerso(p_shooter);
        if (p_shooter != null) {
        	floor = p_shooter.getFloor();
        }
		// Stock the initial location
		startX=p_startX;
		startY=p_startY;
	}
	
	@Override
	public void animate() {
		counter++;
		int valCounter=counter/kind.speed;
		switch (kind) {
			case WAVE:
				alpha-=2;
			case STAR_YELLOW:
				super.animate();
        	case SIMPLEHIT:
        	case FIRESMOKE:
        	case LAVA_DROP:
        	case SMOKE:
        	case DUST:
        	case WATER_SPLASH:
        	case STAFF_TURNING:
        	case HEARTH:
        	case CAULDRON:
        	case GNAP:
        	case SAND_POPOUT:
        		alphaV += alphaA;
        		alpha += alphaV;
				if (valCounter >= kind.seq.length) {
					if (kind.seqLong < 0) {	// Infinite loop
						counter = 0;
					} else {	// Remove entity
						dying=true;
						visible=false;
					}
				} else {
					if (kind == ImpactKind.STAFF_TURNING) {
						// Physic job to handle z, vz, and az
						super.animate();
						// Rotate, blink and reduce alpha channel
						rotation = Rotation.values()[valCounter % 4];
						visible = counter % 2 == 0;
						alpha-=4;
					} else {
						addSpr=kind.seq[valCounter];
						setSprModel(kind.desc, addSpr);
					}
				}
				// Adjust sprite model taking rotation state into account
				SpriteModel sprModel = getSprModel();
				int ty = sprModel.getTaille_y();
				if (rotation.isWidthHeightSwitched() ) {
					ty = sprModel.getTaille_x();
				}
				setAjustedX((int) x);
				setAjustedY((int) y+ty/2);
				break;
        	case DROP_ENERGY:
        		if (counter >= 2*255) {	// End of the animation
        			composite.die(false);
        			dying = true;
        		} else {
        			addSpr = kind.seq[Math.min(kind.seqLong - 1, valCounter)];
        			counter++;
        			composite.setZoom(4 * counter);
        			composite.setAlpha(Math.max(0, alpha));
        			alpha-=2;
        			composite.focus(linkedPerso);
        		}
        		
        		break;
			case EXPLOSION:
			case EXPLOSION_UNDAMAGE:
				if (valCounter == kind.seq.length) {	// End of the sequence
					composite.die(false);
					// Create the ending smoke fog
					for (int i=0;i<4;i++) {
						int dx=(int) (Math.random()*32 - 16);
						int dy=(int) (Math.random()*32 - 16);
						ElementImpact smoke = new ElementImpact(startX + dx, startY + dy, ImpactKind.FIRESMOKE,
                                (Perso) linkedPerso);
						smoke.floor = floor;
						smoke.z = z;
                        EngineZildo.spriteManagement.spawnSprite(smoke);
                    }
				} else {
					addSpr=kind.seq[valCounter];
					if (addSpr == 1) {
						// Create 3 other elements to create the entire explosion
						composite.squareShape(0,0);
					} else if (addSpr == 3) {
						composite.die(true);
						composite.squareShape(4,4);
						addSpr=2;
					}
					if (addSpr < 3) {
						composite.setSprModel(ElementDescription.EXPLO1, addSpr);
					}
				}
				super.animate();
				break;
		}
	}
	
    @Override
    public Collision getCollision() {
        if (kind == ImpactKind.EXPLOSION) {
            return composite.getCollision();
        }
        return null;
    }

    @Override
	public DamageType getDamageType() {
    	return DamageType.EXPLOSION;
    }
    
    @Override
    public boolean isSolid() {
        return kind == ImpactKind.EXPLOSION;
    }      
}
