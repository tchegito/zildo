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

package zildo.monde.sprites.elements;

import zildo.monde.collision.Collision;
import zildo.monde.collision.DamageType;
import zildo.monde.map.Point;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.SpriteModel;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.desc.SpriteDescription;
import zildo.monde.sprites.persos.Perso;
import zildo.prefs.Constantes;

public class ElementGuardWeapon extends Element {

    enum GuardWeapon {
	SWORD(PersoDescription.ARME_EPEE), 
	SPEAR(PersoDescription.ARME_LANCE),
	BOW(PersoDescription.ARC);
	
	SpriteDescription desc;
	
	private GuardWeapon(SpriteDescription p_desc) {
	    desc = p_desc;
	}
    }
    
	public ElementGuardWeapon(Perso p_guard) {
		x=p_guard.getX();
		y=p_guard.getY()-12;
		setDesc(PersoDescription.ARME_EPEE);
	}

	public void setWeapon(GuardWeapon p_weapon) {
	    setDesc(p_weapon.desc);
	}
	
	@Override
	public void animate() {
		SpriteEntity linked=getLinkedPerso();
		if (linked == null || SpriteEntity.ENTITYTYPE_PERSO != linked.getEntityType()) {
			dying=true;
		} else {
			Perso guard=(Perso) linked;
			angle=guard.angle;
			setAddSpr(angle.value);

			// Arme du garde
			int j = (guard.getPos_seqsprite() / (2 * Constantes.speed)) % 2;
			int yy = (int) guard.y;
			int xx = (int) guard.x;
			int zz = 0;
			switch (angle) {
			case NORD:
				yy = yy - 12 - 3 * j;
				xx = xx + 8;
				break;
			case EST:
				xx = xx + 9 + 3 * j;
				zz = 4;
				break;
			case SUD:
				yy = yy + 6 + 3 * j;
				xx = xx - 9;
				break;
			case OUEST:
				xx = xx - 6 - 3 * j;
				zz = 4;
				break;
			}

			x=xx;
			y=yy+3;
			z=zz;
		}
		super.animate();
	}
	
	@Override
	public Collision getCollision() {
		SpriteModel spr=getSprModel();
		Point sizeHorizontal=new Point(spr.getTaille_x(), spr.getTaille_y());

		// Damage type depends on the guard's weapon
		return new Collision(new Point(x,y), sizeHorizontal, (Perso) getLinkedPerso(), DamageType.BLUNT, this);
	}
	
	@Override
	public boolean isSolid() {
		return true;
	}
}
