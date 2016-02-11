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

import zildo.Zildo;
import zildo.client.sound.BankSound;
import zildo.fwk.bank.SpriteBank;
import zildo.fwk.gfx.EngineFX;
import zildo.fwk.gfx.filter.CircleFilter;
import zildo.fwk.script.xml.element.TriggerElement;
import zildo.fwk.ui.UIText;
import zildo.monde.collision.Collision;
import zildo.monde.collision.DamageType;
import zildo.monde.items.Inventory;
import zildo.monde.items.Item;
import zildo.monde.items.ItemCircle;
import zildo.monde.items.ItemKind;
import zildo.monde.items.StoredItem;
import zildo.monde.map.Tile.TileNature;
import zildo.monde.quest.actions.ScriptAction;
import zildo.monde.sprites.Reverse;
import zildo.monde.sprites.Rotation;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.desc.SpriteDescription;
import zildo.monde.sprites.desc.ZildoDescription;
import zildo.monde.sprites.desc.ZildoOutfit;
import zildo.monde.sprites.desc.ZildoSprSequence;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.elements.ElementArrow;
import zildo.monde.sprites.elements.ElementBoomerang;
import zildo.monde.sprites.elements.ElementDynamite;
import zildo.monde.sprites.elements.ElementGear;
import zildo.monde.sprites.elements.ElementGoodies;
import zildo.monde.sprites.elements.ElementImpact;
import zildo.monde.sprites.elements.ElementImpact.ImpactKind;
import zildo.monde.sprites.magic.Affection.AffectionKind;
import zildo.monde.sprites.magic.PersoAffections;
import zildo.monde.sprites.magic.ShieldEffect;
import zildo.monde.sprites.magic.ShieldEffect.ShieldType;
import zildo.monde.sprites.persos.action.HealAction;
import zildo.monde.sprites.persos.action.ScriptedPersoAction;
import zildo.monde.sprites.utils.MouvementPerso;
import zildo.monde.sprites.utils.MouvementZildo;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
import zildo.monde.util.Pointf;
import zildo.resource.Constantes;
import zildo.server.EngineZildo;
import zildo.server.MultiplayerManagement;
import zildo.server.Server;

/**
 * A character controlled by player.
 * 
 * @author Tchegito
 *
 */
public class PersoPlayer extends Perso {
	
	private SpriteEntity pushingSprite;
	private int acceleration; // from 0 to 10

	private Angle sightAngle; // For boomerang

	private int touch; // number of frames zildo is touching something without moving

	private boolean inventoring = false;
	private boolean buying = false;
	private String storeDescription;// Name of the store description (inventory of selling items)
	
	public ItemCircle guiCircle;
	private List<Item> inventory;
	private ShieldEffect shieldEffect;

	private ZildoOutfit outfit;

	private int moonHalf;

	ControllablePerso appearance;
	
	// Linked elements
	Element shield;
	Element sword;

	ZildoSprSequence swordSequence = new ZildoSprSequence();
	
	public ControllablePerso who;
	
	private SpriteEntity boomerang;

	// Sequence for sprite animation
	static int seq_1[] = { 0, 1, 2, 1 };
	static int seq_2[] = { 0, 1, 2, 1, 0, 3, 4, 3 };

	// ////////////////////////////////////////////////////////////////////
	// Construction/Destruction
	// ////////////////////////////////////////////////////////////////////

