package zildo.monde.sprites.persos;

import zildo.monde.collision.Collision;
import zildo.monde.collision.DamageType;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.util.Point;

/** Coal sprite.
 * 
 * Basically, an inoffensive 'character', but can be 'wounded' by enemies only, not hero.
 * 
 * When wounded, coal burns.
 * 
 * Can have 2 different PersoDescription: {@link PersoDescription#COAL}  then {@link PersoDescription#COAL_COLD}
 * when it have finished burning.
 * 
 */
public class PersoCoal extends PersoNJ {

	public PersoCoal() {
		super();
		
	}
	
	@Override
	public void animate(int compteur_animation) {
		info = PersoInfo.ZILDO;
		super.animate(compteur_animation);
	}
	
	// This collision method is used to determine if enemies can touch the coal
	@Override
	public Collision getCollision() {
		return new Collision(new Point(x, y), new Point(16, 16), this, DamageType.HARMLESS, null);
	}
	
	@Override
	public void beingWounded(float cx, float cy, Perso p_shooter, int p_damage) {
		if (desc == PersoDescription.COAL && attente <= 0) {
			// Coal animation
			if (burningFire == null) {
				addFire();
				burningFire.setForeground(true);
				burningFire.zoom = 120;
			}
			attente = 200;	// Wait for next burn
			if (addSpr < 3) {
				addSpr += 1;
				if (addSpr == 3) {
					setDesc(PersoDescription.COAL_COLD);
					addSpr = 0;
					burningFire.zoom = 20;
				}
			}
		}
	}
}
