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

package zildo.monde.sprites.persos;

import java.util.ArrayList;
import java.util.List;

import zildo.client.sound.BankSound;
import zildo.fwk.bank.SpriteBank;
import zildo.fwk.gfx.EngineFX;
import zildo.fwk.script.xml.element.TriggerElement;
import zildo.monde.items.Item;
import zildo.monde.map.Area;
import zildo.monde.map.Tile;
import zildo.monde.map.Tile.TileNature;
import zildo.monde.map.TileCollision;
import zildo.monde.map.TileLight;
import zildo.monde.sprites.Rotation;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.desc.EntityType;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.desc.SpriteAnimation;
import zildo.monde.sprites.desc.SpriteDescription;
import zildo.monde.sprites.desc.ZildoDescription;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.magic.Affection.AffectionKind;
import zildo.monde.sprites.magic.PersoAffections;
import zildo.monde.sprites.persos.action.PersoAction;
import zildo.monde.sprites.persos.ia.PathFinder;
import zildo.monde.sprites.persos.ia.PathFinderChainFollow;
import zildo.monde.sprites.persos.ia.PathFinderFollow;
import zildo.monde.sprites.persos.ia.PathFinderFreeFlying;
import zildo.monde.sprites.persos.ia.PathFinderSquirrel;
import zildo.monde.sprites.persos.ia.PathFinderStraightFlying;
import zildo.monde.sprites.persos.ia.mover.Mover;
import zildo.monde.sprites.utils.FlagPerso;
import zildo.monde.sprites.utils.MouvementPerso;
import zildo.monde.sprites.utils.MouvementZildo;
import zildo.monde.sprites.utils.SoundGetter;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
import zildo.monde.util.Pointf;
import zildo.monde.util.Zone;
import zildo.resource.Constantes;
import zildo.server.EngineZildo;

public abstract class Perso extends Element {

	public enum PersoInfo {
		NEUTRAL, // Hero can talk to
		ENEMY, // Hero can attack
		ZILDO, // Enemy can attack (hero or damageable entity, like coal)
		SHOOTABLE_NEUTRAL; // Hero can attack (hen, duck)
	}

	protected Zone zone_deplacement;
	protected int compte_dialogue;
	private String effect; // String containing desired effect ("noir", "jaune",
							// ...)
	protected PersoInfo info; // 0=Neutre 1=Ennemi 2=Zildo
	protected boolean alerte; // True=Zildo est reperé (Pieds dans l'eau si
								// c'est Zildo)
	protected MouvementPerso quel_deplacement; // Script
	protected int attente; // =0 => pas d'attente
	protected PathFinder pathFinder; // Destination
	protected float px, py; // Quand le perso est propulsé (touché)
	protected float prevX, prevY; // Previous location (to calculate a delta)
	public float deltaMoveX, deltaMoveY; // Previous location (to calculate a delta)
	protected int pos_seqsprite;
	private Element en_bras; // If this is Zildo, what he holds. If any perso,
								// his weapon
	Element feet;
	boolean dieSceneLaunched = false;

	protected MouvementZildo mouvement; // Situation du
										// perso:debout,couché,attaque...
	protected int cptMouvement; // Un compteur pour les mouvements des PNJ
	protected int pv, maxpv; // Points de vie du perso
	private boolean onPlatform = false; // TRUE=character is on a platform

	protected int money;
	protected int countArrow;
	protected int countBomb;
	protected int countKey; // How many keys have perso ? (for PNJ, he gives it when he dies)

	protected Perso shooter; // Last perso who shoot this one

	// Jump
	private Point posAvantSaut;
	protected Point posShadowJump;
	private Angle jumpAngle;

	protected PersoAction action; // Perso doing an action

	private int count = 0;
	protected boolean inWater = false; // Feet in water
	protected boolean underWater = false;
	protected boolean inDirt = false;
	/** Linked to flags defined in {@link FlagPerso} **/
	protected int flagBehavior = 0;

	protected boolean askedVisible = true; // FALSE=a script ask this character to be invisible

	private boolean wounded;
	private Perso dialoguingWith;
	private String dialogSwitch; // Field parseable by ZSSwitch
	private Element following; // Perso followed by this one

	private static SoundGetter footWater = new SoundGetter(BankSound.ZildoPatauge, BankSound.ZildoPatauge2, 500);
	private static SoundGetter footOnSqueak = new SoundGetter(BankSound.Squeak1, BankSound.Squeak2, 800, true);

	private static TileCollision tileCollision = TileCollision.getInstance();

	private static TileLight tileLight = new TileLight();

	PersoAffections affections;

	// Convenient variable, for optimization
	int bottomZ; // 'z' coordinate under the current character's location
	TileNature nature;

	public Item weapon;

	public Item getWeapon() {
		return weapon;
	}

	public void setWeapon(Item weapon) {
		this.weapon = weapon;
	}

	public Element getFollowing() {
		return following;
	}

	public void setFollowing(Element following) {
		this.following = following;
	}

	// Liste des sprites complémentaires du perso (ex:bouclier+casque pour
	// zildo)
	List<Element> persoSprites;

	public Zone getZone_deplacement() {
		return zone_deplacement;
	}

	public void setZone_deplacement(Zone zone_deplacement) {
		this.zone_deplacement = zone_deplacement;
	}

	public int getCompte_dialogue() {
		return compte_dialogue;
	}

	public void setCompte_dialogue(int compte_dialogue) {
		this.compte_dialogue = compte_dialogue;
	}

	public PersoInfo getInfo() {
		return info;
	}

