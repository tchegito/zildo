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

package zildo.fwk.gfx.filter;

import zildo.fwk.gfx.GraphicStuff;
import zildo.monde.util.Pointf;

/**
 * @author Tchegito
 *
 */
public abstract class CloudFilter extends ScreenFilter {

	/**
	 * @param graphicStuff
	 */
	public CloudFilter(GraphicStuff graphicStuff) {
		super(graphicStuff, 256, 256);
	}

	protected float u=0;
	protected float v=0;
	
	protected float offsetU=0;
	protected float offsetV=0;
	protected Pointf wind=new Pointf(0.01f, 0);
	protected Pointf move=new Pointf(0,0);

	double alpha = 0;

	@Override
	public boolean renderFilter() {
		alpha+=0.01f;
		wind=new Pointf(0.25f + (float) (0.3f * Math.sin(alpha) * Math.cos(alpha*3)), 
						0.25f + (float) (0.1f * Math.cos(alpha) * Math.sin(alpha*2)));
		
		return true;
	}
	
	public void setPosition(int x, int y) {
		u = x + offsetU;
		v = -y - offsetV;

		// Blow the wind
		move.add(wind);
		// Avoid too big numbers (fix for Android dirty moves)
		move.x = move.x % textureSizeX;
		move.y = move.y % textureSizeY;
		u+=move.x;
		v+=move.y;
	}
	
	// To handle properly the map transition
	public void addOffset(int x, int y) {
		offsetU += x;
		offsetV += y;
	}
}
