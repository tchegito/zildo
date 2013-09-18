package zildo.monde.sprites.persos;

import zildo.monde.sprites.Reverse;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.utils.MouvementPerso;
import zildo.monde.util.Angle;
import zildo.resource.Constantes;
import zildo.server.EngineZildo;

public class PersoFox extends PersoShadowed {

	Element arm;
	
	public PersoFox() {
		super(ElementDescription.SHADOW, 2);
		pathFinder.speed = 0.5f;
		pv = 2;
		
		//weapon = new Item(ItemKind.BOW);
		initWeapon();
		//setActiveWeapon(GuardWeapon.BOW);
		
		arm = null;
	}

	@Override
	public void finaliseComportement(int compteur_animation) {

		super.finaliseComportement(compteur_animation); // For shadow

		this.setAjustedX((int) x);
		this.setAjustedY((int) y);
		
		int add_spr = 0;
		
		if (quel_deplacement == MouvementPerso.SLEEPING) {
			add_spr = 7;
			guardWeapon.setVisible(false);
			reverse = Reverse.NOTHING;
			switch (angle) {
			case EST:
				reverse = Reverse.HORIZONTAL;
			case OUEST:
				break;
			default:
				add_spr--;
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
		
		if( mouvement != null) {
			switch (mouvement) {
			case BRAS_LEVES:
				if (arm == null) {
					arm = new Element(this);
					arm.setSprModel(PersoDescription.FOX, 8);
					arm.setSpecialEffect(this.getSpecialEffect());
					addPersoSprites(arm);
					EngineZildo.spriteManagement.spawnSprite(arm);
				} else {
					arm.setVisible(true);
				}
				arm.x = x + (angle == Angle.OUEST ? -7 : 7);
				arm.y = y + 2;
				break;
			case VIDE:
				if(arm != null) {
					arm.setVisible(false);
					arm = null;
				}
			}
		}
		PersoDescription d = (PersoDescription) desc;
		setNSpr(d.nth(add_spr));
		
	}
	
	@Override
	public void beingWounded(float cx, float cy, Perso p_shooter, int p_damage) {
		// Make fox invulnerable if sleeping (how coward it is to shoot a sleeping one ? ;) )
		int damage = quel_deplacement == MouvementPerso.SLEEPING ? 0 : p_damage;
		super.beingWounded(cx, cy, p_shooter, damage);
	}
}
