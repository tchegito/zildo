package zildo.monde.client;

import zildo.fwk.engine.EngineZildo;
import zildo.monde.Area;
import zildo.monde.persos.Perso;

public class MapDisplay {

    private byte compteur_animation;
    private int camerax,cameray;
    private boolean map_scrolling;			// Pour éviter de déplacer les objets quand
											// on passe d'une map à l'autre
	private Area currentMap;
	
    public MapDisplay(Area p_map) {
    	currentMap=p_map;
    	
		// Inits map parameters
		camerax=0;
		cameray=0;
    }

	
	public void updateMap() {
		compteur_animation=(byte) ((compteur_animation+1) % (3*20));
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// centerCamera
	///////////////////////////////////////////////////////////////////////////////////////
	public void centerCamera() {
		Perso zildo=EngineZildo.persoManagement.getZildo();
		int x=(int) zildo.getX();
		int y=(int) zildo.getY();
		
		camerax=x-16*10;
	
		cameray=y-16*6;
	
		// Overflow tests
		if (camerax > (16*currentMap.getDim_x() - 16 * 20)) {
			camerax=16*currentMap.getDim_x() - 16 * 20;
		}
		if (cameray > (16*currentMap.getDim_y() - 16 * 15 )) {	// marche avec 17
			cameray=16*currentMap.getDim_y() - 16 * 15 ;
		}
	
		if (camerax < 0) {
			camerax=0;
		}
		if (cameray < 0) {
			cameray=0;
		}
	}
	
	public int getCamerax() {
		return camerax;
	}

	public void setCamerax(int camerax) {
		this.camerax = camerax;
	}

	public int getCameray() {
		return cameray;
	}

	public void setCameray(int cameray) {
		this.cameray = cameray;
	}


	public Area getCurrentMap() {
		return currentMap;
	}
}
