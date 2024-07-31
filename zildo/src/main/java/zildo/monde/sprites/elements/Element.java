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

package zildo.monde.sprites.elements;

import zildo.client.sound.BankSound;
import zildo.fwk.ZMaths;
import zildo.fwk.bank.SpriteBank;
import zildo.fwk.script.context.LocaleVarContext;
import zildo.fwk.script.context.SceneContext;
import zildo.fwk.script.xml.element.TriggerElement;
import zildo.monde.Trigo;
import zildo.monde.collision.Collision;
import zildo.monde.collision.DamageType;
import zildo.monde.items.ItemKind;
import zildo.monde.map.Area;
import zildo.monde.map.Tile.TileNature;
import zildo.monde.sprites.Reverse;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.SpriteModel;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.desc.EntityType;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.desc.SpriteAnimation;
import zildo.monde.sprites.desc.SpriteDescription;
import zildo.monde.sprites.elements.ElementImpact.ImpactKind;
import zildo.monde.sprites.persos.ControllablePerso;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.sprites.utils.MouvementPerso;
import zildo.monde.sprites.utils.SoundGetter;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
import zildo.monde.util.Pointf;
import zildo.monde.util.Vector2f;
import zildo.monde.util.Zone;
import zildo.server.EngineZildo;
import zildo.server.MapManagement;

//TODO: Remove getter/setter for x,y,z

public class Element extends SpriteEntity {

	// Class variables
	private float ancX, ancY, ancZ;
	public float ax, ay, az;
	public float vx, vy, vz;
	public float fx, fy, fz; // Frottements
	protected int spe; // Spe est utilisé selon l'usage
	protected Angle angle;
	public boolean flying;

	public int relativeZ; // Simulate the altitude delta
	protected int addSpr; // Pour les animations (exemple:diamants qui brillent)
	protected Element linkedPerso; // When this element dies, any non-perso
									// linked entity die too.
	protected SpriteEntity linkedEntity;	// To handle case when an invisible element represents an entity (burnable things)
	private Collision permanentCollision = null;	// Example: burning fire for candles, hearth ...
	
	protected Element burningFire;
	private int timeBurning;
	static final int MAX_TIME_BURNING = 200;
	
	protected boolean questTrigger;	// TRUE=taking this element leads to a quest accomplishment
	
	protected Element shadow;
	protected boolean pushable;
	
	public Point defaultSize = new Point(8, 4);
	
	// Scenes to trigger when element respectively 1) reach its target or 2) execute its fall() method
	public String targetScene;
	public String fallScene;
	
	// Soud shuffle for drops
	private static final SoundGetter waterDropSound = new SoundGetter(BankSound.Goutte1, BankSound.Goutte3, 100, true);

	public Element() {
		super();
		this.initialize();
	}

	public Element(int id) {
		super(id);
	}
	
	public Pointf getDelta() {
		return new Pointf(x-ancX, y-ancY);
	}
	
	private void initialize() {
		entityType = EntityType.ELEMENT;

		// Default Bank & Spr
		nBank = SpriteBank.BANK_ELEMENTS;
		nSpr = 0;
		addSpr = 0;

		linkedPerso = null;

		// Default physical value;
		ax = 0.0f;
		ay = 0.0f;
		az = 0.0f;

		vx = 0.0f;
		vy = 0.0f;
		vz = 0.0f;

		x = 0.0f;
		y = 0.0f;
		z = 0.0f;

		fx = 0.0f;
		fy = 0.0f;
		fz = 0.0f;

		flying = false;

		// logger.log(Level.INFO, "Creating Element");
	}

	// Copy constructor
	public Element(Element original) {
		this.x = original.x;
		this.vx = original.vx;
		this.ax = original.ax;
		this.y = original.y;
		this.vy = original.vy;
		this.ay = original.ay;
		this.z = original.z;
		this.vz = original.vz;
		this.az = original.az;
		this.spe = original.spe;
		this.addSpr = original.addSpr;
		this.clientSpecific = original.clientSpecific;
		this.nSpr = original.nSpr;
		this.setSprModel(original.getSprModel());
		this.linkedPerso = original.linkedPerso;
		this.nBank = original.nBank;
		this.foreground = original.foreground;
		this.setDesc(original.desc);
		this.alpha = original.alpha;
		this.floor = original.floor;
		
		this.entityType = EntityType.ELEMENT;
		// logger.log(Level.INFO, "Copying Element");

	}

	public void destroy() {
		linkedPerso = null;
	}

	public void setPos(Vector2f v) {
		x = v.x;
		y = v.y;
	}
	
	public void setSpeed(Vector2f v) {
		vx = v.x;
		vy = v.y;
	}
	
