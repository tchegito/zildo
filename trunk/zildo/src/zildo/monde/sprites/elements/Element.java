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

package zildo.monde.sprites.elements;

import zildo.client.sound.BankSound;
import zildo.fwk.bank.SpriteBank;
import zildo.fwk.script.xml.element.TriggerElement;
import zildo.monde.Trigo;
import zildo.monde.collision.Collision;
import zildo.monde.collision.DamageType;
import zildo.monde.map.Area;
import zildo.monde.map.Tile.TileNature;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.SpriteModel;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.desc.EntityType;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.desc.SpriteAnimation;
import zildo.monde.sprites.elements.ElementImpact.ImpactKind;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
import zildo.monde.util.Pointf;
import zildo.monde.util.Vector2f;
import zildo.server.EngineZildo;
import zildo.server.MapManagement;

//TODO: Remove getter/setter for x,y,z

public class Element extends SpriteEntity {

	// Class variables
	private float ancX, ancY, ancZ;
	public float ax, ay, az;
	public float vx, vy, vz;
	public float fx, fy, fz; // Frottements
	public float alphaV, alphaA;	// Speed and acceleration for alpha
	protected char spe; // Spe est utilisé selon l'usage
	protected Angle angle;
	public boolean flying;

	public int relativeZ; // Simulate the altitude delta
	protected int addSpr; // Pour les animations (exemple:diamants qui brillent)
	protected Element linkedPerso; // When this element dies, any non-perso
									// linked entity die too.

	private boolean questTrigger;	// TRUE=taking this element leads to a quest accomplishment
	
	protected Element shadow;
	protected boolean pushable;
	
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
		
