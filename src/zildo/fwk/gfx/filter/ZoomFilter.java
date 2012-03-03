/**
 * Legend of Zildo
 * Copyright (C) 2006-2012 Evariste Boussaton
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

package zildo.fwk.gfx.filter;

import zildo.client.ClientEngineZildo;
import zildo.fwk.gfx.GraphicStuff;
import zildo.monde.sprites.persos.PersoZildo;
import zildo.monde.util.Point;
import zildo.server.EngineZildo;

public abstract class ZoomFilter extends FadeScreenFilter {


	/**
	 * @param graphicStuff
	 */
	public ZoomFilter(GraphicStuff graphicStuff) {
		super(graphicStuff);
	}

	protected void focusOnZildo() {
		// Focus camera on Zildo, and zoom according to the 'fadeLevel'
		PersoZildo zildo=EngineZildo.persoManagement.getZildo();
		Point zildoPos=zildo.getCenteredScreenPosition();
		ClientEngineZildo.openGLGestion.setZoomPosition(zildoPos);
		float z=2.0f * (float) Math.sin(getFadeLevel() * (0.25f*Math.PI / 256.0f));
		ClientEngineZildo.openGLGestion.setZ(z);
		//EngineZildo.getOpenGLGestion().setZ((float) Math.sin(getFadeLevel() * (0.5f*Math.PI / 256.0f)));
	}

}
