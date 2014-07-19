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
import zildo.monde.collision.Collision;
import zildo.monde.collision.DamageType;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.elements.ElementGuardWeapon.GuardWeapon;
import zildo.monde.sprites.elements.ElementImpact;
import zildo.monde.sprites.elements.ElementImpact.ImpactKind;
import zildo.monde.sprites.utils.MouvementPerso;
import zildo.resource.Constantes;
import zildo.server.EngineZildo;

/**
 * Perso garde "canard"
 * 
 * Some characteristics:<ul>
 * <li>he can change colors following his name (or set via FX attribute in a script) thanks to a pixel shader</li>
 * <li>he can have different weapons : sword, spear and bow, via {@link MouvementPerso}</li>
 * </ul>
 * 
 * @author tchegito
 * 
 */
public class PersoGarde extends PersoNJ {

	static final int[][] seq_gbleu = { { 0, 1, 4, 1, 0, 2, 3, 2 },
			{ 5, 6, 7, 6, 5, 6, 7, 6 }, { 8, 9, 10, 11, 8, 9, 10, 11 },
			{ 12, 13, 14, 13, 12, 13, 14, 13 } };

	// TODO: one day, this could be added for everyone, in a more generic way.
	Element starAura;
	
	public PersoGarde() {
		super();
		initWeapon();
		pv = 2;
		
		starAura = new Element();
		starAura.setVisible(false);
		addPersoSprites(starAura);
	}

	@Override
	public void setQuel_deplacement(MouvementPerso p_script, boolean p_updatePathFinder) {
		super.setQuel_deplacement(p_script, p_updatePathFinder);
		switch (p_script) {
		case ZONELANCE:
			setActiveWeapon(GuardWeapon.SPEAR);
			break;
		case ZONEARC:
			setActiveWeapon(GuardWeapon.BOW);
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
	
	/* (non-Javadoc)
	 * @see zildo.monde.sprites.persos.PersoNJ#beingWounded(float, float, zildo.monde.sprites.persos.Perso, int)
	 */
	@Override
	public void beingWounded(float cx, float cy, Perso p_shooter, int p_damage) {
		if (EngineFX.GUARD_BLACK.equals(getSpecialEffect()) && p_damage == 1) {
			// No strength enough to wound (is it the right place to check this ???)
			EngineZildo.soundManagement.broadcastSound(BankSound.BoomerangTape, this);
			EngineZildo.spriteManagement.spawnSprite(new ElementImpact((int) x, (int) y, ImpactKind.SIMPLEHIT, null));
			p_shooter.project(cx, cy, 1);
		} else {
			super.beingWounded(cx, cy, p_shooter, p_damage);
		}
	}
	
	@Override
	public void setSpecialEffect(EngineFX specialEffect) {
		super.setSpecialEffect(specialEffect);
		if (specialEffect == EngineFX.GUARD_BLACK) {
			addStarAura();
		}
	}
	
	@Override
	public void animate(int compteur_animation) {
		super.animate();
		if (starAura != null) {
			starAura.x = x;
			starAura.y = y;
		}
	}
	private void addStarAura() {
		starAura.setSpecialEffect(EngineFX.STAR);
		starAura.zoom = 800;
		starAura.setVisible(true);
	}
}
