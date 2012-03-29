package zildo.monde.sprites.persos;

import zildo.monde.sprites.persos.ia.PathFinderFlying;
import zildo.monde.sprites.utils.MouvementPerso;
import zildo.monde.util.Point;

import zildo.server.EngineZildo;

public class PersoBat extends PersoShadowed {

	double currentSpeed;
	
	public PersoBat() {
		pathFinder = new PathFinderFlying(this);
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
				
				currentSpeed = 1.5f * Math.random();
				quel_deplacement = MouvementPerso.VOLESPECTRE;
			} else {
				pathFinder.reachDestination((float) currentSpeed);
			}
		}
	}
	
	@Override
	public void finaliseComportement(int compteur_animation) {
		if (alerte) {
			addSpr = (compteur_animation % 16) / 8;
		}
		super.finaliseComportement(compteur_animation);
	}
	
}