	public PersoPlayer(int p_id) { // Only used to create Zildo on a client
		super(p_id);
		inventory = new ArrayList<Item>();
		affections = new PersoAffections(this);

		super.initFields();
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// PersoZildo
	// /////////////////////////////////////////////////////////////////////////////////////
	// Return a perso named Zildo : this game's hero !
	// with a given location.
	// /////////////////////////////////////////////////////////////////////////////////////
	public PersoPlayer(int p_posX, int p_posY, ZildoOutfit p_outfit) {
		super();
		setName("Zildo");

		who = ControllablePerso.ZILDO;
		
		// We could maybe put that somewhere else
		outfit = p_outfit;
		//setDesc(ZildoDescription.UP_FIXED);

		x = p_posX; // 805); //601-32;//-500);
		y = p_posY; // 973); //684+220;//-110);
		angle = Angle.NORD;
		sightAngle = angle;
		setPos_seqsprite(-1);
		setMouvement(MouvementZildo.VIDE);
		setInfo(PersoInfo.ZILDO);
		setMaxpv(6);
		setPv(6);
		setAlerte(false);
		setCompte_dialogue(0);
		setMoney(0);
		setCountKey(0);
		pushingSprite = null;

		shield = new Element(this);
		shield.setX(getX());
		shield.setY(getY());
		shield.setNBank(SpriteBank.BANK_ZILDO);
		shield.setNSpr(ZildoDescription.SHIELD_UP.ordinal()); // Assign initial nSpr to avoid 'isNotFixe'
								// returning TRUE)

		shadow = new Element(this);
		shadow.setDesc(ElementDescription.SHADOW);

		sword = new Element(this);
		sword.setNBank(SpriteBank.BANK_ZILDO);
		sword.setNSpr(ZildoDescription.SWORD0.getNSpr());
		
		shieldEffect = null;

		addPersoSprites(shield);
		addPersoSprites(shadow);
		addPersoSprites(sword);

		// weapon=new Item(ItemKind.SWORD);
		inventory = new ArrayList<Item>();
		affections = new PersoAffections(this);
		// inventory.add(weapon);

		setSpeed(Constantes.ZILDO_SPEED);
	}

	/**
	 * Reset any effects zildo could have before he dies.
	 */
	public void resetForMultiplayer() {
		weapon = new Item(ItemKind.SWORD);
		inventory.clear();
		inventory.add(weapon);

		MultiplayerManagement.setUpZildo(this);

		if (shieldEffect != null) {
			shieldEffect.kill();
			shieldEffect = null;
		}
		affections.clear();

		inWater = false;
		inDirt = false;
	}

	@Override
	public boolean isZildo() {
		return true;
	}

	@Override
	public void initPersoFX() {
		setSpecialEffect(EngineFX.NO_EFFECT);
	}

	public SpriteEntity getPushingSprite() {
		return pushingSprite;
	}
	
	// /////////////////////////////////////////////////////////////////////////////////////
	// attack
	// /////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void attack() {
		boolean outOfOrder = false;
		if (weapon == null || !who.canAttack) {
			return; // No weapon ? No attack
		}
		String sentence;
		switch (weapon.kind) {
		case SWORD:
			EngineZildo.soundManagement.broadcastSound(BankSound.ZildoAttaque, this);
			setMouvement(MouvementZildo.ATTAQUE_EPEE);
			setAttente(6 * 2);
			break;
		case BOW:
			if (attente == 0) {
				if (countArrow > 0) {
					setMouvement(MouvementZildo.ATTAQUE_ARC);
					setAttente(4 * 8);
				} else {
					outOfOrder = true;
				}
			}
			break;
		case BOOMERANG:
			if (attente == 0 && (boomerang == null || !boomerang.isVisible())) {
				setMouvement(MouvementZildo.ATTAQUE_BOOMERANG);
				// Sightangle should not be null, but I got it once in
				// multiplayer test
				boomerang = new ElementBoomerang(sightAngle == null ? Angle.NORD : sightAngle, (int) x, (int) y,
						(int) z, this);
				EngineZildo.spriteManagement.spawnSprite(boomerang);
				setAttente(16);
			}
			break;
		case DYNAMITE:
			if (attente == 0) {
				if (countBomb > 0) {
					Element bomb = new ElementDynamite((int) x, (int) y, 0, this);
					bomb.floor = floor;
					EngineZildo.spriteManagement.spawnSprite(bomb);
					countBomb--;
					setAttente(1);
				} else {
					outOfOrder = true;
				}
			}
			break;
		case FLASK_RED:
			if (getPv() == getMaxpv()) { // If Zildo already has full life, do
											// nothing
				outOfOrder = true;
			} else {
				action = new HealAction(this, 6); // Give back 6 half-hearts
				decrementItem(ItemKind.FLASK_RED);
			}
			break;
		case FLASK_YELLOW:
			affections.add(AffectionKind.INVINCIBILITY);
			decrementItem(ItemKind.FLASK_YELLOW);
			break;
		case MILK:
			sentence = UIText.getGameText("milk.action");
			EngineZildo.dialogManagement.launchDialog(EngineZildo.getClientState(), null, new ScriptAction(sentence));
			break;
		case FLUT:
			if (attente == 0 && mouvement == MouvementZildo.VIDE) {
				setAction(new ScriptedPersoAction(this, "flut"));
			}
			break;
		case NECKLACE:
			String hq = moonHalf > 0 ? ""+Math.min(moonHalf, 2) : "";
			sentence = UIText.getGameText("necklace.action"+hq);
			EngineZildo.dialogManagement.launchDialog(EngineZildo.getClientState(), null, new ScriptAction(sentence));
			break;
		case ROCK_BAG:
			if (attente == 0) {
				Element peeble = new Element();
				peeble.setDesc(ElementDescription.PEEBLE);
				peeble.x = x;
				peeble.y = y;
				peeble.floor = floor;
				EngineZildo.spriteManagement.spawnSprite(peeble);
				peeble.beingThrown(x, y, sightAngle, this);
				peeble.z = 12;
				
				setMouvement(MouvementZildo.ATTAQUE_ROCKBAG);
				EngineZildo.soundManagement.broadcastSound(BankSound.ZildoLance, this);
				setAttente(2 * 6);
			}
			break;
		case FIRE_RING:
			if (weapon.level <= 0) {
				outOfOrder = true;	// Ring hasn't energy anymore
			} else {
				affections.toggle(AffectionKind.FIRE_DAMAGE_REDUCED, weapon);
			}
			break;
		}
		if (outOfOrder) {
			EngineZildo.soundManagement.playSound(BankSound.MenuOutOfOrder, this);
		}
		if (weapon == null) {
			weapon = inventory.get(0);
		}

		walkTile(false);	// To activate any location trigger
		// Trigger the USE one
		TriggerElement trigger = TriggerElement.createUseTrigger(weapon.kind, new Point(x, y) );
		EngineZildo.scriptManagement.trigger(trigger);
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// manageCollision
	// /////////////////////////////////////////////////////////////////////////////////////
	// -create collision zone for Zildo
	// /////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void manageCollision() {
		if (getMouvement() == MouvementZildo.ATTAQUE_EPEE) {
			// La collision avec l'épée de Zildo}
			double cx, cy, beta;
			int rayon;
			cx = getX();
			cy = getY();
			rayon = 4;
			beta = (2.0f * Math.PI * this.getAttente()) / (7 * Constantes.speed);

			switch (this.getAngle()) {
			case NORD:
				beta = beta + Math.PI;
				cy = cy - 16;
				break;
			case EST:
				beta = -beta + Math.PI / 2;
				cy = cy - 4;
				break;
			case SUD:
				cy = cy - 4;
				break;
			case OUEST:
				beta = beta + Math.PI / 2;
				cy = cy - 4;
				cx = cx - 4;
				break;
			}
			if (angle.isHorizontal()) {
				cx = cx + 16 * Math.cos(beta);
				cy = cy + 16 * Math.sin(beta);
			} else {
				cx = cx + 12 * Math.cos(beta);
				cy = cy + 12 * Math.sin(beta);
			}

			// Add this collision record to collision engine
			// Damage type: blunt at start, and then : cutting front
			DamageType dmgType = DamageType.BLUNT;
			if (attente < 6) {
				dmgType = DamageType.CUTTING_FRONT;
			}
			Collision c = new Collision((int) cx, (int) cy, rayon, Angle.NORD, this, dmgType, null);
			EngineZildo.collideManagement.addCollision(c);
		}
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// beingWounded
	// /////////////////////////////////////////////////////////////////////////////////////
	// IN : cx,cy : enemy's position
	// /////////////////////////////////////////////////////////////////////////////////////
	// Invoked when Zildo got wounded by any enemy.
	// /////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void beingWounded(float cx, float cy, Perso p_shooter, int p_damage) {

		// Do we have to cancel the wound ?
		switch (mouvement) {
			case TOMBE: // Used when hero jumps from hill, and when squirrel make regular jump
				if (appearance == ControllablePerso.PRINCESS_BUNNY && p_shooter != null && p_shooter.getDesc() == PersoDescription.BRAMBLE) {
					if (z > p_shooter.getDesc().getSizeZ()) {
						// Squirrel is above the shooter ==> no damage
						return;
					}
					break;	// Take the damage !
				}
			case SAUTE:
				return;
			default:
				if (inventoring || underWater || isAffectedBy(AffectionKind.INVINCIBILITY)) {
					return;
				}
		}
		// Project Zildo away from the enemy
		project(cx, cy, 8);

		if (p_shooter != null && p_shooter.getQuel_deplacement().isAlertable()) {
			p_shooter.setAlerte(true);
		}
		
		if (action != null) {
			action = null;
			setGhost(false);
			setAttente(0);
			// If the persoAction is scripted, kill the running scripts
			EngineZildo.scriptManagement.stopPersoAction(this);
		}
		beingWounded(p_shooter, p_damage);
		super.beingWounded(cx, cy, p_shooter, p_damage);
	}

