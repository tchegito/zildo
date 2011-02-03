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

package zildo.monde.sprites.persos;

import zildo.client.sound.BankSound;
import zildo.fwk.gfx.PixelShaders.EngineFX;
import zildo.monde.Hasard;
import zildo.monde.map.Angle;
import zildo.monde.map.Point;
import zildo.monde.map.Pointf;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.desc.SpriteAnimation;
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
	
	@Override
	public void attack() {
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// manageCollision
	///////////////////////////////////////////////////////////////////////////////////////
	// -create collision zone for this character
	///////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void manageCollision() {
		if ((getInfo() == PersoInfo.ENEMY || getInfo() == PersoInfo.SHOOTABLE_NEUTRAL) && getPv()>0) {
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
	@Override
	public boolean beingWounded(float cx, float cy, Perso p_shooter, int p_damage) {
		project(cx, cy, 6);
		this.setMouvement(MouvementZildo.TOUCHE);
		this.setWounded(true);
		this.setAlerte(true);				// Zildo is detected, if it wasn't done !
		this.setPv(getPv()-p_damage);
		this.setSpecialEffect(EngineFX.PERSO_HURT);
	
		boolean died=(getPv()<=0);
		if (died) {
			die(true, p_shooter);
		}
	
		EngineZildo.soundManagement.broadcastSound(BankSound.MonstreTouche, this);
	
		return died;
	}
	
	@Override
	public void parry(float cx, float cy, Perso p_shooter) {
		project(cx, cy, 2);
		EngineZildo.soundManagement.broadcastSound(BankSound.BoomerangTape, this);
		EngineZildo.spriteManagement.spawnSprite(new ElementImpact((int) x, (int) y, ImpactKind.SIMPLEHIT, null));

	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// stopBeingWounded
	///////////////////////////////////////////////////////////////////////////////////////
	@Override
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
	@Override
	public void animate(int compteur_animation) {
		if (getPv() == 0 || getDialoguingWith() != null) {
			return;
		}
	
		float sx=getX(),sy=getY();
		int xx,yy;
		PersoZildo zildo=EngineZildo.persoManagement.getZildo();
	
		if (zildo != null) {
			if (px != 0.0f || py != 0.0f) {
				// Le perso s'est fait toucher !}
				Pointf location=tryMove(x+px, y+py);
				x=location.x;
				y=location.y;
				px*=0.9f;
				py*=0.9f;
				setAttente(0);
				if ( (Math.abs(px) + Math.abs(py))<0.4f) {
					this.stopBeingWounded();
				}
			}
	
			if (isAlerte() && MouvementPerso.SCRIPT_VOLESPECTRE != quel_deplacement) {
				// Zildo has been caught, so the monster try to reach him, or run away (hen)
				boolean fear=this.getQuel_deplacement()==MouvementPerso.SCRIPT_POULE;
				reachAvoidTarget(zildo, fear);
			} else {
				switch (this.getQuel_deplacement()) {
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
							if (quel_spr == PersoDescription.CORBEAU) {
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
							pathFinder.determineDestinationFlying();
							cptMouvement=0;
						} else if (attente!=0) {
							attente--;
						} else {
							if (quel_spr == PersoDescription.CORBEAU) {
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
							pathFinder.reachDestinationFlying();
							cptMouvement++;
						}
						break;
					case SCRIPT_POULE:
						if (z>0) { // La poule est en l'air, elle n'est plus libre de ses mouvements
							physicMoveWithCollision();
							break;
						}	// Sinon elle agit comme les scripts de zone
					case SCRIPT_ZONEARC:
						 if (pathFinder.target == null && lookForZildo(angle)) {
							 // Get the enemy aligned with Zildo to draw arrows
							 int deltaX=(int) (zildo.x - x);
							 int deltaY=(int) (zildo.y - y);
							 if (deltaX <= deltaY) {
								 pathFinder.target=new Point(zildo.x, y);
							 } else {
								 pathFinder.target=new Point(x, zildo.y);
							 }
						 }
						break;
					default:
						break;
				}
				if (quel_deplacement != MouvementPerso.SCRIPT_POULE &&
					quel_deplacement != MouvementPerso.SCRIPT_OBSERVE &&
					quel_deplacement != MouvementPerso.SCRIPT_VOLESPECTRE) {
                       if (pathFinder.target != null && this.getX() == pathFinder.target.x && this.getY() == pathFinder.target.y) {
                    	   pathFinder.target=null;
                            if (!isGhost() && quel_deplacement != MouvementPerso.SCRIPT_ABEILLE
                                    && (quel_deplacement != MouvementPerso.SCRIPT_RAT || Hasard.lanceDes(8))) {
                                setAttente(10 + (int) (Math.random() * 20));
                            }
                        }
						if (this.getAttente()!=0) {
							if (getQuel_spr() == PersoDescription.BAS_GARDEVERT) {
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
							if (pathFinder.target == null && MouvementPerso.SCRIPT_IMMOBILE!= quel_deplacement) {
								//Pas de destination, donc on en fixe une dans la zone de d�placement
								cptMouvement=0;
					
								if (quel_deplacement == MouvementPerso.SCRIPT_ABEILLE) {
									pathFinder.determineDestinationBee();
								} else {
									pathFinder.determineDestination();
								}
							}
							float vitesse=0.5f;
							if (quel_deplacement == MouvementPerso.SCRIPT_RAT) {
								// Script du rat => plus rapide, et crache des pierres}
								vitesse+=1;
								pos_seqsprite=pos_seqsprite % (8*Constantes.speed-1);
								if (quel_spr.equals(PersoDescription.CRABE) && Math.random()*40==2) {
									//On crache une boule de pierre}
									pos_seqsprite=8*Constantes.speed;
									EngineZildo.spriteManagement.spawnSpriteGeneric(SpriteAnimation.ROCKBALL,(int)x,(int)y,
											(int) (angle.value+Math.random()*4)	// Attention : math.random() �tait 'i' en pascal
											,null, null);
									attente=(int) (Math.random()*5);
								}
							} else if (quel_deplacement == MouvementPerso.SCRIPT_ELECTRIQUE) {
								vitesse=0.2f;
							}
							
							if (pathFinder.target != null) {	// Move character if he has a target
								Pointf loc=pathFinder.reachDestination(vitesse);
								x=loc.x;
								y=loc.y;
								
								walkTile(true);

								// suite_mouvement
								if (quel_deplacement == MouvementPerso.SCRIPT_ELECTRIQUE) {
									angle=Angle.NORD;
						
								} else if (quel_deplacement == MouvementPerso.SCRIPT_ABEILLE) {
									angle=Angle.fromInt(angle.value & 2);
								}
								if (quel_deplacement != MouvementPerso.SCRIPT_VOLESPECTRE) {
									// Collision ?
									if (EngineZildo.mapManagement.collide((int) getX(),(int) getY(),this)) {
										this.setX ( sx);
										this.setY ( sy);
										if (nbShock++ == 3 && !isGhost()) {
											pathFinder.target=null;
											nbShock=0;
										}
										this.setAttente(10 + (int) (Math.random()*20));
									} else {
										this.setPos_seqsprite ( (getPos_seqsprite() + 1) % 512);
									}
								}
							}
						}
					}
				
			}
		}
		
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// initPersoFX
	///////////////////////////////////////////////////////////////////////////////////////
	// Set perso with initial special effect.
	///////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void initPersoFX() {
		if (getQuel_spr() == PersoDescription.GARDE_CANARD) {	// Guard
			String str=getEffect() != null ? getEffect() : getNom();
			if ("jaune".equals(str)) {
				setSpecialEffect(EngineFX.GUARD_YELLOW);
			} else if("vert".equals(str)) {
				setSpecialEffect(EngineFX.GUARD_GREEN);
			} else if("rouge".equals(str)) {
				setSpecialEffect(EngineFX.GUARD_RED);
			} else if("rose".equals(str)) {
				setSpecialEffect(EngineFX.GUARD_PINK);
			} else if("noir".equals(str)) {
				setSpecialEffect(EngineFX.GUARD_BLACK);
			} else {
				// Default color for this guard : blue
				setSpecialEffect(EngineFX.GUARD_BLUE);
			}
		} else {
			setSpecialEffect(EngineFX.NO_EFFECT);
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// finaliseComportement
	///////////////////////////////////////////////////////////////////////////////////////
	// Manage character's graphic side, depending on the position in the animated sequence.
	///////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void finaliseComportement(int compteur_animation) {
	
		this.setAjustedX((int) x);
		this.setAjustedY((int) y);

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
			   if (pathFinder.target != null && pathFinder.target.x>getX())
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
				add_spr=(compteur_animation / 30) % 2;
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
				add_spr=(compteur_animation / 20) % 3;
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
				add_spr=(angle.value & 2) + (compteur_animation / 30) % 2;
				break;
			default:
				add_spr=angle.value*2 + (getPos_seqsprite() % (4*Constantes.speed)) / (2*Constantes.speed);
				break;
		}
	
		this.setNSpr((this.getQuel_spr().first() + add_spr) % 128);
	}
	
	/**
	 * Reach or run away from a given character.
	 * @param p_perso target character
	 * @param p_fear TRUE=avoid / FALSE=reach
	 */
	public void reachAvoidTarget(Perso p_perso, boolean p_fear) {
		float sx=x;
		float sy=y;
		cptMouvement=0;
		float ddx=p_perso.getX();
		float ddy=p_perso.getY();
		if (p_fear) {
			// Character should run away instead of reach the character p_perso
			ddx=2*x-ddx;
			ddy=2*y-ddy;
		}
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
				pathFinder.target=null;
				setAttente(10);	
				// On replace la zone de d�placement autour de l'ennemi
				setZone_deplacement(EngineZildo.mapManagement.range(x-16*5, y-16*5, x+16*5, y+16*5));
			}
		}
		if (!isGhost()) {	// Replace angle if character isn't ghost (moved by script)
			if (x>sx) {
				setAngle(Angle.EST);
			} else if (x<sx) {
				setAngle(Angle.OUEST);
			} else if (y>sy) {
				setAngle(Angle.SUD);
			} else {
				setAngle(Angle.NORD);
			}
		}
		if (sx!=x && sy!=y) {	// Diagonal moves
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
	
	@Override
    public void die(boolean p_link, Perso p_shooter) {
		super.die(p_link, p_shooter);
		
		 if (info==PersoInfo.ENEMY) {
			// Un monstre vient de mourir
			// On teste si un bonus va apparaitre
			int k,m;
			SpriteAnimation anim=null;
			k=1+(int) (Math.random()*6);
			m=0;
			switch (k) {
				case 5: case 6:
					anim=SpriteAnimation.HEART;
					break;
				case 3: case 4:
					anim=SpriteAnimation.DIAMOND;
					break;
				case 1:
					anim=SpriteAnimation.DIAMOND;
					m=2;
					break;
			}
			if (anim!=null) {
				EngineZildo.spriteManagement.spawnSpriteGeneric(anim,(int)x,(int)y,m, null, null);
			}
		 }
	}
	
}