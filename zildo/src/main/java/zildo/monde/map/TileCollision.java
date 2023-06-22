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


package zildo.monde.map;

import java.util.List;

import zildo.fwk.gfx.engine.TileEngine;
import zildo.monde.map.TileInfo.Template;
import zildo.monde.sprites.Reverse;
import zildo.monde.sprites.Rotation;
import zildo.monde.util.Point;

/**
 * @author tchegito
 */
public class TileCollision {

    private final TileInfo[] tileInfos = new TileInfo[TileEngine.tileBankNames.length * 256];

    private TileCollision() {
    }

    final static TileCollision instance = new TileCollision();
    
    public static TileCollision getInstance() {
    	return instance;
    }
    
    /** Return Z of this tile. For example grass is 0, but stump is 5, so squirrel can jump on it.
     * When "blocked" is TRUE, consider any unwalkable tile has z=160. Otherwise, consider a return value different than 0 means
     * tile accepts squirrel jump on it.
     */
    public int getBottomZ(int p_posX, int p_posY, int p_nTile, boolean blocked) {
    	int z = blocked ? 160 : 0;
    	int bank = p_nTile >> 8;
    	if (bank == 0 || bank == 6 || bank == 3 | bank == 9) {
    		switch (p_nTile) {
    		// On the 4 tiles of regular stump, we consider upper region is at z=1 (to get the fall smoother)
    		case Tile.T_STUMP: case Tile.T_STUMP+1:
    			z = p_posY > 2 ? 5 : 1;
    			break;
    		case Tile.T_STUMP+2: case Tile.T_STUMP+3:
    			z = 5;
    			break;
    		case Tile.T_HSTUMP: case Tile.T_HSTUMP+1:
    		case Tile.T_HSTUMP+2: case Tile.T_HSTUMP+3:
    		case Tile.T_PLOT:
    			z = 8;
    			break;
    		case Tile.T_NATUREPALACE_PLATFORM:
    		case Tile.T_NATUREPALACE_PLATFORM2:
    			if (p_posY < 4) {
    				z = 0;break;
    			}
    		case Tile.T_BONES1: case Tile.T_BONES2: 	//Bones
    			z = 4;
    			break;
    		case Tile.T_WATER_MUD:
    			if (tileInfos[p_nTile].collide(p_posX, p_posY)) {
    				z=0;
    			} else {
    				z=-2;
    			}
    			break;
    		case Tile.T_BUSH:
    			z = 10;
    			break;
    		}
    	}
    	return z;
    }
    
    public boolean collide(int p_posX, int p_posY, Tile p_tile, int z) {
    	boolean result =  collide(p_posX, p_posY, p_tile.getValue(), p_tile.reverse, p_tile.rotation, z);

    	return result;
    }
    
    public boolean collide(int p_posX, int p_posY, int p_nTile, Reverse p_reverse, Rotation p_rotate, int p_z) {
    	int px = p_posX;
    	int py = p_posY;
    	//if (true) return false;
    	if (p_reverse != Reverse.NOTHING) {
    		switch (p_reverse) {
    		case HORIZONTAL:
    			px = 16-px;
    			break;
    		case ALL:
    			px = 16-px;
    		case VERTICAL:
    			py = 16-py;
    			break;
    		}
    	}
    	if (p_rotate != Rotation.NOTHING) {
    		// Turning the pattern with 'alpha' radians is equivalent to turning the point to '-alpha' radians
    		Point dest = p_rotate.negate().rotate(new Point(px, py), 16, 16);
    		px = dest.x;
    		py = dest.y;
    	}
		boolean result = tileInfos[p_nTile].collide(px, py);
    	
    	// Check altitude depending on tiles
    	if (result || p_z < 0) {
    		int bottomZ = getBottomZ(p_posX, p_posY, p_nTile, true);
    		result = p_z < bottomZ;
    	}
    	return result;
    }

    /**
     * Assume that p_nTile is in range 0..<maxTile (now : 2304)
     * @param p_nTile
     * @return
     */
    public boolean isTileWalkable(int p_nTile) {
    	return tileInfos[p_nTile].template == Template.WALKABLE;
    }
    
    public TileInfo getTileInfo(int p_nTile) {
    	return tileInfos[p_nTile];
    }
    
	
	public void updateInfoCollision(String p_name, List<Integer> p_tileInfos) {
		int numBank = TileEngine.getBankFromName(p_name);
		for (int i=0;i<p_tileInfos.size();i++) {
    		tileInfos[numBank * 256 + i] = TileInfo.fromInt(p_tileInfos.get(i));
    	}
    }
	
	public void updateInfoForOneTile(int p_numBank, int p_numTile, TileInfo p_tileInfo) {
		tileInfos[p_numBank * 256 + p_numTile] = p_tileInfo;
	}
}