		this.entityType = EntityType.ELEMENT;
		// logger.log(Level.INFO, "Copying Element");

	}

	@Override
	public void finalize() {
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
	 * Renvoie TRUE si l'élément est solide.
	 * 
	 * @return boolean
	 */
	public boolean isSolid() {
		if (desc instanceof ElementDescription) {
			// Volatile elements (need to be refactored, with a real attribute on Descriptions)
			switch ((ElementDescription) desc) {
				case REDSPHERE1:
				case BROWNSPHERE1:
				case SEWER_SMOKE1:
				case SEWER_SMOKE2:
				return false;
			}
		}
		if (desc.isDamageable()) {
			return true;
		}
		// S'il s'agit d'un personnage
		if (entityType.isPerso()) {
			return true;
		}
		if (desc.isBlocking()) {
			return true;
		}
		if (desc == ElementDescription.WATER_LEAF) {
			return true;
		}
		return false;
	}

	/**
	 * Move object, and stop it in case of collision.
	 * 
	 * @return boolean
	 */
	protected boolean physicMoveWithCollision() {
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
					z = ancZ;
					vx = 0;
					vy = 0;
					return true;
				}
			}
		}
		// Out of the map
		Area map = EngineZildo.mapManagement.getCurrentMap();
		if (map.isOutside((int) x, (int) y)) {
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
		
		if (alpha < 0 && fall()) {
			die();
		}
		if (mover != null && mover.isActive()) {
			// Moving is delegated to another object
			mover.reachTarget();
		} else {
			
			// Si ce sprite est valide, est-il un sprite fixe ?
			if (desc.isNotFixe()) {
				// On a trouvé un sprite valide non fixe
				// On calcule sa nouvelle position absolue
				colli = physicMoveWithCollision();
	
				if (nSpr >= 44 && nSpr <= 47) { // Sprite d'animation
					// Morceaux de pierres
					z = z - vz; // On revient en arrière
					vz = vz - az;
					az = az - 1;
					if (az == 0) {
						dying = true;
					}
				}
				// Débordement
				if (x < -4 || y < -4 || x > 64 * 16 || (y-z) > 64 * 16) {
					if (isOutsidemapAllowed() && !isLinkedToZildo()) {
						die();
						dying = true;
					}
				} else {
	
					if (desc.isPushable()) {
						z = ancZ; // z-vz;
						vz = vz - az;
						if (az != 0) {
							if (colli || az == 32) {
								vx = 0;
								vy = 0;
								az = 32;
								vz = 0;
								// Trigger a push event
								// Trigger an object being pushed
								TriggerElement trig = TriggerElement.createPushTrigger(name, angle);
								EngineZildo.scriptManagement.trigger(trig);
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
					// Collision avec les ennemis (uniquement dans le cas où l'objet
					// est en mouvement)
					Collision collision = getCollision();
					if (vx != 0 || vy != 0 || vz != 0 || collision != null) {
						manageCollision();
					}
				}
			}
			if (shadow != null) {
				shadow.x = x;
				shadow.y = y - 1;
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
		// Default, collision from element is related to the linked Perso
		SpriteEntity linked = linkedPerso;
		Element weapon = this;
		if (this.getEntityType().isPerso()) {
			// If Element we're handling is a Perso, adjust infos
			linked = this;
			weapon = null;
		}
		SpriteModel model = getSprModel();
		if (collision == null) {
			int radius = (model.getTaille_x() + model.getTaille_y()) / 4;
			collision = new Collision((int) x, (int) y, radius, Angle.NORD,
					(Perso) linked, getDamageType(), weapon);
		}
		collision.cy -= model.getTaille_y() / 2;
		collision.cy -= z;
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

	protected TileNature getCurrentTileNature() {
		Area area = EngineZildo.mapManagement.getCurrentMap();
		int cx = (int) (x / 16);
		int cy = (int) (y / 16);
		return area.getCaseNature(cx, cy);
	}
	/**
	 * Called when the object fall on the floor, whatever kind of floor.
	 * This method handles behavior when element/perso hits the floor, but doesn't remove it. For this, call {@link #die()}.
	 */
	@Override
	public boolean fall() {
		// 0: some elements just disappear
		// TODO: basically, we shouldn't do that because desc should be set
		ElementDescription d = ElementDescription.fromInt(nSpr);
		switch (d) {
		case SEWER_SMOKE1: case SEWER_SMOKE2:
		case SEWER_VOLUT1: case SEWER_VOLUT2: case SEWER_VOLUT3: case SEWER_VOLUT4:
			return true;
		}
		
		// 1: get the landing point nature
		Area area = EngineZildo.mapManagement.getCurrentMap();
		int cx = (int) (x / 16);
		int cy = (int) (y / 16);
		TileNature nature = getCurrentTileNature();
		
		if (nature != null) {
			switch (nature) {
			case BOTTOMLESS:	// Means LAVA, actually
				EngineZildo.soundManagement.broadcastSound(BankSound.LavaDrop, this);
				EngineZildo.spriteManagement.spawnSpriteGeneric(
						SpriteAnimation.LAVA_DROP, (int) x, (int) y, floor,	0, null, null);
				break;
			case WATER:
				EngineZildo.soundManagement.broadcastSound(BankSound.FallWater, this);
				EngineZildo.spriteManagement.spawnSpriteGeneric(
						SpriteAnimation.WATER_SPLASH, (int) x, (int) y, floor,	0, null, null);
				break;
			case REGULAR:
				// 2: on the floor, many possibilities
				if (nBank == SpriteBank.BANK_ELEMENTS) {
					switch (d) {
					case BUSHES:
						// Le buisson s'effeuille
						EngineZildo.spriteManagement
								.spawnSpriteGeneric(SpriteAnimation.BUSHES, (int) x,
										(int) y, floor, 0, null, null);
						EngineZildo.soundManagement.broadcastSound(BankSound.CasseBuisson, this);
						break;
					case JAR:
					case STONE:
					case STONE_HEAVY:
					case ROCK_BALL:
						EngineZildo.spriteManagement.spawnSpriteGeneric(
								SpriteAnimation.BREAKING_ROCK, (int) x, (int) y, floor, 0, null, null);
						EngineZildo.soundManagement.broadcastSound(BankSound.CassePierre, this);
						break;
					case DYNAMITE:
						break;
					case PEEBLE:
						EngineZildo.soundManagement.broadcastSound(BankSound.PeebleFloor, this);
						EngineZildo.spriteManagement.spawnSpriteGeneric(
								SpriteAnimation.DUST, (int) x, (int) y, floor,	0, null, null);
						break;
					case LEAF:
						if (alpha > 0)	{// Leaf stay on the floor until it's totally disappeared
							alphaV = -1;
							vx=0;
							ax=0;
							return false;
						}
					}
					if (questTrigger) {	// Only for bank ELEMENTS now
						// Activate a quest if we're asked for
						EngineZildo.scriptManagement.takeItem(area.getName(), cx, cy, d);
					}
				}
				break;
			}
		}
		TriggerElement trigger = TriggerElement.createFallTrigger(desc, nature);
		EngineZildo.scriptManagement.trigger(trigger);
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
				double angleMove = Trigo.getAngleRadian(p_deltaX, p_deltaY);
				
				float diagonalForce = (float) (antFactor * Trigo.SQUARE_2);
				for (int j=0;j<4;j++) {
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
						if (Math.abs(p_deltaX) < 0.01) {
							continue;
						} else {
							move2 = new Vector2f(p_deltaX, p_deltaY).rotX();
						}
						break;
					case 3:
						if (Math.abs(p_deltaY) < 0.01) {
							continue;
						} else {
							move2 = new Vector2f(p_deltaX, p_deltaY).rotY();
						}
						break;
					}
							
					if (!mapManagement.collide(x + diagonalForce * move2.x, y + diagonalForce * move2.y, this) &&
							!mapManagement.collide(x + move2.x, y + move2.y, this)) {
						xx = x + move2.x;
						yy = y + move2.y;
						break;
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
		if (desc == ElementDescription.PEEBLE) {
			return DamageType.PEEBLE;
		}
		return DamageType.BLUNT;	// Default
	}

	/**
	 * Called when element is disappearing (in case of out of bounds, for
	 * example)
	 */
	public void die() {
		dying = true;
		if (shadow != null) {
			shadow.dying = true;
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
		if (desc == ElementDescription.PEEBLE) {
			if (z > 4 && p_perso == null) {
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

	public Collision getCollision() {
		return null;
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

	public void addShadow(ElementDescription p_typeShadow) {
		// Add a shadow
		shadow = new Element();
		shadow.x = x;
		shadow.y = y - 1;
		shadow.z = -2;
		shadow.nBank = SpriteBank.BANK_ELEMENTS;
		shadow.nSpr = p_typeShadow.ordinal();
		shadow.setSprModel(p_typeShadow);
		shadow.linkedPerso = this;
		EngineZildo.spriteManagement.spawnSprite(shadow);
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

	public void setTrigger(boolean p_trigger) {
		questTrigger = p_trigger;
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
		String s = x + ", " + y;
		if (nBank == SpriteBank.BANK_ELEMENTS) {
			return s + " (" + ElementDescription.fromInt(nSpr) + ")";
		}
		return s + " (" + nSpr + " - bank " + nBank + ")";
	}
}