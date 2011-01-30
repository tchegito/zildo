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

package zildo.monde.sprites;

import zildo.fwk.Identified;
import zildo.fwk.file.EasyBuffering;
import zildo.fwk.file.EasySerializable;
import zildo.fwk.gfx.PixelShaders.EngineFX;
import zildo.monde.map.Point;
import zildo.monde.map.Zone;
import zildo.monde.sprites.desc.SpriteDescription;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoZildo;
import zildo.monde.sprites.utils.Sprite;
import zildo.server.EngineZildo;


////////////////////////////////////////////////////////////////////////////////////////
//
// S p r i t e   E n t i t y
//
////////////////////////////////////////////////////////////////////////////////////////
// Standard class for every entities on the map, different than a map tile itself.
// It could be moving (Element), talking (Perso) with or without interaction with hero.
////////////////////////////////////////////////////////////////////////////////////////
//
// Technically:
//-------------
//* Provide link with SpriteEngine (display textured quad on screen)
//* Manage collision
public class SpriteEntity extends Identified implements Cloneable, EasySerializable
{
	public static final int ENTITYTYPE_ENTITY =0;
	public static final int ENTITYTYPE_ELEMENT =1;
	public static final int ENTITYTYPE_PERSO =2;
	public static final int ENTITYTYPE_FONT =3;

	public static final int REVERSE_HORIZONTAL = 128;
	public static final int REVERSE_VERTICAL = 64;
	
	// Class variable
	public float x,y,z;	// Real position located by center (z is never initialized with entities)
    private int ajustedX,ajustedY;
	private int scrX,scrY;	// Screen position (so with camera adjustment)
	protected SpriteModel sprModel;	// Reference to the sprite being rendered
	public int nSpr;			// Pour les perso devient une interprétation de 'angle' et 'pos_seqsprite'
	public int nBank;
	protected SpriteDescription desc;	// Interpretation of nSpr and nBank
	private boolean moved;			// True=need to synchronize the vertex buffer, False=no move this frame
	private int linkVertices;	// Index on VertexBuffer's position about quad describing this sprite
	public boolean visible;		// TRUE=visible FALSE=invisible
	private boolean foreground;	// Drawn at last in display sequence. So always on foreground
	public boolean dying;		// TRUE=we must remove them
	protected Point center=new Point();	// Defaults : 1) entity : [x/2, y] 2) element : [x/2, y/2]
	
	private EngineFX specialEffect;		// Utilisé pour changer la couleur d'un garde par exemple
	public int reverse;		// Combination of REVERSE_HORIZONTAL/VERTICAL (or 0)
	public boolean clientSpecific;	// TRUE if this entity should not appear on all client's screen
	
	// To identify which type of entity we're dealing with
	protected int entityType;
	
	public int getScrX() {
		return scrX;
	}

	public void setScrX(int scrX) {
		this.scrX = scrX;
	}

	public int getScrY() {
		return scrY;
	}

	public void setScrY(int scrY) {
		this.scrY = scrY;
	}

	public int getAjustedX() {
		return ajustedX;
	}

	public void setAjustedX(int ajustedX) {
		this.ajustedX = ajustedX;
	}

	public int getAjustedY() {
		return ajustedY;
	}

	public void setAjustedY(int ajustedY) {
		this.ajustedY = ajustedY;
	}
	
	public SpriteModel getSprModel() {
		return sprModel;
	}

	public void setSprModel(SpriteModel sprModel) {
		this.sprModel = sprModel;
    }
    
	public void calculateCenter() {
	}
	
    public Point getCenter() {
		center.x=(int) x-sprModel.getTaille_x() / 2;
        center.y=(int) y-sprModel.getTaille_y() / 2;
    	return center;
    }
    
	public int getNSpr() {
		return nSpr;
	}

	public void setNSpr(int spr) {
		nSpr = spr;
	}
	
	// Set 3 attributes at 1 time
	public void setSpr(Sprite p_sprite) {
		nSpr=p_sprite.spr.getNSpr();
		nBank=p_sprite.spr.getBank();
		reverse=p_sprite.reverse;
	}
	
