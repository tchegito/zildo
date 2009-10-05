package zildo.monde.sprites.elements;

import zildo.fwk.bank.SpriteBank;
import zildo.monde.collision.Collision;
import zildo.monde.collision.DamageType;
import zildo.monde.map.Point;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.SpriteModel;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.persos.Perso;
import zildo.prefs.Constantes;

public class ElementGuardWeapon extends Element {

	public ElementGuardWeapon(Perso p_guard) {
		x=p_guard.getX();
		y=p_guard.getY()-12;
		setNBank(SpriteBank.BANK_PNJ);
		setNSpr(9);
	}
	
	public void animate() {
		SpriteEntity linked=getLinkedPerso();
		if (linked == null || SpriteEntity.ENTITYTYPE_PERSO != linked.getEntityType()) {
			dying=true;
		} else {
			Perso guard=(Perso) linked;
			angle=guard.angle;
			setNSpr(PersoDescription.ARME_EPEE.first() + angle.value);
			setNBank(SpriteBank.BANK_PNJ);

			// Arme du garde
			int j = (guard.getPos_seqsprite() / (2 * Constantes.speed)) % 2;
			int yy = (int) guard.y;
			int xx = (int) guard.x;
			int zz = 0;
			switch (angle) {
			case NORD:
				yy = yy - 8 - 3 * j;
				xx = xx + 8;
				break;
			case EST:
				xx = xx + 6 + 3 * j;
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
	
	public Collision getCollision() {
		SpriteModel spr=getSprModel();
		Point sizeHorizontal=new Point(spr.getTaille_x(), spr.getTaille_y());

		// Damage type depends on the guard's weapon
		return new Collision(new Point(x,y), sizeHorizontal, (Perso) getLinkedPerso(), DamageType.BLUNT);
	}
	
	public boolean isSolid() {
		return true;
	}
}
