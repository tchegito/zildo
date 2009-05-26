package zildo.monde.serveur;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import zildo.fwk.IntSet;
import zildo.fwk.bank.SpriteBank;
import zildo.fwk.engine.EngineZildo;
import zildo.fwk.gfx.PixelShaders;
import zildo.fwk.gfx.engine.SpriteEngine;
import zildo.monde.Collision;
import zildo.monde.Hasard;
import zildo.monde.SpriteModel;
import zildo.monde.decors.Element;
import zildo.monde.decors.ElementAnimMort;
import zildo.monde.decors.ElementGoodies;
import zildo.monde.decors.SpriteEntity;
import zildo.monde.persos.Perso;
import zildo.monde.persos.PersoZildo;
import zildo.prefs.Constantes;



public class SpriteManagement {

	protected Logger logger=Logger.getLogger("SpriteManagement");

    // Index des sprites fixes qui bloquent les persos (rambarde,tonneau)
	static IntSet blockable_sprite=new IntSet(25,26,27,28,68,69,70);

      // Index des sprites que Zildo peut prendre (argent,clé...)
    static IntSet goodies_sprite=new IntSet(10,40,48,51,54);

	private int n_bankspr;
	private List<SpriteBank> banque_spr;
	//std::list<Element> tab_elements; //[MAX_ELEMENTS];
	private List<SpriteEntity> spriteEntities;
	private Collision[] tab_colli=new Collision[30];	//Les monstres
	private Collision[] tab_colliz=new Collision[30];	//Zones d'aggression de Zildo
	private int n_Colliseur;		            //Le nb d'élément dans tab_colli
	private int n_Colliseurz;					//Le nb d'élément dans tab_colliz
	private char sauv_2eligne;                   //Pour le texter
	private char sauv_3eligne;                   //Pour le texter
	private boolean perso_shooting;
	private char[] tab_palette=new char[255];
	private int camerax,cameray;
	
	static public String[] sprBankName={"zildo.spr", "elem.spr", "pnj.spr", "font.spr", "pnj2.spr"};
	// bankOrder works like this { (BanqueN,i) , (BanqueM,j) , (BanqueP,k) ... }

//

/*float rnd()
{
	return 1; //(float)rand() / RAND_MAX;
}
*/
	public SpriteManagement()
	{
	
		// Load sprite banks
		banque_spr=new ArrayList<SpriteBank>();
		n_bankspr=0;
		for (int b=0;b<sprBankName.length;b++) {
			charge_sprites(sprBankName[b]);
		}
	
		// Create another bank for thin dialog's font
		buildFontBank();
	
		// Initialize entities list
		spriteEntities=new ArrayList<SpriteEntity>();
	
		camerax=0;
		cameray=0;

	}
	
