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

import zildo.monde.collision.Collision;
import zildo.monde.collision.DamageType;
import zildo.monde.sprites.elements.ElementGuardWeapon;
import zildo.monde.sprites.elements.ElementGuardWeapon.GuardWeapon;
import zildo.monde.sprites.utils.MouvementPerso;
import zildo.resource.Constantes;

/**
 * Perso garde "canard"
 * 
 * Plusieurs particularités: -il peut changer de couleurs à l'aide d'un pixel
 * shader, qui se base sur son nom. -il peut avoir plusieurs armes différente:
 * épée, lance et arc
 * 
 * @author tchegito
 * 
 */
public class PersoGarde extends PersoNJ {

	final int[][] seq_gbleu = { { 0, 1, 4, 1, 0, 2, 3, 2 },
			{ 5, 6, 7, 6, 5, 6, 7, 6 }, { 8, 9, 10, 11, 8, 9, 10, 11 },
			{ 12, 13, 14, 13, 12, 13, 14, 13 } };

	ElementGuardWeapon weapon;

	public PersoGarde() {
		super();
		weapon = new ElementGuardWeapon(this);
		addPersoSprites(weapon);
		setEn_bras(weapon);
	}

	@Override
	public void setQuel_deplacement(MouvementPerso p_script) {
		super.setQuel_deplacement(p_script);
		switch (p_script) {
		case ZONELANCE:
			weapon.setWeapon(GuardWeapon.SPEAR);
			break;
		case ZONEARC:
			weapon.setWeapon(GuardWeapon.BOW);
			break;
		}
	}

	@Override
	public void finaliseComportement(int compteur_animation) {
		super.finaliseComportement(compteur_animation);

		// Garde bleu
		setNSpr(getDesc().first());
		setAddSpr(seq_gbleu[angle.value][(getPos_seqsprite() % (16 * Constantes.speed))
				/ (2 * Constantes.speed)]);
	}

	@Override
	public Collision getCollision() {
		return new Collision((int) x, (int) y, 10, null, this,
				DamageType.BLUNT, null);
	}
}
