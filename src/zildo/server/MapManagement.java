package zildo.server;


import java.util.logging.Logger;

import zildo.fwk.IntSet;
import zildo.fwk.file.EasyReadingFile;
import zildo.fwk.file.EasyWritingFile;
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
import zildo.prefs.Constantes;


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
	void deleteCurrentMap() {
		
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
        String adjustedMapName = EngineZildo.game.questDiary.getReplacedMapName(mapname);

        // Load a new one
        currentMap = loadMapFile(adjustedMapName);
        currentMap.setName(mapname);
        
        analyseAltitude();

    }
	
	///////////////////////////////////////////////////////////////////////////////////////
	// linkTwoMaps
	///////////////////////////////////////////////////////////////////////////////////////
	Area linkTwoMaps(String mapname)
	{
		// Load next map
		Area secondMap=loadMapFile(mapname);
		// Put the current map next to the second one, so we have both on a double area
		currentMap.translatePoints(currentMap.getDim_x(),0,secondMap);
		return secondMap;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// loadMapFile
	///////////////////////////////////////////////////////////////////////////////////////
	// IN:filename to load as a map
	///////////////////////////////////////////////////////////////////////////////////////
	Area loadMapFile(String mapname) 
	{ 
		System.out.println("Loading "+mapname);
	
		String chemin=Constantes.DATA_PATH;
		chemin+=mapname;
		chemin+=".MAP";
	
		// Infos de base
		EasyReadingFile file=new EasyReadingFile(chemin);
		Area map=Area.deserialize(file, true);
		
		map.setName(mapname);
		
		this.logger.info("Map loaded: "+mapname);

		return map;
	}

	public void saveMapFile(String p_fileName) {
		EasyWritingFile serializedMap = new EasyWritingFile(currentMap.serialize());

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
	    	return tileCollision.collide(modx, mody, on_map);
	    	
	    }
	    
		final int[] tab_add={-1,-1,1,1,-1};
	
		if (tx<0 || ty<0 || 
		    tx>(currentMap.getDim_x()-1)*16 ||
			ty>(currentMap.getDim_y()-1)*16)
			// On empêche la collision sur les bords de cartes
            return !quelElement.isZildo();
	
		
		// On teste les 4 coins d'un carré de 4x4
		for (int i=0;i<4;i++)
		{
			mx=(tx+(size.x / 2)*tab_add[i]);
			my=(ty+(size.y / 2)*tab_add[i+1]);
			on_map=currentMap.readmap((mx / 16),(my / 16));
			modx=mx % 16;
			mody=my % 16;
	
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
		changingMapPoint=currentMap.isChangingMap(x,y);
	
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
	public boolean processChangingMap() {
		if (changingMapPoint != null) {
			// Player is currently on a changing point
			// So we have 3 things to do:
			// 1) turn him on the right angle
			// 2) load new map
			// 3) place zildo at the right location
			PersoZildo zildo=EngineZildo.persoManagement.getZildo();
	
			// 1/3 : angle
			mapScrollAngle=changingMapPoint.getAngle((int) zildo.getX(),(int) zildo.getY(),zildo.getAngle());
			zildo.setAngle(mapScrollAngle);
			int orderX=changingMapPoint.getOrderX();
			int orderY=changingMapPoint.getOrderY();
	
			// 2/3 : load new map
			String previousMapName=currentMap.getName();
			String newMapName=changingMapPoint.getMapname();
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
				if (zildo.getY() > previousDimY*16-16) {
					zildo.setY(8);
				} else if (zildo.getY() < 4) {
					zildo.setY(currentMap.getDim_y() * 16 - 8);
				} else if (zildo.getX() < 4) {
					zildo.setX(currentMap.getDim_x() * 16 - 16);
				} else if (zildo.getX() > previousDimX*16-16) {
					zildo.setX(8);
				}
				//linkTwoMaps(newMapName);
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
		for (int j=0;j<map.getDim_y();j++) {
			Case c=map.get_mapcase(0,j+4);
			int currentZ=c.getZ();
			for (int i=0;i<map.getDim_x();i++) {
				c=map.get_mapcase(i,j+4);
				int onmap=map.readmap(i,j);
				if (leftIncreaseZ.contains(onmap)) {
					currentZ++;
				}
				c.setZ(currentZ);
				if (rightDecreaseZ.contains(onmap)) {
					currentZ--;
				}
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
            y -= 16;
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
        this.currentMap.update();
    }
}