	public void setInfo(PersoInfo info) {
		this.info = info;
	}

	public boolean isAlerte() {
		return alerte;
	}

	public void setAlerte(boolean alerte) {
		this.alerte = alerte;
	}

	public MouvementPerso getQuel_deplacement() {
		return quel_deplacement;
	}

	public void setQuel_deplacement(MouvementPerso p_script, boolean p_updatePathFinder) {
		quel_deplacement = p_script;
		if (p_updatePathFinder) {
			Pointf target = pathFinder.getTarget();
			switch (p_script) {
			case IMMOBILE:
			case WAITING:
			case MOLE:
				setAlerte(false);
			case ZONE:
				setFollowing(null);
				pathFinder = new PathFinder(this);
				pathFinder.setTarget(null);
				break;
			case BIRD:
				pathFinder = new PathFinderStraightFlying(this, 35f, 8.3f);
				pathFinder.setTarget(target); // Keep the previous target
				break;
			case SQUIRREL:
				pathFinder = new PathFinderSquirrel(this);
				break;
			case WAKEUP:
				pos_seqsprite = 0;
				break;
			case CAT:
				pathFinder = new PathFinder(this);
				pathFinder.speed = 0.2f;
				break;
			case FOLLOW:
				// Assume that 'following' has been set before (and keep previous speed)
				float prevSpeed = pathFinder == null ? 0.5f : pathFinder.speed;
				pathFinder = new PathFinderFollow(this, following);
				pathFinder.speed = prevSpeed;
				break;
			case CHAIN_FOLLOW:
				pathFinder = new PathFinderChainFollow(this, ((Perso) following).pathFinder, 8);
				break;
			case FREEFLY:
				pathFinder = new PathFinderFreeFlying(this);
				break;
			case MOBILE_WAIT:
				pathFinder.alwaysReach = true;
			default:
				break;
			}
		}
	}

	public int getAttente() {
		return attente;
	}

	public void setAttente(int attente) {
		this.attente = attente;
	}

	public float getPx() {
		return px;
	}

	public void setPx(float px) {
		this.px = px;
	}

	public float getPy() {
		return py;
	}

	public void setPy(float py) {
		this.py = py;
	}

	/**
	 * Returns TRUE if Zildo is currently being wounded, and projected by shock.
	 */
	public boolean isProjected() {
		return px != 0f || py != 0f;
	}

	public int getPos_seqsprite() {
		return pos_seqsprite;
	}

	public void setPos_seqsprite(int pos_seqsprite) {
		this.pos_seqsprite = pos_seqsprite;
	}

	public Element getEn_bras() {
		return en_bras;
	}

	public void setEn_bras(Element en_bras) {
		this.en_bras = en_bras;
	}

	public MouvementZildo getMouvement() {
		return mouvement;
	}

	public void setMouvement(MouvementZildo mouvement) {
		this.mouvement = mouvement;
		// Handle particular cases
		switch (mouvement) {
		case VIDE:
			addSpr = 0;
			pos_seqsprite = 0;
		default:
			break;
		}
	}

	public int getPv() {
		return pv;
	}

	public void setPv(int pv) {
		this.pv = pv;
	}

	public int getMaxpv() {
		return maxpv;
	}

	public void setMaxpv(int maxpv) {
		this.maxpv = maxpv;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = Math.min(999, Math.max(0, money));
	}

	public String getDialogSwitch() {
		return dialogSwitch;
	}

	public void setDialogSwitch(String p_dialogSwitch) {
		if (p_dialogSwitch != null && p_dialogSwitch.length() > 0) {
			dialogSwitch = p_dialogSwitch;
		} else {
			dialogSwitch = null;
		}
	}

	public boolean isWounded() {
		return wounded;
	}

	public void setWounded(boolean wounded) {
		this.wounded = wounded;
	}

	public List<Element> getPersoSprites() {
		return persoSprites;
	}

	public void setPersoSprites(List<Element> persoSprites) {
		this.persoSprites = persoSprites;
	}

	public void addPersoSprites(Element elem) {
		this.persoSprites.add(elem);
		elem.setLinkedPerso(this);
	}

	public Perso() {
		super();

		initFields();

		feet = new Element(this);
		feet.setNBank(SpriteBank.BANK_ZILDO);
		feet.setNSpr(ZildoDescription.WATFEET1.getNSpr());
		addPersoSprites(feet);

	}

	public boolean isBlinking() {
		return compte_dialogue != 0 || mouvement == MouvementZildo.TOUCHE;
	}

	protected void initFields() {
		entityType = EntityType.PERSO;

		money = (int) Math.random();

		wounded = false;
		alerte = false;
		px = 0.0f;
		py = 0.0f;
		compte_dialogue = 0;
		attente = 0;

		quel_deplacement = MouvementPerso.IMMOBILE;

		persoSprites = new ArrayList<Element>();

		pathFinder = new PathFinder(this);
	}

	public Perso(int id) {
		super(id);
	}

