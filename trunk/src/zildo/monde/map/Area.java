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

package zildo.monde.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import zildo.client.sound.BankSound;
import zildo.fwk.bank.SpriteBank;
import zildo.fwk.file.EasyBuffering;
import zildo.fwk.file.EasySerializable;
import zildo.monde.collision.Collision;
import zildo.monde.dialog.Behavior;
import zildo.monde.dialog.MapDialog;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.elements.ElementImpact;
import zildo.monde.sprites.elements.ElementImpact.ImpactKind;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoNJ;
import zildo.monde.sprites.persos.Perso.PersoInfo;
import zildo.monde.sprites.utils.MouvementPerso;
import zildo.monde.sprites.utils.MouvementZildo;
import zildo.server.EngineZildo;
import zildo.server.SpriteManagement;

public class Area implements EasySerializable {

    class SpawningTile {
        Case previousCase;
        int x, y;
        int cnt=5000;
    }

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static public int M_MOTIF_MASQUE = 128;

	// For roundAndRange
	static public int ROUND_X = 0;
	static public int ROUND_Y = 0;

	private int dim_x, dim_y;
	private String name;
	private Map<Integer, Case> mapdata;
	private List<ChainingPoint> listPointsEnchainement;
	private MapDialog dialogs;

    // To diffuse changes to clients
    private final Collection<Point> changes;
    // To respawn removed items
    private final Collection<SpawningTile> toRespawn;

