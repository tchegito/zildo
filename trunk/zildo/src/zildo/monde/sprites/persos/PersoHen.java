/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
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

package zildo.monde.sprites.persos;

import zildo.client.sound.BankSound;
import zildo.fwk.gfx.EngineFX;
import zildo.monde.Hasard;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.utils.MouvementZildo;
import zildo.monde.util.Point;
import zildo.server.EngineZildo;

public class PersoHen extends PersoShadowed {

	int countSound;
	
    public PersoHen(int x, int y) {
    	super(ElementDescription.SHADOW, 2);
    }

    @Override
    public void animate(int compteur_animation) {

        super.animate(compteur_animation);

        shadow.x = x;
        shadow.y = y;

        if (linkedPerso != null && !flying) {
            // In Zildo's arms
            if (countSound == 0) {
                // Play a hen random sound
                BankSound snd = BankSound.Poule2;
                if (Hasard.lanceDes(5)) {
                    //snd = BankSound.Poule2;
                }
                EngineZildo.soundManagement.broadcastSound(snd, this);
                countSound = 24;
            }
            info = PersoInfo.NEUTRAL;
        } else {
            // Hen is free
            info = PersoInfo.SHOOTABLE_NEUTRAL;
            shadow.y+=2;
            
            if (countSound == 0 && Hasard.lanceDes(8)) {
            	int h = Hasard.rand(13);
                BankSound snd = BankSound.Poule1;
            	if (h < 3) {
            		snd = BankSound.Poule2;
            	} else if (h < 6) {
            		snd = BankSound.Poule3;
            	} else if (h < 9) {
            		snd = BankSound.Poule4;
            	} else {
            		snd = BankSound.Poule5;
            	}
            	snd = getSound();
                countSound = 150 + Hasard.rand(100);
                EngineZildo.soundManagement.broadcastSound(snd, this);
            }
        }

        if (countSound != 0) {
        	countSound--;
        }

    }
    
    /* (non-Javadoc)
     * @see zildo.monde.sprites.elements.Element#fall()
     */
    @Override
    public void fall() {
		flying = false;
		linkedPerso = null;
    }

    @Override
    public void beingWounded(float cx, float cy, Perso p_shooter, int p_damage) {
        project(cx, cy, 1);
        this.setMouvement(MouvementZildo.TOUCHE);
        this.setWounded(true);
        this.setAlerte(true); // Zildo is detected, if it wasn't done !
        this.setSpecialEffect(EngineFX.PERSO_HURT);

        EngineZildo.soundManagement.broadcastSound(BankSound.MonstreTouche2, this);
    }
    
    static BankSound currentSnd = BankSound.Poule1;
    private static BankSound out = BankSound.Poule5.next();
    
    static final BankSound getSound() {
        BankSound next = currentSnd.next();
        if (next == out) {
        	next = BankSound.Poule1;
        }
        currentSnd = next;
        return next;
    }
}