	public void destroy() {
		// Delete linked elements
		if (persoSprites != null && persoSprites.size() > 0) {
			for (Element e : persoSprites) {
				EngineZildo.spriteManagement.deleteSprite(e);
			}
			persoSprites.clear();
		}
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// hide
	// /////////////////////////////////////////////////////////////////////////////////////
	// Sets perso unvisible, and every linked sprites too.
	// /////////////////////////////////////////////////////////////////////////////////////
	public void hide() {
		this.visible = false;
		if (this.persoSprites.size() > 0) {
			for (Element e : persoSprites) {
				e.setVisible(false);
			}
		}
	}

	@Override
	public void setSpecialEffect(EngineFX specialEffect) {
		super.setSpecialEffect(specialEffect);
		if (this.persoSprites.size() > 0) {
			for (Element e : persoSprites) {
				e.setSpecialEffect(specialEffect);
			}
		}
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// getAttackTarget
	// /////////////////////////////////////////////////////////////////////////////////////
	// -return the tile's coordinates immediately near the character
	// /////////////////////////////////////////////////////////////////////////////////////
	public Point getAttackTarget() {

		final int add_anglex[] = { 0, 1, 0, -1 };
		final int add_angley[] = { -1, 0, 1, 0 };

		Point p = new Point();
		p.setX(((int) getX() + 5 * add_anglex[angle.value]) / 16);
		p.setY(((int) getY() + 5 * add_angley[angle.value]) / 16);

		return p;
	}

	public void placeAt(Point p_point) {
		placeAt(p_point.getX(), p_point.getY());
	}

	public void placeAt(int p_posX, int p_posY) {
		int diffX = (int) x - p_posX;
		int diffY = (int) y - p_posY;
		x = p_posX;
		y = p_posY;
		for (Element elem : persoSprites) {
			elem.x += diffX;
			elem.y += diffY;
		}
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Perso=" + name + "\nCoords:(" + x + ", " + y + " " + z + ")");
		if (pathFinder != null && pathFinder.getTarget() != null) {
			Pointf p = pathFinder.getTarget();
			sb.append(" ==> (" + p.x + "," + p.y + ")");
		}
		sb.append("\ninfo=" + info + "\nmvt=" + mouvement + " pv=" + pv);
		return sb.toString();
	}

	/**
	 * Push the character away, with a hit point located at the given coordinates.
	 * 
	 * @param p_cx
	 * @param p_cy
	 */
	protected void project(float p_cx, float p_cy, int p_speed) {
		// Project monster away from the enemy
		float diffx = getX() - p_cx;
		float diffy = getY() - p_cy;
		double norme = Pointf.pythagore(diffx, diffy);
		if (norme == 0.0f) {
			norme = 1.0f; // To avoid 'divide by zero'
		}
		// Then throw it !
		setPx((float) (p_speed * (diffx / norme)));
		setPy((float) (p_speed * (diffy / norme)));
		// Stop current potential attack
		setMouvement(MouvementZildo.VIDE);
	}

	public abstract void initPersoFX();

	/**
	 * All methods overriding this one should call it (before or after, that doesn't
	 * matter).
	 */
	public void beingWounded(float cx, float cy, Perso p_shooter, int p_damage) {
		shooter = p_shooter;
		TriggerElement trig = TriggerElement.createWoundTrigger(name);
		EngineZildo.scriptManagement.trigger(trig);

	}

	public void parry(float cx, float cy, Perso p_shooter) {
	}

	public void stopBeingWounded() {
		boolean died = (getPv() <= 0);
		if (died && !dieSceneLaunched) { // Don't run the scene twice !
			die(true, shooter);
		}
		if (!died) {
			dieSceneLaunched = false;
		}
	}

	public abstract void attack();

	/**
	 * All methods overriding this one should call it (before or after, that doesn't
	 * matter). Basically, called during 'stopBeingWounded' if character hasn't HP
	 * anymore.
	 * 
	 * @param p_link    TRUE=death animation will be linked to character's sprite
	 * @param p_shooter
	 */
	public void die(boolean p_link, Perso p_shooter) {
		// Death !
		EngineZildo.spriteManagement.spawnSpriteGeneric(SpriteAnimation.DEATH, (int) x, (int) y, floor, 0,
				p_link ? this : null, null);
		TriggerElement trig = TriggerElement.createDeathTrigger(name);
		EngineZildo.scriptManagement.trigger(trig);
		setSpecialEffect(EngineFX.PERSO_HURT);
		dieSceneLaunched = true;
	}

	/**
	 * Method designed for Perso rendering. Called every frame, whatever game state
	 * is. So this is the right place for ongoing animations, when ingame menu is
	 * displayed for example, or whatever causing an NPC block.
	 * 
	 * @param compteur_animation
	 */
	public void finaliseComportement(int compteur_animation) {
		feet.setVisible(pv > 0 && (inWater || inDirt));
		if (inWater || inDirt) {
			feet.setX(x);
			feet.setY(y + 9 + 1);
			feet.setZ(3);
			feet.setAddSpr((compteur_animation / 6) % 3);
			if (inWater) {
				feet.setNSpr(ZildoDescription.WATFEET1.getNSpr());
			} else if (inDirt) {
				feet.setNSpr(ZildoDescription.DIRT1.getNSpr());
				feet.setY(feet.getY() - 3);
			}
			feet.setForeground(false);
		}
	}

	public void cancelMove() {
		x = prevX;
		y = prevY;
	}

	/**
	 * Method for Perso animation. Called every frame, <b>when characters are not
	 * blocked</b>. Here is the default one : launch perso's action if exists, store
	 * delta movements, and manage OBSERVE script.
	 * 
	 * @param compteur
	 **/
	public void animate(int compteur) {

		deltaMoveX = x - prevX;
		deltaMoveY = y - prevY;

		prevX = x;
		prevY = y;

		if (action != null && getPv() > 0) {
			if (attente > 0 && !isZildo()) { // Zildo's attente field decrease is already handled in PlayerManagement
				attente--;
			}
			if (action.launchAction()) {
				action = null;
				ghost = false; // Allow player movements
			}
		}

		// Alpha channel evolution
		alphaV += alphaA;
		if (alphaV != 0) {
			setAlpha(alpha + alphaV);
		}

		if (alpha > 255) { // Stop alpha increase when it reaches the max
			alpha = 255;
			alphaV = 0;
			alphaA = 0;
		} else if (alpha < 0) {
			alpha = 0;
			alphaV = 0;
			alphaA = 0;
		}

		switch (this.quel_deplacement) {
		case OBSERVE:
			// Persos qui regardent en direction de Zildo
			Element observed = getFollowing();
			if (observed == null && !isZildo()) {
				observed = EngineZildo.persoManagement.getZildo();
			}
			if (observed != null) {
				sight(observed, true);
			}
		default:
			break;
		}
	}

	// Default : nothing to do (only Zildo can take up objects for now)
	public void takeSomething(int objX, int objY, SpriteDescription d, Element object) {

	}

	private boolean transitionCrossed;
	private boolean transitionMasked;	// Some transition (crack in a wall) should be displayed over character
	// and some others (ladder) should be displayed under

	/**
	 * Fills the {@link onPlatform} variable, checking every walking platform under
	 * current character.
	 **/
	protected boolean checkPlatformUnder() {
		// Check sprite collision
		for (SpriteEntity entity : EngineZildo.spriteManagement.getWalkableEntities()) {
			// found a platform. Is perso on it ?
			Mover vehicle = entity.getMover();
			Zone zz = vehicle.getZone();
			float diffZ = z - vehicle.getFlatZ();
			if (zz.isInto((int) x, (int) y)) {
				// Declare entity is on the mover only if Z matches
				if (0 <= diffZ && diffZ < 0.5) { // 0 <= DiffZ < 0.5
					bottomZ = vehicle.getFlatZ();
					boolean justLinked = vehicle.linkEntity(this);
					if (justLinked) {
						String mapName = EngineZildo.mapManagement.getCurrentMap().getName();
						TriggerElement trigger = TriggerElement.createLocationTrigger(mapName, null, entity.getName(),
								-1, floor);
						EngineZildo.scriptManagement.trigger(trigger);
						onPlatform = true;
					}

					return true;
				}
			} else if (onPlatform && vehicle.isOnIt(this)) {
				vehicle.unlinkEntity(this);
				onPlatform = false;
			}
		}
		return false;
	}

	/**
	 * Perso walk on a tile, so he reacts (water/mud), or tile change (door).
	 * 
	 * @param p_sound TRUE=play sound when modifying map.
	 * @return boolean (TRUE=slow down)
	 */
	public boolean walkTile(boolean p_sound) {

		if (isZildo() && checkPlatformUnder()) { // Only Zildo on platforms, for now
			// Be careful : 'return' here, means that no trigger could be activated
			// But it avoid character to die in lava if he's on a platform.
			return false; // Perso is on a platform
		}

		int cx = (int) (x / 16);
		int cy = (int) (y / 16);
		Area area = EngineZildo.mapManagement.getCurrentMap();
		boolean bottomLess = area.isCaseBottomLess(cx, cy, floor);
		Tile tile = area.readmap(cx, cy, false, floor);
		if (tile == null) {
			if (floor > 0) {
				// To handle properly collision in dragon's cave (juste on the dragon's edge)
				tile = area.readmap(cx, cy, false, floor - 1);
				if (tile != null && tile.bank == 9) {	// Apply this only in Nature Palace. Not very elegant I confess ...
					floor = floor - 1;
				}
			} else {
				tile = area.readmap(cx, cy, false, floor + 1);
				if (tile != null && tile.bank == 9) {
					floor = floor + 1;
				}
			}
		}
		if (tile == null) {
			return false;
		}
		int onmap = tile.getValue();
		if (Tile.isTransitionnable(onmap)) {
			transitionCrossed = true;
		} else if (transitionCrossed) {
			// Try to change perso's floor
			if (floor < area.getHighestFloor() && area.readmap(cx, cy, false, floor + 1) != null) {
				setFloor(floor + 1);
			} else if (floor > 0) {
				if (area.readmap(cx, cy, false, floor) == null) {
					setFloor(floor - 1);
				}
			}
			transitionCrossed = false;
		} else {
			transitionCrossed = false;
		}
		boolean slowDown = false;
		boolean repeatSound = false;
		boolean fall = false;
		inWater = false;
		inDirt = false;
		BankSound snd = null;
		nature = area.getCaseNature((int) x, (int) y, floor);
		int coeffWhiteLight = 15;

		if (!flying && isZildo() && nature == TileNature.WATER) {
			// Water
			diveAndWound();
		} else {
			switch (onmap) {
			// Diminushing light under horizontal doors
			case 97 + 256 * 3:
			case 99 + 256 * 3:
				coeffWhiteLight = Math.min(16 - (int) x % 16, 15);
				break;
			case 102 + 256 * 3:
			case 104 + 256 * 3:
				coeffWhiteLight = Math.min((int) x % 16, 15);
				break;
			case 98 + 256 * 3:
			case 100 + 256 * 3:
				setLight(0x111111);
				break;
			case 159 + 256 * 3:
			case 163 + 256 * 3:
			case 160 + 256 * 3:
			case 164 + 256 * 3:
				setLight(0xc2fbca);
				coeffWhiteLight = -1;
				break; // b2ebba ou 79ba92
			case 191 + 256 * 3: // Access to the right 1
			case 193 + 256 * 3:
				coeffWhiteLight = tileLight.right(1, x, y);
				break;
			case 192 + 256 * 3: // Access to the right 2
			case 194 + 256 * 3:
				coeffWhiteLight = tileLight.right(2, x, y);
				break;
			case 197 + 256 * 3: // Access to the left 1
			case 199 + 256 * 3:
				coeffWhiteLight = tileLight.left(1, x, y);
				break;
			case 196 + 256 * 3: // Access to the left 2
			case 198 + 256 * 3:
				coeffWhiteLight = tileLight.left(2, x, y);
				break;
			// Vertical doors
			case 175 + 256 * 3: // Access the north 1
			case 176 + 256 * 3:
			case 109 + 256 * 3:
			case 110 + 256 * 3:
			case 87 + 256 * 5:
			case 148 + 256 * 5: // Desert cave entrance
				coeffWhiteLight = tileLight.north(2, x, y);
				break;
			case 177 + 256 * 3: // Access to the north 2
			case 178 + 256 * 3:

			case 192 + 256 * 5:
			case 193 + 256 * 5: // Desert cave
			case 224 + 256 * 4:
				coeffWhiteLight = tileLight.north(2, x, y);
				break;
			case 111 + 256 * 3:
			case 112 + 256 * 3:
				coeffWhiteLight = tileLight.north(0, x, y);
				break;
			case 179 + 256 * 3: // Access to the south 2
			case 180 + 256 * 3:
			case 105 + 256 * 3:
			case 106 + 256 * 3:
				coeffWhiteLight = tileLight.south(1, x, y);
				break;
			case 181 + 256 * 3: // Access to the south 1
			case 182 + 256 * 3:
			case 107 + 256 * 3:
			case 108 + 256 * 3:
				coeffWhiteLight = tileLight.south(2, x, y);
				break;
			// Tile used on different rotated value: light coeff will follow
			case 9 + 256 * 10: // lavacave
			case 10 + 256 * 10:
				coeffWhiteLight = tileLight.forRotatedTile(2, x, y, tile.rotation);
				break;
			case 25 + 256 * 9: // nature palace
				coeffWhiteLight = tileLight.forNaturePalaceRotatedDoorStep(x, y, tile.rotation);
				break;
			case 28 + 256 * 9: // Nature palace
				coeffWhiteLight = tileLight.forNaturePalaceRotatedTile(x, y, tile.rotation);
				break;
			case 256 * 9 + 176: // Slab in nature palace
				area.walkSlab(cx, cy, id, true);
				break;
			case 13 + 256 * 10:
			case 14 + 256 * 10:
				coeffWhiteLight = tileLight.forRotatedTile(1, x, y, tile.rotation);
				break;
			case 256 + 22:
			case 256 * 5 + 214:
				if (pathFinder.open) {
					area.writemap(cx, cy, 314);
					area.writemap(cx + 1, cy, 315);
					snd = BankSound.OuvrePorte;
				}
				break;
			case 256 + 23:
			case 256 * 5 + 215:
				if (pathFinder.open) {
					area.writemap(cx - 1, cy, 314);
					area.writemap(cx, cy, 315);
					snd = BankSound.OuvrePorte;
				}
				break;
			case 200:
			case 374:
				if (!flying && z == 0) { // Don't slow down character if he's in the air
					snd = BankSound.ZildoGadou;
					inDirt = true;
					repeatSound = true;
					slowDown = true;
				}
				break;
			case Tile.T_WATER_MUD:
				if (nature != TileNature.WATER_MUD) {
					break;
				}
			case 846:
				// Water
				if (!flying && mouvement != MouvementZildo.TOMBE) {
					inWater = true;
					snd = footWater.getSound();
					repeatSound = true;
				}
				break;
			case 857:
			case 858:
			case 7 * 256 + 129:
			case 7 * 256 + 130:
				handleStairsScene("miniStairsDown");
				slowDown = true;
				break;
			case 7 * 256 + 133:
			case 7 * 256 + 134:
				handleStairsScene("miniStairsDownReverse");
				slowDown = true;
				break;
			case 859: // cave stairs
			case 860:
			case 7 * 256 + 131:
			case 7 * 256 + 132: // Palace1 stairs
			case 768 + 248: // Rock on back2 for stairs (careful with this !!!)
				handleStairsScene("miniStairsUp");
				slowDown = true;
				break;
			case 7 * 256 + 135:
			case 7 * 256 + 136:
				handleStairsScene("miniStairsUpReverse");
				slowDown = true;
				break;
			case 6 * 256 + 197: // Secret stairs in Valori
				handleStairsScene("miniStairsDownCave");
				slowDown = true;
				break;
			case 256 * 2 + 200:
			case 256 * 2 + 198: // Wood stairs going up
			case 256 * 2 + 201: // Wood stairs going down (on the right)
			case 256 * 2 + 213:
			case 256 * 5 + 151: // Stone stairs (in the desert)
				slowDown = true;
				break;
			case 206:
			case 207: // Mountain ladder
			case 170:
			case 171:
			case 172: // Stairs in forest
			case 91 + 7 * 256: // Stairs in palace1
			case 228 + 512:
			case 229 + 512:
			case 230 + 512: // Stairs
			case 212 + 1024:
			case 213 + 1024: // Palace outside stairs
				slowDown = true;
				break;
			// Falls
			case 768 + 217: // grotte
				// case 1536+198: // foret4
			case 256 * 10 + 34: // lava
				if (isZildo()) {
					fall = true;
				}
				break;
			case 1277: // Knives
				if (isZildo()) {
					beingWounded(x + deltaMoveX, y + deltaMoveY, null, 1);
				}
				break;
			case 256 * 2 + 57:
				// Squeaky floor
				if (!flying) {
					snd = footOnSqueak.getSingleSound();
				}
				break;
			case 256 * 3 + 125: // Hole with a floor on map below
				break;
			default:
				if (isZildo() && bottomLess) {
					// Make hero fall if he reach the border of the hill
					if (tileCollision.collide((int) x % 16, (int) y % 16, onmap, tile.reverse, Rotation.NOTHING, 0)) {
						fall = true;
					}
					break;
				}
			}
		}

		// Release a slab
		if (onmap != 256 * 9 + 176 && onmap != 256 * 9 + 177) {
			area.walkSlab(cx, cy, id, false);
		}

		Tile foreTile = area.readForeTile(cx, cy);
		if (foreTile != null) {
			int val = foreTile.getValue();
			switch (val) {
			case 256 * 3 + 126:
			case 256 * 2:
				// Particular case: really ugly, but how handle it properly ? When hero is under
				// a foretile masking him
				// For example, when he crosses vertical door.
				coeffWhiteLight = 0;
				break;
			case 256 * 4 + 219:
				coeffWhiteLight = tileLight.north(1, x, y);
			case 256 * 3 + 195:
				coeffWhiteLight = tileLight.forRotatedTile(2, x, y, foreTile.rotation.succ());
				break;
			case 256 * 3 + 98:
			case 256 * 3 + 100:
			case 256 * 3 + 101:
			case 256 * 3 + 103:
				coeffWhiteLight = 0;
				break;
			case 256 * 3 + 200:
				coeffWhiteLight = tileLight.forRotatedTile(2, x, y, foreTile.rotation.prec());
				break;
			default:
				// In dragon cave, we use foretile on bottomless tiles (this game need to reach
				// an end one day, too much workarounds !)
				if (isZildo() && bottomLess) {
					fall = (tileCollision.collide((int) x % 16, (int) y % 16, val, foreTile.reverse, Rotation.NOTHING,
							0));
				}
			}
		}
		if (coeffWhiteLight != -1) {
			setLight(coeffWhiteLight * 0x111111);
		}
		if (fall && !EngineZildo.scriptManagement.isScripting()) { // Don't redo the same scene
			// Dirty case: bridge over water. We use collision by back2 data, but instead of
			// falling in a pit
			// We want player to splash
			onmap = area.get_mapcase(cx, cy).getBackTile().getValue();
			if (onmap == 108) { // Ponton
				diveAndWound();
			} else {
				// Fall slipping forward
				vx = deltaMoveX / 20;
				vy = deltaMoveY / 20;
				stopBeingWounded(); // Stop potential projection
				setCompte_dialogue(0); // Stop Zildo blink
				if (isZildo() && ((PersoPlayer) this).who == ControllablePerso.PRINCESS_BUNNY) {
					// Adapt fall for hero as a princess)
					EngineZildo.scriptManagement.execute("princessDieInPit", true);
				} else {
					EngineZildo.scriptManagement.execute("dieInPit", true);
				}
			}
		}
		if (repeatSound) {
			if (count > 15) {
				count = 0;
			} else {
				snd = null;
				count++;
			}
		}
		if (snd != null && p_sound) {
			EngineZildo.soundManagement.broadcastSound(snd, this);
		}

		// Trigger "LOCATION" only in single player
		if (!EngineZildo.game.multiPlayer && isZildo()) {
			String mapName = area.getName();
			TriggerElement trig = TriggerElement.createLocationTrigger(mapName, new Point(x, y), null, onmap, floor);
			EngineZildo.scriptManagement.trigger(trig);
		}
		if (shadow != null) {
			shadow.setAlpha(inWater ? 100 : 255);
		}
		return slowDown;
	}

	private void handleStairsScene(String scene) {
		if (!isGhost() && isZildo()) {
			if (mouvement == MouvementZildo.TOUCHE) {
				// Cancel projection movement if hero is wounded (maybe if this isn't good, we
				// may think to consider blocking
				// all tiles leading to this method: stairs tiles)
				x -= px;
				y -= py;
			} else {
				EngineZildo.scriptManagement.execute(scene, true);
			}
		}
	}

	public boolean linkedSpritesContains(SpriteEntity entity) {
		return (persoSprites != null && persoSprites.contains(entity)) || en_bras == entity;
	}

	@Override
	public void setForeground(boolean p_foreground) {
		super.setForeground(p_foreground);
		for (Element e : persoSprites) {
			e.setForeground(p_foreground);
		}
		if (getEn_bras() != null) {
			getEn_bras().setForeground(p_foreground);
		}
	}

	public int getCptMouvement() {
		return cptMouvement;
	}

	public void setCptMouvement(int cptMouvement) {
		this.cptMouvement = cptMouvement;
	}

	public Perso getDialoguingWith() {
		return dialoguingWith;
	}

	public void setDialoguingWith(Perso p_dialoguingWith) {
		this.dialoguingWith = p_dialoguingWith;
	}

	public boolean isUnstoppable() {
		return pathFinder.isUnstoppable();
	}

	public void setUnstoppable(boolean p_value) {
		pathFinder.setUnstoppable(p_value);
	}

	@Override
	public void setVisible(boolean p_visible) {
		super.setVisible(p_visible);
		for (SpriteEntity entity : persoSprites) {
			entity.setVisible(p_visible);
		}
	}

	public void askVisible(boolean p_visible) {
		if (!askedVisible && p_visible) { // Gets out of his invisibility
			setVisible(true);
		}
		askedVisible = p_visible;

	}

	public Pointf getTarget() {
		return pathFinder.getTarget();
	}

	public Integer getTargetZ() {
		return pathFinder.getTargetZ();
	}

	public void setTarget(Pointf target) {
		pathFinder.setTarget(target);
	}

	public void setTargetZ(Integer targetZ) {
		pathFinder.setTargetZ(targetZ);
	}

	public boolean hasReachedTarget() {
		return pathFinder.hasReachedTarget();
	}

	public void setSpeed(float p_speed) {
		if (p_speed > 0.0f) {
			pathFinder.speed = p_speed;
		}
	}

	public float getSpeed() {
		return pathFinder.speed;
	}

	public void setForward(boolean p_forward) {
		pathFinder.backward = p_forward;
	}

	public void setOpen(boolean p_open) {
		pathFinder.open = p_open;
	}

	// Is this perso allowed to pass door/stairs ?
	public boolean isOpen() {
		return pathFinder.open;
	}

	public Pointf reachDestination() {
		return pathFinder.reachDestination(0);
	}

	public String getEffect() {
		return effect;
	}

	public void setEffect(String p_effect) {
		this.effect = p_effect;
	}

	public int getCountArrow() {
		return countArrow;
	}

	public void setCountArrow(int countArrow) {
		this.countArrow = countArrow;
	}

	public int getCountBomb() {
		return countBomb;
	}

	public void setCountBomb(int countBomb) {
		this.countBomb = countBomb;
	}

	public int getCountKey() {
		return countKey;
	}

	public void setCountKey(int countKey) {
		this.countKey = countKey;
	}

	/**
	 * Turn character in order to see given perso.
	 * 
	 * @param p_target
	 * @param p_shortRadius TRUE=sight only if target is in short perimeter /
	 *                      FALSE=sight whenever target is
	 */
	public void sight(Element p_target, boolean p_shortRadius) {
		int xx = (int) (p_target.getX() - getX());
		int yy = (int) (p_target.getY() - getY());
		setAngle(Angle.fromDelta(xx, yy));
	}

	@Override
	public PersoDescription getDesc() {
		return (PersoDescription) desc;
	}

	public void setPathFinder(PathFinder p_pf) {
		pathFinder = p_pf;
	}

	public Point getCenteredScreenPosition() {
		Point pos = new Point(getScrX(), getScrY());
		pos.add(sprModel.getTaille_x() / 2, sprModel.getTaille_y());

		return pos;
	}

	/**
	 * Starts a jump in given angle.
	 * 
	 * @param p_angle should not be null
	 */
	protected void jump(Angle p_angle) {
		// On sauve la position de Zildo avant son saut
		Point zildoAvantSaut = new Point(x, y);
		mouvement = MouvementZildo.SAUTE;
		jumpAngle = p_angle;
		posShadowJump = p_angle.getLandingPoint().translate((int) x, (int) y);
		setEn_bras(null);
		posAvantSaut = zildoAvantSaut;
		attente = 0;
		EngineZildo.soundManagement.broadcastSound(BankSound.ZildoTombe, this);
	}

	/**
	 * Check if character is about to jump : near a cliff with room for landing
	 * point.<br/>
	 * If he can, start jumping calling {@link #jump(Angle)} method. Else, if he
	 * can't because of something blocking on landing, return landing point.
	 * 
	 * @param loc
	 * @returns landing point (only if something blocks on landing)
	 */
	public Angle tryJump(Pointf loc) {
		int cx = (int) (loc.x / 16);
		int cy = (int) (loc.y / 16);
		Angle angleResult = EngineZildo.mapManagement.getAngleJump(angle, cx, cy, floor);

		if (angleResult != null) {
			// Is there a sprite colliding on the jump ? (example: bar blocking jump, in
			// polaky4)
			int xx = (int) (loc.x + angleResult.coords.x * 4);
			int yy = (int) (loc.y + angleResult.coords.y * 4);
			if (EngineZildo.spriteManagement.collideSprite(xx, yy, this)) {
				return null;
			}

			Point landingPoint = angleResult.getLandingPoint().translate((int) x, (int) y);
			if (!EngineZildo.mapManagement.collide(landingPoint.x, landingPoint.y, this)) {
				jump(angleResult);
			} else { // Returns angle so caller can handle particular cases
				return angleResult;
			}
		}
		return null;
	}

	public void landOnGround() {
		fall();
	}

	protected void land() {
		if (!isOnPlatform()) {
			Perso blocker = EngineZildo.persoManagement.collidePerso((int) x, (int) y, this);
			if (blocker != null) {
				// Character has fallen but collides with someone => try to project him
				// (example: turtle)
				project(blocker.x, blocker.y, 1);
				pathFinder.setUnstoppable(true);
			}
		}
		z = bottomZ;
		az = 0;
		vz = 0;
		setMouvement(MouvementZildo.VIDE); // Jump is over, get back to regular movement

		// Check if a lower floor exists (and current one doesn't anymore)
		if (floor > 0) {
			Area map = EngineZildo.mapManagement.getCurrentMap();
			int xx = (int) x / 16;
			int yy = (int) y / 16;
			if (map.readmap(xx, yy, false, floor - 1) != null) {
				if (map.readmap(xx, yy, false, floor) == null) {
					setFloor(floor - 1);
				}
			}
		}

		boolean platformUnder = checkPlatformUnder();

		if (nature != null) {
			switch (nature) {
			case WATER:
				if (!platformUnder) {
					// Character is fallen in the water !
					diveAndWound();
				}
				break;
			case BOTTOMLESS:
				walkTile(false); // Make hero fall if his feet are not feeling the ground
				break;
			case WATER_MUD:
				inWater = true; // no break => we want explicitly that player lands on ground
			default:
				landOnGround();
			}
		} else {
			landOnGround();
		}
	}

	/**
	 * Character is jumping : move him
	 */
	public void moveJump() {
		int nbStep = angle == Angle.SUD ? 40 : 32;
		if (getAttente() >= nbStep) {
			land();
			if (action == null) {
				attente = 0;
			}
		} else {
			Point landingPoint = getJumpAngle().getLandingPoint();
			float pasx = landingPoint.x / (float) nbStep;
			float pasy = landingPoint.y / (float) nbStep;
			x += pasx;
			y += pasy;
			// Trajectoire en cloche
			double beta = (Math.PI * attente) / (float) nbStep;
			z = (int) (14.0f * Math.sin(beta));
			attente++;
		}
	}

	public boolean replaceBeforeJump() {
		Point beforeJumpPos = getPosAvantSaut();
		if (beforeJumpPos == null) {
			return false;
		} else {
			x = beforeJumpPos.getX();
			y = beforeJumpPos.getY();
			beingWounded(x, y, null, 2);
			stopBeingWounded();
			return true;
		}
	}

	public void diveAndWound() {
		if (action == null && !underWater) {
			// If hero lands in water, put back in his ancient location and removes a moon
			// piece
			EngineZildo.scriptManagement.execute("dieInWater", true);
			underWater = true;
		}
	}

	/**
	 * Called when a script move this character with a 'pos' action. In order to
	 * update script, behavior when he's in a particular floor for example.
	 */
	public void beingMoved() {

	}

	public Point getPosAvantSaut() {
		return posAvantSaut;
	}

	public void resetPosAvantSaut() {
		posAvantSaut = null;
	}

	public Angle getJumpAngle() {
		return jumpAngle;
	}

	public void setAction(PersoAction p_action) {
		action = p_action;
		ghost = true; // Cancel player movements
	}

	public boolean isDoingAction() {
		return action != null;
	}

	public boolean isOnPlatform() {
		return onPlatform;
	}

	public boolean isFacing(SpriteEntity p_other) {
		int dx = (int) (p_other.x - x);
		int dy = (int) (p_other.y - y);

		Angle a = Angle.fromDelta(dx, dy);
		return a == angle;
		/*
		 * int angleSignumX = Integer.signum(angle.coords.x); int angleSignumY =
		 * Integer.signum(angle.coords.y); return (Integer.signum(dx) == angleSignumX ||
		 * angleSignumX == 0) && (Integer.signum(dy) == angleSignumY || angleSignumY ==
		 * 0);
		 */
	}

	public boolean isAffectedBy(AffectionKind kind) {
		return affections != null && affections.isAffectedBy(kind);
	}

	public void affect(AffectionKind kind) {
		if (affections == null) {
			affections = new PersoAffections(this);
		}
		affections.add(kind);
	}

	@Override
	public Pointf getDelta() {
		return new Pointf(deltaMoveX, deltaMoveY);
	}

	@Override
	public void setFloor(int p_floor) {
		super.setFloor(p_floor);
		// Set every entities linked to this perso at the same floor
		for (SpriteEntity entity : persoSprites) {
			entity.setFloor(p_floor);
		}
		if (en_bras != null) {
			en_bras.setFloor(p_floor);
		}
	}

	@Override
	public void setAlpha(float p_alpha) {
		alpha = p_alpha;
		// Affect all linked elements (ex: hero and his shield, when he falls into
		// water)
		for (Element e : persoSprites) {
			e.setAlpha(p_alpha);
		}
	}

	@Override
	public void setLight(int light) {
		// Set perso's light and all of his linked elements
		if (persoSprites != null) {
			for (Element e : persoSprites) {
				e.setLight(light);
			}
		}
		if (en_bras != null) {
			en_bras.setLight(light);
		}
		super.setLight(light);
	}

	// Returns the 'z' coordinates of the tile under the character's feet
	public int getBottomZ() {
		return EngineZildo.mapManagement.getPersoBottomZ(this);
	}

	/** Returns current sequence position divided by factor*current speed **/
	public int computeSeq(int factor) {
		return pos_seqsprite == -1 ? -1 : (pos_seqsprite / (factor * Constantes.speed));
	}

	public int computeSeqPositive(int factor) {
		return pos_seqsprite == -1 ? 0 : (pos_seqsprite / (factor * Constantes.speed));
	}

	/**
	 * Same but without the constant speed, to get a real definit delay between each
	 * sprite
	 **/
	public int computeStandardSeqPositive(int factor) {
		return pos_seqsprite == -1 ? 0 : (pos_seqsprite / factor);
	}

	public void setCarriedItem(ElementDescription desc) {
	}

	@Override
	public int getFloorForSort() {
		// When a character is on a transition (=ladder) he should be drawn OVER the
		// tiles,
		// especially to appear ON the tile at (x,y-1). Without that, he appears UNDER
		// the tile.
		int visibleFloor = floor + (transitionCrossed && !transitionMasked ? 1 : 0);
		return Math.min(Constantes.TILEENGINE_FLOOR - 1, visibleFloor);
	}

	// Tells this character that it could move by a Mover and carry people on him
	@Override
	public void initMover() {
		// Other people could climb on him at a z-coordinate of 5
		mover = new Mover(this, 5);
	}

	public int getFlagBehavior() {
		return flagBehavior;
	}

	public void setFlagBehavior(int flagBehavior) {
		this.flagBehavior = flagBehavior;
	}

}