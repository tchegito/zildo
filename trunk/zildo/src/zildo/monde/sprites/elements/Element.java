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

package zildo.monde.sprites.elements;

import java.util.logging.Level;
import java.util.logging.Logger;

import zildo.client.sound.BankSound;
import zildo.fwk.bank.SpriteBank;
import zildo.fwk.collection.IntSet;
import zildo.monde.collision.Collision;
import zildo.monde.collision.DamageType;
import zildo.monde.map.Area;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.SpriteModel;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.desc.EntityType;
import zildo.monde.sprites.desc.SpriteAnimation;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoZildo;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
import zildo.server.EngineZildo;

//TODO: Remove getter/setter for x,y,z

public class Element extends SpriteEntity {

	protected static Logger logger = Logger.getLogger("Element");

	// Elements that Zildo can throw on enemies
	private static IntSet damageableElements = new IntSet(0, 1, 11, 12, 2, 38,
			42, 71, 72, 73, 74, 106, 163);
	// Elements that Zildo can push
	private static IntSet pushableElements = new IntSet(28, 69, 70, 169);

	// Class variables
	private float ancX, ancY, ancZ;
	public float ax, ay, az;
	public float vx, vy, vz;
	public float fx, fy, fz; // Frottements
	protected char spe; // Spe est utilisé selon l'usage
	protected Angle angle;
	public boolean flying;

	protected String name;

	public int relativeZ; // Simulate the altitude delta
	protected int addSpr; // Pour les animations (exemple:diamants qui brillent)
	protected Element linkedPerso; // When this element dies, any non-perso
									// linked entity die too.

	protected Element shadow;

	public Element() {
		super();
		this.initialize();
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

		this.entityType = EntityType.ELEMENT;
		// logger.log(Level.INFO, "Copying Element");

	}

	@Override
	public void finalize() {
		logger.log(Level.INFO, "Deleting Element");
		linkedPerso = null;

	}

