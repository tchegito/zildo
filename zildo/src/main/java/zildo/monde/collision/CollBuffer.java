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

package zildo.monde.collision;

import zildo.fwk.collection.CycleIntBuffer;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;

/**
 * @author Tchegito
 *
 */
public class CollBuffer {
	
	final static int SIZE_X = 64;
	final static int SIZE_Y = 64;
	
	int[][][] presences;
	CycleIntBuffer indexPerso;
	static int[][] capillarity;
	
	final int PRESENCE_SIZE = 3;
	
	public CollBuffer() {
		presences = new int[SIZE_X][SIZE_Y][PRESENCE_SIZE];
		indexPerso = new CycleIntBuffer(512);
		
		capillarity = new int[64][64];

		clear();
	}
	
	public void clear() {
		for (int i=0;i<64;i++) {
			for (int j=0;j<64;j++) {
				for (int k=0;k<PRESENCE_SIZE;k++) {
					presences[i][j][k] = -1;
				}
			}
		}
		indexPerso.init(-1);
		indexPerso.rewind();
		
		for (int i=0;i<64;i++) {
			for (int j=0;j<64;j++) {
				capillarity[j][i] = 0;
			}
		}
	}
	
	public void updateId(int gridX, int gridY, int id) {
		if (id != -1) {
			int previousLoc = indexPerso.get(id);
			int loc = (gridY << 6) + gridX;
			if (previousLoc != loc) {
				if (previousLoc != -1) {
					// Remove previous location
					int ancGridX = previousLoc & 63;
					int ancGridY = previousLoc >> 6;
					resetId(ancGridX, ancGridY, id);
				}
				indexPerso.set(id, loc);
				setId(gridX, gridY, id);
			}
		}
	}
	
	
	public int[] getIds(int gridX, int gridY, int fromId) {
		return presences[gridY][gridX];
	}
	
	public static int howManyAround(int gridX, int gridY) {
		if (isOutOfBounds(gridX, gridY)) {
			return 0;
		} else {
			return capillarity[gridY][gridX];
		}
	}
	
	private void setId(int gridX, int gridY, int fromId) {
		for (int i=0;i<PRESENCE_SIZE;i++) {
			int id = presences[gridY][gridX][i];
			if (id == fromId) break;
			if (id != fromId && id == -1) {
				presences[gridY][gridX][i] = fromId;
				// Increment or decrement capillarity
				applyPatch(gridX, gridY, fromId != -1);
				break;
			}
		}
	}
	
	public void resetId(int gridX, int gridY, int fromId) {
		for (int i=0;i<PRESENCE_SIZE;i++) {
			int id = presences[gridY][gridX][i];
			if (id == fromId) {
				presences[gridY][gridX][i] = -1;
				applyPatch(gridX, gridY, false);
				break;
			}
		}
	}
	
	public void remove(int id) {
		int loc = indexPerso.get(id);
		if (loc != -1) {
			int gridX = loc & 63;
			int gridY = loc >> 6;
			for (int i=0;i<PRESENCE_SIZE;i++) {
				if (presences[gridY][gridX][i] == id) {
					presences[gridY][gridX][i] = -1;
					applyPatch(gridX, gridY, false);
					break;
				}
			}
		}
	}
	
	public static boolean isOutOfBounds(int tx, int ty) {
		return (tx < 0 || ty < 0 || tx >= SIZE_X || ty >= SIZE_Y);
	}
	
	public void applyPatch(int gridX, int gridY, boolean add) {
		// Iterating over the angles, including NULL, which is location itself
		for (Angle a : Angle.values()) {
			Point offset = a.coords;
			int gx = gridX + offset.x;
			int gy = gridY + offset.y;
			if (!isOutOfBounds(gx, gy)) {
				if (add) {
					capillarity[gy][gx]++;
				} else {
					capillarity[gy][gx]--;
				}
			}
		}
	}
}