	/**
	 * @param p_shooter
	 * @param p_damage HP loss (0 means no sound)
	 */
	public void beingWounded(Perso p_shooter, int p_damage) {
		// If hero is carrying something, let it fall
		if (getEn_bras() != null) {
			getEn_bras().az = -0.07f;
			if (getMouvement() == MouvementZildo.FIERTEOBJET) {
				getEn_bras().dying = true;
			}
			setEn_bras(null);
		}
		if (p_damage > 0) {
			EngineZildo.soundManagement.broadcastSound(BankSound.ZildoTouche, this);
		}
		
		setMouvement(MouvementZildo.TOUCHE);
		setWounded(true);
		pv -= p_damage;

		if (guiCircle != null) {
			guiCircle.kill();
			inventoring = false;
			guiCircle = null;
		}

		if (getDialoguingWith() != null) {
			getDialoguingWith().setDialoguingWith(null);
			setDialoguingWith(null); // End dialog
			EngineZildo.dialogManagement.stopDialog(Server.getClientFromZildo(this), true);
		}

	}

	/**
	 * Zildo is dead ! Send messages and respawn (in multiplayer deathmatch)
	 */
	@Override
	public void die(boolean p_link, Perso p_shooter) {
		affections.clear();
		setMouvement(MouvementZildo.TOUCHE);
		if (EngineZildo.game.multiPlayer) {
			super.die(p_link, p_shooter);
			EngineZildo.multiplayerManagement.kill(this, p_shooter);
		} else {
			// Game over
			pos_seqsprite = 0;
			EngineZildo.scriptManagement.execute("death", true);
		}
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// stopBeingWounded
	// /////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void stopBeingWounded()
	{
		setMouvement(MouvementZildo.VIDE);
		if (isWounded()) {
			setCompte_dialogue(64); // Temps d'invulnerabilité de Zildo
		}
		setPx(0.0f);
		setPy(0.0f);
		setSpecialEffect(EngineFX.NO_EFFECT);
		
		pathFinder.setUnstoppable(false);	// Used with turtle, for collision problems (see Perso#land)
		super.stopBeingWounded();
	}


	final int decalxSword[][] = {
			{ 0, 0, 0, 0, 0, 0 }, { 0, 2, 3, 2, 1, 1 },
			{ 0, 0, 0, 0, 0, 0 }, { 0, -2, -5, -2, -1, -1 } };
	
	final int decalxBow[][] = {
			{ -2, -5, -5 }, { 0, 0, 0 }, { 0, 0, 0 }, { -1, -3, -4 }
	};
	final int decalyBow[][] = {
			{ 2, 3, 2 }, { 1, 2, 1 }, { 3, 2, 2 }, { 1, 2, 1 }
	};
	final int decalboucliery[] = { 0, 2, 2, 1, 1, 1 , 0, 1};
	final int decalbouclier2y[] = { 0, 0, 0, 0, -1, -1, 0, 0 };
	final int decalbouclier3y[] = { 0, 0, -1, -1, 0, -1, 0, 0 };

	
	// /////////////////////////////////////////////////////////////////////////////////////
	// animate
	// /////////////////////////////////////////////////////////////////////////////////////
	// Manage all things related to Zildo display : shield, shadow, feets, and
	// object taken.
	// /////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void animate(int compteur_animation)
	{
		super.animate(compteur_animation);
		
		// Initialization of convenient variables
		bottomZ = getBottomZ();
		nature = getCurrentTileNature();
		
		// If zildo's dead, don't display him
		if (getPv() <= 0) {
			if (EngineZildo.game.multiPlayer) {
				setVisible(false);
				return;
			} else {
				Point zPos = getCenteredScreenPosition();
				Zildo.pdPlugin.getFilter(CircleFilter.class).setCenter(zPos.x, zPos.y);
			}
		}

		if (getEn_bras() != null && getEn_bras().dying) {
			setEn_bras(null);
		}
		
		// Affections
		affections.render();
		
		if (compte_dialogue != 0) {
			compte_dialogue--;
			if (compte_dialogue == 0) {
				setWounded(false);
			}
		}

		if (mouvement == MouvementZildo.SAUTE) {
			moveJump();
		}
		SpriteEntity pushedEntity = getPushingSprite();

		if (px != 0.0f || py != 0.0f) {
			// Zildo being hurt !
			Pointf p = tryMove(px, py);
			Pointf delta = new Pointf(p.x - x, p.y - y);
			// Fix projection and reduce movement
			px = delta.x * 0.8f;
			py = delta.y * 0.8f;
			walkTile(false);
			if (Math.abs(px) + Math.abs(py) < 0.2f) {
				stopBeingWounded();
			}
			x = p.x;
			y = p.y;
		} else if (getMouvement() == MouvementZildo.POUSSE && pushedEntity != null) {
			// Zildo est en train de pousser : obstacle bidon ou bloc ?

			if (pushedEntity.getEntityType().isElement()) {
				Element pushedElement = (Element) pushedEntity;
				if (pushedElement.isPushable()) {
					pushedElement.moveOnPush(getAngle());
					// Break link between Zildo and pushed object
					pushSomething(null);
				}
			}
		}

		if (quel_deplacement == MouvementPerso.FOLLOW) {
			// Not very clean to do such specific thing here
			pathFinder.determineDestination();
		}
		
		switch (mouvement) {
		case ATTAQUE_ARC:
			if (attente == 2 * 8) {
				EngineZildo.soundManagement.broadcastSound(BankSound.FlecheTir, this);
				Element arrow = new ElementArrow(angle, (int) x, (int) y, 0, this);
				EngineZildo.spriteManagement.spawnSprite(arrow);
				countArrow--;
			}
			break;
		case TOMBE:	// Character is falling
			z+=vz; 
			// Animate character when he jumps
			if (pos_seqsprite == -1) pos_seqsprite = 1;
			if (z > bottomZ) {
				if (checkPlatformUnder()) {	// Maybe character hit someone under him
					land();
				}
				vz+=az;
			} else if (az != 0) {
				// Fix character on the ground, and cancel movement
				land();
			}
			break;
		}
		//System.out.println(z);


	}

	final int[] seqWakeUp = { 0, 1, 0, 1, 2, 2, 3 };
	
	/** Only allowed when player is a squirrel (see {@link ControllablePerso#PRINCESS_BUNNY}) **/
	public void jump() {
		if (!isBlinking()) {	// Don't allow jump when hero is wounded
			//TODO: make it homogeneous with PathFinderSquirrel#setTarget
			az = -0.1f;
			vz = 1.1f; //2.12f;	// Adjust speed so as hero can't jump to a log from a water mud
			if (nature == TileNature.SWAMP && z < 1) {	// Allow if hero is on a stump UNDER mud (z > 1)
				vz = 0.3f;
			}
			pos_seqsprite = 12;
			mouvement = MouvementZildo.TOMBE;
		}
	}
	
	// /////////////////////////////////////////////////////////////////////////////////////
	// finaliseComportementPnj
	// /////////////////////////////////////////////////////////////////////////////////////
	// Manage character's graphic side, depending on the position in the
	// animated sequence.
	// NOTE: Called even if Zildo is in ghost mode (automatic movement in cinematic).
	// /////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void finaliseComportement(int compteur_animation) {

		float xx = x;
		float yy = y;

		// Default : invisible
		shadow.setVisible(false);
		shield.setVisible(false);
		sword.setVisible(false);

		// Wet feet are displayed differently for each appearance
		int shiftWetFeet = angle.isVertical() || angle == Angle.OUEST ? 1 : 0;
		if (who == ControllablePerso.PRINCESS_BUNNY) {
			// Player is controlling princess, so display her
			setNSpr(PersoDescription.PRINCESS_BUNNY.nth(0));
			int tileBottomZ = getBottomZ();
			if (tileBottomZ < z && mouvement != MouvementZildo.SAUTE) {
				mouvement = MouvementZildo.TOMBE;
				if (az == 0) az = -0.1f;
			}
			shadow.setVisible(true);
			shadow.setX(x);
			shadow.setY(y);
			shadow.setZ(tileBottomZ);
			Constantes.ZILDO_SPEED = 1f;
			int seqPos = 0;
			
			shiftWetFeet = -1 + 3;

			// When squirrel is on a top of a high stump, he have to be foregound
			// Idem when it's on the turtle
			setForeground(z > 7 || isOnPlatform());

			switch (angle) {
			case NORD:
				seqPos = computeSeq(2) % 6;
				setNSpr(PersoDescription.PRINCESS_BUNNY.nth(11 + seqPos % 3));
				setNBank(SpriteBank.BANK_PNJ3);
				reverse = seqPos > 2 ? Reverse.HORIZONTAL : Reverse.NOTHING;
				shadow.setX(x+2);	// adjust shadow
				shadow.setVisible(true);
				break;
			case SUD:
				seqPos = computeSeq(2) % 6;
				setNSpr(PersoDescription.PRINCESS_BUNNY.nth(3 + seqPos % 3));
				setNBank(SpriteBank.BANK_PNJ3);
				reverse = seqPos > 2 ? Reverse.HORIZONTAL : Reverse.NOTHING;
				shadow.setX(x+2);	// adjust shadow
				shadow.setVisible(true);
				break;
			case EST:
				xx -= 2;
				//shiftWetFeet += 2;
			case OUEST:
				reverse = angle == Angle.OUEST ? Reverse.HORIZONTAL : Reverse.NOTHING;
				setNSpr(PersoDescription.PRINCESS_BUNNY.nth(7 + computeSeq(2) % 3));
				setNBank(SpriteBank.BANK_PNJ3);
				shadow.setX(x+1);	// adjust shadow
				break;
			}
			xx -= 7 -3;
			yy -= sprModel.getTaille_y()-1; //21;
			shadow.setZ(shadow.z - 1);	// Display shadow under character => else, this would be weird ;)
			setAjustedX((int) xx);
			setAjustedY((int) yy);
		} else {
			// Appearance : Hero
			if (isAffectedBy(AffectionKind.FIRE_DAMAGE_REDUCED)) {
				if (shieldEffect == null) {
					shieldEffect = new ShieldEffect(this, ShieldType.REDBALL);
				}
				setSpecialEffect(EngineFX.QUAD);
			} else if (shieldEffect != null) {
				setSpecialEffect(EngineFX.NO_EFFECT);
				shieldEffect.kill();
				shieldEffect = null;
			}
			if (isAffectedBy(AffectionKind.INVINCIBILITY)) {
				setSpecialEffect(EngineFX.YELLOW_HALO);
			} else {
				setSpecialEffect(EngineFX.NO_EFFECT);
			}
			// Shield effect animation
			if (shieldEffect != null) {
				shieldEffect.animate();
			}
	
			// Corrections , décalages du sprite
			if (angle == Angle.EST) {
				xx -= 2;
			} else if (angle == Angle.OUEST) {
				xx += 2;
			}
			
			int v;
	
			switch (mouvement) {
			
			case SOULEVE:
				 if (angle == Angle.OUEST){
						xx-=1;
					}
				break;
			case TIRE:
				if (nSpr == 36) {
					if (angle == Angle.OUEST){
						xx += 1;
					} else if (angle == Angle.EST){
						xx -= 2;
					}
				}
				break;
	
	
			case ATTAQUE_EPEE:
				shield.setVisible(false);
				sword.setVisible(true);
				v = pos_seqsprite;
				if (v>=0 && v<6) {
					xx += decalxSword[angle.value][v];
					sword.setSpr(swordSequence.getSpr(angle, v));
					Point p = swordSequence.getOffset(angle, v);
					sword.setX(xx - 4 + p.x);
					sword.setY(yy + 1 - p.y);
				}
				switch (angle) {
				case SUD:
					// Sword must be over Zildo
					sword.setZ(15);
					sword.setY(sword.getY() + 15);
					break;
				default:
					sword.setZ(0);
				}
				break;
	
			case ATTAQUE_ARC:
				v = nSpr - (108 + 3 * angle.value);
				if (v>=0 && v<6) {
					xx += decalxBow[angle.value][v];
					yy += decalyBow[angle.value][v];
				}
				shield.setVisible(false);
			
				break;
			case SAUTE:
				// Zildo est en train de sauter, on affiche l'ombre à son arrivée
	
				shadow.setX(posShadowJump.x); // (float) (xx-ax)); //-6;)
				shadow.setY(posShadowJump.y); // (float) (yy-ay)-3);
				shadow.setZ(0);
				shadow.setVisible(true);
				shield.setVisible(false);
	
				break;
	
			case FIERTEOBJET:
				nSpr = ZildoDescription.ARMSRAISED.ordinal();
				yy++;
				break;
			case TOMBE:
				xx=x-6;	// Calculate from x, because xx has already been modified
				yy=y+8;
				if (z != 0) {
					shadow.setVisible(true);
					shadow.setX(x);
					shadow.setY(y);
					shadow.setZ(0);
				}
				break;
			case MORT:
				xx-=2;
				break;
			case SLEEPING:
				
				break;
			}
	
			// On affiche Zildo
	
			// Ajustemenent
			sprModel = EngineZildo.spriteManagement.getSpriteBank(SpriteBank.BANK_ZILDO).get_sprite(nSpr + addSpr);
			xx -= 7;
			yy -= sprModel.getTaille_y() - 2; //21;
	
	
			setAjustedX((int) xx);
			setAjustedY((int) yy);
			if (!askedVisible) {
				setVisible(false);
			}
			
			//////////////////////////////////////////////////
			// End of the part previously in 'animate' method (this was wrong, because this only concerns
			// rendering.
			//////////////////////////////////////////////////
			
			reverse = Reverse.NOTHING;
			switch (getMouvement())
			{
			case VIDE:
				setSpr(ZildoDescription.getMoving(angle, computeSeq(1) % 8));
				// Shield
				if (hasItem(ItemKind.SHIELD)) {
					shield.setForeground(false);
					shield.reverse = Reverse.NOTHING;
					switch (angle) {
					case NORD:
						shield.setX(xx + 2);
						shield.setY(yy + 20);
						shield.setZ(5 - 1 - decalbouclier3y[nSpr % 8]);
						shield.setNSpr(83);
						shield.setNBank(SpriteBank.BANK_ZILDO);
						break;
					case EST:
						shield.setX(xx + 14); // PASCAL : +10
						shield.setY(yy + 17 + decalbouclier2y[(nSpr - ZildoDescription.RIGHT_FIXED.ordinal()) % 8]);
						shield.setZ(0.0f);
						shield.setNSpr(84);
						shield.setNBank(SpriteBank.BANK_ZILDO);
						break;
					case SUD:
						shield.setX(xx + 13); // PASCAL : -3)
						shield.setY(yy + 23+1+ decalboucliery[(nSpr - ZildoDescription.DOWN_FIXED.ordinal()) % 8]);
						//System.out.println(yy + " ==> "+(nSpr - ZildoDescription.DOWN_FIXED.ordinal()) % 8+ " = "+decalboucliery[(nSpr - ZildoDescription.DOWN_FIXED.ordinal()) % 6]+" ==> "+shield.y);
						shield.setZ(1 + 4);
						shield.setNSpr(85);
						shield.setNBank(SpriteBank.BANK_ZILDO);
						break;
					case OUEST:
						shield.setX(xx + 3);
						shield.setY(yy + 18 - decalbouclier2y[(nSpr - ZildoDescription.RIGHT_FIXED.ordinal()) % 8]);
						//System.out.println(yy + " ==> "+(nSpr - ZildoDescription.RIGHT_FIXED.ordinal()) % 8+ " = "+decalbouclier2y[(nSpr - ZildoDescription.RIGHT_FIXED.ordinal()) % 8]+" ==> "+shield.y);
						shield.setZ(0.0f);
						shield.setNSpr(84);
						shield.setForeground(true);
						shield.reverse = Reverse.HORIZONTAL;
						shield.setNBank(SpriteBank.BANK_ZILDO);
						break;
					}
					shield.setVisible(true);
				}
				break;
			case SAUTE:
				setNSpr(angle.value + ZildoDescription.JUMP_UP.getNSpr());
				break;
			case BRAS_LEVES:
	
				// On affiche ce que Zildo a dans les mains
				Element en_bras = getEn_bras();
				// Corrections...
				if (en_bras != null) {
					int objZ = (int) en_bras.getZ();
	
					int variation = seq_1[((getPos_seqsprite() % (4 * Constantes.speed)) / Constantes.speed)];
	
					//en_bras.setX(objX);
					//en_bras.setY(objY);
					en_bras.setZ(objZ - variation);
					
					// Corrections , décalages du sprite
					float xxx = x;
					float yyy = y;
					if (angle == Angle.EST) {
						xxx -= 2;
					} else if (angle == Angle.OUEST) {
						xxx += 2;
					}
	
					en_bras.setX(xxx + 1);
					en_bras.setY(yyy); // + 3);
					en_bras.setZ(17 - 3 - variation);
				}
				setSpr(ZildoDescription.getArmraisedMoving(angle, (pos_seqsprite % (8 * Constantes.speed)) / Constantes.speed));
				break;
			case SOULEVE:
				switch (angle) {
				case NORD:
					setNSpr(ZildoDescription.PULL_UP1.ordinal());
					break;
				case EST:
					setNSpr(ZildoDescription.LIFT_RIGHT.ordinal());
					break;
				case SUD:
					setNSpr(ZildoDescription.PULL_DOWN1.ordinal());
					break;
				case OUEST:
					setNSpr(ZildoDescription.LIFT_LEFT.ordinal());
					break;
				}
				break;
			case TIRE:
				setSpr(ZildoDescription.getPulling(angle,  pos_seqsprite));
				break;
			case TOUCHE:
				setNSpr(ZildoDescription.WOUND_UP.getNSpr() + angle.value);
				break;
			case POUSSE:
				setSpr(ZildoDescription.getPushing(angle, pos_seqsprite/2));
				break;
			case ATTAQUE_EPEE:
				pos_seqsprite = (((6 * 2 - getAttente() - 1) % (6 * 2)) / 2);
				setSpr(ZildoDescription.getSwordAttacking(angle, pos_seqsprite));
				break;
			case ATTAQUE_ROCKBAG:
				pos_seqsprite = (((2 * 6 - getAttente() - 1) % (2 * 6)) / 6);
				setSpr(ZildoDescription.getSwordAttacking(angle, pos_seqsprite));
				break;
			case ATTAQUE_ARC:
				setSpr(ZildoDescription.getBowAttacking(angle, getAttente()));
				break;
			case MORT:
				setNSpr(ZildoDescription.LAYDOWN);
				break;
			case TOMBE:
				setNSpr(ZildoDescription.FALLING);
				break;
			case PLAYING_FLUT:
				setNSpr(ZildoDescription.ZILDO_FLUT);
				break;
			case SLEEPING:
				setNSpr(ZildoDescription.SLEEPING);
				break;
			case WAKEUP:
				int seqPos = (getPos_seqsprite() / (6 * seqWakeUp.length)) % seqWakeUp.length;
				setAddSpr(seqWakeUp[seqPos]);
				if (seqPos == seqWakeUp.length -1) {
					setPos_seqsprite(pos_seqsprite - 2);
				}
				setNSpr(ZildoDescription.SLEEPING);
				break;
			}
	
			if (outfit != null && nBank == SpriteBank.BANK_ZILDO) {
				setNBank(outfit.getNBank());
			}
			
			// GUI circle
			if (guiCircle != null) {
				guiCircle.animate();
				if (guiCircle.isReduced()) {
					inventoring = false;
					guiCircle = null;
				}
			}
		}

		// End of specific rendering, depending on appearance (hero, or bunny)
		// Now, common rendering
		

		if (pv > 0) {
			boolean touche = (mouvement == MouvementZildo.TOUCHE || getCompte_dialogue() != 0);
			// Zildo blink
			touche = (touche && ((compteur_animation >> 1) % 2) == 0);
			visible = !touche;
			for (Element elem : persoSprites) { // Blink linked elements too
				if (elem.isVisible()) {
					elem.setVisible(visible);
				}
			}
		} else {
			// Zildo should stay focused at die scene
			setSpecialEffect(EngineFX.FOCUSED);
		}
		
		super.finaliseComportement(compteur_animation);
		feet.setX(x + shiftWetFeet);
		feet.setY(y + 9 + 1);
	}