	/**
	 * If this methods returns TRUE, then element is submitted to physics.
	 * 
	 * @return boolean
	 */
	public boolean IsNotFixe() {
		int a = this.nSpr;
		if (nBank == SpriteBank.BANK_GEAR) {
			return false;
		}
		if ((a > 2 && a <= 12)
				|| (a == 0 || a == 1)
				|| (a == 28)
				||
				// (a>=32 && a<=39) ||
				(a >= 40 && a <= 42)
				|| (a >= 44 && a <= 56)
				|| (a >= 69 && a <= 74)
				|| (a >= ElementDescription.BOOMERANG1.ordinal() && a <= ElementDescription.BOOMERANG4
						.ordinal()) || (a == ElementDescription.BOMB.ordinal())
				|| a == ElementDescription.BOMBS3.ordinal()
				|| a == ElementDescription.KEY.ordinal()
				|| a == ElementDescription.STAFF_POUM.ordinal()) {
			return true;
		} else {
			return false;
		}
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
		}
	}

	/**
	 * Renvoie TRUE si l'élément est solide.
	 * 
	 * @return boolean
	 */
	public boolean isSolid() {
		if (damageableElements.contains(nSpr)) {
			return true;
		}
		// S'il s'agit d'un personnage
		if (entityType.isPerso()) {
			return true;
			// PersoDescription desc=((Perso)this).getQuel_spr();
			// return (desc.equals(PersoDescription.POULE));
		}
		if (desc.isBlocking()) {
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
		if (isSolid() || pushableElements.contains(nSpr)) {
			SpriteEntity linked = this.getLinkedPerso();
			boolean partOfPerso = false;
			if (linked != null && linked.getEntityType().isPerso()) {
				Perso perso = (Perso) this.getLinkedPerso();
				partOfPerso = perso == null ? false : perso
						.linkedSpritesContains(this);
			}
			if (!partOfPerso
					&& EngineZildo.mapManagement
							.collide((int) x, (int) y, this)) {
				// Collision : on stoppe le mouvement, on ne laisse plus que la
				// chute pour finir au sol
				x = ancX;
				y = ancY;
				z = ancZ;
				vx = 0;
				vy = 0;
				return true;
			}
		}
		// Out of the map
		Area map = EngineZildo.mapManagement.getCurrentMap();
		if (x < 0 || y < 0 || x > map.getDim_x() * 16
				|| y > map.getDim_y() * 16) {
			return true;
		}
		return false;
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// animate
	// -animate the current element, and we expect that arranged position is
	// stored in
	// ajustedX and ajustedY.
	// /////////////////////////////////////////////////////////////////////////////////////
	// OUT : List of element which have to be removed from element's list
	// /////////////////////////////////////////////////////////////////////////////////////
	// Be careful : we can animate an element which is declared as invisible (by
	// VISIBLE boolean)
	// /////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void animate() {
		// Perso pnj;
		boolean colli;

		// Si ce sprite est valide, est-il un sprite fixe ?
		if (this.IsNotFixe()) {
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
			// Débordement}
			if (x < -4 || y < -4 || x > 64 * 16 || (y-z) > 64 * 16) {
				die();
				dying = true;
			} else {

				if (pushableElements.contains(nSpr)) {
					z = ancZ; // z-vz;
					vz = vz - az;
					if (az != 0) {
						if (colli || az == 32) {
							vx = 0;
							vy = 0;
							az = 32;
							vz = 0;
						} else {
							az = az + 1;
						}
					}
				} else if (!isGoodies() && ((z < 4 && vz != 0.0f) || colli)) {
					if (!beingCollided(null)) {
						// Le sprite doit 'mourir'
						fall();
						dying = true;
					}
				}
			}
		}
		if (isSolid() || flying) {// Tous les sprites n'entrent pas en collision
			// On teste la collision avec le décor
			if (nSpr == 42) {
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
			} else if (!isGoodies() && damageableElements.contains(nSpr)) {
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

	public void setSprModel(ElementDescription p_desc) {
		this.setNBank(SpriteBank.BANK_ELEMENTS);
		this.setNSpr(p_desc.ordinal());
		this.setSprModel(EngineZildo.spriteManagement.getSpriteBank(nBank)
				.get_sprite(p_desc.ordinal()));
	}

	public void setSprModel(ElementDescription p_desc, int p_addSpr) {
		setSprModel(p_desc);
		addSpr = p_addSpr;
		this.setSprModel(EngineZildo.spriteManagement.getSpriteBank(nBank)
				.get_sprite(p_desc.ordinal() + p_addSpr));
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

	public char getSpe() {
		return spe;
	}

	public void setSpe(char spe) {
		this.spe = spe;
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

	/**
	 * Appelée lorsque l'objet tombe au sol.
	 */
	@Override
	public void fall() {
		if (nBank == SpriteBank.BANK_ELEMENTS) {
			// TODO: basically, we shouldn't do that because desc should be set
			ElementDescription d = ElementDescription.fromInt(nSpr);
			switch (d) {
			case BUSHES:
				// Le buisson s'effeuille
				EngineZildo.spriteManagement
						.spawnSpriteGeneric(SpriteAnimation.BUSHES, (int) x,
								(int) y, 0, null, null);
				EngineZildo.soundManagement.broadcastSound(
						BankSound.CasseBuisson, this);
				break;
			case JAR:
			case STONE:
			case STONE_HEAVY:
			case ROCK_BALL:
				EngineZildo.spriteManagement.spawnSpriteGeneric(
						SpriteAnimation.BREAKING_ROCK, (int) x, (int) y, 0,
						null, null);
				EngineZildo.soundManagement.broadcastSound(
						BankSound.CassePierre, this);
				break;
			case BOMB:
				break;
			case HEN:
				// La poule reprend vie dans le tableau de perso
				/*
				 * pnj.x=x; pnj.y=y;pnj.z=0; with pnj do begin
				 * zone_deplacement[0]=round(x-16*5);
				 * zone_deplacement[1]=round(y-16*5);
				 * zone_deplacement[2]=round(x+16*5);
				 * zone_deplacement[3]=round(y+16*5);
				 * quel_spr=35;quel_deplacement=SCRIPT_POULE; info=0;
				 * nom='poule'; px=0; py=0; alerte=false;pv=1; attente=0; end;
				 * spawn_perso(pnj);
				 */
			}
		}
		if (shadow != null) {
			shadow.dying = true;
		}
	}

	/**
	 * Some elements can damage. Default is no damage.
	 * 
	 * @return DamageType
	 */
	public DamageType getDamageType() {
		return null;
	}

	/**
	 * Called when element is disappearing (in case of out of bounds, for
	 * example)
	 */
	protected void die() {
	}

	/**
	 * Returns TRUE if zildo can push this element.
	 * 
	 * @return boolean
	 */
	public boolean isPushable() {
		// We shouldn't look for zildo's position normally.
		PersoZildo zildo = EngineZildo.persoManagement.getZildo();
		boolean pushable = false;
		if (desc instanceof ElementDescription) {
			ElementDescription d = (ElementDescription) desc;
			pushable = d.isPushable();
		}
		return ((pushable ||
		// Cas spécial : 28=tonneau sur la map 'polakyg' : passage secret !}
				// TODO:clean this for episode 2
		(nSpr == 28
				&& EngineZildo.mapManagement.getCurrentMap().getName()
						.equals("polakyg") && zildo.getY() >= 7 * 16)) && az == 0.0f);
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
		EngineZildo.soundManagement.broadcastSound(BankSound.ZildoPousse,
				new Point(x, y));
		az = 1.0f;
	}

	/**
	 * Called when this element is collided by something.
	 * 
	 * @return FALSE if element must disappear, TRUE otherwise.
	 */
	public boolean beingCollided(Perso p_perso) {
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	protected void addShadow(ElementDescription p_typeShadow) {
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
		return false;
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