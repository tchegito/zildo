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

package zildo.monde.sprites.persos;

import zildo.client.sound.BankSound;
import zildo.fwk.gfx.EngineFX;
import static zildo.server.EngineZildo.hasard;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.utils.MouvementZildo;
import zildo.monde.sprites.utils.SoundGetter;
import zildo.monde.sprites.utils.SoundWrapper;
import zildo.monde.util.Angle;
import zildo.server.EngineZildo;

public class PersoPoultry extends PersoShadowed {

	int countSound;
	
	// Sound getters are static to avoid several poultries of same kind rendering same sound
	// At the same time (ugly)
	private static SoundGetter henFree = new SoundGetter(BankSound.Poule1, BankSound.Poule5, 150);
	private static SoundGetter henCaught = new SoundGetter(BankSound.Poule6, BankSound.Poule8, 24, true);
	private static SoundGetter duckFree = new SoundGetter(BankSound.Duck1, BankSound.Duck3, 150);
	private static SoundGetter duckCaught = new SoundGetter(BankSound.Duck4, BankSound.Duck6, 24, true);
	private static SoundGetter cat = new SoundGetter(BankSound.Cat1, BankSound.Cat3, 500);
	
	// Wrappers allows each individual to switch between free and caught stance
	SoundWrapper henSound = new SoundWrapper(henFree, henCaught);
	SoundWrapper duckSound = new SoundWrapper(duckFree, duckCaught);
	SoundWrapper catSound = new SoundWrapper(cat);
	
	SoundWrapper specSound;
	
    public PersoPoultry(PersoDescription desc, int x, int y) {
    	super(ElementDescription.SHADOW, 2);
    	
    	setDesc(desc);
    	switch (desc) {
    	case CANARD:
    		specSound = duckSound;
    		break;
    	case POULE:
    	default:
    		specSound = henSound;
    		break;
    	case BROWN_CAT:
    	case GREY_CAT:
    		specSound = catSound;
    	}
    	// To avoid all sounds starting together
    	countSound = hasard.rand(specSound.getDuration());
    }

    @Override
    public void animate(int compteur_animation) {

        super.animate(compteur_animation);

        shadow.x = x;
        shadow.y = y;

        if (linkedPerso != null && !flying) {
            // In Zildo's arms
            if (countSound == 0) {
                // Play a caught animal random sound
                BankSound snd = specSound.getSound();
                EngineZildo.soundManagement.broadcastSound(snd, this);
                countSound = specSound.getDuration();
            }
            info = PersoInfo.NEUTRAL;
        } else {
            // Hen is free
            info = PersoInfo.SHOOTABLE_NEUTRAL;
            shadow.y+=2;
            
            if (countSound == 0 && hasard.lanceDes(8)) {
            	BankSound snd = specSound.getSound();
                countSound = specSound.getDuration() + hasard.rand(100);
                EngineZildo.soundManagement.broadcastSound(snd, this);
            }
        }

        if (countSound != 0) {
        	countSound--;
        }

    }
    
    @Override
    public boolean fall() {
		flying = false;
		linkedPerso = null;
		return true;
    }

    @Override
	public void beingTaken() {
    	specSound.switchTo(1);
    	countSound = 0;
	}
    
    @Override
    public void beingThrown(float fromX, float fromY, Angle throwingAngle, Element thrower) {
    	super.beingThrown(fromX, fromY, throwingAngle, thrower);
    	specSound.switchTo(0);
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
}