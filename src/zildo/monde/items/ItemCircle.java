package zildo.monde.items;

import java.util.ArrayList;
import java.util.List;

import zildo.fwk.bank.SpriteBank;
import zildo.monde.decors.SpriteEntity;
import zildo.monde.map.Point;
import zildo.server.EngineZildo;

public class ItemCircle {

	public enum CirclePhase {
		EXPANSION, FIXED, REDUCTION, ROTATE_LEFT, ROTATE_RIGHT; 
		
		public boolean isSizeChanging() {
			return this==EXPANSION || this==REDUCTION;
		}
		public boolean isRotating() {
			return this==ROTATE_LEFT || this==ROTATE_RIGHT;
		}
	}
	
	private List<SpriteEntity> guiSprites;
	private Point center;
	private CirclePhase phase;	// 0=create 1=fixed 2=remove 3=scroll
	private int itemSelected;	// From 0 to guiSprites.size()
	private int count;
	
	public ItemCircle() {
		guiSprites=new ArrayList<SpriteEntity>();
		count=0;
		itemSelected=0;
		phase=CirclePhase.EXPANSION;
	}
	
	public List<SpriteEntity> getSprites() {
		return guiSprites;
	}
	
	/**
	 * Create the circle with given items
	 * @param p_items
	 * @param p_x
	 * @param p_y
	 */
	public void create(List<Item> p_items, int p_x, int p_y) {
		count=0;
		guiSprites.clear();

		center=new Point(p_x, p_y);
		for (Item item : p_items) {
			int typ=item.kind.ordinal();
            SpriteEntity e = EngineZildo.spriteManagement.spawnSprite(SpriteBank.BANK_ELEMENTS, 4 + typ, p_x, p_y, true);
            e.clientSpecific=true;
            guiSprites.add(e);
		}
		display();
	}
	
	/**
	 * Place the entity to have the circle around the center, regards to circle's phase.
	 */
	private void display() {
		
		int rayon=32;
		double alpha=0;
		double pas=2*Math.PI / guiSprites.size();
		if (phase.isSizeChanging()) {
			rayon=count;
		} else if (phase.isRotating()) {
			double diff=(pas * count / 32);
			if (phase==CirclePhase.ROTATE_LEFT) {
				diff=-diff;
			}
			alpha+=diff;
		}
		alpha+=pas*itemSelected;
		// Create the inventory sprites
		for (SpriteEntity entity : guiSprites) {
			int itemX=(int) (center.getX() + rayon*Math.sin(alpha));
			int itemY=(int) (center.getY() - rayon*Math.cos(alpha));
			entity.x=itemX;
			entity.y=itemY;
			
			alpha+=pas;
		}		
	}
	
	/**
	 * Main method for caller.
	 */
	public void animate() {
		switch (phase) {
		case EXPANSION:
		case ROTATE_LEFT:
		case ROTATE_RIGHT:
			if (count < 32) {
				count+=2;
			} else {
				if (phase==CirclePhase.ROTATE_LEFT) {
					itemSelected--;
				} else if (phase==CirclePhase.ROTATE_RIGHT) {
					itemSelected++;
				}
				phase=CirclePhase.FIXED;
			}
			break;
		case REDUCTION:
			if (count > 0) {
				count-=2;
			} else {
				phase=CirclePhase.FIXED;
				kill();
			}
			break;
		}
		display();
	}
	
	public void rotate(boolean p_clockWise) {
		if (!phase.isRotating()) {
			if (p_clockWise) {
				phase=CirclePhase.ROTATE_RIGHT;
			} else {
				phase=CirclePhase.ROTATE_LEFT;
			}
			count=0;
		}
	}
	
	public boolean isAvailable() {
		return phase==CirclePhase.FIXED;
	}
	
	public boolean isReduced() {
		return guiSprites.size() == 0;
	}
	
	/**
	 * Soft remove circle.
	 */
	public void close() {
		phase=CirclePhase.REDUCTION;
	}
	
	/**
	 * Emergency remove circle.
	 */
	public void kill() {
		for (SpriteEntity e : guiSprites) {
			e.dying=true;
		}
		guiSprites.clear();
	}
}
