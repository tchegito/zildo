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

import java.util.Iterator;

import zildo.fwk.bank.SpriteBank;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.elements.Element;

/**
 * Perso garde vert.
 * 
 * La particularité est qu'il est constitué de deux sprites:
 * -un pour le corps
 * -un pour la tête
 * 
 * Il est possible d'effectuer des combinaisons, comme par exemple le corps de la princesse et la tête de garde.
 * @author tchegito
 *
 */
public class PersoGardeVert extends PersoNJ {

	public static final int[] mouvetete={0,3,1,0};

	public PersoGardeVert() {
		super();
		// On crée la tête du garde
		Element teteGarde=new Element();
		teteGarde.setX(getX());
		teteGarde.setY(getY()-12);
		teteGarde.setNBank(SpriteBank.BANK_PNJ);
		teteGarde.setNSpr(PersoDescription.HAUT_GARDEVERT.first());
		addPersoSprites(teteGarde);
		
		pv = 2;
	}
	
	@Override
	public void finaliseComportement(int compteur_animation) {
		super.finaliseComportement(compteur_animation);
		
		// On affiche la tête du garde vert
		Iterator<Element> it=this.persoSprites.iterator();
		Element teteGarde=it.next();

		teteGarde.setNSpr(PersoDescription.HAUT_GARDEVERT.first() + (angle.value + mouvetete[cptMouvement])  % 4);
		teteGarde.setX(x);
		teteGarde.setY(y);
		teteGarde.setZ(9 + computeSeq(2) % 2);
		
		int add_spr=angle.value*2 + computeSeq(2) % 2;

		this.setNSpr((getDesc().first()+add_spr) % 128);

	}
}
