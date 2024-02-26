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

package zildo.monde.map;

import static zildo.server.EngineZildo.hasard;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import zildo.Zildo;
import zildo.client.sound.Ambient.Atmosphere;
import zildo.client.sound.BankSound;
import zildo.fwk.bank.SpriteBank;
import zildo.fwk.collection.IntSet;
import zildo.fwk.file.EasyBuffering;
import zildo.fwk.file.EasySerializable;
import zildo.fwk.script.xml.element.TriggerElement;
import zildo.monde.dialog.Behavior;
import zildo.monde.dialog.MapDialog;
import zildo.monde.items.ItemKind;
import zildo.monde.map.Case.TileLevel;
import zildo.monde.map.Tile.TileNature;
import zildo.monde.sprites.Reverse;
import zildo.monde.sprites.Rotation;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.desc.EntityType;
import zildo.monde.sprites.desc.GearDescription;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.desc.SpriteAnimation;
import zildo.monde.sprites.desc.SpriteDescription;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.elements.ElementImpact;
import zildo.monde.sprites.elements.ElementImpact.ImpactKind;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.Perso.PersoInfo;
import zildo.monde.sprites.persos.PersoNJ;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.sprites.utils.MouvementPerso;
import zildo.monde.sprites.utils.MouvementZildo;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
import zildo.monde.util.Zone;
import zildo.resource.Constantes;
import zildo.server.EngineZildo;
import zildo.server.SpriteManagement;

/**
 * Class modelizing a map where the hero can play.
 * 
 * @author Tchegito
 * 
 */
public class Area implements EasySerializable {

	class SpawningTile {
		Case previousCase;
		int x, y;
		int cnt;
		String awaitedQuest;	// Name of the awaited quest to be done
		boolean fog;	// display a fog during the respawn
		int floor;
	}

	final static int TILE_VIEWPORT_X = (Zildo.viewPortX / 16);// + 1;
	final static int TILE_VIEWPORT_Y = (Zildo.viewPortY / 16);// + 1;
	
	public static final byte MIDDLE_FLOOR = 1;	// Default floor
	
	final static int DEFAULT_SPAWNING_TIME = 5000;	// Number of frames until the tile respawns
	
	// For roundAndRange
	static public int ROUND_X = 0;
	static public int ROUND_Y = 0;

	static public int lineSize = 128; // Max-size of a map's line

	private Point offset; // For scrolling map

	private byte lowestFloor = 1;
	private byte highestFloor = 1;	// Default (but should be read from the map)
	
	private int dim_x, dim_y;
	private String name;
	private Case[][][] mapdata;	// Floor x ordinate x abscissa
	private List<ChainingPoint> listChainingPoint;
	private MapDialog dialogs;
	private Point scrollOffset;	// Set in ZEditor to place the map when in-game
	private Point originalDim;	// In case scrollOffset is filled, we will change dimension, so keep it the original

	private Atmosphere atmosphere;

	// Elements linked to a given case (into chest, bushes, jar ...)
	private Map<Integer, CaseItem> caseItem;

	// To diffuse changes to clients
	private final Collection<Point> changes;
	// To respawn removed items
	private final Collection<SpawningTile> toRespawn;
	// Respawn points for Zildo (multiplayer only)
	private final List<Point> respawnPoints;

	Map<Integer, Point> walkedSlabs;	// Character ID => coordinates of the pushed slab
	
	private Point alertLocation;	// Sound able to alert enemies
	private int alertDuration = 0;
	
	public List<Point> getRespawnPoints() {
		return respawnPoints;
	}

	public Area() {
		this(64, 64);
	}
	
	public Area(int dimX, int dimY) {
		mapdata = new Case[Constantes.TILEENGINE_FLOOR][Constantes.TILEENGINE_HEIGHT][Constantes.TILEENGINE_HEIGHT];
		listChainingPoint = new ArrayList<ChainingPoint>();

		changes = new HashSet<Point>();
		toRespawn = new HashSet<SpawningTile>();

		caseItem = new HashMap<Integer, CaseItem>();
		respawnPoints = new ArrayList<Point>();

		offset = new Point(0, 0);
		scrollOffset = new Point(0, 0);
		originalDim = new Point(dimX, dimY);
		dim_x = dimX;
		dim_y = dimY;
		
		walkedSlabs = new HashMap<>();
	}

