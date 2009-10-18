package zildo.monde.sprites.persos;

import zildo.client.SoundPlay.BankSound;
import zildo.fwk.gfx.PixelShaders;
import zildo.monde.Hasard;
import zildo.monde.map.Angle;
import zildo.monde.map.Point;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.elements.ElementImpact;
import zildo.monde.sprites.elements.ElementImpact.ImpactKind;
import zildo.monde.sprites.utils.MouvementPerso;
import zildo.monde.sprites.utils.MouvementZildo;
import zildo.prefs.Constantes;
import zildo.server.EngineZildo;




//////////////////////////////////////////////////////////////////////
// Construction/Destruction
//////////////////////////////////////////////////////////////////////

public class PersoNJ extends Perso {

	public PersoNJ() {
		super();
		setPos_seqsprite(0);
	}
	
	public void attack() {
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// manageCollision
	///////////////////////////////////////////////////////////////////////////////////////
	// -create collision zone for this character
	///////////////////////////////////////////////////////////////////////////////////////
	public void manageCollision() {
		if (getInfo() == 1 && getPv()>0) {
			// Add this collision record to collision engine
			super.manageCollision();
			/*
			List<Element> elem=new ArrayList<Element>();
			elem.add(this);
			elem.addAll(persoSprites);
			for (Element e:elem) {
				if (e.isSolid()) {
					Collision c=e.getCollision();
					SpriteModel spr=e.getSprModel();
					if (c == null) {
						int size=(spr.getTaille_x() + spr.getTaille_y()) / 4;
						c=new Collision((int) e.x, (int) e.y, size, getAngle(), this, DamageType.BLUNT, null);
					}
                	c.cy-=spr.getTaille_y() / 2;
                    c.cy-=e.z;
					//EngineZildo.collideManagement.addCollision(c);
				}
			}
			*/
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// beingWounded
	///////////////////////////////////////////////////////////////////////////////////////
	// IN : cx,cy : enemy's position
	///////////////////////////////////////////////////////////////////////////////////////
	// Invoked when this character gets wounded by any enemy (=ZILDO)
	///////////////////////////////////////////////////////////////////////////////////////
	public boolean beingWounded(float cx, float cy, Perso p_shooter) {
		project(cx, cy, 6);
		this.setMouvement(MouvementZildo.TOUCHE);
		this.setWounded(true);
		this.setAlerte(true);				// Zildo is detected, if it wasn't done !
		this.setPv(getPv()-1);
		this.setSpecialEffect(PixelShaders.ENGINEFX_PERSO_HURT);
	
		boolean died=(getPv()==0);
		if (died) {
			die(true, p_shooter);
		}
	
		EngineZildo.soundManagement.broadcastSound(BankSound.MonstreTouche, this);
	
		return died;
	}
	
	public void parry(float cx, float cy, Perso p_shooter) {
		project(cx, cy, 2);
		EngineZildo.soundManagement.broadcastSound(BankSound.BoomerangTape, this);
		EngineZildo.spriteManagement.spawnSprite(new ElementImpact((int) x, (int) y, ImpactKind.SIMPLEHIT));

	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// stopBeingWounded
	///////////////////////////////////////////////////////////////////////////////////////
	public void stopBeingWounded()
	{
		setPx(0.0f);
		setPy(0.0f);
		setX((int)getX());
		setY((int)getY());
		setWounded(false);
		initPersoFX();
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// animate
	///////////////////////////////////////////////////////////////////////////////////////
	// Move a PNJ to his location (dx,dy) set by determineDestination()
	///////////////////////////////////////////////////////////////////////////////////////
	public void animate(int compteur_animation)
	{
		if (getPv() == 0 || getDialoguingWith() != null) {
			this.setAjustedX((int) getX());
			this.setAjustedY((int) getY());
			return;
		}
	
		float sx=getX(),sy=getY();
		int xx,yy;
		PersoZildo zildo=EngineZildo.persoManagement.getZildo();
	
		if (px != 0.0f || py != 0.0f) {
			// Le perso s'est fait toucher !}
			Point location=tryMove((int) (x+px), (int) (y+py));
			x=location.x;
			y=location.y;
			px*=0.9f;
			py*=0.9f;
			setAttente(0);
			if ( (Math.abs(px) + Math.abs(py))<0.4f) {
				this.stopBeingWounded();
			}
		}

		if (isAlerte() && !MouvementPerso.SCRIPT_VOLESPECTRE.equals(quel_deplacement)) {
			// Zildo est reper� le monstre lui fonce dessus
			reachTarget(zildo);
		} else {
			switch (this.getQuel_deplacement()) {
				case SCRIPT_IMMOBILE:
					break;
				case SCRIPT_OBSERVE:
					// Persos qui regardent en direction de Zildo
					xx=(int) (getX() - zildo.getX());
					yy=(int) (getY() - zildo.getY());
					if (Math.abs(yy) >= Math.abs(xx) || Math.abs(xx)>96 || Math.abs(yy)>96) {
						setAngle(Angle.SUD);
					} else {
						if (xx>0) {
							setAngle(Angle.OUEST);
						} else {
							setAngle(Angle.EST);
						}
					}
					break;
				case SCRIPT_VOLESPECTRE:
					double alpha;
					if (cptMouvement==100) {
						if (quel_spr.equals(PersoDescription.CORBEAU)) {
							// Corbeau : on centre la zone de d�placement sur Zildo}
							int pasx, pasy;
							if ((int)zildo.x / 16 > x / 16) {
								pasx=16;
							} else pasx=-16;
							if ((int)zildo.y / 16 > y/ 16) {
								pasy=16;
							} else pasy=-16;
							zone_deplacement.incX1(pasx*3);
							zone_deplacement.incY1(pasy*3);
							zone_deplacement.incX2(pasx*3);
							zone_deplacement.incY2(pasy*3);
						}
						attente=1+(int)Math.random()*5;
						determineDestination();
						dx=(int)((dx+Math.random()*20.0f-10.0f-x)/2);
						dy=(int)((dy+Math.random()*20.0f-10.0f-y)/2);
						cptMouvement=0;
					} else if (attente!=0) {
						attente--;
					} else {
						if (quel_spr.equals(PersoDescription.CORBEAU)) {
							if (pos_seqsprite!=0) {
								pos_seqsprite=(4*Constantes.speed)+(pos_seqsprite-4*Constantes.speed+1) % (8*Constantes.speed);
							} else {
								// Est-ce que Zildo est dans les parages ?}
								alpha=x-zildo.x;
								float vitesse=y-zildo.y;
								alpha=Math.sqrt(alpha*alpha+vitesse*vitesse);
								if (alpha<16*5) {
									pos_seqsprite=4*Constantes.speed;
								}
								break;
							}
						}
						// On se d�place en courbe}
						alpha=Math.PI*(cptMouvement/100.0f)-Math.PI/2.0f;
						z=(float) (2.0f+10.0f*Math.sin(alpha+Math.PI/2.0f));
						alpha=(Math.PI/100.0f)*Math.cos(alpha);
						x+=dx*alpha;
						y+=dy*alpha;
						if (dx<0) {
							angle=Angle.EST;
						} else angle=Angle.NORD;
						cptMouvement++;
					}
					break;
				case SCRIPT_POULE:
					if (z>0) { // La poule est en l'air, elle n'est plus libre de ses mouvements
						physicMoveWithCollision();
						break;
					}	// Sinon elle agit comme les scripts de zone
				default:
					if (this.getX() == this.getDx() &&
						this.getY() == this.getDy()) {
						setDx(-1);
						if (quel_deplacement!=MouvementPerso.SCRIPT_ABEILLE &&
								(quel_deplacement!=MouvementPerso.SCRIPT_RAT || Hasard.lanceDes(8))) {
							setAttente(10+(int) (Math.random()*20));
						}
					}
					if (this.getAttente()!=0) {
						if (getQuel_spr().equals(PersoDescription.BAS_GARDEVERT)) {
							//Garde vert => Il tourne la t�te pour faire une ronde}
							if (this.getAttente()==1 && cptMouvement<2) {
								if (!alerte && lookForZildo(Angle.rotate(angle, PersoGardeVert.mouvetete[cptMouvement]))) {
									alerte=true;
									EngineZildo.soundManagement.broadcastSound(BankSound.MonstreTrouve, this);
								}
								cptMouvement++;
								setAttente(20);
							}
						} else if (getQuel_spr().equals(PersoDescription.GARDE_CANARD)) {
							setAlerte(lookForZildo(angle));
						}
						this.setAttente(getAttente() - 1);
					} else {
						// On d�place le PNJ
						if (this.getDx() == -1) {
							//Pas de destination, donc on en fixe une dans la zone de d�placement
							cptMouvement=0;
				
							if (quel_deplacement.equals(MouvementPerso.SCRIPT_ABEILLE)) {
								dx=(int)(x+(5.0f+Math.random()*10.0f)*Math.cos(2.0f*Math.PI*Math.random()));
								dy=(int)(y+(5.0f+Math.random()*10.0f)*Math.sin(2.0f*Math.PI*Math.random()));
							} else {
								determineDestination();
							}
						}
						float vitesse=0.5f;
						if (quel_deplacement.equals(MouvementPerso.SCRIPT_RAT)) {
							// Script du rat => plus rapide, et crache des pierres}
							vitesse+=1;
							pos_seqsprite=pos_seqsprite % (8*Constantes.speed-1);
							if (quel_spr.equals(PersoDescription.CRABE) && Math.random()*40==2) {
								//On crache une boule de pierre}
								pos_seqsprite=8*Constantes.speed;
								EngineZildo.spriteManagement.spawnSpriteGeneric(SPR_BOULEPIERRE,(int)x,(int)y,
										(int) (angle.value+Math.random()*4)	// Attention : math.random() �tait 'i' en pascal
										,null);
								attente=(int) (Math.random()*5);
							}
						} else if (quel_deplacement.equals(MouvementPerso.SCRIPT_ELECTRIQUE)) {
							vitesse=0.2f;
						}
						
						int immo=0;
						if ((getDx() - getX()) > vitesse) {
							this.setX(getX() + vitesse);
							this.setAngle(Angle.EST);
						} else if ((getDx() - getX()) <vitesse) {
							this.setX(getX() - vitesse);
							this.setAngle(Angle.OUEST);
						} else {
							immo++;
						}
						if ((getDy()-getY())>vitesse) {
							this.setY(getY() + vitesse);
							this.setAngle(Angle.SUD);
						} else if ((getDy() - getY())<vitesse) {
							this.setY(getY() - vitesse);
							this.setAngle(Angle.NORD);
						} else {
							immo++;
						}
						
						// suite_mouvement
						if (quel_deplacement.equals(MouvementPerso.SCRIPT_ELECTRIQUE)) {
							angle=Angle.NORD;
						} else if (quel_deplacement.equals(MouvementPerso.SCRIPT_ABEILLE)) {
							angle=Angle.fromInt(angle.value & 2);
						}
						if (!quel_deplacement.equals(MouvementPerso.SCRIPT_VOLESPECTRE)) {
							// Collision ?
							if (immo == 2 || (!quel_deplacement.equals(MouvementPerso.SCRIPT_VOLESPECTRE) && EngineZildo.mapManagement.collide((int) getX(),(int) getY(),this))) {
								this.setX ( sx);
								this.setY ( sy);
								this.setDx(-1);
								this.setAttente(10 + (int) (Math.random()*20));
							} else {
								this.setPos_seqsprite ( (getPos_seqsprite() + 1) % 512);
							}
						}
					}
				}
			//}
		}
		if (!isWounded()) {
			// Destination
		}
	
		 //else {
	
			

			//case SCRIPT_ZONE:
		/*
				if (!pasDeMouvement && !quel_deplacement.equals(MouvementPerso.SCRIPT_IMMOBILE)) {
					// Attempt to move character
					

				}
*/	

		//}
	
		this.setAjustedX((int) x);
		this.setAjustedY((int) y);
		
		finaliseComportement(compteur_animation);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// initPersoFX
	///////////////////////////////////////////////////////////////////////////////////////
	// Set perso with initial special effect.
	///////////////////////////////////////////////////////////////////////////////////////
	public void initPersoFX() {
		if (getQuel_spr().equals(PersoDescription.GARDE_CANARD)) {	// Guard
			if ("jaune".equals(getNom())) {
				setSpecialEffect(PixelShaders.ENGINEFX_GUARD_YELLOW);
			} else if("vert".equals(getNom())) {
				setSpecialEffect(PixelShaders.ENGINEFX_GUARD_GREEN);
			} else if("rouge".equals(getNom())) {
				setSpecialEffect(PixelShaders.ENGINEFX_GUARD_RED);
			} else {
				// Default color for this guard : blue
				setSpecialEffect(PixelShaders.ENGINEFX_GUARD_BLUE);
			}
		} else {
			setSpecialEffect(PixelShaders.ENGINEFX_NO_EFFECT);
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// finaliseComportement
	///////////////////////////////////////////////////////////////////////////////////////
	// Manage character's graphic side, depending on the position in the animated sequence.
	///////////////////////////////////////////////////////////////////////////////////////
	public void finaliseComportement(int compteur_animation) {
	
		final int[] seqp={0,2,0,1}; //Persos � 3 sprites
	
		int add_spr=0;
		PersoDescription quelSpriteWithBank=this.getQuel_spr();
	
		// Animated sequence adjustment
	
		switch (quelSpriteWithBank) {
			case GARDE_CANARD:

		       
		       break;
			case POULE:
			   //Poule
			   add_spr=(getPos_seqsprite() % (8*Constantes.speed)) / (2*Constantes.speed);
			   if (getDx()>getX())
				   add_spr+=41;
				break;
			case VIEUX:
			   //Vieux saoul � 3 sprites
			   if ((int) (Math.random()*30)==2)
				   setPos_seqsprite((int) (Math.random()*3));
				add_spr=getPos_seqsprite();
				break;
			case MOUSTACHU:
			case VIEUX_SAGE:
			case BUCHERON_ASSIS:
			case BANDIT_CHAPEAU:
			case MOUSTACHU_ASSIS:
				//Persos � 1 seul sprite
				add_spr=0;
				break;
			case VOYANT:
			case DRESSEUR_SERPENT:
			case SORCIERE:
			case VIEILLE_BALAI:
			case ALCOOLIQUE:
			case SORCIER_CAGOULE:
			case VIEUX_SAGE2:
				//Persos toujours de face, � 2 sprites seulement
				add_spr=(compteur_animation / 30);
				break;
			case ENFANT:
			case VOLEUR:
			case BANDIT:
				//Persos � 3 angles
				add_spr=seqp[angle.value];
				break;
			case CURE:
			case GARDE_BOUCLIER:
				//Persos � 4 sprites : pr�tre,garde bouclier
				add_spr=angle.value;
				break;
			case VAUTOUR:
			case ELECTRIQUE:
				//Persos � 3 sprite et 1 angle
				add_spr=compteur_animation / 20;
				break;
			case SPECTRE:
				//Perso � 2 sprites (gauche/droite)
				add_spr=angle.value;
				break;
			case CORBEAU:
			case CRABE:
				//Persos � 3 sprites par angle
				add_spr=angle.value*3 + (getPos_seqsprite() % (12*Constantes.speed)) / (4*Constantes.speed);
				break;
			case ABEILLE:
				add_spr=angle.value + (compteur_animation / 30);
				break;
			default:
				add_spr=angle.value*2 + (getPos_seqsprite() % (4*Constantes.speed)) / (2*Constantes.speed);
				break;
		}
	
		this.setNSpr((this.getQuel_spr().first() + add_spr) % 128);
	}
	
	/**
	 * Se dirige vers le personnage pass� en param�tre
	 * @param perso
	 */
	public void reachTarget(Perso perso) {
		float sx=x;
		float sy=y;
		cptMouvement=0;
		float ddx=perso.getX();
		float ddy=perso.getY();
		if ((int)ddx>(int)sx) {
			setX(getX()+Constantes.MONSTER_SPEED);
		} else if ((int)ddx<(int)x) {
			setX(getX()-Constantes.MONSTER_SPEED);
		}
		if ((int)ddy>(int)y) {
			setY(getY()+Constantes.MONSTER_SPEED);
		} else if ((int)ddy<(int)y) {
			setY(getY()-Constantes.MONSTER_SPEED);
		}
		setPos_seqsprite( (getPos_seqsprite()+1) % 512);
		if (EngineZildo.mapManagement.collide((int) x,(int) y,this)) {
			// Le monstre est g�n� par un obstacle
			if (!EngineZildo.mapManagement.collide((int) sx,(int) y,this)) {
				setX(sx);
			} else if (!EngineZildo.mapManagement.collide((int) x,(int) sy,this)) {
				setY(sy);
			} else {
				setX(sx);
				setY(sy);
				setPos_seqsprite(0);
				setAlerte(false);
				setDx(-1);
				setAttente(10);	
				// On replace la zone de d�placement autour de l'ennemi
				setZone_deplacement(EngineZildo.mapManagement.range(x-16*5, y-16*5, x+16*5, y+16*5));
			}
		}
		if (x>sx) {
			setAngle(Angle.EST);
		} else if (x<sx) {
			setAngle(Angle.OUEST);
		} else if (y>sy) {
			setAngle(Angle.SUD);
		} else {
			setAngle(Angle.NORD);
		}
		if (sx!=x && sy!=y) {
			setX(sx+(x-sx)*0.8f);
			setY(sy+(y-sy)*0.8f);
		}
		
	}
	/*
	{_Renvoie TRUE si Zildo est dans les parages
	 _Le monstre qui le cherche � l'index 'i'}
	 */
	boolean lookForZildo(Angle angle) {
		int dix,diy;
		boolean temp,r;
		final int DISTANCEMAX=16*6;
		PersoZildo zildo=EngineZildo.persoManagement.getZildo();
		r=false;
		// On calcule la distance en x et en y entre le perso et Zildo
		dix=(int) (x-zildo.x);
		diy=(int) (y-zildo.y);
		temp=(Math.abs(dix) >Math.abs(diy));
		if (angle.isHorizontal()==temp) {
			switch (angle) {
			case NORD:if (diy>0 && diy<DISTANCEMAX)  r=true;break;
			case EST:if (dix<0 && dix>-DISTANCEMAX) r=true;break;
			case SUD:if (diy<0 && diy>-DISTANCEMAX) r=true;break;
			case OUEST:if (dix>0 && dix<DISTANCEMAX)  r=true;break;
			}
		}
		return r;
	}
	
	public void fall() {
		 if (info==1) {
			// Un monstre vient de mourir
			// On teste si un bonus va apparaitre
			int k,l,m;
			k=1+(int) (Math.random()*6);
			l=-1;m=0;
			switch (k) {
				case 5: case 6:
				l=SPR_COEUR;
				break;
				case 3: case 4:
				l=SPR_DIAMANT;
				case 1:
				l=SPR_DIAMANT;
				m=2;
				break;
			}
			if (l!=-1) {
				EngineZildo.spriteManagement.spawnSpriteGeneric(l,(int)x,(int)y,m, null);
			}
		 }
	}
	
}