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
import zildo.fwk.gfx.EngineFX;
import static zildo.server.EngineZildo.hasard;
import zildo.monde.collision.Collision;
import zildo.monde.collision.DamageType;
import zildo.monde.items.Item;
import zildo.monde.items.ItemKind;
import zildo.monde.map.Area;
import zildo.monde.sprites.Reverse;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.SpriteModel;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.desc.SpriteAnimation;
import zildo.monde.sprites.desc.SpriteDescription;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.elements.ElementArrow;
import zildo.monde.sprites.elements.ElementGuardWeapon;
import zildo.monde.sprites.elements.ElementGuardWeapon.GuardWeapon;
import zildo.monde.sprites.elements.ElementImpact;
import zildo.monde.sprites.elements.ElementImpact.ImpactKind;
import zildo.monde.sprites.persos.action.ShotArrowAction;
import zildo.monde.sprites.utils.FlagPerso;
import zildo.monde.sprites.utils.MouvementPerso;
import zildo.monde.sprites.utils.MouvementZildo;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
import zildo.monde.util.Pointf;
import zildo.monde.util.Zone;
import zildo.resource.Constantes;
import zildo.server.EngineZildo;

//////////////////////////////////////////////////////////////////////
// Construction/Destruction
//////////////////////////////////////////////////////////////////////

public class PersoNJ extends Perso {

	ElementGuardWeapon guardWeapon;
	ElementDescription carriedItem;	// Object that monster will give when he died
	
	public PersoNJ() {
		super();
		setPos_seqsprite(0);
		pv = 1;
	}

	/** Special constructor for 'ghost' character => rarely used. See ActionExecutor#render for lookFor **/
	public PersoNJ(int id) {
		super(id);
	}
	
	@Override
	public void attack() {
		if (weapon != null) {
			switch (weapon.kind) {
				case HAMMER:
					Point smashLocation = angle.coords.multiply(16).translate(new Point(x, y));
					Collision c = new Collision(smashLocation.x, 
												smashLocation.y, 8, Angle.NORD, this, DamageType.SMASH, null);
					EngineZildo.collideManagement.addCollision(c);
					break;
				case BOW:
					action = new ShotArrowAction(this);
				default:
					break;
			}
		}
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// manageCollision
	// /////////////////////////////////////////////////////////////////////////////////////
	// -create collision zone for this character
	// /////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void manageCollision() {
		if ((getInfo() == PersoInfo.ENEMY || getInfo() == PersoInfo.SHOOTABLE_NEUTRAL) && getPv() > 0) {
			// Add this collision record to collision engine
			super.manageCollision();
			/*
			 * List<Element> elem=new ArrayList<Element>(); elem.add(this); elem.addAll(persoSprites); for (Element
			 * e:elem) { if (e.isSolid()) { Collision c=e.getCollision(); SpriteModel spr=e.getSprModel(); if (c ==
			 * null) { int size=(spr.getTaille_x() + spr.getTaille_y()) / 4; c=new Collision((int) e.x, (int) e.y, size,
			 * getAngle(), this, DamageType.BLUNT, null); } c.cy-=spr.getTaille_y() / 2; c.cy-=e.z;
			 * //EngineZildo.collideManagement.addCollision(c); } }
			 */
		}
	}

	public Collision getCollision() {
		if (desc == PersoDescription.BITEY) {
			// Bitey need a special collision (but this could be appended for someone else too)
			SpriteModel model = getSprModel();
			Zone borders = model.getEmptyBorders();
			int offX = 0;
			if (borders != null) {
				if (!reverse.isHorizontal()) {
					offX -= (borders.x1 +borders.x2 )/2;
					offX += borders.x1;	// Shift because between [x,x1] this is an empty border
				} else {
					offX -= (borders.x1-borders.x2)/2;
				}
			}
			
			return new Collision(new Point(x+offX, y), new Point(model.getTaille_x(), model.getTaille_y()), this, DamageType.BLUNT, null);
		}
		return super.getCollision();
	};
	
	// /////////////////////////////////////////////////////////////////////////////////////
	// beingWounded
	// /////////////////////////////////////////////////////////////////////////////////////
	// IN : cx,cy : enemy's position
	// /////////////////////////////////////////////////////////////////////////////////////
	// Invoked when this character gets wounded by any enemy (=ZILDO)
	// /////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void beingWounded(float cx, float cy, Perso p_shooter, int p_damage) {
		List<Element> linked = new ArrayList<>();
		// For butcher, remove progressively all his linked sprites
		if (desc == PersoDescription.BUTCHER) {
			for (SpriteEntity e : EngineZildo.spriteManagement.getSpriteEntities(null)) {
				if (e instanceof Element) {
					Element ee = (Element) e;
					if (ee.getLinkedPerso() == this) {
						linked.add(ee);
					}
				}
			}
		}
		project(cx, cy, 6);
		for (Element e : linked) {
			e.alphaA = -1;
		}
		setMouvement(MouvementZildo.TOUCHE);
		setWounded(true);
		if (quel_deplacement.isAlertable()) {
			setAlerte(true); // Zildo is detected, if it wasn't done !
		}
		setPv(getPv() - p_damage);
		setSpecialEffect(EngineFX.PERSO_HURT);

