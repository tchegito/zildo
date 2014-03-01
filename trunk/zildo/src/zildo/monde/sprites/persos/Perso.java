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

package zildo.monde.sprites.persos;

import java.util.ArrayList;
import java.util.List;

import zildo.client.sound.BankSound;
import zildo.fwk.gfx.EngineFX;
import zildo.fwk.script.xml.element.TriggerElement;
import zildo.monde.items.Item;
import zildo.monde.map.Area;
import zildo.monde.map.Tile;
import zildo.monde.map.TileCollision;
import zildo.monde.sprites.Reverse;
import zildo.monde.sprites.Rotation;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.SpriteModel;
import zildo.monde.sprites.desc.EntityType;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.desc.SpriteAnimation;
import zildo.monde.sprites.desc.SpriteDescription;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.persos.action.PersoAction;
import zildo.monde.sprites.persos.ia.PathFinder;
import zildo.monde.sprites.persos.ia.PathFinderSquirrel;
import zildo.monde.sprites.persos.ia.PathFinderStraightFlying;
import zildo.monde.sprites.utils.MouvementPerso;
import zildo.monde.sprites.utils.MouvementZildo;
import zildo.monde.sprites.utils.SoundGetter;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
import zildo.monde.util.Pointf;
import zildo.monde.util.Zone;
import zildo.server.EngineZildo;

public abstract class Perso extends Element {

	public enum PersoInfo {
		NEUTRAL, ENEMY, ZILDO, SHOOTABLE_NEUTRAL;
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
	protected float prevX, prevY;	// Previous location (to calculate a delta)
	public float deltaMoveX, deltaMoveY;	// Previous location (to calculate a delta)
	protected int pos_seqsprite;
	private Element en_bras; // If this is Zildo, what he holds. If any perso,
								// his weapon
	protected MouvementZildo mouvement; // Situation du
										// perso:debout,couché,attaque...
	protected int cptMouvement; // Un compteur pour les mouvements des PNJ
	private int coming_map; // 1 si Zildo entre sur une map,sinon 255
	protected int pv, maxpv; // Points de vie du perso
	private boolean onPlatform = false;	// TRUE=character is on a platform
	
	protected int money;
	protected int countArrow;
	protected int countBomb;
	protected int countKey; // How many keys have perso ? (for PNJ, he gives it
							// when he dies)

	protected Perso shooter;	// Last perso who shoot this one
	
	// Jump
	private Point posAvantSaut;
	protected Point posShadowJump;
	private Angle jumpAngle;

	protected PersoAction action; // Perso doing an action

	private int count = 0;
	protected boolean inWater = false;	// Feet in water
	protected boolean underWater = false;
	protected boolean inDirt = false;

	protected boolean askedVisible = true;	// FALSE=a script ask this character to be invisible
	
	private boolean wounded;
	private Perso dialoguingWith;
	private String dialogSwitch; // Field parseable by ZSSwitch
	private Element following; // Perso followed by this one

	private static SoundGetter footWater = new SoundGetter(BankSound.ZildoPatauge, BankSound.ZildoPatauge2, 500);
	private static SoundGetter footOnSqueak = new SoundGetter(BankSound.Squeak1, BankSound.Squeak2, 800, true);

	private static TileCollision tileCollision = TileCollision.getInstance();
	
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
			Point target = pathFinder.getTarget();
			switch (p_script) {
			case IMMOBILE:
				setAlerte(false);
			case ZONE:
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
		case TOMBE:
			// Center Zildo on his tile
			x = 16 * (int) (x/16) + 8;
			y = 16 * (int) (y/16) + 8;
			break;
		case VIDE:
			addSpr = 0;
			pos_seqsprite = 0;
			break;
		}
	}

	public int getComing_map() {
		return coming_map;
	}

