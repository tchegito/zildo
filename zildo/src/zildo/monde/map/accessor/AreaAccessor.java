/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
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

package zildo.monde.map.accessor;

import zildo.monde.map.Area;
import zildo.monde.map.Case;

/**
 * Really simple class to allow different way to read a map. Useful for ZEditor, and for game too, to
 * avoid too many check with "if/then/else" at each case read.
 * 
 * @author Tchegito
 *
 */
public abstract class AreaAccessor {

	public Area area;
	
	int dim_x;
	int dim_y;
	
	public void setArea(Area area) {
		this.area = area;
		dim_x = area.getDim_x();
		dim_y = area.getDim_y();
	}
	
	public abstract Case get_mapcase(int x, int y);
}
