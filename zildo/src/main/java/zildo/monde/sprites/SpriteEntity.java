/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
 * 
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

import zildo.Zildo;
import zildo.fwk.db.Identified;
import zildo.fwk.file.EasyBuffering;
import zildo.fwk.file.EasySerializable;
import zildo.fwk.gfx.EngineFX;
import zildo.monde.collision.Collision;
import zildo.monde.collision.DamageType;
import zildo.monde.map.Area;
import zildo.monde.map.Tile;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.desc.EntityType;
import zildo.monde.sprites.desc.GearDescription;
import zildo.monde.sprites.desc.SpriteDescription;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.sprites.persos.ia.mover.MoveOrder;
import zildo.monde.sprites.persos.ia.mover.Mover;
import zildo.monde.sprites.utils.Sprite;
import zildo.monde.util.Point;
import zildo.monde.util.Zone;
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
public class SpriteEntity extends Identified implements Cloneable,
		EasySerializable {
	//public static final int Reverse.HORIZONTAL = 128;
	//public static final int Reverse.VERTICAL = 64;
	public static final int FOREGROUND = 32; // Only for MAP format
	public static final int REPEATED_OR_ROTATED = 16; // Fields 'repeatX' and 'repeatY' are different than 1
	public static final int PUSHABLE = 64;	// Only for elements, TRUE if Zildo can push it
	
	// Class variable
	public float x, y, z; // Real position located by center (z is never
							// initialized with entities)
	private int ajustedX, ajustedY;
	private int scrX, scrY; // Screen position (so with camera adjustment)
	protected SpriteModel sprModel; // Reference to the sprite being rendered
	public int nSpr; // Pour les perso devient une interprétation de 'angle' et 'pos_seqsprite'
	public int nBank;
	protected SpriteDescription desc; // Interpretation of nSpr and nBank
	private int linkVertices; // Index on VertexBuffer's position about quad
								// describing this sprite
	public boolean visible; // TRUE=visible FALSE=invisible
	protected boolean foreground; // Drawn at last in display sequence. So always
								// on foreground
	public boolean dying; // TRUE=we must remove them
	protected Point center = new Point(); // Defaults : 1) entity : [x/2, y] 2)
											// element : [x/2, y/2]
	public int floor=1;	// Indicates sprite's floor: 0,1,2
	
	protected String name;
	protected Mover mover;	// Allow moving without physics (if NULL => regular movement)
	protected boolean ghost = false; // TRUE=script control it (here because of Mover things, but
	// isn't apart the basic structure needed to display on client side

	protected EngineFX specialEffect; // Utilisé pour changer la couleur d'un
									// garde par exemple
	protected float alpha = 255; // 0..255 alpha channel (we use float for speed/acceleration calculation)
	public int light = 0xffffff;	/// 0x0RGB where each color is on 8 bits
	public int zoom = 255;	//0..255 zoom factor : 255=full size
	public int zoomV = 0;	// speed on zoom (on each frame, we'll apply zoom = zoom + zoomV)
	public float alphaV, alphaA;	// Speed and acceleration for alpha

	public byte repeatX=1, repeatY=1;


	public Reverse reverse = Reverse.NOTHING; // Combination of Reverse.HORIZONTAL/VERTICAL (or 0)
	public Rotation rotation = Rotation.NOTHING;
	public boolean clientSpecific; // TRUE if this entity should not appear on all client's screen

	private double iota;
	
	// To identify which type of entity we're dealing with
	protected EntityType entityType;
	
	public int getAlpha() {
		return (int) alpha;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}
	
	public int getLight() {
		return light;
	}
	
	public void setLight(int light) {
		this.light = light;
	}
	
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
	
	public Point getEffectiveSize() {
		int sx = sprModel.getTaille_x();
		int sy = sprModel.getTaille_y();
		// Is the sprite rotated ?
		int rx = repeatX;
		int ry = repeatY;
		if (rotation.isWidthHeightSwitched()) {
			sx = sprModel.getTaille_y(); rx = repeatY; 
			sy = sprModel.getTaille_x(); ry = repeatX;
		}
		// Equivalent to 'entity.getCenter()' but with the right width/height
		// Is the sprite repeated ?
		sx*=rx;
		sy*=ry;
		return new Point(sx, sy);
	}

	public void setSprModel(SpriteModel sprModel) {
		this.sprModel = sprModel;
	}

	public void calculateCenter() {
	}

	public Point getCenter() {
		center.x = (int) x - sprModel.getTaille_x() / 2;
		center.y = (int) y - sprModel.getTaille_y();
		return center;
	}

	public int getNSpr() {
		return nSpr;
	}

	public void setNSpr(int spr) {
		nSpr = spr;
	}

	public void setNSpr(SpriteDescription sprDesc) {
		nSpr = sprDesc.getNSpr();
	}

	// Set 4 attributes at 1 time
	public void setSpr(Sprite p_sprite) {
		nSpr = p_sprite.nSpr;
		nBank = p_sprite.spr.getBank();
		reverse = p_sprite.reverse;
		rotation = p_sprite.rotate == null ? Rotation.NOTHING : p_sprite.rotate;
	}

	public int getNBank() {
		return nBank;
	}

	public void setNBank(int bank) {
		nBank = bank;
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

	public EntityType getEntityType() {
		return entityType;
	}

	public void setEntityType(EntityType entityType) {
		this.entityType = entityType;
	}

	// Constructor / Destructor
	public SpriteEntity() {
		initialize();
	}

	public SpriteEntity(int p_id) {
		id = p_id;
	}

	public SpriteEntity(int x, int y, boolean p_createId) {
		if (p_createId) {
			// If it's not requested, we don't create a new ID (fonts for
			// example doesn't need an ID because
			// they are only client side)
			initialize();
		} else {
			// Basic : set special effet
			setSpecialEffect(EngineFX.NO_EFFECT);
		}

		this.x = x;
		this.y = y;
	}

	private void initialize() {
		entityType = EntityType.ENTITY;

		// Default : entity is visible
		visible = true;

		// Default : entity is part of the background
		foreground = false;

		specialEffect = EngineFX.NO_EFFECT;

		reverse = Reverse.NOTHING;
	}

	public boolean isZildo() {
		return false;
	}

	public boolean isGoodies() {
		return false;
	}

	/**
	 * Called when this entity fall on the ground (any: lava, earth, water...)
	 */
	public boolean fall() {
		return true;
	}

	/**
	 * @return the SpriteDescription associated to this object (constant, for
	 *         now)
	 */
	public SpriteDescription getDesc() {
		if (desc == null) {
			desc = SpriteDescription.Locator.findSpr(nBank, nSpr);

		}
		return desc;
	}

	public void setDesc(SpriteDescription p_desc) {
		desc = p_desc;
		if (p_desc != null) {
			nSpr = p_desc.getNSpr();
			nBank = p_desc.getBank();
			setSprModel(EngineZildo.spriteManagement.getSpriteBank(nBank)
					.get_sprite(nSpr));
		}
	}

	/**
	 * Basically, an entity doesn't move. Except for specific sprites on which hero can walk (moving platform for example).
	 */
	public void animate() {
		alphaV += alphaA;
		alpha += alphaV;
		if (alpha < 0 && fall()) {
			dying = true;
		}
		zoom += zoomV;
		
		Collision floorColli = getFloorCollision();
		if (floorColli != null) {
			EngineZildo.collideManagement.addFloorCollision(floorColli);
		}
		
		if (mover != null && mover.isActive()) {
			// Moving is delegated to another object
			int dx = ajustedX - (int) x;
			int dy = ajustedY - (int) y;
			mover.reachTarget();
				
			ajustedX = (int) x + dx;
			ajustedY = (int) y + dy;
			return;
		} else if (desc instanceof GearDescription) {
			switch ((GearDescription) desc) {
				case LAVA1:
				case LAVA2:
				case LAVA3:
					zoom = 230 + (int) (25 * Math.sin(iota*0.8));
					iota += 0.1;
					alpha = 200 + (int) (55 * Math.cos(iota/3));
			default:
				break;
			}
		}
	}

	/**
	 * Serialize useful fields from this entity.
	 * 
	 * @param p_buffer
	 */
	@Override
	public void serialize(EasyBuffering p_buffer) {
		boolean isZildo = this.isZildo();
		boolean[] boolRot = rotation.getBooleans();
		p_buffer.putBooleans(isZildo, isVisible(), isForeground(), dying,
				reverse == Reverse.HORIZONTAL,
				reverse == Reverse.VERTICAL,
				boolRot[0], boolRot[1]);
		p_buffer.put(this.getId());
		if (isZildo) {
			// Zildo needs extra info
			PersoPlayer zildo = (PersoPlayer) this;
			p_buffer.put((byte) zildo.getMaxpv());
			p_buffer.put((byte) zildo.getPv());
			p_buffer.put(zildo.getMoney());
			p_buffer.put((byte) zildo.getCountArrow());
			p_buffer.put((byte) zildo.getCountBomb());
		}
		p_buffer.put(this.getAjustedX());
		p_buffer.put(this.getAjustedY());
		p_buffer.put((int) this.x);
		p_buffer.put((int) this.y);
		p_buffer.put((int) this.z);
		p_buffer.put((byte) this.getNBank());
		p_buffer.put((byte) this.getSpecialEffect().ordinal());
		p_buffer.put((byte) this.getEntityType().intValue());
		p_buffer.put(this.getSprModel().getId());
	}

	/**
	 * Deserialize a byte buffer into a SpriteEntity
	 * 
	 * @param p_buffer
	 * @return SpriteEntity
	 */
	public static SpriteEntity deserialize(EasyBuffering p_buffer) {
		boolean[] bools = p_buffer.readBooleans(8);
		boolean isZildo = bools[0];
		int id = p_buffer.readInt();
		SpriteEntity entity;
		if (isZildo) {
			entity = new PersoPlayer(id);
			// Zildo needs extra info
			PersoPlayer zildo = (PersoPlayer) entity;
			zildo.setMaxpv(p_buffer.readUnsignedByte());
			zildo.setPv(p_buffer.readUnsignedByte());
			zildo.setMoney(p_buffer.readInt());
			zildo.setCountArrow(p_buffer.readUnsignedByte());
			zildo.setCountBomb(p_buffer.readUnsignedByte());
		} else {
			entity = new SpriteEntity(id);
		}
		entity.setAjustedX(p_buffer.readInt());
		entity.setAjustedY(p_buffer.readInt());
		entity.x = p_buffer.readInt();
		entity.y = p_buffer.readInt();
		entity.z = p_buffer.readInt();
		entity.setVisible(bools[1]);
		entity.setForeground(bools[2]);
		entity.dying = bools[3];
		entity.setNBank(p_buffer.readUnsignedByte());
		entity.setSpecialEffect(EngineFX.values()[p_buffer.readUnsignedByte()]);
		entity.setEntityType(EntityType.fromInt(p_buffer.readUnsignedByte()));
		int idSprModel = p_buffer.readInt();
		entity.setSprModel(Identified.fromId(SpriteModel.class, idSprModel));
		entity.reverse = Reverse.fromBooleans(bools[4], bools[5]);
		entity.rotation = Rotation.fromBooleans(bools[6], bools[7]);
		return entity;
	}

	@Override
	public SpriteEntity clone() {
		try {
			SpriteEntity cloned = (SpriteEntity) super.clone();
			if (EntityType.PERSO == getEntityType()) {
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
	 * We don't override equals methode, because of collection operations. We
	 * still want that 'remove', 'get', etc, use the JVM's id as reference.
	 * 
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
		if (this.ajustedX != p_other.ajustedX
				|| this.ajustedY != p_other.ajustedY) {
			return false;
		}
		if (this.scrX != p_other.scrX || this.scrY != p_other.scrY) {
			return false;
		}
		if (this.visible != p_other.visible
				|| this.foreground != p_other.foreground
				|| this.reverse != p_other.reverse) {
			return false;
		}
		if (this.sprModel.getId() != p_other.sprModel.getId()) {
			return false;
		}

		// Entities are the same
		return true;
	}

	/**
	 * Check if entity is inside the viewport (TRUE).
	 * @return boolean
	 */
	public boolean isInsideView() {
		int sx = sprModel.getTaille_x(); int sy = sprModel.getTaille_y();
		if (rotation == Rotation.CLOCKWISE || rotation == Rotation.COUNTERCLOCKWISE) {
			int keepSy = sx; sx = sy ; sy = keepSy;
		}
		return (scrX+(sx*repeatX) >= 0 && 
			   (scrY-z+(sy*repeatY)+1) >= 0 && 
		        scrX <= Zildo.viewPortX && 
		        (scrY-z) <= Zildo.viewPortY);		
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Entity id=" + id + "\nx=" + x + "\ny=" + y + "\nnSpr="
				+ nSpr + "\nvisible=" + visible);
		return sb.toString();
	}

	/**
	 * Returns TRUE only if entity is manipulated by a script currently processing.
	 * @return boolean
	 */
	public boolean isGhost() {
		return ghost;
	}

	public void setGhost(boolean p_ghost) {
		ghost = p_ghost;
	}
	
	protected Boolean cacheIsBlocking = null;
	
	public boolean isBlocking() {
		// A sprite entity is never blocking ! That's the rule ;)
		// Except for ROCK_PILLAR outside of lava.
		if (cacheIsBlocking == null) {
			cacheIsBlocking = computeIsBlocking();
		}
		return cacheIsBlocking.booleanValue();
	}
	
	protected boolean computeIsBlocking() {
		if (getDesc().isBlocking()) {
			// Special case: ROCK_PILLAR => no collision if it stands in lava
			Area map = EngineZildo.mapManagement.getCurrentMap();
			if (Tile.T_LAVA == map.readmap((int)x>>4, (int)y>>4)) {
				return false;
			}
			return true;
		}
		return false;
	}

	public Zone getZone() {
		Zone zone = new Zone(scrX, scrY, sprModel.getTaille_x()*repeatX,
				sprModel.getTaille_y()*repeatY);

		return zone;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	// Tells this entity that it could move by a Mover
	public void initMover() {
		mover = new Mover(this);
	}
	
	public void setMover(MoveOrder m) {
		if (mover == null) {	// Create a mover, if this entity haven't any
			mover = new Mover(this);
		}
		mover.merge(m);
	}
	
	public Mover getMover() {
		return mover;
	}
	
	// Just for inheritance. No meaning for an entity
	public void setPushable(boolean pushable) {
		
	}
	
	public int getFloor() {
		return floor;
	}
	
	// Tricky methods, just for sprite sorting purpose (see Perso#getFloorForSort)
	public int getFloorForSort() {
		return floor;
	}
	
	public void setFloor(int fl) {
		floor = fl;
	}
	
	public Collision getFloorCollision() {
		if (desc == ElementDescription.POISONGOOP) {
			return new Collision(new Point(x, y), new Point(5, 3), null, DamageType.SLOWNESS, null);
		}
		return null;
	}
	
	public String getTargetScene() {
		return null;
	}
	public String getFallScene() {
		return null;
	}
	
	public void shift(Point p_offset) {
		x+=p_offset.x;
		y+=p_offset.y;
		setAjustedX(getAjustedX() + p_offset.x);
		setAjustedY(getAjustedY() + p_offset.y);
	}
}