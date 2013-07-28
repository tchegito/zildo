package zildo.monde.sprites.persos;

import zildo.client.sound.BankSound;
import zildo.monde.Hasard;
import zildo.monde.map.Area;
import zildo.monde.sprites.persos.ia.PathFinderStraightFlying;
import zildo.monde.sprites.utils.MouvementPerso;
import zildo.monde.util.Anticiper;
import zildo.monde.util.Point;
import zildo.monde.util.Pointf;
import zildo.server.EngineZildo;

public class PersoBat extends PersoShadowed {

	double currentSpeed;
	double speedAlpha;
	
	final float distanceSight = 48f;
	int countSound = 0;
	
	static Anticiper anticiper = new Anticiper(1.5f);
	
	public PersoBat() {
		pathFinder = new PathFinderStraightFlying(this, 6, 2);
		pv = 2;
	}
	
	@Override
	public void move() {
		PersoZildo zildo = EngineZildo.persoManagement.getZildo();
		if (!alerte) {
			// Detects Zildo
			if (zildo != null) {
				double distance = Point.distance(x,  y, zildo.x, zildo.y);
				if (distance < distanceSight) {
					alerte = true;
				} else {
					Area area = EngineZildo.mapManagement.getCurrentMap();
					if (area.isAnAlertAtLocation(x, y)) {
						currentSpeed = 1.4f + 0.2f * Math.random();
						Point p = area.getAlertLocation();
						p.x += Hasard.intervalle(12);
						p.y += Hasard.intervalle(12);
						pathFinder.setTarget(p);
						alerte = true;
					}
				}
			}
		} else {
			if (pathFinder.getTarget() == null) {
				// Is Zildo close enough ?
				float distance = Point.distance(zildo.x, zildo.y, x, y);
				if (distance > distanceSight) {
					// Stop bat
					alerte = false;
					pathFinder.setTarget(null);
					addSpr = 0;
					z=0;
				} else {
					// Set a target by guessing future Zildo's location
					Point t = anticiper.anticipeTarget(this, zildo);
					pathFinder.setTarget(t);
					currentSpeed = 1.4f + 0.2f * Math.random();
					quel_deplacement = MouvementPerso.VOLESPECTRE;
					
					if (countSound <= 0) {
						EngineZildo.soundManagement.broadcastSound(BankSound.Bat, this);
						countSound = 24;
					}
					speedAlpha = 0;
				}
			} else {
				if (speedAlpha < Math.PI / 2) {
					speedAlpha+= 0.03f;
				}
				float frameSpeed = (float) (currentSpeed*Math.sin(speedAlpha));
				Pointf pos = pathFinder.reachDestination(frameSpeed);
				if (pos.x != Float.NaN && pos.y != Float.NaN) { 
					x = pos.x;
					y = pos.y;
				}
			}
			if (countSound > 0){
				countSound--;
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
