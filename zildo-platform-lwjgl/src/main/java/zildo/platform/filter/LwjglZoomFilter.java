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

package zildo.platform.filter;

import org.lwjgl.opengl.GL11;

import zildo.client.ClientEngineZildo;
import zildo.fwk.gfx.GraphicStuff;
import zildo.fwk.gfx.filter.FilterEffect;
import zildo.fwk.gfx.filter.ZoomFilter;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.util.Point;
import zildo.server.EngineZildo;

public class LwjglZoomFilter extends ZoomFilter {

	public LwjglZoomFilter(GraphicStuff graphicStuff) {
		super(graphicStuff);
	}
	
	@Override
	public boolean renderFilter()
	{
		focusOnZildo();
				
		GL11.glDisable(GL11.GL_BLEND);

		return true;
	}
	
	@Override
	protected void focusOnZildo() {
		// Focus camera on Zildo, and zoom according to the 'fadeLevel'
		PersoPlayer zildo=EngineZildo.persoManagement.getZildo();
		Point zildoPos=zildo.getCenteredScreenPosition();
		ClientEngineZildo.openGLGestion.setZoomPosition(zildoPos);
		float z=2.0f * (float) Math.sin(getFadeLevel() * (0.25f*Math.PI / 256.0f));
		ClientEngineZildo.openGLGestion.setZ(z);
		//EngineZildo.getOpenGLGestion().setZ((float) Math.sin(getFadeLevel() * (0.5f*Math.PI / 256.0f)));
	}
	
	/**
	 * Re-initialize z coordinate
	 */
	@Override
	public void doOnInactive(FilterEffect effect) {
		ClientEngineZildo.openGLGestion.setZ(0);
	}	
}
