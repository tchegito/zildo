package zildo.client;

import zildo.monde.decors.SpriteEntity;
import zildo.monde.map.Area;

public class MapDisplay {

    private int camerax,cameray;
    private boolean map_scrolling;			// Pour éviter de déplacer les objets quand
											// on passe d'une map à l'autre
	private Area currentMap;
	
	private int compteur_animation;			// clone from mapManagement (for now)
	
    public MapDisplay(Area p_map) {
    	currentMap=p_map;
    	
		// Inits map parameters
		camerax=0;
		cameray=0;
    }
	
	///////////////////////////////////////////////////////////////////////////////////////
	// centerCamera
	///////////////////////////////////////////////////////////////////////////////////////
	public void centerCamera() {
		SpriteEntity zildo=ClientEngineZildo.spriteDisplay.getZildo();
		int x= (int) zildo.x;
		int y= (int) zildo.y;
		
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

	public int getCompteur_animation() {
        compteur_animation = (byte) ((compteur_animation + 1) % (3 * 20));
        return compteur_animation;
	}

	public Area getCurrentMap() {
		return currentMap;
	}
	
	public void setCurrentMap(Area map) {
		currentMap=map;
		ClientEngineZildo.tileEngine.prepareTiles(map);

	}
}
