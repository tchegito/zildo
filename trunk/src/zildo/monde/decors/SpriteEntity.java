package zildo.monde.decors;

import zildo.fwk.gfx.PixelShaders;
import zildo.monde.SpriteModel;


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
public class SpriteEntity
{
	public static final int ENTITYTYPE_ENTITY =0;
	public static final int ENTITYTYPE_ELEMENT =1;
	public static final int ENTITYTYPE_PERSO =2;
	public static final int ENTITYTYPE_FONT =3;


	// Class variable
	private int scrX,scrY;	// Screen position (so with camera adjustment)
	private SpriteModel sprModel;	// Reference to the sprite being rendered
	protected int nSpr;			// Pour les perso devient une interprétation de 'angle' et 'pos_seqsprite'
	protected int nBank;
	private boolean moved;			// True=need to synchronize the vertex buffer, False=no move this frame
	private int linkVertices;	// Index on VertexBuffer's position about quad describing this sprite
	protected boolean visible;		// TRUE=visible FALSE=invisible
	private boolean foreground;	// Drawn at last in display sequence. So always on foreground

	private int specialEffect;		// Utilisé pour changer la couleur d'un garde par exemple

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
		entityType=ENTITYTYPE_ENTITY;

		// Default : entity is visible
		visible=true;

		// Default : entity is part of the background
		foreground=false;

		specialEffect=PixelShaders.ENGINEFX_NO_EFFECT;
	}

	/**
	 * Appelée lorsqu'on supprime cette entité
	 */
	public void fall() {
		
	}

}