	public void setFriction(Vector2f v) {
		fx = v.x;
		fy = v.y;
	}
	
	/**
	 * Let's do the physical law job.
	 */
	protected void physicMove() {
		// On conserve les anciennes positions pour les collisions
		ancX = x;
		ancY = y;
		ancZ = z;
		vx = (vx + ax) * (1 - fx);
		vy = (vy + ay) * (1 - fy);
		vz = (vz + az) * (1 - fz);
		x = x + vx;
		y = y + vy;
		z = z + vz;
		if (z < 0) { // L'objet tombe au sol
			z = 0;
			vz = 0;
			az = 0;
			fx *= 4;
			fy *= 4;
			fz *= 4;
			fall();
		}
	}

	/**
	 * Returns TRUE if element is considered to collide with environment.
	 * 
	 * @return boolean
	 */
	public boolean isSolid() {
		boolean entitySolid = super.isBlocking();
		if (entitySolid) {
			return true;
		}
		if (desc != null) {
			if (desc instanceof ElementDescription) {
				// Volatile elements (need to be refactored, with a real attribute on Descriptions)
				switch ((ElementDescription) desc) {
					case REDSPHERE1:
					case FIRE_BALL:
					case BROWNSPHERE1:
					case SEWER_SMOKE1:
					case SEWER_SMOKE2:
					//case WILL_O_WIST:
						return false;
					case WATER_LEAF:
						return true;
					default:
				}
			}
			if (desc.isDamageable()) {
				return true;
			}
		}
		// S'il s'agit d'un personnage
		if (entityType.isPerso()) {
			return true;
		}
		return false;
	}

	/**
	 * Move object, and stop it in case of collision.
	 * 
	 * @return boolean
	 */
	public boolean physicMoveWithCollision() {
		if (ax == 0f && ay == 0f && az == 0f && vx == 0f && vy == 0f && vz == 0f) {
			return false;
		}
		physicMove();
		if (isSolid() || desc.isPushable()) {
			SpriteEntity linked = this.getLinkedPerso();
			boolean partOfPerso = false;
			if (linked != null && linked.getEntityType().isPerso()) {
				Perso perso = (Perso) this.getLinkedPerso();
				partOfPerso = perso == null ? false : perso
						.linkedSpritesContains(this);
			}
			if (!desc.isPushable()) {	// Sprite being pushed by hero SHALL NOT hurt him !
				manageCollision();
			}
			int subY = getSprModel().getTaille_y() >> 1;
			// TODO: it would be good to squeeze collision check for element not involved into, like sewer smoke
			// or any animation sprite
			if (!partOfPerso && EngineZildo.mapManagement.collide(x, y-subY, this)) {
				if (desc.isSliping()) {
					float movedX = x; float movedY = y-subY;
					x = ancX; y = ancY - subY;
					Pointf loc = tryMove(movedX - x, movedY - y);
					Pointf diff = new Pointf(loc.x - ancX, loc.y - ancY + subY);
					if (diff.x != vx || diff.y != vy) {
						vx = diff.x*0.9f;
						vy = diff.y*0.9f;
					}
					x = loc.x;
					y = loc.y + subY;
				} else {
					// Stops the movement, just let the element falling
					x = ancX;
					y = ancY;
					//z = ancZ;
					vx = 0;
					vy = 0;
					return true;
				}
			}
		}
		// Out of the map
		Area map = EngineZildo.mapManagement.getCurrentMap();
		if (map.isOutside((int) x, (int) y) && !isOutsidemapAllowed()) {
			return true;
		}
		return false;
	}

