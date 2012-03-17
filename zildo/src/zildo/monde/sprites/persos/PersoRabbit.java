package zildo.monde.sprites.persos;

import zildo.monde.Hasard;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.desc.PersoDescription;

/**
 * Secret of Mana's rabbit (he doesn't look like a rabbit at all !)
 * 
 * @author tchegito
 */
public class PersoRabbit extends PersoShadowed {

	boolean jumping = false;
	int idleTime = 0;

	public PersoRabbit() {
		super(ElementDescription.SHADOW, 2);
		setDesc(PersoDescription.RABBIT);
		setInfo(PersoInfo.ENEMY);
		setPv(2);
	}

	@Override
	public void animate(int compteur_animation) {
		if (isAlerte() || true) {
			// Rabbit has a target : we make him jump to hit
			if (idleTime == 0 && !jumping) {
				jumping = true;
				az = -0.1f;
				vz = 1.2f;
			}
		}
		super.animate(compteur_animation);

		z = z + vz;
		if (z < 0) {
			z = 0;
			vz = 0;
			az = 0;
			jumping = false;
		}
		vz = vz + az;
	}

	@Override
	public void finaliseComportement(int compteur_animation) {
		// Look minor variation
		if (!jumping) {
			if (idleTime == 0) {
				idleTime = 4 + Hasard.rand(10);
				if (!alerte) {
					idleTime+= 32 + Hasard.rand(10); 
				}
				addSpr = Hasard.rand(2);
			} else {
				idleTime--;
			}
		} else {
			addSpr = 2;
			if (vz >= 0) {
				addSpr++;
			}
		}
		super.finaliseComportement(compteur_animation);
	}
}
