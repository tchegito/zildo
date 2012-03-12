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

package zildo.monde.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import zildo.client.sound.Ambient.Atmosphere;
import zildo.client.sound.BankSound;
import zildo.fwk.IntSet;
import zildo.fwk.bank.SpriteBank;
import zildo.fwk.file.EasyBuffering;
import zildo.fwk.file.EasySerializable;
import zildo.monde.Hasard;
import zildo.monde.dialog.Behavior;
import zildo.monde.dialog.MapDialog;
import zildo.monde.items.ItemKind;
import zildo.monde.sprites.Reverse;
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
import zildo.monde.sprites.persos.PersoZildo;
import zildo.monde.sprites.utils.MouvementPerso;
import zildo.monde.sprites.utils.MouvementZildo;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
import zildo.monde.util.Zone;
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
		int cnt = 5000;
	}

	// For roundAndRange
	static public int ROUND_X = 0;
	static public int ROUND_Y = 0;

	static public int lineSize = 128; // Max-size of a map's line

	private Point offset; // For scrolling map

	private int dim_x, dim_y;
	private String name;
	private Map<Integer, Case> mapdata;
	private List<ChainingPoint> listChainingPoint;
	private MapDialog dialogs;

	private Atmosphere atmosphere;

	// Elements linked to a given case (into chest, bushes, jar ...)
	private Map<Integer, ElementDescription> caseItem;

	// To diffuse changes to clients
	private final Collection<Point> changes;
	// To respawn removed items
	private final Collection<SpawningTile> toRespawn;
	// Respawn points for Zildo (multiplayer only)
	private final List<Point> respawnPoints;

	public List<Point> getRespawnPoints() {
		return respawnPoints;
	}

	public Area() {
		mapdata = new HashMap<Integer, Case>();
		listChainingPoint = new ArrayList<ChainingPoint>();

		changes = new HashSet<Point>();
		toRespawn = new HashSet<SpawningTile>();

		caseItem = new HashMap<Integer, ElementDescription>();
		respawnPoints = new ArrayList<Point>();

		offset = new Point(0, 0);
	}

	public Area(Atmosphere p_atmo) {
		this();
		dim_x = 64;
		dim_y = 64;
		int empty = p_atmo.getEmptyTile();
		for (int i = 0; i < dim_x * dim_y; i++) {
			int x = i % dim_x;
			int y = i / dim_x;
			writemap(x, y, empty);
		}
		atmosphere = p_atmo;
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// get_Areacase
	// /////////////////////////////////////////////////////////////////////////////////////
	// IN : coordinates
	// OUT: Case object at the given coordinates
	// /////////////////////////////////////////////////////////////////////////////////////
	public Case get_mapcase(int x, int y) {
		return mapdata.get(y * lineSize + x);
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// set_Areacase
	// /////////////////////////////////////////////////////////////////////////////////////
	// IN:coordinates, Case object
	// /////////////////////////////////////////////////////////////////////////////////////
	public void set_mapcase(int x, int y, Case c) {
		mapdata.put(y * lineSize + x, c);
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// get_animatedAreacase
	// /////////////////////////////////////////////////////////////////////////////////////
	// IN:coordinates, frame index
	// /////////////////////////////////////////////////////////////////////////////////////
	Case get_animatedAreacase(int x, int y, int compteur_animation) {
		Case temp = this.get_mapcase(x, y);
		temp.getBackTile().index = temp.getAnimatedMotif(compteur_animation);
		return temp;
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// readArea
	// /////////////////////////////////////////////////////////////////////////////////////
	// IN : coordinates on Area
	// foreground: FALSE=on the floor TRUE=foreground
	// OUT: return motif + bank*256
	// /////////////////////////////////////////////////////////////////////////////////////
	// Return n_motif + n_banque*256 from a given position on the Area
	public Tile readmap(int x, int y, boolean p_foreground) {
		Case temp = this.get_mapcase(x, y + 4);
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

	public int readmap(int x, int y) {
		Tile tile = readmap(x, y, false);
		if (tile == null) {
			return -1;
		} else {
			return tile.getValue();
		}
	}

	public int readAltitude(int x, int y) {
		Case temp = this.get_mapcase(x, (y + 4));
		if (temp == null) {
			return 0;
		}
		return temp.getZ();
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// writeArea
	// /////////////////////////////////////////////////////////////////////////////////////
	// IN:x,y (coordinates on Area), quoi =motif + bank*256
	// /////////////////////////////////////////////////////////////////////////////////////
	public void writemap(int x, int y, int quoi) {
		Case temp = this.get_mapcase(x, y + 4);
		if (temp == null) {
			temp = new Case();
		}
		Tile back = temp.getBackTile();
		back.index = quoi & 255;
		back.bank = quoi >> 8;
		this.set_mapcase(x, y + 4, temp);

		changes.add(new Point(x, y + 4));
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
	public ChainingPoint isChangingMap(float x, float y, Angle p_angle) {
		// On parcourt les points d'enchainements
		int ax = (int) (x / 16);
		int ay = (int) (y / 16);
		boolean border;
		List<ChainingPoint> candidates = new ArrayList<ChainingPoint>();
		if (listChainingPoint.size() != 0) {
			for (ChainingPoint chPoint : listChainingPoint) {
				// Area's borders
				border = isAlongBorder((int) x, (int) y);
				if (chPoint.isCollide(ax, ay, border)) {
					candidates.add(chPoint);
				}
			}
		}
		if (candidates.size() == 1) {
			return candidates.get(0);
		} else if (candidates.size() > 0) {
			// More than one possibility : we must be on a map corner
			for (ChainingPoint ch : candidates) {
				Angle chAngle = ch.getAngle((int) x, (int) y, p_angle);
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
			// We're gonna get a sort number in each coordinate for all chaining
			// point
			// referring to the same Area.
			for (ChainingPoint chP : listChainingPoint) {
				if (chP.getMapname().equals(ch.getMapname())) {
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
	public void attackTile(Point tileLocation) {
		// Check if Zildo destroy something on a tile
		int onmap = readmap(tileLocation.x, tileLocation.y);
		switch (onmap) {
		case 165: // Bushes
			Point spriteLocation = new Point(tileLocation.x * 16 + 8, tileLocation.y * 16 + 8);
			EngineZildo.spriteManagement.spawnSpriteGeneric(SpriteAnimation.BUSHES, spriteLocation.x, spriteLocation.y,
					0, null, null);
			EngineZildo.soundManagement.broadcastSound(BankSound.CasseBuisson, spriteLocation);

			takeSomethingOnTile(tileLocation, true, null);
			break;
		case 374: // Mud
			writemap(tileLocation.x, tileLocation.y, 375);
			break;
		}
	}
	
	public void smashTile(Point tileLocation) {
		int onmap = readmap(tileLocation.x, tileLocation.y);
		switch (onmap) {
		case 173:
			writemap(tileLocation.x, tileLocation.y, 174);
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
	 */
	public void takeSomethingOnTile(Point tileLocation, boolean p_destroy, Perso p_perso) {
		int on_Area = this.readmap(tileLocation.getX(), tileLocation.getY());
		int resultTile;
		SpriteAnimation anim = SpriteAnimation.FROMGROUND;
		switch (on_Area) {
		case 165: // Bush
		default:
			resultTile = 166;
			break;
		case 167: // Rock
		case 169: // Heavy rock
			resultTile = 168;
			break;
		case 751: // Jar
			resultTile = 752;
			break;
		case 743: // Chest
			resultTile = 744;
			anim = SpriteAnimation.FROM_CHEST;
			break;
		}
		// Notify that this case should reappear after a given time
		SpawningTile spawnTile = new SpawningTile();
		spawnTile.x = tileLocation.x;
		spawnTile.y = tileLocation.y;
		spawnTile.previousCase = new Case(get_mapcase(tileLocation.x, tileLocation.y + 4));
		toRespawn.add(spawnTile);

		this.writemap(tileLocation.getX(), tileLocation.getY(), resultTile);

		// Is there something planned to appear ?
		Point p = new Point(tileLocation.x * 16 + 8, tileLocation.y * 16 + 8);
		ElementDescription desc = getCaseItem(tileLocation.x, tileLocation.y);
		SpriteManagement sprMgt = EngineZildo.spriteManagement;

		if (p_perso != null && anim == SpriteAnimation.FROM_CHEST) {
			if (desc == null) {
				desc = ElementDescription.BLUEMONEY1;
			}
			sprMgt.spawnSpriteGeneric(SpriteAnimation.FROM_CHEST, p.x, p.y + 8, 0, p_perso, desc);
		} else {
			if (desc != null) {
				sprMgt.spawnSpriteGeneric(anim, p.x, p.y + 5, 0, null, desc);
			} else {
				boolean multiPlayer = EngineZildo.game.multiPlayer;
				PersoZildo zildo = EngineZildo.persoManagement.getZildo();
				if (Hasard.lanceDes(Hasard.hazardBushes_Arrow) && (multiPlayer || zildo.hasItem(ItemKind.BOW))) {
					sprMgt.spawnSpriteGeneric(SpriteAnimation.ARROW, p.x, p.y + 5, 0, null, null);
				} else if (Hasard.lanceDes(Hasard.hazardBushes_Diamant)) {
					sprMgt.spawnSpriteGeneric(SpriteAnimation.DIAMOND, p.x, p.y + 5, 0, null, null);
				} else if (Hasard.lanceDes(Hasard.hazardBushes_Heart)) {
					sprMgt.spawnSpriteGeneric(SpriteAnimation.HEART, p.x + 3, p.y + 5, p_destroy ? 0 : 1, null, null);
				} else if (Hasard.lanceDes(Hasard.hazardBushes_Bombs) && (multiPlayer || zildo.hasItem(ItemKind.BOMB))) {
					sprMgt.spawnSpriteGeneric(SpriteAnimation.FROMGROUND, p.x + 3, p.y + 5, 0, null,
							ElementDescription.BOMBS3);
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

	public void addChainingPoint(ChainingPoint ch) {
		listChainingPoint.add(ch);
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

	public List<ChainingPoint> getListPointsEnchainement() {
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
		int n_sprites = entities.size();
		int n_persos = persos.size();

		// 1) Header
		p_file.put((byte) atmosphere.ordinal());
		p_file.put((byte) this.getDim_x());
		p_file.put((byte) this.getDim_y());
		p_file.put((byte) persos.size());
		p_file.put((byte) n_sprites);
		p_file.put((byte) n_pe);

		// 2) Save the map cases
		for (int i = 0; i < this.getDim_y(); i++) {
			for (int j = 0; j < this.getDim_x(); j++) {
				Case temp = this.get_mapcase(j, i + 4);

				if (temp == null) {
					temp = new Case();
				}
				temp.serialize(p_file);
			}
		}

		// 3) Chaining points
		if (n_pe != 0) {
			for (ChainingPoint ch : this.getListPointsEnchainement()) {
				ch.serialize(p_file);
			}
		}

		// 4) Sprites
		if (n_sprites != 0) {
			for (SpriteEntity entity : entities) {
				p_file.put((int) entity.x);
				p_file.put((int) entity.y);
				int foreground = entity.isForeground() ? SpriteEntity.FOREGROUND : 0;
				int repeated = (entity.repeatX > 1 || entity.repeatY > 1) ? SpriteEntity.REPEATED : 0;
				p_file.put((byte) (entity.getNBank() | entity.reverse.getValue() | foreground | repeated));
				p_file.put((byte) entity.getNSpr());
				if (repeated > 0) {
					p_file.put(entity.repeatX);
					p_file.put(entity.repeatY);
				}
			}
		}

		// 5) Persos (characters)
		if (n_persos != 0) {
			for (Perso perso : persos) {
				p_file.put((int) perso.x);
				p_file.put((int) perso.y);
				p_file.put((int) perso.z);
				PersoDescription desc = perso.getDesc();
				p_file.put((byte) desc.getBank());
				p_file.put((byte) desc.first());
				p_file.put((byte) perso.getInfo().ordinal());
				p_file.put(perso.getDialogSwitch());
				// p_file.put((byte) 0); //(byte) perso.getEn_bras());
				p_file.put((byte) perso.getQuel_deplacement().ordinal());
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
					for (int i : behav.replique) {
						p_file.put((byte) i);
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
		int n_persos = p_buffer.readUnsignedByte();
		int n_sprites = p_buffer.readUnsignedByte();
		int n_pe = p_buffer.readUnsignedByte();

		// La map
		for (int i = 0; i < map.getDim_y(); i++) {
			for (int j = 0; j < map.getDim_x(); j++) {
				Case temp = Case.deserialize(p_buffer);

				map.set_mapcase(j, i + 4, temp);

				if (p_spawn && !EngineZildo.game.editing) {
					Tile backTile = temp.getBackTile();
					if (backTile.index == 99 && backTile.bank == 1) {
						// Fumée de cheminée
						spriteManagement.spawnSpriteGeneric(SpriteAnimation.CHIMNEY_SMOKE, j * 16, i * 16 - 4, 0, null,
								null);
					}
					// Is this chest already opened ?
					if (backTile.index == (743 & 255) && backTile.bank == 2) {
						if (EngineZildo.scriptManagement.isOpenedChest(map.getName(), new Point(j, i))) {
							backTile.index = 744 & 255;
						}
					}
				}

			}
		}

		// Les P.E
		if (n_pe != 0) {
			for (int i = 0; i < n_pe; i++) {
				map.addChainingPoint(ChainingPoint.deserialize(p_buffer));
			}
		}
		// Compute chaining points
		map.addChainingContextInfos();

		// Les sprites
		if (n_sprites != 0) {
			for (int i = 0; i < n_sprites; i++) {
				int x = p_buffer.readInt();
				int y = p_buffer.readInt();
				short nSpr;
				short multi = p_buffer.readUnsignedByte();
				int nBank = multi & 15;
				int reverse = multi & Reverse.ALL.getValue();
				nSpr = p_buffer.readUnsignedByte();
				if (p_spawn) {
					// If this sprite is on a chest tile, link them
					int ax = x / 16;
					int ay = y / 16;
					int tileDesc = map.readmap(ax, ay);
					switch (tileDesc) {
					case 744: // Opened chest (don't spawn the linked item)
						break;
					case 743: // Chest
					case 165: // Bushes
					case 167: // Stone
					case 169: // Heavy stone
					case 751: // Jar
						map.setCaseItem(ax, ay, nSpr);
						if (!zeditor) { // We have to see the sprites in ZEditor
							break;
						}
					default: // else, show it as a regular element
						SpriteDescription desc = SpriteDescription.Locator.findSpr(nBank, nSpr);
						if (desc == GearDescription.GREEN_DOOR) {
							ChainingPoint ch = map.getCloseChainingPoint(ax, ay);
							if (ch != null && EngineZildo.scriptManagement.isOpenedDoor(map.getName(), ch)) {
								break;
							}
						}
						SpriteEntity entity = spriteManagement.spawnSprite(desc, x, y, false, Reverse.fromInt(reverse),
								false);
						if ((multi & SpriteEntity.FOREGROUND) != 0) {
							entity.setForeground(true);
						}
						if ((multi & SpriteEntity.REPEATED) != 0) {
							entity.repeatX = p_buffer.readByte();
							entity.repeatY = p_buffer.readByte();
						}
						break;
					}
				}
			}
		}

		// Les persos
		if (n_persos != 0) {
			for (int i = 0; i < n_persos; i++) {
				PersoNJ perso;
				int x = p_buffer.readInt();
				int y = p_buffer.readInt();
				int z = p_buffer.readInt();

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
				if (!p_spawn) {
					perso = new PersoNJ();
				} else {
					perso = (PersoNJ) EngineZildo.persoManagement.createPerso((PersoDescription) desc, x, y, z, name,
							angle);

					perso.setInfo(PersoInfo.values()[info]);
					perso.setQuel_deplacement(MouvementPerso.fromInt(move));
					if (desc == PersoDescription.PANNEAU && perso.getQuel_deplacement() != MouvementPerso.IMMOBILE) {
						// Fix a map bug : sign perso should be unmoveable
						perso.setQuel_deplacement(MouvementPerso.IMMOBILE);
					} else if (desc == PersoDescription.GARDE_CANARD && perso.getInfo() != PersoInfo.ENEMY) {
						// Another map bug : guards are always hostile
						perso.setInfo(PersoInfo.ENEMY);
					}

					Zone zo = new Zone();
					zo.setX1(map.roundAndRange(perso.getX() - 16 * 5, Area.ROUND_X));
					zo.setY1(map.roundAndRange(perso.getY() - 16 * 5, Area.ROUND_Y));
					zo.setX2(map.roundAndRange(perso.getX() + 16 * 5, Area.ROUND_X));
					zo.setY2(map.roundAndRange(perso.getY() + 16 * 5, Area.ROUND_Y));
					perso.setZone_deplacement(zo);
					perso.setMaxpv(3);
					perso.setPv(3);
					perso.setTarget(null);
					perso.setMouvement(MouvementZildo.VIDE);
					perso.setDialogSwitch(dialogSwitch);

					perso.initPersoFX();

					spriteManagement.spawnPerso(perso);
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
						short[] comportement = new short[10];
						p_buffer.readUnsignedBytes(comportement, 0, 10);
						map.dialogs.addBehavior(nomPerso, comportement);
					}
				}
			}
		}

		if (p_spawn) {
			map.correctDoorHouse();
			map.correctTrees();
		}
		return map;
	}

	private void correctDoorHouse() {
		Case c;
		for (int j = 0; j < getDim_y(); j++) {
			for (int i = 0; i < getDim_x(); i++) {
				int onmap = readmap(i, j);
				if (onmap == 278) {
					// We found a door on a house
					for (int l = -3; l < 0; l++) {
						for (int k = -2; k < 3; k++) {
							c = get_mapcase(i + k, j + 4 + l);
							if (c.getForeTile() == null) {
								c.setForeTile(c.getBackTile());
							}
						}
					}
				}
			}
		}
	}

	private final static IntSet treeToBlock = new IntSet(144, 145, 148, 149, 23 + 256 * 4, 24 + 256 * 4, 27 + 256 * 4,
			28 + 256 * 4, 39 + 256 * 4, 40 + 256 * 4, 43 + 256 * 4, 44 + 256 * 4);

	/**
	 * Add blocking tile on the hidden part of the tree in order to limit the move of characters under the tree.
	 */
	private void correctTrees() {
		for (int j = 0; j < getDim_y(); j++) {
			for (int i = 0; i < getDim_x(); i++) {
				Case c = get_mapcase(i, j + 4);
				Tile foreTile = c.getForeTile();
				if (foreTile != null && treeToBlock.contains(foreTile.index + foreTile.bank * 256)) {
					c.getBackTile().index = 152;
					c.getBackTile().bank = 0;
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
		if (EngineZildo.game.multiPlayer) {
			// Only respawn bushes and chests in multiplayer
			for (Iterator<SpawningTile> it = toRespawn.iterator(); it.hasNext();) {
				SpawningTile spawnTile = it.next();
				if (spawnTile.cnt == 0) {
					int x = spawnTile.x * 16 + 8;
					int y = spawnTile.y * 16 + 8;
					// Respawn the tile if nothing bothers at location
					int radius = 8;
					if (EngineZildo.mapManagement.collideSprite(x, y, radius, null)) {
						spawnTile.cnt++;
					} else {
						this.set_mapcase(spawnTile.x, spawnTile.y + 4, spawnTile.previousCase);
						EngineZildo.spriteManagement.spawnSprite(new ElementImpact(x, y, ImpactKind.SMOKE, null));
						changes.add(new Point(spawnTile.x, spawnTile.y + 4));
						it.remove();
					}
				} else {
					spawnTile.cnt--;
				}
			}
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
	 */
	public void setCaseItem(int p_x, int p_y, int p_nSpr) {
		ElementDescription desc = ElementDescription.fromInt(p_nSpr);
		caseItem.put(lineSize * p_y + p_x, desc);
	}

	/**
	 * Get the linked item description from a given position (if exists).
	 * 
	 * @param p_x
	 *            map X coordinate
	 * @param p_y
	 *            map Y coordinate
	 * @return ElementDescription
	 */
	public ElementDescription getCaseItem(int p_x, int p_y) {
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
		List<ChainingPoint> points = getListPointsEnchainement();
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
			if (isOutside((int) entity.x, (int) entity.y)) {
				found.add(entity);
			}
		}
		return found;
	}

	/**
	 * Returns TRUE if the given point (in map coordinates : 0..64,0..64) is outside the map.
	 * 
	 * @param tx
	 * @param ty
	 * @return boolean
	 */
	public boolean isOutside(int tx, int ty) {
		return (tx < 0 || ty < 0 ||
				tx > (dim_x - 1) * 16 + 15 || ty > (dim_y - 1) * 16 + 15);
	}

	public Atmosphere getAtmosphere() {
		return atmosphere;
	}

	public void setAtmosphere(Atmosphere atmosphere) {
		this.atmosphere = atmosphere;
	}
}