	/**
	 * Zildo take some goodies. It could be a heart, an arrow, or a weapon...
	 * 
	 * @param p_element
	 *            (can be null, if p_money is filled)
	 * @param p_money
	 *            >0 ==> Zildo gets some money
	 * @return boolean : TRUE=element should disappear / FALSE=element stays
	 */
	public boolean pickGoodies(ElementGoodies p_element, int p_value) {
		// Effect on perso
		if (p_value != 0 && (p_element == null || p_element.getDesc() == ElementDescription.GOLDPURSE1)) { 
			// Zildo gets/looses some money
			setMoney(money + p_value);
			if (p_value > 0) {
				EngineZildo.soundManagement.broadcastSound(BankSound.ZildoRecupItem, this);
			} else {
				EngineZildo.soundManagement.broadcastSound(BankSound.ZildoGagneArgent, this);
			}
		} else {
			int elemNSpr=p_element.getNSpr();
			ElementDescription d = ElementDescription.fromInt(elemNSpr);
			ItemKind kind = d.getItem();
			if (kind != null && kind.canBeInInventory()) {
				pickItem(kind, p_element);
				return false;
			} else {
				// Automatic behavior (presentation text, ammos adjustments)
				EngineZildo.scriptManagement.automaticBehavior(this, null, d);

				useItem(d, p_value);
			}
		}
		if (p_element != null) {
			p_element.markTaken();
		}
		return true;
	}

