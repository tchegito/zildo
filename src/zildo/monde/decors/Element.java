package zildo.monde.decors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import zildo.fwk.IntSet;
import zildo.fwk.bank.SpriteBank;
import zildo.monde.map.Angle;
import zildo.monde.persos.Perso;
import zildo.monde.persos.PersoZildo;
import zildo.monde.persos.utils.PersoDescription;
import zildo.server.EngineZildo;


//TODO: Remove getter/setter for x,y,z

public class Element extends SpriteEntity {
	
	protected static Logger logger=Logger.getLogger("Element");

	// Eléments que Zildo peut envoyer sur les ennemis
	private static IntSet elementsSolides=new IntSet(0,1,11,12,2,38,42);
	// Eléments que Zildo peut pousser
	private static IntSet elementsMobiles=new IntSet(28,69,70);
	
	// Sprites animés génériques
	public static final	int SPR_BUISSON = 0;          //Animation du buisson qui s'effeuille
	public static final	int SPR_MORT = 1;
	public static final	int SPR_BOULEPIERRE = 2;
	public static final	int SPR_ECLATEPIERRE = 3;
	public static final	int SPR_DIAMANT = 4;
	public static final	int SPR_COEUR = 5;
	public static final	int SPR_FROMCHEST = 6;
	public static final	int SPR_FUMEE = 7;

	// Class variables
	private float ancX, ancY, ancZ;
	public float ax,ay,az;
	public float vx,vy,vz;
	public float fx,fy,fz;	// Frottements
	protected char spe; //Spe est utilisé selon l'usage

	protected int addSpr;	// Pour les animations (exemple:diamants qui brillent)
	protected SpriteEntity linkedPerso;	// When this element dies, any non-perso linked entity die too.

	public Element() {
		super();
		this.initialize();
	}
	

	private void initialize() {
		entityType=ENTITYTYPE_ELEMENT;
	
		// Default Bank & Spr
		nBank=SpriteBank.BANK_ELEMENTS;
		nSpr=0;
		addSpr=0;
	
		linkedPerso=null;
	
		// Default physical value;
		ax=0.0f;
		ay=0.0f;
		az=0.0f;
	
		vx=0.0f;
		vy=0.0f;
		vz=0.0f;
	
		x=0.0f;
		y=0.0f;
		z=0.0f;
	
		fx=0.0f;
		fy=0.0f;
		fz=0.0f;
		//logger.log(Level.INFO, "Creating Element");
	}

	public Element(SpriteEntity perso)
	{
		this.initialize();
		linkedPerso=perso;
	}
	
	// Copy constructor
	public Element(Element original) {
		this.x=original.x;	this.vx=original.vx;	this.ax=original.ax;
		this.y=original.y;	this.vy=original.vy;	this.ay=original.ay;
		this.z=original.z;	this.vz=original.vz;	this.az=original.az;
		this.spe=original.spe;
		this.addSpr=original.addSpr;
		this.linkedPerso=original.linkedPerso;
	
		this.entityType=ENTITYTYPE_ELEMENT;
		//logger.log(Level.INFO, "Copying Element");
		
	}


	public void finalize()
	{
		logger.log(Level.INFO, "Deleting Element");
	
		// IMPORTANT : We shouldn't remove linked perso, because it would be a double delete !
		if (linkedPerso != null) {
			// Remove linked entity
			//EngineZildo::spriteManagement.deleteSprite(linkedPerso);
		}
		linkedPerso=null;
	
	}
	
	public boolean IsNotFixe()
	{
		int a=this.nSpr;
		if ((a>2 && a<=12) ||
			(a==0 || a==1) ||
			(a==28) ||
			(a>=32 && a<=39) ||
			(a>=40 && a<=42) ||
			(a>=44 && a<=56) ||
			(a==69) ||
			(a==70)
			)
			return true;
		else
			return false;
	}
	
	/**
	 * Let's do the physical law job.
	 */
	protected void physicMove() {
		// On conserve les anciennes positions pour les collisions
		ancX=x; ancY=y; ancZ=z;
	    vx=(vx+ax)*(1-fx);
	    vy=(vy+ay)*(1-fy);
	    vz=(vz+az)*(1-fz);
	    x =x+vx;
	    y =y+vy;
	    z =z+vz;
	    if (z<0) {	// L'objet tombe au sol
	    	z=0;
	    	vz=0;
	    	az=0;
	    	fx*=4;
	    	fy*=4;
	    	fz*=4;
	    }
	}
	
