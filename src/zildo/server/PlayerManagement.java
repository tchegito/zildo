/**
 * Legend of Zildo
 * Copyright (C) 2006-2011 Evariste Boussaton
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

package zildo.server;

import zildo.client.sound.BankSound;
import zildo.fwk.IntSet;
import zildo.fwk.input.KeyboardInstant;
import zildo.fwk.input.KeyboardState;
import zildo.monde.dialog.DialogManagement;
import zildo.monde.dialog.WaitingDialog.CommandDialog;
import zildo.monde.items.ItemKind;
import zildo.monde.map.Angle;
import zildo.monde.map.Area;
import zildo.monde.map.Point;
import zildo.monde.map.Pointf;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.desc.SpriteAnimation;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoZildo;
import zildo.monde.sprites.persos.Perso.PersoInfo;
import zildo.monde.sprites.utils.MouvementZildo;
import zildo.prefs.Constantes;
import zildo.prefs.KeysConfiguration;
import zildo.server.state.ClientState;
import zildo.server.state.DialogState;
import zildo.server.state.GamePhase;


public class PlayerManagement {

	private PersoZildo heros;
	private KeyboardInstant instant;
	private KeyboardState keysState;
	private DialogState dialogState;
	private ClientState client;
	
	private GamePhase gamePhase;
	
	public PlayerManagement()
	{
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// manageKeyboard
	///////////////////////////////////////////////////////////////////////////////////////
	//
	// Main method
	// ///////////
	// -dispatch the keyboard management between various situations:
	// *regular moving on the map
	// *conversation
	// *topic selection
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
		if (client.event.mapChange) {
			gamePhase=GamePhase.MAPCHANGE;
		} else if (EngineZildo.scriptManagement.isScripting()) {
			gamePhase=GamePhase.SCRIPT;
		} else if (dialogState.dialoguing) {
			if (heros.isInventoring()) {
				gamePhase=GamePhase.BUYING;
			} else {
				gamePhase=GamePhase.DIALOG;
			}
		} else {
			gamePhase=GamePhase.INGAME;
		} 

		if (ghost) {
			// Scripting move
			automaticMove();
			
			handleCommon();
		} else {
			// User move
			handleCommon();
			
			if (dialogState.dialoguing) {
				if (EngineZildo.dialogManagement.isTopicChoosing()) {
					// Topic selection
					handleTopicSelection();
				} else {
					// Conversation
					handleConversation();
					if (heros.isInventoring()) {
						// Inside the zildo's inventory
						handleInventory();
					}
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
		Pointf pos=heros.reachDestination(Constantes.ZILDO_SPEED);
	 	
		adjustMovement(pos.x, pos.y);
		heros.finaliseComportement(EngineZildo.compteur_animation);
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
			
			if (heros.getEn_bras() == null && gamePhase.moves) {
				if (instant.isKeyDown(KeysConfiguration.PLAYERKEY_ATTACK)) {
					keyPressAttack();
				} else {
					keyReleaseAttack();
				}
			}
			
			if (instant.isKeyDown(KeysConfiguration.PLAYERKEY_INVENTORY)) {
				keyPressInventory();
			} else {
				keyReleaseInventory();
			}
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// handleRegularMoving
	///////////////////////////////////////////////////////////////////////////////////////
	void handleRegularMoving()
	{
		float xx=heros.getX();
		float yy=heros.getY();
	
		int onMap=-1;
	
		MapManagement mapManagement=EngineZildo.mapManagement;
	
		boolean needMovementAdjustment=true;
		
		float zildoSpeed=Constantes.ZILDO_SPEED * (heros.getAcceleration() / 10 ) * EngineZildo.extraSpeed;
		
		if (heros.getMouvement() == MouvementZildo.SAUTE) {
	    	// Zildo est en train de sauter ! Il est donc inactif
			if (heros.getAttente() == 32) {
				heros.setMouvement(MouvementZildo.VIDE); // Fin du saut, on repasse en mouvement normal
				// Si Zildo atterit dans l'eau, on le remet à son ancienne position avec un coeur de moins
				int cx=(int) (xx / 16);
				int cy=(int) (yy / 16);
				onMap=mapManagement.getCurrentMap().readmap(cx,cy);
				if (onMap>=108 && onMap<=138)
				{
					Point zildoAvantSaut=heros.getPosAvantSaut();
					// Zildo is fallen in the water !
					heros.setX(zildoAvantSaut.getX());
					heros.setY(zildoAvantSaut.getY());
					xx=heros.getX();
					yy=heros.getY();
					heros.beingWounded(null, 2);
					heros.stopBeingWounded();
					EngineZildo.soundManagement.broadcastSound(BankSound.ZildoPlonge, heros);
					
				} else {
					heros.setForeground(false);
					EngineZildo.soundManagement.broadcastSound(BankSound.ZildoAtterit, heros);
				}
				heros.setAttente(0);
			} else {
				Point landingPoint=heros.getJumpAngle().getLandingPoint();
				float pasx=landingPoint.x / 32.0f;
				float pasy=landingPoint.y / 32.0f;
				heros.setX(heros.getX()+pasx);
				heros.setY(heros.getY()+pasy);
				heros.setAttente(heros.getAttente()+1);
			}
		} else {
			if (heros.getAttente()!=0) {
				heros.setAttente(heros.getAttente()-1);
				if (heros.getMouvement()!=MouvementZildo.ATTAQUE_EPEE) {
					needMovementAdjustment=false;
				}
			} else {
				//TODO: Move that section into PersoZildo
				switch (heros.getMouvement()) {
					case SOULEVE:
						heros.setMouvement(MouvementZildo.BRAS_LEVES);
						break;
                    case FIERTEOBJET:
                    	if (heros.getEn_bras() != null) {
                    		heros.getEn_bras().dying=true;
                    	}
                    	heros.setAngle(Angle.SUD);
                    case ATTAQUE_EPEE:
                    case ATTAQUE_ARC:
                    case ATTAQUE_BOOMERANG:
						heros.setMouvement(MouvementZildo.VIDE);		// Awaiting for key pressed
						break;
				}
			}
	
	
			// Read keys from directInput
			Angle sauvangle=heros.getAngle();
			// ATTACK key
	
			if (heros.getAttente() == 0 && heros.getMouvement()!=MouvementZildo.ATTAQUE_EPEE && heros.getMouvement()!=MouvementZildo.ATTAQUE_ARC) {
				// Zildo can move ONLY if he isn't attacking
				// LEFT/RIGHT key
				if (instant.isKeyDown(KeysConfiguration.PLAYERKEY_LEFT)) {
					xx-=zildoSpeed;
					heros.setAngle(Angle.OUEST);
					heros.increaseAcceleration();
				} else if (instant.isKeyDown(KeysConfiguration.PLAYERKEY_RIGHT)) {
					xx+=zildoSpeed;
					heros.setAngle(Angle.EST);
					heros.increaseAcceleration();
				}
	
				// UP/DOWN key
				if (instant.isKeyDown(KeysConfiguration.PLAYERKEY_UP)) {
					yy-=zildoSpeed;
					heros.setAngle(Angle.NORD);
					heros.increaseAcceleration();
				} else if (instant.isKeyDown(KeysConfiguration.PLAYERKEY_DOWN)) {
					yy+=zildoSpeed;
					heros.setAngle(Angle.SUD);
					heros.increaseAcceleration();
				}
				
				if (xx == heros.getX() && yy == heros.getY()) {
					heros.decreaseAcceleration();
				}
			}
	
			if (heros.getMouvement()==MouvementZildo.TIRE) {
				if (heros.getAngle()!=sauvangle && Angle.rotate(heros.getAngle(),sauvangle.value).isVertical()) {	 // A Vérifier !
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
			if (heros.x == xx &&
					heros.y == yy) {
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
				adjustMovement(xx, yy);
			}
		}
		if (heros.getTouch()==16 && heros.getMouvement()==MouvementZildo.VIDE) {
			heros.setTouch(15);
			heros.setMouvement(MouvementZildo.POUSSE);
		}
	
		// Interpret animation paramaters to get the real sprite to display
		heros.finaliseComportement(EngineZildo.compteur_animation);
	}
	
	/**
	 * 
	 * @param xx
	 * @param yy
	 */
	private void adjustMovement(float xx, float yy) {
		// Is it a valid movement ?
		// Adjustment
		heros.setPos_seqsprite((heros.getPos_seqsprite()+1) % 512);

        Pointf secureLocation = heros.tryMove(xx, yy);
        xx = secureLocation.x;
        yy = secureLocation.y;

		if (heros.x == xx && heros.y == yy) {
			if (heros.getMouvement()==MouvementZildo.VIDE) {
				if (heros.getTouch()==15) {
					//On regarde si Zildo peut sauter
					int cx=(int) (xx / 16);
					int cy=(int) (yy / 16);
					Angle angleResult=EngineZildo.mapManagement.getAngleJump(heros.getAngle(), cx, cy);
					if (angleResult!=null && heros.getPushingSprite() == null) {
						Point landingPoint=angleResult.getLandingPoint().translate((int) heros.x, (int) heros.y);
						if (!EngineZildo.mapManagement.collide(landingPoint.x, landingPoint.y, heros)) {
							heros.jump(angleResult);
						}
					}
				}	//if touch=15
				heros.setPos_seqsprite(-1);
				heros.setTouch(heros.getTouch()+1);
			} else
				heros.setPos_seqsprite((heros.getPos_seqsprite()+1) % 512); // Sinon on augmente (Zildo pousse)
			
		} else if (!heros.getMouvement().equals(MouvementZildo.SAUTE)) {
            // Pas d'obstacles ? Mais peut-être une porte !
            boolean ralentit = heros.walkTile(true);

			// -. Yes
		    heros.setTouch(0);                          // Zildo n'est pas bloqué => 0
			if (heros.getMouvement()==MouvementZildo.POUSSE)
				heros.setMouvement(MouvementZildo.VIDE);

			float diffx=xx - heros.x;
			float diffy=yy - heros.y;

			// Calculate the sight angle (for boomerang) to have a 8-value angle, instead a 4 value
			Angle sightAngle=heros.getAngle();
			if (diffx != 0 && diffy != 0) {
				sightAngle=Angle.fromDirection((int) diffx, (int) diffy);
			}
			heros.setSightAngle(sightAngle);
			
			float coeff=1.0f;

			// On ralentit le mouvement de Zildo s'il est diagonal, ou si Zildo est dans un escalier
			if (ralentit || (diffx!=0 && diffy!=0 && heros.getMouvement()!=MouvementZildo.TOUCHE))
			{
				if (ralentit)
					coeff=0.5f;
				else
					coeff=0.7f;
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
				EngineZildo.dialogManagement.actOnDialog(client.location, CommandDialog.ACTION);
			} else if (gamePhase == GamePhase.INGAME && !heros.isInventoring()) { //
				if (heros.getMouvement()==MouvementZildo.BRAS_LEVES) {
					heros.throwSomething();
				} else if (heros.getMouvement()!=MouvementZildo.BRAS_LEVES && 
						heros.getMouvement()!=MouvementZildo.SOULEVE) {
					// On teste s'il y a un personnage à qui parler
				/*    with_dialogue=0;
					i=0;
					repeat
					 i=i+1;
					 if tab_perso[i].etat=true then
					  if check_colli(round(tab_perso[i].x),round(tab_perso[i].y),
									 round(x),round(y),10,10) then
					   with_dialogue=i;
					until (with_dialogue<>0) or (i=MAX_PERSO-1);
				*/
					Perso persoToTalk=EngineZildo.persoManagement.
						collidePerso((int) heros.x, (int) heros.y, heros, 10);
		
					if (persoToTalk!=null && persoToTalk.getInfo() != PersoInfo.ENEMY && !persoToTalk.isZildo()) {
					 // On vérifie qu'il ne s'agit pas d'une poule
						if (persoToTalk.getQuel_spr().equals(PersoDescription.POULE)) {
							heros.takeSomething((int)persoToTalk.x, (int)persoToTalk.y, ElementDescription.HEN, persoToTalk);
						} else if (persoToTalk.getDialoguingWith() == null) {
							// On vérifie que Zildo regarde la personne
							cx=(int) persoToTalk.getX();
							cy=(int) persoToTalk.getY();
							cestbon=false;
							switch (heros.getAngle()) {
								case NORD:if (cy<heros.getY()) cestbon=true;break;
								case EST:if (cx>heros.getX()) cestbon=true;break;
								case SUD:if (cy>heros.getY()) cestbon=true;break;
								case OUEST:if (cx<heros.getX()) cestbon=true;break;
							}
							if (cestbon) {
								// On change l'angle du perso
								if ( Math.abs(cx-heros.getX())>(Math.abs(cy-heros.getY())*0.7f) ) {
									if (cx>heros.getX()) persoangle=Angle.OUEST; else persoangle=Angle.EST;
								} else {
									if (cy>heros.getY()) persoangle=Angle.NORD; else persoangle=Angle.SUD;
								}
								//perso.setAngle((persoangle+2) % 4);
								// Est-ce que ce perso/sprite a plusieurs angles ?
								if (persoToTalk.getNSpr()!=82 && persoToTalk.getNSpr()!=83 && persoToTalk.getNSpr()!=84)
									persoToTalk.setAngle(persoangle);
		
								// Launch the dialog
								EngineZildo.dialogManagement.launchDialog(client, persoToTalk, null);
							}
						}
					} else {
						// Check for Sprite
						Element elem = EngineZildo.spriteManagement.collideElement((int) heros.x, (int) heros.y, heros, 10);
						if (elem != null) {
							if (elem.getLinkedPerso() != null) {
								elem=elem.getLinkedPerso();
							}
							ElementDescription desc=ElementDescription.fromInt(elem.getNSpr());
							// Zildo can take a non-flying pickable sprite (ex: bomb)
							if (EngineZildo.spriteManagement.pickableSprites.contains(desc) && !elem.flying) {
								heros.takeSomething((int) elem.x, (int) elem.y, null, elem);
							}
						} else {
							// Check for special tile on the map
							final int add_anglex[]={0,1,0,-1};
							final int add_angley[]={-1,0,1,0};
							int newx=((int)heros.x+5*add_anglex[heros.getAngle().value]) / 16;
							int newy=((int)heros.y+3*add_angley[heros.getAngle().value]) / 16;
							Area map=EngineZildo.mapManagement.getCurrentMap();
							int on_map=map.readmap(newx,newy);
							ElementDescription objDesc=null;
							if (new IntSet(165,167,169,751).contains(on_map)) {
								//On ramasse l'objet
								switch (on_map) {
								case 165:
									objDesc=ElementDescription.BUSHES;
									if (((int)Math.random()*6)==5) {
										EngineZildo.spriteManagement.spawnSpriteGeneric(SpriteAnimation.DIAMOND,newx*16+8,newy*16+10,0, null, null);
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
								}
	                            if (objDesc != null) {
	                                heros.takeSomething(newx * 16 + 8, newy * 16 + 14, objDesc, null);
	                                map.takeSomethingOnTile(new Point(newx, newy), false, heros);
	                            }
							} else if (on_map==743 && heros.getAngle()==Angle.NORD) {
								//Zildo a trouvé un coffre ! C'est pas formidable ?
								EngineZildo.soundManagement.broadcastSound(BankSound.ZildoOuvreCoffre, heros);
								heros.setAttente(60);
                                map.takeSomethingOnTile(new Point(newx, newy), false, heros);
								// Mark this event : chest opened
								EngineZildo.scriptManagement.openChest(map.getName(), new Point(newx, newy));
							} else if (!EngineZildo.mapManagement.isWalkable(on_map)) {
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
		if (!keysState.key_attackPressed && heros.getEn_bras() == null && !client.dialogState.dialoguing && !heros.isInventoring()) {
			// Set Zildo in attack stance
			heros.attack();
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
	// keyPressTopic
	///////////////////////////////////////////////////////////////////////////////////////
	void keyPressTopic() {
		if (!keysState.key_topicPressed && !client.dialogState.dialoguing) {
			DialogManagement dialogManagement=EngineZildo.dialogManagement;
			dialogManagement.launchTopicSelection();
	
			keysState.key_topicPressed=true;
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// keyReleaseTopic
	///////////////////////////////////////////////////////////////////////////////////////
	void keyReleaseTopic() {
		if (keysState.key_topicPressed) {
			keysState.key_topicPressed=false;
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// keyPressInventory
	///////////////////////////////////////////////////////////////////////////////////////
	void keyPressInventory() {
		if (!keysState.key_inventoryPressed && gamePhase != GamePhase.DIALOG && gamePhase != GamePhase.SCRIPT && heros.getMouvement()==MouvementZildo.VIDE) {
			if (!heros.isInventoring()) {
				heros.lookInventory();
			} else {
				heros.closeInventory();
				EngineZildo.dialogManagement.actOnDialog(client.location, CommandDialog.ACTION);
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