	/** Use an item : either use it when player press the right button, or when he picks it up.
	 * NOTE: p_value can provide some special values, from element's name. Like a precise number of
	 * dynamite, arrows ... 
	 */
	private void useItem(ElementDescription d, int p_value) {
		BankSound toPlay = null;
		switch (d) {
		case GOLDCOIN1:
			money ++;
			break;
		case THREEGOLDCOINS1:
			money += 3;
			break;
		case GOLDPURSE1:
			money += 20;
			break;
		case DROP_FLOOR:
		case DROP_SMALL:
		case DROP_MEDIUM:
			if (pv < maxpv) {
				pv = Math.min(pv+2, maxpv);
				// Blue energy animation 
				ElementImpact energy = new ElementImpact((int) x, (int) y, ImpactKind.DROP_ENERGY, this);
				EngineZildo.spriteManagement.spawnSprite(energy);
				toPlay = BankSound.ZildoRecupCoeur;
			} else {
				toPlay = BankSound.ZildoRecupItem;
			}
			break;
		case ARROW_UP:
			countArrow += 5;
			break;
		case QUAD1:
			affections.add(AffectionKind.QUAD_DAMAGE);
			EngineZildo.multiplayerManagement.pickUpQuad();
			break;
		case DYNAMITE:
			countBomb ++;
			break;
		case BOMBS3:
			countBomb += p_value == 0 ? 3 : p_value;
			break;
		case KEY:
			countKey++;
			break;
		}
		// Sound
		switch (d) {
		/*
		case GREENMONEY1:
		case BLUEMONEY1:
		case REDMONEY1:
			toPlay = BankSound.ZildoItem;
			break;
			*/
		case QUAD1:
			toPlay = BankSound.QuadDamage;
			break;
		case KEY:
			toPlay = BankSound.ZildoKey;
			break;
		case HEART_FRAGMENT:
			toPlay = BankSound.ZildoMoon;
			moonHalf++;
			break;
		case DROP_FLOOR:
		case DROP_SMALL:
		case DROP_MEDIUM:
			break;	// Already done
		default:
			toPlay = BankSound.ZildoRecupItem;
			break;
		}
		if (toPlay != null) {	// Isn't it obvious ?
			EngineZildo.soundManagement.broadcastSound(toPlay, this);
		}		
	}
	/**
	 * Zildo picks something up (bushes, hen...) Object can be already on the
	 * map (hen), or we can spawn it there (bushes, jar).
	 * 
	 * @param objX
	 * @param objY
	 * @param d
	 *            sprite's description, in case no object is supplied
	 * @param object
	 *            the taken element
	 */
	@Override
	public void takeSomething(int objX, int objY, SpriteDescription d, Element object) {
		EngineZildo.soundManagement.broadcastSound(BankSound.ZildoRamasse, this);

		Element elem = object;
		if (object == null) {
			elem = new Element();
			elem.setNBank(d.getBank());
			elem.setNSpr(d.getNSpr());
			elem.addShadow(ElementDescription.SHADOW);
		}
		elem.beingTaken();
		elem.setScrX(objX);
		elem.setScrY(objY);
		elem.setX(objX);
		elem.setY(objY);
		elem.setZ(4);
		elem.setVisible(true);
		elem.flying = false;
		elem.setForeground(true);
		
		elem.setLinkedPerso(this); // Link to Zildo

		if (object == null) {
			EngineZildo.spriteManagement.spawnSprite(elem);
		}

		// On passe en position "soulève", et on attend 20 frames
		setMouvement(MouvementZildo.SOULEVE);
		setAttente(20);
		setEn_bras(elem);
	}

