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

package zildo.server;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zildo.client.sound.BankSound;
import zildo.fwk.ZUtils;
import zildo.fwk.bank.SpriteBank;
import zildo.fwk.collection.ListMerger;
import zildo.fwk.file.EasyBuffering;
import zildo.monde.collision.Collision;
import zildo.monde.collision.PersoCollision;
import zildo.monde.collision.SpriteCollision;
import zildo.monde.items.ItemKind;
import zildo.monde.map.Area;
import zildo.monde.sprites.Reverse;
import zildo.monde.sprites.Rotation;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.SpriteModel;
import zildo.monde.sprites.SpriteStore;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.desc.EntityType;
import zildo.monde.sprites.desc.SpriteAnimation;
import zildo.monde.sprites.desc.SpriteDescription;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.elements.ElementAnimMort;
import zildo.monde.sprites.elements.ElementClouds;
import zildo.monde.sprites.elements.ElementDynamite;
import zildo.monde.sprites.elements.ElementFireballs;
import zildo.monde.sprites.elements.ElementGear;
import zildo.monde.sprites.elements.ElementGoodies;
import zildo.monde.sprites.elements.ElementHearts;
import zildo.monde.sprites.elements.ElementImpact;
import zildo.monde.sprites.elements.ElementImpact.ImpactKind;
import zildo.monde.sprites.elements.ElementLauncher;
import zildo.monde.sprites.elements.ElementPoison;
import zildo.monde.sprites.elements.ElementSewerSmoke;
import zildo.monde.sprites.elements.ElementSmoke;
import zildo.monde.sprites.elements.ElementStaffs;
import zildo.monde.sprites.elements.ElementStars;
import zildo.monde.sprites.elements.ElementStars.StarKind;
import zildo.monde.sprites.elements.ElementThrown;
import zildo.monde.sprites.elements.ElementWeapon;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
import zildo.monde.util.Pointf;
import zildo.monde.util.Zone;
import zildo.server.state.ClientState;

public class SpriteManagement extends SpriteStore {

	// Items that hero can pick up and throw
	EnumSet<ElementDescription> pickableSprites = EnumSet.of(ElementDescription.DYNAMITE);
	// Items that hero can take into his inventory
	EnumSet<ElementDescription> takableSprites = EnumSet.of(ElementDescription.SPADE);
	
	boolean spriteUpdating;	// TRUE=new sprites are added to SpriteEntitiesToAdd / FALSE=regular list

	List<SpriteEntity> clientSpecificEntities;
	final List<SpriteEntity> spriteEntitiesToAdd; // Used for sprites created during the animation phase

	List<SpriteEntity> suspendedEntities;
	
	List<SpriteEntity> walkableEntities;	// Platforms
	
	ListMerger<SpriteEntity> mergedEntities;	// Just for the 'getNamed*' methods, is a merge of spriteEntities and spriteEntitiesToAdd

	/** See {@link #updateSprites()} **/
	
	// Multiplayer: To identify entities which have been modified in the current frame
	Map<Integer, SpriteEntity> backupEntities; 

	private List<SpriteEntity> toDelete = ZUtils.arrayList();
	
	SpriteCollision sprColli;
	public PersoCollision persoColli;
	
	private boolean temporaryBlocked;
	
	public SpriteManagement() {

		super();

		clientSpecificEntities = new ArrayList<SpriteEntity>();
		spriteEntitiesToAdd = new ArrayList<SpriteEntity>();
		suspendedEntities = new ArrayList<SpriteEntity>();
		backupEntities = new HashMap<Integer, SpriteEntity>();
		sprColli = new SpriteCollision();
		persoColli = new PersoCollision();
		walkableEntities = new ArrayList<SpriteEntity>();
		
		// Sad but at some times, entities being created are either on the first or the second list
		// And for the 'getName*' we need to find the entity wherever it is.
		mergedEntities = new ListMerger<SpriteEntity>(spriteEntities, spriteEntitiesToAdd);

	}

	public Element createElement(SpriteDescription desc, int x, int y, int z, Reverse reverse, Rotation rotation) {
		// SpriteEntity informations
		Element element;
		int nBank = desc.getBank();
		if (nBank == SpriteBank.BANK_GEAR) {
			element = new ElementGear(x, y);
			element.setAjustedX(x);
			element.setAjustedY(y);
		} else if (desc == ElementDescription.DYNAMITE) {
			element = new ElementDynamite(x, y, z, null);
		} else {
    		if (desc == ElementDescription.PORTAL_KEY) {
    			// Handle this in a more generic way
    			element = new ElementGoodies();
    		} else {
    			element = new Element();
    		}
		}
		element.setDesc(desc);
		element.setX(x);
		element.setY(y);
		element.setZ(z);
		element.reverse = reverse;
		element.rotation = rotation;
		
		return element;
	}
	// /////////////////////////////////////////////////////////////////////////////////////
	// spawnElement
	// /////////////////////////////////////////////////////////////////////////////////////
	// IN:nBank, nSpr, x, y
	// /////////////////////////////////////////////////////////////////////////////////////
	// Spawn an element with minimal requirements
	// -build an element with given parameters
	// -add it to the sprite engine
	public Element spawnElement(SpriteDescription desc, int x, int y, int z, Reverse reverse, Rotation rotation) {
		Element element = createElement(desc, x, y, z, reverse, rotation);
		spawnSprite(element);
		return element;
	}

