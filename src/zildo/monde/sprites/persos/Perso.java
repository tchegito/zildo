package zildo.monde.sprites.persos;

import java.util.ArrayList;
import java.util.List;

import zildo.fwk.gfx.PixelShaders.EngineFX;
import zildo.monde.map.Angle;
import zildo.monde.map.Point;
import zildo.monde.map.Pointf;
import zildo.monde.map.Zone;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.utils.MouvementPerso;
import zildo.monde.sprites.utils.MouvementZildo;
import zildo.server.EngineZildo;
import zildo.server.MapManagement;

public abstract class Perso extends Element {
	
	public enum PersoInfo {
		NEUTRAL, ENEMY, ZILDO, SHOOTABLE_NEUTRAL;
	}

	protected Zone zone_deplacement;
    private int compte_dialogue;
    private String nom;
    protected PersoInfo info;					// 0=Neutre  1=Ennemi  2=Zildo
    protected boolean alerte;				// True=Zildo est reperé (Pieds dans l'eau si c'est Zildo)
    protected MouvementPerso quel_deplacement;      // Script
    protected PersoDescription quel_spr;				
    protected int attente;				// =0 => pas d'attente
    protected int dx,dy,dz;				// Destination
    protected float px,py;				// Quand le perso est propulsé (touché)
    protected int pos_seqsprite;
    private Element en_bras;			// Si c'est Zildo, l'objet qu'il porte.Note : 10=poule
    protected MouvementZildo mouvement;			// Situation du perso:debout,couché,attaque...
    protected int cptMouvement;	// Un compteur pour les mouvements des PNJ
    private int coming_map;		// 1 si Zildo entre sur une map,sinon 255
    private int pv,maxpv;			// Points de vie du perso

    private int money;

    private boolean wounded;
    private Perso dialoguingWith;
    
	// Liste des sprites complémentaires du perso (ex:bouclier+casque pour zildo)
	List<Element>	persoSprites;

	public Zone getZone_deplacement() {
		return zone_deplacement;
	}

	public void setZone_deplacement(Zone zone_deplacement) {
		this.zone_deplacement = zone_deplacement;
	}

	public int getCompte_dialogue() {
		return compte_dialogue;
	}

