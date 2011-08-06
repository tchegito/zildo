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

import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.elements.Element;

/**
 * @author Tchegito
 * 
 */
public abstract class PersoShadowed extends PersoNJ {

	Element shadow;
	
	public PersoShadowed() {
		shadow = new Element();
		shadow.setSprModel(ElementDescription.SHADOW);
		addPersoSprites(shadow);
	}
	
	public PersoShadowed(ElementDescription p_shadowType) {
		this();
		shadow.setSprModel(p_shadowType);
	}
	
	@Override
	public void finaliseComportement(int compteur_animation) {
	    // Move character's shadow
	    if (persoSprites.size() >0) {
	        Element ombre=persoSprites.get(0);
	        ombre.setX(x);
	        ombre.setY(y-1);
	        ombre.setZ(-7);
	        ombre.setVisible(z>=0);
	    }
	    super.finaliseComportement(compteur_animation);
	}
}
