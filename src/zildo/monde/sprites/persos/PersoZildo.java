/**
 * Legend of Zildo
 * Copyright (C) 2006-2010 Evariste Boussaton
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import zildo.SinglePlayer;
import zildo.client.sound.BankSound;
import zildo.fwk.bank.SpriteBank;
import zildo.fwk.gfx.PixelShaders.EngineFX;
import zildo.fwk.script.xml.TriggerElement;
import zildo.monde.collision.Collision;
import zildo.monde.collision.DamageType;
import zildo.monde.items.Item;
import zildo.monde.items.ItemCircle;
import zildo.monde.items.ItemKind;
import zildo.monde.map.Angle;
import zildo.monde.map.Point;
import zildo.monde.quest.actions.GameOverAction;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.desc.ZildoDescription;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.elements.ElementArrow;
import zildo.monde.sprites.elements.ElementBomb;
import zildo.monde.sprites.elements.ElementBoomerang;
import zildo.monde.sprites.utils.MouvementZildo;
import zildo.monde.sprites.utils.ShieldEffect;
import zildo.monde.sprites.utils.ShieldEffect.ShieldType;
import zildo.prefs.Constantes;
import zildo.server.EngineZildo;
import zildo.server.MultiplayerManagement;


public class PersoZildo extends Perso {
	
	private SpriteEntity pushingSprite;
	private Point posAvantSaut;
	private Point posShadowJump;
	private Angle jumpAngle;
	
	private Angle sightAngle;	// For boomerang
	
	private int touch;	// number of frames zildo is touching something without moving
	
	private boolean inventoring=false; 
	public ItemCircle guiCircle;
	private List<Item> inventory;
	private ShieldEffect shieldEffect;
	
	public Item weapon;

	private SpriteEntity boomerang;
	private int quadDuration;
	
	// Sequence for sprite animation
	static int seq_1[]={0,1,2,1};
	static int seq_2[]={0,1,2,1,0,3,4,3};
	
	//////////////////////////////////////////////////////////////////////
	// Construction/Destruction
	//////////////////////////////////////////////////////////////////////
	
	public PersoZildo(int p_id) {	// Only used to create Zildo on a client
		id=p_id;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// PersoZildo
	///////////////////////////////////////////////////////////////////////////////////////
	// Return a perso named Zildo : this game's hero !
	// with a given location.
	///////////////////////////////////////////////////////////////////////////////////////
    public PersoZildo(int p_posX, int p_posY) {
        super();
        this.setNom("Zildo");

        // We could maybe put that somewhere else
        this.setNBank(SpriteBank.BANK_ZILDO);
        setNSpr(0);

        setX(p_posX); // 805); //601-32;//-500);
        setY(p_posY); // 973); //684+220;//-110);
		setAngle(Angle.NORD);
		setPos_seqsprite(-1);
		setMouvement(MouvementZildo.VIDE);
		setInfo(PersoInfo.ZILDO);
		setMaxpv(10);
		setPv(10);
		setAlerte(false);
		setCompte_dialogue(0);
	    setMoney(0);
	    pushingSprite = null;

	    Element bouclier = new Element(this);
		bouclier.setX(getX());
		bouclier.setY(getY());
		bouclier.setNBank(SpriteBank.BANK_ZILDO);
		bouclier.setNSpr(103);				// Assign initial nSpr to avoid 'isNotFixe' returning TRUE)
	
		Element ombre=new Element(this);
		ombre.setNBank(SpriteBank.BANK_ZILDO);
		ombre.setNSpr(103);
	
		Element piedsMouilles=new Element(this);
		piedsMouilles.setNBank(SpriteBank.BANK_ZILDO);
		piedsMouilles.setNSpr(100);
	
		shieldEffect = null;
		
		addPersoSprites(bouclier);
		addPersoSprites(ombre);
		addPersoSprites(piedsMouilles);
		
		weapon=new Item(ItemKind.SWORD);
		inventory=new ArrayList<Item>();
		inventory.add(weapon);
	}
    
    /**
     * Reset any effects zildo could have before he dies.
     */
    public void resetForMultiplayer() {
		weapon=new Item(ItemKind.SWORD);
		inventory=new ArrayList<Item>();
		inventory.add(weapon);

		MultiplayerManagement.setUpZildo(this);
    	
		if (shieldEffect != null) {
			shieldEffect = null;
			shieldEffect.kill();
		}
		quadDuration=0;

    }
	
	@Override
	public boolean isZildo() {
		return true;
	}
	
	@Override
	public void initPersoFX() {
	
	}
	
	public SpriteEntity getPushingSprite() {
		return pushingSprite;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// attack
	///////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void attack() {
		switch (weapon.kind) {
		case SWORD:
			EngineZildo.soundManagement.broadcastSound(BankSound.ZildoAttaque, this);
			setMouvement(MouvementZildo.ATTAQUE_EPEE);
			setAttente(6*2);
			break;
		case BOW:
			if (attente == 0 && countArrow > 0) {
				setMouvement(MouvementZildo.ATTAQUE_ARC);
				setAttente(4*8);
			}
			break;
		case BOOMERANG:
            if (attente == 0 && (boomerang == null || !boomerang.isVisible())) {
                setMouvement(MouvementZildo.ATTAQUE_BOOMERANG);
                boomerang=new ElementBoomerang(sightAngle, (int) x, (int) y, (int) z, this);
                EngineZildo.spriteManagement.spawnSprite(boomerang);
                setAttente(16);
            }
            break;
		case BOMB:
			if (attente == 0 && countBomb > 0) {
                Element bomb=new ElementBomb((int) x, (int) y, 0, this);
                EngineZildo.spriteManagement.spawnSprite(bomb);
                countBomb--;
				setAttente(1);
			}
			break;
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// manageCollision
	///////////////////////////////////////////////////////////////////////////////////////
	// -create collision zone for Zildo
	///////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void manageCollision() {
		if (getMouvement()==MouvementZildo.ATTAQUE_EPEE) {
			// La collision avec l'épée de Zildo}
			double cx,cy,alpha;
			int rayon;
			cx=getX();
			cy=getY();
			rayon=4;
			alpha=(2.0f*Math.PI*this.getAttente()) / (7*Constantes.speed);
			
			switch (this.getAngle()) {
				case NORD:
					alpha=alpha + Math.PI;
					cy=cy-16;
					break;
				case EST:
					alpha=-alpha + Math.PI/2;
					cy=cy-4;
					break;
				case SUD:
					cy=cy-4;
					break;
				case OUEST:
					alpha=alpha + Math.PI/2;
					cy=cy-4;
					cx=cx-4;
					break;
			}
			if (angle.isHorizontal()) {
				cx=cx+16*Math.cos(alpha);
				cy=cy+16*Math.sin(alpha);
			} else {
				cx=cx+12*Math.cos(alpha);
				cy=cy+12*Math.sin(alpha);
			}
	
			// Add this collision record to collision engine
			// Damage type: blunt at start, and then : cutting front
			DamageType dmgType=DamageType.BLUNT;
			if (attente < 6) {
				dmgType=DamageType.CUTTING_FRONT;
			}
			Collision c=new Collision((int) cx, (int) cy, rayon, Angle.NORD, this, dmgType, null);
			EngineZildo.collideManagement.addCollision(c);
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// beingWounded
	///////////////////////////////////////////////////////////////////////////////////////
	// IN : cx,cy : enemy's position
	///////////////////////////////////////////////////////////////////////////////////////
	// Invoked when Zildo got wounded by any enemy.
	///////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean beingWounded(float cx, float cy, Perso p_shooter, int p_damage) {
		// Project Zildo away from the enemy
		float diffx=getX()-cx;
		float diffy=getY()-cy;
		float norme=(float) Math.sqrt( (diffx*diffx) + (diffy*diffy) );
	    if (norme==0.0f) {
			norme=1.0f;           //Pour éviter le 'divide by zero'
		}
		// Et on l'envoie !
		setPx(8*(diffx/norme));
		setPy(8*(diffy/norme));

		return beingWounded(p_shooter, p_damage);
    }

	public boolean beingWounded(Perso p_shooter, int p_damage) {
		setMouvement(MouvementZildo.TOUCHE);
		setWounded(true);
		this.setPv(getPv()-p_damage);
		
		// Si Zildo a quelque chose dans les mains, on doit le laisser tomber
		if (getEn_bras() != null) {
			getEn_bras().az=-0.07f;
			setEn_bras(null);
		}
		EngineZildo.soundManagement.broadcastSound(BankSound.ZildoTouche, this);
	
		if (guiCircle != null) {
			guiCircle.kill();
			inventoring=false;
			guiCircle=null;
		}
		
        boolean die = getPv() <= 0;
        if (die) {
            die(false, p_shooter);
        }

        return die;
	}
	
    /**
     * Zildo is dead ! Send messages and respawn (in multiplayer deathmatch)
     */
    @Override
	public void die(boolean p_link, Perso p_shooter) {
        super.die(p_link, p_shooter);
        if (EngineZildo.game.multiPlayer) {
        	EngineZildo.multiplayerManagement.kill(this, p_shooter);
        	EngineZildo.respawnClient(this);
        } else {
        	// Game over
        	EngineZildo.dialogManagement.launchDialog(SinglePlayer.getClientState(), null, new GameOverAction());
        }
    }
	
	///////////////////////////////////////////////////////////////////////////////////////
	// stopBeingWounded
	///////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void stopBeingWounded()
	{
		setMouvement(MouvementZildo.VIDE);
		setCompte_dialogue(64);     // Temps d'invulnerabilité de Zildo
		setPx(0.0f);
		setPy(0.0f);
		setSpecialEffect(EngineFX.NO_EFFECT);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// animate
	///////////////////////////////////////////////////////////////////////////////////////
	// Manage all things related to Zildo display : shield, shadow, feets, and object taken.
	///////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void animate(int compteur_animation)
	{
		// If zildo's dead, don't display him
		if (getPv() <= 0) {
			setVisible(false);
			return;
		}
		
		// Get zildo
		Perso zildo=this;
		int xx=(int) zildo.getX();
		int yy=(int) zildo.getY();
	
		// Get connected sprites
		Iterator<Element> it=zildo.getPersoSprites().iterator();
		Element bouclier=it.next();
		Element ombre=it.next();
		Element piedsMouilles=it.next();
		
		final int decalxSword[][]={
			{1,-1,-1,-5,-10,-13},{0,2,3,2,1,1},
			{-5,-6,-4,-4,-4,-4},{-2,-12,-18,-14,-13,-8}};
		final int decalySword[][]={
			{2,-6,-11,-6,-3,1},{1,0,3,3,2,2},
			{1,3,3,6,3,3},{1,0,3,3,2,2}};
	
		final int decalxBow[][]={
				{-2,-5,-5},{0,0,0},{0,0,0},{-1,-3,-4}
		};
		final int decalyBow[][]={
				{2,3,2},{1,2,1},{3,2,2},{1,2,1}
		};
		int decalboucliery[]={0,0,0,-1,-2,-1};
		int decalbouclier2y[]={0,-1,-1,0,-1,-1,0,0};
	    int decalbouclier3y[]={0,0,-1,-2,0,-1,0,0};
	
	    boolean touche;
	
		// Get variables to reduce code amount
		int nSpr=zildo.getNSpr();
		Angle angle=zildo.getAngle();
		MouvementZildo mouvement=zildo.getMouvement();
		Element en_bras=zildo.getEn_bras();
	
        // Default : invisible
        ombre.setVisible(false);
        piedsMouilles.setVisible(inWater);
        bouclier.setVisible(false);

        if (isQuadDamaging()) {
            if (shieldEffect == null) {
                shieldEffect = new ShieldEffect(this, ShieldType.REDBALL);
            }
            setSpecialEffect(EngineFX.QUAD);
        } else if (shieldEffect != null) {
            setSpecialEffect(EngineFX.NO_EFFECT);
            shieldEffect.kill();
            shieldEffect = null;
        }
        // Shield effect animation
        if (shieldEffect != null) {
        	shieldEffect.animate();
        }
        
		// Corrections , décalages du sprite
		if (angle==Angle.EST)
			xx-=2;
		else if (angle==Angle.OUEST)
			xx+=2;

		
		switch (mouvement) {
			 // Bouclier
			case VIDE:
				if (hasItem(ItemKind.SHIELD)) {
					bouclier.setForeground(false);
					switch (angle) {
						case NORD:
							bouclier.setX(xx+8);
							bouclier.setY(yy+2);
							bouclier.setZ(5-1-decalbouclier3y[nSpr]);
							bouclier.setNSpr(103);
							bouclier.setNBank(SpriteBank.BANK_ZILDO);
							break;
						case EST:
							bouclier.setX(xx+9);	// PASCAL : +10
							bouclier.setY(yy-2+ decalbouclier2y[nSpr-ZildoDescription.RIGHT_FIXED.ordinal()]);
							bouclier.setZ(0.0f);
							bouclier.setNSpr(104);
							bouclier.setNBank(SpriteBank.BANK_ZILDO);
							break;
						case SUD:
							bouclier.setX(xx-4);	// PASCAL : -3)
							bouclier.setY(yy+4);
							bouclier.setZ(1+1-decalboucliery[nSpr-ZildoDescription.DOWN_FIXED.ordinal()]);
							bouclier.setNSpr(105);
							bouclier.setNBank(SpriteBank.BANK_ZILDO);
							break;
						case OUEST:
							bouclier.setX(xx-8);
							bouclier.setY(yy-2+ decalbouclier2y[nSpr-ZildoDescription.LEFT_FIXED.ordinal()]);
							bouclier.setZ(0.0f);
							bouclier.setNSpr(106);
							bouclier.setNBank(SpriteBank.BANK_ZILDO);
							break;
					}
					bouclier.setVisible(true);
				}
				break;
	
			case BRAS_LEVES:
				yy++;
				if (angle.isVertical()) {
					yy++;
				}
				if (en_bras != null) {
                    en_bras.setX(xx + 1);
                    en_bras.setY(yy + 3);
                    en_bras.setZ(17);
				}
				break;
			case SOULEVE:
			    yy+=3;
				break;
			case TIRE:
				if (angle.isHorizontal()) yy+=1; else {
					if (angle==Angle.NORD) yy+=3; else yy+=4;
				}
				if (nSpr==47) xx-=3;
				break;
	
			case POUSSE:
				yy+=1;
				if (angle==Angle.NORD) yy+=1;
				else if (angle==Angle.SUD) yy+=3;
				break;
	
			case ATTAQUE_EPEE:
				xx+=decalxSword[angle.value][nSpr-(54+6*angle.value)];
				yy+=decalySword[angle.value][nSpr-(54+6*angle.value)];
				bouclier.setVisible(false);
				break;
	
			case ATTAQUE_ARC:
				xx+=decalxBow[angle.value][nSpr-(108+3*angle.value)];
				yy+=decalyBow[angle.value][nSpr-(108+3*angle.value)];
				bouclier.setVisible(false);
				break;
			case TOUCHE:
				nSpr=78+angle.value;
				break;
	
			case SAUTE:
				// Zildo est en train de sauter, on affiche l'ombre à son arrivée
	
				ombre.setX(posShadowJump.x); //(float) (xx-ax)); //-6;)
				ombre.setY(posShadowJump.y); //(float) (yy-ay)-3);
				ombre.setNSpr(2);
				ombre.setNBank(SpriteBank.BANK_ELEMENTS);
				ombre.setZ(0);
				ombre.setVisible(true);
				bouclier.setVisible(false);
	
				// Trajectoire en cloche
				double alpha=(Math.PI*attente)/32.0f;
				yy=yy-(int) (8.0f*Math.sin(alpha));
	
				break;
	
			case FIERTEOBJET:
				nSpr=ZildoDescription.ARMSRAISED.ordinal();
				yy++;
				break;
		}
	
		// On affiche Zildo
		piedsMouilles.setX(x+ (angle.isVertical() || angle==Angle.OUEST ? 1 : 0));
		piedsMouilles.setY(y+9 + 1);
		piedsMouilles.setZ(3);
		piedsMouilles.setNSpr(100 + (compteur_animation / 6) % 3);
		piedsMouilles.setForeground(false);
		
		touche=(mouvement==MouvementZildo.TOUCHE || zildo.getCompte_dialogue()!=0);
		// Zildo blink
		touche=( touche && ((compteur_animation >> 1) % 2)==0 );
		visible=!touche;
		for (Element elem :persoSprites) {	// Blink linked elements too
			if (elem.isVisible()) {
				elem.setVisible(visible);
			}
		}
	
		// Ajustemenent
		xx-=7;
		yy-=21;
	
	
		if (zildo.isAlerte()) {
			// Zildo a les pieds dans l'eau
			//spriteManagement.aff_spriteplace(BANK_ZILDO,100+(compteur_animation / 20),xx+1,yy+1);
		}
		
		if (mouvement==MouvementZildo.BRAS_LEVES)
		{
			// On affiche ce que Zildo a dans les mains
	
			// Corrections...
			if (en_bras != null) {
				int objX=(int) en_bras.getX();
				int objY=(int) en_bras.getY();
				int objZ=(int) en_bras.getZ();
				if (angle==Angle.EST) objX++;
				else if (angle==Angle.OUEST) objX--;
				int variation=seq_1[((zildo.getPos_seqsprite() % (4*Constantes.speed)) / Constantes.speed)];
		
				en_bras.setX(objX);
				en_bras.setY(objY);
				en_bras.setZ(objZ + variation);
			}
			if (en_bras.getNSpr()==32) {// Il s'agit d'une poule
				//spriteManagement.aff_sprite(BANK_PNJ,35+compteur_animation / 20,xx-7,yy-21-8);
			} else {
				//spriteManagement.aff_sprite(BANK_ELEMENTS,en_bras,xx-7,yy-21-8);
			}
		} else if (mouvement==MouvementZildo.SOULEVE)
		{
			// Si Zildo est en train de soulever un objet, on l'affiche
			//xx-=8;yy-=11;
			//if (angle==1) xx+=6;
			//else if (angle==3) xx-=6;
			//spriteManagement.aff_sprite(BANK_ELEMENTS,en_bras,xx,yy-8);
		}
	
		// GUI circle
		if (guiCircle != null) {
			guiCircle.animate();
			if (guiCircle.isReduced()) {
				inventoring=false;
				guiCircle=null;
			}
		}
		
		// Quad damage
		if (quadDuration > 0) {
			quadDuration--;
			if (quadDuration == 160) {
				EngineZildo.soundManagement.playSound(BankSound.QuadDamageLeaving, this);
			}
		}
		zildo.setAjustedX(xx);
		zildo.setAjustedY(yy);
		zildo.setNSpr(nSpr);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// finaliseComportementPnj
	///////////////////////////////////////////////////////////////////////////////////////
	// Manage character's graphic side, depending on the position in the animated sequence.
	///////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void finaliseComportement(int compteur_animation) {
		
		final int[] seq_zildoBow={0,1,2,1};
		reverse = 0;
		switch (getMouvement())
		{
		case VIDE:
			setSpr(ZildoDescription.getMoving(angle, ((pos_seqsprite+1) % (8*Constantes.speed)) / Constantes.speed));
			//setNSpr(angle.value*7 + seq_zildoDeplacement[angle.value][((pos_seqsprite+1) % (8*Constantes.speed)) / Constantes.speed]);
			break;
		case SAUTE:
			setNSpr(angle.value + 96);
			break;
		case BRAS_LEVES:
			if (angle.isVertical()) {
				setNSpr(angle.value*4 + seq_2[(pos_seqsprite % (8*Constantes.speed)) / Constantes.speed]+28);
			} else {
				setNSpr(((angle.value-1) / 2)*8 + seq_1[(pos_seqsprite % (4*Constantes.speed)) / Constantes.speed]+33);
				}
			break;
		case SOULEVE:
			switch (angle) {
				case NORD:setNSpr(44); break;
				case EST:setNSpr(52); break;
				case SUD:setNSpr(48); break;
				case OUEST:setNSpr(53); break;
			}
			break;
		case TIRE:
			setNSpr(44+2*angle.value+pos_seqsprite);
			break;
		case TOUCHE:
			setNSpr(78+angle.value);
			break;
		case POUSSE:
			if (angle==Angle.NORD) {
				setNSpr(seq_2[(pos_seqsprite/2 % (8*Constantes.speed)) / Constantes.speed]+82);
			} else {
				setNSpr(angle.value*3 + seq_1[(pos_seqsprite/2 % (4*Constantes.speed)) / Constantes.speed]+84);
			}
			break;
		case ATTAQUE_EPEE:
		    setNSpr(angle.value*6 + (((6*2-getAttente()-1) % (6*2)) / 2) + 54);
			break;
		case ATTAQUE_ARC:
			
		    setNSpr(angle.value*3 + seq_zildoBow[(((4*8-getAttente()-1) % (4*8)) / 8)] + 108);
			if (attente==2*8) {
				EngineZildo.soundManagement.broadcastSound(BankSound.FlecheTir, this);
				Element arrow=new ElementArrow(angle, (int) x, (int) y, 0, this);
				EngineZildo.spriteManagement.spawnSprite(arrow);
				countArrow--;
			}
		    break;
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// pickGoodies
	///////////////////////////////////////////////////////////////////////////////////////
	public void pickGoodies(int nSpr) {
		// Effect on perso
		int money=this.getMoney();
		int pv=this.getPv();
		ElementDescription desc=ElementDescription.fromInt(nSpr);
		switch (desc) {
		case GREENMONEY1:
			setMoney(money+1);
			break;
		case BLUEMONEY1:
			setMoney(money+5);
			break;
		case REDMONEY1:
			setMoney(money+20);
			break;
		case HEART: case HEART_LEFT:
			setPv(pv+1);
			break;
		case ARROW_UP:
			countArrow++;
			break;
		case QUAD1:
			quadDuration=MultiplayerManagement.QUAD_TIME_DURATION;
			EngineZildo.multiplayerManagement.pickUpQuad();
			break;
		}
		// Sound
		switch (desc) {
			case GREENMONEY1: case BLUEMONEY1: case REDMONEY1:
				EngineZildo.soundManagement.broadcastSound(BankSound.ZildoRecupArgent, this);
				break;
			case QUAD1:
				EngineZildo.soundManagement.broadcastSound(BankSound.QuadDamage, this);
				break;
			case HEART: case HEART_LEFT:
				default:
				EngineZildo.soundManagement.broadcastSound(BankSound.ZildoRecupCoeur, this);
				break;
		}
	}
	
	/**
	 * Zildo pick up something (bushes, hen...)
	 * Object can be already on the map (hen), or we can spawn it there (bushes, jar).
	 * @param obj
	 */
	@Override
	public void takeSomething(int objX, int objY, int obj, Element object) {
		EngineZildo.soundManagement.broadcastSound(BankSound.ZildoRamasse, this);

		Element elem=object;
		if (object == null) {
			elem=new Element();
			elem.setNBank(SpriteBank.BANK_ELEMENTS);
			elem.setNSpr(obj);
		}
		elem.setScrX(objX);
		elem.setScrY(objY);
		elem.setX(objX);
		elem.setY(objY);
		elem.setZ(4);
        elem.setVisible(true);
        elem.flying=false;
        
        elem.setLinkedPerso(this);	// Link to Zildo
        
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
		//On jette un objet
		Element element=getEn_bras();
		element.setLinkedPerso(null);
		setEn_bras(null);
		setMouvement(MouvementZildo.VIDE);
		element.setX(getX()+1);
		element.setY(getY()+4);
		element.setZ(21.0f+1.0f);
		element.setVx(0.0f);
		element.setVy(0.0f);
		element.setVz(0.0f);
		element.setAx(0.0f);
		element.setAy(0.0f);
		element.setAz(-0.07f);
		element.setLinkedPerso(this);	// Declare this element thrown by Zildo (so it can't collide with him)
		element.setAngle(angle);
		element.flying=true;
		element.relativeZ=EngineZildo.mapManagement.getCurrentMap().readAltitude((int) x/16, (int) y/16);

		switch (getAngle()) {
			case NORD:
				element.setVy(-4.0f);
				element.setFy(0.04f);
				break;
			case EST:
				element.setVx(4.0f);
				element.setFx(0.04f);
				break;
			case SUD:
				element.setVy(4.0f);
				element.setFy(0.04f);
				break;
			case OUEST:
				element.setVx(-4.0f);
				element.setFx(0.04f);
				break;
		}
		EngineZildo.soundManagement.broadcastSound(BankSound.ZildoLance, this);		
	}
	
	public void lookInventory() {
		if (inventory.size() > 0) {
			EngineZildo.soundManagement.playSound(BankSound.MenuOut, this);		
			inventoring=true;
			guiCircle=new ItemCircle();
			int sel=inventory.indexOf(weapon);
			guiCircle.create(inventory, sel, this);
		}
	}
	
	public void closeInventory() {
		EngineZildo.soundManagement.playSound(BankSound.MenuIn, this);		
		guiCircle.close();	// Ask for the circle to close
		weapon=inventory.get(guiCircle.getItemSelected());
	}
	
	/**
	 * Directly add an item to the inventory
	 * @param p_item
	 */
	public void addInventory(Item p_item) {
		inventory.add(p_item);
	}
	
	public boolean isInventoring() {
		return inventoring;
	}
	
    /**
     * Zildo takes an item.
     * @param p_kind
     */
    public void pickItem(ItemKind p_kind) {
        inventory.add(new Item(p_kind));
        attente=20;
        mouvement=MouvementZildo.FIERTEOBJET;
        Element elem=EngineZildo.spriteManagement.spawnElement(SpriteBank.BANK_ELEMENTS, 
        		p_kind.representation.ordinal(), 
        		(int) x + 5, 
        		(int) y + 1, 20);
        setEn_bras(elem);
        EngineZildo.soundManagement.playSound(BankSound.ZildoTrouve, this);

        // Adventure trigger
        TriggerElement trig=TriggerElement.createInventoryTrigger(p_kind);
        EngineZildo.scriptManagement.trigger(trig);
    }

    /**
     * Return TRUE if Zildo has an item from given kind.
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
	
    /**
     * Return all Zildo's inventory. Useful for saving a game.
     * @return List<Item>
     */
    public List<Item> getInventory() {
    	return inventory;
    }
    
	/**
	 * Zildo avance contre un SpriteEntity
	 * @param object
	 */
	public void pushSomething(SpriteEntity object) {
		pushingSprite=object;
	}

	/**
	 * Starts a jump in given angle.
	 * @param p_angle should not be null
	 */
	public void jump(Angle p_angle) {
		// On sauve la position de Zildo avant son saut
		Point zildoAvantSaut=new Point(x, y);
		mouvement=MouvementZildo.SAUTE;
		jumpAngle=p_angle;
		posShadowJump=p_angle.getLandingPoint().translate((int) x, (int) y);
		setEn_bras(null);
		posAvantSaut=zildoAvantSaut;
		EngineZildo.soundManagement.broadcastSound(BankSound.ZildoTombe, this);		
	}
	
	public Point getPosAvantSaut() {
		return posAvantSaut;
	}

	public Angle getJumpAngle() {
		return jumpAngle;
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
	
	public boolean isQuadDamaging() {
		return quadDuration > 0;
	}
}