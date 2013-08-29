package zildo.monde.sprites.persos;

import zildo.monde.items.Item;
import zildo.monde.items.ItemKind;
import zildo.monde.sprites.Reverse;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.elements.ElementGuardWeapon;
import zildo.monde.sprites.elements.ElementGuardWeapon.GuardWeapon;
import zildo.monde.sprites.utils.MouvementPerso;
import zildo.monde.util.Angle;
import zildo.resource.Constantes;

public class PersoFox extends PersoShadowed {

	ElementGuardWeapon guardWeapon;
	
	public PersoFox() {
		super(ElementDescription.SHADOW, 3);
		pathFinder.speed = 0.5f;
		pv = 2;
		
		weapon = new Item(ItemKind.BOW);
		guardWeapon = new ElementGuardWeapon(this);
		guardWeapon.setWeapon(GuardWeapon.BOW);
		addPersoSprites(guardWeapon);
		setEn_bras(guardWeapon);
	}
	
	/* (non-Javadoc)
	 * @see zildo.monde.sprites.persos.PersoShadowed#finaliseComportement(int)
	 */
	@Override
	public void finaliseComportement(int compteur_animation) {
		
		this.setAjustedX((int) x);
		this.setAjustedY((int) y);
		
		int add_spr = 0;
		
		if (quel_deplacement == MouvementPerso.SLEEPING) {
			add_spr = 6;
			guardWeapon.setVisible(false);
			reverse = Reverse.NOTHING;
			switch (angle) {
			case EST:
				reverse = Reverse.HORIZONTAL;
			case OUEST:
				add_spr++;
				break;
			}
		} else {
			add_spr = angle.value * 2;
			if (angle == Angle.EST) {
				reverse = Reverse.HORIZONTAL;
			} else {
				reverse = Reverse.NOTHING;
				if (angle == Angle.OUEST) {
					add_spr = 2;
				}
			}
			if (pos_seqsprite != 0) {
				int vr;
				if (angle.isHorizontal()) {
					vr = (pos_seqsprite % (4 * Constantes.speed)) / (2 * Constantes.speed);
				} else {
					vr = (pos_seqsprite % (8 * Constantes.speed)) / (2 * Constantes.speed);
					reverse = Reverse.NOTHING;
					if (vr >= 2) {
						vr-=2;
						reverse = Reverse.HORIZONTAL;
					}
				}
				add_spr+=vr;
			}
		}
		PersoDescription d = (PersoDescription) desc;
		this.setNSpr(d.nth(add_spr));
	}
}