	/**
	 * Animate the current element, and we expect that arranged position will be stored in 
	 * ajustedX and ajustedY.<br/>
	 * Be careful : we can animate an element which is declared invisible (by
	 * VISIBLE boolean).
	 */
	@Override
	public void animate() {
		// Perso pnj;
		boolean colli;

		// Alpha channel evolution
		alphaV += alphaA;
		alpha += alphaV;
		if (shadow != null) {	
			if (z > 10) {
				// Enhancement: display shadow with reduced alpha if element is very high (z > 10)
				shadow.alpha = 255 - Math.max(0, (z-10) * 3);
			} else {
				shadow.alpha = alpha;
			}
		}
		
		if (alpha < 0 && fall()) {
			die();
		}
		if (mover != null && mover.isActive()) {
			// Moving is delegated to another object
			mover.reachTarget();
		} else {
			
			// Si ce sprite est valide, est-il un sprite fixe ?
			if (getDesc().isNotFixe()) {
				// On a trouvé un sprite valide non fixe
				// On calcule sa nouvelle position absolue
				colli = physicMoveWithCollision();
	
				// Specific animations
				if (desc == ElementDescription.BULLET) {
					// Rotate the bullet
					rotation = rotation.succ();
				} /*else 	if (nSpr >= 44 && nSpr <= 47) { // Sprite d'animation
					// Morceaux de pierres
					z = z - vz; // On revient en arrière
					vz = vz - az;
					az = az - 1;
					if (az == 0) {
						dying = true;
					}
				} */
				// Débordement
				Area area = EngineZildo.mapManagement.getCurrentMap();
				if (x < -4 || y < -4 || x > 16 * (area.getDim_x()) || (y-z) > 16 * (area.getDim_y())) {
					if (!isOutsidemapAllowed() && !isLinkedToZildo()) {
						die();
						dying = true;
					}
				} else {
	
					if (desc.isPushable()) {
						z = ancZ; // z-vz;
						vz = vz - az;
						if (az != 0) {
							if (colli || az == 31) {	// field 'az' is used as a counter from 0 to 32 with a speed of 0.5 => 16 pixels movement
								vx = 0;
								vy = 0;
								az = 0;
								vz = 0;
								// Trigger a push event
								// Trigger an object being pushed
								TriggerElement trig = TriggerElement.createPushTrigger(name, angle);
								EngineZildo.scriptManagement.trigger(trig);
								// Break link with pusher
								if (getLinkedPerso() != null) {
									((PersoPlayer)linkedPerso).pushSomething(null);
									setLinkedPerso(null);
								}
							} else {
								az = az + 1;
							}
						}
					} else if (!isGoodies() && ((z < 4 && vz != 0.0f) || colli)) {
						if (!beingCollided(null) && fall()) {
							// Sprite must 'die'
							die();
						}
					}
				}
			}
			if (isSolid() || flying) {// Tous les sprites n'entrent pas en collision
				// On teste la collision avec le décor
				if (false && nSpr == 42) {
					// Collision avec Zildo}
					z = z - vz;
					/*
					 * colli=collide(round(x+vx),round(y+vy-z),round(vz)); with
					 * tab_colli[n_colliseur] do begin
					 * cx=round(x)-camerax;cy=round(y)-round(z)-cameray; cr=8;
					 * n_colliseur++; if (colli) {
					 * //spawnsprite_generic(SPR_ECLATEPIERRE,round(x),round(y),0);
					 * }
					 */
				} else if (!isGoodies() && desc.isDamageable()) {
					if (desc.isNotFixe() && isSolid() && !desc.isPushable()) {
						// Collision already handled in physicMoveWithCollision (OPTIMIZATION)
					} else {
						// Collision with enemies (only when object is moving)
						Collision collision = getCollision();
						if (vx != 0 || vy != 0 || vz != 0 || collision != null) {
							manageCollision();
						}
					}
				}
			}
			if (shadow != null) {
				shadow.x = x;
				shadow.y = y - 1;
			}
			if (burningFire != null) {
				burningFire.x = x;
				burningFire.y = y;
				timeBurning++;
				if (desc.isBurnable()) {
					if (timeBurning >= MAX_TIME_BURNING) {
						EngineZildo.scriptManagement.execute("spawnAshes("+x+","+y+")", false, new SceneContext(), null);
						die();
					} else {
						SpriteEntity entity = linkedEntity == null ? this : linkedEntity;
						entity.setLight(ZMaths.ratioLight(0xffeedd, 1f - (float) timeBurning / MAX_TIME_BURNING)); //(int) (timeBurning * 0xffeedd / MAX_TIME_BURNING));
					}
				}
			}
		}
		
		// Animated sprites
		if (desc == ElementDescription.CANDLE1) {
			addSpr = (spe++ % (3*8)) / 8;
		}
		setAjustedX((int) x);
		setAjustedY((int) y);
	}

	/**
	 * Add to the engine the {@link Collision} object representing the region of
	 * this element.
	 * <p/>
	 * There's two cases:<br/>
	 * -<b>Element</b>: collision's perso is related to this element's linked
	 * Perso<br/>
	 * . The element is the weapon.<br/>
	 * -<b>Perso</b>: collision's is related to this Perso, with no weapon.
	 */
	public void manageCollision() {
		Collision collision = getCollision();
		if (permanentCollision != null && permanentCollision.weapon != null) {
			collision.cx = (int) permanentCollision.weapon.x;
			collision.cy = (int) permanentCollision.weapon.y;
		}
		// Default, collision from element is related to the linked Perso
		Element linked = linkedPerso;
		Element weapon = this;
		if (getEntityType().isPerso()) {
			// If Element we're handling is a Perso, adjust infos
			linked = this;
			weapon = null;
		} else {
			// Check if this is a real weapon
    		ItemKind kind = ItemKind.fromDesc(desc);
    		if (!desc.isDamageable() && (kind == null || !kind.isWeapon())) {
    			weapon = null;
    		}
		}
		SpriteModel model = getSprModel();
		if (collision == null) {
			// Default collision: heuristic with a radius of (sizeX + sizeY) / 4
			// We integrate sprite offset too with emptyBorders
			int cx = (int) x;
			int cy = (int) y;
			Zone z = model.getEmptyBorders();
			if (z != null) {
				cy += z.y1;
			}
			int radius = (model.getTaille_x() + model.getTaille_y()) / 4;
			collision = new Collision(cx, cy, radius, Angle.NORD,
					(Perso) linked, getDamageType(), weapon);
		}

		if (permanentCollision == null || permanentCollision.weapon != null) {
			collision.cy -= model.getTaille_y() / 2;
			collision.cy -= z;
		}
		EngineZildo.collideManagement.addCollision(collision);
	}