		EngineZildo.soundManagement.broadcastSound(BankSound.MonstreTouche, this);
		
		super.beingWounded(cx, cy, p_shooter, p_damage);
	}

	@Override
	public void parry(float cx, float cy, Perso p_shooter) {
		project(cx, cy, 2);
		EngineZildo.soundManagement.broadcastSound(BankSound.BoomerangTape, this);
		EngineZildo.spriteManagement.spawnSprite(new ElementImpact((int) x, (int) y, ImpactKind.SIMPLEHIT, null));

	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// stopBeingWounded
	// /////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void stopBeingWounded()
	{
		setPx(0.0f);
		setPy(0.0f);
		setX((int) getX());
		setY((int) getY());
		setWounded(false);
		initPersoFX();
		
		super.stopBeingWounded();
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// animate
	// /////////////////////////////////////////////////////////////////////////////////////
	// Common animation for PNJ. D
	// Move a PNJ to his target location (dx,dy) set by determineDestination()
	// /////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Common animation for PNJ. Do following things: <ol>
	 * <li>Run <b>custom action</b>, if any</li>
	 * <li>Is he dead, or dialoguing ? If so, quit.</li>
	 * <li><b>Jump</b> movement, if he is doing so.</li>
	 * <li><b>Projection</b>, if character is being wounded.</li>
	 * <li><b>Reach</b> his target, if any.</li>
	 * <li>Common move based on {@link MouvementPerso}.</li>
	 * <li>Call to specific {@link #move()} method.</li>
	 * </ol>
	 * @param compteur_animation
	 */
	@Override
	public void animate(int compteur_animation) {

		super.animate(compteur_animation);

		// If character has a delegate action, then do nothing else
		if (action != null) {
			blinkIfWounded();
			move();
		} else {
	
			if ((getPv() == 0 && !isWounded() )|| getDialoguingWith() != null) {
				return;
			}
	
	
			if (mouvement == MouvementZildo.SAUTE) {
				moveJump();
			} else {
				blinkIfWounded();
	
				move();
			}
		}
		if (!askedVisible) {
			setVisible(false);
		}
	}

	private void blinkIfWounded() {
		if (px != 0.0f || py != 0.0f) {
			// Character gets hurt !
			if (desc == null || ((PersoDescription)desc).isProjectable()) {	// Don't project bramble or turret !
				Pointf location = tryMove(px, py);
				x = location.x;
				y = location.y;
			}
			px *= 0.9f;
			py *= 0.9f;
			setAttente(0);
			if ((Math.abs(px) + Math.abs(py)) < 0.4f) {
				stopBeingWounded();
			}
		}		
	}
	/**
	 * Move method for PNJ. Classes deriving from this one should override this method
	 * for specific moves. Note that common one like projection, or target reaching and so on,
	 * are handled by {@link #animate(int)} method.
	 */
	public void move() {
		if (pv <=0) {
			return;
		}
		PersoPlayer zildo = EngineZildo.persoManagement.getZildo();
		float sx = getX(), sy = getY();

		if (isAlerte() && MouvementPerso.VOLESPECTRE != quel_deplacement
				&& MouvementPerso.ZONEARC != quel_deplacement) {
			// Zildo has been caught, so the monster try to reach him, or run away (hen)
			boolean fear = quel_deplacement.isAfraid();
			reachAvoidTarget(zildo, fear);
			walkTile(true);
		} else {
			Area area = EngineZildo.mapManagement.getCurrentMap();
			if (!isAlerte() && quel_deplacement.isAlertable() && area.isAnAlertAtLocation(x, y)) {
				setAlerte(true);
			}
			// Common moves
			if (zildo != null) {
				switch (quel_deplacement) {
				case VOLESPECTRE:
					double beta;
					if (cptMouvement == 100) {
						if (desc == PersoDescription.CORBEAU) {
							// Black bird : focus moving zone on Zildo
							int pasx, pasy;
							if ((int) zildo.x / 16 > x / 16) {
								pasx = 16;
							} else {
								pasx = -16;
							}
							if ((int) zildo.y / 16 > y / 16) {
								pasy = 16;
							} else {
								pasy = -16;
							}
							zone_deplacement.incX1(pasx * 3);
							zone_deplacement.incY1(pasy * 3);
							zone_deplacement.incX2(pasx * 3);
							zone_deplacement.incY2(pasy * 3);
						}
						attente = 1 + (int) Math.random() * 5;
						if (pathFinder.getTarget() == null || desc == PersoDescription.CORBEAU) {
							pathFinder.determineDestination();
						}
						cptMouvement = 0;
					} else if (attente != 0) {
						attente--;
					} else {
						if (desc == PersoDescription.CORBEAU) {
							if (pos_seqsprite != 0) {
								pos_seqsprite = (4 * Constantes.speed) + (pos_seqsprite - 4 * Constantes.speed + 1)
										% (8 * Constantes.speed);
							} else {
								// Est-ce que Zildo est dans les parages ?}
								beta = x - zildo.x;
								float vitesse = y - zildo.y;
								beta = Math.sqrt(beta * beta + vitesse * vitesse);
								if (beta < 16 * 5) {
									pos_seqsprite = 4 * Constantes.speed;
								}
								break;
							}
						}
						// On se déplace en courbe
						pathFinder.reachDestination(0); // Speed is unused
						cptMouvement++;
					}
					break;
				case HEN:
					if (z > 0) { // La poule est en l'air, elle n'est plus libre de ses mouvements
						physicMoveWithCollision();
					} // Sinon elle agit comme les scripts de zone
					break;
				case ZONEARC:
					if (!isWounded() && isAlerte() && zildo.isAlive() && attente == 0) {
						// Get the enemy aligned with Zildo to draw arrows
						int xx = (int) getX();
						int yy = (int) getY();
						int deltaX = Math.abs((int) (zildo.x - xx-2));
						int deltaY = Math.abs((int) (zildo.y - yy));
						// Alert potential allies
						EngineZildo.mapManagement.getCurrentMap().alertAtLocation(new Point(zildo.x, zildo.y));
						if (zildo.floor <= floor && (deltaX <= 1 || deltaY <= 1)) {
							// Get sight on Zildo and shoot !
							Angle previousAngle = angle;
							sight(zildo, false);
							// If character switched angle, we need to recalculate his bow location
							if (angle != previousAngle) getEn_bras().animate();
							// First, assure that no obstacle is on the way
							Point deltaCheck = angle.coords.multiply(16);
							ElementArrow simulatedArrow = new ElementArrow(angle, (int)getEn_bras().x, (int)getEn_bras().y, 0, this);
							Point arrowPrevisionPos = new Point(simulatedArrow.x, simulatedArrow.y);
							boolean obstacle = false;
							arrowPrevisionPos.y -= (getSprModel().getTaille_y() >> 1) / 2;	// Adjust arrow hitbox
							for (int pas=0;pas<5 && !obstacle;pas++) {
								arrowPrevisionPos.add(deltaCheck);
								if (arrowPrevisionPos.distance(new Point(zildo.getX(), zildo.getY())) < 8) {
									break;
								}
								if (EngineZildo.mapManagement.collide(arrowPrevisionPos.x, arrowPrevisionPos.y, this)) {
									obstacle = true;
								}
							}
							if (!obstacle) {
								action = new ShotArrowAction(this);
							} else {
								setAttente(30);
							}
							break;
						} else if (pathFinder.hasNoTarget()) {
							// Gets on a right position to shoot Zildo
							if (deltaX <= deltaY) {
								pathFinder.setTarget(new Pointf(zildo.x-2, yy));
							} else {
								pathFinder.setTarget(new Pointf(xx, zildo.y));
							}
						}
					} else if (lookForZildo(angle)) {
						setAlerte(true);
					}
					break;
				case WAKEUP:
					pos_seqsprite++;
					break;
				case FOLLOW:
					pathFinder.determineDestination();
					break;
				case CHAIN_FOLLOW:
					Pointf p = pathFinder.reachDestination(0);
					x=p.x;
					y=p.y;
					break;
				default:
					break;
				}
			}
			if (quel_deplacement != MouvementPerso.OBSERVE &&
					quel_deplacement != MouvementPerso.VOLESPECTRE &&
					quel_deplacement != MouvementPerso.SLEEPING &&
					quel_deplacement != MouvementPerso.CHAIN_FOLLOW) {
				if (pathFinder.hasReachedTarget()) {
					pathFinder.setTarget(null);
					pathFinder.setTargetZ(null);
					destinationReached();
				}
				if (!isGhost() && info == PersoInfo.ENEMY && !isAlerte() && 
						(quel_deplacement == null || quel_deplacement.isAlertable())) {
					setAlerte(lookForZildo(angle));
				}
				if (this.getAttente() > 0) {
					if (desc == PersoDescription.BAS_GARDEVERT) {
						// Turns his head around to look for Zildo
						if (attente == 1 && cptMouvement < 3) {
							if (!alerte
									&& lookForZildo(angle.rotate(PersoGardeVert.mouvetete[cptMouvement]))) {
								alerte = true;
								EngineZildo.soundManagement.broadcastSound(BankSound.MonstreTrouve, this);
							}
							cptMouvement++;
							setAttente(20);
						}
					}
					attente--;
					// Stop hen's movements when it's flying (TODO : this isn't very clean, idem for fish !)
				} else if (quel_deplacement != MouvementPerso.HEN || z == 0 || (desc == PersoDescription.FISH && !flying)) {
					// On déplace le PNJ
					if (pathFinder.hasNoTarget() && quel_deplacement.isMobileZone()) {
						// Pas de destination, donc on en fixe une dans la zone de déplacement
						cptMouvement = 0;

						pathFinder.determineDestination();
					}
					float vitesse = pathFinder.speed;
					/*
					if (quel_deplacement == MouvementPerso.RAT) {
						// Script du rat => plus rapide, et crache des pierres}
						vitesse += 1.5;
						pos_seqsprite = pos_seqsprite % (8 * Constantes.speed - 1);
						if (quel_spr == PersoDescription.CRABE && Math.random() * 40 == 2) {
							// On crache une boule de pierre}
							pos_seqsprite = 8 * Constantes.speed;
							EngineZildo.spriteManagement.spawnSpriteGeneric(SpriteAnimation.ROCKBALL, (int) x,
									(int) y,
									(int) (angle.value + Math.random() * 4) // Attention : math.random() était 'i'
																			// en pascal
									, null, null);
							attente = (int) (Math.random() * 5);
						}
					} else if (quel_deplacement == MouvementPerso.ELECTRIC) {
						vitesse = 0.2f;
					}
*/
					if (!pathFinder.hasNoTarget()) { // Move character if he has a target
						Pointf loc = pathFinder.reachDestination(vitesse);
						// Check for infinite movement (A => B => A => B ...)
						boolean hasCollided = loc.x == x && loc.y == y;
						if ( (deltaMoveX != 0 || deltaMoveY != 0) && loc.isSame(new Pointf(x-deltaMoveX, y-deltaMoveY))) {
							pathFinder.setTarget(null);
						} else {
							boolean slowDown = walkTile(true);
							
							if (slowDown) {
								x = (loc.x + x) / 2;
								y = (loc.y + y) / 2;
							} else {
								x = loc.x;
								y = loc.y;
							}
						}

						if (mover != null) {	// Move people on the character
							Pointf delta = new Pointf(x-prevX, y-prevY);
							mover.takePeople(delta);
						}
						
						// suite_mouvement
						if (quel_deplacement == MouvementPerso.BEE) {
							angle = Angle.fromInt(angle.value & 2);
						}
						// Does character reach his destination ?
						if (pathFinder.hasNoTarget()) {
							destinationReached();
							pos_seqsprite = 0;
						}

						if (mouvement!=MouvementZildo.SAUTE) {
							// Collision ?
							if (!quel_deplacement.isFlying() && hasCollided) {
								this.setX(sx);
								this.setY(sy);
								pathFinder.collide();
								pos_seqsprite = 0;
							} else {
								pos_seqsprite = (pos_seqsprite + 1) % 512;
							}
						}
					}
				}
			}
		}
	}
	
	// /////////////////////////////////////////////////////////////////////////////////////
	// initPersoFX
	// /////////////////////////////////////////////////////////////////////////////////////
	// Set perso with initial special effect.
	// /////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void initPersoFX() {
		if (desc == PersoDescription.GARDE_CANARD || desc == PersoDescription.FOX) { // Guard
			String str = getEffect() != null ? getEffect() : getName();
			if ("jaune".equals(str)) {
				setSpecialEffect(EngineFX.GUARD_YELLOW);
			} else if ("vert".equals(str)) {
				setSpecialEffect(EngineFX.GUARD_GREEN);
			} else if ("rouge".equals(str)) {
				setSpecialEffect(EngineFX.GUARD_RED);
			} else if ("rose".equals(str)) {
				setSpecialEffect(EngineFX.GUARD_PINK);
			} else if (str.startsWith("noir")) {
				setSpecialEffect(EngineFX.GUARD_BLACK);
			} else if ("bleu".equals(str)) {
				setSpecialEffect(EngineFX.ROBBER_BLUE);
			} else if ("focused".equals(str)) {
				setSpecialEffect(EngineFX.FOCUSED);
			} else {
				// Default color for this guard : blue
				setSpecialEffect(EngineFX.GUARD_BLUE);
			}
		} else if ("fire".equals(getEffect())) {
			addFire();
		} else {
			setSpecialEffect(EngineFX.NO_EFFECT);
		}
	}

	final int[] seqWakeUp = { 0, 2, 0, 2, 3 };

	/**
	 * finaliseComportement : 
	 * Manage character's graphic side, depending on the position in the animated sequence.
	 * 
	 * NOTE: There's a dirty things here, about "addSpr" field of Perso.
	 * Here, we calculate 'nSpr' with initial value of 'nSpr' + PersoDesription.nth(addSpr).
	 * In most of the cases, this is harmless, but if the PersoDescription contains a non-contiguous 
	 * sequence of sprites (example {@link PersoDescription#FISH}) this will lead to a problem.
	 * Symptom is when game is paused : 'move' method of Perso isn't called anymore, so if it handle
	 * the 'addSpr' set, a sprite could be badly rendered, with the wrong graphics ... (arg)
	 */
	@Override
	public void finaliseComportement(int animationCounter) {

		int compteur_animation = animationCounter;
		
		if ((flagBehavior & FlagPerso.F_STATIC) != 0) {
			compteur_animation = 0;
		}
		
		this.setAjustedX((int) x);
		this.setAjustedY((int) y);

		final int[] seqp = { 0, 2, 0, 1 }; // 3 sprites characters
		final int[] seqv = { 0, 1, 2, 1 }; // another 3 sprites
		final int[] seqbadguy = { 0, 1, 2, 1, 0, 1, 2, 1};
		final int[] seqbadguyreverse = { 0, 1, 2, 1, 3, 4, 5, 4};
		
		int add_spr = 0;
		PersoDescription quelSpriteWithBank = (PersoDescription) desc;

		int seq2 = computeSeq(3) % 2;

		// Animated sequence adjustment

		switch (quelSpriteWithBank) {
		case GARDE_CANARD:

			break;
		case POULE:
		case CANARD:
		case GREY_CAT:
		case BROWN_CAT:
			// Poule
			if (linkedPerso != null) {
				pos_seqsprite++;
			}
			add_spr = computeSeq(2) % 4;
			if (add_spr > 1) {
				if (quel_deplacement == MouvementPerso.HEN) {
					setAjustedY(getAjustedY() - (add_spr - 1));
				}
				add_spr = 1;
			}
			reverse = Reverse.NOTHING;
			if (deltaMoveX > 0) {
				reverse = Reverse.HORIZONTAL;
			}
			if (quel_deplacement == MouvementPerso.CAT && !alerte && deltaMoveX == 0 && deltaMoveY == 0) {
				add_spr = 2;
			}
			break;
		case VIEUX:
			// Vieux saoul à 3 sprites
			if ((int) (Math.random() * 30) == 2) {
				setPos_seqsprite((int) (Math.random() * 3));
			}
			add_spr = getPos_seqsprite();
			break;
		case VIEILLE_BANC:
			if ((int) (Math.random() * 30) == 2) {
				setPos_seqsprite(Math.random() > 0.5 ? 0 : 1);
			}
			add_spr = getPos_seqsprite();
			break;
		case MOUSTACHU:
		case FISHER:
		case MINSK:
		case INVENTOR:
		case COOK:
		case HECTOR:
		case ELEORIC:
			reverse = seq2 == 0 ? Reverse.NOTHING : Reverse.HORIZONTAL;
			switch (angle) {
			case NORD:
				add_spr = 1;
				break;
			case EST:
				add_spr = 2 + seq2;
				reverse = Reverse.HORIZONTAL;
				break;
			case SUD:
				add_spr = 0;
				break;
			case OUEST:
				add_spr = 2 + seq2;
				reverse = Reverse.NOTHING;
			default:
				break;
			}
			break;
		case VIEUX_SAGE:
		case VIEUX_SAGE3:
		case BUCHERON_ASSIS:
		case MOUSTACHU_ASSIS:
		case BUCHERON_DEBOUT:
		case KING:
			// Persos à 1 seul sprite
			add_spr = 0;
			break;
		case BANDIT_CHAPEAU:
			add_spr = (angle.value % 4) * 2;
			if (angle == Angle.OUEST) {
				add_spr = 2;
				reverse = Reverse.HORIZONTAL;
			} else {
				reverse = Reverse.NOTHING;
			}
			add_spr+=seq2;
			break;
		case VOYANT:
		case DRESSEUR_SERPENT:
		case SORCIERE:
		case VIEILLE_BALAI:
		case ALCOOLIQUE:
		case SORCIER_CAGOULE:
		case VIEUX_SAGE2:
			// Persos toujours de face, à 2 sprites seulement
			add_spr = (compteur_animation / 30) % 2;
			break;
		case SORCERER:
			if (((compteur_animation / 30) % 2) == 0) {
				reverse = Reverse.HORIZONTAL;
			} else {
				reverse = Reverse.NOTHING;
			}
			break;
		case ENFANT:
		case VOLEUR:
		case BANDIT:
			// Persos à 3 angles
			add_spr = seqp[angle.value];
			break;
		case GARCON_BRUN:
		case GARCON_BLEU:
		case GARCON_JAUNE:
			add_spr = seqp[angle.value];
			reverse = Reverse.NOTHING;
			if (add_spr == 2) {
				add_spr = 1;
				reverse = Reverse.HORIZONTAL;
			}
			break;
		case CURE:
		case GARDE_BOUCLIER:
			// Persos à 4 sprites : prêtre,garde bouclier
			add_spr = angle.value;
			break;
		case VAUTOUR:
			// Persos à 3 sprite et 1 angle
			add_spr = seqv[(compteur_animation / 20) % 4];
			shadow.setDesc(ElementDescription.SHADOW);
			shadow.setVisible(true);
			break;
		case SPECTRE:
			// Perso à 2 sprites (gauche/droite)
			add_spr = angle.value;
			break;
		case CORBEAU:
		case CRABE:
			// Persos à 3 sprites par angle
			add_spr = angle.value * 3 + computeSeq(4) % 3;
			break;
		case ABEILLE:
			add_spr = (angle.value & 2) + (compteur_animation / 30) % 2;
			break;
		case OISEAU_VERT:
			if (angle == Angle.OUEST) {
				reverse = Reverse.HORIZONTAL;
			} else {
				reverse = Reverse.NOTHING;
			}
			add_spr = (compteur_animation % 32) / 16;
			break;
		case LAPIN:
		case PRINCESS_BUNNY:
			addSpr = pathFinder.getTarget() == null ? 0 : 1;
		case RABBIT:
		case CACTUS:
			reverse = angle == Angle.OUEST ? Reverse.HORIZONTAL : Reverse.NOTHING;
			break;
		case PRINCESSE_COUCHEE:
			int seqPos = (getPos_seqsprite() / 40) % 5;
			add_spr = seqWakeUp[seqPos];
			break;
		case CHAUVESOURIS:
		case FIRETHING:
			add_spr = 0;
			break;
		case RAT:
			add_spr = angle.value * 2;
			if (angle == Angle.EST){
				reverse = Reverse.HORIZONTAL;
			} else {
				reverse = Reverse.NOTHING;
				if (angle == Angle.OUEST) {
					add_spr = 2;
				}
			}
			int varying = computeSeq(2) % 2;
			if (angle.isHorizontal()) {
				add_spr += varying;
			} else if (varying == 1) {
				reverse = (reverse == Reverse.HORIZONTAL) ? Reverse.NOTHING : Reverse.HORIZONTAL;
			}
			break;
		case BIG_RAT:
			add_spr = angle.value * 2;
			if (angle == Angle.EST){
				reverse = Reverse.HORIZONTAL;
			} else {
				reverse = Reverse.NOTHING;
				if (angle == Angle.OUEST) {
					add_spr = 2;
				}
			}
			int vary = computeSeq(2) % 2;
			add_spr+=vary;
			break;
		case FISH:
			add_spr = addSpr;
			addSpr = 0;
			break;
		case PANNEAU:
		case PAPER_NOTE:
		case STONE_SPIDER:
		case FOX:
		case FALCOR:
		case SLEEPING_KING:
		case FIREFLY:
		case BRAMBLE:
		case VOLANT_BLEU:
		case ECTOPLASME:
		case ARBUSTE_VIVANT:
		case SINGER:
		case HAUT_GARDEVERT:
			add_spr = 0;
			break;
		case FLYINGSERPENT:
			reverse = angle == Angle.OUEST ? Reverse.HORIZONTAL : Reverse.NOTHING;
			add_spr = (getPos_seqsprite() % (6 * Constantes.speed)) / (2 * Constantes.speed);
			break;
		case IGOR:
			add_spr = (angle.value * 3); reverse = Reverse.NOTHING;
			if (angle == Angle.OUEST) {
				add_spr =3; reverse = Reverse.HORIZONTAL;
			}
			add_spr += seqp[computeSeq(3) % 4];
			break;
		case BUTCHER:
			reverse = Reverse.NOTHING;
			int s = computeSeqPositive(4);
			switch (angle) {
			case NORD:
				add_spr = 5 + s % 2; 
				reverse = s%4 > 1 ? Reverse.HORIZONTAL : Reverse.NOTHING; break;
			case OUEST:
				reverse = Reverse.HORIZONTAL;
			case EST:
				add_spr = 2; 
				add_spr += seqv[s%4];
				break;
			case SUD:
				add_spr = s % 2; 
				reverse = s%4 > 1 ? Reverse.HORIZONTAL : Reverse.NOTHING; 
				break;
			}
			break;
		case TURTLE:
			reverse = angle == Angle.OUEST ? Reverse.HORIZONTAL : Reverse.NOTHING;
			if (getAddSpr() < 8) {
				add_spr = computeSeq(2) % 3;
				boolean smallShadow = false;
				switch (angle) {
				case EST:
				case OUEST:
					// Shadow adjustment
					shadow.setX(x + (angle == Angle.OUEST ? 3 : -3));
					shadow.setY(y-2);
					if (getAddSpr() < 3) {	// Turtle in its shelf
						smallShadow = true;
						shadow.setX(x);
						shadow.setY(y-3);
						if (getAddSpr() == 2) shadow.setY(y-2);
					} else {
						if (getAddSpr() == 3) shadow.setX(x);
					}
					break;
					//0 1 2 3 1 2 3
					case SUD:
						add_spr = 7 + computeSeq(2) % 7;
						smallShadow = true;
						shadow.setY(y-2);
						break;
					case NORD:
						add_spr = 14 + computeSeq(2) % 7;
						smallShadow = true;
						shadow.setY(y-2);
					default:
						break;
				}
				shadow.setDesc(smallShadow ? ElementDescription.SHADOW : ElementDescription.SHADOW_LARGE);
			} else {	// Turtle head => no shadow
				shadow.setVisible(false);
			}
			break;
		case TURRET_HEART:
			if (addSpr == 0) {
				if (shadow == null) addShadow(ElementDescription.SHADOW_LARGE);
				shadow.setVisible(true);
				shadow.setY(y+2);
				break;
			}	// No break is intentional
		case TURRET:
			if (shadow != null) {
				shadow.setVisible(false);
			}
			break;
		case FIRE_ELEMENTAL:
		case COAL:
		case COAL_COLD:
		case BITEY:
		case MOLE:
			break;
		case DARKGUY:
			int cs = computeSeqPositive(4);
			if (angle == Angle.SUD) {
				add_spr = seqbadguyreverse[cs % seqbadguy.length];
				if ((cs % 8) > 3)
				reverse = Reverse.NOTHING;
			} else if (angle == Angle.NORD) {
				add_spr = 6 + seqbadguy[cs % seqbadguy.length];
				reverse = (cs % 8) > 4 ? Reverse.HORIZONTAL : Reverse.NOTHING;
			}
			if ("thrower1".equals(name)) {
				//System.out.println(add_spr + " " + reverse);
			}
			break;
		case SCORPION:
			Reverse r = Reverse.NOTHING;
			switch (angle) {
			case NORD:
				add_spr = 5;
				break;
			case NORDEST:
				add_spr = 4;
				break;
			case EST:
				add_spr = 3;
				break;
			case SUDEST:
				add_spr = 0;	// 1 for animation
				r = Reverse.HORIZONTAL;
				break;
			case SUD:
				add_spr = 2;
				break;
			case SUDOUEST:
				add_spr = 0;
				break;
			case OUEST:
				add_spr = 3;
				r = Reverse.HORIZONTAL;
				break;
			case NORDOUEST:
				add_spr = 4;
				r = Reverse.HORIZONTAL;
				break;
			}
			reverse = r;
			break;
		default:
			add_spr = angle.value * 2 + computeSeq(2) % 2;
			break;
		}

		PersoDescription d = (PersoDescription) desc;
		switch (d) {
		case PRINCESSE:
			if (angle == Angle.OUEST) {
				add_spr -= 4;
				reverse = Reverse.HORIZONTAL;
			} else {
				reverse = Reverse.NOTHING;
			}
			break;

		case SOFIASKY:
		case FERMIERE:
		case LOUISE:
		case VACTO:
			reverse = Reverse.NOTHING;
			if (add_spr >= 6) {
				add_spr -= 4;
				reverse = Reverse.HORIZONTAL;
			}
			if (angle == Angle.NORD || angle == Angle.SUD) {
				// 3-sprites sequence from 2 with reverse
				add_spr = computeSeq(2) % 4;
				if (add_spr == 3) {
					add_spr = 1;
					reverse = Reverse.HORIZONTAL;
				} else if (add_spr == 2) {
					add_spr = 0;
				}
				add_spr += angle.value * 2;
			}
			break;
		case MOUSTACHU_ASSIS:
			if (quel_deplacement != MouvementPerso.IMMOBILE) {
				setDesc(PersoDescription.MOUSTACHU);
			}
		default:
			break;
		}

		this.setNSpr(d.nth(add_spr));
		super.finaliseComportement(compteur_animation);
	}

	/**
	 * Reach or run away from a given character.
	 * 
	 * @param p_perso
	 *            target character
	 * @param p_fear
	 *            TRUE=avoid / FALSE=reach
	 */
	public void reachAvoidTarget(Perso p_perso, boolean p_fear) {
		float sx = x;
		float sy = y;
		cptMouvement = 0;
		float ddx = p_perso.getX();
		float ddy = p_perso.getY();
		if (p_fear) {
			// Character should run away instead of reach the character p_perso
			ddx = 2 * x - ddx;
			ddy = 2 * y - ddy;
		}
		if ((int) ddx > (int) sx) {
			setX(getX() + Constantes.MONSTER_SPEED);
		} else if ((int) ddx < (int) x) {
			setX(getX() - Constantes.MONSTER_SPEED);
		}
		if ((int) ddy > (int) y) {
			setY(getY() + Constantes.MONSTER_SPEED);
		} else if ((int) ddy < (int) y) {
			setY(getY() - Constantes.MONSTER_SPEED);
		}
		setPos_seqsprite((getPos_seqsprite() + 1) % 512);
		if (EngineZildo.mapManagement.collide((int) x, (int) y, this)) {
			// Le monstre est gêné par un obstacle
			if (!EngineZildo.mapManagement.collide((int) sx, (int) y, this)) {
				setX(sx);
			} else if (!EngineZildo.mapManagement.collide((int) x, (int) sy, this)) {
				setY(sy);
			} else {
				setX(sx);
				setY(sy);
				setPos_seqsprite(0);
				pathFinder.setTarget(null);
				setAlerte(false);
				setAttente(10);
				// On replace la zone de déplacement autour de l'ennemi
				setZone_deplacement(EngineZildo.mapManagement.range(x - 16 * 5, y - 16 * 5, x + 16 * 5, y + 16 * 5));
			}
		}
		if (!isGhost()) { // Replace angle if character isn't ghost (moved by script)
			if (x > sx) {
				angle = Angle.EST;
			} else if (x < sx) {
				angle = Angle.OUEST;
			} else if (y > sy) {
				angle = Angle.SUD;
			} else {
				angle = Angle.NORD;
			}
			if (p_fear) {
				angle = angle.rotate(2);
			}
		}
		if (sx != x && sy != y) { // Diagonal moves
			setX(sx + (x - sx) * 0.8f);
			setY(sy + (y - sy) * 0.8f);
		}

	}

	public void destinationReached() {
		if (!isGhost() && quel_deplacement != MouvementPerso.BEE
				&& (quel_deplacement != MouvementPerso.RAT || hasard.lanceDes(8))) {
			switch (quel_deplacement) {
			case BEE:
				break;	// No wait
			case CAT:
				setAttente(60 + hasard.rand(40));
				break;
			case RAT:
				if (hasard.lanceDes(8)) {
					break;
				}
			case SQUIRREL:
				setAttente(5 + hasard.rand(15));
				break;
			case HEN:
				setAttente(10 + hasard.rand(20));
			default:
				break;
			}
		}
	}

	/**
	 * Look for Zildo around him, only if the character is "alertable".
	 * @return TRUE = Zildo is around the character / FALSE = not in the field of view
	 */
	boolean lookForZildo(Angle p_angle) {
		if (!quel_deplacement.isAlertable()) {
			return false;
		}
		PersoPlayer zildo = EngineZildo.persoManagement.getZildo();
		if (zildo == null || !zildo.isAlive()) {	// Maybe he's dead ?
			return false;
		}
		int dix, diy;
		boolean temp, r;
		final int DISTANCEMAX = 16 * 6;
		r = false;
		// On calcule la distance en x et en y entre le perso et Zildo
		dix = (int) (x - zildo.x);
		diy = (int) (y - zildo.y);
		temp = (Math.abs(dix) > Math.abs(diy));
		if (p_angle.isHorizontal() == temp) {
			switch (p_angle) {
			case NORD:
				if (diy > 0 && diy < DISTANCEMAX) {
					r = true;
				}
				break;
			case EST:
				if (dix < 0 && dix > -DISTANCEMAX) {
					r = true;
				}
				break;
			case SUD:
				if (diy < 0 && diy > -DISTANCEMAX) {
					r = true;
				}
				break;
			case OUEST:
				if (dix > 0 && dix < DISTANCEMAX) {
					r = true;
				}
			default:
				break;
			}
		}
		return r;
	}

	@Override
	public void die(boolean p_link, Perso p_shooter) {
		super.die(p_link, p_shooter);

		if (info == PersoInfo.ENEMY) {
			// Monster just died. We check, a bonus may appear.
			int m = 0;
			SpriteAnimation anim = null;
			
			ElementDescription itemDesc = carriedItem;
			if (carriedItem != null) {
				// Item is configured to show up when he dies
				anim = SpriteAnimation.FROMGROUND;
			} else {
				int k = hasard.de6();
				switch (k) {
				case 5:
				case 6:
					anim = SpriteAnimation.BLUE_DROP;
					break;
				case 3:
				case 4:
					anim = SpriteAnimation.GOLDCOIN;
					break;
				case 1:
					anim = SpriteAnimation.GOLDCOIN;
					m = 2;
					break;
				}
			}
			if (anim != null) {
				Element elem = EngineZildo.spriteManagement.spawnSpriteGeneric(anim, (int) x, (int) y, floor, m, null, itemDesc);
				if (carriedItem != null) {
					elem.setName(name); // Propagate character's name to carried item, in order to identify him in quests
					elem.setTrigger(true);
				}
			}
			if (shadow != null) {
				shadow.dying = true;
			}
			EngineZildo.scriptManagement.stopPersoAction(this);
		}
	}

	protected void initWeapon() {
		guardWeapon = new ElementGuardWeapon(this);
		addPersoSprites(guardWeapon);
		setEn_bras(guardWeapon);
	}

	/**
	 * Modify character's weapon. Suppose that {@link GuardWeapon} has been properly initialized.
	 * @param weapon GuardWeapon
	 */
	public void setActiveWeapon(GuardWeapon weapon) {
		if (guardWeapon != null) {
			guardWeapon.setWeapon(weapon);
			switch (weapon) {
			case BOW:
				setWeapon(new Item(ItemKind.BOW));
				break;
			case SPEAR:
				break;
			case SWORD:
				setWeapon(new Item(ItemKind.SWORD));
				break;
			}
			guardWeapon.setVisible(weapon != null);
		}
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
			default:
			
		}
	}
	
	public void setCarriedItem(ElementDescription p_desc) {
		carriedItem = p_desc;
	}
}