	public void setCompte_dialogue(int compte_dialogue) {
		this.compte_dialogue = compte_dialogue;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public PersoInfo getInfo() {
		return info;
	}

	public void setInfo(PersoInfo info) {
		this.info = info;
	}

	public boolean isAlerte() {
		return alerte;
	}

	public void setAlerte(boolean alerte) {
		this.alerte = alerte;
	}

	public MouvementPerso getQuel_deplacement() {
		return quel_deplacement;
	}

	public void setQuel_deplacement(MouvementPerso quel_deplacement) {
		this.quel_deplacement = quel_deplacement;
	}

	public PersoDescription getQuel_spr() {
		return quel_spr;
	}

	public void setQuel_spr(PersoDescription quel_spr) {
		this.quel_spr = quel_spr;
	}
	
	public int getAttente() {
		return attente;
	}

	public void setAttente(int attente) {
		this.attente = attente;
	}

	public int getDx() {
		return dx;
	}

	public void setDx(int dx) {
		this.dx = dx;
	}

	public int getDy() {
		return dy;
	}

	public void setDy(int dy) {
		this.dy = dy;
	}

	public int getDz() {
		return dz;
	}

	public void setDz(int dz) {
		this.dz = dz;
	}

	public float getPx() {
		return px;
	}

	public void setPx(float px) {
		this.px = px;
	}

	public float getPy() {
		return py;
	}

	public void setPy(float py) {
		this.py = py;
	}

	public int getPos_seqsprite() {
		return pos_seqsprite;
	}

	public void setPos_seqsprite(int pos_seqsprite) {
		this.pos_seqsprite = pos_seqsprite;
	}

	public Element getEn_bras() {
		return en_bras;
	}

	public void setEn_bras(Element en_bras) {
		this.en_bras = en_bras;
	}

	public MouvementZildo getMouvement() {
		return mouvement;
	}

	public void setMouvement(MouvementZildo mouvement) {
		this.mouvement = mouvement;
	}

	public int getComing_map() {
		return coming_map;
	}

	public void setComing_map(int coming_map) {
		this.coming_map = coming_map;
	}

	public int getPv() {
		return pv;
	}

	public void setPv(int pv) {
		this.pv = pv;
	}

	public int getMaxpv() {
		return maxpv;
	}

	public void setMaxpv(int maxpv) {
		this.maxpv = maxpv;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public boolean isWounded() {
		return wounded;
	}

	public void setWounded(boolean wounded) {
		this.wounded = wounded;
	}

	public List<Element> getPersoSprites() {
		return persoSprites;
	}

	public void setPersoSprites(List<Element> persoSprites) {
		this.persoSprites = persoSprites;
	}

	public void addPersoSprites(Element elem) {
		this.persoSprites.add(elem);
		elem.setLinkedPerso(this);
	}
	
	public Perso() {
		super();
		entityType=SpriteEntity.ENTITYTYPE_PERSO;
	
		money=(int)Math.random();
	
		wounded=false;
		alerte=false;
		px=0.0f;
		py=0.0f;
		compte_dialogue=0;
		attente=0;
		
		quel_deplacement=MouvementPerso.SCRIPT_IMMOBILE;
		
		persoSprites=new ArrayList<Element>();
	
		logger.info("Creating Perso");
	
	}
	
	public void finalize() {
		logger.info("Deleting Perso");
		// Delete linked elements
		if (persoSprites != null && persoSprites.size() > 0) {
			for (Element e : persoSprites) {
				EngineZildo.spriteManagement.deleteSprite(e);
			}
			persoSprites.clear();
		}
		logger.info(" ... Ok");
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// hide
	///////////////////////////////////////////////////////////////////////////////////////
	// Sets perso unvisible, and every linked sprites too.
	///////////////////////////////////////////////////////////////////////////////////////
	public void hide() {
		this.visible=false;
		if (this.persoSprites.size() > 0) {
			for (Element e : persoSprites) {
				e.setVisible(false);
			}
		}
	}
	
	public void setSpecialEffect(EngineFX specialEffect) {
		super.setSpecialEffect(specialEffect);
		if (this.persoSprites.size() > 0) {
			for (Element e : persoSprites) {
				e.setSpecialEffect(specialEffect);
			}
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// determineDestination
	///////////////////////////////////////////////////////////////////////////////////////
	// Set a location (dx,dy) in the current perso, inside the movement area (zone_deplacement)
	// This is where we assign a new position, horizontally and/or vertically depending on the
	// character's script.
	///////////////////////////////////////////////////////////////////////////////////////
	void determineDestination() {
		int j=13+3;
		while (true) {
			this.dx=(int) this.getX();
			this.dy=(int) this.getY();
	
			// On déplace le perso soit horizontalement, soit verticalement,
			// ou les 2 si c'est une poule. Car les poules ont la bougeotte.
			if (j%2==0 || MouvementPerso.persoDiagonales.contains(quel_deplacement) )
				this.dx+= (16*Math.random()*j) - 8*j;
	
			if (j%2==1 || MouvementPerso.persoDiagonales.contains(quel_deplacement) )
				this.dy+= (16*Math.random()*j) - 8*j;
	
			j--; // On diminue le rayon jusqu'à être dans la zone
	
			if ((this.dx>=zone_deplacement.getX1() && this.dy>=zone_deplacement.getY1() &&
				 this.dx<=zone_deplacement.getX2() && this.dy<=zone_deplacement.getY2()) ||
				(j==-1) )
				break;
		}
	
	    if (j==-1) {  // En cas de pépin
			this.dx=zone_deplacement.getX1();
			this.dy=zone_deplacement.getY1();
	    }
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// getAttackTarget
	///////////////////////////////////////////////////////////////////////////////////////
	// -return the tile's coordinates immediately near the character
	///////////////////////////////////////////////////////////////////////////////////////
	public Point getAttackTarget() {
		
		final int add_anglex[]={0,1,0,-1};
		final int add_angley[]={-1,0,1,0};
	
		Point p=new Point();
		p.setX(((int)getX()+5*add_anglex[angle.value]) / 16);
		p.setY(((int)getY()+5*add_angley[angle.value]) / 16);
	
		return p;
	}
	
    public void placeAt(Point p_point) {
        placeAt(p_point.getX(), p_point.getY());
    }

    public void placeAt(int p_posX, int p_posY) {
        int diffX = (int) x - p_posX;
        int diffY = (int) y - p_posY;
        x = p_posX;
        y = p_posY;
        for (Element elem : persoSprites) {
            elem.x += diffX;
            elem.y += diffY;
        }
    }
    
	public String toString() {
		StringBuffer sb=new StringBuffer();
		sb.append("Perso="+nom+"\nx="+x+"\ny="+y+"\ninfo="+info+"\nmvt="+mouvement);
		return sb.toString();
	}

	/**
	 * Push the character away, with a hit point located at the given coordinates.
	 * @param p_cx
	 * @param p_cy
	 */
	protected void project(float p_cx, float p_cy, int p_speed) {
		// Project monster away from the enemy
		float diffx=getX()-p_cx;
		float diffy=getY()-p_cy;
		double norme=Math.sqrt( (diffx*diffx) + (diffy*diffy) );
	    if (norme==0.0f) {
			norme=1.0f;           //Pour éviter le 'divide by zero'
		}
		// Et on l'envoie !
		this.setPx((float) (p_speed*(diffx/norme)));
		this.setPy((float) (p_speed*(diffy/norme)));		
	}
	
	/**
	 * Try to move character at the given location, and returns corrected one.<p/>
	 * The correction is based on two methods:
	 * -transform diagonal movement into lateral
	 * -transform lateral movement into diagonal<p/>
	 * If no one succeeds, returns the original location.
	 * @param p_xx
	 * @param p_yy
	 * @return corrected location, or same one if character can't move at all.
	 */
    public Point tryMove(int p_xx, int p_yy) {
        MapManagement mapManagement = EngineZildo.mapManagement;
        int xx = p_xx;
        int yy = p_yy;

        if (mapManagement.collide(xx, yy, this)) {
            int diffx = xx - (int) x;
            int diffy = yy - (int) y;
            if (diffx != 0 && diffy != 0) {
                // Diagonal move impossible => try lateral move
                if (!mapManagement.collide(xx, (int) y, this))
                    yy = (int) y;
                else if (!mapManagement.collide((int) x, yy, this))
                    xx = (int) x;
            } else {

                // Lateral move impossible => try diagonal move
                int speed;
                if (diffx == 0) {
                    speed = Math.abs(diffy);
                    if (!mapManagement.collide(xx + speed, yy, this))
                        xx += speed;
                    else if (!mapManagement.collide(xx - speed, yy, this))
                        xx -= speed;
                } else if (diffy == 0) {
                    speed = Math.abs(diffx);
                    if (!mapManagement.collide(xx, yy + speed, this))
                        yy += speed;
                    else if (!mapManagement.collide(xx, yy - speed, this))
                        yy -= speed;
                }
            }
            if (mapManagement.collide(xx, yy, this)) {
            	xx=(int) x;
            	yy=(int) y;
            }
        }
        return new Point(xx, yy);
    }
    
	public abstract void initPersoFX();

    public abstract boolean beingWounded(float cx, float cy, Perso p_shooter, int p_damage);
    
    public void parry(float cx, float cy, Perso p_shooter) {}
    	
	public abstract void stopBeingWounded();

	public abstract void attack();
	
    public void die(boolean p_link, Perso p_shooter) {
        // Death !
        EngineZildo.spriteManagement.spawnSpriteGeneric(Element.SPR_MORT, (int) x, (int) y, 0, p_link ? this : null);
    }
    
	public abstract void finaliseComportement(int compteur_animation);
	
	// Default function : nothing
	public void animate(int compteur) {
		
	}
	
	// Default : nothing to do (only Zildo can take up objects for now)
	public void takeSomething(int objX, int objY, int obj, Element object) {
		
	}
	
	public boolean linkedSpritesContains(SpriteEntity entity) {
		return persoSprites.contains(entity) || en_bras==entity;
	}

	public int getCptMouvement() {
		return cptMouvement;
	}

	public void setCptMouvement(int cptMouvement) {
		this.cptMouvement = cptMouvement;
	}

	public Perso getDialoguingWith() {
		return dialoguingWith;
	}

	public void setDialoguingWith(Perso p_dialoguingWith) {
		this.dialoguingWith = p_dialoguingWith;
	}
	
	public void setVisible(boolean p_visible) {
		super.setVisible(p_visible);
		for (SpriteEntity entity : persoSprites) {
			entity.setVisible(p_visible);
		}
	}
	
	/**
	 * Shouldn't modify heros location !
	 * @param p_speed
	 * @return int
	 */
	public Pointf reachDestination(float p_speed) {
		int immo=0;
		Pointf pos=new Pointf(x, y);
		if (x < dx) {
			pos.x+= p_speed;
			if (pos.x > dx) {
				pos.x=dx;
			}
			angle = Angle.EST;
		} else if (x > dx) {
			pos.x-= p_speed;
			if (pos.x < dx) {
				pos.x=dx;
			}
			angle=Angle.OUEST;
		} else {
			immo++;
		}
		if (y < dy) {
			pos.y+= p_speed;
			if (pos.y > dy) {
				pos.y=dy;
			}
			angle=Angle.SUD;
		} else if (y > dy) {
			pos.y-= p_speed;
			if (pos.y < dy) {
				pos.y=dy;
			}
			angle=Angle.NORD;
		} else {
			immo++;
		}
		
		return pos;
	}
}