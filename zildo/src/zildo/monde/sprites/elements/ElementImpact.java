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

package zildo.monde.sprites.elements;

import zildo.client.sound.BankSound;
import zildo.monde.collision.Collision;
import zildo.monde.collision.DamageType;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.utils.CompositeElement;
import zildo.server.EngineZildo;

public class ElementImpact extends Element {

	public enum ImpactKind {
		SIMPLEHIT(ElementDescription.IMPACT1, 4,1), 
		EXPLOSION(ElementDescription.EXPLO1, new int[] {0,0,0,1,1,2,2,1,1,2,2,3}, 2), 
		FIRESMOKE(ElementDescription.EXPLOSMOKE1, 3,8),
		SMOKE(ElementDescription.SMOKE, new int[] {0,1,52,53,54}, 8),
		STAR_YELLOW(ElementDescription.STAR1, new int[] {0,1,2,1,0}, 8);
		
		ElementDescription desc;
		int seqLong;	// Size of the sequence of the sprite's life
		int speed;	// Number of frame during each sprite animation
		int[] seq=null;
		
		/**
		 * Default constructor : create a linear sprite sequence (1,2,3,...)
		 */
		private ImpactKind(ElementDescription p_desc, int p_seqLong, int p_speed) {
			desc=p_desc;
			seqLong=p_seqLong;
			speed=p_speed;
			seq=new int[seqLong];
			for (int i=0;i<seqLong;i++) {
				seq[i]=i;
			}
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
			case SIMPLEHIT:
			case FIRESMOKE:
			case STAR_YELLOW:
				setSprModel(kind.desc);
				break;
			case EXPLOSION:
				setSprModel(ElementDescription.EXPLO1);
				y+=getSprModel().getTaille_y()/2;
				composite=new CompositeElement(this);
				EngineZildo.soundManagement.broadcastSound(BankSound.Explosion, this);
		}
		addSpr=0;
        setLinkedPerso(p_shooter);
		// Stock the initial location
		startX=p_startX;
		startY=p_startY;
	}
	
	@Override
	public void animate() {
		counter++;
		int valCounter=counter/kind.speed;
		switch (kind) {
			case STAR_YELLOW:
        	case SIMPLEHIT:
        	case FIRESMOKE:
        	case SMOKE:
				if (valCounter == kind.seqLong) {
					dying=true;
					visible=false;
				} else {
					addSpr=kind.seq[valCounter];
					setSprModel(kind.desc, addSpr);
				}
				setAjustedX((int) x);
				setAjustedY((int) y+getSprModel().getTaille_y()/2);
				break;
			case EXPLOSION:
				if (valCounter == kind.seq.length) {	// End of the sequence
					composite.die(false);
					// Create the ending smoke fog
					for (int i=0;i<4;i++) {
						int dx=(int) (Math.random()*32 - 16);
						int dy=(int) (Math.random()*32 - 16);
                        EngineZildo.spriteManagement.spawnSprite(new ElementImpact(startX + dx, startY + dy, ImpactKind.FIRESMOKE,
                                (Perso) linkedPerso));
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