	public Area(Atmosphere p_atmo) {
		this(64, 64);
		int empty = p_atmo.getEmptyTile();
		for (int i = 0; i < dim_x * dim_y; i++) {
			int x = i % dim_x;
			int y = i / dim_x;
			writemap(x, y, empty);
		}
		atmosphere = p_atmo;
		dialogs = new MapDialog();
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// get_Areacase
	// /////////////////////////////////////////////////////////////////////////////////////
	// IN : coordinates
	// OUT: Case object at the given coordinates
	// /////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Return {@link Case} object at given map coordinates.<br/>
	 * In this method, floor isn't provided, so we select the highest floor.
	 */
	public Case get_mapcase(int x, int y) {
		// TODO: a method without overflow tests could be used sometimes to enhance performance
		if (x < 0 || x >= dim_x) {
			return null;
		}
		if (y < 0 || y >= dim_y) {
			return null;
		}
		byte floor = highestFloor;
		Case c = null;
		while (floor>=0) {
			c = mapdata[floor][y][x];
			if (c != null) break;
			floor--;
		}
		return c;
	}

	public int getHighestCaseFloor(int x, int y) {
		int value = -1;
		if (x >= 0 && x < dim_x
		 && y >= 0 && y < dim_y) {
			value = highestFloor;
			while (value>=0) {
				if (mapdata[value][y][x] != null) break;
				value--;
			}
		}
		return value;
	}
	
	public Case get_mapcase(int x, int y, int floor) {
		if (x < 0 || x >= dim_x) {
			return null;
		}
		if (y < 0 || y >= dim_y) {
			return null;
		}
		return getMapcaseWithoutOverflow(x, y, floor);
	}
	
	/** Method without overflow test, to gain some performance **/
	public Case getMapcaseWithoutOverflow(int x, int y, int floor) {
		return mapdata[floor][y][x];
	}
	
	/** Set a {@link Case} object in the map, without specifying floor. Default is MIDDLE_FLOOR.**/
	public void set_mapcase(int x, int y, Case c) {
		mapdata[highestFloor][y][x] = c;
	}
	
	public void set_mapcase(int x, int y, byte floor, Case c) {
		mapdata[floor][y][x] = c;
		if (floor > highestFloor) {
			highestFloor = floor;
		} else if (floor < lowestFloor) {
			lowestFloor = floor;
		}
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// readmap
	// /////////////////////////////////////////////////////////////////////////////////////
	// IN : coordinates on Area
	// foreground: FALSE=on the floor TRUE=foreground
	// OUT: return motif + bank*256
	// /////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Basically, return the higher tile from given map coordinates, with a little bit of intelligence.<ol>
	 * <li>if 'foreground' is asked, and if case has a foreground tile => return it</li>
	 * <li>if tile has a back2, return it</li>
	 * <li>else return back tile</li>
	 * </ol>
	 * @param x
	 * @param y
	 * @param p_foreground TRUE=> returns the foreground tile, if it exists.
	 * @return Tile
	 */
	public Tile readmap(int x, int y, boolean p_foreground) {
		return readmap(x, y, p_foreground, -1);
	}
	
	public Tile readForeTile(int x, int y) {
		Case c = get_mapcase(x, y);
		if (c != null) {
			return c.getForeTile();
		}
		return null;
	}

	public Tile readmap(int x, int y, boolean p_foreground, int floor) {
		Case temp;
		if (floor == -1) {	// No floor, so we take the highest
			temp = get_mapcase(x, y);
		} else {
			temp = get_mapcase(x, y, floor);
		}
		if (temp == null) {
			return null;
		}

		// Is there two layers on this tile ?
		boolean masked = temp.getForeTile() != null;

		if (p_foreground && masked) {
			return temp.getForeTile();
		} else {
			if (temp.getBackTile2() != null) {
				return temp.getBackTile2();
			} else {
				return temp.getBackTile();
			}
		}
	}
	
	/**
	 * Returns TRUE if case part pointed by PIXEL coordinates is in water.
	 */
	public boolean isInWater(int cx, int cy) {
		// TODO: for now, we only check the global case, but will be more accurate later
		int val = readmap(cx / 16, cy / 16);
		return val == 256*2 + 255;
	}
	
	final IntSet waterBank = new IntSet(154, 156, 188, 189, 190, 255);
	final IntSet waterDeep = new IntSet().addRange(108, 138).addRange(208, 222)
			.addRange(224, 228).addRange(230, 245).addRange(247, 253);
	final IntSet waterFountain = new IntSet().addRange(105, 106);

	// TODO: test one day if a precalculated array will be faster than this long 'if' process
	public TileNature getCaseNature(int xx, int yy, int floor) {
		int x = xx / 16;
		int y = yy / 16;
		Case temp;
		if (floor != -1) {
			temp = this.get_mapcase(x, y, floor);
		} else {
			temp = this.get_mapcase(x, y);
		}
		if (temp == null) {
			return null;
		}
		
		// 1: bottom less (we have to read the BACK tile
		int val = temp.getBackTile().getValue();
		Tile back2 = temp.getBackTile2();
		int back2Val = back2 == null ? -1 : back2.getValue();
		if (Tile.isBottomFloor(val)) {
			return TileNature.BOTTOMFLOOR;
		}
		if (Tile.isBottomLess(val, back2Val)) {
			return TileNature.BOTTOMLESS;
		}
		if (Tile.isBottomJump(val)) {
			return TileNature.BOTTOMJUMP;
		}
		if (val == Tile.T_BUSH) {
			return TileNature.BUSH;
		} else if (val == Tile.T_WATER_FEW) {
			return TileNature.WATER_MUD;
		} else if (val == Tile.T_SWAMP && back2 == null) {
			return TileNature.SWAMP;
		} else if (val == Tile.T_WATER_MUD) {
			// Make double check with following 'if' clause
		} else {
			// 2: water (could be on back or back2)
			val = back2Val == -1 ? val : back2Val;
		}
		
		if (val == Tile.T_WATER_MUD) {
			// Double check for water mud : it doesn't cover the whole tile
			int mx = xx % 16;
			int my = yy % 16;
			if (TileCollision.getInstance().getBottomZ(mx, my, Tile.T_WATER_MUD, false) <0)
				return TileNature.WATER_MUD;
		}
		if ( waterBank.contains(val - 256*2) || waterDeep.contains(val)) {
			return TileNature.WATER;
		}
		if (waterFountain.contains(val-256)) {
			int mx = xx % 16;
			int my = yy % 16;
			if (my > 7) {
				if ((val == (256+105) && mx > 7) || (val == 256+106 && mx < 8)) {
					return TileNature.WATER;
				}
			}
		}
		return TileNature.REGULAR;
		
	}
	public TileNature getCaseNature(int xx, int yy) {
		return getCaseNature(xx, yy, -1);
	}

	/**
	 * Returns TRUE if case is bottom less (example: lava or void)
	 */
	public boolean isCaseBottomLess(int x, int y, int floor) {
		Case temp;
		if (floor != -1) {
			temp = this.get_mapcase(x, y, floor);
		} else {
			temp = get_mapcase(x, y);
		}
		// Added because in dragon cave, we display tiles on lower floor, between dragon and edge
		if (temp == null && floor < highestFloor && floor > 0) {
			temp = get_mapcase(x, y, floor - 1);
		}
		if (temp == null) {
			return false;
		}
		int val = temp.getBackTile().getValue();
		// As a temporary feature : 108 means water coast, to allow smarter 'ponton' collision
		if (Tile.isBottomLess(val, -1) || val == 108) {
			return true;
		}
		return false;
	}

	// Return n_motif + n_banque*256 from a given position on the Area
	public int readmap(int x, int y) {
		Tile tile = readmap(x, y, false);
		if (tile == null) {
			return -1;
		} else {
			return tile.getValue();
		}
	}

	public int readAltitude(int x, int y) {
		Case temp = this.get_mapcase(x, y);
		if (temp == null) {
			return 0;
		}
		return temp.getZ();
	}

	public void writemap(int x, int y, int quoi, TileLevel level) {
		writemap(x, y, quoi, level, Rotation.NOTHING);
	}
	
	/**
	 * writemap
	 * @param x,y: coordinates
	 * @param quoi: value (bank*256 + index)
	 * @param back: TRUE means back tile
	 * @param back2: TRUE means 
	 */
	public void writemap(int x, int y, int quoi, TileLevel level, Rotation rot) {
		Case temp = this.get_mapcase(x, y);
		if (temp == null) {
			temp = new Case();
			set_mapcase(x, y, temp);
		} else {
			temp.setModified(true);
		}
		Tile tile;
		if (quoi == -1) {
			switch (level) {
			case BACK:
				temp.setBackTile(null);
				break;
			case BACK2:
				temp.setBackTile2(null);
				break;
			case FORE:
				temp.setForeTile(null);
				break;
			}
		} else {
			switch (level) {
			case BACK:
				default:
				tile = temp.getBackTile();
				break;
			case BACK2:
				tile = temp.getBackTile2();
				if (tile == null) {
					tile = new Tile(0, temp);
					temp.setBackTile2(tile);
				}
				break;
			case FORE:
				tile = temp.getForeTile();
				if (tile == null) {
					tile = new Tile(0, temp);
					temp.setForeTile(tile);
				}
				break;
			}
			tile.set(quoi, rot, Reverse.NOTHING);
		}
		changes.add(new Point(x, y));
	}

	public void writemap(int x, int y, int quoi) {
		writemap(x, y, quoi, TileLevel.BACK, Rotation.NOTHING);
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// roundAndRange
	// /////////////////////////////////////////////////////////////////////////////////////
	// IN:float to round and range, indicator on which coordinate to compute
	// ROUND_X(default) -. x , ROUND_Y -. y
	// /////////////////////////////////////////////////////////////////////////////////////
	// Trunc a float, and get it into the Area, with limits considerations.
	// /////////////////////////////////////////////////////////////////////////////////////
	public int roundAndRange(float x, int whatToRound) {
		int result = (int) x;
		if (x < 0) {
			x = 0;
		}
		int max = dim_x;
		if (whatToRound == ROUND_Y) {
			max = dim_y;
		}
		if (x > (max * 16 - 16)) {
			x = max * 16 - 16;
		}

		return result;
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// isAlongBorder
	// /////////////////////////////////////////////////////////////////////////////////////
	public boolean isAlongBorder(int x, int y) {
		return (x < 4 || x > dim_x * 16 - 8 || y < 4 || y > dim_y * 16 - 4);
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// isChangingArea
	// /////////////////////////////////////////////////////////////////////////////////////
	// IN : x,y (pixel coordinates for perso location)
	// /////////////////////////////////////////////////////////////////////////////////////
	// Return ChainingPoint if Zildo's crossing one (door, or Area's border)
	// /////////////////////////////////////////////////////////////////////////////////////
	public ChainingPoint isChangingMap(float x, float y, Angle p_angle, int fromFloor) {
		// On parcourt les points d'enchainements
		int ax = (int) (x / 8);
		int ay = (int) (y / 8);
		boolean border;
		List<ChainingPoint> candidates = new ArrayList<ChainingPoint>();
		if (listChainingPoint.size() != 0) {
			for (ChainingPoint chPoint : listChainingPoint) {
				if (chPoint.getComingAngle() == Angle.NULL) {
					continue;	// This point is only a landing position
				}
				// Area's borders
				border = isAlongBorder((int) x, (int) y);
				int chFloor = chPoint.getFloor();	// 255 (or -1 in ZEditor) means on any floor
				if (chPoint.isCollide(ax, ay, border) && (chFloor == 255 || chFloor == fromFloor)) {
					candidates.add(chPoint);
				}
			}
		}
		if (candidates.size() == 1) {
			return candidates.get(0);
		} else if (candidates.size() > 0) {
			// More than one possibility : we must be on a map corner
			for (ChainingPoint ch : candidates) {
				Angle chAngle = ch.getComingAngle().opposite();
				if (chAngle == p_angle) {
					return ch;
				}
			}
			// return first one (default)
			return candidates.get(0);
		}
		return null;
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// addContextInfos
	// /////////////////////////////////////////////////////////////////////////////////////
	// Fill the given ChainingPoint with two extra infos: 'orderX' and 'orderY'
	// /////////////////////////////////////////////////////////////////////////////////////
	void addChainingContextInfos() {
		for (ChainingPoint ch : listChainingPoint) {
			int orderX = 0;
			int orderY = 0;
			// We're gonna get a sort number in each coordinate for all chaining point referring to the same Area.
			for (ChainingPoint chP : listChainingPoint) {
				if (chP.getMapname().equals(ch.getMapname()) ) { 	// Linking to the same map
					if (chP.getPx() <= ch.getPx()) {
						orderX++;
					}
					if (chP.getPy() <= ch.getPy()) {
						orderY++;
					}
				}
			}
			ch.setOrderX(orderX);
			ch.setOrderY(orderY);
		}
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// getTarget
	// /////////////////////////////////////////////////////////////////////////////////////
	// IN : comingArea -. Area's name
	// /////////////////////////////////////////////////////////////////////////////////////
	public ChainingPoint getTarget(String comingArea, int orderX, int orderY) {
		if (listChainingPoint.size() != 0) {
			for (ChainingPoint chPoint : listChainingPoint) {
				if (chPoint.getMapname().equals(comingArea)) {
					if (orderX == 0 && orderY == 0) {
						return chPoint;
					} else {
						// Get the right one, because there is several
						// connections between
						// the two Areas.
						if (chPoint.getOrderX() == orderX && chPoint.getOrderY() == orderY) {
							return chPoint;
						}
					}
				}
			}
		}
		return null;
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// attackTile
	// /////////////////////////////////////////////////////////////////////////////////////
	public void attackTile(int floor, Point tileLocation, Perso perso) {
		// Check if Zildo destroy something on a tile
		int onmap = readmap(tileLocation.x, tileLocation.y);
		int spe = 0;
		switch (onmap) {
		case Tile.T_NETTLE:
			spe = 1;	// Will blow the nettle with different leaf sprite
			// Remember that nettle has been cut (only if nettle quest is started ! thanks Ruben ;))
			if (EngineZildo.persoManagement.getZildo().getCountNettleLeaf() >= 0) {
				EngineZildo.scriptManagement.actOnTile(getName(), tileLocation);
			}
		case Tile.T_BUSH: // Bushes
			Point spriteLocation = new Point(tileLocation.x * 16 + 8, tileLocation.y * 16 + 8);
			EngineZildo.spriteManagement.spawnSpriteGeneric(SpriteAnimation.BUSHES, spriteLocation.x, spriteLocation.y,
					floor, spe, null, null);
			EngineZildo.soundManagement.broadcastSound(BankSound.CasseBuisson, spriteLocation);

			takeSomethingOnTile(tileLocation, true, perso, true);
			break;
		case 374: // Mud
			writemap(tileLocation.x, tileLocation.y, 375);
			break;
		}
		TriggerElement attackTrigger = TriggerElement.createTileAttackTrigger(tileLocation);
		EngineZildo.scriptManagement.trigger(attackTrigger);
	}
	
	/** When the tile receives a fork blow **/
	public void forkTile(Perso attacker,Point pos) {
		int onmap = readmap(pos.x, pos.y);
		ElementDescription desc = null;
		switch (onmap) {
		case Tile.T_BUSH:
			int resultTile = Tile.T_BUSH_CUT;
			writemap(pos.getX(), pos.getY(), resultTile);
			desc = ElementDescription.BUSHES;
			EngineZildo.soundManagement.broadcastSound(BankSound.ZildoRamasse, attacker);

			break;
		case 256+224:	// Bottom (last)
			writemap(pos.getX(), pos.getY(), 256 + 209, TileLevel.BACK2);
			desc = ElementDescription.STRAW;
			break;
		case 256+108:	// Bottom
			writemap(pos.getX(), pos.getY(), -1, TileLevel.BACK2);
			writemap(pos.getX(), pos.getY()+1, 256 + 224, TileLevel.BACK2);
			desc = ElementDescription.STRAW;
			break;
		case 250+256: case 252+256:
			writemap(pos.getX(), pos.getY(), 256 + 108, TileLevel.BACK2);
		case 253+256:
			desc = ElementDescription.STRAW;
			break;
		}
		if (desc != null) {
			Element bush = EngineZildo.spriteManagement.spawnElement(desc, pos.getX()*16+8, pos.getY()*16+8, 
					2, Reverse.NOTHING, Rotation.NOTHING);
			bush.addShadow(ElementDescription.SHADOW_SMALL);
			if (attacker.isZildo()) {
				((PersoPlayer)attacker).grabWithFork(bush);
			}
		}
	}

	public void walkSlab(int cx, int cy, int id, boolean pushed) {
		Point loc = walkedSlabs.get(id);
		if (pushed && loc == null) {
			SpriteEntity symbol = getSpriteOnCase(cx, cy);
			walkedSlabs.put(id,  new Point(cx, cy));
			writemap(cx, cy, 256*9 + 177);
			EngineZildo.soundManagement.broadcastSound(BankSound.Slab1, loc);
			if (symbol != null) symbol.setAjustedY(symbol.getAjustedY()+1);
		} if (!pushed && loc != null) {
			SpriteEntity symbol = getSpriteOnCase(loc.x, loc.y);
			walkedSlabs.remove(id);
			writemap(loc.x, loc.y, 256*9 + 176);
			EngineZildo.soundManagement.broadcastSound(BankSound.Slab2, loc);
			if (symbol != null) symbol.setAjustedY(symbol.getAjustedY()-1);
		}
	}

	private SpriteEntity getSpriteOnCase(int cx, int cy) {
		for (SpriteEntity entity : EngineZildo.spriteManagement.getSpriteEntities(null)) {
			if (entity.getEntityType() == EntityType.ENTITY && (int) entity.x / 16 == cx && (int) entity.y / 16 == cy) {
				return entity;
			}
		}
		return null;
	}
	
	/**
	 * Explode a wall at a given location, looking for a crack sprite. 
	 * @param loc
	 * @param ingame TRUE means that player is doing action. FALSE means that we restore a saved state => no explosion sound
	 * @param givenCrack element representing the crack in the wall (if provided, don't look for any others, indeed)
	 */
	public void explodeTile(Point loc, boolean ingame, Element givenCrack) {
		// Look for a 'crack'
		Element crack = givenCrack;
		if (crack == null) {
			crack = EngineZildo.spriteManagement.collideElement(loc.x, loc.y+8, null, 8, 
				GearDescription.CRACK1, GearDescription.CRACK2, GearDescription.BOULDER);
		}
		if (crack != null) {
			
			Point tileLoc = new Point(crack.getCenter());
			tileLoc.x /=16;
			tileLoc.y /=16;
			// Remove crack and replace tile with opened wall
			crack.dying = true;
			Rotation rotated = crack.rotation;
			if (crack.reverse == Reverse.VERTICAL) {
				rotated = rotated.succ().succ();
			}
			int onmap = readmap(tileLoc.x, tileLoc.y);
			switch (onmap) {
			case 3:
				TilePattern.explodedSouthHill.apply(tileLoc.x, tileLoc.y, this, rotated);
				break;
			case 12:
				tileLoc.y--;
			case 31:	// Hill
				TilePattern.explodedHill.apply(tileLoc.x, tileLoc.y, this, rotated);
				break;
			case 144+256*3:
				tileLoc.y--;
			case 129+256*3:	 // Grey cave (north)
			case 148+256*3:	// (south)
			case 150+256*3:	// (west)
			case 146+256*3:	// (east)
				TilePattern.explodedCave.apply(tileLoc.x, tileLoc.y, this, rotated);
				break;
			case 285:
				TilePattern.explodedHouseWall.apply(tileLoc.x, tileLoc.y, this, rotated);
				break;
			case 256 * 3 + 22:
				TilePattern.explodedRockCave.apply(tileLoc.x, tileLoc.y, this, rotated);
				break;
			}
			// Play secret sound
			if (ingame) {
				EngineZildo.scriptManagement.explodeWall(name, new Point(crack.x / 16, (crack.y-1) / 16));
				EngineZildo.soundManagement.broadcastSound(BankSound.ZildoSecret, loc);
			}
		}
	}
	
	
	
	/**
	 * A tile being "hammered". Can lower plots.
	 * @param tileLocation
	 */
	public void smashTile(Point tileLocation) {
		int onmap = readmap(tileLocation.x, tileLocation.y);
		switch (onmap) {
		case 173:
			writemap(tileLocation.x, tileLocation.y, 174);
			EngineZildo.soundManagement.broadcastSound(BankSound.Hammer, tileLocation.multiply(16f));
			break;
		}
	}

	/**
	 * Something disappeared on a tile (jar, bushes, rock ...)
	 * 
	 * @param tileLocation
	 *            location
	 * @param p_destroy
	 *            TRUE if tile is attacked / FALSE for simple action (ex: Zildo picks up a bush)
	 * @param p_spawnGoodies TODO
	 */
	public void takeSomethingOnTile(Point tileLocation, boolean p_destroy, Perso p_perso, boolean p_spawnGoodies) {
		int x = tileLocation.getX();
		int y = tileLocation.getY();
		int on_Area = readmap(x, y);
		int resultTile;
		SpriteAnimation anim = SpriteAnimation.FROMGROUND;
		if (Tile.isClosedChest(on_Area)) {	// Chest ?
			resultTile = Tile.getOpenedChest(on_Area);
			anim = SpriteAnimation.FROM_CHEST;
		} else {
			switch (on_Area) {
			case Tile.T_BUSH: // Bush
			default:
				resultTile = Tile.T_BUSH_CUT;
				break;
			case Tile.T_NETTLE:
				resultTile = Tile.T_NETTLE_CUT;
				break;
			case 167: // Rock
			case 169: // Heavy rock
				resultTile = 168;
				break;
			case 256*5+195:
				resultTile = -1;
				break;
			case 751: // Jar
				resultTile = 752;
				break;
			}
		}
		// Notify that this case should reappear after a given time (only in multiplayer mode)
		if (EngineZildo.game.multiPlayer) {
			addSpawningTile(tileLocation, null, DEFAULT_SPAWNING_TIME, true, p_perso != null ? p_perso.floor : highestFloor);
		}

		// Trigger
		TriggerElement trigger = TriggerElement.createLiftTrigger(name, tileLocation);
		EngineZildo.scriptManagement.trigger(trigger);
		
		// Remove tile on back2, if present
		boolean spawnGoodies = true;
		Case temp = this.get_mapcase(x, y);
		if (temp.getBackTile2() != null) {
			if (anim == SpriteAnimation.FROM_CHEST || resultTile == Tile.T_NETTLE_CUT) {	// Nettle cut are on back2 (...)
				// A chest is open => replace by the right tile
				temp.getBackTile2().index = resultTile % 256;
				temp.setModified(true);
			} else {
				// Remove back2 (bush/jar/whatever is taken => remove it)
				temp.setBackTile2(null);
				// Particular case : button under a jar !
				if (Tile.isButton(temp.getBackTile().getValue())) {
					spawnGoodies = false;
				}
			}
		} else {
			writemap(tileLocation.getX(), tileLocation.getY(), resultTile);
		}
		// Is there something planned to appear ?
		Point p = new Point(tileLocation.x * 16 + 8, tileLocation.y * 16 + 8);
		CaseItem item = getCaseItem(tileLocation.x, tileLocation.y);
		ElementDescription desc = item == null ? null : item.desc;
		SpriteManagement sprMgt = EngineZildo.spriteManagement;

		if (p_perso != null && anim == SpriteAnimation.FROM_CHEST) {
			if (desc == null) {
				desc = ElementDescription.THREEGOLDCOINS1;
			}
			Element elem = sprMgt.spawnSpriteGeneric(SpriteAnimation.FROM_CHEST, p.x, p.y + 8, p_perso.floor, 0, p_perso, desc);
			if (item != null) {
				elem.setName(item.name);
			}
		} else {
			if (desc != null) {
				boolean questTrigger = false;
				if (desc == ElementDescription.KEY) {
					if (EngineZildo.scriptManagement.isTakenItem(name, tileLocation, null, desc)) {
						return;	// Don't spawn item because player has already taken it
					} else {
						questTrigger = true;
					}
				}
				Element elem = sprMgt.spawnSpriteGeneric(anim, p.x, p.y + 5, 1, 0, p_perso, desc);
				elem.setName(item.name);
				elem.setTrigger(questTrigger);
			} else {
				boolean multiPlayer = EngineZildo.game.multiPlayer;
				if (spawnGoodies) {
					PersoPlayer zildo = EngineZildo.persoManagement.getZildo();
	
					int floor = zildo.floor;
					if ((multiPlayer || zildo.hasItem(ItemKind.BOW) && hasard.lanceDes(hasard.hazardBushes_Arrow))) {
						sprMgt.spawnSpriteGeneric(SpriteAnimation.ARROW, p.x, p.y + 5, floor, 0, zildo, null);
					} else if (hasard.lanceDes(hasard.hazardBushes_GoldCoin)) {
						sprMgt.spawnSpriteGeneric(SpriteAnimation.GOLDCOIN, p.x, p.y + 5, floor, 0, zildo, null);
					} else if (hasard.lanceDes(hasard.hazardBushes_BlueDrop)) {
						sprMgt.spawnSpriteGeneric(SpriteAnimation.BLUE_DROP, p.x + 3, p.y + 5, floor, p_destroy ? 0 : 1, zildo, null);
					} else if (multiPlayer && hasard.lanceDes(hasard.hazardBushes_Bombs)) {
						sprMgt.spawnSpriteGeneric(SpriteAnimation.FROMGROUND, p.x + 3, p.y + 5, floor, 0,
								zildo, ElementDescription.BOMBS3);
					}
				}
			}
		}

	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// translatePoints
	// /////////////////////////////////////////////////////////////////////////////////////
	// Shift every Area's point by this vector (shiftX, shiftY) to another Area
	// /////////////////////////////////////////////////////////////////////////////////////
	public void translatePoints(int shiftX, int shiftY, Area targetArea) {
		Case tempCase;
		for (int i = 0; i < dim_y; i++) {
			for (int j = 0; j < dim_x; j++) {
				tempCase = get_mapcase(j, i);
				targetArea.set_mapcase(j + shiftX, i + shiftY, tempCase);
			}
		}
	}
	
	public void addSpawningTile(Point tileLocation, String awaitedQuest, int time, boolean fog, int floor) {
		SpawningTile spawnTile = new SpawningTile();
		spawnTile.x = tileLocation.x;
		spawnTile.y = tileLocation.y;
		spawnTile.previousCase = new Case(get_mapcase(tileLocation.x, tileLocation.y));
		spawnTile.cnt = time;
		spawnTile.awaitedQuest = awaitedQuest;
		spawnTile.fog = fog;
		spawnTile.floor = floor;
		toRespawn.add(spawnTile);
	}

	public void addChainingPoint(ChainingPoint ch) {
		listChainingPoint.add(ch);
	}

	/**
	 * Returns chaining point linked to given map name.
	 * @param name
	 * @return ChainingPoint
	 */
	public ChainingPoint getNamedChainingPoint(String p_name) {
		for (ChainingPoint ch : listChainingPoint) {
			if (ch.getMapname().equals(p_name)) {
				return ch;
			}
		}
		return null;
	}
	
	public void removeChainingPoint(ChainingPoint ch) {
		listChainingPoint.remove(ch);
	}

	public int getDim_x() {
		return dim_x;
	}

	public void setDim_x(int dim_x) {
		this.dim_x = dim_x;
	}

	public int getDim_y() {
		return dim_y;
	}

	public void setDim_y(int dim_y) {
		this.dim_y = dim_y;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ChainingPoint> getChainingPoints() {
		return listChainingPoint;
	}

	public void setListPointsEnchainement(List<ChainingPoint> listPointsEnchainement) {
		this.listChainingPoint = listPointsEnchainement;
	}

	public boolean isModified() {
		return !changes.isEmpty();
	}

	public Collection<Point> getChanges() {
		return changes;
	}

	public void resetChanges() {
		changes.clear();
	}

	/**
	 * Serialize the map into an EasyWritingFile object.
	 * 
	 * @return EasyWritingFile
	 */
	@Override
	public void serialize(EasyBuffering p_file) {

		// Get the right lists to serialize the right number of each one
		List<SpriteEntity> entities = filterExportableSprites(EngineZildo.spriteManagement.getSpriteEntities(null));
		List<Perso> persos = filterExportablePersos(EngineZildo.persoManagement.tab_perso);

		int n_pe = listChainingPoint.size();
		int n_persos = persos.size();
		int nbFloors = highestFloor - lowestFloor + 1;
		
		// 0) Exclude "built from perso" sprites
		for (Iterator<SpriteEntity> it=entities.iterator(); it.hasNext();) {
			SpriteEntity entity = it.next();
			if (entity.getEntityType().isElement()) {
				Element elem = (Element) entity;
				if (elem.getLinkedPerso() != null || elem.getDesc() instanceof PersoDescription) {
					it.remove();
				}
			}
		}
		int n_sprites = entities.size();

		// 1) Header
		p_file.put((byte) atmosphere.ordinal());
		p_file.put((byte) dim_x);
		p_file.put((byte) dim_y);
		p_file.put((byte) nbFloors);
		p_file.put((byte) persos.size());
		p_file.put((byte) n_sprites);
		p_file.put((byte) n_pe);
		
		p_file.put((byte) scrollOffset.x);
		p_file.put((byte) scrollOffset.y);

		// 2) Save the map cases
		
		for (int fl = lowestFloor ;fl <= highestFloor ;fl++) {
			p_file.put((byte) fl);
			for (int i = 0; i < dim_y; i++) {
				for (int j = 0; j < dim_x; j++) {
					Case temp = get_mapcase(j, i, fl);
	
					if (temp == null) {
						Case.serializeNull(p_file);
					} else {
						temp.serialize(p_file);
					}
				}
			}
		}

		// 3) Chaining points
		if (n_pe != 0) {
			for (ChainingPoint ch : this.getChainingPoints()) {
				// Calculate the area concerned by this chaining point and set 'vertical' attribute correctly
				if (ch.isBorder() && ch.getZone(this).y2 > 16) {
					ch.setVertical(true);
				}
				ch.serialize(p_file);
			}
		}

		// 4) Sprites
		if (n_sprites != 0) {
			Element elem;
			String entityName;
			for (SpriteEntity entity : entities) {
				elem = null;
				if (entity.getEntityType().isElement()) {
					elem = (Element) entity;
				}
				p_file.put((int) entity.x);
				p_file.put((int) entity.y);
				if (nbFloors > 1) {
					p_file.put((byte) entity.floor);	// Default floor
				}
				int foreground = entity.isForeground() ? SpriteEntity.FOREGROUND : 0;
				int repeated = (entity.repeatX > 1 || entity.repeatY > 1 || entity.rotation != Rotation.NOTHING) ? SpriteEntity.REPEATED_OR_ROTATED : 0;
				int pushable = (elem != null && elem.isPushable()) ? SpriteEntity.PUSHABLE : 0;
				// FIXME: Warning ! PUSHABLE has 64 as value, but reverse could be 64 too ! (means VERTICAL)
				p_file.put((byte) (entity.getNBank() | entity.reverse.getValue() | foreground | repeated | pushable));
				p_file.put(entity.getNSpr());
				if (repeated > 0) {
					if (entity.rotation != Rotation.NOTHING) {
						p_file.put((byte) (entity.rotation.value | 128));
					}
					p_file.put(entity.repeatX);
					p_file.put(entity.repeatY);
				}
				entityName = entity.getName();
				p_file.put(entityName);
			}
		}

		// 5) Persos (characters)
		if (n_persos != 0) {
			for (Perso perso : persos) {
				p_file.put((int) perso.x);
				p_file.put((int) perso.y);
				p_file.put((int) perso.z);
				if (nbFloors > 1) {
					p_file.put((byte) perso.getFloor());	// Default floor
				}
				PersoDescription desc = perso.getDesc();
				p_file.put((byte) desc.getBank());
				p_file.put((byte) desc.first());
				p_file.put((byte) perso.getInfo().ordinal());
				p_file.put(perso.getDialogSwitch());
				// p_file.put((byte) 0); //(byte) perso.getEn_bras());
				p_file.put((byte) perso.getQuel_deplacement().valeur);
				p_file.put((byte) perso.getAngle().ordinal());
				p_file.put(perso.getName());
			}
		}

		// 6) Sentences
		if (dialogs != null) {
			List<String> phrases = dialogs.getDialogs();
			if (phrases.size() > 0) {
				p_file.put((byte) phrases.size());
				// On lit les phrases
				for (String s : phrases) {
					p_file.put(s);
				}
				// On lit le nom
				Map<String, Behavior> behaviors = dialogs.getBehaviors();
				for (Entry<String, Behavior> entry : behaviors.entrySet()) {
					p_file.put(entry.getKey());
					Behavior behav = entry.getValue();
					int len = behav.getLength();
					p_file.put((byte) len);
					for (int i=0;i<len;i++) {
						p_file.put((byte) behav.replique[i]);
					}
				}
			}
		}
	}

	/**
	 * @param p_buffer
	 * @param p_name
	 *            map name
	 * @return Area
	 */
	public static Area deserialize(EasyBuffering p_buffer, String p_name, boolean p_spawn) {

		Area map = new Area();
		map.setName(p_name);

		SpriteManagement spriteManagement = EngineZildo.spriteManagement;

		boolean zeditor = p_spawn && EngineZildo.game.editing;

		map.setAtmosphere(Atmosphere.values()[p_buffer.readUnsignedByte()]);
		map.setDim_x(p_buffer.readUnsignedByte());
		map.setDim_y(p_buffer.readUnsignedByte());
		map.originalDim = new Point(map.dim_x, map.dim_y);
		int nbFloors = p_buffer.readUnsignedByte();
		int n_persos = p_buffer.readUnsignedByte();
		int n_sprites = p_buffer.readUnsignedByte();
		int n_pe = p_buffer.readUnsignedByte();
		Point scrollOffset = new Point(p_buffer.readUnsignedByte(), p_buffer.readUnsignedByte());
		map.scrollOffset = scrollOffset; 

		if (zeditor) {	// If we're in the editor, we don't want map to be shifted
			scrollOffset = new Point(0, 0);
		}
		// La map
		for (int n=nbFloors; n>0; n--) {
			byte fl = (byte) p_buffer.readUnsignedByte();
			for (int ii = 0; ii < map.getDim_y(); ii++) {
				int i = ii + scrollOffset.y;
				for (int jj = 0; jj < map.getDim_x(); jj++) {
					int j = jj + scrollOffset.x;
					Case temp = Case.deserialize(p_buffer);
	
					if (temp != null) {
						map.set_mapcase(j, i, fl, temp);
		
						if (p_spawn && !EngineZildo.game.editing) {
							
							if (temp.getOneValued(256 + 99) != null) {
								// Fumée de cheminée
								spriteManagement.spawnSpriteGeneric(SpriteAnimation.CHIMNEY_SMOKE, j * 16, i * 16 - 4, fl, 0,
										null, null);
							}
							if (temp.getBackTile().getValue() == 256*9 + 53) {
								Element hearth = new ElementImpact(j*16+14, i*16+6 - 4, ImpactKind.HEARTH, null);
								hearth.z = 0;	// Default is z=4 for ElementImpact
								spriteManagement.spawnSprite(hearth);
							} else if (temp.getOneValued(256*6 + 231) != null) {
								if (EngineZildo.scriptManagement.isTileDone(map.getName(), new Point(j, i))) {
									temp.getBackTile2().index = 232;
								}
							} else {
								Tile tile = temp.getOneValued(512 + 231, 512 + 49, 512 + 59, 512 + 61);
								// Is this chest already opened ?
								if (tile != null ) {
									if (EngineZildo.scriptManagement.isTileDone(map.getName(), new Point(j, i))) {
										tile.index = Tile.getOpenedChest(tile.getValue()) & 255;
									}
								}
							}
						} 
					}
				}
			}
		}

		// Chaining points
		if (n_pe != 0) {
			for (int i = 0; i < n_pe; i++) {
				ChainingPoint ch = ChainingPoint.deserialize(p_buffer);
				ch.setPx((short) (ch.getPx() + scrollOffset.x * 2));
				ch.setPy((short) (ch.getPy() + scrollOffset.y * 2));
				map.addChainingPoint(ch);
			}
		}
		// Compute chaining points
		map.addChainingContextInfos();

		// Modify map dimension in-game only
		if (scrollOffset.x > 0) {
			map.dim_x = 64;
		}
		if (scrollOffset.y > 0) {
			map.dim_y = 64;
		}
		
		int floor;
		// Les sprites
		List<SpriteEntity> addedEntities = new ArrayList<SpriteEntity>();
		if (n_sprites != 0) {
			for (int i = 0; i < n_sprites; i++) {
				int x = p_buffer.readInt() + scrollOffset.x * 16;
				int y = p_buffer.readInt() + scrollOffset.y * 16;
				floor = 1;
				if (nbFloors > 1) {
					floor = p_buffer.readByte();
				}
				short multi = p_buffer.readUnsignedByte();
				// Multi contains many information : 0--15 : bank
				// 16 : REPEATED or ROTATED
				// 32 : FOREGROUND
				// 64 : PUSHABLE
				// 128 : TWO-bytes for index
				int nBank = multi & 15;
				int reverse = multi & Reverse.ALL.getValue();
				int nSpr = p_buffer.readInt();
				SpriteEntity entity = null;
				
				Rotation rot = Rotation.NOTHING;
				byte repX=0, repY=0;
				
				if ((multi & SpriteEntity.REPEATED_OR_ROTATED) != 0) {
					int temp = p_buffer.readByte();
					if ((temp & 128) != 0) {
						rot = Rotation.fromInt(temp & 127);
						temp = p_buffer.readByte();
					}
					repX = (byte) temp;
					repY = p_buffer.readByte();
				}

				String entName= p_buffer.readString();

				if (p_spawn) {
					// If this sprite is on a chest tile, link them
					int ax = x / 16;
					int ay = (y-1) / 16;
					int tileDesc = map.readmap(ax, ay);
					boolean linkable = Tile.isLinkableToItem(tileDesc);
					if (linkable) {
						map.setCaseItem(ax, ay, nSpr, entName);
					}
					boolean openedChest = Tile.isOpenedChest(tileDesc);
					// We have to see the sprites in ZEditor
					SpriteDescription desc = SpriteDescription.Locator.findSpr(nBank, nSpr);
					boolean shouldSpawn = (!linkable && !openedChest)|| zeditor;
					if (desc == GearDescription.GREEN_DOOR ||	// Opened door ?
						desc == GearDescription.CAVE_KEYDOOR ||
						desc == GearDescription.CAVE_MASTERDOOR) {
						ChainingPoint ch = map.getCloseChainingPoint(ax, ay);
						if (ch != null && EngineZildo.scriptManagement.isOpenedDoor(map.getName(), ch)) {
							shouldSpawn = false;
						}
					}
					if (shouldSpawn) {
						entity = spriteManagement.spawnSprite(desc, x, y, false, Reverse.fromInt(reverse),
								false);
						addedEntities.add(entity);
						if (desc instanceof GearDescription && ((GearDescription)desc).isExplodable()) {	// Exploded wall ?
							if (EngineZildo.scriptManagement.isExplodedWall(map.getName(), new Point(ax, ay))) {
								map.explodeTile(new Point(x, y), false, (Element) entity);
							}
						}
						if ((multi & SpriteEntity.FOREGROUND) != 0) {
							entity.setForeground(true);
						}
						if ((multi & SpriteEntity.REPEATED_OR_ROTATED) != 0) {
							entity.rotation = rot;
							entity.repeatX = repX;
							entity.repeatY = repY;
						}
					}
					if (entity != null) {
						entity.setName(entName);
						entity.setPushable((multi & SpriteEntity.PUSHABLE) != 0);
						entity.setFloor(floor);
					}
				}
			}
		}

		// Characters (Persos)
		if (n_persos != 0) {
			for (int i = 0; i < n_persos; i++) {
				int x = p_buffer.readInt() + scrollOffset.x * 16;
				int y = p_buffer.readInt() + scrollOffset.y * 16;
				int z = p_buffer.readInt();
				
				floor = 1;
				if (nbFloors > 1) {
					floor = p_buffer.readByte();
				}

				int sprBank = p_buffer.readUnsignedByte();
				int sprDesc = p_buffer.readUnsignedByte();
				SpriteDescription desc = SpriteDescription.Locator.findSpr(sprBank, sprDesc);
				if (desc.getBank() == SpriteBank.BANK_ZILDO) {
					desc = PersoDescription.ZILDO;
				}

				// Read the character informations
				int info = p_buffer.readUnsignedByte();
				// int en_bras=p_buffer.readUnsignedByte();
				// if (en_bras!= 0) {
				// throw new RuntimeException("enbras="+en_bras);
				// }
				String dialogSwitch = p_buffer.readString();
				int move = p_buffer.readUnsignedByte();
				int angle = p_buffer.readUnsignedByte();
				String name = p_buffer.readString();

				if ("zildo".equals(name)) {
					desc = PersoDescription.ZILDO;
					map.respawnPoints.add(new Point(x, y));
					if (!zeditor) { // We have to see persos in ZEditor
						continue;
					}
				}

				// And spawn it if necessary
				if (p_spawn) {
					Perso perso = EngineZildo.persoManagement.createPerso((PersoDescription) desc, x, y, z, name,
							angle);

					perso.setInfo(PersoInfo.values()[info]);
					perso.setQuel_deplacement(MouvementPerso.fromInt(move), false);
					if (desc == PersoDescription.PANNEAU && perso.getQuel_deplacement() != MouvementPerso.IMMOBILE) {
						// Fix a map bug : sign perso should be unmoveable
						perso.setQuel_deplacement(MouvementPerso.IMMOBILE, true);
					}

					Zone zo = new Zone();
					zo.x1 = map.roundAndRange(perso.getX() - 16 * 5, Area.ROUND_X);
					zo.y1 = map.roundAndRange(perso.getY() - 16 * 5, Area.ROUND_Y);
					zo.x2 = map.roundAndRange(perso.getX() + 16 * 5, Area.ROUND_X);
					zo.y2 = map.roundAndRange(perso.getY() + 16 * 5, Area.ROUND_Y);
					perso.setZone_deplacement(zo);
					if (perso.getMaxpv() == 0) {
						perso.setMaxpv(3);
						perso.setPv(3);
					}
					perso.setTarget(null);
					perso.setMouvement(MouvementZildo.VIDE);
					perso.setDialogSwitch(dialogSwitch);

					perso.initPersoFX();

					spriteManagement.spawnPerso(perso);
					perso.setFloor(floor);
					
					if (!zeditor) { // Check if a sprite is located on this character
						for (SpriteEntity entity : addedEntities) {
							if (Point.distance(entity.x, entity.y, perso.x, perso.y) < 8) {
								// This object should be carried by character
								ElementDescription elemDesc = (ElementDescription) entity.getDesc();
								if (!EngineZildo.scriptManagement.isTakenItem(p_name, null, perso.getName(), elemDesc)) {
									((PersoNJ)perso).setCarriedItem(elemDesc);
								}
								EngineZildo.spriteManagement.deleteSprite(entity);
							}
						}
					}
				}
			}
		}
		
		// Les Phrases
		int n_phrases = 0;
		map.dialogs = new MapDialog();
		if (!p_buffer.eof()) {
			n_phrases = p_buffer.readUnsignedByte();
			if (n_phrases > 0) {
				// On lit les phrases
				for (int i = 0; i < n_phrases; i++) {
					String phrase = p_buffer.readString();
					map.dialogs.addSentence(phrase);
				}
				if (!p_buffer.eof()) {
					while (!p_buffer.eof()) {
						// On lit le nom
						String nomPerso = p_buffer.readString();
						// On lit le comportement
						short[] comportement = new short[15];
						for (int i=0;i<comportement.length;i++) {
							comportement[i] = 0;
						}
						int lenComp = p_buffer.readByte();
						p_buffer.readUnsignedBytes(comportement, 0, lenComp);
						map.dialogs.addBehavior(nomPerso, comportement);
					}
				}
			}
		}

		if (!zeditor) {
			// Complete outside of map visible in the viewport with empty tile
			map.arrange();
		}
		
		if (p_spawn) {
			map.correctTrees();
		}
		return map;
	}

	private final static IntSet treeToBlock = new IntSet(144, 145, 148, 149, 23 + 256 * 4, 24 + 256 * 4, 27 + 256 * 4,
			28 + 256 * 4, 39 + 256 * 4, 40 + 256 * 4, 43 + 256 * 4, 44 + 256 * 4);

	/**
	 * Add blocking tile on the hidden part of the tree in order to limit the move of characters under the tree.
	 */
	private void correctTrees() {
		for (int j = 0; j < getDim_y(); j++) {
			for (int i = 0; i < getDim_x(); i++) {
				Case c = get_mapcase(i, j);
				if (c != null) {
					Tile foreTile = c.getForeTile();
					if (foreTile != null && treeToBlock.contains(foreTile.index + foreTile.bank * 256)) {
						c.getBackTile().index = 152;
						c.getBackTile().bank = 0;
					}
				}
			}
		}
	}

	/**
	 * Keep only the exportable sprites. Those which are eliminated are:
	 * <ul>
	 * <li>Zildo</li>
	 * <li>sprites related to others (ex:shadow)</li>
	 * <li>house's smoke (should be fixed)</li>
	 * </ul>
	 * 
	 * @param p_spriteEntities
	 * @return
	 */
	public List<SpriteEntity> filterExportableSprites(List<SpriteEntity> p_spriteEntities) {
		List<SpriteEntity> filteredEntities = new ArrayList<SpriteEntity>();
		for (SpriteEntity entity : p_spriteEntities) {
			EntityType type = entity.getEntityType();
			boolean ok = true;
			// In singleplayer, we have to exclude the sprites related to
			// others. Indeed, its will be created with the mother entity.
			if (!EngineZildo.game.multiPlayer && entity.getEntityType().isElement()) {
				Element elem = (Element) entity;
				if (elem.getLinkedPerso() != null) {
					ok = false;
				}
				if (elem.getNSpr() == ElementDescription.SMOKE_SMALL.ordinal()
						&& elem.getNBank() == SpriteBank.BANK_ELEMENTS) {
					ok = false;
					// Exclude smoke too (spawned on houses)
				}
			}
			if (entity.isZildo()) {
				ok = false;
			}

			if (entity.isVisible() && ok && (type == EntityType.ELEMENT || type == EntityType.ENTITY)) {
				filteredEntities.add(entity);
			}
		}
		return filteredEntities;
	}

	public List<Perso> filterExportablePersos(List<Perso> p_persos) {
		List<Perso> filteredPersos = new ArrayList<Perso>();
		for (Perso perso : p_persos) {
			if (!perso.isZildo()) {
				filteredPersos.add(perso);
			}
		}
		return filteredPersos;
	}

	public MapDialog getMapDialog() {
		return dialogs;
	}

	/**
	 * Respawns disappeared things in multiplayer mode.
	 */
	public void update() {
		// Only respawn bushes and chests in multiplayer
		for (Iterator<SpawningTile> it = toRespawn.iterator(); it.hasNext();) {
			SpawningTile spawnTile = it.next();
			if (spawnTile.awaitedQuest != null) {
				if (EngineZildo.scriptManagement.isQuestProcessing(spawnTile.awaitedQuest)) {
					// Wait for given quest to be over
					continue;
				}
			}
			if (spawnTile.cnt == 0) {
				int x = spawnTile.x * 16 + 8;
				int y = spawnTile.y * 16 + 8;
				// Respawn the tile if nothing bothers at location
				int radius = 8;
				if (EngineZildo.mapManagement.collideSprite(x, y, radius, null)) {
					spawnTile.cnt++;
				} else {
					set_mapcase(spawnTile.x, spawnTile.y, (byte) spawnTile.floor, spawnTile.previousCase);
					spawnTile.previousCase.setModified(true);
					if (spawnTile.fog) { 
						EngineZildo.spriteManagement.spawnSprite(new ElementImpact(x, y, ImpactKind.SMOKE, null));
					}
					changes.add(new Point(spawnTile.x, spawnTile.y));
					it.remove();
				}
			} else {
				spawnTile.cnt--;
			}
		}
		if (alertDuration == 0) {
			alertLocation = null;
		} else {
			alertDuration--;
		}
	}
	
	/**
	 * Link a tile with an item description. (useful for chest)
	 * 
	 * @param p_x
	 *            map X coordinate
	 * @param p_y
	 *            map Y coordinate
	 * @param p_nSpr
	 * @param p_name TODO
	 */
	public void setCaseItem(int p_x, int p_y, int p_nSpr, String p_name) {
		ElementDescription desc = ElementDescription.fromInt(p_nSpr);
		caseItem.put(lineSize * p_y + p_x, new CaseItem(desc, p_name));
	}

	/**
	 * Get the linked item description from a given position (if exists).
	 * 
	 * @param p_x
	 *            map X coordinate
	 * @param p_y
	 *            map Y coordinate
	 * @return CaseItem
	 */
	public CaseItem getCaseItem(int p_x, int p_y) {
		return caseItem.get(lineSize * p_y + p_x);
	}

	public Point getOffset() {
		return offset;
	}

	public void setOffset(Point offset) {
		this.offset = offset;
	}

	/**
	 * Returns the closest chaining point from given map-coordinates.
	 * 
	 * @param p_px
	 *            int in range 0..63
	 * @param p_py
	 *            int in range 0..63
	 * @return ChainingPoint
	 */
	public ChainingPoint getCloseChainingPoint(int p_px, int p_py) {
		List<ChainingPoint> points = getChainingPoints();
		for (ChainingPoint ch : points) {
			Zone z = ch.getZone(this);
			for (Angle a : Angle.values()) {
				if (!a.isDiagonal()) {
					int px = p_px + a.coords.x;
					int py = p_py + a.coords.y;
					if (z.isInto(16 * px, 16 * py)) {
						return ch;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Returns entities outside the map range (0..dim_x, 0..dim_y). Only used in ZEditor.
	 * 
	 * @return List<SpriteEntity>
	 */
	public List<SpriteEntity> getOutOfBoundEntities() {
		List<SpriteEntity> found = new ArrayList<SpriteEntity>();
		List<SpriteEntity> entities = filterExportableSprites(EngineZildo.spriteManagement.getSpriteEntities(null));
		List<Perso> persos = filterExportablePersos(EngineZildo.persoManagement.tab_perso);
		entities.addAll(persos);
		for (SpriteEntity entity : entities) {
			if (isOutside((int) entity.x, entity.getAjustedY())) {
				found.add(entity);
			}
		}
		return found;
	}

	/**
	 * Returns TRUE if the given point (in pixel coordinates : 0..16*64, 0..16*64) is outside the map.
	 * 
	 * @param tx
	 * @param ty
	 * @return boolean
	 */
	public boolean isOutside(int tx, int ty) {
		if (EngineZildo.mapManagement.getPreviousMap() != null) {
			return false;
		}
		return (tx < 0 || ty < 0 ||
				tx > ((dim_x - 1) << 4) + 15 || 
				ty > ((dim_y - 1) << 4) + 15);
	}

	public Atmosphere getAtmosphere() {
		return atmosphere;
	}

	public void setAtmosphere(Atmosphere atmosphere) {
		this.atmosphere = atmosphere;
	}
	
	public void alertAtLocation(Point p) {
		alertLocation = p;
		alertDuration = 5;
	}
	
	final float distanceHeard = 64f;
	
	public boolean isAnAlertAtLocation(float x, float y) {
		if (alertLocation == null) {
			return false;
		}
		double distance = Point.distance(x, y, alertLocation.x, alertLocation.y);
		return distance < distanceHeard;
	}
	
	public Point getAlertLocation() {
		return new Point(alertLocation);
	}
	
	public byte getLowestFloor() {
		return lowestFloor;
	}
	
	public byte getHighestFloor() {
		return highestFloor;
	}
	
	private void arrange() {
		// Complete map size with minimum viewport
		int emptyTile = atmosphere.getEmptyTile();
		for (int dy = dim_y ; dy < TILE_VIEWPORT_Y ; dy++) {
			for (int dx = 0; dx < Math.max(TILE_VIEWPORT_X, dim_x); dx++) {
				writemap(dx, dy, emptyTile);
			}
		}
		for (int dx = dim_x ; dx < TILE_VIEWPORT_X ; dx++) {
			for (int dy = 0; dy < Math.max(TILE_VIEWPORT_Y, dim_y); dy++) {
				writemap(dx, dy, emptyTile);
			}
		}
		if (dim_x < TILE_VIEWPORT_X) {
			dim_x = TILE_VIEWPORT_X;
		}
		if (dim_y < TILE_VIEWPORT_Y) {
			dim_y = TILE_VIEWPORT_Y;
		}
		
	}
	
	public static String[] findAllAreasName() {
		File[] areaFiles = Zildo.pdPlugin.listFiles(Constantes.MAP_PATH, new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".map");
			}
		});
		String[] res = new String[areaFiles.length];
		int i=0;
		for (File f : areaFiles) {
			String name = f.getName();
			int posPoint = name.indexOf(".");
			res[i++] = f.getName().substring(0, posPoint);
		}
		return res;
	}

	/** Reinitialize map tiles **/
	public void clearTiles() {
		mapdata = new Case[Constantes.TILEENGINE_FLOOR][Constantes.TILEENGINE_HEIGHT][Constantes.TILEENGINE_HEIGHT];
		int empty = atmosphere.getEmptyTile();
		for (int i = 0; i < dim_x * dim_y; i++) {
			int x = i % dim_x;
			int y = i / dim_x;
			writemap(x, y, empty);
		}
	}

	/** Shift everything (tiles/chaining points/sprites) with given vector **/
	public void shift(int shiftX, int shiftY) {
		// 0) Tiles
		// Clone tiles in an array that we'll release when this method will be over
		Case[][][] mapdataCloned = mapdata.clone();
		// 1) Change dimension
		int movedX = dim_x;
		int movedY = dim_y;
		dim_x = Math.min(64, dim_x + shiftX);
		dim_y = Math.min(64, dim_y + shiftY);
		clearTiles();
		for (int f=lowestFloor;f<=highestFloor;f++) {
			for (int y=0;y<movedY;y++) {
				int yy = y+shiftY;
				if (yy >= 0 && yy < dim_y) {
					for (int x=0;x<movedX;x++) {
						int xx = x+shiftX;
						if (xx >=0 && xx < dim_x) {
							mapdata[f][yy][xx] = mapdataCloned[f][y][x];
						}
					}
				}
			}
		}


		// 2) Chaining points
		for (ChainingPoint chp : listChainingPoint) {
			chp.setPx((short) (chp.getPx() + shiftX*2));
			chp.setPy((short) (chp.getPy() + shiftY*2));
		}
		// 3) Sprites
		EngineZildo.spriteManagement.shiftAllEntities(shiftX*16, shiftY*16);
	}

	public Point getScrollOffset() {
		return scrollOffset;
	}
	
	public Point getOriginalDim() {
		return originalDim;
	}
	
	public Point getNextMapOffset(Area nextMap, Angle p_mapScrollAngle, boolean mapBuilder) {
		Angle angleShift = p_mapScrollAngle.opposite();
		Point coords = angleShift.coords;
		Area mapReference = nextMap;
		Point scrollOffset = mapReference.getScrollOffset().multiply(16);
		switch (angleShift) {
		case OUEST:
		case NORD:
			mapReference = this;
		default:
		}
		if (!mapBuilder) {
			scrollOffset = mapReference.getScrollOffset().multiply(16);
		}
		
		Point ret = new Point(coords.x * 16 * mapReference.getDim_x(),
						 	  coords.y * 16 * mapReference.getDim_y());
		
		if (p_mapScrollAngle.isVertical()) { //.getScrollOffset().x != 0 && getScrollOffset().x != 0) {
			scrollOffset.x = 0;
		}
		// Place the map accordingly to the scroll offset defined in ZEditor props
		ret.sub(scrollOffset);
		return ret;
	}
	
	@Override
	public String toString() {
		return name+" ("+dim_x+"x"+dim_y+")";
	}
}