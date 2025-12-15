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

package zildo.fwk.gfx.primitive;

import zildo.Zildo;
import zildo.fwk.opengl.compatibility.VBOBuffers;
import zildo.monde.map.Tile;
import zildo.monde.util.Point;
import zildo.resource.Constantes;

/**
 * Groupe of primitive from same layer (=graphic bank). They share all the same vertex and textures buffer.<br/>
 * But we make sure that texture buffer is unique for this group with {@link VBOBuffers#resetTextureBuffer()}
 * @author Tchegito
 *
 */
public class TileGroupPrimitive {

	boolean shaderAvailable;
	
	protected TilePrimitive[][] meshes;
	
	public TileGroupPrimitive(int nbGroup, boolean shaderAvailable) {
		meshes = new TilePrimitive[Constantes.TILEENGINE_FLOOR][nbGroup];
		this.shaderAvailable = shaderAvailable;
	}
	
	public void startInitialization() {
		for (int floor = 0; floor < Constantes.TILEENGINE_FLOOR; floor++) {
			for (int i = 0; i < meshes[floor].length; i++) {
				if (meshes[floor][i] == null) {
					int maxScreenPoints = ((Zildo.viewPortX >> 4)+1) * ((Zildo.viewPortY >> 4)+1);
					maxScreenPoints*=4;	// 4 vertices for a quad
					maxScreenPoints*=2;	// 2 screens for be quiet
					meshes[floor][i] = new TilePrimitive(maxScreenPoints);
				}		
				meshes[floor][i].startInitialization();
			}
		}
	}
	
	public void endInitialization() {
		for (int floor = 0; floor < Constantes.TILEENGINE_FLOOR; floor++) {
			for (int i = 0; i < meshes[floor].length; i++) {
				meshes[floor][i].endInitialization();
			}
		}
	}
	
	public void clearBuffers() {
		for (int floor = 0; floor < Constantes.TILEENGINE_FLOOR; floor++) {
			for (int i = 0; i < meshes[floor].length; i++) {
				if (meshes[floor][i] != null) {
					meshes[floor][i].clearBuffers();
				}
			}
		}
	}
	public void cleanUp()
	{
		for (int floor = 0; floor < Constantes.TILEENGINE_FLOOR; floor++) {
			for (TilePrimitive tp : meshes[floor]) {
				if (tp != null) {
					tp.cleanUp();
				}
			}
		}
	}
    
	/**
	 * Update tile at given location. If the tile's bank is different (and inferior to previous one)
	 * we have to remove the previous tile. Because its are displayed in ascendant order, so
	 * greater bank win at display phase.
	 * @param tile
	 * @param x
	 * @param y
	 * @param floor TODO
	 * @param n_motif
	 * @param hasChanged
	 */
	public void updateTile(Tile tile, int x, int y, int floor, int n_motif, boolean hasChanged) {
		byte bank = tile.bank;
		if (tile.previousBank > tile.bank) {
			// Remove tile from previous primitive
			removeTile(tile.previousBank, x, y, floor);
			tile.previousBank = tile.bank;
		}

		int xTex = (n_motif % 16) << 4;
		int yTex = (n_motif >> 4) << 4; // +1;

		meshes[floor][bank].updateTile(x,
				y,
				xTex,
				yTex, 
				tile.reverse, tile.rotation, hasChanged);
	}
    
    public void removeTile(int nth, int x, int y, int floor) {
    	meshes[floor][nth].removeTile(x, y);
    }

    public void initFreeBuffer(Point camera) {
		for (int floor = 0; floor < Constantes.TILEENGINE_FLOOR; floor++) {
	    	for (int i=0;i<meshes[floor].length;i++) {
	    		meshes[floor][i].fillFreeIndex(camera);
	    	}
		}
    }
    /**
     * Render every mesh only if there's some vertices in it.
     * @param action action to be executed before render of each mesh (useful for texture binding)
     */
    public void render(int floor, ActionNthRunner action) {
    	for (int i=0;i<meshes[floor].length;i++) {
    		if (meshes[floor][i] != null && !meshes[floor][i].isEmpty()) {
    			action.execute(i, shaderAvailable);
    			meshes[floor][i].render();
    		}
    	}
    }

    /**
     * Based on Action design pattern, just to be elegant with this class' philosophy : simple call
     * from outside, and all job is done here.
     */
    public interface ActionNthRunner {
    	void execute(int numTexture, boolean shaderAvailable);
    }
}
