package zildo.monde.decors;

import java.util.ArrayList;
import java.util.List;


public class ElementGoodies extends Element {

	// Coeur : nSpr=40
	// Diamant : nSpr=10
	
	public ElementGoodies() {
		super();
		spe=540;	// Durée de vie du goodies, en frames (on tourne en général à 60FPS : 540==9sec)
	}
	
	public List<SpriteEntity> animate() {
		
		super.animate();
		
		List<SpriteEntity> deads=new ArrayList<SpriteEntity>();
		
		spe--;
		
		if (nSpr == 40) {
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
		
		if (nSpr==10 || (nSpr>=48 && nSpr<=56)) {
			// Il s'agit d'un diamant ou du coeur (10)
			x=x-vx;
			vx=(int)(vx+1) % 100;
			// 1) brillance
			if (vx<33 && nSpr!=10) {		// Les diamants brillent
				addSpr=(int)(vx / 10) % 3;
			}
			// 2) s'arrête sur le sol
			if (z<4) {
				z=4;
			}
		}
		
		if (spe==0) {
			// Le sprite doit mourir
			deads.add(this);
		} else if (spe<120) {
			visible=(spe%4>1);
		} else if (spe<60) {
			visible=(spe%2==0);
		}
		
		setAjustedX((int) x);
		setAjustedY((int) y);
		return deads;
	}
	
}