	public void setComing_map(int coming_map) {
		this.coming_map = coming_map;
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
		this.money = Math.max(0, money);
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
	
	@Override
	public void finalize() {
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
		sb.append("Perso=" + name + "\nCoords:(" + x + "," + y + ")");
		if (pathFinder != null && pathFinder.getTarget() != null) {
			Point p = pathFinder.getTarget();
			sb.append(" ==> ("+p.x+","+p.y+")");
		}
		sb.append("\ninfo=" + info + "\nmvt=" + mouvement);
		return sb.toString();
	}

	/**
	 * Push the character away, with a hit point located at the given
	 * coordinates.
	 * 
	 * @param p_cx
	 * @param p_cy
	 */
	protected void project(float p_cx, float p_cy, int p_speed) {
		// Project monster away from the enemy
		float diffx = getX() - p_cx;
		float diffy = getY() - p_cy;
		double norme = Math.sqrt((diffx * diffx) + (diffy * diffy));
		if (norme == 0.0f) {
			norme = 1.0f; // To avoid 'divide by zero'
		}
		// Then throw it !
		this.setPx((float) (p_speed * (diffx / norme)));
		this.setPy((float) (p_speed * (diffy / norme)));
		// Stop current potential attack
		setMouvement(MouvementZildo.VIDE);
	}

	public abstract void initPersoFX();

	/**
	 * All methods overriding this one should call it (before or after, that doesn't matter).
	 */
	public void beingWounded(float cx, float cy, Perso p_shooter, int p_damage) {
		shooter = p_shooter;
	}

	public void parry(float cx, float cy, Perso p_shooter) {
	}

	public void stopBeingWounded() { 
		boolean died = (getPv() <= 0);
		if (died) {
			die(true, shooter);
		}			
	}

	public abstract void attack();

	/**
	 * All methods overriding this one should call it (before or after, that doesn't matter).
	 * Basically, called during 'stopBeingWounded' if character hasn't HP anymore.
	 * @param p_link
	 * @param p_shooter
	 */
	public void die(boolean p_link, Perso p_shooter) {
		// Death !
		EngineZildo.spriteManagement.spawnSpriteGeneric(SpriteAnimation.DEATH, (int) x, (int) y, 0, p_link ? this
				: null, null);
		TriggerElement trig = TriggerElement.createDeathTrigger(name);
		EngineZildo.scriptManagement.trigger(trig);
		setSpecialEffect(EngineFX.PERSO_HURT);
	}

	public abstract void finaliseComportement(int compteur_animation);

	// Default function : nothing
	public void animate(int compteur) {

		deltaMoveX = x - prevX;
		deltaMoveY = y - prevY;
		
		prevX = x;
		prevY = y;
		
		if (action != null && getPv() > 0) {
			if (attente > 0 && !isZildo()) {	// Zildo's attente field decrease is already handled in PlayerManagement
				attente--;
			}
			if (action.launchAction()) {
				action = null;
				ghost = false;	// Allow player movements
			}
		}
		
		// Alpha channel evolution
		alphaV += alphaA;
		alpha += alphaV;

		if (alpha > 255) {	// Stop alpha increase when it reaches the max
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
			break;
		}
	}

	// Default : nothing to do (only Zildo can take up objects for now)
	public void takeSomething(int objX, int objY, SpriteDescription d, Element object) {

	}

	private Point transitionCrossed;
	private Angle transitionAngle;

	private boolean checkPlatformUnder() {
		// Check sprite collision
		for (SpriteEntity entity : EngineZildo.spriteManagement.getWalkableEntities()) {
			// found a platform. Is perso on it ?
			Point middle = entity.getCenter();
			SpriteModel model = entity.getSprModel();
			Zone zz = new Zone(middle.x, middle.y, model.getTaille_x(), model.getTaille_y());
			if (zz.isInto((int) x, (int) y)) {
				boolean justLinked = entity.getMover().linkEntity(this);
				if (justLinked) {
					String mapName = EngineZildo.mapManagement.getCurrentMap().getName();
					TriggerElement trigger = TriggerElement.createLocationTrigger(mapName, null, entity.getName(), -1);
					EngineZildo.scriptManagement.trigger(trigger);
					onPlatform = true;
				}

				return true;
			} else if (onPlatform) {
				entity.getMover().unlinkEntity(this);
				onPlatform = false;
			}
		}
		return false;
	}
	
	/**
	 * Perso walk on a tile, so he reacts (water), or tile change (door).
	 * 
	 * @param p_sound
	 *            TRUE=play sound when modifying map.
	 * @return boolean (TRUE=slow down)
	 */
	public boolean walkTile(boolean p_sound) {

		if (isZildo() && checkPlatformUnder()) {	// Only Zildo on platforms, for now
			// Be careful : 'return' here, means that no trigger could be activated
			// But it avoid character to die in lava.
			return false;	// Perso is on a platform
		}
		
		int cx = (int) (x / 16);
		int cy = (int) (y / 16);
		Area area = EngineZildo.mapManagement.getCurrentMap();
		boolean bottomLess = area.isCaseBottomLess(cx,  cy);
		Tile tile = area.readmap(cx, cy, false);
		if (tile == null) {
			return false;
		}
		int onmap = tile.getValue();
		if (tile.parent.getTransition() != null) {
			// Transitional case
			if (transitionCrossed == null) {
				setForeground(true);
				transitionCrossed = new Point(cx, cy);
				transitionAngle = tile.parent.getTransition();
			}
		} else if (transitionCrossed != null) {
			// Is Perso gone in the right direction ?
			Angle choosed = Angle.fromDirection(cx - transitionCrossed.x, cy - transitionCrossed.y);
			if (choosed != transitionAngle) {
				setForeground(false);
			}
			transitionCrossed = null;
		}
		boolean slowDown = false;
		boolean repeatSound = false;
		boolean fall = false;
		inWater = false;
		inDirt = false;
		BankSound snd = null;
		if (!flying && Tile.isWater(onmap)) {
			// Water
			diveAndWound();
		} else {
			switch (onmap) {
			case 256 + 22: case 256*5 + 214:
				if (pathFinder.open) {
					area.writemap(cx, cy, 314);
					area.writemap(cx + 1, cy, 315);
					snd = BankSound.OuvrePorte;
				}
				break;
			case 256 + 23: case 256*5 + 215:
				if (pathFinder.open) {
					area.writemap(cx - 1, cy, 314);
					area.writemap(cx, cy, 315);
					snd = BankSound.OuvrePorte;
				}
				break;
			case 200:
			case 374:
				if (!flying) {
					snd = BankSound.ZildoGadou;
					inDirt = true;
					repeatSound = true;
					slowDown = true;
				}
				break;
			case 846:
				// Water
				if (!flying) {
					inWater = true;
					snd = footWater.getSound();
					repeatSound = true;
				}
				break;
			case 857:
			case 858:
			case 861:
			case 862:
			case 7*256+129: case 7*256+130:
				if (!isGhost() && isZildo()) {
					EngineZildo.scriptManagement.execute("miniStairsDown", true);
				}
				slowDown = true;
				break;
			case 859:
			case 860:
			case 863:
			case 864:	// Cave stairs
			case 7*256+131: case 7*256+132:
			case 768 + 248:	// Rock on back2 for stairs (careful with this !!!)
				if (!isGhost() && isZildo()) {
					EngineZildo.scriptManagement.execute("miniStairsUp", true);
				}
				slowDown = true;
				break;
			case 256*2 + 200: case 256 * 2 + 198:	// Wood stairs going up
			case 256*2 + 201:	// Wood stairs going down (on the right)
				slowDown = true;
				break;
			case 206:
			case 207: // Mountain ladder
			case 170:
			case 171:
			case 172: // Stairs in forest
			case 91+7*256:	// Stairs in palace1
			case 228+512: case 229+512: case 230+512:	// Stairs
			case 212+1024: case 213+1024:	// Palace outside stairs
				slowDown = true;
				break;
			// Falls
			case 768+217:	// grotte
			//case 1536+198: // foret4
				if (isZildo()) {
					fall = true;
				}
				break;
			case 1277:	// Knives
				if (isZildo()) {
					beingWounded(x + deltaMoveX, y + deltaMoveY, null, 1);
				}
				break;
			case 256*2 + 57:
				// Squeaky floor
				if (!flying){ 
					snd = footOnSqueak.getSingleSound();
				}
				break;
			default:
				if (isZildo() && bottomLess) {
					// Make hero fall if he reach the border of the hill
					if (!tileCollision.collide((int) x % 16, (int) y % 16, onmap, Reverse.NOTHING, Rotation.NOTHING)) {
						fall = true;
					}
					break;
				}
			}
		}
		if (fall) {
			stopBeingWounded();	// Stop potential projection
			setCompte_dialogue(0);	// Stop Zildo blink
			EngineZildo.scriptManagement.execute("dieInPit", true);
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
			TriggerElement trig = TriggerElement.createLocationTrigger(mapName, new Point(x, y), null, onmap);
			EngineZildo.scriptManagement.trigger(trig);
		}
		return slowDown;
	}

	public boolean linkedSpritesContains(SpriteEntity entity) {
		return persoSprites.contains(entity) || en_bras == entity;
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
		return pathFinder.unstoppable;
	}

	public void setUnstoppable(boolean p_value) {
		pathFinder.unstoppable = p_value;
	}

	@Override
	public void setVisible(boolean p_visible) {
		super.setVisible(p_visible);
		for (SpriteEntity entity : persoSprites) {
			entity.setVisible(p_visible);
		}
	}

	public void askVisible(boolean p_visible) {
		askedVisible = p_visible;
	}
	
	public Point getTarget() {
		return pathFinder.getTarget();
	}

	public void setTarget(Point target) {
		this.pathFinder.setTarget(target);
	}

	public boolean hasReachedTarget() {
		return pathFinder.getTarget() == null || pathFinder.hasReachedTarget();
	}

	public void setSpeed(float p_speed) {
		if (p_speed > 0.0f) {
			pathFinder.speed = p_speed;
		}
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

	public Pointf reachDestination(float p_speed) {
		return pathFinder.reachDestination(p_speed);
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
	 * @param p_shortRadius
	 *            TRUE=sight only if target is in short perimeter / FALSE=sight
	 *            whenever target is
	 */
	public void sight(Element p_target, boolean p_shortRadius) {
		int xx = (int) (getX() - p_target.getX());
		int yy = (int) (getY() - p_target.getY());
		if (Math.abs(yy) >= Math.abs(xx) || (p_shortRadius && (Math.abs(xx) > 96 || Math.abs(yy) > 96))) {
			if (yy > 0) {
				setAngle(Angle.NORD);
			} else {
				setAngle(Angle.SUD);
			}
		} else {
			if (xx > 0) {
				setAngle(Angle.OUEST);
			} else {
				setAngle(Angle.EST);
			}
		}
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
	 * @param p_angle
	 *            should not be null
	 */
	private void jump(Angle p_angle) {
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
	 * Check if character is about to jump : near a cliff with room for landing point.<br/>
	 * If he can, start jumping.
	 * @param loc
	 */
	public void tryJump(Pointf loc) {
		int cx=(int) (loc.x / 16);
		int cy=(int) (loc.y / 16);
		Angle angleResult=EngineZildo.mapManagement.getAngleJump(angle, cx, cy);
		SpriteEntity pushed = null;
		if (isZildo()) {
			pushed = ((PersoZildo)this).getPushingSprite();
		}
		if (angleResult!=null && pushed == null) {
			// Is there a sprite colliding on the jump ? (example: bar blocking jump, in polaky4)
			int xx = (int) (loc.x + angleResult.coords.x * 4);
			int yy = (int) (loc.y + angleResult.coords.y * 4);
			if (EngineZildo.spriteManagement.collideSprite(xx, yy, this)) {
				return;
			}
			
			Point landingPoint=angleResult.getLandingPoint().translate((int) x, (int) y);
			if (!EngineZildo.mapManagement.collide(landingPoint.x, landingPoint.y, this)) {
				jump(angleResult);
			}
		}
	}
	
	/**
	 * Character is jumping : move him
	 */
	public void moveJump() {
		if (getAttente() == 32) {
			setMouvement(MouvementZildo.VIDE); // Fin du saut, on repasse en mouvement normal
			// Si Zildo atterit dans l'eau, on le remet Ã  son ancienne position avec un coeur de moins
			int cx=(int) (x / 16);
			int cy=(int) (y / 16);
			int onMap=EngineZildo.mapManagement.getCurrentMap().readmap(cx,cy);
			
			boolean platformUnder = checkPlatformUnder();
			
			if (!platformUnder && Tile.isWater(onMap)) {
				// Character is fallen in the water !
				diveAndWound();
			} else {
				setForeground(false);
				EngineZildo.soundManagement.broadcastSound(BankSound.ZildoAtterit, this);
			}
			z=0;
			if (action == null) {
				attente = 0;
			}
		} else {
			Point landingPoint=getJumpAngle().getLandingPoint();
			float pasx=landingPoint.x / 32.0f;
			float pasy=landingPoint.y / 32.0f;
			x+=pasx;
			y+=pasy;
			// Trajectoire en cloche
			double beta = (Math.PI * attente) / 32.0f;
			z =  (int) (8.0f * Math.sin(beta));
			attente++;
		}	
	}
	
	public boolean replaceBeforeJump() {
		Point beforeJumpPos=getPosAvantSaut();
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
			EngineZildo.scriptManagement.execute("dieInWater", true);
			underWater = true;
		}
	}

	/** Called when a script move this character with a 'pos' action.
	 * In order to update script, behavior when he's in a particular floor for example.
	 */
	public void beingMoved() {
		
	}
	
	@Override
	public void setDesc(SpriteDescription p_desc) {
		super.setDesc(p_desc);
		switch ((PersoDescription) desc) {
			case ABEILLE:
			case CORBEAU:
			case VAUTOUR:
			case CHAUVESOURIS:
			case SPECTRE:
			case OISEAU_VERT:
				flying = true;
		}

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
		ghost = true;	// Cancel player movements
	}

	public boolean isOnPlatform() {
		return onPlatform;
	}
	
	public boolean isFacing(Perso p_other) {
		int dx = (int) (p_other.x - x);
		int dy = (int) (p_other.y - y);
		
		int angleSignumX = Integer.signum(angle.coords.x);
		int angleSignumY = Integer.signum(angle.coords.y);
		return  (Integer.signum(dx) == angleSignumX || angleSignumX == 0) &&
				(Integer.signum(dy) == angleSignumY || angleSignumY == 0);
	}
}