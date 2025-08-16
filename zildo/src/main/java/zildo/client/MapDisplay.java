/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
 * 
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

import zildo.Zildo;
import zildo.fwk.gfx.filter.CloudFilter;
import zildo.monde.map.Area;
import zildo.monde.map.accessor.AreaAccessor;
import zildo.monde.map.accessor.HighestFloorAccessor;
import zildo.monde.map.accessor.OneFloorAreaAccessor;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;

public class MapDisplay {

	public static int CENTER_X = 16 * 10;
	public static int CENTER_Y = 16 * 6;
	
    private Point camera;		// Current camera location
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
    	CENTER_X = Zildo.viewPortX / 2;
    	CENTER_Y = Zildo.viewPortY / 2;
    	currentMap=p_map;
    	
		// Inits map parameters
		camera=new Point(0,0);
		targetCamera=null;
    }
	
	///////////////////////////////////////////////////////////////////////////////////////
	// centerCamera
    // smooth: TRUE => we'll use target camera to move smoothly to the target
	///////////////////////////////////////////////////////////////////////////////////////
	public void centerCamera(boolean smooth) {
		Point precCamera = new Point(camera);
		Point newCam = new Point(camera);
		if (focused != null && targetCamera == null) {
			int x= (int) focused.x;
			int y= (int) focused.y;
			newCam = new Point(x - CENTER_X, y - CENTER_Y);

		} else if (targetCamera != null) {
			// Move the camera to the target
			int camSpeed=cameraSpeed;
			if (scrollingAngle != null) {	// double speed if map is scrolling
				camSpeed*=2;
			}
			int diffX = camera.x - targetCamera.x;
			int diffY = camera.y - targetCamera.y;
			if (diffX < 0) {
				newCam.x+=Math.min(camSpeed, -diffX);
			} else if (diffX > 0) {
				newCam.x-=Math.min(camSpeed, diffX);
			}
			if (diffY < 0) {
				newCam.y+=Math.min(camSpeed, -diffY);
			} else if (diffY > 0) {
				newCam.y-=Math.min(camSpeed, diffY);
			}

        }
		// Overflow tests
		if (scrollingAngle == null && targetCamera == null) {
			newCam = calculateMapScroll(newCam);
		}
        
		if (smooth) {
			targetCamera = newCam;
		} else {
			camera = newCam;
	        if (targetCamera != null) {
				if (targetCamera.equals(camera) || camera.equals(precCamera)) {
					targetCamera=null;
					scrollingAngle=null;
				}
	        }
		}
		
        Zildo.pdPlugin.getFilter(CloudFilter.class).setPosition(camera.x, camera.y);
    }
	
	public Point calculateMapScroll(Point cam) {
		Point newCam = new Point(cam);
		if (focused != null) {
			int x= (int) focused.x;
			int y= (int) focused.y;
			newCam = new Point(x - CENTER_X, y - CENTER_Y);
		}
        return cropCamera(newCam);
	}
	
	/** Keep the given camera location inside the map **/
	private Point cropCamera(Point location) {
		Point so = currentMap.getScrollOffset();
		int minX = 16 * so.x;
		int minY = 16 * so.y;
		int maxX = 16 * currentMap.getOriginalDim().x + minX;
		int maxY = 16 * currentMap.getOriginalDim().y + minY;
		if (location.x > (maxX - (CENTER_X << 1))) {
			location.x=maxX - (CENTER_X << 1);
		}
		if (location.y > (maxY - (CENTER_Y << 1) )) {
			location.y=maxY - (CENTER_Y << 1) ;
		}
		if (location.x < minX) {
			location.x=minX;
		}
        if (location.y < minY) {
        	location.y = minY;
        }
        return location;
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
        compteur_animation = compteur_animation + 1;
        return compteur_animation;
	}

	public Area getCurrentMap() {
		return currentMap;
	}
	
	public void setCurrentMap(Area map) {
		currentMap=map;
		ClientEngineZildo.tileEngine.prepareTiles();
		// Initialize the area accessor (to display highest floor, or just one, if map doesn't have much)
		if (currentMap != null) {
			AreaAccessor accessor = null;
			if (currentMap.getHighestFloor() > 0) {
				accessor = new HighestFloorAccessor();
			} else {
				accessor = new OneFloorAreaAccessor();
			}
			ClientEngineZildo.tileEngine.setAreaAccessor(accessor);
		}
	}
	
	public void setTargetCamera(Point p_point) {
		targetCamera=cropCamera(p_point);		
	}
	
	/**
	 * Prepare camera's move for next map.
	 * @param p_angle 
	 */
	public void shiftForMapScroll(Angle p_angle) {
		Point cam = new Point(getCamera());
		Point computed = calculateMapScroll(cam);
		if (p_angle.isHorizontal()) {
			if (computed.x - cam.x != 0) {
				cam.x = computed.x;
			}
		} else {
			if (computed.y - cam.y != 0) {
				cam.y = computed.y;
			}
		}
		
    	Point movedCam;
    	switch (p_angle) {
    	case NORD:
    		movedCam=cam.translate(0, Zildo.viewPortY);
    		break;
    	case EST:
    		movedCam=cam.translate(-Zildo.viewPortX, 0);
    		break;
    	case SUD:
    		movedCam=cam.translate(0, -Zildo.viewPortY);
    		break;
    	case OUEST:
    		movedCam=cam.translate(Zildo.viewPortX, 0);
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
	
	/**
	 * Reset map display, when game is over.
	 */
	public void reset() {
		setCurrentMap(null);
		foreBackController.setDisplaySpecific(true, true);
		targetCamera = null;
		scrollingAngle = null;
	}
}
