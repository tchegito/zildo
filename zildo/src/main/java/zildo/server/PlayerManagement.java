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

import zildo.Zildo;
import zildo.client.PlatformDependentPlugin;
import zildo.client.PlatformDependentPlugin.KnownPlugin;
import zildo.client.sound.BankSound;
import zildo.fwk.input.KeyboardInstant;
import zildo.fwk.input.KeyboardState;
import zildo.monde.dialog.WaitingDialog.CommandDialog;
import zildo.monde.items.ItemKind;
import zildo.monde.map.Area;
import zildo.monde.map.Tile;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.desc.SpriteAnimation;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.Perso.PersoInfo;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.sprites.utils.MouvementZildo;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
import zildo.monde.util.Pointf;
import zildo.monde.util.Vector2f;
import zildo.resource.KeysConfiguration;
import zildo.server.state.ClientState;
import zildo.server.state.DialogState;
import zildo.server.state.GamePhase;


public class PlayerManagement {

	private PersoPlayer heros;
	private KeyboardInstant instant;
	private KeyboardState keysState;
	private DialogState dialogState;
	private ClientState client;
	
	private GamePhase gamePhase;
	
	public PlayerManagement()
	{
	}
	
	static float cosPiSur4 = 0.66f; //(float) Math.cos(Math.PI / 4f);

	///////////////////////////////////////////////////////////////////////////////////////
	// manageKeyboard
	///////////////////////////////////////////////////////////////////////////////////////
	//
	// Main method
	// ///////////
	// -dispatch the keyboard management between various situations:
	// *regular moving on the map
	// *conversation
	///////////////////////////////////////////////////////////////////////////////////////
	public void manageKeyboard(ClientState p_state) {
		// Save key state
		heros=p_state.zildo;
		instant=p_state.keys;
		keysState=p_state.keysState;
		dialogState=p_state.dialogState;
		client=p_state;
		
		// Determine the game phase
		boolean ghost=heros.isGhost();
		if (EngineZildo.scriptManagement.isScripting()) {
			gamePhase=GamePhase.SCRIPT;
		} else if (client.event.mapChange) {
			gamePhase=GamePhase.MAPCHANGE;
		} else if (dialogState.isDialoguing()) {
			if (heros.isInventoring()) {
				gamePhase=GamePhase.BUYING;
			} else {
				gamePhase=GamePhase.DIALOG;
			}
		} else {
			gamePhase=GamePhase.INGAME;
		} 

		Vector2f dir = instant.getDirection();
		if (Zildo.recordMovements) {
			boolean oneKeyPressed = false;
			for (KeysConfiguration key : KeysConfiguration.values()) {
				if (instant.isKeyDown(key)) {
					EngineZildo.game.recordMovement(dir, key);
					oneKeyPressed = true;
				}
			}
			if (!oneKeyPressed && dir != null) {
				EngineZildo.game.recordMovement(dir, null);
			}
		}
		// Specific for touchscreen : "touch frame" is equivalent to "touch Action key"
		// Except for BUYING action ! 
		if (dialogState.isDialoguing() && gamePhase != GamePhase.BUYING && PlatformDependentPlugin.currentPlugin == KnownPlugin.Android) {
			instant.setKeyMerged(KeysConfiguration.PLAYERKEY_ACTION, 
					KeysConfiguration.PLAYERKEY_DIALOG
					//KeysConfiguration.PLAYERKEY_ATTACK,
					//KeysConfiguration.PLAYERKEY_ACTION
					);
		}
		if (heros.isInventoring()) {
			boolean allow = true;
			if (dir != null) {
				// Refuses to leave inventory if user presses a diagonal movement => we prefer switching items instead
				Angle ang = Angle.fromDirection(Math.round(dir.x), Math.round(dir.y));
				allow &= ang != Angle.SUDOUEST && ang != Angle.SUDEST;
			}
			if (allow) {
			instant.setKeyMerged(KeysConfiguration.PLAYERKEY_INVENTORY,
					KeysConfiguration.PLAYERKEY_UP, 
					KeysConfiguration.PLAYERKEY_INVENTORY,
					KeysConfiguration.PLAYERKEY_DOWN
					//KeysConfiguration.PLAYERKEY_ACTION,	// These keys was added for gamepad support, but ruins game experience (see Issue 81)
					//KeysConfiguration.PLAYERKEY_ATTACK
					);	
			}
		}
		
		
		if (ghost) {
			// Scripting move
			automaticMove();
			
			handleCommon();
		} else {
			// User move
			handleCommon();
			
			if (dialogState.isDialoguing()) {
				// Conversation
				handleConversation();
				if (heros.isInventoring()) {
					// Inside the zildo's inventory
					handleInventory();
				}
			} else if (heros.isInventoring()) {
				// Inside the zildo's inventory
				handleInventory();
			} else if (heros.isAlive() && gamePhase.moves) {
				// Regular moving on the map
				handleRegularMoving();
			}
		}
	}
	
