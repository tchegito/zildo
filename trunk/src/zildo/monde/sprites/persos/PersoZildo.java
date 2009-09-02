package zildo.monde.sprites.persos;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import zildo.fwk.bank.SpriteBank;
import zildo.fwk.gfx.PixelShaders;
import zildo.monde.items.Item;
import zildo.monde.items.ItemCircle;
import zildo.monde.items.ItemKind;
import zildo.monde.map.Angle;
import zildo.monde.map.Point;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.desc.ZildoDescription;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.elements.ElementArrow;
import zildo.monde.sprites.elements.ElementBomb;
import zildo.monde.sprites.elements.ElementBoomerang;
import zildo.monde.sprites.utils.MouvementZildo;
import zildo.prefs.Constantes;
import zildo.server.EngineZildo;


public class PersoZildo extends Perso {
	
	private SpriteEntity pushingSprite;
	private Point posAvantSaut;
	
	private boolean inventoring=false; 
	public ItemCircle guiCircle;
	private List<Item> inventory;
	
	public Item weapon;
	public int countArrow;
	public int countBomb;
	private SpriteEntity boomerang;
	
	// Sequence for sprite animation
	static int seq_1[]={0,1,2,1};
	static int seq_2[]={0,1,2,1,0,3,4,3};
	
	// Positions in the 'PersoSprites' List
	static int linkedSpr_SHIELD=0;
	static int linkedSpr_SHADOW=1;
	static int linkedSpr_WET_FEET=2;
	static int linkedSpr_CARRIED=3;
	
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
		setDx(0);
		setDy(0);
		setAngle(Angle.NORD);
		setPos_seqsprite(-1);
		setMouvement(MouvementZildo.MOUVEMENT_VIDE);
		setInfo(2);
		setMaxpv(10);
		setPv(12);
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
		ombre.setX(getX());
		ombre.setY(getY());
		ombre.setNBank(SpriteBank.BANK_ZILDO);
		ombre.setNSpr(103);
	
		Element piedsMouilles=new Element(this);
		piedsMouilles.setX(getX());
		piedsMouilles.setY(getY());
		piedsMouilles.setNBank(SpriteBank.BANK_ZILDO);
		piedsMouilles.setNSpr(103);
	
		addPersoSprites(bouclier);
		addPersoSprites(ombre);
		addPersoSprites(piedsMouilles);

		weapon=new Item(ItemKind.BOOMERANG);

		inventory=new ArrayList<Item>();
		inventory.add(weapon);
		inventory.add(new Item(ItemKind.BOW));
		inventory.add(new Item(ItemKind.SWORD));
		inventory.add(new Item(ItemKind.BOMB));
		
