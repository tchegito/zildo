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


package zildo.monde.map;

import java.util.List;

import zildo.fwk.gfx.engine.TileEngine;
import zildo.monde.map.TileInfo.Template;

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
    
    public boolean collide(int p_posX, int p_posY, int p_nTile) {
        return tileInfos[p_nTile].collide(p_posX, p_posY);
    }

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