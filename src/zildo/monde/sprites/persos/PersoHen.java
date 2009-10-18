package zildo.monde.sprites.persos;

import zildo.client.SoundPlay.BankSound;
import zildo.monde.Hasard;
import zildo.monde.map.Point;
import zildo.server.EngineZildo;

public class PersoHen extends PersoNJ {

	public void animate(int compteur_animation) {
		super.animate(compteur_animation);
		
		if (linkedPerso != null && !flying) {
			if (attente==0) {
				// Play a hen random sound
				BankSound snd=BankSound.Poule1;
				if (Hasard.lanceDes(5)) {
					snd=BankSound.Poule2;
				}
				EngineZildo.soundManagement.broadcastSound(snd, new Point(x, y));
				attente=24;
			} else {
				attente--;
			}
		}
	}
}
