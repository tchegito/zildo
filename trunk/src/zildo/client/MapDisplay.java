package zildo.client;

import zildo.monde.map.Angle;
import zildo.monde.map.Area;
import zildo.monde.map.Point;
import zildo.monde.sprites.SpriteEntity;

public class MapDisplay {

    private Point camera;
	private Point targetCamera;

    private Area currentMap;
	   
	private int compteur_animation;			// clone from mapManagement (for now)
	
    public MapDisplay(Area p_map) {
    	currentMap=p_map;
    	
		// Inits map parameters
		camera=new Point(0,0);
		targetCamera=null;
    }
	
	///////////////////////////////////////////////////////////////////////////////////////
	// centerCamera
	///////////////////////////////////////////////////////////////////////////////////////
	public void centerCamera() {
		if (targetCamera == null) {
			SpriteEntity zildo=ClientEngineZildo.spriteDisplay.getZildo();
			int x= (int) zildo.x;
			int y= (int) zildo.y;
			
			camera.x=x-16*10;
		
			camera.y=y-16*6;
		
			// Overflow tests
			if (camera.x > (16*currentMap.getDim_x() - 16 * 20)) {
				camera.x=16*currentMap.getDim_x() - 16 * 20;
			}
			if (camera.y > (16*currentMap.getDim_y() - 16 * 15 )) {	// marche avec 17
				camera.y=16*currentMap.getDim_y() - 16 * 15 ;
			}
		
			if (camera.x < 0) {
				camera.x=0;
			}
	        if (camera.y < 0) {
	            camera.y = 0;
	        }
		} else {
			// Move the camera to the target
			if (camera.x < targetCamera.x) {
				camera.x+=2;
			} else if (camera.x > targetCamera.x) {
				camera.x-=2;
			}
			if (camera.y < targetCamera.y) {
				camera.y+=2;
			} else if (camera.y > targetCamera.y) {
				camera.y-=2;
			}
			
			if (targetCamera.equals(camera)) {
				targetCamera=null;
			}
        }
    }
	
	public boolean isScrolling() {
		return targetCamera != null;
	}
	
	public Point getCamera() {
		return camera;
	}

	public Point getTargetCamera() {
		return targetCamera;
	}
	
	public void setCamera(Point p_camera) {
		this.camera = p_camera;
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
	
	public void setTargetCamera(Point p_point) {
		targetCamera=p_point;		
	}
	
	/**
	 * Prepare camera's move for next map.
	 * @param p_angle 
	 */
	public void shiftForMapScroll(Angle p_angle) {
    	Point camera=getCamera();
    	Point movedCamera;
    	switch (p_angle) {
    	case NORD:
    		movedCamera=camera.translate(0, 240);
    		break;
    	case EST:
    		movedCamera=camera.translate(-320, 0);
    		break;
    	case SUD:
    		movedCamera=camera.translate(0, -240);
    		break;
    	case OUEST:
    		movedCamera=camera.translate(320, 0);
    		break;
    	default:
    		throw new RuntimeException("Can't scroll to "+p_angle+" !");
    	}
    	setCamera(movedCamera);
        setTargetCamera(camera);
	}
}
