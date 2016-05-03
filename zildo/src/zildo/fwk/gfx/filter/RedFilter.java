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

import zildo.Zildo;
import zildo.client.ClientEngineZildo;
import zildo.fwk.gfx.GraphicStuff;
import zildo.monde.util.Vector3f;
import zildo.monde.util.Vector4f;

/**
 * @author Tchegito
 *
 */
public class RedFilter extends ScreenFilter {

	/**
	 * @param graphicStuff
	 */
	public RedFilter(GraphicStuff graphicStuff) {
		super(graphicStuff);
	}

	@Override
	public void preFilter() {
		Vector3f v = ClientEngineZildo.ortho.getAmbientColor();
		ClientEngineZildo.ortho.box(0, 0, Zildo.viewPortX, Zildo.viewPortY, 0, new Vector4f(1*v.x, 0, 0, 1));
	}

	@Override
	public boolean renderFilter() {
		return true;
	}

}
