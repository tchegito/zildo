package zildo.server;

import zildo.fwk.IntSet;
import zildo.fwk.input.KeyboardInstant;
import zildo.fwk.input.KeyboardState;
import zildo.monde.decors.Element;
import zildo.monde.decors.SpriteEntity;
import zildo.monde.dialog.DialogManagement;
import zildo.monde.map.Angle;
import zildo.monde.map.Point;
import zildo.monde.persos.Perso;
import zildo.monde.persos.PersoZildo;
import zildo.monde.persos.utils.MouvementZildo;
import zildo.monde.persos.utils.PersoDescription;
import zildo.prefs.Constantes;
import zildo.prefs.KeysConfiguration;


public class PlayerManagement {



	private PersoZildo heros;
	private KeyboardInstant instant;
	private KeyboardState keysState;
	
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
		handleCommon();
	
		if (EngineZildo.dialogManagement.isDialoguing()) {
			if (EngineZildo.dialogManagement.isTopicChoosing()) {
				// Topic selection
				handleTopicSelection();
			} else {
				// Conversation
				handleConversation();
			}
		} else {
			// Regular moving on the map
			handleRegularMoving();
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// handleCommon
	///////////////////////////////////////////////////////////////////////////////////////
	void handleCommon() {
	
		if (instant.isKeyDown(KeysConfiguration.PLAYERKEY_ATTACK)) {
			keyPressAttack(heros);
		} else {
			keyReleaseAttack(heros);
		}
	
		if (false) {	// Unable for now the Topic key (this will come later)
			if (instant.isKeyDown(KeysConfiguration.PLAYERKEY_TOPIC)) {
				keyPressTopic(heros);
			} else {
				keyReleaseTopic(heros);
			}
		}
	
		if (instant.isKeyDown(KeysConfiguration.PLAYERKEY_ACTION)) {
			keyPressAction(heros);
		} else {
			keyReleaseAction(heros);
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// handleRegularMoving
	///////////////////////////////////////////////////////////////////////////////////////
	void handleRegularMoving()
	{
		int xx=(int) heros.getX();
		int yy=(int) heros.getY();
	
		int onMap=-1;
	
		final Point[] saut_angle={
			new Point(0,-40), new Point(48,16),new Point(0,56),  new Point(-48,16),
			new Point(32,-32),new Point(32,48),new Point(-32,48),new Point(-32,-32)};
	
		MapManagement mapManagement=EngineZildo.mapManagement;
	
		boolean needMovementAdjustment=true;
		SpriteEntity pushedEntity=heros.getPushingSprite();

		if (heros.getCompte_dialogue() != 0) {
			heros.setCompte_dialogue(heros.getCompte_dialogue()-1);
			if (heros.getCompte_dialogue() == 0) {
				heros.setWounded(false);
			}
		}
		if (heros.getPx()!=0.0f || heros.getPy()!=0.0f) {
			// Zildo s'est fait toucher !
			xx+=heros.getPx();
			yy+=heros.getPy();
			heros.setPx(heros.getPx()*0.8f);
			heros.setPy(heros.getPy()*0.8f);
			if (Math.abs(heros.getPx()) + Math.abs(heros.getPy()) <0.2f) {
				heros.stopBeingWounded();
			}
		} else if (heros.getMouvement() == MouvementZildo.MOUVEMENT_POUSSE && pushedEntity!=null)  {
		    // Zildo est en train de pousser : obstacle bidon ou bloc ?
			
			if (pushedEntity.getEntityType() == SpriteEntity.ENTITYTYPE_ELEMENT) {
				Element pushedElement=(Element) pushedEntity;
				if (pushedElement.isPushable()) {
					pushedElement.moveOnPush(heros.getAngle());
					// On casse le lien entre Zildo et l'objet poussé
					heros.pushSomething(null);
				}
			}
		}
		
		int zildoSpeed=Constantes.ZILDO_SPEED * EngineZildo.extraSpeed;
		
		if (heros.getMouvement() == MouvementZildo.MOUVEMENT_SAUTE) {
	    	// Zildo est en train de sauter ! Il est donc inactif
			if (heros.getEn_bras() == 32) {
				heros.setMouvement(MouvementZildo.MOUVEMENT_VIDE); // Fin du saut, on repasse en mouvement normal
				// Si Zildo atterit dans l'eau, on le remet à son ancienne position avec un coeur de moins
				int cx=xx / 16;
				int cy=yy / 16;
				onMap=mapManagement.getCurrentMap().readmap(cx,cy);
				if (onMap>=108 && onMap<=138)
				{
					Point zildoAvantSaut=heros.getPosAvantSaut();
					// Zildo est tombé dans l'eau !
					heros.setCompte_dialogue(50);
					heros.setX(zildoAvantSaut.getX());
					heros.setY(zildoAvantSaut.getY());
					heros.setPv(heros.getPv()-2);
					EngineZildo.broadcastSound("ZildoPlonge", heros);
				} else {
					EngineZildo.broadcastSound("ZildoAtterit", heros);
				}
				heros.setEn_bras(0);
			} else {
				float pasx=(float)saut_angle[heros.getDz()].getX() / 32.0f;
				float pasy=(float)saut_angle[heros.getDz()].getY() / 32.0f;
				heros.setX(heros.getX()+pasx);
				heros.setY(heros.getY()+pasy);
				heros.setEn_bras(heros.getEn_bras()+1);
			}
		} else {
			if (heros.getAttente()!=0) {
				heros.setAttente(heros.getAttente()-1);
				if (heros.getMouvement()!=MouvementZildo.MOUVEMENT_ATTAQUE_EPEE) {
					needMovementAdjustment=false;
				}
			} else if (heros.getMouvement()==MouvementZildo.MOUVEMENT_SOULEVE) {
				heros.setMouvement(MouvementZildo.MOUVEMENT_BRAS_LEVES);
			} else if (heros.getMouvement()==MouvementZildo.MOUVEMENT_ATTAQUE_EPEE) {
				heros.setMouvement(MouvementZildo.MOUVEMENT_VIDE);		// Awaiting for key pressed
			}
	
	
			// Read keys from directInput
			Angle sauvangle=heros.getAngle();
			// ATTACK key
	
			if (heros.getMouvement()!=MouvementZildo.MOUVEMENT_ATTAQUE_EPEE) {
				// Zildo can move ONLY if he isn't attacking
				// LEFT/RIGHT key
				if (instant.isKeyDown(KeysConfiguration.PLAYERKEY_LEFT)) {
					xx-=zildoSpeed;
					heros.setAngle(Angle.OUEST);
				} else if (instant.isKeyDown(KeysConfiguration.PLAYERKEY_RIGHT)) {
					xx+=zildoSpeed;
					heros.setAngle(Angle.EST);
				}
	
				// UP/DOWN key
				if (instant.isKeyDown(KeysConfiguration.PLAYERKEY_UP)) {
					yy-=zildoSpeed;
					heros.setAngle(Angle.NORD);
				} else if (instant.isKeyDown(KeysConfiguration.PLAYERKEY_DOWN)) {
					yy+=zildoSpeed;
					heros.setAngle(Angle.SUD);
				}
			}
	
			if (heros.getMouvement()==MouvementZildo.MOUVEMENT_TIRE) {
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
			if ((int) heros.x == xx &&
				(int) heros.y == yy) {
				if (heros.getMouvement().equals(MouvementZildo.MOUVEMENT_POUSSE)) {
					heros.setMouvement(MouvementZildo.MOUVEMENT_VIDE);
				}
				heros.setPos_seqsprite(-1);
				heros.setNSpr(0);
				heros.setDx(0);
			} else {
			// Is it a valid movement ?
				// Adjustment
				heros.setPos_seqsprite((heros.getPos_seqsprite()+1) % 512);
		
				if (mapManagement.collide(xx,yy,heros))
				{
					int diffx=xx-(int) heros.x;
					int diffy=yy-(int) heros.y;
					if (diffx!=0 && diffy!=0) {
						// Déplacement diagonale -. Déplacement latéral}
						if (!mapManagement.collide((int) xx,(int) heros.getY(),heros))
							yy=(int) heros.y;
						else if (!mapManagement.collide((int) heros.getX(),yy,heros))
							xx=(int) heros.x;
					} else {
		
						// Déplacement latéral -. Déplacement diagonal}
						if (diffx==0)
						{
							if (!mapManagement.collide(xx+zildoSpeed,yy,heros))
								xx+=zildoSpeed;
							else if (!mapManagement.collide(xx-zildoSpeed,yy,heros))
								xx-=zildoSpeed;
						} else if (diffy==0) {
							if (!mapManagement.collide(xx,yy+zildoSpeed,heros))
								yy+=zildoSpeed;
							else if (!mapManagement.collide(xx,yy-zildoSpeed,heros))
								yy-=zildoSpeed;
						}
					}
				}
		
				if (mapManagement.collide(xx,yy,heros))
				{
					if (heros.getMouvement()==MouvementZildo.MOUVEMENT_VIDE)
					{
						if (heros.getDx()==15)
						{
							//On regarde si Zildo peut sauter
							int cx=xx / 16;
							int cy=yy / 16;
							onMap=mapManagement.getCurrentMap().readmap(cx,cy);
							heros.setDz(8);
							switch (onMap) {
								// Saut diagonal
								case 35:case 106: heros.setDz(6); break;
								case 19:case 100: heros.setDz(7); break;
								case 23:case 102: heros.setDz(4); break;
								case 27:case 104:  heros.setDz(5); break;
								default:
									// Saut latéral}
									switch (heros.getAngle()) {
									case NORD:
										onMap=mapManagement.getCurrentMap().readmap(cx,cy-1);
										if (onMap==21 || onMap==3 || onMap==839)
											heros.setDz(0);
										break;
									case EST:
										onMap=mapManagement.getCurrentMap().readmap(cx+1,cy);
								// Display motif number
										if (onMap==25 || onMap==9 || onMap==842)
											heros.setDz(1);
										break;
									case SUD:
										onMap=mapManagement.getCurrentMap().readmap(cx,cy+1);
										if (onMap==32 || onMap==31 || onMap==844)
											heros.setDz(2);
										break;
									case OUEST:
										onMap=mapManagement.getCurrentMap().readmap(cx-1,cy);
										if (onMap==17 || onMap==15 || onMap==841)
											heros.setDz(3);
										break;
									}	// switch angle
							}	// switch onmap
							if (heros.getDz()!=8 && heros.getPushingSprite() == null)
							{
								// On sauve la position de Zildo avant son saut
								Point zildoAvantSaut=new Point((int) heros.getX(),
														 (int) heros.getY());
								heros.setMouvement(MouvementZildo.MOUVEMENT_SAUTE);
								heros.setDx(xx+saut_angle[heros.getDz()].getX());
								heros.setDy(yy+saut_angle[heros.getDz()].getY());
								heros.setX(xx);
								heros.setY(yy);
								heros.setEn_bras(0);
								heros.setPosAvantSaut(zildoAvantSaut);
								EngineZildo.broadcastSound("ZildoTombe", heros);
							}
						}	//if dx=15
						heros.setPos_seqsprite(-1);
						heros.setDx(heros.getDx()+1);
					}	//if mouvement==MOUVEMENT_VIDE
					else
						heros.setPos_seqsprite((heros.getPos_seqsprite()+1) % 512); // Sinon on augmente (Zildo pousse)
				}	// if Collide == true
				else if (!heros.getMouvement().equals(MouvementZildo.MOUVEMENT_SAUTE)) {
					// Pas d'obstacles ? Mais peut-être une porte !
					int cx=xx / 16;
					int cy=yy / 16;
					onMap=mapManagement.getCurrentMap().readmap(cx,cy);
					boolean ralentit=false;
					switch (onMap) {
						case 278:
							mapManagement.getCurrentMap().writemap(cx,cy,314);
							mapManagement.getCurrentMap().writemap(cx+1,cy,315);
							EngineZildo.broadcastSound("OuvrePorte", heros);
							break;
						case 279:
							mapManagement.getCurrentMap().writemap(cx-1,cy,314);
							mapManagement.getCurrentMap().writemap(cx,cy,315);
							EngineZildo.broadcastSound("OuvrePorte", heros);
							break;
						case 857: case 858: case 859: case 860:
						case 861: case 862: case 863: case 864:
							ralentit=true;
							break;
					}
		
					// -. Yes
				    heros.setDx(0);                          // Zildo n'est pas bloqué => 0
					if (heros.getMouvement()==MouvementZildo.MOUVEMENT_POUSSE)
						heros.setMouvement(MouvementZildo.MOUVEMENT_VIDE);
		
					int diffx=xx - (int) heros.x;
					int diffy=yy - (int) heros.y;
					float coeff;
					// On ralentit le mouvement de Zildo s'il est diagonal, ou si Zildo est dans un escalier
					if (ralentit || (diffx!=0 && diffy!=0 && heros.getMouvement()!=MouvementZildo.MOUVEMENT_TOUCHE))
					{
						if (ralentit)
							coeff=0.5f;
						else
							coeff=0.8f;
		
		
						heros.setX(heros.getX()+diffx*coeff);
						heros.setY(heros.getY()+diffy*coeff);
					} else
					{
						heros.setX(xx);
						heros.setY(yy);
					}
				}
			}
		}
		if (heros.getDx()==16 && heros.getMouvement()==MouvementZildo.MOUVEMENT_VIDE) {
			heros.setDx(15);
			heros.setMouvement(MouvementZildo.MOUVEMENT_POUSSE);
		}
	
		// Interpret animation paramaters to get the real sprite to display
		heros.finaliseComportement(EngineZildo.mapManagement.getCompteur_animation());
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
				EngineZildo.dialogManagement.actOnDialog(DialogManagement.ACTIONDIALOG_UP);
				keysState.key_upPressed=true;
			}
		} else {
			keysState.key_upPressed=false;
		}
	
		if (instant.isKeyDown(KeysConfiguration.PLAYERKEY_DOWN)) {
			if (!keysState.key_downPressed) {
				EngineZildo.dialogManagement.actOnDialog(DialogManagement.ACTIONDIALOG_DOWN);
				keysState.key_downPressed=true;
			}
		} else {
			keysState.key_downPressed=false;
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// keyPressAction
	///////////////////////////////////////////////////////////////////////////////////////
	void keyPressAction(PersoZildo perso) {
	
	    int cx,cy;
	    Angle persoangle;
	    boolean cestbon;
	
	
		if (!keysState.key_actionPressed) {
			if (EngineZildo.dialogManagement.isDialoguing()) {
				EngineZildo.dialogManagement.actOnDialog(DialogManagement.ACTIONDIALOG_ACTION);
			} else { //
				if (perso.getMouvement()==MouvementZildo.MOUVEMENT_BRAS_LEVES) {
					perso.throwSomething();
				} else if (perso.getMouvement()!=MouvementZildo.MOUVEMENT_BRAS_LEVES) {
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
						collidePerso((int) perso.getX(),(int) perso.getY(),perso,10);
		
					if (persoToTalk!=null) {
					 // On vérifie qu'il ne s'agit pas d'une poule
						if (persoToTalk.getQuel_spr().equals(PersoDescription.POULE)) {
							//tab_perso[with_dialogue].etat=False;
							perso.takeSomething((int)persoToTalk.x, (int)persoToTalk.y, 32, persoToTalk);
						} else {
							// On vérifie que Zildo regarde la personne
							cx=(int) persoToTalk.getX();
							cy=(int) persoToTalk.getY();
							cestbon=false;
							switch (perso.getAngle()) {
								case NORD:if (cy<perso.getY()) cestbon=true;break;
								case EST:if (cx>perso.getX()) cestbon=true;break;
								case SUD:if (cy>perso.getY()) cestbon=true;break;
								case OUEST:if (cx<perso.getX()) cestbon=true;break;
							}
							if (cestbon) {
								// On change l'angle du perso
								if ( Math.abs(cx-perso.getX())>(Math.abs(cy-perso.getY())*0.7f) ) {
									if (cx>perso.getX()) persoangle=Angle.OUEST; else persoangle=Angle.EST;
								} else {
									if (cy>perso.getY()) persoangle=Angle.NORD; else persoangle=Angle.SUD;
								}
								//perso.setAngle((persoangle+2) % 4);
								// Est-ce que ce perso/sprite a plusieurs angles ?
								if (persoToTalk.getNSpr()!=82 && persoToTalk.getNSpr()!=83 && persoToTalk.getNSpr()!=84)
									persoToTalk.setAngle(persoangle);
		
								// On demande la phrase du garde
								DialogManagement dialogManagement=EngineZildo.dialogManagement;
								dialogManagement.launchDialog(persoToTalk);
							}
						}
					} else {
						//On teste s'il y a un objet
						final int add_anglex[]={0,1,0,-1};
						final int add_angley[]={-1,0,1,0};
						int newx=((int)perso.x+5*add_anglex[perso.getAngle().value]) / 16;
						int newy=((int)perso.y+3*add_angley[perso.getAngle().value]) / 16;
						int on_map=EngineZildo.mapManagement.getCurrentMap().readmap(newx,newy);
						int enBras=-1;
						if (new IntSet(165,167,169,751).contains(on_map)) {
							//On ramasse l'objet
							switch (on_map) {
							case 165:
								enBras=1;
								if (((int)Math.random()*6)==5) {
									EngineZildo.spriteManagement.spawnSpriteGeneric(Element.SPR_DIAMANT,newx*16+8,newy*16+10,0, null);
								}
								break;
							case 167:enBras=12;break;
							case 169:enBras=11;break;
							case 751:enBras=0;break;
							}
							if (enBras != -1) {
								perso.takeSomething(newx*16+8, newy*16+14, enBras, null);
							}
							int j;
							if (on_map==165) {
								j=166;
							} else if (on_map==751) {
								j=752;
							} else j=168;
							EngineZildo.mapManagement.getCurrentMap().writemap(newx, newy, j);
						} else if (on_map==743 && perso.getAngle()==Angle.NORD) {
							//Zildo a trouvé un coffre ! C'est pas formidable ?
							EngineZildo.mapManagement.getCurrentMap().writemap(newx, newy, 744);
							//Musique('c:\musique\midi\zelda\Trouve.mid');
							EngineZildo.spriteManagement.spawnSpriteGeneric(Element.SPR_FROMCHEST,16*newx+8,16*newy+16,51, null);
							//EngineZildo.setWaitingScene(20);
						} else if (!EngineZildo.mapManagement.isWalkable(on_map)) {
							perso.setMouvement(MouvementZildo.MOUVEMENT_TIRE);
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
	void keyReleaseAction(Perso perso) {
		if (keysState.key_actionPressed) {
			keysState.key_actionPressed=false;
			if (perso.getMouvement()==MouvementZildo.MOUVEMENT_TIRE) {
				perso.setMouvement(MouvementZildo.MOUVEMENT_VIDE);
			}
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// keyPressAttack
	///////////////////////////////////////////////////////////////////////////////////////
	void keyPressAttack(Perso perso) {
		if (!keysState.key_attackPressed && perso.getEn_bras() == 0 && !EngineZildo.dialogManagement.isDialoguing()) {
			// Set Zildo in attack stance
			perso.attack();
			// Get the attacked tile
			Point tileAttacked=perso.getAttackTarget();
			// And ask 'map' object to react
			EngineZildo.mapManagement.getCurrentMap().attackTile(tileAttacked);
		}
		keysState.key_attackPressed=true;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// keyReleaseAttack
	///////////////////////////////////////////////////////////////////////////////////////
	void keyReleaseAttack(Perso perso) {
		if (keysState.key_attackPressed) {
			keysState.key_attackPressed=false;
			if (perso.getMouvement()==MouvementZildo.MOUVEMENT_TIRE) {
				perso.setMouvement(MouvementZildo.MOUVEMENT_VIDE);
			}
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// keyPressTopic
	///////////////////////////////////////////////////////////////////////////////////////
	void keyPressTopic(Perso perso) {
		if (!keysState.key_topicPressed && !EngineZildo.dialogManagement.isDialoguing()) {
			DialogManagement dialogManagement=EngineZildo.dialogManagement;
			dialogManagement.launchTopicSelection();
	
			keysState.key_topicPressed=true;
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// keyReleaseTopic
	///////////////////////////////////////////////////////////////////////////////////////
	void keyReleaseTopic(Perso perso) {
		if (keysState.key_topicPressed) {
			keysState.key_topicPressed=false;
		}
	}
}