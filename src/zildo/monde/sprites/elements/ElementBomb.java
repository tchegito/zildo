/**
 * Legend of Zildo
 * Copyright (C) 2006-2010 Evariste Boussaton
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

import zildo.client.SoundPlay.BankSound;
import zildo.fwk.gfx.PixelShaders.EngineFX;
import zildo.monde.collision.Collision;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.elements.ElementImpact.ImpactKind;
import zildo.monde.sprites.persos.Perso;
import zildo.server.EngineZildo;

public class ElementBomb extends Element {

	Perso shooter;
	int counter;
	
	public ElementBomb(int p_startX, int p_startY, int p_startZ, Perso p_shooter) {
		x=p_startX;
		y=p_startY;
		z=p_startZ;
		setSprModel(ElementDescription.BOMB);
		counter=100;
		
        // Add a shadow
		addShadow(ElementDescription.SHADOW_SMALL);
        
        EngineZildo.soundManagement.broadcastSound(BankSound.PlanteBombe, this);
	}
	
	public void animate() {
		counter--;
		if (counter==0) {
			dying=true;
			shadow.dying=true;
			EngineZildo.spriteManagement.spawnSprite(new ElementImpact((int) x, (int) y, ImpactKind.EXPLOSION, shooter));
		} else if (counter<30) {
			setSpecialEffect(EngineFX.PERSO_HURT);
		}
		super.animate();
	}
	
    public Collision getCollision() {
        return null;
    }

}
