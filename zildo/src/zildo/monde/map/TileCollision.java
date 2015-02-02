/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
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
    /*
    public int getBottomZ(int p_posX, int p_posY, Tile p_tile) {
    	return collide(p_posX, p_posY, p_tile)
    }
    */
    final static int HSTUMP = 225 + 256*6;	// Higher stump's first tile
    
    public int getBottomZ(Tile p_tile, boolean blocked) {
    	int z = blocked ? 16 : 0;
    	if (p_tile != null && (p_tile.bank == 0 || p_tile.bank == 6)) {
    		switch (p_tile.getValue()) {
    		case 159: case 160: case 161: case 162:
    			z = 5;
    			break;
    		case HSTUMP: case HSTUMP+1: case HSTUMP+2: case HSTUMP+3:
    			z = 10;
    		}
    	}
    	return z;
    }
    
    public boolean collide(int p_posX, int p_posY, Tile p_tile, int z) {
    	boolean result =  collide(p_posX, p_posY, p_tile.getValue(), p_tile.reverse, p_tile.rotation);
    	
    	// Check altitude depending on tiles
    	if (result) {
    		int bottomZ = getBottomZ(p_tile, true);
    		result = z < bottomZ;
    	}
    	return result;
    }
    
    public boolean collide(int p_posX, int p_posY, int p_nTile, Reverse p_reverse, Rotation p_rotate) {
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
		return tileInfos[p_nTile].collide(px, py);
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