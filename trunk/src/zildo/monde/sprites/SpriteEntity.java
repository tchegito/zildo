package zildo.monde.sprites;

import zildo.fwk.Identified;
import zildo.fwk.file.EasyBuffering;
import zildo.fwk.gfx.PixelShaders;
import zildo.monde.sprites.persos.PersoZildo;
import zildo.monde.sprites.utils.Sprite;


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
public class SpriteEntity extends Identified
{
	public static final int ENTITYTYPE_ENTITY =0;
	public static final int ENTITYTYPE_ELEMENT =1;
	public static final int ENTITYTYPE_PERSO =2;
	public static final int ENTITYTYPE_FONT =3;

	public static final int REVERSE_HORIZONTAL = 1;
	public static final int REVERSE_VERTICAL = 2;
	
	// Class variable
	public float x,y,z;	// Real position located by center (z is never initialized with entities)
    private int ajustedX,ajustedY;
	private int scrX,scrY;	// Screen position (so with camera adjustment)
	private SpriteModel sprModel;	// Reference to the sprite being rendered
	public int nSpr;			// Pour les perso devient une interprétation de 'angle' et 'pos_seqsprite'
	public int nBank;
	private boolean moved;			// True=need to synchronize the vertex buffer, False=no move this frame
	private int linkVertices;	// Index on VertexBuffer's position about quad describing this sprite
	public boolean visible;		// TRUE=visible FALSE=invisible
	private boolean foreground;	// Drawn at last in display sequence. So always on foreground
	public boolean dying;		// TRUE=we must remove them
	
	private int specialEffect;		// Utilisé pour changer la couleur d'un garde par exemple
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

	public int getSpecialEffect() {
		return specialEffect;
	}

	public void setSpecialEffect(int specialEffect) {
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

		specialEffect=PixelShaders.ENGINEFX_NO_EFFECT;
		
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
	 * Serialize useful fields from this entity.
	 * @param p_buffer
	 */
	public void serializeEntity(EasyBuffering p_buffer) {
		boolean isZildo=this.isZildo();
		p_buffer.put(isZildo);
		p_buffer.put(this.getId());
		if (isZildo) {
			// Zildo needs extra info
			PersoZildo zildo=(PersoZildo) this;
			p_buffer.put(zildo.getPv());
			p_buffer.put(zildo.getMaxpv());
			p_buffer.put(zildo.getMoney());
		}
		p_buffer.put(this.getAjustedX());
		p_buffer.put(this.getAjustedY());
		p_buffer.put((int) this.x);
		p_buffer.put((int) this.y);
		p_buffer.put(this.getScrX());
		p_buffer.put(this.getScrY());
		p_buffer.put(this.z);
		p_buffer.put(this.isVisible());
		p_buffer.put(this.isForeground());
		p_buffer.put(this.getNBank());
		p_buffer.put(this.getSpecialEffect());
		p_buffer.put(this.getEntityType());
		p_buffer.put(this.getSprModel().getId());
		p_buffer.put(this.reverse);
	}
	
	/**
	 * Deserialize a byte buffer into a SpriteEntity
	 * @param p_buffer
	 * @return SpriteEntity
	 */
	public static SpriteEntity deserializeOneEntity(EasyBuffering p_buffer) {
		boolean isZildo=p_buffer.readBoolean();
		int id=p_buffer.readInt();
		SpriteEntity entity;
		if (isZildo) {
			entity=new PersoZildo(id);
			// Zildo needs extra info
			PersoZildo zildo=(PersoZildo) entity;
			zildo.setPv(p_buffer.readInt());
			zildo.setMaxpv(p_buffer.readInt());
			zildo.setMoney(p_buffer.readInt());
		} else {
			entity=new SpriteEntity(id);
		}
		entity.setAjustedX(p_buffer.readInt());
		entity.setAjustedY(p_buffer.readInt());
		entity.x=p_buffer.readInt();
		entity.y=p_buffer.readInt();
		entity.setScrX(p_buffer.readInt());
		entity.setScrY(p_buffer.readInt());
		entity.z=p_buffer.readFloat();
		entity.setVisible(p_buffer.readBoolean());
		entity.setForeground(p_buffer.readBoolean());
		entity.setNBank(p_buffer.readInt());
		entity.setSpecialEffect(p_buffer.readInt());
		entity.setEntityType(p_buffer.readInt());
		int idSprModel=p_buffer.readInt();
		entity.setSprModel(Identified.fromId(SpriteModel.class, idSprModel));
        entity.reverse = p_buffer.readInt();
		return entity;
	}
}


