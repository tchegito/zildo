/**
 * Legend of Zildo
 * Copyright (C) 2006-2012 Evariste Boussaton
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
	
	public CollBuffer() {
		presences = new int[SIZE_X][SIZE_Y][2];
		indexPerso = new CycleIntBuffer(512);
		
		capillarity = new int[64][64];

		clear();
	}
	
	public void clear() {
		for (int i=0;i<64;i++) {
			for (int j=0;j<64;j++) {
				for (int k=0;k<2;k++) {
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
		int previousLoc = indexPerso.get(id);
		int loc = (gridY << 6) + gridX;
		if (previousLoc != loc) {
			if (previousLoc != -1) {
				// Remove previous location
				int ancGridX = previousLoc & 63;
				int ancGridY = previousLoc >> 6;
				resetId(ancGridX, ancGridY, id);
			}
			setId(gridX, gridY, id);
			indexPerso.set(id, loc);
		}
	}
	
	
	public int getId(int gridX, int gridY, int fromId) {
		int id = presences[gridY][gridX][0];
		if (id == fromId) {
			id = presences[gridY][gridX][1];
		}
		return id;
	}
	
	public static int howManyAround(int gridX, int gridY) {
		if (isOutOfBounds(gridX, gridY)) {
			return 0;
		} else {
			return capillarity[gridY][gridX];
		}
	}
	
	private void setId(int gridX, int gridY, int fromId) {
		int id = presences[gridY][gridX][0];
		if (id != fromId) {
			if (id == -1) {
				presences[gridY][gridX][0] = fromId;
			} else {
				// TODO: and what should we do if this room isn't empty ?
				presences[gridY][gridX][1] = fromId;
			}
			// Increment or decrement capillarity
			applyPatch(gridX, gridY, fromId != -1);
		}
	}
	
	private void resetId(int gridX, int gridY, int fromId) {
		int id = presences[gridY][gridX][0];
		if (id != fromId) {
			presences[gridY][gridX][1] = -1;
		} else {
			presences[gridY][gridX][0] = -1;
		}
		applyPatch(gridX, gridY, false);
	}
	
	public void remove(int id) {
		int loc = indexPerso.get(id);
		if (loc != -1) {
			int gridX = loc & 63;
			int gridY = loc >> 6;
			int a = presences[gridY][gridX][0];
			int b = presences[gridY][gridX][1];
			if (a == id) {
				presences[gridY][gridX][0] = b;
				if (b != -1) {
					presences[gridY][gridX][1] = -1;
				}
			} else {
				presences[gridY][gridX][1] = -1;
			}
			applyPatch(gridX, gridY, false);
		}
	}
	
	public static boolean isOutOfBounds(int tx, int ty) {
		return (tx < 0 || ty < 0 || tx >= SIZE_X || ty >= SIZE_Y);
	}
	
	public void applyPatch(int gridX, int gridY, boolean add) {
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
		if (add) {
			capillarity[gridY][gridX]++;
		} else {
			capillarity[gridY][gridX]--;
		}
	}
}