	/**
	 * Zildo throws what he got in his raised arms. (enBras)
	 */
	public void throwSomething() {
		// On jette un objet
		Element element = getEn_bras();
		setMouvement(MouvementZildo.VIDE);
		if (element != null) {
			setEn_bras(null);
			element.beingThrown(x, y, angle, this);
			EngineZildo.soundManagement.broadcastSound(BankSound.ZildoLance, this);
		}
	}

	/**
	 * Called when 'attente' is equals to 0.
	 */
	public void endMovement() {
		switch (mouvement) {
		case SOULEVE:
			setMouvement(MouvementZildo.BRAS_LEVES);
			break;
        case FIERTEOBJET:
        	if (getEn_bras() != null) {
        		getEn_bras().dying=true;
        	}
        	setAngle(Angle.SUD);
        case ATTAQUE_EPEE:
        case ATTAQUE_ARC:
        case ATTAQUE_BOOMERANG:
        case ATTAQUE_ROCKBAG:
			setMouvement(MouvementZildo.VIDE);		// Awaiting for key pressed
			break;
	}
		
	}
	/**
	 * Display Zildo's inventory around him
	 */
	public void lookInventory() {
		if (inventory.size() == 0) {
			// no inventory !
			EngineZildo.soundManagement.playSound(BankSound.MenuOutOfOrder, this);
			return;
		}
		Inventory inv = Inventory.fromItems(inventory);
		int sel = inv.indexOf(weapon);
		if (sel == -1) {
			sel = 0;
		}
		lookItems(inv, sel, this, null);
	}

