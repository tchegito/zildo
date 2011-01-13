/**
 * Legend of Zildo
 * Copyright (C) 2006-2011 Evariste Boussaton
 * Based on original Zelda : link to the past (C) Nintendo 1992
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package zildo.client;

import zildo.monde.map.Angle;
import zildo.monde.map.Area;
import zildo.monde.map.Point;
import zildo.monde.sprites.SpriteEntity;

public class MapDisplay {

    private Point camera;		// Current camera locatino
    private Point targetCamera;	// Target camera location (if not null, camera moves smoothly to it)
    private final int cameraSpeed=2;
    private SpriteEntity focused;
	
    private Angle scrollingAngle;
	
    private Area currentMap;
    private Area previousMap;
    
    private int compteur_animation;			// clone from mapManagement (for now)
    
    public ForeBackController foreBackController=new ForeBackController();

    // ZEditor specific
    boolean displayBackground=true;
    boolean displayForeground=true;
    
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
		if (focused != null && targetCamera == null) {
			int x= (int) focused.x;
			int y= (int) focused.y;
			
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
		} else if (targetCamera != null) {
			// Move the camera to the target
			int camSpeed=cameraSpeed;
			if (scrollingAngle != null) {	// double speed if map is scrolling
				camSpeed*=2;
			}
			if (camera.x < targetCamera.x) {
				camera.x+=camSpeed;
			} else if (camera.x > targetCamera.x) {
				camera.x-=camSpeed;
			}
			if (camera.y < targetCamera.y) {
				camera.y+=camSpeed;
			} else if (camera.y > targetCamera.y) {
				camera.y-=camSpeed;
			}
			
			if (targetCamera.equals(camera)) {
				targetCamera=null;
				scrollingAngle=null;
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
    	Point cam=getCamera();
    	Point movedCam;
    	switch (p_angle) {
    	case NORD:
    		movedCam=cam.translate(0, 240);
    		break;
    	case EST:
    		movedCam=cam.translate(-320, 0);
    		break;
    	case SUD:
    		movedCam=cam.translate(0, -240);
    		break;
    	case OUEST:
    		movedCam=cam.translate(320, 0);
    		break;
    	default:
    		throw new RuntimeException("Can't scroll to "+p_angle+" !");
    	}
    	setCamera(movedCam);
        setTargetCamera(cam);
        scrollingAngle=p_angle;
	}

	public Angle getScrollingAngle() {
		return scrollingAngle;
	}

	public void setFocusedEntity(SpriteEntity p_entity) {
		focused=p_entity;
	}

	public Area getPreviousMap() {
		return previousMap;
	}

	public void setPreviousMap(Area p_previousMap) {
		previousMap = p_previousMap;
	}
}
