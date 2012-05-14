/**
 * Legend of Zildo
 * Copyright (C) 2006-2012 Evariste Boussaton
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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import zildo.Zildo;
import zildo.client.ClientEngineZildo;
import zildo.client.sound.Ambient.Atmosphere;
import zildo.fwk.collection.IntSet;
import zildo.fwk.file.EasyBuffering;
import zildo.fwk.file.EasyWritingFile;
import zildo.fwk.gfx.filter.CloudFilter;
import zildo.fwk.script.xml.element.TriggerElement;
import zildo.monde.map.Area;
import zildo.monde.map.Case;
import zildo.monde.map.ChainingPoint;
import zildo.monde.map.Tile;
import zildo.monde.map.TileCollision;
import zildo.monde.sprites.desc.EntityType;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoZildo;
import zildo.monde.sprites.utils.MouvementZildo;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
import zildo.monde.util.Zone;
import zildo.resource.Constantes;

public class MapManagement {

	protected Logger logger = Logger.getLogger("MapManagement");

	private Area currentMap;
	private Area previousMap;

	private TileCollision tileCollision;

	ChainingPoint changingMapPoint;
	Angle mapScrollAngle;
	TriggerElement currentMapTrigger;
	
	public MapManagement() {
		tileCollision = TileCollision.getInstance();

		// Init variables
		currentMap = null;
		mapScrollAngle = null;
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// deleteCurrentMap
	// /////////////////////////////////////////////////////////////////////////////////////
	public void deleteCurrentMap() {

		// Before, it was a guiManagement.clean() here

		EngineZildo.persoManagement.clearPersos(false);

		EngineZildo.spriteManagement.clearSprites(false);

	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// charge_map
	// /////////////////////////////////////////////////////////////////////////////////////
	// IN:filename to load as a map
	// /////////////////////////////////////////////////////////////////////////////////////
	// Destroy any data referring to current map, and given one
	// /////////////////////////////////////////////////////////////////////////////////////
	public void loadMap(String p_mapname, boolean p_additive) {
		// Remove previous map
		if (p_additive) {
			previousMap = currentMap; // Keep previous map for the scrolling
										// phasis
		} else {
			if (currentMap != null) {
				this.deleteCurrentMap();
			}
			previousMap = null;
		}

		// Adjust map according the quest diary
		String adjustedMapName = EngineZildo.scriptManagement
				.getReplacedMapName(p_mapname);

		// Trigger the location
		if (!EngineZildo.game.editing) {
			currentMapTrigger = TriggerElement
					.createLocationTrigger(adjustedMapName, null);
			EngineZildo.scriptManagement.trigger(currentMapTrigger);
			EngineZildo.scriptManagement.prepareMapSubTriggers(adjustedMapName);
		}


		// Do the map replacements
		EngineZildo.scriptManagement.doMapReplacements(adjustedMapName);
		
		// Load a new one
		currentMap = loadMapFile(adjustedMapName, p_mapname);
		EngineZildo.spriteManagement.initForNewMap();

		if (!EngineZildo.game.editing) {
			if (!EngineZildo.soundManagement.isForceMusic()) {
				ClientEngineZildo.soundPlay.playMapMusic(currentMap);
			}
			switch (ClientEngineZildo.ambient.getWeather(currentMap)) {
				case CLOUD:
					ClientEngineZildo.filterCommand.active(CloudFilter.class, true, null);
					break;
				case USUAL:
					ClientEngineZildo.filterCommand.active(CloudFilter.class, false, null);
					break;
			}

		}

		analyseAltitude();

	}

	public void clearMap() {
		Atmosphere savedAtmo = Atmosphere.OUTSIDE;	// Default 
		if (currentMap != null) {
			savedAtmo = currentMap.getAtmosphere();
			deleteCurrentMap();
		}
		currentMap = new Area(savedAtmo);
		currentMap.setName("Nouvelle");
		analyseAltitude();
	}

	/**
	 * Load the given map
	 * 
	 * @param p_mapname
	 *            file name
	 * @param p_refMapname
	 *            original file name (without adjustment due to quests)
	 * @return
	 */
	Area loadMapFile(String p_mapname, String p_refMapname) {
		//System.out.println("Loading " + p_mapname);

		// If file name isn't complete, do it.
		if (p_mapname.indexOf("/") == -1 && p_mapname.indexOf("\\") == -1
				&& p_mapname.toLowerCase().indexOf(".map") == -1) {
			p_mapname = p_mapname.toLowerCase() + ".map";
		}

		// Infos de base
		EasyBuffering file=Zildo.pdPlugin.openFile(Constantes.MAP_PATH+p_mapname);
		Area map = Area.deserialize(file, p_refMapname, true);

		this.logger.info("Map loaded: " + p_mapname);

		return map;
	}

	public void saveMapFile(String p_fileName) {
		EasyBuffering file = new EasyBuffering();
		currentMap.serialize(file);
		EasyWritingFile serializedMap = new EasyWritingFile(file);

		serializedMap.saveFile(Constantes.MAP_PATH + p_fileName);
	}

	public boolean isWalkable(int p_onmap) {
		return tileCollision.isTileWalkable(p_onmap);
	}

	/**
	 * Convenience method with float. <br/>
	 * Same that {@link #collide(int, int, Element)}
	 * 
	 * @param tx
	 * @param ty
	 * @param quelElement
	 * @return
	 */
	public boolean collide(float tx, float ty, Element quelElement) {
		return collide((int) tx, (int) ty, quelElement);
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// collide
	// /////////////////////////////////////////////////////////////////////////////////////
	// IN:map coordinates and a character to deal with
	// OUT:TRUE if the given character collides with something or somebody
	// /////////////////////////////////////////////////////////////////////////////////////
	public boolean collide(int tx, int ty, Element quelElement) {
		int modx, mody;
		int on_map; // La case où se déplace le joueur

		if (currentMap == null) {
			return false;
		}

		// Is it a ghost ?
		boolean ghost = false;
		Perso p = quelElement != null && quelElement.getEntityType().isPerso() ? (Perso) quelElement : null;
		if (p != null && p.isUnstoppable()) {
			return false;
		}
		ghost = p !=null && p.isGhost();
		
		if (currentMap.isOutside(tx, ty)) {
			// Detect element out of the map, except for 3 cases : Zildo, ghosts and single pleyer
			return quelElement != null && !ghost && (!quelElement.isZildo() || EngineZildo.game.multiPlayer);
		}
		Angle angleFlying = null;
		Point size = new Point(8, 4); // Default size
		if (quelElement != null && quelElement.flying
				&& quelElement.getAngle() != null
				&& EntityType.PERSO != quelElement.getEntityType()) {
			// Flying object
			angleFlying = quelElement.getAngle();

			if (quelElement.getCollision() != null) {
				size = quelElement.getCollision().size;
			}
			ty -= quelElement.z;
			int cx = (tx / 16);
			int cy = (ty / 16);
			int caseZ = currentMap.readAltitude(cx, cy);
			on_map = currentMap.readmap(cx, cy);
			int elemAltitude = quelElement.relativeZ + (int) quelElement.getZ()
					/ 16;

			modx = tx % 16;
			mody = ty % 16;
			if (caseZ < elemAltitude) { // We are too high => no collision
				return false;
			} else if (caseZ == elemAltitude
					&& getAngleJump(angleFlying, cx, cy) != null) {
				return false; // Same altitude but under the cliff => no
								// collision
			} else if (caseZ > elemAltitude) {
				return true; // Obstacle
			}
			if (tileCollision.collide(modx, mody, on_map)) {
				return true;
			}
			return EngineZildo.spriteManagement.collideSprite(tx, ty,
					quelElement);

		}

		// Check collision on tile level
		boolean collideTile = collideTile(tx, ty, ghost, size, quelElement);

		if (collideTile) {
			return true;
		}
		
		// Collision with friendly NPC
		if (EngineZildo.persoManagement.collidePerso(tx, ty, quelElement) != null)
			return true;

		// Collision with sprites
		if (EngineZildo.spriteManagement.collideSprite(tx, ty, quelElement))
			return true;

		// Returns computed result
		return false;
	}

	private boolean collideTile(int tx, int ty, boolean ghost, Point size, Element quelElement) {
		int mx, my; // Position map
		int on_map;
		int modx, mody;
		
		final int[] tab_add = { -1, -1, 1, 1, -1 };

		// Check on back or fore ground, depending on the character we're checking
		boolean foreground = quelElement != null && quelElement.isForeground();

		// On teste les 4 coins d'un carré de 4x4
		for (int i = 0; i < 4; i++) {
			mx = (tx + (size.x / 2) * tab_add[i]);
			my = (ty + (size.y / 2) * tab_add[i + 1]);
			Tile tile = currentMap.readmap((mx / 16), (my / 16), foreground);
			if (tile == null) {
				continue;
			}
			on_map = tile.getValue();
			modx = mx % 16;
			mody = my % 16;

			if (currentMap.isOutside(mx, my)) {
				// On empêche la collision sur les bords de cartes
				return quelElement != null && !quelElement.isZildo() && !ghost;
			}
			if (tileCollision.collide(modx, mody, on_map)) {
				return true;
			}
		}
		return false;
	}

	public boolean collideSprite(int tx, int ty, int p_radius, Element p_element) {

		if (EngineZildo.persoManagement.collidePerso(tx, ty, null, p_radius) != null)
			return true;

		if (EngineZildo.spriteManagement.collideSprite(tx, ty, p_element))
			return true;

		// Returns computed result
		return false;
	}

	public Angle getAngleJump(Angle angle, int cx, int cy) {
		Area area = getCurrentMap();
		int onMapCurrent = area.readmap(cx, cy);
		int onMap = 0;
		Angle result = null;
		switch (onMapCurrent) {
		// Saut diagonal
		case 35:
		case 106:
			result = Angle.SUDOUEST;
			break; // 6
		case 19:
		case 100:
			result = Angle.NORDOUEST;
			break; // 7
		case 23:
		case 102:
			result = Angle.SUDEST;
			break; // 5
		case 27:
		case 104:
			result = Angle.NORDEST;
			break; // 4
		default:
			// Saut latéral}
			switch (angle) {
			case NORD:
				onMap = area.readmap(cx, cy - 1);
				if (onMap == 21 || onMap == 3 || onMap == 839)
					result = Angle.NORD;
				break;
			case EST:
				onMap = area.readmap(cx + 1, cy);
				if (onMap == 25 || onMap == 9 || onMap == 842
						|| onMapCurrent == 9)
					result = Angle.EST;
				break;
			case SUD:
				onMap = area.readmap(cx, cy + 1);
				if (onMap == 32 || onMap == 31 || onMap == 3*256 + 76 || onMap == 3*256 + 5)
					result = Angle.SUD;
				break;
			case OUEST:
				onMap = area.readmap(cx - 1, cy);
				if (onMap == 17 || onMap == 15 || onMap == 841
						|| onMapCurrent == 15)
					result = Angle.OUEST;
				break;
			}
		}
		return result;
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// isChangingMap
	// /////////////////////////////////////////////////////////////////////////////////////
	public boolean isChangingMap(PersoZildo p_zildo) {
		// Get zildo's position
		float x = p_zildo.getX();
		float y = p_zildo.getY();
		// Store the changing point to use it when processing map swap
		boolean noChange = changingMapPoint == null;
		if (currentMap != null) {
			changingMapPoint = currentMap.isChangingMap(x, y, p_zildo
					.getAngle());
		}
		if (changingMapPoint != null && noChange) {
			// EngineZildo.soundManagement.playSound(BankSound.ZildoMonte,
			// p_zildo);
		}
		return (changingMapPoint != null);
	}

	/**
	 * Returns current chaining point.
	 * 
	 * @return ChainingPoint
	 */
	public ChainingPoint getChainingPoint() {
		return changingMapPoint;
	}

	public Angle getMapScrollAngle() {
		return mapScrollAngle;
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// processChangingMap
	// /////////////////////////////////////////////////////////////////////////////////////
	// -load new map
	// -set Zildo's position and angle
	// /////////////////////////////////////////////////////////////////////////////////////
	public boolean processChangingMap(ChainingPoint p_changingMapPoint) {
		if (p_changingMapPoint != null) {
			// Player is currently on a changing point
			// So we have 3 things to do:
			// 1) turn him on the right angle
			// 2) load new map
			// 3) place zildo at the right location
			PersoZildo zildo = EngineZildo.persoManagement.getZildo();

			// 1/3 : angle
			mapScrollAngle = p_changingMapPoint.getAngle((int) zildo.getX(),
					(int) zildo.getY(), zildo.getAngle());
			zildo.setAngle(mapScrollAngle);
			int orderX = p_changingMapPoint.getOrderX();
			int orderY = p_changingMapPoint.getOrderY();

			// 2/3 : load new map
			String previousMapName = currentMap.getName();
			String newMapName = p_changingMapPoint.getMapname();
			int previousDimX = currentMap.getDim_x();
			int previousDimY = currentMap.getDim_y();
			boolean isAlongBorder = currentMap.isAlongBorder((int) zildo.getX(), (int) zildo.getY());

			EngineZildo.spriteManagement.notifyLoadingMap(true);
			
			loadMap(newMapName, isAlongBorder);

			// 3/3 : location
			// get the target chaining point and place zildo through it

			ChainingPoint chPointTarget = currentMap.getTarget(previousMapName,
					orderX, orderY);
			if (chPointTarget == null || isAlongBorder) {
				// chPointTarget should never be null !
				// But there is a map (polaky, left border) which fails...
				Point dest = new Point(zildo.x, zildo.y);
				zildo.x = (int) zildo.x;
				zildo.y = (int) zildo.y;
				if (zildo.getY() > previousDimY * 16 - 16) {
					zildo.setY(8 - 8);
					dest.y = (int) zildo.y + 8;
				} else if (zildo.getY() < 4) {
					zildo.setY(currentMap.getDim_y() * 16 - 8 + 8);
					dest.y = (int) zildo.y - 8;
				} else if (zildo.getX() < 4) {
					zildo.setX(currentMap.getDim_x() * 16 - 16 + 16);
					dest.x = (int) zildo.x - 16;
				} else if (zildo.getX() > previousDimX * 16 - 16) {
					zildo.setX(8 - 16);
					dest.x = (int) zildo.x + 16;
				}
				zildo.setGhost(true);
				zildo.setTarget(dest);

				shiftPreviousMap(mapScrollAngle);
			} else {
				zildo.setX(chPointTarget.getPx() * 16 + 16);
				zildo.setY(chPointTarget.getPy() * 16 + 8);
				float zx = zildo.getX();
				float zy = zildo.getY();
				if( chPointTarget.isSingle()) {
					zx-=8;
				} else if (chPointTarget.isVertical()) {
					// Vertical chaining point
					zildo.setX(zx - 8);
					zildo.setY(zy + 8);
				}
				Angle a = mapScrollAngle;
				int movedX = (int) zx;
				int movedY = (int) zy;
				// We will try 2 different angles. Usually Zildo gets in and out
				// with the same angle.
				// But stairs are always north to north.
				for (int i = 0; i < 2; i++) {
					movedX = (int) zx;
					movedY = (int) zy;
					switch (a) {
					case NORD:
						movedY -= 16;
						break;
					case EST:
						movedX += 16;
						movedY += 4;
						break;
					case SUD:
						movedY += 16;
						break;
					case OUEST:
						movedX -= 32;
						movedY += 4;
						break;
					}
					if (collide(2 * movedX - (int) zx, 2 * movedY - (int) zy, zildo)) {
						a = Angle.rotate(a, 2);
					} else {
						break;
					}
					changingMapPoint = chPointTarget;
				}
				zildo.setAngle(a);
				zildo.setX(movedX);
				zildo.setY(movedY);
				if (zildo.getEn_bras() != null) {
					zildo.getEn_bras().dying = true;
					zildo.setMouvement(MouvementZildo.VIDE);
					zildo.setEn_bras(null); // Loose his object
				}
				zildo.walkTile(false);
			}
			zildo.finaliseComportement(EngineZildo.compteur_animation);

			// Adjust map at Zildo's location
			if (zildo != null) {
				zildo.walkTile(false);
			}
			
			EngineZildo.spriteManagement.notifyLoadingMap(false);
		}
		return false;
	}

	/**
	 * Shift the previous map to the right position, in order to have it
	 * sticked to the new one. So we calculate the shift coordinates.
	 * @param p_mapScrollAngle
	 */
	private void shiftPreviousMap(Angle p_mapScrollAngle) {
		// 
		Angle angleShift = Angle.rotate(p_mapScrollAngle, 2);
		Point coords = angleShift.coords;
		Area mapReference = currentMap;
		switch (angleShift) {
		case OUEST:
		case NORD:
			mapReference = previousMap;
		default:
		}
		Point offset = new Point(coords.x * 16 * mapReference.getDim_x(),
				coords.y * 16 * mapReference.getDim_y());
		previousMap.setOffset(offset);

		// And shift all entities (except Zildo) with same offset
		EngineZildo.spriteManagement.translateEntitiesWithoutZildo(offset);
	}

	private int normalizeX(int x) {
		if (x < 0) {
			return 0;
		}
		if (x > currentMap.getDim_x() * 16 - 1) {
			return currentMap.getDim_x() * 16 - 1;
		}
		return x;
	}

	private int normalizeY(int y) {
		if (y < 0) {
			return 0;
		}
		if (y > currentMap.getDim_y() * 16 - 1) {
			return currentMap.getDim_y() * 16 - 1;
		}
		return y;
	}

	/**
	 * Create a range inside the map, according to current dimensions.
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return Zone
	 */
	public Zone range(int x1, int y1, int x2, int y2) {
		return new Zone(normalizeX(x1), normalizeY(y1), normalizeX(x2),
				normalizeY(y2));
	}

	public Zone range(float x1, float y1, float x2, float y2) {
		return new Zone((int) x1, (int) y1, (int) x2, (int) y2);
	}

	// Hill boundaries
	private final IntSet leftIncreaseZ = new IntSet(100, 106, 107); // 17, 15,
																	// 841, 15);
	private final IntSet rightDecreaseZ = new IntSet(102, 103, 104); // 25, 9,
																		// 842,
																		// 9);

	/**
	 * Update map with setting the altitude (z attribute)
	 */
	public void analyseAltitude() {
		Area map = getCurrentMap();
		int sizeX = map.getDim_x();
		int sizeY = map.getDim_y();
		int i, j;
		Case c;

		int[][] tabZ = new int[sizeY][sizeX];
		for (j = 0; j < sizeY; j++) {
			for (i = 0; i < sizeX; i++) {
				tabZ[j][i] = 0;
			}
		}

		// Read left to right
		for (j = 0; j < sizeY; j++) {
			c = map.get_mapcase(0, j + 4);
			if (c != null) {
				int currentZ = c.getZ();
				for (i = 0; i < sizeX; i++) {
					c = map.get_mapcase(i, j + 4);
					int onmap = map.readmap(i, j);
					if (leftIncreaseZ.contains(onmap)) {
						currentZ++;
					}
					tabZ[j][i] = currentZ;
					if (rightDecreaseZ.contains(onmap)) {
						currentZ--;
					}
				}
			}
		}

		// Set the altitude of each case
		for (j = 0; j < sizeY; j++) {
			// calculate the minimal altitude for this line
			int min = tabZ[j][0];
			for (i = 0; i < sizeX; i++) {
				if (tabZ[j][i] < min) {
					min = tabZ[j][i];
				}
			}

			for (i = 0; i < sizeX; i++) {
				int currentZ = tabZ[j][i];
				currentZ -= min; // correct altitude with threshold
				c = map.get_mapcase(i, j + 4);
				if (c != null) {
					c.setZ(currentZ);
				}
			}
		}
	}

	/**
	 * Return a respawn position, at an empty place.
	 * 
	 * @return Point
	 */
	public Point getRespawnPosition() {
		List<Point> points = new ArrayList<Point>();
		if (currentMap == null) {
			//points.add(new Point(16*26, 45*16));
			points.add(new Point(231+450-200, 360+130-50-150 +250));	// 231+450 is good for preintro
		} else {
			points = currentMap.getRespawnPoints();
		}

		int n = (int) (points.size() * Math.random());
		//EngineZildo.spriteManagement.spawnSprite(new ElementStars(
		//		StarKind.TRAIL, 150, 360));

		Point p = new Point(points.get(n));
		while (collide(p.x, p.y, null)) {
			p.x += 16;
		}
		//p=new Point(900, 340);
		return p;
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

	public void notifiyScrollOver() {
		previousMap = null;
	}
	
	public Area getPreviousMap() {
		return previousMap;
	}
	
	public TriggerElement getCurrentMapTrigger() {
		return currentMapTrigger;
	}

}