	public int getIndexSelection() {
		return inventory.indexOf(getWeapon());
	}
	
	public void lookItems(Inventory p_items, int p_sel, Perso p_involved, String p_storeName) {
		inventoring = true;
		guiCircle = new ItemCircle(this);
		buying = p_storeName != null;
		guiCircle.create(p_items, p_sel, p_involved, buying);
		storeDescription = p_storeName;
	}

	/**
	 * Zildo buy an item at a store. Check his money, and add item to his
	 * inventory if he has enough.
	 */
	public void buyItem() {
		StoredItem stItem = guiCircle.getItemSelected();
		int remains = money - stItem.price;
		Item item = stItem.item;
		if (remains < 0) {
			// Not enough money
			EngineZildo.soundManagement.playSound(BankSound.MenuOutOfOrder, this);
		}/* else if (inventory.size() == 8) {
			// Too much items
			EngineZildo.soundManagement.playSound(BankSound.MenuOutOfOrder, this);
		}*/ else {
			money -= stItem.price;
			SpriteDescription d = item.kind.representation;
			if (item.kind.canBeInInventory()) {
				if (item.kind.canBeMultiple() || inventory.indexOf(item) == -1) {
					inventory.add(item);
				}
			}
			// Be sure that description is instance of ElementDescription, but more for runtime reason
			// In fact, that must never happen.
			if (d instanceof ElementDescription) {
				useItem((ElementDescription) d, 0);
			}
			guiCircle.decrementSelected();
			EngineZildo.scriptManagement.sellItem(storeDescription, item);
			EngineZildo.soundManagement.playSound(BankSound.ZildoGagneArgent, this);
		}
	}

	public void closeInventory() {
		EngineZildo.soundManagement.playSound(BankSound.MenuIn, this);
		guiCircle.close(); // Ask for the circle to close
		if (!buying) {
			weapon = guiCircle.getItemSelected().item;
			Perso perso = getDialoguingWith();
			if (perso != null) {
				perso.setDialoguingWith(null);
				setDialoguingWith(null);
			}
		}
	}

