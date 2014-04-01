/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
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

package zildo.server;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import zildo.Zildo;
import zildo.client.ClientEngineZildo;
import zildo.client.sound.Ambient.Atmosphere;
import zildo.client.sound.Ambient.Weather;
import zildo.client.sound.BankMusic;
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
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.desc.EntityType;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.Perso.PersoInfo;
import zildo.monde.sprites.persos.PersoZildo;
import zildo.monde.sprites.persos.ia.mover.BasicMoveOrder;
import zildo.monde.sprites.utils.MouvementPerso;
import zildo.monde.sprites.utils.MouvementZildo;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
import zildo.monde.util.Vector3f;
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
	
	// Keep the location/angle Zildo had when entering on this map
	Angle startAngle;
	Point startLocation;
	
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

		EngineZildo.scriptManagement.clearUnlockingScripts();
		
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// charge_map
	// /////////////////////////////////////////////////////////////////////////////////////
	// IN:filename to load as a map
	// /////////////////////////////////////////////////////////////////////////////////////
	// Destroy any data referring to current map, and given one
	// /////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Destroy any data referring to current map, and load the given one.
	 * @param p_mapname new map name to load (with or without MAP extension)
	 * @param p_additive TRUE=load a map without removing the current one (for scrolling)
	 */
	public void loadMap(String p_mapname, boolean p_additive) {
		// Remove previous map
		if (p_additive) {
			previousMap = currentMap; // Keep previous map for the scrolling
										// phasis
		} else {
			if (currentMap != null) {
				deleteCurrentMap();
			}
			previousMap = null;
		}

		// Adjust map according the quest diary
		String adjustedMapName = EngineZildo.scriptManagement
				.getReplacedMapName(p_mapname);
		
		// Trigger the location (only on non-additive mode: additive means that we're preparing a scrolling)
		// In a scrolling mode, triggering will be effective once map has completely scrolled.
		if (!EngineZildo.game.editing && !p_additive) {
			currentMapTrigger = TriggerElement
					.createLocationTrigger(adjustedMapName, null, null, -1);
			EngineZildo.scriptManagement.trigger(currentMapTrigger);
			EngineZildo.scriptManagement.prepareMapSubTriggers(p_mapname);
		}

		// Load a new one
		currentMap = loadMapFile(adjustedMapName, p_mapname);
		// Do the map replacements
		Atmosphere atmo = currentMap.getAtmosphere();
		Weather weather = ClientEngineZildo.ambient.getWeather(currentMap);
		EngineZildo.scriptManagement.execMapScript(adjustedMapName, atmo, p_additive);
		
		EngineZildo.spriteManagement.initForNewMap();

		if (!EngineZildo.game.editing) {
			if (!EngineZildo.soundManagement.isForceMusic()) {
				BankMusic mus = BankMusic.forName(EngineZildo.scriptManagement.getReplacedZikName(atmo.music.name()));
				
				ClientEngineZildo.soundPlay.playMusic(mus, atmo);
			}
			if (atmo == Atmosphere.CAVE) {
				// Inside cave, we want full light
				ClientEngineZildo.ortho.setFilteredColor(new Vector3f(1, 1, 1));
			}
			switch (weather) {
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
		//System.out.println("Loading " + p_mapname + "("+p_refMapname+")");
		
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
		return collide(Math.round(tx), Math.round(ty), quelElement);
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

		//if (true) return false;
		// Is it a ghost ?
		boolean ghost = false;
		Perso p = quelElement != null && quelElement.getEntityType().isPerso() ? (Perso) quelElement : null;
		if (p != null && p.isUnstoppable()) {
			return false;
		}
		ghost = p !=null && p.isGhost();
		
		if (currentMap.isOutside(tx, ty)) {
			// Detect element out of the map, except for 3 cases : Zildo, ghosts and single pleyer
			if (quelElement != null && !quelElement.isOutsidemapAllowed()) {
				return !ghost && (!quelElement.isZildo() || EngineZildo.game.multiPlayer);
			}
		}
		Point size = new Point(8, 4); // Default size
		if (quelElement != null && quelElement.getCollision() != null) {
			Point elemSize = quelElement.getCollision().size;
			if (elemSize != null) {
				size = elemSize;
			}
		}
		if (quelElement != null && quelElement.flying
				&& quelElement.getAngle() != null) {
			if (EntityType.PERSO != quelElement.getEntityType()) {
				// Flying object
				Angle angleFlying = quelElement.getAngle();
	
				ty -= quelElement.z;
				int cx = (tx / 16);
				int cy = (ty / 16);
				int caseZ = currentMap.readAltitude(cx, cy);
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
	
				if (currentMap.isCaseBottomLess(cx, cy)) {
					return false;
				}
				Tile tile = currentMap.readmap(cx, cy, false);
				if (tile == null) {
					return false;
				}
				if (tileCollision.collide(modx, mody, tile)) {
					return true;
				}
				return EngineZildo.spriteManagement.collideSprite(tx, ty,
						quelElement);
			} else {
				// Flying perso : HEN, DUCK, FISH
				int cx = (tx / 16);
				int cy = (ty / 16);
				modx = tx % 16;
				mody = ty % 16;
				Tile tile = currentMap.readmap(cx, cy, false);
				if (tile == null) {
					return false;
				}
				on_map = tile.getValue();
				if (tileCollision.collide(modx, mody, tile)) {
					IntSet waterBank = new IntSet(154, 155, 156, 157, 158, 159, 188, 189, 190, 191, 192, 193);
					if (waterBank.contains(on_map - 256*2)) {
						// Water bank => is it above ?
						if (quelElement.z > 12) {
							return false;
						}
					}
					return true;
				}
				return false;
			}
		}

		// Check collision on tile level
		boolean collideTile = collideTile(tx, ty, ghost, size, quelElement);

		if (collideTile) {
			return true;
		}
		
		// Collision with characters
		Perso perso = EngineZildo.persoManagement.collidePerso(tx, ty, quelElement);
		if (perso != null) {
			if (p != null) {
				// If zildo crosses an enemy, this is not a collision, but a wound ! (except if he's blinking)
				if (p.isZildo() && perso.getInfo() == PersoInfo.ENEMY && p.getCompte_dialogue() == 0) {
					return false;
				}
				if (p.getInfo() == PersoInfo.ENEMY && perso.isZildo() && perso.getCompte_dialogue() == 0) {
					return false;
				}
				
				if (perso.getQuel_deplacement() == MouvementPerso.FOLLOW && perso.getFollowing() == quelElement) {
					return false;
				}
			}
			return true;
		}
		
		// Collision with sprites
		if (EngineZildo.spriteManagement.collideSprite(tx, ty, quelElement))
			return true;

		// Returns computed result
		return false;
	}

	private final static IntSet particularTiles = 
		new IntSet(256 + 22, 256+23)
		.addRange(256*3 + 89, 256*3 + 96);

	final static Point[] tabPointRef = new Point[] {
		new Point(-1, -1), new Point(-1, 1),
		new Point(1, -1),  new Point(1, 1)
	};
		
	// Wider grid to check collision (for large objects)
	final static Point[] tabGridRef = new Point[] {
		new Point(-1, -1), new Point(0, -1), new Point(1, -1),
		new Point(-1, 0), new Point(0, 0), new Point(1, 0),
		new Point(-1, 1), new Point(0, 1), new Point(1, 1)
	};

	public boolean collideTile(int tx, int ty, boolean ghost, Point size, Element quelElement) {
		int mx, my; // Position map
		int on_map;
		int modx, mody;
		
		// Check on back or fore ground, depending on the character we're checking
		boolean foreground = quelElement != null && quelElement.isForeground();

		// Check 4 corners of a 4x4 sized square
		Point[] ref = tabPointRef;
		if (size != null && Math.max(size.x, size.y) > 12) {
			ref = tabGridRef;
		}
		for (Point pt : ref) {
			mx = (tx + (size.x / 2) * pt.x);
			my = (ty + (size.y / 2) * pt.y);

			if (currentMap.isOutside(mx, my)) {
				// Avoid collision on the map's borders
				boolean coll = quelElement != null && !quelElement.isOutsidemapAllowed() && !quelElement.isZildo() && !ghost;
				if (coll) {
					return true;
				} else {
					continue;
				}
			}

			int scaledX = mx / 16;
			int scaledY = my / 16;
			// Don't collide if case is bottom less (example: lava tile)
			if (currentMap.isCaseBottomLess(scaledX, scaledY)) {
				continue;
			}
			Case mapCase = currentMap.get_mapcase(scaledX, scaledY);
			if (mapCase == null) {
				continue;
			}
			Tile tile = foreground ? mapCase.getForeTile() : mapCase.getBackTile();
			if (tile == null) {
				continue;
			}
			Tile tileBack2 = mapCase.getBackTile2();

			on_map = tile.getValue();
			modx = mx % 16;
			mody = my % 16;

			// Sum each layer of collision : Back, then Back2 (except for ladder !)
			if (tileBack2 != null && tileCollision.collide(modx, mody, tileBack2)) {
				return true;
			} else {
				int back2val = tileBack2 != null ? tileBack2.getValue() : 0;
				// Very bad !!! HARD-CODED tile values ! 
				// If ladder or bridge, we allow not to check collision on back tile
				boolean isLadder = back2val == 206 || back2val == 207;
				isLadder |= back2val == 207+256*5 || back2val == 208+256*5 || back2val == 209+256*5;
				if (!isLadder && tileCollision.collide(modx, mody, tile)) {
					return true;
				}
				// Special case : impassable for NPC, but right for hero
				if (particularTiles.contains(on_map)) {
					// Okay => check if given element is a character which is allowed
					// to pass particular tiles
					if (quelElement != null && quelElement.getEntityType() == EntityType.PERSO) {
						Perso perso = (Perso) quelElement;
						if (!perso.isOpen()) {
							return true;
						}
					}
				}
				
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
		case 512+160:
		case 512+161:
			result = angle;	// Bed
		break;
		default:
			// Saut latéral}
			switch (angle) {
			case NORD:
				onMap = area.readmap(cx, cy - 1);
				if (onMap == 21 || onMap == 3 || onMap == 839 || onMap == 19 || onMap == 18 || onMap == 20 || onMap == 0 || onMap == 7 ||
						onMap == 256 * 3 + 135)	// Cave grey wall
					result = angle;
				break;
			case EST:
				onMap = area.readmap(cx + 1, cy);
				if (onMap == 25 || onMap == 9 || onMap == 842 || onMap == 134
						|| onMapCurrent == 9 ||
						onMap == 22 || onMap == 23 || onMap == 24)
					result = angle;
				break;
			case SUD:
				onMap = area.readmap(cx, cy + 1);
				if (onMap == 32 || onMap == 31 || onMap == 3*256 + 76 || onMap == 3*256 + 5 ||
						onMap == 256 * 3 + 129) // Cave grey wall
					result = angle;
				break;
			case OUEST:
				onMap = area.readmap(cx - 1, cy);
				if (onMap == 17 || onMap == 15 || onMap == 841 || onMap == 19 || onMap == 18 || onMap == 20 || onMap == 0 || onMap == 7
						|| onMapCurrent == 15 ||
						// Water border
						onMap == 135)
					result = angle;
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
		if (currentMap != null) {
			// Determine angle, according to previous location
			Angle ang = p_zildo.getAngle();
			if (p_zildo.isProjected()) {
				ang = Angle.rotate(ang, 2);	// Zildo is stepping back, so inverse his angle
			}
			changingMapPoint = currentMap.isChangingMap(x, y, ang);
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
			mapScrollAngle = p_changingMapPoint.getComingAngle().opposite();
			zildo.setAngle(mapScrollAngle);
			int orderX = p_changingMapPoint.getOrderX();
			int orderY = p_changingMapPoint.getOrderY();
			zildo.resetPosAvantSaut();	// Reset location before jump (nonsense on a new map)
			
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
				if (!zildo.isOnPlatform()) {
					Point dest = new Point(zildo.x, zildo.y);
					zildo.y = (int) zildo.y;
					if (zildo.y > previousDimY * 16 - 16) {
						zildo.setY(8 - 8);
						dest.y = (int) zildo.y + 8;
					} else if (zildo.y < 4) {
						zildo.setY(currentMap.getDim_y() * 16 - 8 + 8);
						dest.y = (int) zildo.y - 8;
					} else if (zildo.x < 4) {
						zildo.setX(currentMap.getDim_x() * 16 - 16 + 16);
						dest.x = (int) zildo.x - 16;
					} else if (zildo.x > previousDimX * 16 - 16) {
						zildo.setX(8 - 16);
						dest.x = (int) zildo.x + 16;
					}
					// Translate everyone but Zildo because we just did it
					shiftPreviousMap(mapScrollAngle, false);
					zildo.setTarget(dest);
				} else {
					// Translate everybody to the new map reference
					shiftPreviousMap(mapScrollAngle, true);
					// Move the platform accordingly
					Point dest = mapScrollAngle.coords.multiply(16);
					for (SpriteEntity entity : EngineZildo.spriteManagement.getWalkableEntities()) {
						if (entity.getMover().isOnIt(zildo)) {
							entity.setMover(new BasicMoveOrder((int) (entity.x + dest.x), 
																(int) (entity.y + dest.y), 1f));
							entity.setGhost(true);
							Element placeHolder = entity.getMover().getPlaceHolder();
							placeHolder.vx=0;
							placeHolder.vy=0;
						}
					}
				}
				zildo.setGhost(true);
			} else {
				zildo.setX(chPointTarget.getPx() * 8 + 16);
				zildo.setY(chPointTarget.getPy() * 8 + 8);
				float zx = zildo.getX();
				float zy = zildo.getY();
				if( chPointTarget.isSingle()) {
					zx-=8;
				} else if (chPointTarget.isVertical()) {
					// Vertical chaining point
					zildo.setX(zx - 8);
					zildo.setY(zy + 8);
				}
				Angle a = chPointTarget.getComingAngle();
				if (a == Angle.NULL) {
					a = Angle.SUD;	// Special angle => Zildo just fell
				} else {
					int movedX = (int) zx;
					int movedY = (int) zy;
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
					zildo.setX(movedX);
					zildo.setY(movedY);
				}
				changingMapPoint = chPointTarget;
				zildo.setAngle(a);
				if (zildo.getEn_bras() != null) {
					zildo.getEn_bras().dying = true;
					zildo.setMouvement(MouvementZildo.VIDE);
					zildo.setEn_bras(null); // Loose his object
				}
				zildo.walkTile(false);
			}
			zildo.finaliseComportement(EngineZildo.compteur_animation);

			// Save starting location
			startAngle = zildo.getAngle();
			setStartLocation(new Point(zildo.x, zildo.y));
			if (zildo.getTarget() != null) {
				setStartLocation(zildo.getTarget());
			}
			// If hero is along a border, the game will be backed up when scroll will be over
			if (!isAlongBorder && EngineZildo.scriptManagement.isAllowedToSave()) {
				EngineZildo.backUpGame();	// Save an automatic backup game to restore if hero dies
			}

			postLoadMap(isAlongBorder);
			
			EngineZildo.spriteManagement.notifyLoadingMap(false);
		}
		return false;
	}

	/**
	 * Do some post-initialization:<ul>
	 * <li>Clear the way around Zildo (open door for example)</li>
	 * <li>Init Zildo's followers location and behavior</li></ul>
	 */
	public void postLoadMap(boolean p_scroll) {
		PersoZildo zildo = EngineZildo.persoManagement.getZildo();

		// Someone following Zildo ?
		Perso follower = EngineZildo.persoManagement.getFollower(zildo);
		if (follower != null) {
			if (p_scroll) {	// If map is scrolling, moves smoothly the character
				follower.setTarget(new Point(zildo.x, zildo.y));
			} else {
				follower.setX(zildo.x);
				follower.setY(zildo.y);
			}
			follower.askVisible(true);
			follower.setAngle(zildo.getAngle());
		}
		// Adjust map at Zildo's location
		if (zildo != null) {
			zildo.walkTile(false);
		}	
	}
	
	/**
	 * Shift the previous map to the right position, in order to have it
	 * sticked to the new one. So we calculate the shift coordinates.
	 * @param p_mapScrollAngle
	 * @param p_translateZildo TRUE=Zildo will be shifted too
	 */
	private void shiftPreviousMap(Angle p_mapScrollAngle, boolean p_translateZildo) {
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
		EngineZildo.spriteManagement.translateEntities(offset, p_translateZildo);
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
	private final static IntSet leftIncreaseZ = new IntSet(100, 106, 107, 
															16, 17, 18, 20,
															81, 83, 86,
															93, 95); // 17, 15,
																	// 841, 15);
	private final static IntSet rightDecreaseZ = new IntSet(102, 103, 104,
															24, 25, 26,
															80, 82, 85,
															92, 94); // 25, 9,
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
			c = map.get_mapcase(0, j);
			if (c != null) {
				int currentZ = c.getZ();
				for (i = 0; i < sizeX; i++) {
					c = map.get_mapcase(i, j);
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
				c = map.get_mapcase(i, j);
				if (c != null) {
					c.setZ(currentZ);
				}
			}
		}
		
		// Second pass : (the only one which will survive after McFlac implementation)
		IntSet waterTank = new IntSet().addRange(188, 193).addRange(154, 159);
		for (j = 0; j < sizeY; j++) {
			for (i = 0; i < sizeX; i++) {
				int value = map.readmap(i, j) - 256*2;
				if (waterTank.contains(value)) {
					// Found some lowered case (water tank)
					c = map.get_mapcase(i, j);
					c.setZ(c.getZ() - 8);
				}
			}
		}
	}

	/**
	 * Respawn Zildo to his starting location in the current area.
	 */
	public void respawn(boolean relocate, int damage) {
		PersoZildo zildo = EngineZildo.persoManagement.getZildo();
		if (relocate) {
			zildo.setX(startLocation.x);
			zildo.setY(startLocation.y);
			zildo.setAngle(startAngle);
			zildo.beingWounded(null, damage);
			zildo.stopBeingWounded();
		}
		zildo.setForeground(false);
		zildo.askVisible(true);	// Set him back to visible
	}
	
	/**
	 * Return a respawn position, at an empty place.
	 * 
	 * @return Point
	 */
	public Point getRespawnPosition() {
		List<Point> points = new ArrayList<Point>();
		if (currentMap == null) {
			//points.add(new Point(231+450-220+30-20, 360+130-50-150 +250));	// for coucou
			points.add(new Point(231+450+30-20, 360+130-50-150 +250));	// for foretg2
			//points.add(new Point(231+450, 360+130-50-150 +250+50));	// 231+450 is good for preintro
		} else {
			points = currentMap.getRespawnPoints();
		}

		int n = (int) (points.size() * Math.random());
		//EngineZildo.spriteManagement.spawnSprite(new ElementStars(
		//		StarKind.TRAIL, 150, 360));

		points.add(new Point(231+450+30-20, 360+130-50-150 +250));	// for foretg2
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

	public void setStartLocation(Point loc) {
		startLocation = new Point(loc);
	}
	
	public void setStartAngle(Angle a) {
		startAngle = a;
	}
	
	public Point getStartLocation() {
		return startLocation;
	}
	
	public Angle getStartAngle() {
		return startAngle;
	}

	public void notifiyScrollOver() {
		previousMap = null;
		if (!EngineZildo.game.editing) {
			// Adjust map according the quest diary
			String name = currentMap.getName();
			String adjustedMapName = EngineZildo.scriptManagement.getReplacedMapName(name);
			currentMapTrigger = TriggerElement.createLocationTrigger(adjustedMapName, null, null, -1);
			EngineZildo.scriptManagement.trigger(currentMapTrigger);
			EngineZildo.scriptManagement.prepareMapSubTriggers(name);
		}

	}
	
	public Area getPreviousMap() {
		return previousMap;
	}
	
	public TriggerElement getCurrentMapTrigger() {
		return currentMapTrigger;
	}

}