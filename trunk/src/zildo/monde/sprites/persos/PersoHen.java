/**
 * Legend of Zildo
 * Copyright (C) 2006-2011 Evariste Boussaton
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

package zildo.monde.sprites.persos;

import zildo.client.sound.BankSound;
import zildo.fwk.bank.SpriteBank;
import zildo.fwk.gfx.PixelShaders.EngineFX;
import zildo.monde.Hasard;
import zildo.monde.map.Point;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.utils.MouvementZildo;
import zildo.server.EngineZildo;

public class PersoHen extends PersoNJ {

    private final Element shadow;

    public PersoHen(int x, int y) {
        // Add a shadow
        shadow = new Element();
        shadow.x = x;
        shadow.y = y;
        shadow.z = 0;
        shadow.nBank = SpriteBank.BANK_ELEMENTS;
        shadow.nSpr = ElementDescription.SHADOW.ordinal();
        shadow.setSprModel(ElementDescription.SHADOW);
        EngineZildo.spriteManagement.spawnSprite(shadow);
    }

    @Override
    public void animate(int compteur_animation) {

        super.animate(compteur_animation);

        shadow.x = x;
        shadow.y = y;

        if (linkedPerso != null && !flying) {
            // In Zildo's arms
            if (attente == 0) {
                // Play a hen random sound
                BankSound snd = BankSound.Poule1;
                if (Hasard.lanceDes(5)) {
                    snd = BankSound.Poule2;
                }
                EngineZildo.soundManagement.broadcastSound(snd, new Point(x, y));
                attente = 24;
            } else {
                attente--;
            }
            info = PersoInfo.NEUTRAL;
        } else {
            // Hen is free
            info = PersoInfo.SHOOTABLE_NEUTRAL;
            shadow.y+=2;
        }
        
    }

    @Override
    public boolean beingWounded(float cx, float cy, Perso p_shooter, int p_damage) {
        project(cx, cy, 1);
        this.setMouvement(MouvementZildo.TOUCHE);
        this.setWounded(true);
        this.setAlerte(true); // Zildo is detected, if it wasn't done !
        this.setSpecialEffect(EngineFX.PERSO_HURT);

        EngineZildo.soundManagement.broadcastSound(BankSound.MonstreTouche2, this);

        return false;
    }
}