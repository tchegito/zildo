package zildo.monde.sprites.persos;

import zildo.client.SoundPlay.BankSound;
import zildo.fwk.gfx.PixelShaders;
import zildo.monde.Hasard;
import zildo.monde.map.Point;
import zildo.monde.sprites.utils.MouvementZildo;
import zildo.server.EngineZildo;

public class PersoHen extends PersoNJ {
	
	public void animate(int compteur_animation) {

		super.animate(compteur_animation);
		
		if (linkedPerso != null && !flying) {
			// In Zildo's arms
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
			info=PersoInfo.NEUTRAL;
		} else {
			// Hen is free
			info=PersoInfo.SHOOTABLE_NEUTRAL;
		}
	}
	
	public boolean beingWounded(float cx, float cy, Perso p_shooter) {
		project(cx, cy, 1);
		this.setMouvement(MouvementZildo.TOUCHE);
		this.setWounded(true);
		this.setAlerte(true);				// Zildo is detected, if it wasn't done !
		this.setSpecialEffect(PixelShaders.ENGINEFX_PERSO_HURT);
	
		EngineZildo.soundManagement.broadcastSound(BankSound.MonstreTouche2, this);
	
		return false;
	}
}
