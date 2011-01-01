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

public class PersoVolant extends PersoNJ {

	public PersoVolant() {
		super();
		//if (getQuel_deplacement().equals(MouvementPerso.SCRIPT_VOLESPECTRE)) {
			setCptMouvement(100);
			setForeground(true);
			
			Element ombre=new Element();
			ombre.setX(x);
			ombre.setY(y-12);
			ombre.setSprModel(ElementDescription.SHADOW_SMALL);
			addPersoSprites(ombre);
		//}
	}
	
	@Override
	public void finaliseComportement(int compteur_animation) {
		// On déplace l'ombre du perso
		if (persoSprites.size() >0) {
			Element ombre=persoSprites.get(0);
			ombre.setX(x);
			ombre.setY(y+6);
			ombre.setVisible(z>0);
		}
		super.finaliseComportement(compteur_animation);
	}
	
}
