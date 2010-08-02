/**
 * Legend of Zildo
 * Copyright (C) 2006-2010 Evariste Boussaton
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

package zildo.server;


import java.util.logging.Logger;

import zildo.client.ClientEngineZildo;
import zildo.fwk.IntSet;
import zildo.fwk.file.EasyBuffering;
import zildo.fwk.file.EasyReadingFile;
import zildo.fwk.file.EasyWritingFile;
import zildo.fwk.script.xml.TriggerElement;
import zildo.monde.collision.Collision;
import zildo.monde.map.Angle;
import zildo.monde.map.Area;
import zildo.monde.map.Case;
import zildo.monde.map.ChainingPoint;
import zildo.monde.map.Point;
import zildo.monde.map.TileCollision;
import zildo.monde.map.Zone;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.persos.PersoZildo;


public class MapManagement {

	protected Logger logger=Logger.getLogger("MapManagement");

	private Area currentMap;
	private TileCollision tileCollision;
	
	ChainingPoint changingMapPoint;
	Angle mapScrollAngle;
	
	public MapManagement()
	{
		tileCollision=new TileCollision();
		
		// Init variables
		currentMap=null;
		mapScrollAngle=null;
	}
	
	public void finalize()
	{
		this.deleteCurrentMap();
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// deleteCurrentMap
	///////////////////////////////////////////////////////////////////////////////////////
	public void deleteCurrentMap() {
		
		// Before, it was a guiManagement.clean() here
		
		EngineZildo.persoManagement.clearPersosWithoutZildo();
	
		EngineZildo.dialogManagement.clearDialogs();
	
		EngineZildo.spriteManagement.clearSpritesWithoutZildo();
	
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// charge_map
	///////////////////////////////////////////////////////////////////////////////////////
	// IN:filename to load as a map
	///////////////////////////////////////////////////////////////////////////////////////
	// Destroy any data referring to current map, and given one
	///////////////////////////////////////////////////////////////////////////////////////
    public void charge_map(String mapname) {
        // Remove previous map
        if (currentMap != null) {
            this.deleteCurrentMap();
        }

        // Adjust map according the quest diary
        String adjustedMapName = EngineZildo.scriptManagement.getReplacedMapName(mapname);
        
        // Trigger the location
        TriggerElement trig=TriggerElement.createLocationTrigger(adjustedMapName);
        EngineZildo.scriptManagement.trigger(trig);
        
        // Misc
        if (EngineZildo.game.multiPlayer) {
        	EngineZildo.multiplayerManagement.spawnQuad();
        }
        
        // Load a new one
        currentMap = loadMapFile(adjustedMapName);
        currentMap.setName(mapname);
        
        // Adjust map at Zildo's location
        PersoZildo zildo=EngineZildo.persoManagement.getZildo();
        if (zildo != null) {
        	zildo.walkTile(false);
        }
        
        if (!EngineZildo.game.editing && !EngineZildo.soundManagement.isForceMusic()) {
        	ClientEngineZildo.soundPlay.playMapMusic(currentMap);
		}
		
        analyseAltitude();

    }

    ///////////////////////////////////////////////////////////////////////////////////////
	// loadMapFile
	///////////////////////////////////////////////////////////////////////////////////////
	// IN:filename to load as a map
	///////////////////////////////////////////////////////////////////////////////////////
	Area loadMapFile(String mapname) 
	{ 
		System.out.println("Loading "+mapname);
	
		// If file name isn't complete, do it.
		if (mapname.indexOf("/") ==-1 && mapname.indexOf("\\") == -1 && mapname.toUpperCase().indexOf(".MAP") == -1) {
			mapname=mapname+=".MAP";
		}
		
		// Infos de base
		EasyReadingFile file=new EasyReadingFile(mapname);
		Area map=Area.deserialize(file, true);
		
		map.setName(mapname);
		
		this.logger.info("Map loaded: "+mapname);

		return map;
	}

	public void saveMapFile(String p_fileName) {
		EasyBuffering file = new EasyBuffering();
		currentMap.serialize(file);
		EasyWritingFile serializedMap = new EasyWritingFile(file);

		serializedMap.saveFile(p_fileName);
    }

	public boolean isWalkable(int p_onmap) {
		return tileCollision.collide(p_onmap); 
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// collide
	///////////////////////////////////////////////////////////////////////////////////////
	// IN:map coordinates and a character to deal with
	// OUT:TRUE if the given character collides with something or somebody
	///////////////////////////////////////////////////////////////////////////////////////
	public boolean collide(int tx,int ty,Element quelElement)
	{
		int mx,my;            // Position map
	    int modx,mody;
	    int on_map;           // La case où se déplace le joueur
	    
		if (currentMap == null) {
			return false;
		}
		
		if (tx<0 || ty<0 || 
		    tx>(currentMap.getDim_x()-1)*16+15 ||
			ty>(currentMap.getDim_y()-1)*16+15)
			// On empêche la collision sur les bords de cartes
            return quelElement != null && !quelElement.isZildo();
		
	    Angle angleFlying=null;
		Point size=new Point(8,4);	// Default size
        if (quelElement != null && quelElement.flying && quelElement.getAngle() != null
                && SpriteEntity.ENTITYTYPE_PERSO != quelElement.getEntityType()) {
		    angleFlying=quelElement.getAngle();

	    	if (quelElement.getCollision() != null) {
	    		size=quelElement.getCollision().size;
	    	}
    		ty-=quelElement.z;
	    	int cx=(tx / 16);
	    	int cy=(ty / 16);
	    	int caseZ=currentMap.readAltitude(cx,cy);
	    	on_map=currentMap.readmap(cx, cy);
			int elemAltitude=quelElement.relativeZ+(int) quelElement.getZ()/16;

			modx=tx%16;
			mody=ty%16;
	    	if (caseZ < elemAltitude) {	// We are too high => no collision
	    		return false;
	    	} else if( caseZ == elemAltitude && getAngleJump(angleFlying, cx, cy) != null) {
	    		return false; // Same altitude but under the cliff => no collision
	    	}  else if (caseZ > elemAltitude) {
	    		return true;	// Obstacle
	    	}
	    	if (tileCollision.collide(modx, mody, on_map)) {
	    		return true;
	    	}
	    	return EngineZildo.spriteManagement.collideSprite(tx,ty,quelElement);
	    	
	    }
	    
		final int[] tab_add={-1,-1,1,1,-1};
		
		// On teste les 4 coins d'un carré de 4x4
		for (int i=0;i<4;i++)
		{
			mx=(tx+(size.x / 2)*tab_add[i]);
			my=(ty+(size.y / 2)*tab_add[i+1]);
			on_map=currentMap.readmap((mx / 16),(my / 16));
			modx=mx % 16;
			mody=my % 16;
	
			if (mx<0 || my<0 || 
				    mx>(currentMap.getDim_x()-1)*16+15 ||
					my>(currentMap.getDim_y()-1)*16+15)
					// On empêche la collision sur les bords de cartes
		            return !quelElement.isZildo();
			
			if (tileCollision.collide(modx, mody, on_map)) {
				return true;
			}
		}
	
	 
	  // Collision avec les pnj alliés
		if (EngineZildo.persoManagement.collidePerso(tx,ty,quelElement)!=null)
			return true;
	
		if (EngineZildo.spriteManagement.collideSprite(tx,ty,quelElement))
			return true;

		// Returns computed result
		return false;
	}
	
	public boolean collideSprite(int tx, int ty, Collision p_colli) {
		
		if (EngineZildo.persoManagement.collidePerso(tx,ty,null, p_colli.cr)!=null)
			return true;
	
		if (EngineZildo.spriteManagement.collideSprite(tx,ty,p_colli.perso))
			return true;

		// Returns computed result
		return false;
	}
	
	public Angle getAngleJump(Angle angle, int cx,int cy) {
		Area area=getCurrentMap();
		int onMapCurrent=area.readmap(cx, cy);
		int onMap=0;
		Angle result=null;
		switch (onMapCurrent) {
			// Saut diagonal
			case 35:case 106: result=Angle.SUDOUEST; break; //6
			case 19:case 100: result=Angle.NORDOUEST; break; //7
			case 23:case 102: result=Angle.SUDEST; break; //5
			case 27:case 104:  result=Angle.NORDEST; break; //4
			default:
				// Saut latéral}
				switch (angle) {
				case NORD:
					onMap=area.readmap(cx,cy-1);
					if (onMap==21 || onMap==3 || onMap==839)
						result=Angle.NORD;
					break;
				case EST:
					onMap=area.readmap(cx+1,cy);
					if (onMap==25 || onMap==9 || onMap==842 || onMapCurrent == 9)
						result=Angle.EST;
					break;
				case SUD:
					onMap=area.readmap(cx,cy+1);
					if (onMap==32 || onMap==31 || onMap==844)
						result=Angle.SUD;
					break;
				case OUEST:
					onMap=area.readmap(cx-1,cy);
					if (onMap==17 || onMap==15 || onMap==841 || onMapCurrent == 15)
						result=Angle.OUEST;
					break;
				}
		}	
		return result;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// isChangingMap
	///////////////////////////////////////////////////////////////////////////////////////
	public boolean isChangingMap(PersoZildo p_zildo) {
		// Get zildo's position
		float x=p_zildo.getX();
		float y=p_zildo.getY();
		// Store the changing point to use it when processing map swap
		if (currentMap != null) { 
			changingMapPoint=currentMap.isChangingMap(x,y, p_zildo.getAngle());
		}
		return (changingMapPoint !=null);
	}
	
	/**
	 * Returns current chaining point.
	 * @return ChainingPoint
	 */
	public ChainingPoint getChainingPoint() {
		return changingMapPoint;
	}
	
	public Angle getMapScrollAngle() {
		return mapScrollAngle;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// processChangingMap
	///////////////////////////////////////////////////////////////////////////////////////
	// -load new map
	// -set Zildo's position and angle
	///////////////////////////////////////////////////////////////////////////////////////
	public boolean processChangingMap(ChainingPoint p_changingMapPoint) {
		if (p_changingMapPoint != null) {
			// Player is currently on a changing point
			// So we have 3 things to do:
			// 1) turn him on the right angle
			// 2) load new map
			// 3) place zildo at the right location
			PersoZildo zildo=EngineZildo.persoManagement.getZildo();
	
			// 1/3 : angle
			mapScrollAngle=p_changingMapPoint.getAngle((int) zildo.getX(),(int) zildo.getY(),zildo.getAngle());
			zildo.setAngle(mapScrollAngle);
			int orderX=p_changingMapPoint.getOrderX();
			int orderY=p_changingMapPoint.getOrderY();
	
			// 2/3 : load new map
			String previousMapName=currentMap.getName();
			String newMapName=p_changingMapPoint.getMapname();
			int previousDimX=currentMap.getDim_x();
			int previousDimY=currentMap.getDim_y();
			boolean isAlongBorder=currentMap.isAlongBorder((int) zildo.getX(), (int) zildo.getY());
			// ATTENTION !
			// Cette ligne ne va pas marcher dans le cas d'un scroll d'une map à l'autre
			// On ne trouvera jamais le point d'enchaînement, et en plus on perdra la map
			// courante.
			// ATTENTION !
			this.charge_map(newMapName);
			
			// 3/3 : location
			// get the target chaining point and place zildo through it
	
			ChainingPoint chPointTarget=currentMap.getTarget(previousMapName, orderX, orderY);
			if (chPointTarget == null || isAlongBorder) {
				// chPointTarget should never be null !
				// But there is a map (polaky, left border) which fails...
				Point dest=new Point(zildo.x, zildo.y);
				zildo.x=(int) zildo.x;
				zildo.y=(int) zildo.y;
				if (zildo.getY() > previousDimY*16-16) {
					zildo.setY(8 - 8);
					dest.y=(int) zildo.y + 8;
				} else if (zildo.getY() < 4) {
					zildo.setY(currentMap.getDim_y() * 16 - 8 + 8);
					dest.y=(int) zildo.y - 8;
				} else if (zildo.getX() < 4) {
					zildo.setX(currentMap.getDim_x() * 16 - 16 + 16);
					dest.x=(int) zildo.x - 16;
				} else if (zildo.getX() > previousDimX*16-16) {
					zildo.setX(8 - 16);
					dest.x=(int) zildo.x +16;
				}
                zildo.setGhost(true);
                zildo.setTarget(dest);
				zildo.finaliseComportement(EngineZildo.compteur_animation);
				return true;
			} else {
				zildo.setX((chPointTarget.getPx() & 127) * 16 + 16);
				zildo.setY((chPointTarget.getPy() & 127) * 16 + 8);
			}
			float zx=zildo.getX();
			float zy=zildo.getY();
			if ((chPointTarget.getPx() & 128) != 0) {
				// Vertical chaining point
				zildo.setX(zx-8);
				zildo.setY(zy+8);
			}
			switch (mapScrollAngle) {
				case NORD:
					zildo.setY(zy-16);
					break;
				case EST:
					zildo.setX(zx+16);
					break;
				case SUD:
					zildo.setY(zy+16);
					break;
				case OUEST:
					zildo.setX(zx-32);
					break;
			}

            zildo.setEn_bras(null); // Loose his object
            zildo.walkTile(false);
            zildo.finaliseComportement(EngineZildo.compteur_animation);
			return true;
		}
		return false;
	}

	private int normalizeX(int x) {
		if (x<0) {
			return 0;
		}
		if (x>currentMap.getDim_x() * 16 - 1) {
			return currentMap.getDim_x() * 16 - 1;
		}
		return x;
	}
	
	private int normalizeY(int y) {
		if (y<0) {
			return 0;
		}
		if (y>currentMap.getDim_y() * 16 - 1) {
			return currentMap.getDim_y() * 16 - 1;
		}
		return y;
	}
	
	/**
	 * Create a range inside the map, according to current dimensions.
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return Zone
	 */
	public Zone range(int x1, int y1, int x2, int y2) {
		return new Zone(normalizeX(x1), normalizeY(y1), normalizeX(x2),normalizeY(y2));
	}
	public Zone range(float x1, float y1, float x2, float y2) {
		return new Zone((int) x1, (int) y1, (int) x2, (int) y2);
	}

	// Hill boundaries
	private final IntSet leftIncreaseZ=new IntSet(100, 106, 107); //17, 15, 841, 15);
	private final IntSet rightDecreaseZ=new IntSet(102, 103, 104); //25, 9, 842, 9);
	
	/**
	 * Update map with setting the altitude (z attribute)
	 */
	public void analyseAltitude() {
		Area map=getCurrentMap();
		int sizeX=map.getDim_x();
		int sizeY=map.getDim_y();
		int i,j;
		Case c;
		
		int[][] tabZ=new int[sizeY][sizeX];
		for (j=0;j<sizeY;j++) {
			for (i=0;i<sizeX;i++) {
				tabZ[j][i]=0;
			}
		}
		
		// Read left to right
		for (j=0;j<sizeY;j++) {
			c=map.get_mapcase(0,j+4);
			int currentZ=c.getZ();
			for (i=0;i<sizeX;i++) {
				c=map.get_mapcase(i,j+4);
				int onmap=map.readmap(i,j);
				if (leftIncreaseZ.contains(onmap)) {
					currentZ++;
				}
				tabZ[j][i]=currentZ;
				if (rightDecreaseZ.contains(onmap)) {
					currentZ--;
				}
			}
		}
		
		// Set the altitude of each case
		for (j=0;j<sizeY;j++) {
			// calculate the minimal altitude for this line
			int min=tabZ[j][0];
			for (i=0;i<sizeX;i++) {
				if (tabZ[j][i] < min) {
					min = tabZ[j][i];
				}
			}
			
			for (i=0;i<sizeX;i++) {
				int currentZ=tabZ[j][i];
				currentZ-=min;	// correct altitude with threshold
				c=map.get_mapcase(i,j+4);
				c.setZ(currentZ);
			}
		}
	}
	
    /**
     * Return a respawn position, at an empty place. (temporary : this must be fixed in the map)
     * @return
     */
    public Point getRespawnPosition() {
        int x = 831;
        int y = 360;
        while (collide(x, y, null)) {
            x += 16;
        }
        return new Point(x, y);
    }
    
	public Area getCurrentMap() {
		return currentMap;
	}

	public void setCurrentMap(Area currentMap) {
		this.currentMap = currentMap;
	}

    public void updateMap() {
    	if (currentMap != null) {
    		currentMap.update();
    	}
    }
}