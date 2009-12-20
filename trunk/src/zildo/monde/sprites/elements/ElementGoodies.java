package zildo.monde.sprites.elements;

import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoZildo;
import zildo.server.EngineZildo;


public class ElementGoodies extends Element {

	// Coeur : nSpr=40
	// Diamant : nSpr=10
	
	private int timeToAcquire;	// Untakeable. Zildo has just to wait to have it (for chest)
	protected boolean volatil=true;	// TRUE=goodies disappear after a delay
	
	public ElementGoodies() {
		super();
		spe=540;	// Durée de vie du goodies, en frames (on tourne en général à 60FPS : 540==9sec)
	}
	
	public ElementGoodies(Perso p_zildo) {
		linkedPerso=p_zildo;
		timeToAcquire=60;
	}
	
	public void animate() {
		
		super.animate();
		
		if (volatil) {
			spe--;
		}
		
		ElementDescription spr=ElementDescription.fromInt(nSpr);
		if (spr == ElementDescription.HEART_LEFT) {
			// Coeur voletant vers le sol
			if (vx<=-0.15) {
				ax=0.01f;
				addSpr=0;	// Coeur tourné vers la gauche
			} else if (vx>=0.15) {
				ax=-0.01f;
				addSpr=1;	// Coeur tourné vers la droite
			}
			if (z<=4) {
				nSpr=10;
				addSpr=0;
				vx=0;
				ax=0;
				y=y+3;
			}
		}
		
		
		if (spr==ElementDescription.HEART || spr.isMoney()) {
			// Il s'agit d'un diamant ou du coeur (10)
			int eff=EngineZildo.compteur_animation % 100;
			// 1) brillance
			if (eff<33 && spr!=ElementDescription.HEART) {		// Les diamants brillent
				addSpr=(eff / 10) % 3;
			}
			// 2) s'arrête sur le sol
			if (z<4) {
				z=4;
			}
		}
		
		if (timeToAcquire > 0) {
			timeToAcquire--;
			if (timeToAcquire == 0) {
				// Zildo will now have the goodies
				((PersoZildo)linkedPerso).pickGoodies(nSpr);
				dying=true;
			}
		}
		
		if (spe==0) {
			// Le sprite doit mourir
			dying=true;
		} else if (spe<120) {
			visible=(spe%4>1);
		} else if (spe<60) {
			visible=(spe%2==0);
		}
		
		setAjustedX((int) x);
		setAjustedY((int) y);
	}
	
	public boolean isGoodies() {
		return true;
	}
	
	public boolean beingCollided(Perso p_perso) {
		return true;
	}

}