	/**
	 * Renvoie TRUE si l'élément est solide.
	 * @return boolean
	 */
	private boolean isSolid() {
		if (elementsSolides.contains(nSpr)) {
			return true;
		}
		// S'il s'agit d'un personnage
		if (entityType==SpriteEntity.ENTITYTYPE_PERSO) {
			PersoDescription desc=((Perso)this).getQuel_spr();
			return (desc.equals(PersoDescription.POULE));
		}
		return false;
	}
	
	/**
	 * Move object, and stop it in case of collision.
	 * @return boolean
	 */
	protected boolean physicMoveWithCollision() {
		physicMove();
		if (isSolid() || elementsMobiles.contains(nSpr)) {
			PersoZildo zildo=EngineZildo.persoManagement.getZildo();
			if (!zildo.linkedSpritesContains(this) && EngineZildo.mapManagement.collide((int)x,(int)y,this)) {
				// Collision : on stoppe le mouvement, on ne laisse plus que la chute pour finir au sol
				EngineZildo.mapManagement.collide((int)x,(int)y,this);
				x=ancX;
				y=ancY;
				z=ancZ;
				if (!elementsMobiles.contains(nSpr)) {
					ax=ay=0;
					vx=vy=0;
				}
				return true;
			}
		}
		return false;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// animate
	///////////////////////////////////////////////////////////////////////////////////////
	// OUT : List of element which have to be removed from element's list
	///////////////////////////////////////////////////////////////////////////////////////
	// Be careful : we can animate an element which is declared as invisible (by VISIBLE boolean)
	///////////////////////////////////////////////////////////////////////////////////////
	public List<SpriteEntity> animate()
	{
	    //Perso pnj;
	    boolean colli;
	
	    List<SpriteEntity> deads=new ArrayList<SpriteEntity>();

	    //Si ce sprite est valide, est-il un sprite fixe ?
		if (this.IsNotFixe())
		{
			// On a trouvé un sprite valide non fixe
			// On calcule sa nouvelle position absolue
			colli=physicMoveWithCollision();
	
			if (nSpr>=44 && nSpr<=47) { // Sprite d'animation
				// Morceaux de pierres
				z=z-vz;                         // On revient en arrière
				vz=vz-az;
				az=az-1;
				if (az==0) {
					deads.add(this);
				}
			} else if (isSolid()) {// Tous les sprites n'entrent pas en collision
				// On teste la collision avec le décor
				if (nSpr==42) {
					//Collision avec Zildo}
					z=z-vz;
					/*colli=collide(round(x+vx),round(y+vy-z),round(vz));
					with tab_colli[n_colliseur] do begin
					cx=round(x)-camerax;cy=round(y)-round(z)-cameray;
					cr=8;
					n_colliseur++;
					if (colli) {
						//spawnsprite_generic(SPR_ECLATEPIERRE,round(x),round(y),0);
					}
					*/
				} else {
				  // Collision avec les ennemis (uniquement dans le cas où l'objet est en mouvement)
					if (vx != 0 || vy != 0 || vz !=0) {
						EngineZildo.collideManagement.addCollision(true, 
								(int)x-3, 
								(int)y-getSprModel().getTaille_y()/2-(int)z, 6, Angle.NORD, (Perso) linkedPerso);
					}
				}
			}
			// Débordement}
			if (x<-4 || y<-4 || x>64*16 || y>64*16) {
				return Arrays.asList((SpriteEntity) this);
			}
	
			if (elementsMobiles.contains(nSpr)) {
				z=ancZ; //z-vz;
				vz=vz-az;
				if (az!=0) {
					/*etat=false;              // Pour tromper collide, on supprime le sprite
					colli=collide(round(x),round(y),0);
					etat=true;               // Et on le remet
					*/
					if (colli || az==32) {
						vx=0;vy=0;az=32;vz=0;
					} else {
						az=az+1;
					}
				}
			} else if (z<4 || colli) {
			 // Le sprite doit 'mourir'
				deads.add(this);
			} else if (z>28 && nSpr==6) {
				nSpr=5;         // Fumée de cheminée
			} else if (z>48 && nSpr==5) {
				z=16.0f;x=(int)(x / 16)*16 +16; // On remet la fumée à sa place
				vx=0.2f;vz=0.0f;
				nSpr=6;
			}
		}
	
		return deads;	// NULL ==> Element is still alive
	}


	public float getX() {
		return x;
	}


	public void setX(float x) {
		this.x = x;
	}


	public float getY() {
		return y;
	}


	public void setY(float y) {
		this.y = y;
	}


	public float getZ() {
		return z;
	}


	public void setZ(float z) {
		this.z = z;
	}


	public float getAx() {
		return ax;
	}


	public void setAx(float ax) {
		this.ax = ax;
	}


	public float getAy() {
		return ay;
	}


	public void setAy(float ay) {
		this.ay = ay;
	}


	public float getAz() {
		return az;
	}


	public void setAz(float az) {
		this.az = az;
	}


	public float getVx() {
		return vx;
	}


	public void setVx(float vx) {
		this.vx = vx;
	}


	public float getVy() {
		return vy;
	}


	public void setVy(float vy) {
		this.vy = vy;
	}


	public float getVz() {
		return vz;
	}


	public void setVz(float vz) {
		this.vz = vz;
	}


	public char getSpe() {
		return spe;
	}


	public void setSpe(char spe) {
		this.spe = spe;
	}


	public int getAddSpr() {
		return addSpr;
	}


	public void setAddSpr(int addSpr) {
		this.addSpr = addSpr;
	}


	public SpriteEntity getLinkedPerso() {
		return linkedPerso;
	}


	public void setLinkedPerso(SpriteEntity linkedPerso) {
		this.linkedPerso = linkedPerso;
	}
	
	/**
	 * Appelée lorsque l'objet tombe au sol.
	 */
	public void fall() {
		if (nSpr==1) {
			// Le buisson s'effeuille
			EngineZildo.spriteManagement.spawnSpriteGeneric(Element.SPR_BUISSON,(int) x,(int) y,0, null);
			EngineZildo.broadcastSound("CasseBuisson", this);
		} else if (nSpr==12 || nSpr==42) {
			EngineZildo.spriteManagement.spawnSpriteGeneric(Element.SPR_ECLATEPIERRE,(int) x,(int) y,0, null);
			EngineZildo.broadcastSound("CassePierre", this);
		} else if (nSpr==32) {
			// La poule reprend vie dans le tableau de perso
			/*
			pnj.x=x;        pnj.y=y;pnj.z=0;
			with pnj do begin
			zone_deplacement[0]=round(x-16*5);
			zone_deplacement[1]=round(y-16*5);
			zone_deplacement[2]=round(x+16*5);
			zone_deplacement[3]=round(y+16*5);
			quel_spr=35;quel_deplacement=SCRIPT_POULE;
			info=0;     nom='poule';
			px=0;       py=0;       alerte=false;pv=1;
			attente=0;
			end;
			spawn_perso(pnj);
			*/
		}
	}

	/**
	 * Returns TRUE if zildo can push this element.
	 * @return boolean
	 */
	public boolean isPushable() {
		// We shouldn't look for zildo's position normally.
		PersoZildo zildo=EngineZildo.persoManagement.getZildo();
		return ((nSpr==69 || nSpr==70 ||
				// Cas spécial : 28=tonneau sur la map 'polakyg' : passage secret !}
				(nSpr==28 && EngineZildo.mapManagement.getCurrentMap().getName().equals("polakyg") && zildo.getY()>=7*16))
				&& az==0.0f);
	}

	public float getFx() {
		return fx;
	}

	/**
	 * Move on a given direction, when pushed by a character.
	 * @param ang
	 */
	public void moveOnPush(Angle ang) {
		switch (ang) {
			case NORD:vy=-0.5f;break;
			case EST:vx=0.5f;break;
			case SUD:vy=0.5f;break;
			case OUEST:vx=-0.5f;break;
		}
		az=1.0f;	
	}

	public void setFx(float fx) {
		this.fx = fx;
	}


	public float getFy() {
		return fy;
	}


	public void setFy(float fy) {
		this.fy = fy;
	}


	public float getFz() {
		return fz;
	}


	public void setFz(float fz) {
		this.fz = fz;
	}
}