		countArrow=20;
		countBomb=20;
		
	}
	
	public boolean isZildo() {
		return true;
	}
	
	public void initPersoFX() {
	
	}
	
	public SpriteEntity getPushingSprite() {
		return pushingSprite;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// attack
	///////////////////////////////////////////////////////////////////////////////////////
	public void attack() {
		switch (weapon.kind) {
		case SWORD:
			EngineZildo.soundManagement.broadcastSound("ZildoAttaque", this);
			setMouvement(MouvementZildo.MOUVEMENT_ATTAQUE_EPEE);
			setAttente(6*2);
		
			// Get the attacked tile
			Point tileAttacked=getAttackTarget();
			// And ask 'map' object to react
			EngineZildo.mapManagement.getCurrentMap().attackTile(tileAttacked);
			break;
		case BOW:
			if (attente == 0 && countArrow > 0) {
				setMouvement(MouvementZildo.MOUVEMENT_ATTAQUE_ARC);
				setAttente(4*8);
			}
			break;
		case BOOMERANG:
            if (attente == 0 && (boomerang == null || !boomerang.isVisible())) {
                setMouvement(MouvementZildo.MOUVEMENT_ATTAQUE_BOOMERANG);
                boomerang=new ElementBoomerang(angle, (int) x, (int) y, (int) z, this);
                EngineZildo.spriteManagement.spawnSprite(boomerang);
                setAttente(16);
            }
            break;
		case BOMB:
			if (attente == 0 && countBomb > 0) {
                Element bomb=new ElementBomb((int) x, (int) y,0);
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
	public void manageCollision() {
		if (getMouvement()==MouvementZildo.MOUVEMENT_ATTAQUE_EPEE) {
			// La collision avec l'épée de Zildo}
			double cx,cy,alpha;
			int rayon;
			cx=getX();
			cy=getY();
			rayon=6;
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
			EngineZildo.collideManagement.addCollision((int) cx, (int) cy, rayon, null, Angle.NORD, this);
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// beingWounded
	///////////////////////////////////////////////////////////////////////////////////////
	// IN : cx,cy : enemy's position
	///////////////////////////////////////////////////////////////////////////////////////
	// Invoked when Zildo got wounded by any enemy.
	///////////////////////////////////////////////////////////////////////////////////////
	public boolean beingWounded(float cx, float cy, Perso p_shooter) {
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
		setMouvement(MouvementZildo.MOUVEMENT_TOUCHE);
		setWounded(true);
		this.setPv(getPv()-1);
	
		// Si Zildo a quelque chose dans les mains, on doit le laisser tomber
		if (getEn_bras() != 0) {
			Element elem=persoSprites.get(3);
			persoSprites.remove(3);
			elem.az=-0.07f;
			setEn_bras(0);
		}
		EngineZildo.soundManagement.broadcastSound("ZildoTouche", this);
	
		if (guiCircle != null) {
			guiCircle.kill();
			inventoring=false;
			guiCircle=null;
		}
		
        boolean die = getPv() == 0;
        if (die) {
            die(false, p_shooter);
        }

        return die;
    }

    /**
     * Zildo is dead ! Send messages and respawn (in multiplayer deathmatch)
     */
    public void die(boolean p_link, Perso p_shooter) {
        super.die(p_link, p_shooter);
        EngineZildo.messageManagement.displayDeathMessage(this, p_shooter);
        EngineZildo.respawnClient(this);
    }
	
	///////////////////////////////////////////////////////////////////////////////////////
	// stopBeingWounded
	///////////////////////////////////////////////////////////////////////////////////////
	public void stopBeingWounded()
	{
		setMouvement(MouvementZildo.MOUVEMENT_VIDE);
		setCompte_dialogue(64);     // Temps d'invulnerabilité de Zildo
		setPx(0.0f);
		setPy(0.0f);
		setSpecialEffect(PixelShaders.ENGINEFX_NO_EFFECT);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// animate
	///////////////////////////////////////////////////////////////////////////////////////
	// Manage all things related to Zildo display : shield, shadow, feets, and object taken.
	///////////////////////////////////////////////////////////////////////////////////////
	public void animate(int compteur_animation)
	{
		// Get zildo
		Perso zildo=this;
		int xx=(int) zildo.getX();
		int yy=(int) zildo.getY();
	
		// Get connected sprites
		Iterator<Element> it=zildo.getPersoSprites().iterator();
		Element bouclier=it.next();
		Element ombre=it.next();
		Element piedsMouilles=it.next();
		Element objetEnMain=null;
		if (it.hasNext()) {
			objetEnMain=it.next();
		}
		
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
		int en_bras=zildo.getEn_bras();
	
		// Default : invisible
		ombre.setVisible(false);
		piedsMouilles.setVisible(false);
		bouclier.setVisible(false);
	
		// Corrections , décalages du sprite
		if (angle==Angle.EST)
			xx-=2;
		else if (angle==Angle.OUEST)
			xx+=2;
	
		switch (mouvement) {
			 // Bouclier
			case MOUVEMENT_VIDE:
				switch (angle) {
					case NORD:
						bouclier.setX((float) (xx+8));
						bouclier.setY((float) (yy-1));
						bouclier.setZ((float) (5-1-decalbouclier3y[nSpr]));
						bouclier.setNSpr(103);
						bouclier.setNBank(SpriteBank.BANK_ZILDO);
						break;
					case EST:
						bouclier.setX((float) (xx+9));	// PASCAL : +10
						bouclier.setY((float) (yy-5+ decalbouclier2y[nSpr-ZildoDescription.RIGHT_FIXED.ordinal()]));
						bouclier.setZ(0.0f);
						bouclier.setNSpr(104);
						bouclier.setNBank(SpriteBank.BANK_ZILDO);
						break;
					case SUD:
						bouclier.setX((float) (xx-4));	// PASCAL : -3)
						bouclier.setY((float) (yy+1));
						bouclier.setZ((float) (1+1-decalboucliery[nSpr-ZildoDescription.DOWN_FIXED.ordinal()]));
						bouclier.setNSpr(105);
						bouclier.setNBank(SpriteBank.BANK_ZILDO);
						break;
					case OUEST:
						bouclier.setX((float) (xx-8));
						bouclier.setY((float) (yy-5+ decalbouclier2y[nSpr-ZildoDescription.LEFT_FIXED.ordinal()]));
						bouclier.setZ(0.0f);
						bouclier.setNSpr(106);
						bouclier.setNBank(SpriteBank.BANK_ZILDO);
						break;
				}
				bouclier.setVisible(true);
				break;
	
			case MOUVEMENT_BRAS_LEVES:
				if (angle.isVertical()) yy+=1; else yy+=2;
				if (objetEnMain != null) {
					objetEnMain.setX(xx);
					objetEnMain.setY(yy);
					objetEnMain.setZ(21);
				}
				break;
			case MOUVEMENT_SOULEVE:
			    yy+=3;
				break;
			case MOUVEMENT_TIRE:
				if (angle.isHorizontal()) yy+=1; else {
					if (angle==Angle.NORD) yy+=3; else yy+=4;
				}
				if (nSpr==47) xx-=3;
				break;
	
			case MOUVEMENT_POUSSE:
				yy+=1;
				if (angle==Angle.NORD) yy+=1;
				else if (angle==Angle.SUD) yy+=3;
				break;
	
			case MOUVEMENT_ATTAQUE_EPEE:
				xx+=decalxSword[angle.value][nSpr-(54+6*angle.value)];
				yy+=decalySword[angle.value][nSpr-(54+6*angle.value)];
				bouclier.setVisible(false);
				break;
	
			case MOUVEMENT_ATTAQUE_ARC:
				xx+=decalxBow[angle.value][nSpr-(108+3*angle.value)];
				yy+=decalyBow[angle.value][nSpr-(108+3*angle.value)];
				bouclier.setVisible(false);
				break;
			case MOUVEMENT_TOUCHE:
				nSpr=78+angle.value;
				break;
	
			case MOUVEMENT_SAUTE:
				// Zildo est en train de sauter, on affiche l'ombre à son arrivée
	
				ombre.setX(zildo.getDx()); //(float) (xx-ax)); //-6;)
				ombre.setY(zildo.getDy()); //(float) (yy-ay)-3);
				ombre.setNSpr(2);
				ombre.setNBank(SpriteBank.BANK_ELEMENTS);
				ombre.setZ(0);
				ombre.setVisible(true);
				bouclier.setVisible(false);
	
				// Trajectoire en cloche
				double alpha=(Math.PI*(float)en_bras)/32.0f;
				yy=yy-(int) (8.0f*Math.sin(alpha));
	
				break;
	
			case MOUVEMENT_FIERTEOBJET:
				yy-=10;
				break;
		}
	
		// On affiche Zildo
	
		touche=(mouvement==MouvementZildo.MOUVEMENT_TOUCHE || zildo.getCompte_dialogue()!=0);
		// Clignotement de Zildo
		touche=( touche && ((compteur_animation >> 1) % 2)==0 );
		visible=!touche;
	
		// Ajustemenent
		xx-=7;
		yy-=21;
	
	
		if (zildo.isAlerte()) {
			// Zildo a les pieds dans l'eau
			//spriteManagement.aff_spriteplace(BANK_ZILDO,100+(compteur_animation / 20),xx+1,yy+1);
		}
		
		if (mouvement==MouvementZildo.MOUVEMENT_BRAS_LEVES)
		{
			// On affiche ce que Zildo a dans les mains
	
			// Corrections...
			if (objetEnMain != null) {
				int objX=(int) objetEnMain.getX();
				int objY=(int) objetEnMain.getY();
				if (angle==Angle.EST) objX++;
				else if (angle==Angle.OUEST) objX--;
				objY+=seq_1[((zildo.getPos_seqsprite() % (4*Constantes.speed)) / Constantes.speed)];
		
				objetEnMain.setX(objX);
				objetEnMain.setY(objY);
			}
			if (en_bras==32) {// Il s'agit d'une poule
				//spriteManagement.aff_sprite(BANK_PNJ,35+compteur_animation / 20,xx-7,yy-21-8);
			} else {
				//spriteManagement.aff_sprite(BANK_ELEMENTS,en_bras,xx-7,yy-21-8);
			}
		} else if (mouvement==MouvementZildo.MOUVEMENT_SOULEVE)
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
		
		zildo.setAjustedX(xx);
		zildo.setAjustedY(yy);
		zildo.setNSpr(nSpr);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// finaliseComportementPnj
	///////////////////////////////////////////////////////////////////////////////////////
	// Manage character's graphic side, depending on the position in the animated sequence.
	///////////////////////////////////////////////////////////////////////////////////////
	public void finaliseComportement(int compteur_animation) {
		
		final int[] seq_zildoBow={0,1,2,1};
		switch (getMouvement())
		{
		case MOUVEMENT_VIDE:
			setSpr(ZildoDescription.getMoving(angle, ((pos_seqsprite+1) % (8*Constantes.speed)) / Constantes.speed));
			//setNSpr(angle.value*7 + seq_zildoDeplacement[angle.value][((pos_seqsprite+1) % (8*Constantes.speed)) / Constantes.speed]);
			break;
		case MOUVEMENT_SAUTE:
			setNSpr(angle.value + 96);
			break;
		case MOUVEMENT_BRAS_LEVES:
			if (angle.isVertical()) {
				setNSpr(angle.value*4 + seq_2[(pos_seqsprite % (8*Constantes.speed)) / Constantes.speed]+28);
			} else {
				setNSpr(((angle.value-1) / 2)*8 + seq_1[(pos_seqsprite % (4*Constantes.speed)) / Constantes.speed]+33);
				}
			break;
		case MOUVEMENT_SOULEVE:
			switch (angle) {
				case NORD:setNSpr(44); break;
				case EST:setNSpr(52); break;
				case SUD:setNSpr(48); break;
				case OUEST:setNSpr(53); break;
			}
			break;
		case MOUVEMENT_TIRE:
			setNSpr(44+2*angle.value+pos_seqsprite);
			break;
		case MOUVEMENT_TOUCHE:
			setNSpr(78+angle.value);
			break;
		case MOUVEMENT_POUSSE:
			if (angle==Angle.NORD) {
				setNSpr(seq_2[(pos_seqsprite/2 % (8*Constantes.speed)) / Constantes.speed]+82);
			} else {
				setNSpr(angle.value*3 + seq_1[(pos_seqsprite/2 % (4*Constantes.speed)) / Constantes.speed]+84);
			}
			break;
		case MOUVEMENT_ATTAQUE_EPEE:
		    setNSpr(angle.value*6 + (((6*2-getAttente()-1) % (6*2)) / 2) + 54);
			break;
		case MOUVEMENT_ATTAQUE_ARC:
			
		    setNSpr(angle.value*3 + seq_zildoBow[(((4*8-getAttente()-1) % (4*8)) / 8)] + 108);
			if (attente==2*8) {
				EngineZildo.soundManagement.broadcastSound("FlecheTir", this);
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
		}
		// Sound
		switch (desc) {
			case GREENMONEY1: case BLUEMONEY1: case REDMONEY1:
				EngineZildo.soundManagement.broadcastSound("ZildoRecupArgent", this);
				break;
			case HEART: case HEART_LEFT:
				default:
				EngineZildo.soundManagement.broadcastSound("ZildoRecupCoeur", this);
				break;
		}
	}
	
	/**
	 * Zildo pick up something (bushes, hen...)
	 * Object can be already on the map (hen), or we can spawn it there (bushes, jar).
	 * @param obj
	 */
	public void takeSomething(int objX, int objY, int obj, Element object) {
		EngineZildo.soundManagement.broadcastSound("ZildoRamasse", this);

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

        addPersoSprites(elem);	// Link to Zildo
        
		if (object == null) {
			EngineZildo.spriteManagement.spawnSprite(elem);
		}
		
		// On passe en position "soulève", et on attend 20 frames
		setMouvement(MouvementZildo.MOUVEMENT_SOULEVE);
		setAttente(20);
		setEn_bras(obj);

	}
	
	/**
	 * Zildo throws what he got in his raised arms. (enBras)
	 */
	public void throwSomething() {
		//On jette un objet
		Element element=getPersoSprites().get(linkedSpr_CARRIED);
		getPersoSprites().remove(linkedSpr_CARRIED);
		setEn_bras(0);
		setMouvement(MouvementZildo.MOUVEMENT_VIDE);
		element.setX(getX());
		element.setY(getY());
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
		EngineZildo.soundManagement.broadcastSound("ZildoLance", this);		
	}
	
	public void lookInventory() {
		if (inventory.size() > 0) {
			EngineZildo.soundManagement.playSound("MenuOut", this);		
			inventoring=true;
			guiCircle=new ItemCircle();
			int sel=inventory.indexOf(weapon);
			guiCircle.create(inventory, sel, this);
		}
	}
	
	public void closeInventory() {
		EngineZildo.soundManagement.playSound("MenuIn", this);		
		guiCircle.close();	// Ask for the circle to close
		weapon=inventory.get(guiCircle.getItemSelected());
	}
	
	public boolean isInventoring() {
		return inventoring;
	}
	
	/**
	 * Zildo avance contre un SpriteEntity
	 * @param object
	 */
	public void pushSomething(SpriteEntity object) {
		pushingSprite=object;
	}

	public Point getPosAvantSaut() {
		return posAvantSaut;
	}

	public void setPosAvantSaut(Point posAvantSaut) {
		this.posAvantSaut = posAvantSaut;
	}
}