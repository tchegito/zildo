package zildo.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import zildo.fwk.IntSet;
import zildo.fwk.file.EasyBuffering;
import zildo.monde.Hasard;
import zildo.monde.map.Angle;
import zildo.monde.map.Point;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.SpriteModel;
import zildo.monde.sprites.SpriteStore;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.elements.ElementAnimMort;
import zildo.monde.sprites.elements.ElementBoomerang;
import zildo.monde.sprites.elements.ElementGoodies;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoZildo;
import zildo.monde.sprites.persos.Perso.PersoInfo;
import zildo.prefs.Constantes;
import zildo.server.state.ClientState;



public class SpriteManagement extends SpriteStore {

	protected Logger logger=Logger.getLogger("SpriteManagement");

    // Index des sprites fixes qui bloquent les persos (rambarde,tonneau)
	static IntSet blockable_sprite=new IntSet(25,26,27,28,68,69,70);

    boolean spriteUpdating;
    
	List<SpriteEntity> clientSpecificEntities;
	List<SpriteEntity> spriteEntitiesToAdd; // Used for sprites created during the animation phase
	/** See {@link #updateSprites()} **/
//
	Map<Integer, SpriteEntity> backupEntities;    // To identify entities which have been modified in the current frame
	
	public SpriteManagement()
	{

		super();

        clientSpecificEntities=new ArrayList<SpriteEntity>();
        spriteEntitiesToAdd=new ArrayList<SpriteEntity>();
        backupEntities=new HashMap<Integer, SpriteEntity>();
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
	// spawnElement
	///////////////////////////////////////////////////////////////////////////////////////
	// IN:nBank, nSpr, x, y
	///////////////////////////////////////////////////////////////////////////////////////
	// Spawn an element with minimal requirements
	// -build an element with given parameters
	// -add it to the sprite engine
	public Element spawnElement(int nBank, int nSpr, int x, int y, int z)
	{
	
		// SpriteEntity informations
		Element element=new Element();
		element.setX(x);
		element.setY(y);
		element.setZ(z);
		element.setNSpr(nSpr);
		element.setNBank(nBank);
		element.setMoved(false);
	
		spawnSprite(element);
		
		return element;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// spawnPerso
	///////////////////////////////////////////////////////////////////////////////////////
	// IN:Perso object
	///////////////////////////////////////////////////////////////////////////////////////
	public void spawnPerso(Perso perso)
	{
		SpriteEntity entity=perso;
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
		ElementDescription elemDesc=null;
		int j;
	
		switch (typeSprite)
		{
		case Element.SPR_FUMEE:
			elemDesc=ElementDescription.SMOKE_SMALL;
			element=new Element();
			element.setX(x+16.0f);
			element.setY(y+34.0f);
			element.setZ(16.0f);
			element.setVx(0.2f); // + rnd()*0.05f);
			element.setVy(0.0f);
			element.setVz(0.0f);
			element.setAx(-0.01f);
			element.setAy(0.0f);
			element.setAz(0.01f); // + rnd()*0.005f);
	
			element.setSprModel(elemDesc);
	
			element.setScrX ( (int) element.x);
			element.setScrY ( (int) element.y);
	
			spawnSprite(element);
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
				element.setNSpr(ElementDescription.LEAF1.ordinal()+(j % 2));
				spawnSprite(element);
				// Peut-être qu'un diamant va apparaitre !
			}
			if (Hasard.lanceDes(Constantes.hazardBushes_Arrow)) {
				spawnSpriteGeneric(Element.SPR_ARROW,x,y+5,0, null);
			} else if (Hasard.lanceDes(Constantes.hazardBushes_Diamant)) {
				spawnSpriteGeneric(Element.SPR_DIAMANT,x,y+5,0, null);
			} else if (Hasard.lanceDes(Constantes.hazardBushes_Heart)) {
				spawnSpriteGeneric(Element.SPR_COEUR,x+3,y+5,0, null);
			}
			break;
	
		case Element.SPR_DIAMANT:
		case Element.SPR_ARROW:
			// Diamant
			element=new ElementGoodies();
			element.setX(x);
			element.setY(y);
			element.setZ(4.0f);
			element.setVz(1.5f);
			element.setAz(-0.1f);
			if (typeSprite == Element.SPR_DIAMANT) {
				element.setSprModel(ElementDescription.GREENMONEY1, misc*3);
			} else {
				element.setSprModel(ElementDescription.ARROW_UP);
				element.setY(y-3);
			}
			// Ombre
			element2=new Element();
			element2.setX(x);
			element2.setY(y-2);
			element2.setSprModel(ElementDescription.SHADOW_MINUS);
			spawnSprite(element2);
			element.setLinkedPerso(element2);
			spawnSprite(element);
			break;
	
		case Element.SPR_COEUR:
			element=new ElementGoodies();
			element.setX(x-1);
			element.setY(y);
			element.setZ(11.0f);
			element.setVx(0.15f);
			element.setVz(-0.04f);
			element.setAx(-0.01f);
			element.setSprModel(ElementDescription.HEART_LEFT);
			spawnSprite(element);
			break;
			
		case Element.SPR_MORT:
			element=new ElementAnimMort(miscPerso);
			element.setX(x);
			element.setY(y);
			element.setZ(8.0f);
			spawnSprite(element);
		   break;
		   
		case Element.SPR_ECLATEPIERRE:
			Angle temp=Angle.NORDOUEST;
			for (j=0;j<4;j++) {
				element=new Element();
				Point move=temp.coords;
				element.setX(x);
				element.setY(y);
				element.setZ(4);
				element.setVx(0.5f * move.x + (float) Math.random() * 0.2f);
				element.setVy(0.5f * move.y + (float) Math.random() * 0.2f);
				element.setVz(1f);
				element.setAz(-0.08f);
				element.setFx(0.04f);
				element.setFy(0.04f);
				element.setNSpr(ElementDescription.TINY_ROCK1.ordinal()+(j % 2));
				spawnSprite(element);
				
				temp=Angle.rotate(temp, 1);
			}
			break;
		
		case Element.SPR_FROMCHEST:
			element=new ElementGoodies(miscPerso);
			element.x=x;
			element.y=y;
			element.z=16;
			element.vx=0;
			element.vy=0.0f;
			element.vz=0.2f;
			element.ax=0;
			element.fy=0.005f;
			element.fz=0.02f;
			element.nSpr=misc;
			spawnSprite(element);
		}
	
	}

	///////////////////////////////////////////////////////////////////////////////////////
	// spawnSprite
	///////////////////////////////////////////////////////////////////////////////////////
	// IN:nBank, nSpr, x, y
	///////////////////////////////////////////////////////////////////////////////////////
	// Spawn a sprite with minimal requirements
	// -build an entity with given parameters
	// -add it to the sprite engine
    public SpriteEntity spawnSprite(int nBank, int nSpr, int x, int y, boolean p_foreground) {

        SpriteModel spr = getSpriteBank(nBank).get_sprite(nSpr);

        if (nSpr == 69 || nSpr == 70 || nSpr == 28) {
            // Particular sprite (Block that Zildo can move, chest...)
            return spawnElement(nBank, nSpr, x, y + spr.getTaille_y() / 2 - 3, 0);
        }

        // SpriteEntity informations
        SpriteEntity entity = new SpriteEntity(x, y, true);

        entity.setNSpr(nSpr);
        entity.setNBank(nBank);
        entity.setMoved(false);
        entity.setForeground(p_foreground);
	
		entity.x=x - (spr.getTaille_x() >> 1);
		entity.y=y - (spr.getTaille_y() >> 1);
	
		spawnSprite(entity);
		
		return entity;
	}
	
	protected void addSpriteEntities(SpriteEntity p_entity) {
		if (!spriteUpdating) {
			spriteEntities.add(p_entity);
		} else {
			spriteEntitiesToAdd.add(p_entity);
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// updateSprites
	///////////////////////////////////////////////////////////////////////////////////////
	// IN:camerax, cameray
	///////////////////////////////////////////////////////////////////////////////////////
	// Do sprite's stuff
	// -animate sprites & persos
	public void updateSprites(boolean p_blockMoves)
	{
        spriteUpdating = true;
        spriteEntitiesToAdd.clear();

        // Backup current entities, if backup buffer is empty
        if (backupEntities.size() == 0) {
	        for (SpriteEntity entity : spriteEntities) {
	            SpriteEntity cloned = entity.clone();
	            backupEntities.put(cloned.getId(), cloned);
	        }
        }
		
		// Do perso animations
		// Mandatory to do that first, because one perso can be connected to other sprites
		for (SpriteEntity entity : spriteEntities) {
			if (entity.getEntityType() == SpriteEntity.ENTITYTYPE_PERSO) {
				Perso perso=(Perso)entity;
				if (!p_blockMoves || perso.getInfo() == PersoInfo.ZILDO) {
					// Animate persos
					perso.animate(EngineZildo.compteur_animation % (3*20));
					// Get sprite model
					SpriteModel spr=getSpriteBank(entity.getNBank()).get_sprite(perso.getNSpr());
					perso.setSprModel(spr);
					perso.manageCollision();
					
					if (!perso.isZildo()) {
						// Non-zildo sprite haven't same way to display correctly (bad...)
						perso.setAjustedX(perso.getAjustedX() - (spr.getTaille_x() / 2) );
						perso.setAjustedY(perso.getAjustedY() - (spr.getTaille_y() - 3) );
					}
				}
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
			if (entity.dying) {
				toDelete.add(entity);
			} else if (entity.getEntityType()==SpriteEntity.ENTITYTYPE_ELEMENT) {
				// X, vX, aX, ...
				element = (Element)entity;
				element.animate();
				if (element.dying) {
					SpriteEntity linkedOne=element.getLinkedPerso();
					// L'élément est arrivé au terme de son existence : on le supprime de la liste
					if (linkedOne != null && SpriteEntity.ENTITYTYPE_ELEMENT == linkedOne.getEntityType()) {
						toDelete.add(linkedOne);
					}
					toDelete.add(element);
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
		
		spriteUpdating=false;
		spriteEntities.addAll(spriteEntitiesToAdd);
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

	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// collideSprite
	///////////////////////////////////////////////////////////////////////////////////////
	// -Return TRUE wether the given sprite collide one BLOCKING element/entity.
	// -Do appropriate stuffs wether character encounters a sprite (bonus, item...)
	///////////////////////////////////////////////////////////////////////////////////////
	public boolean collideSprite(int tx, int ty, Element elem) {
		final int tab_add[]={-1,-1,1,1,-1};
		
		SpriteEntity entityRef=elem;
		boolean found=false;
		int x=0,y=0;
		boolean isBlockable;
		boolean isGoodies;
		boolean isZildo=elem!=null && elem.getEntityType()==SpriteEntity.ENTITYTYPE_PERSO && ((Perso)elem).isZildo();
		Element element;
		List<SpriteEntity> listToRemove=new ArrayList<SpriteEntity>();
        
		for (SpriteEntity entity : spriteEntities) {
			element=null;
			if (entity != entityRef && 
				(entity.getEntityType() == SpriteEntity.ENTITYTYPE_ELEMENT ||
				 entity.getEntityType() == SpriteEntity.ENTITYTYPE_ENTITY)) {
				isBlockable=blockable_sprite.contains(entity.getNSpr());
				isGoodies=entity.isGoodies();
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
								if (isGoodies) {
									if (isZildo) {
										((PersoZildo)elem).pickGoodies(entity.getNSpr());
										elem.fall();
										listToRemove.add(entity);
									} else {
										if (elem.getClass().equals(ElementBoomerang.class) ) {
											// Boomerang catches some goodies
											((ElementBoomerang)elem).grab(element);
										}
									}
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
			entity.dying=true;
		}
		
		// No collision
		return found;
	}
	
	/**
	 * Get a SpriteEntity list from a ByteBuffer.
	 * @param p_buffer
	 * @return List<SpriteEntity>
	 */
	public static List<SpriteEntity> deserializeEntities(EasyBuffering p_buffer) {
		List<SpriteEntity> entities=new ArrayList<SpriteEntity>();
		while (!p_buffer.eof()) {
			entities.add(SpriteEntity.deserializeOneEntity(p_buffer));
		}
		return entities;
	}
	
    /**
     * Serialize every entities into one unique ByteBuffer.
     * @param p_entire TRUE=client want all entities / FALSE=just send delta
     * @return buffer
     */
    public EasyBuffering serializeEntities(boolean p_entire) {
        List<SpriteEntity> s = spriteEntities;
        if (!p_entire) {
            s = getModifiedEntities();
        }
        backupEntities.clear();
        return serializeEntities(s, false);
    }
	
	/**
	 * Serialize given entities into one unique ByteBuffer.
	 * @param p_listEntities entities to serialize
	 * @param p_clientSpecific FALSE=serialize only the common entities
	 * @return buffer
	 */
	public EasyBuffering serializeEntities(List<SpriteEntity> p_listEntities, boolean p_clientSpecific) {
		EasyBuffering b=new EasyBuffering();
		for (SpriteEntity entity : p_listEntities) {
			if (!entity.clientSpecific || p_clientSpecific) {
				entity.serializeEntity(b);
			}
		}
		return b;
	}

	/**
	 * Return sprite entities according to the given client.
	 * SpriteEntity which have 'clientSpecific' at TRUE are only transmitted if we have the right client.
	 * @param p_cl
	 * @return List<SpriteEntity>
	 */
	public List<SpriteEntity> getSpriteEntities(ClientState p_cl) {
		// Filter the entities to keep only the common and those from given client
		List<SpriteEntity> entities=new ArrayList<SpriteEntity>();
		List<SpriteEntity> clSprites=new ArrayList<SpriteEntity>();
		if (p_cl != null && p_cl.zildo.guiCircle != null) {
			clSprites=p_cl.zildo.guiCircle.getSprites();
		}
		for (SpriteEntity entity : spriteEntities) {
			if (!entity.clientSpecific || clSprites.contains(entity)) {
				entities.add(entity);
			}
		}
		return entities;
	}
 
	/**
	 * Prepare a list of modified entities on this frame.
	 * @return List<SpriteEntity>
	 */
	private List<SpriteEntity> getModifiedEntities() {
		List<SpriteEntity> returned=new ArrayList<SpriteEntity>();
		for (SpriteEntity entity : spriteEntities) {
			int id=entity.getId();
			SpriteEntity backedUp=backupEntities.get(id);
			if (backedUp == null) {
				// If this entity hasn't been backed up, it's a new one ==> we send it
				returned.add(entity);
			} else {
				// If this entity has been backed up, we must check wether it has changed
				if (!backedUp.isSame(entity)) {
					returned.add(entity);
				}
				backupEntities.remove(id);
			}
		}
		// Send the entity which has been removed this frame
		for (SpriteEntity entity : backupEntities.values()) {
			entity.dying=true;
			entity.clientSpecific=false;	// Send to all clients
			returned.add(entity);
		}
		backupEntities.clear();
		return returned;
	}
}