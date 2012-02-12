/**
 * Legend of Zildo
 * Copyright (C) 2006-2012 Evariste Boussaton
 * Based on original Zelda : link to the past (C) Nintendo 1992
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
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.elements.ElementImpact.ImpactKind;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.util.Angle;
import zildo.server.EngineZildo;

/**
 * Zildo's boomerang
 * 
 * He throws it, then it came back. It has some miscellaneous properties :
 * -when it hits something, came back.
 * -when it cames back, it can't collide any tile, but it can wound enemies. (see {@link #isSolid()}
 * 
 * @author tchegito
 *
 */
public class ElementBoomerang extends ElementThrown {

	private static final float speed=2.5f;
	
	private Element grabbed;	// Element that boomerang has catched
	
	int count=0;
	boolean comingBack=false;
	
    public ElementBoomerang(Angle p_angle, int p_startX, int p_startY, int p_startZ, Perso p_shooter) {
        super(p_angle, p_startX, p_startY, p_startZ, speed, p_shooter);
        setSprModel(ElementDescription.BOOMERANG1);
        ax=-vx*0.015f;
        ay=-vy*0.015f;
        grabbed=null;
	}
    
    @Override
	public void animate() {
    	addSpr=(count/5) % 4;
    	if (count % 9 == 0) {
    		EngineZildo.soundManagement.broadcastSound(BankSound.Boomerang, this);
    	}
    	count+=1;
    	if (comingBack) {
    		// Boomerang is coming back to Zildo
    		Perso p=(Perso) getLinkedPerso();
    		float deltaY=y-p.y;
    		float deltaX=x-p.x;
    		if (Math.abs(deltaX)<=speed && Math.abs(deltaY)<=speed) {
    			// Zildo got it back
    			dying=true;
    		}
    		// Calculate hypothenus between boomerang and zildo's location to get correct speed
    		double hypo=Math.sqrt(deltaY*deltaY + deltaX*deltaX);
    		float speedHypo=(float) (speed/hypo);
    		vx=-speedHypo*deltaX;
    		vy=-speedHypo*deltaY;
    	} else if (Math.abs(vx)<=0.1f && Math.abs(vy)<=0.1f) {
    		comingBack=true;
    	}
    	super.animate();
    	if (grabbed != null) {
    		grabbed.x=x;
    		grabbed.y=y;
    	}
    }

    @Override
	public boolean beingCollided(Perso p_perso) {
    	// Boomerang hit something, so give him back to Zildo
    	comingBack=true;
		if (p_perso == null) {	// If boomerang hit a character, don't spawn an impact
			EngineZildo.soundManagement.broadcastSound(BankSound.BoomerangTape, this);
			EngineZildo.spriteManagement.spawnSprite(new ElementImpact((int) x, (int) y, ImpactKind.SIMPLEHIT, null));
		}
    	return true;
    }
    
    @Override
	public boolean isSolid() {
		return !comingBack;
	}
    
    public void grab(Element p_elem) {
    	if (grabbed == null) {
    		grabbed=p_elem;
    		comingBack=true;
    	}
    }
}