	//TODO: this should be removed, and replaced by setDesc
	public void setSprModel(ElementDescription p_desc) {
		this.setNBank(SpriteBank.BANK_ELEMENTS);
		this.setNSpr(p_desc.ordinal());
		this.setSprModel(EngineZildo.spriteManagement.getSpriteBank(nBank)
				.get_sprite(p_desc.ordinal()));
		//desc = p_desc;
	}

	public void setSprModel(ElementDescription p_desc, int p_addSpr) {
		setSprModel(p_desc);
		addSpr = p_addSpr;
		setSprModel(EngineZildo.spriteManagement.getSpriteBank(nBank)
				.get_sprite(p_desc.ordinal() + p_addSpr));
	}

	public void setSprModel(PersoDescription p_desc, int p_addSpr) {
		setNBank(p_desc.getBank());
		setNSpr(p_desc.nth(p_addSpr));
		//addSpr = p_desc.nth(p_addSpr);
		setSprModel(EngineZildo.spriteManagement.getSpriteBank(nBank)
				.get_sprite(nSpr));
	}
	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}

	public float getAx() {
		return ax;
	}

	public void setAx(float ax) {
		this.ax = ax;
	}

	public float getAy() {
		return ay;
	}

	public void setAy(float ay) {
		this.ay = ay;
	}

	public float getAz() {
		return az;
	}

	public void setAz(float az) {
		this.az = az;
	}

	public float getVx() {
		return vx;
	}

	public void setVx(float vx) {
		this.vx = vx;
	}

	public float getVy() {
		return vy;
	}

	public void setVy(float vy) {
		this.vy = vy;
	}

	public float getVz() {
		return vz;
	}

	public void setVz(float vz) {
		this.vz = vz;
	}

	public int getAddSpr() {
		return addSpr;
	}

	public void setAddSpr(int addSpr) {
		this.addSpr = addSpr;
	}

	public Element getLinkedPerso() {
		return linkedPerso;
	}

	public void setLinkedPerso(Element linkedPerso) {
		this.linkedPerso = linkedPerso;
	}

	public void setLinkedEntity(SpriteEntity entity) {
		this.linkedEntity = entity;
	}
	
	public TileNature getCurrentTileNature() {
		Area area = EngineZildo.mapManagement.getCurrentMap();
		if (area != null) {
			return area.getCaseNature((int) x, (int) y);
		} else {
			return null;
		}
	}
	
	/**
	 * Called when the object fall on the floor, whatever kind of floor.
	 * This method handles behavior when element/perso hits the floor, but doesn't remove it. For this, call {@link #die()}.
	 * If it returns TRUE, element should be removed.
	 */
	@Override
	public boolean fall() {
		// 0: some elements just disappear
		// TODO: basically, we shouldn't do that because desc should be set
		ElementDescription d = ElementDescription.fromInt(nSpr);
		switch (d) {
		case SEWER_SMOKE1: case SEWER_SMOKE2:
		case SEWER_VOLUT1: case SEWER_VOLUT2: case SEWER_VOLUT3: case SEWER_VOLUT4:
		case FIREWIND1: case FIREWIND2: case FIREWIND3:
		case NOTE: case NOTE2:
			return true;
		}
		
		// 0: check if a scene has to be triggered
		if (fallScene != null) {
			EngineZildo.scriptManagement.execute(fallScene,  false);
			return false;	// Bypass all other treatments
		}
		
		// 1: get the landing point nature
		int cx = (int) (x / 16);
		int cy = (int) (y / 16);
		TileNature nature = getCurrentTileNature();
		
		if (nature != null) {
			EngineZildo.mapManagement.getCurrentMap().alertAtLocation(new Point(x, y));

			// Exception: for bushes, no matter what is on the floor, it should scatter its leaves
			if (desc == ElementDescription.BUSHES) {
				nature = TileNature.REGULAR;
			}
			
			switch (nature) {
			case BOTTOMLESS:	// Means LAVA, actually
				EngineZildo.soundManagement.broadcastSound(BankSound.LavaDrop, this);
				EngineZildo.spriteManagement.spawnSpriteGeneric(
						SpriteAnimation.LAVA_DROP, (int) x, (int) y, floor,	0, null, null);
				break;
			case WATER:
			case WATER_MUD:
				Element splash = EngineZildo.spriteManagement.spawnSpriteGeneric(
						SpriteAnimation.WATER_SPLASH, (int) x, (int) y, floor,	0, null, null);
				if (desc == ElementDescription.DROP_SMALL) {
					// If a small drop falls in water, reduce the splash animation, and renders a small sound
					EngineZildo.soundManagement.broadcastSound(waterDropSound.getSound(), this);
					splash.zoom = 128;
				} else {
					EngineZildo.soundManagement.broadcastSound(BankSound.FallWater, this);
				}
				break;
			case BUSH:
				if (isZildo()) {
					// Bushes blow up when hero falls on it
					EngineZildo.mapManagement.getCurrentMap().attackTile(floor, new Point(cx, cy), (PersoPlayer) this );
				}
				break;
			case REGULAR:
				// 2: on the floor, many possibilities
				if (nBank == SpriteBank.BANK_ELEMENTS) {
					switch (d) {
					case BUSHES:
						// Bushes blow up
						EngineZildo.spriteManagement.spawnSpriteGeneric(SpriteAnimation.BUSHES, (int) x,
										(int) y, floor, 0, null, null);
						EngineZildo.soundManagement.broadcastSound(BankSound.CasseBuisson, this);
						break;
					case STRAW:
						Point dir = getLinkedPerso().angle.coords;
						EngineZildo.scriptManagement.execute("throwStraw("+x+","+y+","+dir.x+","+dir.y+")", false, new SceneContext(), null);
						break;
					case BUNCH_LEAVESFORK:
						if (visible) {
							dir = getLinkedPerso().angle.coords;
							EngineZildo.scriptManagement.execute("throwLeaves("+x+","+y+","+dir.x+","+dir.y+")", false, new SceneContext(), null);
						}
						break;
					case POISONBALL:
						if (alpha == 255 && z < 4) {
							Reverse rev = EngineZildo.hasard.de6() > 3 ? Reverse.HORIZONTAL : Reverse.NOTHING;
							SpriteEntity goop = EngineZildo.spriteManagement.spawnSprite(ElementDescription.POISONGOOP, (int) x, (int) y, false, rev,
									false);
							goop.alphaV = -0.5f;
							goop.floor = linkedPerso.floor;
						}
						break;
					case JAR:
					case STONE:
					case STONE_HEAVY:
					case ROCK_BALL:
					case AMPHORA:
						EngineZildo.spriteManagement.spawnSpriteGeneric(
								SpriteAnimation.BREAKING_ROCK, (int) x, (int) y, floor, 0, null, null);
						break;
					case DYNAMITE:
						break;
					case PEEBLE:
						EngineZildo.soundManagement.broadcastSound(BankSound.PeebleFloor, this);
						EngineZildo.spriteManagement.spawnSpriteGeneric(
								SpriteAnimation.DUST, (int) x, (int) y, floor,	0, null, null);
						break;
					case BULLET:
						EngineZildo.spriteManagement.spawnSprite(new ElementImpact((int) x, (int) y, ImpactKind.SIMPLEHIT, null));
						EngineZildo.soundManagement.broadcastSound(BankSound.BoomerangTape, this);
						break;
					case LEAF:
					case STRAWF1: case STRAWF2: case STRAWF3:
					case NETTLE_LEAF:
						if (alpha > 0)	{// Leaf stay on the floor until it's totally disappeared
							alphaV = d == ElementDescription.NETTLE_LEAF ? -0.5f : -1;
							vx=0; vy =0;
							ax=0;
							return false;
						}
						break;
					case STAFF_POUM:
						Element e = new ElementImpact((int) x, (int) y, ImpactKind.STAFF_TURNING, null);
						e.z = z;
						EngineZildo.spriteManagement.spawnSprite(e);
						EngineZildo.soundManagement.broadcastSound(BankSound.FlechePlante, this);
						break;
					}
				} else if (entityType.isPerso()) {
					EngineZildo.soundManagement.broadcastSound(BankSound.ZildoAtterit, this);
				}
				break;
			}
		}
		// Hack because hero hasn't a proper desc defined (that sucks !)
		SpriteDescription currentDesc = desc;
		if (desc == null && isZildo()) {
			currentDesc = ((PersoPlayer) this).who == ControllablePerso.PRINCESS_BUNNY ? PersoDescription.PRINCESS_BUNNY : PersoDescription.ZILDO;
		}
		// Trigger both a fallen object, and an attack on the tile
		TriggerElement trigger = TriggerElement.createFallTrigger(currentDesc, nature, name);
		EngineZildo.scriptManagement.trigger(trigger);
		
		if (desc != ElementDescription.TINY_ROCK) {
			// Tiny rock shouldn't trigger tileattack
			trigger = TriggerElement.createTileAttackTrigger(new Point(cx, cy));
			EngineZildo.scriptManagement.trigger(trigger);
		}
		// Unregister this element locale variable name, if any
		LocaleVarContext.unregisterId(name);
		return true;
	}

	/**
	 * Try to move character with the given delta, and returns corrected one.
	 * <p/>
	 * The correction is based on two methods: <ul>
	 * <li>transform diagonal movement into lateral</li>
	 * <li>transform lateral movement into diagonal</li>
	 * </ul>
	 * If no one succeeds, returns the original location.
	 * 
	 * @param p_deltaX
	 * @param p_deltaY
	 * @return corrected location, or same one if character can't move at all.
	 */
	public Pointf tryMove(float p_deltaX, float p_deltaY) {
		MapManagement mapManagement = EngineZildo.mapManagement;
		float xx = x + p_deltaX;
		float yy = y + p_deltaY;

		// Calculate an anticipation factor, to check collision in advance
		// For better diagonal movement
		double force = Pointf.distance(0, 0, p_deltaX, p_deltaY);
		float antFactor = Math.min(4, 2 + (int) force);

		for (int i=0;i<3;i++) {
			// Try three times : one with anticipation, and one smaller and one with reduced movement
			if (mapManagement.collide(x + antFactor * p_deltaX, y + antFactor * p_deltaY, this)) {
				float keepX = xx;
				float keepY = yy;
				// We need different behavior from follower than rest of people. See TestFollowPerso#notBlocked and TestFollowPerso#jumpHill
				boolean follower = (getEntityType().isPerso() && ((Perso)this).getQuel_deplacement() == MouvementPerso.FOLLOW);
				double angleMove = Trigo.getAngleRadian(p_deltaX, p_deltaY);
				
				// We're gonna check 5 transformations (add/remove PI/4, Projection on X/Y axis
				float diagonalForce = (float) (antFactor * Trigo.SQUARE_2);
				for (int j=0;j<6;j++) {
					Vector2f move2;
					switch (j) {
					case 0:
						default:
						move2 = Trigo.vect(angleMove - Trigo.PI_SUR_4, force);
						break;
					case 1:
						move2 = Trigo.vect(angleMove + Trigo.PI_SUR_4, force);
						break;
					case 2:
						diagonalForce = antFactor;	// No more diagonal for these 2 attempts
						if (Math.abs(p_deltaX) < 0.01 || p_deltaY == 0) {
							continue;
						} else {
							move2 = new Vector2f(p_deltaX, p_deltaY).rotX();
						}
						break;
					case 3:
						if (Math.abs(p_deltaY) < 0.01 || p_deltaX == 0) {
							continue;
						} else {
							move2 = new Vector2f(p_deltaX, p_deltaY).rotY();
						}
						break;
					// These extra-case have been added during a testcase: TestCollision#doorBorder
					case 4: // Lock angle on quarters (NORTH, WEST, SOUTH, EAST), and try to turn (like 1 and 2)
					case 5:
						if (i == 0) continue;	// Not with full anticipation
						Point ac = Angle.fromDelta(p_deltaX, p_deltaY).coords;
						double gridedAngle = Trigo.getAngleRadian(ac.x, ac.y);
						if (j==4) {
							move2 = Trigo.vect(gridedAngle - Trigo.PI_SUR_4, force);
						} else {
							move2 = Trigo.vect(gridedAngle + Trigo.PI_SUR_4, force);
						}
					}
					
					if ((j == 2 || j == 3)) {
						// Allow diagonal movement to become lateral, only if it leads to a real position
						// (it fixes the bug where jump was quite impossible to make because of lateral movement)
						// Difference between
  						//if ( (follower || !mapManagement.collide(x + diagonalForce * move2.x, y + diagonalForce * move2.y, this)) && 

						if ( (follower || !mapManagement.collide(x + diagonalForce * p_deltaX, y + diagonalForce * p_deltaY, this)) && 
								!mapManagement.collide(x + move2.x, y + move2.y, this)) {
							xx = x + move2.x;
							yy = y + move2.y;
							break;
						}
					} else {
							
						if (!mapManagement.collide(x + diagonalForce * move2.x, y + diagonalForce * move2.y, this) &&
								!mapManagement.collide(x + move2.x, y + move2.y, this)) {
							xx = x + move2.x;
							yy = y + move2.y;
							break;
						}
					}
				}
				if (xx == keepX && yy == keepY) { //mapManagement.collide(xx, yy, this)) {
					xx = x;
					yy = y;
					antFactor /= 3;
				} else {
					break;
				}
			} else {
				if (mapManagement.collide(x + p_deltaX, y + p_deltaY, this)) {
					// Collision ! So we keep the initial location
					xx = x;
					yy = y;
				} else if (antFactor <= 1f) {	// No collision, and move is not anticipated => so we move
					xx = x + p_deltaX;
					yy = y + p_deltaY;
					break;
				}
				antFactor /= 3;
			}
		}

		return new Pointf(xx, yy);
	}
	
	/**
	 * Some elements can damage. Default is no damage.
	 * 
	 * @return DamageType
	 */
	public DamageType getDamageType() {
		// NOTE: can't do a proper switch/case because "desc" field could be any implementation of SpriteDescription
		// which itself isn't an enum class, allowing "switch"
		if (desc == ElementDescription.PEEBLE) {
			return DamageType.PEEBLE;
		} else if (desc == ElementDescription.SEWER_VOLUT1) {
			return DamageType.POISON;
		} else if (desc == ElementDescription.STONE_HEAVY) {
			return DamageType.BIG_BLUNT;
		} else if (desc == ElementDescription.FIRE_BALL) {
			return DamageType.FIRE;
		}
		return DamageType.BLUNT;	// Default
	}

	/**
	 * Called when element is disappearing (in case of out of bounds, for example)
	 */
	public void die() {
		dying = true;
		if (shadow != null) {
			shadow.dying = true;
		}
		if (burningFire != null) {
			burningFire.dying = true;
		}
		if (linkedEntity != null) {
			linkedEntity.dying = true;
		}
	}

	/**
	 * Returns TRUE if zildo can push this element.
	 * 
	 * @return boolean
	 */
	public boolean isPushable() {
		return pushable;
	}

	@Override
	public void setPushable(boolean pushable) {
		this.pushable = pushable;
	}
	
	public float getFx() {
		return fx;
	}

	/**
	 * Move on a given direction, when pushed by a character.
	 * 
	 * @param ang
	 */
	public void moveOnPush(Angle ang) {
		switch (ang) {
		case NORD:
			vy = -0.5f;
			break;
		case EST:
			vx = 0.5f;
			break;
		case SUD:
			vy = 0.5f;
			break;
		case OUEST:
			vx = -0.5f;
			break;
		}
		EngineZildo.soundManagement.broadcastSound(BankSound.ZildoPousse, this);
		az = 1.0f;
		angle = ang;
		// One push only
		pushable = false;
	}

	/**
	 * Overridable method, called when element is taken by someone. (example: hen, duck...)
	 */
	public void beingTaken() { };
	
	/**
	 * 
	 * @param fromX
	 * @param fromY
	 * @param throwingAngle
	 * @param thrower optional
	 */
	public void beingThrown(float fromX, float fromY, Angle throwingAngle, Element thrower) {
		x = fromX + 1;
		y = fromY;
		z = 21.0f + 1.0f;
		vx = 0.0f;
		vy = 0.0f;
		vz = 0.0f;
		ax = 0.0f;
		ay = 0.0f;
		az = -0.07f;
		setForeground(false);
		setLinkedPerso(thrower); // Declare the thrower so it can't collide with him
		
		if (throwingAngle == Angle.NULL) {
			throwingAngle = thrower.angle;
		}
		setAngle(throwingAngle);
		flying = true;
		relativeZ = EngineZildo.mapManagement.getCurrentMap().readAltitude((int) x / 16, (int) y / 16);

		vx = 4 * throwingAngle.coordf.x;
		vy = 4 * throwingAngle.coordf.y;
		fx = 0.01f * Math.abs(vx);
		fy = 0.01f * Math.abs(vy);
	}
	
	/**
	 * Called when this element is collided by something.
	 * 
	 * @return FALSE if element must disappear, TRUE otherwise.
	 */
	public boolean beingCollided(Perso p_perso) {
		boolean doesImpact = desc != null && desc.doesImpact();
		if (doesImpact) {
			if (desc == ElementDescription.PEEBLE) {
				doesImpact = (z > 4 && p_perso == null);
			}
			if (doesImpact) {
				// Produce impact sound only on wall (not enemies)
				Element impact = new ElementImpact((int) x, (int) y, ImpactKind.SIMPLEHIT, null);
				impact.floor = floor;
				EngineZildo.spriteManagement.spawnSprite(impact);
				EngineZildo.soundManagement.broadcastSound(BankSound.BoomerangTape, this);
			}
			// Alert that a sound happened at this location
			EngineZildo.mapManagement.getCurrentMap().alertAtLocation(new Point(x, y));
		}
		return false;
	}

	/** Maybe replace that by a set(DamageType) automating all related stuff **/
	public void setPermanentCollision(Collision collision) {
		permanentCollision = collision;
	}
	
	public Collision getCollision() {
		return permanentCollision;
	}

	public void setFx(float fx) {
		this.fx = fx;
	}

	public float getFy() {
		return fy;
	}

	public void setFy(float fy) {
		this.fy = fy;
	}

	public float getFz() {
		return fz;
	}

	public void setFz(float fz) {
		this.fz = fz;
	}

	public Angle getAngle() {
		return angle;
	}

	public void setAngle(Angle angle) {
		this.angle = angle;
	}

	public Element addShadow(ElementDescription p_typeShadow) {
		// Add a shadow
		shadow = new Element();
		shadow.x = x;
		shadow.y = y - 1;
		shadow.z = -2;
		shadow.nBank = SpriteBank.BANK_ELEMENTS;
		shadow.nSpr = p_typeShadow.ordinal();
		shadow.setSprModel(p_typeShadow);
		shadow.linkedPerso = this;
		spawnShadow();
		return shadow;
	}

	/** Only element itself has access to its shadow **/
	public void spawnShadow() {
		if (shadow != null && shadow.getId() == -1) {
			EngineZildo.spriteManagement.spawnSprite(shadow);
		}
	}
	
	public boolean hasShadow() {
		return shadow != null;
	}
	
	@Override
	public boolean isGhost() {
		if (linkedPerso != null && linkedPerso.getEntityType().isPerso()) {
			Perso p = (Perso) linkedPerso;
			return p.isGhost();
		}
		return ghost;
	}
	
	/** Returns TRUE if this element is linked in such way to Zildo:<ul>
	 * <li>it's a part of his sprite set (shield, feet, aura, sword)</li>
	 * <li>it's a part of a character following Zildo</li>
	 * @return boolean
	 */
	public boolean isLinkedToZildo() {
		if (linkedPerso == null) {
			return false;
		}
		if (linkedPerso.isZildo()) {
			return true;
		}
		if (linkedPerso.getEntityType().isPerso()) {
			Element linked = ((Perso) linkedPerso).getFollowing();
			if (linked != null) {
				return linked.isZildo();
			}
		}
		// Recursively recalls (totally unlikely that we calls this more than once)
		return linkedPerso.isLinkedToZildo();
	}

	// Set element's floor and its shadow (if exists)
	public void setFloor(int fl) {
		super.setFloor(fl);
		if (shadow != null) {
			shadow.setFloor(fl);
		}
	}
	
	public void setTrigger(boolean p_trigger) {
		questTrigger = p_trigger;
	}
	
	public void addFire(int addZ) {
		if (burningFire == null) {
			burningFire = new ElementFire((int) x, (int) y);
			EngineZildo.spriteManagement.spawnSprite(burningFire);
			// In order to access the fire element in script
			burningFire.setName(this.name +"_f");
		}
		burningFire.x = x;
		burningFire.y = y;
		burningFire.z = z + addZ;
		burningFire.floor = floor;
		timeBurning = 0;
	}
	
	public void addFire() {
		addFire(0);
	}

	public boolean isOutsidemapAllowed() {
		boolean allowed = false;
		if (desc instanceof ElementDescription) {
			allowed = ((ElementDescription)desc).isOutsidemapAllowed();
		}
		return allowed;
	}

	@Override
	public String toString() {
		String s = (name != null ? (name+"\n") : "") + x + ", " + y;
		if (nBank == SpriteBank.BANK_ELEMENTS) {
			return s + " (id=" + id+", " + desc + ")";
		}
		return s + " (id=" + id + ", " + desc + ")";
	}
	
	public Point getDefaultSize() {
		return defaultSize;
	}
	
	public static Point getElementSize(Element e) {
		Point size = new Point(8, 4);
		Collision colli = null;
		if (e != null) {
			size = e.getDefaultSize(); // Default size
			colli = e.getCollision();
		}
		if (e != null && colli != null) {
			Point elemSize = colli.size;
			if (elemSize != null) {
				size = elemSize;
			}
		}
		return size;
	}
	
	@Override
	public int getFloorForSort() {
		if (linkedPerso!=null && linkedPerso.isZildo()) { // && !flying) {
			// For example, if hero is carrying an object above his head, they should be displayed at the same floor
			return linkedPerso.getFloorForSort();
		} else {
			return super.getFloorForSort();
		}
	}
	
	@Override
	public String getTargetScene() {
		return targetScene;
	}
	@Override
	public String getFallScene() {
		return fallScene;
	}
}