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
import java.util.List;

import zildo.client.sound.BankSound;
import zildo.fwk.gfx.PixelShaders.EngineFX;
import zildo.monde.map.Point;
import zildo.monde.map.Pointf;
import zildo.monde.map.Zone;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.desc.SpriteDescription;
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
    private String effect;	// String containing desired effect ("noir", "jaune", ...)
    protected PersoInfo info;					// 0=Neutre  1=Ennemi  2=Zildo
    protected boolean alerte;				// True=Zildo est reperé (Pieds dans l'eau si c'est Zildo)
    protected MouvementPerso quel_deplacement;      // Script
    protected PersoDescription quel_spr;				
    protected int attente;				// =0 => pas d'attente
    protected PathFinder pathFinder;				// Destination
    protected int nbShock;				// Number of times character hit something going to his target
    protected float px,py;				// Quand le perso est propulsé (touché)
    protected int pos_seqsprite;
    private Element en_bras;			// Si c'est Zildo, l'objet qu'il porte.Note : 10=poule
    protected MouvementZildo mouvement;			// Situation du perso:debout,couché,attaque...
    protected int cptMouvement;	// Un compteur pour les mouvements des PNJ
    private int coming_map;		// 1 si Zildo entre sur une map,sinon 255
    private int pv,maxpv;			// Points de vie du perso
	private boolean ghost=false;	// TRUE=script control him

    private int money;
    protected int countArrow;
    protected int countBomb;
	
	private int count=0;
	protected boolean inWater=false;
	
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
		if (MouvementPerso.SCRIPT_IMMOBILE == quel_deplacement) {
			this.pathFinder.target=null;
		}
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
	
		pathFinder=new PathFinder(this);
		
		logger.info("Creating Perso");
	
	}
	
	@Override
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
	
	@Override
	public void setSpecialEffect(EngineFX specialEffect) {
		super.setSpecialEffect(specialEffect);
		if (this.persoSprites.size() > 0) {
			for (Element e : persoSprites) {
				e.setSpecialEffect(specialEffect);
			}
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
    
	@Override
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
	public void takeSomething(int objX, int objY, SpriteDescription desc, Element object) {
		
	}
	
    
	/**
	 * Zildo walk on a tile, so he reacts (water), or tile change (door).
	 * @param p_sound TRUE=play sound when modifying map.
	 * @return boolean (TRUE=slow down)
	 */
    public boolean walkTile(boolean p_sound) {
        int cx = (int) (x / 16);
        int cy = (int) (y / 16);
        MapManagement mapManagement = EngineZildo.mapManagement;
        int onmap = mapManagement.getCurrentMap().readmap(cx, cy);
        boolean slowDown = false;
        inWater = false;
        BankSound snd = null;
        switch (onmap) {
            case 278:
            	if (pathFinder.open) {
	                mapManagement.getCurrentMap().writemap(cx, cy, 314);
	                mapManagement.getCurrentMap().writemap(cx + 1, cy, 315);
	                snd = BankSound.OuvrePorte;
            	}
                break;
            case 279:
            	if (pathFinder.open) {
	                mapManagement.getCurrentMap().writemap(cx - 1, cy, 314);
	                mapManagement.getCurrentMap().writemap(cx, cy, 315);
	                snd = BankSound.OuvrePorte;
            	}
                break;
            case 200:
                snd = BankSound.ZildoGadou;
            	break;
            case 846:
                // Water
                inWater = true;
                if (count > 15) {
                    snd = BankSound.ZildoPatauge;
                    count = 0;
                } else {
                    count++;
                }
                break;
            case 857:
            case 858:
            case 859:
            case 860:
            case 861:
            case 862:
            case 863:
            case 864:
                slowDown = true;
                break;
        }
        if (snd != null && p_sound && isZildo()) {
            EngineZildo.soundManagement.broadcastSound(snd, this);
        }

        return slowDown;
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
	
	public boolean isGhost() {
		return ghost;
	}

	public void setGhost(boolean ghost) {
		this.ghost = ghost;
	}
	
	@Override
	public void setVisible(boolean p_visible) {
		super.setVisible(p_visible);
		for (SpriteEntity entity : persoSprites) {
			entity.setVisible(p_visible);
		}
	}

	public Point getTarget() {
		return pathFinder.target;
	}

	public void setTarget(Point target) {
		this.pathFinder.target = target;
	}
	
	public boolean hasReachedTarget() {
		return pathFinder.target == null || pathFinder.hasReachedTarget();
	}
	
	public void setSpeed(float p_speed) {
		if (p_speed > 0.0f) {
			pathFinder.speed=p_speed;
		}
	}
	
	public void setForward(boolean p_forward) {
		pathFinder.backward=p_forward;
	}
	
	public void setOpen(boolean p_open) {
		pathFinder.open=p_open;
	}
	
	public Pointf reachDestination(float p_speed) {
		return pathFinder.reachDestination(p_speed);
	}

	public String getEffect() {
		return effect;
	}

	public void setEffect(String p_effect) {
		this.effect = p_effect;
	}

	public int getCountArrow() {
		return countArrow;
	}

	public void setCountArrow(int countArrow) {
		this.countArrow = countArrow;
	}

	public int getCountBomb() {
		return countBomb;
	}

	public void setCountBomb(int countBomb) {
		this.countBomb = countBomb;
	}
}