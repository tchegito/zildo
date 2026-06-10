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

package zeditor.tools.sprites;

import java.util.List;

import zeditor.tools.tiles.GraphChange;
import zildo.monde.util.Zone;

/**
 * @author Tchegito
 *
 */
public class SpriteBanque {

	Zone[] zones;
	/** List of all picture where sprites should be grabbed **/
	List<GraphChange> pkmChanges;
	
	protected int[][] coordsInt;
	
	public Zone[] getZones() {
		if (zones == null) {
			int[][] coordsInt = getCoordsInt();
			zones = new Zone[coordsInt.length];
			int p=0;
			for (int[] crds : coordsInt) {
				zones[p++] = new Zone(crds[0], crds[1], crds[2], crds[3]);
			}
		}
		return zones;
	}
	
	public List<GraphChange> getPkmChanges() {
		return pkmChanges;
	}
	
    protected int[][] getCoordsInt() {
    	return coordsInt;
    }
}
