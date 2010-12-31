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

package zildo.monde.items;

import java.util.ArrayList;
import java.util.List;

import zildo.client.sound.BankSound;
import zildo.monde.map.Point;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.persos.PersoZildo;
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
	private int itemSelected;	// From 0 to guiSprites.size()-1
	private int count;
	private PersoZildo heros;	// The one at the center of the circle
	
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
	public void create(List<Item> p_items, int p_selected, PersoZildo p_heros) {
		count=0;
		itemSelected=p_selected;
		guiSprites.clear();
		heros=p_heros;
		
		center=new Point((int) heros.x-2, (int) heros.y-12);
		for (Item item : p_items) {
            SpriteEntity e = EngineZildo.spriteManagement.spawnSprite(item.kind.representation, center.x, center.y, true);
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
			if (phase==CirclePhase.ROTATE_RIGHT) {
				diff=-diff;
			}
			alpha+=diff;
		}
		alpha-=pas*itemSelected;
		// Create the inventory sprites
		for (SpriteEntity entity : guiSprites) {
			int itemX=(int) (center.getX() + rayon*Math.sin(alpha));
			int itemY=(int) (center.getY() - rayon*Math.cos(alpha));
			entity.setAjustedX(itemX);
			entity.setAjustedY(itemY);
			
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
					itemSelected= (itemSelected+guiSprites.size() -1) % guiSprites.size();
					EngineZildo.soundManagement.playSound(BankSound.MenuMove, heros);
				} else if (phase==CirclePhase.ROTATE_RIGHT) {
					itemSelected= (itemSelected +1) % guiSprites.size();
					EngineZildo.soundManagement.playSound(BankSound.MenuMove, heros);
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
				phase=CirclePhase.ROTATE_LEFT;
			} else {
				phase=CirclePhase.ROTATE_RIGHT;
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
	
	public int getItemSelected() {
		return itemSelected;
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