	public int getNBank() {
		return nBank;
	}

	public void setNBank(int bank) {
		nBank = bank;
	}

	public boolean isMoved() {
		return moved;
	}

	public void setMoved(boolean moved) {
		this.moved = moved;
	}

	public int getLinkVertices() {
		return linkVertices;
	}

	public void setLinkVertices(int linkVertices) {
		this.linkVertices = linkVertices;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isForeground() {
		return foreground;
	}

	public void setForeground(boolean foreground) {
		this.foreground = foreground;
	}

	public EngineFX getSpecialEffect() {
		return specialEffect;
	}

	public void setSpecialEffect(EngineFX specialEffect) {
		this.specialEffect = specialEffect;
	}

	public int getEntityType() {
		return entityType;
	}

	public void setEntityType(int entityType) {
		this.entityType = entityType;
	}

	// Constructor / Destructor
	public SpriteEntity() {
		initialize();
	}

	public SpriteEntity(int p_id) {
		id=p_id;
	}
	
	public SpriteEntity(int x, int y, boolean p_createId) {
		if (p_createId) {
			// If it's not requested, we don't create a new ID (fonts for example doesn't need an ID because
			// they are only client side)
			initialize();
		} else {
			// Basic : set special effet
			setSpecialEffect(EngineFX.NO_EFFECT);
		}

		this.x=x;
		this.y=y;
	}
	
	private void initialize() {
		entityType=ENTITYTYPE_ENTITY;

		// Default : entity is visible
		visible=true;

		// Default : entity is part of the background
		foreground=false;

		specialEffect=EngineFX.NO_EFFECT;
		
		reverse=0;
		
		if (id == -1) {
			// Initialize ID if it's not done yet
			initializeId(SpriteEntity.class);
		}
	}
	
	public boolean isZildo() {
		return false;
	}
	
	public boolean isGoodies() {
		return false;
	}
	
	/**
	 * Appelée lorsqu'on supprime cette entité
	 */
	public void fall() {
		
	}

	/**
	 * @return the SpriteDescription associated to this object (constant, for now)
	 */
	public SpriteDescription getDesc() {
		if (desc == null) {
			desc=SpriteDescription.Locator.findSpr(nBank, nSpr);

		}
		return desc;
	}
	
	public void setDesc(SpriteDescription p_desc) {
		desc=p_desc;
		nSpr=p_desc.getNSpr();
		nBank=p_desc.getBank();
        setSprModel(EngineZildo.spriteManagement.getSpriteBank(nBank).get_sprite(nSpr));
	}
	
	public void animate() {
		ajustedX = (int) x - (sprModel.getTaille_x() >> 1);
		ajustedY = (int) y - (sprModel.getTaille_y() >> 1);
	}
	
	/**
	 * Serialize useful fields from this entity.
	 * @param p_buffer
	 */
	public void serialize(EasyBuffering p_buffer) {
		boolean isZildo=this.isZildo();
		p_buffer.putBooleans(isZildo, isVisible(), isForeground(), dying, 
				(reverse & REVERSE_HORIZONTAL) != 0, 
				(reverse & REVERSE_VERTICAL) != 0);
		p_buffer.put(this.getId());
		if (isZildo) {
			// Zildo needs extra info
			PersoZildo zildo=(PersoZildo) this;
			p_buffer.put((byte) zildo.getPv());
			p_buffer.put((byte) zildo.getMaxpv());
			p_buffer.put(zildo.getMoney());
			p_buffer.put((byte) zildo.getCountArrow());
			p_buffer.put((byte) zildo.getCountBomb());
		}
		p_buffer.put(this.getAjustedX());
		p_buffer.put(this.getAjustedY());
		p_buffer.put((int) this.x);
		p_buffer.put((int) this.y);
		//p_buffer.put(this.getScrX());
		//p_buffer.put(this.getScrY());
		p_buffer.put(this.z);
		p_buffer.put((byte) this.getNBank());
		p_buffer.put((byte) this.getSpecialEffect().ordinal());
		p_buffer.put((byte) this.getEntityType());
		p_buffer.put(this.getSprModel().getId());
	}
	
	/**
	 * Deserialize a byte buffer into a SpriteEntity
	 * @param p_buffer
	 * @return SpriteEntity
	 */
	public static SpriteEntity deserialize(EasyBuffering p_buffer) {
		boolean[] bools=p_buffer.readBooleans(6);
		boolean isZildo=bools[0];
		int id=p_buffer.readInt();
		SpriteEntity entity;
		if (isZildo) {
			entity=new PersoZildo(id);
			// Zildo needs extra info
			PersoZildo zildo=(PersoZildo) entity;
			zildo.setPv(p_buffer.readUnsignedByte());
			zildo.setMaxpv(p_buffer.readUnsignedByte());
			zildo.setMoney(p_buffer.readInt());
			zildo.setCountArrow(p_buffer.readUnsignedByte());
			zildo.setCountBomb(p_buffer.readUnsignedByte());
		} else {
			entity=new SpriteEntity(id);
		}
		entity.setAjustedX(p_buffer.readInt());
		entity.setAjustedY(p_buffer.readInt());
		entity.x=p_buffer.readInt();
		entity.y=p_buffer.readInt();
		//entity.setScrX(p_buffer.readInt());
		//entity.setScrY(p_buffer.readInt());
		entity.z=p_buffer.readFloat();
		entity.setVisible(bools[1]);
		entity.setForeground(bools[2]);
		entity.dying=bools[3];
		entity.setNBank(p_buffer.readUnsignedByte());
		entity.setSpecialEffect(EngineFX.values()[p_buffer.readUnsignedByte()]);
		entity.setEntityType(p_buffer.readUnsignedByte());
		int idSprModel=p_buffer.readInt();
		entity.setSprModel(Identified.fromId(SpriteModel.class, idSprModel));
		int reverse=0;
		if (bools[4]) {
			reverse|=REVERSE_HORIZONTAL;
		}
		if (bools[5]) {
			reverse|=REVERSE_VERTICAL;			
		}
        entity.reverse = reverse;
		return entity;
	}
	
    @Override
    public SpriteEntity clone() {
        try {
            SpriteEntity cloned = (SpriteEntity) super.clone();
            if (ENTITYTYPE_PERSO == getEntityType()) {
                Perso clonedPerso = (Perso) cloned;
                clonedPerso.setPersoSprites(null);
            }
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Unable to clone entity !");
        }
    }
    
    /**
     * Compare each decisive fields between 2 entities.<br/>
     * We don't override equals methode, because of collection operations. We still want that 'remove', 'get', etc, use the JVM's id as
     * reference.
     * @param p_obj
     * @return boolean
     */
    public boolean isSame(SpriteEntity p_other) {
        if (p_other == null) {
            return false;
        }
        // Compare each decisive fields
        if (this.x != p_other.x || this.y != p_other.y || this.z != p_other.z) {
            return false;
        }
        if (this.ajustedX != p_other.ajustedX || this.ajustedY != p_other.ajustedY) {
            return false;
        }
        if (this.scrX != p_other.scrX || this.scrY != p_other.scrY) {
            return false;
        }
        if (this.visible != p_other.visible || this.foreground != p_other.foreground || this.reverse != p_other.reverse) {
            return false;
        }
        if (this.sprModel.getId() != p_other.sprModel.getId()) {
            return false;
        }

        // Entities are the same
        return true;
    }
    
    @Override
	public String toString() {
    	StringBuffer sb=new StringBuffer();
    	sb.append("Entity id="+id+"\nx="+x+"\ny="+y+"\nnSpr="+nSpr+"\nvisible="+visible);
    	return sb.toString();
    }
    
    public Zone getZone() {
    	Zone z=new Zone(scrX, scrY, sprModel.getTaille_x(), sprModel.getTaille_y());
    	return z;
    }
}