	public Area() {
		mapdata = new HashMap<Integer, Case>();
		listPointsEnchainement = new ArrayList<ChainingPoint>();

		changes = new HashSet<Point>();
		toRespawn = new HashSet<SpawningTile>();
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// get_Areacase
	// /////////////////////////////////////////////////////////////////////////////////////
	// IN : coordinates
	// OUT: Case object at the given coordinates
	// /////////////////////////////////////////////////////////////////////////////////////
	public Case get_mapcase(int x, int y) {
		return mapdata.get(y * this.dim_x + x);
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// set_Areacase
	// /////////////////////////////////////////////////////////////////////////////////////
	// IN:coordinates, Case object
	// /////////////////////////////////////////////////////////////////////////////////////
	public void set_mapcase(int x, int y, Case c) {
		mapdata.put(y * this.dim_x + x, c);
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// get_animatedAreacase
	// /////////////////////////////////////////////////////////////////////////////////////
	// IN:coordinates, frame index
	// /////////////////////////////////////////////////////////////////////////////////////
	Case get_animatedAreacase(int x, int y, int compteur_animation) {
		Case temp = this.get_mapcase(x, y);
		temp.setN_motif(temp.getAnimatedMotif(compteur_animation));
		return temp;
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// readArea
	// /////////////////////////////////////////////////////////////////////////////////////
	// IN : coordinates on Area
	// OUT: return motif + bank*256
	// /////////////////////////////////////////////////////////////////////////////////////
	// Return n_motif + n_banque*256 from a given position on the Area
	public int readmap(int x, int y) {
		Case temp = this.get_mapcase(x, y + 4);
		if (temp == null) {
			return -1;
		}
		int a = temp.getN_banque() & 31;
		int b = temp.getN_motif();
		/*
		 * if (a==2 && b==0) { a=temp.n_banque_masque & 31; b=temp.n_motif_masque; }
		 */
		a = a << 8;
		return a + b;
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
		temp.setN_motif(quoi & 255);
		// Don't squeeze the foreground tile
		int nBanque=temp.getN_banque() & 0xb0;
		temp.setN_banque(quoi >> 8 | nBanque);
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
		if (x < 0)
			x = 0;
		int max = dim_x;
		if (whatToRound == ROUND_Y)
			max = dim_y;
		if (x > (max * 16 - 16))
			x = max * 16 - 16;

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
		List<ChainingPoint> candidates=new ArrayList<ChainingPoint>();
		if (listPointsEnchainement.size() != 0) {
			for (ChainingPoint chPoint : listPointsEnchainement) {
				// Area's borders
				border = isAlongBorder((int) x, (int) y);
				if (chPoint.isCollide(ax, ay, border)) {
					addChainingContextInfos(chPoint);
					candidates.add(chPoint);
				}
			}
		}
		if (candidates.size() == 1) {
			return candidates.get(0);
		} else if (candidates.size() > 0) {
			// More than one possibility : we must be on a map corner
			for (ChainingPoint ch : candidates) {
				Angle chAngle=ch.getAngle((int) x, (int) y, p_angle);
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
	void addChainingContextInfos(ChainingPoint chPoint) {
		int orderX = 0;
		int orderY = 0;
		// We're gonna get a sort number in each coordinate for all chaining point
		// referring to the same Area.
		for (ChainingPoint chP : listPointsEnchainement) {
			if (chP.getMapname().equals(chPoint.getMapname())) {
				if (chP.getPx() <= chPoint.getPx()) {
					orderX++;
				}
				if (chP.getPy() <= chPoint.getPy()) {
					orderY++;
				}
			}
		}
		chPoint.setOrderX(orderX);
		chPoint.setOrderY(orderY);
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// getTarget
	// /////////////////////////////////////////////////////////////////////////////////////
	// IN : comingArea -. Area's name
	// /////////////////////////////////////////////////////////////////////////////////////
	public ChainingPoint getTarget(String comingArea, int orderX, int orderY) {
		if (listPointsEnchainement.size() != 0) {
			for (ChainingPoint chPoint : listPointsEnchainement) {
				if (chPoint.getMapname().equals(comingArea)) {
					if (orderX == 0 && orderY == 0) {
						return chPoint;
					} else {
						// Get the right one, because there is several connections between
						// the two Areas.
						addChainingContextInfos(chPoint);
						if (chPoint.getOrderX() == orderX && chPoint.getOrderY() == orderY) {
							return chPoint;
						}
					}
				}
			}
		}
		return null;
	}

	/*
	 * // On place Zildo sur son bon angle si c'est pas le cas} int angle {On change de Area} temp:=name; {On sauve l'ancien nom}
	 * fade(FALSE); charger_aventure_Area(Area1,tab_pe[i].Areaname); {On cherche le point de r‚apparition de Zildo} if n_pe<>0 then {Ce
	 * nombre ne PEUT pas ˆtre nul} for j:=0 to n_pe-1 do if tab_pe[j].Areaname=temp then begin x:=(tab_pe[j].px and 127)*16+16;
	 * y:=(tab_pe[j].py and 127)*16+8; if (tab_pe[j].px and 128) <> 0 then begin x:=x-8;y:=y+8; end; coming_Area:=1; {On met Zildo un peu en
	 * avant} case angle of 0:y:=y-16; 1:x:=x+16; 2:y:=y+16; 3:x:=x-16; end; camerax:=round(x)-16*10; cameray:=round(y)-16*6; if
	 * camerax>(16*dim_x-16*20) then camerax:=16*dim_x-16*20; if cameray>(16*dim_y-16*13+8) then cameray:=16*dim_y-16*13+8; if camerax<0
	 * then camerax:=0; if cameray<0 then cameray:=0; exit; end; }
	 */

	// /////////////////////////////////////////////////////////////////////////////////////
	// attackTile
	// /////////////////////////////////////////////////////////////////////////////////////
    public void attackTile(Point tileLocation) {
        // On teste si Zildo détruit un buisson
        int on_Area = this.readmap(tileLocation.getX(), tileLocation.getY());
        if (on_Area == 165) {
            Point spriteLocation = new Point(tileLocation.getX() * 16 + 8, tileLocation.getY() * 16 + 8);
            EngineZildo.spriteManagement.spawnSpriteGeneric(Element.SPR_BUISSON, spriteLocation.getX(), spriteLocation.getY(), 0, null);
            EngineZildo.soundManagement.broadcastSound(BankSound.CasseBuisson, spriteLocation);

            takeSomethingOnTile(tileLocation);
        }

    }

    /**
     * Something disappeared on a tile (jar, bushes, rock ...)
     * @param tileLocation location
     */
    public void takeSomethingOnTile(Point tileLocation) {
        int on_Area = this.readmap(tileLocation.getX(), tileLocation.getY());
        int resultTile;
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
        }
        SpawningTile spawnTile=new SpawningTile();
        spawnTile.x=tileLocation.x;
        spawnTile.y=tileLocation.y;
        spawnTile.previousCase=new Case(get_mapcase(tileLocation.x, tileLocation.y + 4));
        toRespawn.add(spawnTile);
        this.writemap(tileLocation.getX(), tileLocation.getY(), resultTile);
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

	public void addPointEnchainement(ChainingPoint ch) {
		listPointsEnchainement.add(ch);
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
		return listPointsEnchainement;
	}

	public void setListPointsEnchainement(List<ChainingPoint> listPointsEnchainement) {
		this.listPointsEnchainement = listPointsEnchainement;
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
	 * @return EasyWritingFile
	 */
	public void serialize(EasyBuffering p_file) {

		// Get the right lists to serialize the right number of each one
		List<SpriteEntity> entities = filterExportableSprites(EngineZildo.spriteManagement.getSpriteEntities(null));
		List<Perso> persos = filterExportablePersos(EngineZildo.persoManagement.tab_perso);

		int n_pe = listPointsEnchainement.size();
		int n_sprites = entities.size();
		int n_persos = persos.size();

		// 1) Header
		p_file.put((byte) this.getDim_x());
		p_file.put((byte) this.getDim_y());
		p_file.put((byte) persos.size());
		p_file.put((byte) n_sprites);
		p_file.put((byte) n_pe);

		// 2) Save the map cases
		for (int i = 0; i < this.getDim_y(); i++) {
			for (int j = 0; j < this.getDim_x(); j++) {
				Case temp = this.get_mapcase(j, i + 4);

				p_file.put((byte) temp.getN_motif());
				p_file.put((byte) temp.getN_banque());
				p_file.put((byte) temp.getN_motif_masque());
				p_file.put((byte) temp.getN_banque_masque());
			}
		}

		// 3) Chaining points
		if (n_pe != 0) {
			for (ChainingPoint ch : this.getListPointsEnchainement()) {
				p_file.put((byte) ch.getPx());
				p_file.put((byte) ch.getPy());
				p_file.put(ch.getMapname(), 9);
			}
		}

		// 4) Sprites
		if (n_sprites != 0) {
			int nSprites = 0;
			for (SpriteEntity entity : entities) {
				// Only element
				p_file.put((int) entity.x);
				p_file.put((int) entity.y);
				p_file.put((byte) entity.getNSpr());
				nSprites++;
			}
		}

		// 5) Persos (characters)
		if (n_persos != 0) {
			for (Perso perso : persos) {
				p_file.put((int) perso.x);
				p_file.put((int) perso.y);
				p_file.put((int) perso.z);
				p_file.put((byte) perso.getQuel_spr().first());
				p_file.put((byte) perso.getInfo().ordinal());
				p_file.put((byte) 0); //(byte) perso.getEn_bras());
				p_file.put((byte) perso.getQuel_deplacement().ordinal());
				p_file.put((byte) perso.getAngle().ordinal());
				p_file.put(perso.getNom(), 9);
			}
		}

		// 6) Sentences
		int nPhrases = dialogs.getN_phrases();
		p_file.put((byte) nPhrases);
		if (nPhrases > 0) {
			// On lit les phrases
			String[] sentences = dialogs.getDialogs();
			for (int i = 0; i < nPhrases; i++) {
				p_file.put(sentences[i]);
			}
			// On lit le nom
			Map<String, Behavior> behaviors = dialogs.getBehaviors();
			for (Entry<String, Behavior> entry : behaviors.entrySet()) {
				p_file.put(entry.getKey(), 9);
				Behavior behav = entry.getValue();
				for (int i : behav.replique) {
					p_file.put((byte) i);
				}
			}
		}
	}

	/**
	 * @param p_buffer
	 * @return Area
	 */
	public static Area deserialize(EasyBuffering p_buffer, boolean p_spawn) {

		Area map = new Area();
		SpriteManagement spriteManagement = EngineZildo.spriteManagement;

		map.setDim_x(p_buffer.readUnsignedByte());
		map.setDim_y(p_buffer.readUnsignedByte());
		int n_persos = p_buffer.readUnsignedByte();
		int n_sprites = p_buffer.readUnsignedByte();
		int n_pe = p_buffer.readUnsignedByte();

		// La map
		for (int i = 0; i < map.getDim_y(); i++)
			for (int j = 0; j < map.getDim_x(); j++) {
				// System.out.println("x="+j+" y="+i);
				Case temp = new Case();
				temp.setN_motif(p_buffer.readUnsignedByte());
				temp.setN_banque(p_buffer.readUnsignedByte());
				temp.setN_motif_masque(p_buffer.readUnsignedByte());
				temp.setN_banque_masque(p_buffer.readUnsignedByte());

				map.set_mapcase(j, i + 4, temp);

				if (temp.getN_motif() == 99 && temp.getN_banque() == 1 && p_spawn) {
					// Fumée de cheminée
					spriteManagement.spawnSpriteGeneric(Element.SPR_FUMEE, j * 16, i * 16, 0, null);
				}
			}
		/*
		 * with spr do begin x:=j*16+16; y:=i*16+28; z:=16; vx:=0.3+random(5)*0.01;vy:=0;vz:=0; ax:=-0.01;ay:=0;az:=0.01+random(5)*0.001;
		 * quelspr:=6; end; spawn_sprite(spr); end;
		 */

		// Les P.E
		ChainingPoint pe;
		if (n_pe != 0) {
			for (int i = 0; i < n_pe; i++) {
				pe = new ChainingPoint();
				pe.setPx(p_buffer.readUnsignedByte());
				pe.setPy(p_buffer.readUnsignedByte());
				pe.setMapname(p_buffer.readString(9));
				map.addPointEnchainement(pe);
			}
		}

		// Les sprites
		if (n_sprites != 0) {
			for (int i = 0; i < n_sprites; i++) {
				int x = (p_buffer.readUnsignedByte() << 8) + p_buffer.readUnsignedByte();
				int y = (p_buffer.readUnsignedByte() << 8) + p_buffer.readUnsignedByte();
				short nSpr;
				nSpr = p_buffer.readUnsignedByte();
				if (p_spawn) {
					spriteManagement.spawnSprite(SpriteBank.BANK_ELEMENTS, nSpr, x, y, false);
				}
			}
		}

		// Les persos
		if (n_persos != 0) {
			for (int i = 0; i < n_persos; i++) {
				PersoNJ perso;
				int x = (p_buffer.readUnsignedByte() << 8) + p_buffer.readUnsignedByte();
				int y = (p_buffer.readUnsignedByte() << 8) + p_buffer.readUnsignedByte();
				int z = (p_buffer.readUnsignedByte() << 8) + p_buffer.readUnsignedByte();

                int sprDesc = p_buffer.readUnsignedByte();
                if (sprDesc > 128) {
                    sprDesc -= 2;
                }
                PersoDescription desc = PersoDescription.fromNSpr(sprDesc);

                // Read the character informations
                int info=p_buffer.readUnsignedByte();
				int en_bras=p_buffer.readUnsignedByte();
				if (en_bras!= 0) {
					throw new RuntimeException("enbras="+en_bras);
				}
				int move=p_buffer.readUnsignedByte();
				int angle=p_buffer.readUnsignedByte();
				String name=p_buffer.readString(9);
				
				// And spawn it if necessary
				if (!p_spawn) {
					perso = new PersoNJ();
				} else {
					perso = (PersoNJ) EngineZildo.persoManagement.createPerso(desc, x, y, z, name, angle);

					perso.setInfo(PersoInfo.values()[info]);
					perso.setEn_bras(null);
					perso.setQuel_deplacement(MouvementPerso.fromInt(move));
					if (desc==PersoDescription.PANNEAU && perso.getQuel_deplacement() != MouvementPerso.SCRIPT_IMMOBILE) {
						// Fix a map bug : sign perso should be unmoveable
						perso.setQuel_deplacement(MouvementPerso.SCRIPT_IMMOBILE);	
					} else if (desc==PersoDescription.GARDE_CANARD && perso.getInfo()!=PersoInfo.ENEMY) {
						// Another map bug : guards are always hostile
						perso.setInfo(PersoInfo.ENEMY);
					}

					Zone zo = new Zone();
					zo.setX1(map.roundAndRange(perso.getX() - 16 * 5, Area.ROUND_X));
					zo.setY1(map.roundAndRange(perso.getY() - 16 * 5, Area.ROUND_Y));
					zo.setX2(map.roundAndRange(perso.getX() + 16 * 5, Area.ROUND_X));
					zo.setY2(map.roundAndRange(perso.getY() + 16 * 5, Area.ROUND_Y));
					perso.setZone_deplacement(zo);
					perso.setPv(3);
					perso.setTarget(null);
					perso.setMouvement(MouvementZildo.VIDE);
	
					perso.initPersoFX();

					spriteManagement.spawnPerso(perso);
				}
			}
		}

		// Les Phrases
		int n_phrases = 0;
		if (!p_buffer.eof()) {
			n_phrases = p_buffer.readUnsignedByte();
			if (n_phrases > 0) {
				map.dialogs = new MapDialog();
				// On lit les phrases
				for (int i = 0; i < n_phrases; i++) {
					String phrase = p_buffer.readString();
					map.dialogs.addSentence(phrase);
				}
				if (!p_buffer.eof()) {
					while (!p_buffer.eof()) {
						// On lit le nom
						String nomPerso = p_buffer.readString(9);
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
                            if ((c.getN_banque() & Area.M_MOTIF_MASQUE) == 0) {
                                c.setN_banque_masque(c.getN_banque());
                                c.setN_motif_masque(c.getN_motif());
                                c.setN_banque(c.getN_banque() | Area.M_MOTIF_MASQUE);
                            }
                        }
                    }
                }
            }
        }
    }

	public List<SpriteEntity> filterExportableSprites(List<SpriteEntity> p_spriteEntities) {
		List<SpriteEntity> filteredEntities = new ArrayList<SpriteEntity>();
		for (SpriteEntity entity : p_spriteEntities) {
			int type = entity.getEntityType();
			boolean ok = true;
			if (entity.getEntityType() == SpriteEntity.ENTITYTYPE_ELEMENT) {
				Element elem = (Element) entity;
				if (elem.getLinkedPerso() != null) {
					ok = false;
				}
			}
			if (entity.isZildo()) {
				ok = false;
			}
			if (entity.isVisible() && ok && (type == SpriteEntity.ENTITYTYPE_ELEMENT || type == SpriteEntity.ENTITYTYPE_ENTITY)) {
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

    public void update() {
        for (Iterator<SpawningTile> it = toRespawn.iterator(); it.hasNext();) {
            SpawningTile spawnTile = it.next();
            if (spawnTile.cnt == 0) {
                int x = spawnTile.x * 16 + 8;
                int y = spawnTile.y * 16 + 8;
                // Respawn the tile if nothing bothers at location
                Collision colli=new Collision();
                colli.cr=8;
                if (EngineZildo.mapManagement.collideSprite(x, y, colli)) {
                    spawnTile.cnt++;
                } else {
                    this.set_mapcase(spawnTile.x, spawnTile.y + 4, spawnTile.previousCase);
                    EngineZildo.spriteManagement.spawnSprite(new ElementImpact(x, y, ImpactKind.SMOKE, null));
                    changes.add(new Point(spawnTile.x, spawnTile.y+4));
                    it.remove();
                }
            } else {
                spawnTile.cnt--;
            }
        }
    }
}