	public void finalize()
	{
		// When whe got here, everything is deleted in 'spriteEntities' but Zildo.
		// So we just have to delete him, and turn is play !
		//PersoZildo zildo=EngineZildo.persoManagement.get_zildo();
		//delete zildo;
	
		// Delete the sort objects
		//delete lastInBank;
		//delete quadOrder;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// charge_sprites
	///////////////////////////////////////////////////////////////////////////////////////
	public void charge_sprites(String filename)
	{
		SpriteBank sprBank=new SpriteBank();
	
		sprBank.charge_sprites(filename);
	
		banque_spr.add(sprBank);
	
		// Relase memory allocated for tile graphics, because it's in directX memory now. 
		//delete (sprBank.sprites_buf);
		
		// Increase number of loaded banks
		n_bankspr++;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// buildFontBank
	///////////////////////////////////////////////////////////////////////////////////////
	public void buildFontBank()
	{
	
		/*
		HFONT dialogFont= CreateFont( 10, 0, 0, 0, 0,0,	// les derniers : bold, italic
	                                 FALSE, FALSE, DEFAULT_CHARSET, OUT_DEFAULT_PRECIS,
	                                 CLIP_DEFAULT_PRECIS, DRAFT_QUALITY,
	                                 DEFAULT_PITCH, null); //"Times new roman" );
	
	*/
		SpriteBank sprBank=new SpriteBank();
		sprBank.setName("FONTES2.spr");

		//EngineZildo.spriteEngine.createTextureFromFontStyle(sprBank);
	
		banque_spr.add(sprBank);
	
		// Increase number of loaded banks
		n_bankspr++;
	
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// getSpriteBank
	///////////////////////////////////////////////////////////////////////////////////////
	public SpriteBank getSpriteBank(int nBank)
	{
		return banque_spr.get(nBank);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// spawnSprite
	///////////////////////////////////////////////////////////////////////////////////////
	// IN:nBank, nSpr, x, y
	///////////////////////////////////////////////////////////////////////////////////////
	// Spawn a sprite with minimal requirements
	// -build an entity with given parameters
	// -add it to the sprite engine
	public void spawnSprite(int nBank, int nSpr, int x, int y)
	{
	
		SpriteModel spr=getSpriteBank(nBank).get_sprite(nSpr);

		if (nSpr == 69 || nSpr == 70 || nSpr == 28) {
			// Particular sprite (Block that Zildo can move, chest...)
			spawnElement(nBank, nSpr, x,y+spr.getTaille_y() / 2 - 3);
			return;
		}

		// SpriteEntity informations
		SpriteEntity entity=new SpriteEntity(x, y);

		entity.setNSpr(nSpr);
		entity.setNBank(nBank);
		entity.setMoved(false);
	
		entity.setScrX(x - (spr.getTaille_x() >> 1));
		entity.setScrY(y - (spr.getTaille_y() >> 1));
	
		spawnSprite(entity);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// spawnFont
	///////////////////////////////////////////////////////////////////////////////////////
	// IN:nBank, nSpr, x, y
	///////////////////////////////////////////////////////////////////////////////////////
	// Spawn a font character, same as spawnSprite, instead of :
	// -type is ENTITYTYPE_FONT
	// -no alignment
	public SpriteEntity spawnFont(int nBank, int nSpr, int x, int y, boolean visible)
	{
	
		// SpriteEntity informations
		SpriteEntity entity=new SpriteEntity(x,y);
		entity.setScrX(x);
		entity.setScrY(y);
		entity.setNSpr(nSpr);
		entity.setNBank(nBank);
		entity.setMoved(false);
		entity.setForeground(true);	// Fonts are in front of the scene
	
		entity.setEntityType(SpriteEntity.ENTITYTYPE_FONT);
	
		entity.setVisible(visible);
	
		entity.setSpecialEffect(PixelShaders.ENGINEFX_FONT_HIGHLIGHT);
	
		spawnSprite(entity);
	
		return entity;
	}
	///////////////////////////////////////////////////////////////////////////////////////
	// spawnElement
	///////////////////////////////////////////////////////////////////////////////////////
	// IN:nBank, nSpr, x, y
	///////////////////////////////////////////////////////////////////////////////////////
	// Spawn an element with minimal requirements
	// -build an element with given parameters
	// -add it to the sprite engine
	public void spawnElement(int nBank, int nSpr, int x, int y)
	{
	
		// SpriteEntity informations
		Element element=new Element();
		element.setX((float) x);
		element.setY((float) y);
		element.setNSpr(nSpr);
		element.setNBank(nBank);
		element.setMoved(false);
	
		spawnSprite(element);
	}
	///////////////////////////////////////////////////////////////////////////////////////
	// spawnSprite
	///////////////////////////////////////////////////////////////////////////////////////
	// IN:SpriteEntity object
	///////////////////////////////////////////////////////////////////////////////////////
	// Spawn a given SpriteEntity
	// -get the right Sprite object
	// -add resulted entity to the sprite engine
	public void spawnSprite(SpriteEntity entity)
	{
		int nBank=entity.getNBank();
		int nSpr=entity.getNSpr();
	
		SpriteModel spr=getSpriteBank(nBank).get_sprite(nSpr);
		SpriteEngine spriteEngine=EngineZildo.spriteEngine;
	
		entity.setSprModel(spr);
	
		if (entity.isVisible()) { // TODO:test this : && fillingMeshes) {
			// If the entity came here unvisible, we don't add it now to avoid flickering
			//spriteEngine.addSprite(entity);
		}
	
		spriteEntities.add(entity);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// spawnPerso
	///////////////////////////////////////////////////////////////////////////////////////
	// IN:Perso object
	///////////////////////////////////////////////////////////////////////////////////////
	public void spawnPerso(Perso perso)
	{
		SpriteEntity entity=(SpriteEntity)perso;
		entity.setScrX ( (int) perso.x);
		entity.setScrY ( (int) perso.y);
		entity.setMoved(false);
	
		spawnSprite(entity);
	
		// Spawn connected sprites
		if (perso.getPersoSprites().size() != 0) {
			for (Element element : perso.getPersoSprites()) {
				spawnSprite(element);
			}
		}
	
		EngineZildo.persoManagement.addPerso(perso);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// spawnSpriteGeneric
	///////////////////////////////////////////////////////////////////////////////////////
	// IN:sprite type, coordinates and :
	//     misc      :money value (just for SPR_DIAMANT)
	//     miscPerso :pointer on perso dying (just for SPR_MORT)
	///////////////////////////////////////////////////////////////////////////////////////
	public void spawnSpriteGeneric(int typeSprite, int x, int y, int misc, Perso miscPerso)
	{
		Element element=null;
		Element element2=null;
		SpriteModel spr = null;
		int j;
	
		switch (typeSprite)
		{
		case Element.SPR_FUMEE:
			spr=getSpriteBank(SpriteBank.BANK_ELEMENTS).get_sprite(6);
			element=new Element();
			element.setX(50.0f+16.0f);
			element.setY(50.0f+28.0f);
			element.setZ(16.0f);
			element.setVx(0.3f); // + rnd()*0.05f);
			element.setVy(0.0f);
			element.setVz(0.0f);
			element.setAx(-0.01f);
			element.setAy(0.0f);
			element.setAz(0.01f); // + rnd()*0.005f);
	
			element.setNSpr(6);
			element.setSprModel(spr);
	
			element.setScrX ( (int) element.x);
			element.setScrY ( (int) element.y);
	
			spawnSprite((SpriteEntity)element);
			break;
	
		case Element.SPR_BUISSON:
			for (j=0;j<8;j++) {
				element=new Element();
				element.setX((float) (x+Math.random()*10-5));
				element.setY((float) (y+Math.random()*6-2));
				element.setZ((float) (7+Math.random()*10));
				element.setVx(0.2f*(j-1));
				element.setVz((float) (-0.5f+Math.random()*3*0.1f));
				element.setAx(-0.05f*element.getVx());
				element.setNSpr(3+(j % 2));
				spawnSprite(element);
				// Peut-être qu'un diamant va apparaitre !
			}
			if (Hasard.lanceDes(Constantes.hazardBushes_Diamant)) {
				spawnSpriteGeneric(Element.SPR_DIAMANT,x,y+2,0, null);
			} else if (Hasard.lanceDes(Constantes.hazardBushes_Heart)) {
				spawnSpriteGeneric(Element.SPR_COEUR,x+3,y+2,0, null);
			}
			break;
	
		case Element.SPR_DIAMANT:
			// Diamant
			element=new ElementGoodies();
			element.setX((float) x);
			element.setY((float) y);
			element.setZ(4.0f);
			element.setVz(1.5f);
			element.setAz(-0.1f);
			element.setNSpr(48+misc*3);
			// Ombre
			element2=new Element();
			element2.setX((float) x);
			element2.setY((float) y-2);
			element2.setNSpr(60);
			spawnSprite(element2);
			element.setLinkedPerso(element2);
			spawnSprite(element);
			break;
	
		case Element.SPR_COEUR:
			element=new ElementGoodies();
			element.setX((float) x-1);
			element.setY((float) y);
			element.setZ(11.0f);
			element.setVx(0.15f);
			element.setVz(-0.04f);
			element.setAx(-0.01f);
			element.setNSpr(40);
			spawnSprite(element);
			break;
	
		case Element.SPR_MORT:
			element=new ElementAnimMort(miscPerso);
			element.setX((float) x);
			element.setY((float) y);
			element.setZ(8.0f);
			spawnSprite(element);
		   break;
		}
	
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// deleteSprite
	///////////////////////////////////////////////////////////////////////////////////////
	// IN:entity to destroy
	///////////////////////////////////////////////////////////////////////////////////////
	// -delete given sprite and linked entity
	///////////////////////////////////////////////////////////////////////////////////////
	public void deleteSprite(SpriteEntity entity)
	{
		if (entity != null) {
			entity.fall();
			
			spriteEntities.remove(entity);
			if (entity.getEntityType() == SpriteEntity.ENTITYTYPE_ELEMENT) {
				Element element=(Element)entity;
				SpriteEntity linkedEntity=element.getLinkedPerso();
				// On regarde si cet élément est lié à un autre élément
				if (linkedEntity != null && linkedEntity.getEntityType() == SpriteEntity.ENTITYTYPE_ELEMENT) {
					// Oui c'est le cas donc on supprime aussi l'autre élément
					deleteSprite(element.getLinkedPerso());
				}
				element.finalize();
			} else if (entity.getEntityType() == SpriteEntity.ENTITYTYPE_PERSO) {
				Perso perso=(Perso)entity;
				perso.finalize();
			} else {
				//entity.finalize();
			}
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// updateSprites
	///////////////////////////////////////////////////////////////////////////////////////
	// IN:camerax, cameray
	///////////////////////////////////////////////////////////////////////////////////////
	// Do sprite's stuff
	// -move camera
	// -animate sprites & persos
	// -insert sprites into sort array
	public void updateSprites()
	{
		// Get useful pointers
		MapManagement mapManagement=EngineZildo.mapManagement;
		
		// Do perso animations
		// Mandatory to do that first, because one perso can be connected to other sprites
		for (SpriteEntity entity : spriteEntities) {
			if (entity.getEntityType() == SpriteEntity.ENTITYTYPE_PERSO) {
				// Animate persos
				Perso perso=(Perso)entity;
				perso.animate(mapManagement.getCompteur_animation());
				// Get sprite model
				SpriteModel spr=getSpriteBank(entity.getNBank()).get_sprite(perso.getNSpr());
				perso.setSprModel(spr);
				perso.manageCollision();
			}
		}
		
		List<SpriteEntity> toDelete=new ArrayList<SpriteEntity>();
		for (Iterator<SpriteEntity> it=spriteEntities.iterator();it.hasNext();) {
			SpriteEntity entity=it.next();
			if (toDelete.contains(entity)) {
				continue;	// It's a dead one
			}
			Element element = null;
			// Calcul physique du sprite
			if (entity.getEntityType()==SpriteEntity.ENTITYTYPE_ELEMENT) {
				// X, vX, aX, ...
				element = (Element)entity;
				List<SpriteEntity> deads=element.animate();
				if (deads!=null && !deads.isEmpty()) {
					// L'élément est arrivé au terme de son existence : on le supprime de la liste
					if (element.getLinkedPerso() != null && SpriteEntity.ENTITYTYPE_ELEMENT == element.getLinkedPerso().getEntityType()) {
						toDelete.add(element.getLinkedPerso());
					}
					toDelete.addAll(deads);
				} else {
					if (element.isVisible()) {
						SpriteModel spr=getSpriteBank(entity.getNBank()).get_sprite(entity.getNSpr() + element.getAddSpr());
						entity.setSprModel(spr);
					}
				}
			}
		}
		
		// Remove what need to
		for (SpriteEntity entity : toDelete) {
			deleteSprite(entity);
		}

		//delete quadOrder;


	}
		
	///////////////////////////////////////////////////////////////////////////////////////
	// clearSpritesWithoutZildo
	///////////////////////////////////////////////////////////////////////////////////////
	// -Delete every sprites in the entities list
	// -Clean the sort array
	// -Reinitializes local camera
	///////////////////////////////////////////////////////////////////////////////////////
	public void clearSpritesWithoutZildo()
	{
		// Get Zildo to avoid to remove it
		Perso zildo=EngineZildo.persoManagement.getZildo();
	
		// Destroy entities
		List<SpriteEntity> listToRemove=new ArrayList<SpriteEntity>();

		for(SpriteEntity entity : spriteEntities) {
			if (entity != null) {
				boolean canDelete=true;
				if (entity == zildo) {
					// This IS Zildo ! So we keep him
					canDelete=false;
				} else if (entity.getEntityType() == SpriteEntity.ENTITYTYPE_ELEMENT) {
					Element element=(Element)entity;
					if (element.getLinkedPerso() == zildo) {
						// This is an element related to zildo, so we can't remove it now
						canDelete=false;
					}
				} else if (entity.getEntityType() == SpriteEntity.ENTITYTYPE_PERSO) {
					canDelete=false;
				}
				if (canDelete) {
					listToRemove.add(entity);
					entity=null;
				}
			}
		}

		for (SpriteEntity entity : listToRemove) {
			this.logger.info("Removing entity");
			deleteSprite(entity);
		}
	
		// Here we used to clear sort array
		
		// To avoid sprite being drawn anywhere
		setCamerax(0);
		setCameray(0);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// collideSprite
	///////////////////////////////////////////////////////////////////////////////////////
	// -Return TRUE wether the given sprite collide one BLOCKING element/entity.
	// -Do appropriate stuffs wether character encounters a sprite (bonus, item...)
	///////////////////////////////////////////////////////////////////////////////////////
	public boolean collideSprite(int tx, int ty, Element elem) {
		final int tab_add[]={-1,-1,1,1,-1};
		
		SpriteEntity entityRef=(SpriteEntity)elem;
		boolean found=false;
		int x=0,y=0;
		boolean isBlockable;
		boolean isGoodies;
		boolean isZildo=elem!=null && elem.getEntityType()==SpriteEntity.ENTITYTYPE_PERSO && ((Perso)elem).isZildo();
		Element element;
		List<SpriteEntity> listToRemove=new ArrayList<SpriteEntity>();

		MapManagement mapManagement=EngineZildo.mapManagement;
		
		for (SpriteEntity entity : spriteEntities) {
			element=null;
			if (entity != entityRef && 
				(entity.getEntityType() == SpriteEntity.ENTITYTYPE_ELEMENT ||
				 entity.getEntityType() == SpriteEntity.ENTITYTYPE_ENTITY)) {
				isBlockable=blockable_sprite.contains(entity.getNSpr());
				isGoodies=goodies_sprite.contains(entity.getNSpr());
				SpriteModel sprModel=entity.getSprModel();
				int sx=sprModel.getTaille_x();
				int sy=sprModel.getTaille_y();
				if (isGoodies || isBlockable) {
					boolean canDealWith=false;
					if (entity.getEntityType() == SpriteEntity.ENTITYTYPE_ELEMENT) {
						// The elements
						element=(Element)entity;
						if (element.getLinkedPerso() == null || element.getLinkedPerso() != elem) {
							canDealWith=true;
							x=(int) element.getX() - sx/2;
							y=(int) element.getY() - sy/2;
						}
					} else {
						// The entities
						x=(int) entity.x; //ScrX() + mapManagement.getCamerax();
						y=(int) entity.y; //ScrY() + mapManagement.getCameray();
						canDealWith=true;
					}
					if (canDealWith) {
						// Test collision with element

						for (int j=0;j<4 && !found;j++) {
							int px=tx+4*tab_add[j];
							int py=ty+2*tab_add[j+1];
							if ( px>=x && py>=y && px<=(x+sx) && py<=(y+sy) ) {
								// On signale que Zildo exerce une poussée contre une entité
								if (!isGoodies && isZildo) {
									((PersoZildo)elem).pushSomething(entity);
								}
								// Is it a goodies ?
								if (isGoodies && isZildo) {
									((PersoZildo)elem).pickGoodies(entity.getNSpr());
									listToRemove.add(entity);
								} else {
									found=true;
								}
							}
						}
					}
				}
	
			}
		}
	
		for (SpriteEntity entity : listToRemove) {
			// La méthode suivante va peut-être supprimer un élément lié à celui-ci (exemple:l'ombre)
			deleteSprite(entity);
		}
	
		// No collision
		return found;
	}

	public int getCamerax() {
		return camerax;
	}

	public void setCamerax(int camerax) {
		this.camerax = camerax;
	}

	public int getCameray() {
		return cameray;
	}

	public void setCameray(int cameray) {
		this.cameray = cameray;
	}
	
	public List<SpriteEntity> getSpriteEntities() {
		return spriteEntities;
	}
}