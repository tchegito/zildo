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

package zeditor.tools.tiles;

/**
 * @author Tchegito
 *
 */
public class GraphChange {

	public final String imageName;
	public final int nTile;
	public final int shiftY;
	public final boolean transparency;
	
	public GraphChange(String p_imageName, int p_nTile, int p_shiftY) {
		this(p_imageName, p_nTile, p_shiftY, false);
	}
	
	public GraphChange(String p_imageName, int p_nTile, int p_shiftY, boolean p_transparency) {
		imageName=p_imageName;
		nTile=p_nTile;
		shiftY=p_shiftY;
		transparency = p_transparency;
	}
}
