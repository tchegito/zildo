package zildo.monde.sprites.persos;

import zildo.monde.sprites.persos.ia.PathFinderStraightFlying;
import zildo.monde.sprites.utils.MouvementPerso;
import zildo.monde.util.Point;
import zildo.monde.util.Pointf;
import zildo.server.EngineZildo;

public class PersoBat extends PersoShadowed {

	double currentSpeed;
	double speedAlpha;
	
	public PersoBat() {
		pathFinder = new PathFinderStraightFlying(this, 6, 2);
		pv = 2;
	}
	
	@Override
	public void animate(int compteur_animation) {
		PersoZildo zildo = EngineZildo.persoManagement.getZildo();
		if (!alerte) {
			// Detects Zildo
			if (zildo != null) {
				double distance = Point.distance(x,  y, zildo.x, zildo.y);
				if (distance < 48) {
					alerte = true;
				}
			}
		} else {
			if (pathFinder.getTarget() == null) {
				// Set a target
				double xx = 2*zildo.x - x;
				double yy = 2*zildo.y - y;
				pathFinder.setTarget(new Point((int) xx, (int) yy));
				
				currentSpeed = 1f + 0.5f * Math.random();
				quel_deplacement = MouvementPerso.VOLESPECTRE;
				
				speedAlpha = 0;
			} else {
				speedAlpha+= 0.02f;
				Pointf pos = pathFinder.reachDestination((float) (currentSpeed*Math.sin(speedAlpha)));
				if (pos.x != Float.NaN && pos.y != Float.NaN) { 
					x = pos.x;
					y = pos.y;
				}
			}
		}
		super.animate(compteur_animation);
	}
	
	@Override
	public void finaliseComportement(int compteur_animation) {
		if (alerte) {
			addSpr = (compteur_animation % 16) / 8;
		}
		super.finaliseComportement(compteur_animation);
	}
	
}