	/**
	 * Zildo is in ghost mode. It provides scripting moves.
	 */
	public void automaticMove() {
		Pointf pos=heros.reachDestination();
	 	
		float deltaX = pos.x - heros.x;
		float deltaY = pos.y - heros.y;
		
		if (deltaX != 0 || deltaY != 0 || heros.getTarget() != null) {
			adjustMovement(deltaX, deltaY);
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// handleCommon
	///////////////////////////////////////////////////////////////////////////////////////
	void handleCommon() {

	
		if (instant.isKeyDown(KeysConfiguration.PLAYERKEY_ACTION)) {
			keyPressAction();
		} else {
			keyReleaseAction();
		}
		if (heros.isAlive()) {
			
			if (instant.isKeyDown(KeysConfiguration.PLAYERKEY_ATTACK)) {
				keyPressAttack();
			} else {
				keyReleaseAttack();
			}
			
			if (heros.who.canInventory) {	// Does controlled character have an inventory ? 
				if (instant.isKeyDown(KeysConfiguration.PLAYERKEY_INVENTORY)) {
					keyPressInventory();
				} else {
					keyReleaseInventory();
				}
			}
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// handleRegularMoving
	///////////////////////////////////////////////////////////////////////////////////////
	void handleRegularMoving()
	{
		float deltaX=0;
		float deltaY=0;
	
		boolean needMovementAdjustment=true;
		
		float zildoSpeed=heros.getSpeed() * (heros.getAcceleration() / 10 ) * EngineZildo.extraSpeed;
		
		switch (heros.getMouvement()) {
			case SAUTE:
		    	// Zildo's jumping ! Then he's inactive for player
				break;
			case HOLD_FORK:
				zildoSpeed /= 2f;
			default:
			if (heros.getAttente()!=0) {
				heros.setAttente(heros.getAttente()-1);
				if (heros.getMouvement()!=MouvementZildo.ATTAQUE_EPEE) {
					needMovementAdjustment=false;
				}
			} else {
				heros.endMovement();
			}
	
	
			// Read keys from directInput
			Angle sauvangle=heros.getAngle();
			// ATTACK key
	
			boolean busy = false;
			switch (heros.getMouvement()) {
				case PLAYING_FLUT:
				case ATTAQUE_ARC:
				case ATTAQUE_EPEE:
					busy = true;
				default:
			}
			if (heros.getAttente() == 0 && !busy) {
				// Zildo can move ONLY if he isn't attacking
				// LEFT/RIGHT key
				Vector2f direction = instant.getDirection();
				if (direction != null) {
					deltaX = zildoSpeed * direction.x;
					deltaY = zildoSpeed * direction.y;
					heros.increaseAcceleration();
					if (deltaX != 0 || deltaY != 0) {
						heros.setAngle(Angle.fromDelta(deltaX, deltaY));
					}
				}/*
				if (instant.isKeyDown(KeysConfiguration.PLAYERKEY_LEFT)) {
					deltaX-=zildoSpeed;
					heros.setAngle(Angle.OUEST);
					heros.increaseAcceleration();
				} else if (instant.isKeyDown(KeysConfiguration.PLAYERKEY_RIGHT)) {
					deltaX+=zildoSpeed;
					heros.setAngle(Angle.EST);
					heros.increaseAcceleration();
				}
	
				// UP/DOWN key
				if (instant.isKeyDown(KeysConfiguration.PLAYERKEY_UP)) {
					deltaY-=zildoSpeed;
					heros.setAngle(Angle.NORD);
					heros.increaseAcceleration();
				} else if (instant.isKeyDown(KeysConfiguration.PLAYERKEY_DOWN)) {
					deltaY+=zildoSpeed;
					heros.setAngle(Angle.SUD);
					heros.increaseAcceleration();
				}
				*/
				if (deltaX == 0 && deltaY == 0) {
					heros.decreaseAcceleration();
				}
			}
	
			if (heros.getMouvement()==MouvementZildo.TIRE) {
				if (heros.getAngle()!=sauvangle && heros.getAngle().rotate(sauvangle.value).isVertical()) {	 // A Vérifier !
					heros.setPos_seqsprite(1);	// Zildo recule sa tête pour tirer
				} else {
					heros.setPos_seqsprite(0);
				}
				heros.setAngle(sauvangle);
				needMovementAdjustment=false;
			}
		}

		if (needMovementAdjustment) {
			// Is there any movement ?
			if (0 == deltaX && 0 == deltaY) {
				if (heros.getMouvement().equals(MouvementZildo.POUSSE)) {
					heros.setMouvement(MouvementZildo.VIDE);
				}
				heros.setPos_seqsprite(-1);
				heros.setNSpr(0);
				heros.setTouch(0);
				
				// Test collision even if Zildo doesn't move.
				// Useful with boomerang catching goodies.
				EngineZildo.spriteManagement.collideSprite((int) heros.x, (int) heros.y, heros);
			} else {
				// Reset pushed object before collision could set one
				Angle angleDirection = Angle.fromDirection(Math.round(deltaY),  Math.round(deltaY));
				if (heros.getMouvement() == MouvementZildo.POUSSE && angleDirection != heros.getAnglePush()) {
					heros.setMouvement(MouvementZildo.VIDE);
				}
				heros.pushSomething(null);
				adjustMovement(deltaX, deltaY);
			}
		}
		if (heros.getTouch()==16 && heros.getMouvement()==MouvementZildo.VIDE) {
			heros.setTouch(15);
			heros.setMouvement(MouvementZildo.POUSSE);
			heros.setAnglePush(heros.getAngle());
		}
	
		// Interpret animation paramaters to get the real sprite to display
		// TODO: this is already done in SpriteManagement#updateSprites
		heros.finaliseComportement(EngineZildo.nFrame);
	}
	
	/**
	 * 
	 * @param deltaX
	 * @param deltaY
	 */
	private void adjustMovement(float p_deltaX, float p_deltaY) {
		// Is it a valid movement ?
		// Adjustment
		heros.setPos_seqsprite((heros.getPos_seqsprite()+1) % 512);

        Pointf secureLocation;
        if (heros.isGhost()) {
        	// 'tryMove' has already been called in this case
        	secureLocation = new Pointf(heros.x + p_deltaX, heros.y + p_deltaY);
        } else {
        	secureLocation = heros.tryMove(p_deltaX, p_deltaY);
        }
        float xx = secureLocation.x;
        float yy = secureLocation.y;

		if (Math.abs(heros.x - xx) <= Math.abs(0.5*p_deltaX) && Math.abs(heros.y - yy) <= Math.abs(0.5*p_deltaY)) {
			if (heros.getMouvement()==MouvementZildo.VIDE) {
				if (heros.getTouch()>=15) {
					//On regarde si Zildo peut sauter
					heros.tryJump(secureLocation);

				}	//if touch=15
				heros.setPos_seqsprite(-1);
				heros.setTouch(heros.getTouch()+1);
			}
		} else if (!heros.getMouvement().equals(MouvementZildo.SAUTE)) {
            // Pas d'obstacles ? Mais peut-être une porte !
            boolean ralentit = heros.walkTile(true);

			// -. Yes
		    heros.setTouch(0);                          // Zildo n'est pas bloqué => 0

			float diffx=xx - heros.x;
			float diffy=yy - heros.y;

			// Calculate the sight angle (for boomerang) to have a 8-valued angle, instead a 4-valued
			Angle sightAngle=heros.getAngle();
			if (diffx != 0 && diffy != 0) {
				sightAngle=Angle.fromDirection((int) diffx, (int) diffy);
			}
			heros.setSightAngle(sightAngle);
			
			float coeff=1.0f;

			// On ralentit le mouvement de Zildo s'il est diagonal, ou si Zildo est dans un escalier
			if (ralentit || (diffx!=0 && diffy!=0 && heros.isGhost() && heros.getMouvement()!=MouvementZildo.TOUCHE))
			{
				if (ralentit)
					coeff = 0.4f;
				else {
					//coeff = cosPiSur4;
				}
			}

			heros.setX(heros.getX()+diffx*coeff);
			heros.setY(heros.getY()+diffy*coeff);
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// handleInventory
	///////////////////////////////////////////////////////////////////////////////////////
	void handleInventory() {
		if (heros.guiCircle.isAvailable()) {
			if (instant.isKeyDown(KeysConfiguration.PLAYERKEY_LEFT)) {
				heros.guiCircle.rotate(true);
			} else if (instant.isKeyDown(KeysConfiguration.PLAYERKEY_RIGHT)) {
				heros.guiCircle.rotate(false);
			}
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// handleConversation
	///////////////////////////////////////////////////////////////////////////////////////
	void handleConversation() {
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// handleTopicSelection
	///////////////////////////////////////////////////////////////////////////////////////
	void handleTopicSelection() {
		if (instant.isKeyDown(KeysConfiguration.PLAYERKEY_UP)) {
			if (!keysState.key_upPressed) {
				EngineZildo.dialogManagement.actOnDialog(client.location, CommandDialog.UP);
				keysState.key_upPressed=true;
			}
		} else {
			keysState.key_upPressed=false;
		}
	
		if (instant.isKeyDown(KeysConfiguration.PLAYERKEY_DOWN)) {
			if (!keysState.key_downPressed) {
				EngineZildo.dialogManagement.actOnDialog(client.location, CommandDialog.DOWN);
				keysState.key_downPressed=true;
			}
		} else {
			keysState.key_downPressed=false;
		}
	}

	///////////////////////////////////////////////////////////////////////////////////////
	// keyPressAction
	///////////////////////////////////////////////////////////////////////////////////////
	void keyPressAction() {
	
	    int cx,cy;
	    Angle persoangle;
	    boolean cestbon;
	
	
		if (!keysState.key_actionPressed) {
			if (gamePhase == GamePhase.BUYING) {
				heros.buyItem();
			} else if (gamePhase == GamePhase.DIALOG || gamePhase == GamePhase.SCRIPT) {
			    EngineZildo.dialogManagement.goOnDialog(client);
			} else if (gamePhase == GamePhase.INGAME && !heros.isInventoring() && heros.getAttente() == 0) { //
				if (heros.getMouvement()==MouvementZildo.BRAS_LEVES) {
					heros.throwSomething();
				} else if (heros.getMouvement()!=MouvementZildo.BRAS_LEVES && 
						heros.getMouvement()!=MouvementZildo.SOULEVE && !heros.isDoingAction()) {
					// Get a spot reachable in hero's direction
					Point coords = heros.getAngle().coords;
					int locX = (int) heros.x + coords.x * 8;
					int locY = (int) heros.y + coords.y * 8;
					
					Perso persoToTalk=EngineZildo.persoManagement.collidePerso(locX, locY, heros, 4);
					if (heros.who.canTalk &&  persoToTalk!=null && persoToTalk.getInfo() != PersoInfo.ENEMY 
							&& !persoToTalk.isZildo() && persoToTalk.getDesc() != PersoDescription.TURTLE ) {
					 // Check that this perso can be picked up (hen, duck for example)
						PersoDescription desc = persoToTalk.getDesc();
						if (desc.isTakable()) {
							// Check that any obstacle isn't on the way
							Point middle = Point.middle((int) heros.x, (int) heros.y, locX, locY);
							if (!EngineZildo.mapManagement.collideTile(middle.x, middle.y, false, new Point(2, 2), persoToTalk)) {
								heros.takeSomething((int)persoToTalk.x, (int)persoToTalk.y, null, persoToTalk);
							}
						} else if (persoToTalk.getDialoguingWith() == null && 
								!desc.isDamageable()) {
							// On vérifie que Zildo regarde la personne
							cx=(int) persoToTalk.getX();
							cy=(int) persoToTalk.getY();
							cestbon=false;
							switch (heros.getAngle()) {
								case NORD:if (cy<heros.getY()) cestbon=true;break;
								case EST:if (cx>heros.getX()) cestbon=true;break;
								case SUD:if (cy>heros.getY()) cestbon=true;break;
								case OUEST:if (cx<heros.getX()) cestbon=true;
								default:break;
							}
							if (cestbon) {
								// On change l'angle du perso
								if ( Math.abs(cx-heros.getX())>(Math.abs(cy-heros.getY())*0.7f) ) {
									if (cx>heros.getX()) persoangle=Angle.OUEST; else persoangle=Angle.EST;
								} else {
									if (cy>heros.getY()) persoangle=Angle.NORD; else persoangle=Angle.SUD;
								}
								persoToTalk.setAngle(persoangle);
		
								// Launch the dialog
								EngineZildo.dialogManagement.launchDialog(client, persoToTalk, null);
								gamePhase= GamePhase.DIALOG;
							}
						}
					} else if (heros.who.canPickup) {
						// Check for Sprite
						Element elem = EngineZildo.spriteManagement.collideElement(locX, locY, heros, 4);
						if (elem != null) {
							if (elem.getLinkedPerso() != null) {
								elem=elem.getLinkedPerso();
							}
							ElementDescription desc=ElementDescription.fromInt(elem.getNSpr());
							// Zildo can take a non-flying pickable sprite (ex: bomb)
							if (EngineZildo.spriteManagement.pickableSprites.contains(desc) && !elem.flying) {
								heros.takeSomething((int) elem.x, (int) elem.y, null, elem);
							}
							if (EngineZildo.spriteManagement.takableSprites.contains(desc)) {
								heros.pickItem(ItemKind.fromDesc(desc), elem);
							}
						} else {
							// Check for special tile on the map
							final int add_anglex[]={0,1,0,-1};
							final int add_angley[]={-1,0,1,0};
							int newx=((int)heros.x+6*add_anglex[heros.getAngle().value]) / 16;
							int newy=((int)heros.y+4*add_angley[heros.getAngle().value]) / 16;
							Area map=EngineZildo.mapManagement.getCurrentMap();
							int on_map=map.readmap(newx,newy);
							ElementDescription objDesc=null;
							if (Tile.isPickableTiles(on_map)) {
								//On ramasse l'objet
								switch (on_map) {
								case 165:
									objDesc=ElementDescription.BUSHES;
									if (((int)Math.random()*6)==5) {
										EngineZildo.spriteManagement.spawnSpriteGeneric(SpriteAnimation.GOLDCOIN,newx*16+8,newy*16+10,1, heros.floor, null, null);
									}
									break;
								case 167:	// With any gloves, Zildo can lift this stone
									if (heros.hasItem(ItemKind.GLOVE) || heros.hasItem(ItemKind.GLOVE_IRON)) {
										objDesc=ElementDescription.STONE;
									}
									break;
								case 169:	// Only with iron glove, he can lift this heavy stone
									if (heros.hasItem(ItemKind.GLOVE_IRON)) {
										objDesc=ElementDescription.STONE_HEAVY;
									}
									break;
								case 751:objDesc=ElementDescription.JAR;break;
								case 256*5+195:objDesc=ElementDescription.AMPHORA;break;
								}
	                            if (objDesc != null) {
	                                heros.takeSomething(newx * 16 + 8, newy * 16 + 14, objDesc, null);
	                                map.takeSomethingOnTile(new Point(newx, newy), false, heros, true);
	                            }
							} else if (Tile.isClosedChest(on_map) && heros.getAngle()==Angle.NORD) {
								// Hero found a chest ! Great, isn't it ?
								EngineZildo.soundManagement.broadcastSound(BankSound.ZildoOuvreCoffre, heros);
                                map.takeSomethingOnTile(new Point(newx, newy), false, heros, true);
								// Mark this event : chest opened
								EngineZildo.scriptManagement.actOnTile(map.getName(), new Point(newx, newy));
							} else if (on_map >= 0 && !EngineZildo.mapManagement.isWalkable(on_map)) {
								heros.setMouvement(MouvementZildo.TIRE);
							}
						}
					}
				}
			}
		}
		keysState.key_actionPressed=true;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// keyReleaseAction
	///////////////////////////////////////////////////////////////////////////////////////
	void keyReleaseAction() {
		if (keysState.key_actionPressed) {
			keysState.key_actionPressed=false;
			if (heros.getMouvement()==MouvementZildo.TIRE) {
				heros.setMouvement(MouvementZildo.VIDE);
			}
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// keyPressAttack
	///////////////////////////////////////////////////////////////////////////////////////
	void keyPressAttack() {
		if (!keysState.key_attackPressed) {
			// Y KEY is used to attack (use selected item from inventory)
			if (gamePhase == GamePhase.DIALOG || gamePhase == GamePhase.SCRIPT) {
				EngineZildo.dialogManagement.goOnDialog(client);
			} else if (heros.who.canFreeJump && heros.getMouvement() != MouvementZildo.TOMBE && heros.getMouvement() != MouvementZildo.SAUTE) {
				// Controlled character can jump with Y KEY
				heros.jump();
			} else if (gamePhase.moves && heros.getEn_bras() == null && !client.dialogState.isDialoguing() && !heros.isInventoring()) {
				// Set Zildo in attack stance
				heros.attack();
			}
		}
		keysState.key_attackPressed=true;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// keyReleaseAttack
	///////////////////////////////////////////////////////////////////////////////////////
	void keyReleaseAttack() {
		if (keysState.key_attackPressed) {
			keysState.key_attackPressed=false;
			if (heros.getMouvement()==MouvementZildo.TIRE) {
				heros.setMouvement(MouvementZildo.VIDE);
			}
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// keyPressInventory
	///////////////////////////////////////////////////////////////////////////////////////
	void keyPressInventory() {
		if (!keysState.key_inventoryPressed && gamePhase != GamePhase.MAPCHANGE && gamePhase != GamePhase.DIALOG && gamePhase != GamePhase.SCRIPT 
				&& heros.getMouvement()==MouvementZildo.VIDE && heros.getAttente() == 0) {
			// Dialog state can possibly be updated by script action (Issue 146)
			if (!heros.isInventoring()) {
				if (!dialogState.isDialoguing()) {
					heros.lookInventory();
				}
			} else {
				heros.closeInventory();
			}
			keysState.key_inventoryPressed=true;
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// keyReleaseTopic
	///////////////////////////////////////////////////////////////////////////////////////
	void keyReleaseInventory() {
		if (keysState.key_inventoryPressed) {
			keysState.key_inventoryPressed=false;
		}
	}
}