	/**
	 * Spawn a character, and all his linked "persoSprites".
	 * @param perso
	 */
	public void spawnPerso(Perso perso) {
		SpriteEntity entity = perso;
		entity.setScrX((int) perso.x);
		entity.setScrY((int) perso.y);

		spawnSprite(entity);

		// Spawn connected sprites
		if (perso.getPersoSprites().size() != 0) {
			for (Element element : perso.getPersoSprites()) {
				if (element.getId() == -1) {	// Some entities are already spawned, like shadow
					spawnSprite(element);
				}
			}
		}

		// Store walkable entities
		if (ElementDescription.isPlatform(perso.getDesc())) {
			entity.initMover();
			walkableEntities.add(entity);
		}
		
		EngineZildo.persoManagement.addPerso(perso);
	}

	/**
	 * Spawns a generic sprite animation (see note for particular case)
	 * @param typeSprite
	 * @param x
	 * @param y
	 * @param floor floor to spark the animation
	 * @param misc money value (just for GOLDCOIN)
	 * @param miscPerso perso dying (just for DEATH)
	 * @param desc
	 * @return Spawned {@link Element}
	 * Note: Can return NULL (example: ask for a blue drop, but hero hasn't necklace ==> nothing spawned)
	 */
	public Element spawnSpriteGeneric(SpriteAnimation typeSprite, int x, int y, int floor,
			int misc, Perso miscPerso, ElementDescription desc) {
		Element element = null;
		ElementDescription elemDesc = null;
		int j;

		// First of all : check if this is a right place (no collision, and no lava)
		switch (typeSprite) {
			case GOLDCOIN :
			case ARROW :
			case FROMGROUND:
			case BLUE_DROP:
				if (EngineZildo.mapManagement.collide(x, y, miscPerso) && desc == null) {
					return null;
				}
			default:
		}
		switch (typeSprite) {
			case CHIMNEY_SMOKE :
				elemDesc = ElementDescription.SMOKE_SMALL;
				element = new ElementSmoke(x, y);
				element.setZ(6.0f);
				element.setVx(0.2f+0.1f*(float) Math.random());
				element.setVy(0.0f);
				element.setVz(0.0f);
				element.setAx(-0.01f);
				element.setAy(0.0f);
				element.setAz(0.01f); // + rnd()*0.005f);

				element.setSprModel(elemDesc);

				element.setScrX((int) element.x);
				element.setScrY((int) element.y);

				element.setForeground(true);
				break;

			case BUSHES :
				for (j = 0; j < 8; j++) {
					Element e;
					if (misc == 1) {	// Nettle's considered as a goodies
						ElementGoodies goodies = new ElementGoodies();
						goodies.setShadowDesc(ElementDescription.SHADOW_MINUS);
						e = goodies;
					} else {
						e = new Element();
					}
					e.setX((float) (x + Math.random() * 10 - 5));
					e.setY((float) (y + Math.random() * 6 - 2));
					e.setZ((float) (7 + Math.random() * 10));
					e.setVx(0.2f * (j - 1));
					e.setVz((float) (-0.5f + Math.random() * 3 * 0.1f));
					e.setAx(-0.05f * e.getVx());
					if (misc == 1) {
						e.setNSpr(ElementDescription.NETTLE_LEAF.ordinal());
					} else {
						e.setNSpr(ElementDescription.LEAF_GREEN.ordinal());
					}
					if ( (j%2) == 0) {
						e.reverse = Reverse.HORIZONTAL;
					}
					spawnSprite(e);
					// Peut-être qu'un diamant va apparaitre !
				}
				break;

			case GOLDCOIN :
			case ARROW :
			case FROMGROUND :
				// Diamond, arrows, everything coming from ground
				element = new ElementGoodies();
				element.setX(x);
				element.setY(y);
				element.setZ(4.0f);
				element.setVz(1.5f);
				element.setAz(-0.1f);
				ElementDescription shadow = ElementDescription.SHADOW_MINUS;
				if (typeSprite == SpriteAnimation.GOLDCOIN) {
					switch (misc) {
					case 0:
						element.setDesc(ElementDescription.GOLDCOIN1);
						break;
					case 1:
						element.setDesc(ElementDescription.THREEGOLDCOINS1);
						 shadow = ElementDescription.SHADOW;
						break;
					default:
						element.setDesc(ElementDescription.GOLDPURSE1);
						break;
					}
				} else if (typeSprite == SpriteAnimation.FROMGROUND) {
					element.setSprModel(desc);
				} else {
					element.setSprModel(ElementDescription.ARROW_UP);
					element.setY(y - 3);
				}
				((ElementGoodies)element).setShadowDesc(shadow);
				break;

			case BLUE_DROP :
				if (EngineZildo.scriptManagement.isBlueDropDisplayable()) {
					element = new ElementGoodies();
					element.setX(x - 1);
					element.setY(y);
					if (misc == 1) { // Heart should be on the ground
						element.setX(x-3);
						element.setY(y);
						element.setZ(0);
						element.setSprModel(ElementDescription.DROP_FLOOR);
					} else {
						element.setZ(11.0f);
						//element.setVx(0.15f);
						element.setVz(-0.02f);
						element.setAz(-0.01f);
						//element.setAx(-0.01f);
						element.setSprModel(ElementDescription.DROP_SMALL);
					}
					((ElementGoodies)element).setShadowDesc(ElementDescription.SHADOW_MINUS);
				}
				break;

			case DEATH :
				element = new ElementAnimMort(miscPerso);
				element.setX(x);
				element.setY(y);
				element.setZ(8.0f);
				break;

			case BREAKING_ROCK :
				Angle temp = Angle.NORDOUEST;
				for (j = 0; j < 4; j++) {
					Element e = new Element();
					Point move = temp.coords;
					e.setX(x);
					e.setY(y);
					e.setZ(4);
					e.setVx(0.5f * move.x + (float) Math.random() * 0.2f);
					e.setVy(0.5f * move.y + (float) Math.random() * 0.2f);
					e.setVz(1f);
					e.setAz(-0.08f);
					e.setFx(0.04f);
					e.setFy(0.04f);
					e.setNSpr(ElementDescription.TINY_ROCK.ordinal());
					e.setFloor(floor);
					if (j % 2 == 1) {
						e.rotation = Rotation.UPSIDEDOWN;
					}
					e.alphaV = -10;
					spawnSprite(e);

					temp = temp.rotate(1);
				}
				EngineZildo.soundManagement.broadcastSound(BankSound.CassePierre, new Point(x, y));

				break;

			case FROM_CHEST :
				element = new ElementGoodies(miscPerso);
				element.x = x;
				element.y = y;
				element.z = 16;
				element.vx = 0;
				element.vy = 0.0f;
				element.vz = 0.18f;
				element.ax = 0;
				element.fy = 0.005f;
				element.fz = 0.03f;
				element.setDesc(desc);
				break;
			case STAR_CIRCLE:
				element = new ElementStars(StarKind.CIRCLE, x, y);
				break;
			case STAR_SHINE:
				element = new ElementImpact(x, y, ImpactKind.STAR_YELLOW, null);
				element.setForeground(true);
				break;
			case STAR_TRAIL:
				element = new ElementStars(StarKind.TRAIL, x, y);
				break;
			case CLOUD_FOG:
				element = new ElementClouds(x, y);
				EngineZildo.soundManagement.broadcastSound(BankSound.CannonBall, element);
				break;
			case HEARTS:
				element = new ElementHearts(x, y);
				break;
			case ROCKBALL:
				element = new ElementThrown(misc == 0 ? Angle.EST : Angle.OUEST, x, y, 15, 2.9f, null) {
				    @Override
				    public void animate() {
				        super.animate();
				    }					
				};
				element.vy = EngineZildo.hasard.intervalle(0.2f);
				element.vz = 1.2f;
				element.az = -0.1f;
				//element.fx = 0.01f;
				element.setDesc(ElementDescription.ROCK_BALL);
				element.setForeground(true);
				Element sh = element.addShadow(ElementDescription.SHADOW_MINUS);
				sh.z = 0;
				break;
			case STAFF_POUM:
			    	element = new ElementStaffs(x, y);
				break;
			case BIG_FIREBALL:
				element = new ElementFireballs(x, y, Angle.fromInt(misc));
				break;
			case LAVA_DROP:
				element = new ElementImpact(x, y, ImpactKind.LAVA_DROP, null);
				break;
			case DUST:
				element = new ElementImpact(x, y, ImpactKind.DUST, null);
				break;
			case POISONCLOUD:
				element = new ElementPoison(x, y, miscPerso);
				break;
			case WATER_SPLASH:
				element = new ElementImpact(x, y, ImpactKind.WATER_SPLASH, null);
				break;
			case SEWER_SMOKE:
				element = new ElementSewerSmoke(x, y, Angle.fromInt(misc));
				break;
		}
		if (element != null) {
			int f = floor;
			// Find the highest existent floor (for bat dropping their item on an inaccessible floor for player)
			Area area = EngineZildo.mapManagement.getCurrentMap();
			if (area != null && !element.isForeground() && (miscPerso == null || !miscPerso.isZildo())) {	// Don't modify floor for 'foreground' element, like smoke
				// Be careful about the 'currentMap'. If we come from Area.deserialize, currentMap is not updated yet
				while (f > 0) {
					if (EngineZildo.mapManagement.getCurrentMap().get_mapcase(x >> 4, y >> 4, f) != null) break;
					f--;
				}
			}
			element.setFloor(f);
			spawnSprite(element);
		}
		
		return element;
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// spawnSprite
	// /////////////////////////////////////////////////////////////////////////////////////
	// IN:nBank, nSpr, x, y
	// /////////////////////////////////////////////////////////////////////////////////////
	// Spawn a sprite with minimal requirements
	// -build an entity with given parameters
	// -add it to the sprite engine
	
	/**
	 * Spawn a sprite with minimal requirements:<ul>
	 * <li>build an entity with given parameters</li>
	 * <li>add it to the sprite engine</li>
	 * @param x
	 * @param y
	 * @param p_foreground TRUE=above the other sprites (GUI) / FALSE=in-game sprite
	 * @param p_reverse reverse flag
	 * @param p_adjustPos TRUE=center the element / FALSE=no location adjustment
	 */
	public SpriteEntity spawnSprite(SpriteDescription desc, int x, int y,
			boolean p_foreground, Reverse p_reverse, boolean p_adjustPos) {
		SpriteEntity sprite = createSprite(desc, x, y, p_foreground, p_reverse, p_adjustPos);
		if (sprite != null) {	// It can be null just with QUAD1 on multiplayer map (see if we can get rid of this)
			spawnSprite(sprite);
		}
		return sprite;
	}
	
	public SpriteEntity createSprite(SpriteDescription desc, int x, int y,
			boolean p_foreground, Reverse p_reverse, boolean p_adjustPos) {

		if (desc.isPushable() || desc == ElementDescription.STONE_HEAVY) { // || nSpr == 179) {
			// Particular sprite (Block that Zildo can move, chest...)
			return createElement(desc, x, y, 0, Reverse.NOTHING, Rotation.NOTHING); // + spr.getTaille_y() / 2 - 3,
					//0);
		}

		// SpriteEntity informations
		SpriteEntity entity;
		ElementDescription elemDesc=null;
		if (desc instanceof ElementDescription) {
			elemDesc=(ElementDescription) desc;
		}
		boolean weapon = false;
		if (elemDesc != null) {
			ItemKind kind = ItemKind.fromDesc(elemDesc);
			weapon = kind != null && kind.isWeapon();
		}
		if (!p_foreground && weapon) {
			entity = new ElementWeapon(x, y);
		} else if (desc == ElementDescription.LAUNCHER1) {
			entity = new ElementLauncher(x, y);
			entity.setAjustedX(x);
			entity.setAjustedY(y);
		} else if (desc.getBank() == SpriteBank.BANK_GEAR && !desc.isOnGround()) {
			entity = new ElementGear(x, y);
			entity.setAjustedX(x);
			entity.setAjustedY(y);
		} else if (desc == ElementDescription.QUAD1) {
			EngineZildo.multiplayerManagement.spawnQuad(x, y);
			return null;
		} else if (desc == ElementDescription.CAULDRON1) {
			entity = new ElementImpact(x, y - 4, ImpactKind.CAULDRON, null);
			entity.z = 0;	// Default is z=4 for ElementImpact
		} else {
		    entity = new SpriteEntity(x, y, true);
		    int adjustX = 0;
		    int adjustY = 0;
		    SpriteModel spr = getSpriteBank(desc.getBank()).get_sprite(desc.getNSpr());
		    adjustX = -(spr.getTaille_x() >> 1);
		    if (p_adjustPos) {
		    	adjustY = -(spr.getTaille_y() >> 1);
		    } else {
		    	adjustY = -spr.getTaille_y();
		    }
		    entity.setAjustedX(x + adjustX);
		    entity.setAjustedY(y + adjustY);
		}
		
		entity.setDesc(desc);
		entity.setForeground(p_foreground);

		entity.reverse=p_reverse;
		
		// Store walkable entities
		if (ElementDescription.isPlatform(desc)) {
			entity.initMover();
			walkableEntities.add(entity);
		}
		
		return entity;
	}

	@Override
	protected void addSpriteEntities(SpriteEntity p_entity) {
		if (!spriteUpdating) {
			spriteEntities.add(p_entity);
		} else {
			spriteEntitiesToAdd.add(p_entity);
		}
	}

	@Override
	protected boolean removeEntity(SpriteEntity entity) {
		boolean res = super.removeEntity(entity);
		if (!res) {
			res = spriteEntitiesToAdd.remove(entity);
		}
		return res;
	}
	
	/** Delete entity. Update {@link PersoManagement#tab_perso} if needed, and {@link SpriteManagement#walkableEntities} if needed. **/
	public void deleteSprite(SpriteEntity entity) {
		// Update walkable entities
		if (walkableEntities.contains(entity)) {
			walkableEntities.remove(entity);
		}

		super.deleteSprite(entity);
	}
	
	/** Update collision buffers for sprites and characters **/
	public void updateCollisions() {
		sprColli.initFrame(spriteEntities);
		persoColli.initFrame(EngineZildo.persoManagement.tab_perso);		
	}
	
	/**
	 * Do sprite's stuff<ul>
	 * <li>animate sprites & persos<li>
	 * <li>delete if need (the only place to do this)</li></ul>
	 * @param p_blockMoves TRUE=don't animate perso except Zildo
	 */
	public void updateSprites(boolean p_blockMoves) {
		spriteUpdating = true;
		spriteEntitiesToAdd.clear();
		
		// Backup current entities, if backup buffer is empty
		if (EngineZildo.game.multiPlayer && backupEntities.size() == 0) {
			for (SpriteEntity entity : spriteEntities) {
				SpriteEntity cloned = entity.clone();
				backupEntities.put(cloned.getId(), cloned);
			}
		}

		updateCollisions();

		boolean blockNPC = p_blockMoves || temporaryBlocked;
		
		// Do perso animations
		// Mandatory to do that first, because one perso can be connected to
		// other sprites
		int compt=EngineZildo.nFrame; // % (3 * 20);
		for (SpriteEntity entity : spriteEntities) {
			if (entity.getEntityType().isPerso()) {
				Perso perso = (Perso) entity;
				// TODO: actually we allow moving for character following (Igor or mole) JUST in purpose
				// of moving map: so Igor can move during map is scrolling. But this shouldn't happen during
				// in-game menu
				boolean allowedToMoveAndCollide = !blockNPC || /*perso.getInfo() == PersoInfo.ZILDO ||*/ perso.getFollowing() != null;
				if (allowedToMoveAndCollide) {
					// Animate persos
					perso.animate(compt);
				}
				perso.finaliseComportement(compt);
				updateSprModel(perso);
				SpriteModel spr = perso.getSprModel();
				if (allowedToMoveAndCollide) {
					perso.manageCollision();
				}
				
				if (!perso.isZildo()) {
					// Non-zildo sprite haven't same way to display correctly (bad...)
					int ajX = perso.getAjustedX();
					int ajY = perso.getAjustedY() - (spr.getTaille_y() - 3);
					if (spr.getEmptyBorders() != null) {
						Zone offsets = spr.getEmptyBorders();
						if (!perso.reverse.isHorizontal()) {
							ajX -= (spr.getTaille_x()+offsets.x1 +offsets.x2 )/2;
							ajX += offsets.x1;	// Shift because between [x,x1] this is an empty border
						} else {
							ajX -= (spr.getTaille_x()-offsets.x2+offsets.x1)/2;
						}
						ajY += offsets.y1;
					} else {
						ajX -= spr.getTaille_x() / 2;
					}
					perso.setAjustedX(ajX);
					perso.setAjustedY(ajY);
				}
			}
		}

		// Remove entities marked as 'dying' in previous frame
		for (SpriteEntity entity : toDelete) {
			deleteSprite(entity);
		}
		
		toDelete.clear();
		for (SpriteEntity entity : spriteEntities) {
			if (toDelete.contains(entity) || suspendedEntities.contains(entity)) {
				continue; // It's a dead one
			}
			Element element = null;
			// Calcul physique du sprite
			if (entity.dying) {
				toDelete.add(entity);
			} else if (entity.getEntityType().isEntity() && !p_blockMoves) {
				entity.animate();
			} else if (entity.getEntityType().isFont()) {
				// Particular case: animate and set screen coordinates
				entity.animate();
				entity.setScrX((int) entity.x);
				entity.setScrY((int) entity.y);
			} else if (entity.getEntityType().isElement()) {
				// X, vX, aX, ...
				element = (Element) entity;
				if (!blockNPC || element.isLinkedToZildo()) {
					element.animate();
					if (element.dying) {
						SpriteEntity linkedOne = element.getLinkedPerso();
						// L'élément est arrivé au terme de son existence : on le supprime de la liste
						if (linkedOne != null && EntityType.ELEMENT == linkedOne.getEntityType()) {
							toDelete.add(linkedOne);
						}
						toDelete.add(element);
					} else {
						if (element.isVisible()) {
							updateSprModel(element);
						}
					}
				}
			}
		}

		// Notify collision engine (and entities will be really removed next frame, in order to notify SpriteDisplay)
		for (SpriteEntity entity : toDelete) {
			if (entity.getEntityType().isPerso()) {
				persoColli.notifyDeletion((Perso) entity);
			} else if (entity.getId() != -1){
				sprColli.notifyDeletion(entity);
			}
		}

		spriteUpdating = false;
		spriteEntities.addAll(spriteEntitiesToAdd);
	}

	/** Update SpriteModel, which used to display the correct texture part, according to nBank, nSpr and addSpr **/
	private void updateSprModel(Element element) {
		SpriteModel spr = getSpriteBank(element.getNBank())
			.get_sprite(element.getNSpr() + element.getAddSpr());
		element.setSprModel(spr);		
	}
	
	private void updateSprModel(Perso perso) {
		SpriteModel spr = getSpriteBank(perso.getNBank())
			.get_sprite(perso.getNSpr() );
		perso.setSprModel(spr);		
	}
	
	public void updateSprModel(SpriteEntity entity) {
		SpriteModel spr = getSpriteBank(entity.getNBank())
			.get_sprite(entity.getNSpr());
		entity.setSprModel(spr);		
	}
	
	// /////////////////////////////////////////////////////////////////////////////////////
	// clearSpritesWithoutZildo
	// /////////////////////////////////////////////////////////////////////////////////////
	// -Delete every sprites in the entities list
	// -Clean the sort array
	// -Reinitializes local camera
	// /////////////////////////////////////////////////////////////////////////////////////
	public void clearSprites(boolean includingZildo) {
		// Get Zildo to avoid to remove it
		Perso zildo = EngineZildo.persoManagement.getZildo();

		// Destroy entities
		List<SpriteEntity> listToRemove = new ArrayList<SpriteEntity>();

		for (SpriteEntity entity : spriteEntities) {
			if (entity != null) {
				boolean canDelete = true;
				if (entity == zildo) {
					// This IS Zildo ! So we keep him
					canDelete = includingZildo;
				} else if (entity.getEntityType().isElement()) {
					Element element = (Element) entity;
					if (zildo != null && element.getLinkedPerso() == zildo) {
						// This is an element related to zildo, so we can't
						// remove it now
						canDelete = includingZildo;
					}
				} else if (entity.getEntityType().isPerso()) {
					canDelete = false;
				}
				if (canDelete) {
					listToRemove.add(entity);
				}
			}
		}

		for (SpriteEntity entity : listToRemove) {
			deleteSprite(entity);
		}
		
		walkableEntities.clear();
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// collideSprite
	// /////////////////////////////////////////////////////////////////////////////////////
	// -Return TRUE wether the given sprite collide one BLOCKING element/entity.
	// -Do appropriate stuffs wether character encounters a sprite (bonus,
	// item...)
	// /////////////////////////////////////////////////////////////////////////////////////
	public boolean collideSprite(int tx, int ty, Element elem) {
		return sprColli.checkCollision(tx, ty, elem);
	}
	
	/**
	 * Find an element near a given one.<br/>
	 * WARNING: it's an unoptimized method, contrary to {@link #collideSprite(int, int, Element)}, but it's used only once when
	 * player press action key. So it's acceptable now.
	 * @param x
	 * @param y
	 * @param quelElement
	 * @param radius
	 * @return Element
	 */
    public Element collideElement(int x, int y, Element quelElement, int radius, SpriteDescription... expectedDesc) {
        Perso perso = null;
        if (quelElement != null && quelElement.getEntityType().isPerso()) {
            perso = (Perso) quelElement;
        }

        for (SpriteEntity entity : spriteEntities) {
        	if (entity.getEntityType().isElement() && !entity.dying) {
	            if (entity != quelElement) {
	                int tx = (int) entity.x;
	                int ty = (int) entity.y;
	                if (Collision.checkCollisionCircles(x, y, tx, ty, radius, radius)) {
	                    if (perso != null && perso.isZildo() && perso.linkedSpritesContains(entity)) {
	                    	// Collision between hero and object he's carrying => let it go
	                    } else if (quelElement == null || quelElement.getLinkedPerso() != entity) {
	                    	// Check that found element is one of expected ones (and it's not dying)
	                    	if (expectedDesc != null && expectedDesc.length > 0) {
	                    		for (SpriteDescription sDesc : expectedDesc) {
	                    			if (sDesc == entity.getDesc()) {
	                    				return (Element) entity;
	                    			}
	                    		}
	                    		continue;	// Check next one
	                    	}
	                    	Element elem = (Element) entity;
	                    	// Found an element, but is it really the most pertinent ? (ex:shadow)
	                    	if (elem.getLinkedPerso() != null) {
	                    		return elem.getLinkedPerso();
	                    	}
	                        return elem;
	                    }
	                }
	            }
        	}
        }
        return null;
    }
    
	/**
	 * Get a SpriteEntity list from a ByteBuffer.
	 * 
	 * @param p_buffer
	 * @return List<SpriteEntity>
	 */
	public static List<SpriteEntity> deserializeEntities(EasyBuffering p_buffer) {
		List<SpriteEntity> entities = new ArrayList<SpriteEntity>();
		while (!p_buffer.eof()) {
			entities.add(SpriteEntity.deserialize(p_buffer));
		}
		return entities;
	}

	/**
	 * Serialize every entities into one unique ByteBuffer.
	 * 
	 * @param p_entire
	 *            TRUE=client want all entities / FALSE=just send delta
	 * @return buffer
	 */
	public EasyBuffering serializeEntities(boolean p_entire) {
		List<SpriteEntity> s = spriteEntities;
		if (!p_entire) {
			s = getModifiedEntities();
		}
		backupEntities.clear();
		return serializeEntities(s, false);
	}

	/**
	 * Serialize given entities into one unique ByteBuffer.
	 * 
	 * @param p_listEntities
	 *            entities to serialize
	 * @param p_clientSpecific
	 *            FALSE=serialize only the common entities
	 * @return buffer
	 */
	public EasyBuffering serializeEntities(List<SpriteEntity> p_listEntities,
			boolean p_clientSpecific) {
		EasyBuffering b = new EasyBuffering();
		for (SpriteEntity entity : p_listEntities) {
			if (!entity.clientSpecific || p_clientSpecific) {
				entity.serialize(b);
			}
		}
		return b;
	}

	/**
	 * Return sprite entities according to the given client. SpriteEntity which
	 * have 'clientSpecific' at TRUE are only transmitted if we have the right
	 * client.
	 * 
	 * @param p_cl
	 * @return List<SpriteEntity>
	 */
	public List<SpriteEntity> getSpriteEntities(ClientState p_cl) {
		// Filter the entities to keep only the common and those from given client
		// TODO: parameter isn't used anymore, but this should cause problem in multiplayer maybe. We keep it until we get through this.
		List<SpriteEntity> entities = new ArrayList<SpriteEntity>();
		for (SpriteEntity entity : spriteEntities) {
			if (!entity.clientSpecific) {
				entities.add(entity);
			}
		}
		return entities;
	}

	/**
	 * Prepare a list of modified entities on this frame.
	 * 
	 * @return List<SpriteEntity>
	 */
	private List<SpriteEntity> getModifiedEntities() {
		List<SpriteEntity> returned = new ArrayList<SpriteEntity>();
		for (SpriteEntity entity : spriteEntities) {
			int id = entity.getId();
			SpriteEntity backedUp = backupEntities.get(id);
			if (backedUp == null) {
				// If this entity hasn't been backed up, it's a new one ==> we
				// send it
				returned.add(entity);
			} else {
				// If this entity has been backed up, we must check whether it
				// has changed
				if (!backedUp.isSame(entity)) {
					returned.add(entity);
				}
				backupEntities.remove(id);
			}
		}
		// Send the entity which has been removed this frame
		for (SpriteEntity entity : backupEntities.values()) {
			entity.dying = true;
			entity.clientSpecific = false; // Send to all clients
			returned.add(entity);
		}
		backupEntities.clear();
		return returned;
	}
	
	/**
	 * Returns TRUE if the given entity is already spawned.
	 * @param p_entity
	 * @return boolean
	 */
	public boolean isSpawned(SpriteEntity p_entity) {
	    return p_entity.getLinkVertices() != 0 || p_entity.getScrX() + p_entity.getScrY() > 0;
	}
	
	/**
	 * Translate every entities with the given offset.
	 * @param p_offset
	 * @param p_translateZildo TRUE=Translate Zildo too / FALSE=Don't touch him
	 */
	public void translateEntities(Point p_offset, boolean p_translateZildo) {
	    for (SpriteEntity entity : spriteEntities) {
	    	if (!entity.clientSpecific && (!entity.isZildo() || p_translateZildo)) {
	    		if (entity.getEntityType().isElement()) {
	    			Element e=(Element) entity;
	    			if (e.getLinkedPerso() != null && e.getLinkedPerso().isZildo()) {
	    				continue;
	    			}
	    		}
	    		entity.x+=p_offset.x;
	    		entity.y+=p_offset.y;
	    		entity.setAjustedX(entity.getAjustedX() + p_offset.x);
	    		entity.setAjustedY(entity.getAjustedY() + p_offset.y);
	    		
	    		// Shift also the target of the character, if any
	    		if (entity.getEntityType().isPerso()) {
	    			Perso perso = (Perso) entity;
	    			if (perso.getTarget() != null) {
	    				perso.getTarget().add(p_offset);
	    			}
	    		}
	    	}
	    }
	}
	
	/** Called twice around the map loading process. Once with FALSE, and second time with TRUE, when map is fully loaded. **/
	public void notifyLoadingMap(boolean p_loading, boolean p_scroll) {
		spriteUpdating=p_loading;
		if (!p_loading) {
			// Loading is over. We have to keep all current entities in order to delete
			// them at the end of the scroll.
			// Except those are currently manipulated by script (ghost is TRUE)
			for (SpriteEntity entity : spriteEntities) {
				// Keep entity which are moving with running scripts
				// If they belong to the previous map, they will be removed when scroll is over
				if (entity.isGhost()) {
					continue;
				} else if (p_scroll) {
					suspendedEntities.add(entity);
				}
			}
			spriteEntities.addAll(spriteEntitiesToAdd);
			spriteEntitiesToAdd.clear();
		}
	}
	
	/** Called when map scroll is over: to remove all entities from previous map **/
	public void clearSuspendedEntities() {
		List<SpriteEntity> toRemove = new ArrayList<SpriteEntity>();
		for (SpriteEntity entity : suspendedEntities) {
			if (entity != null && !entity.isZildo()) {
				toRemove.add(entity);
			}
		}
		suspendedEntities.clear();
		// Now check all entities out of the current map (exception those following someone on current map)
		Area map = EngineZildo.mapManagement.getCurrentMap();
		for (SpriteEntity entity : spriteEntities) {
			if (!entity.isZildo() && map.isOutside((int) entity.x, (int) entity.y)) {
				if (entity.getEntityType().isPerso()) {
					Perso p = (Perso) entity;
					Pointf target = p.getTarget();
					if (p.getTarget() == null || map.isOutside((int) target.x, (int) target.y)) {
						toRemove.add(entity);
					}
				}
			}
		}
		// Now remove all gathered entities, taking care of walkable ones
		for (SpriteEntity entity : toRemove) {
			// Check if this entity is an element linked to Zildo
			if (entity.getEntityType().isElement() && ((Element)entity).isLinkedToZildo()) {
				continue;
			}
			if (entity.getEntityType().isPerso()) {
				EngineZildo.scriptManagement.stopPersoAction((Perso) entity);
			}
			deleteSprite(entity);
			if (walkableEntities.contains(entity)) {
				walkableEntities.remove(entity);
			}
		}
	}
	
	/**
	 * Returned the element with given name.
	 * @param p_name
	 * @return element
	 */
    public Element getNamedElement(String p_name) {
        if (p_name != null && !"".equals(p_name)) {
            for (SpriteEntity p : mergedEntities) {
            	if (p.getEntityType() == EntityType.ELEMENT && p_name.equalsIgnoreCase(p.getName())) {
                    return (Element) p;
                }
            }
        }
        return null;
    }
    
	/**
	 * Returned the entity with given name.
	 * @param p_name
	 * @return element
	 */
    public SpriteEntity getNamedEntity(String p_name) {
        if (p_name != null && !"".equals(p_name)) {
            for (SpriteEntity p : mergedEntities) {
                if (p_name.equalsIgnoreCase(p.getName())) {
                    return p;
                }
            }
        }
        return null;
    }
    
	/**
	 * Returned the entity with given name.
	 * @param p_name
	 * @return element
	 */
    public List<SpriteEntity> getNamedEntities(String p_name) {
    	List<SpriteEntity> result = new ArrayList<>();
        if (p_name != null && !"".equals(p_name)) {
            for (SpriteEntity p : mergedEntities) {
                if (p_name.equalsIgnoreCase(p.getName())) {
                    result.add(p);
                }
            }
        }
        return result;
    }
    
	/**
	 * Return the first discovered character inside a circular zone around a given character.
	 * @param p_looker Looking character
	 * @param radius radius of the circular zone
	 * @return Perso
	 */
	public SpriteEntity lookFor(Perso p_looker, int radius, ElementDescription desc, boolean sight) {
		if (p_looker != null) {
			for (SpriteEntity s : spriteEntities) {
				if (s != p_looker && s.getDesc() == desc && s.visible) {
					double distance = Point.distance(p_looker.x,  p_looker.y, s.x, s.y);
					if (distance < radius * 16) {
						// Check sight (if needed)
						if (sight && !p_looker.isFacing(s)) continue;
						return s;
					}
				}
			}
		}
		return null;
	}
    public List<SpriteEntity> getWalkableEntities() {
    	return walkableEntities;
    }
    
    public void blockNonHero() {
    	temporaryBlocked = true;
    }
    
    public void unblockNonHero() {
    	temporaryBlocked = false;
    }

    public boolean isBlockedNonHero() {
    	return temporaryBlocked;
    }
    
    /**
     * Called just after map has been loaded.
     */
    public void initForNewMap() {
    	sprColli.clear();
    	persoColli.clear();
    	unblockNonHero();
    }
}