	/**
	 * Directly add an item to the inventory
	 * 
	 * @param p_item
	 */
	private void addInventory(Item p_item) {
		inventory.add(p_item);
		if (getWeapon() == null) {
			setWeapon(p_item);
        }
	}

	public boolean isInventoring() {
		return inventoring;
	}

	/**
	 * Zildo takes an item.
	 * 
	 * @param p_kind
	 * @param p_element
	 *            NULL if we have to spawn the element / otherwise, element
	 *            already is on the map.
	 */
	public void pickItem(ItemKind p_kind, Element p_element) {
		if (getEn_bras() == null) { // Doesn't take 2 items at 1 time
			addInventory(new Item(p_kind));
			attente = 40;
			mouvement = MouvementZildo.FIERTEOBJET;
			Element elem = p_element;
			if (elem == null) {
				elem = EngineZildo.spriteManagement.spawnElement(p_kind.representation,
						(int) x,
						(int) y, 0, Reverse.NOTHING, Rotation.NOTHING);
			}
			// Place item right above Zildo
			elem.x = x + 5;
			elem.y = y + 1;
			elem.z = 20f;
			setEn_bras(elem);
			EngineZildo.soundManagement.playSound(BankSound.ZildoTrouve, this);

			// Automatic behavior (presentation text, ammos adjustments)
			EngineZildo.scriptManagement.automaticBehavior(this, p_kind, null);

			// Adventure trigger
			if (!EngineZildo.game.multiPlayer) {
				TriggerElement trig = TriggerElement.createInventoryTrigger(p_kind);
				EngineZildo.scriptManagement.trigger(trig);
			}
		}
	}

	/**
	 * Zildo loose an item from his inventory.
	 * 
	 * @param p_kind
	 */
	public void removeItem(ItemKind p_kind) {
		int index = 0;
		for (Item i : inventory) {
			if (i.kind == p_kind) {
				if (super.getWeapon() == i) {
					setWeapon(null);
				}
				inventory.remove(index);
				return;
			}
			index++;
		}
	}

	/**
	 * Reduce item's quantity and focus on another one if necessary.
	 */
	public void decrementItem(ItemKind p_kind) {
		removeItem(p_kind);
		setWeapon(null);
		for (Item i : inventory) {
			if (i.kind == p_kind) {
				setWeapon(i);
			}
		}
	}
	
	/**
	 * Return TRUE if Zildo has an item from given kind.
	 * 
	 * @param p_kind
	 * @return boolean
	 */
	public boolean hasItem(ItemKind p_kind) {
		for (Item i : inventory) {
			if (i.kind == p_kind) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Item getWeapon() {
		Item item = super.getWeapon();
		if (item == null && !inventory.isEmpty()) {
			// No weapon is selected => take the first one, if it exists
			// This case occurs only after game has just been loaded
			weapon = inventory.get(0);
			item = weapon;
		}
		return item;
	}
	/**
	 * Return all Zildo's inventory. Useful for saving a game.
	 * 
	 * @return List<Item>
	 */
	public List<Item> getInventory() {
		return inventory;
	}

	/**
	 * Zildo avance contre un SpriteEntity
	 * 
	 * @param object
	 */
	public void pushSomething(Element object) {
		if (object == null || object.isPushable()) {
			pushingSprite = object;
		}
		if (object != null && object.getDesc().getBank() == SpriteBank.BANK_GEAR) {
			((ElementGear) object).push(this);
		}
	}

	public int getTouch() {
		return touch;
	}

	public void setTouch(int touch) {
		this.touch = touch;
	}

	public boolean isAlive() {
		return getPv() > 0;
	}

	public void setSightAngle(Angle sightAngle) {
		this.sightAngle = sightAngle;
	}
	
	public int getMoonHalf() {
		return moonHalf;
	}

	public void setMoonHalf(int moonHalf) {
		this.moonHalf = moonHalf;
	}
	
	int[] accels = new int[] { 0, 1, 1, 1, 2, 2, 3, 6, 8, 10, 10 };

	public float getAcceleration() {
		return accels[acceleration];
	}

	public void increaseAcceleration() {
		if (acceleration != 10) {
			acceleration += 1;
		}
	}

	public void decreaseAcceleration() {
		if (acceleration > 1) {
			acceleration -= 1;
		}
	}
	
	/** Hero HP increases, using 2 moon fragments. */
	public void gainHPWithNecklace() {
		moonHalf -= 2;
		maxpv+=2;
		pv = maxpv;
	}
	
	@Override
	public Collision getCollision() {
        int size = 7;
        int zildoY = (int) y-10;
        if (who == ControllablePerso.PRINCESS_BUNNY) {
        	size = 3;	// Squirrel is tinier
        	zildoY += 4;
        }
        return new Collision((int) x, zildoY, size, null, this, null, null);		
	}
	
	public void setAppearance(ControllablePerso who) {
		this.who = who;
		switch (who) {
		case ZILDO:
			shadow.setDesc(ElementDescription.SHADOW);
			feet.zoom=255;
			defaultSize = new Point(8, 4);
			break;
		case PRINCESS_BUNNY:
			shadow.setDesc(ElementDescription.SHADOW_SMALL);
			feet.zoom=100;
			defaultSize = new Point(2, 2);
			break;
		}
		appearance = who;
	}
	
	@Override
	public Angle tryJump(Pointf loc) {
		if (getPushingSprite() == null) {	// Don't allow jump with hero is pushing something
			Angle angleJump = super.tryJump(loc);
			if (angleJump != null) {
				Point landingPoint = angleJump.getLandingPoint().translate((int) x, (int) y);
				if (appearance == ControllablePerso.PRINCESS_BUNNY) {
					// Check if squirrel can go on this tile
					int z = EngineZildo.mapManagement.getTileBottomZ(landingPoint.x, landingPoint.y);
					if (z != 0) {
						jump(angleJump);
					}
				}
			}
		}
		return null;
	}
}