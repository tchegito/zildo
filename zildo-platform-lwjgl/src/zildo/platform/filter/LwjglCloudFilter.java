/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
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
import zildo.fwk.gfx.filter.CloudFilter;
import zildo.monde.sprites.Reverse;

/**
 * @author Tchegito
 * 
 */
public class LwjglCloudFilter extends CloudFilter {

	public LwjglCloudFilter(GraphicStuff graphicStuff) {
		super(graphicStuff);
	}
	
	@Override
	public boolean renderFilter() {
		// Make the wind blow
		super.renderFilter();
		
		super.startInitialization();
		updateQuad(0, 0, u, v, Reverse.NOTHING);
		this.endInitialization();

		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		GL11.glTranslatef(0, -sizeY, 1);

		/*
		float colorFactor = 0.4f;
		float transparency = 0.4f + (float) (0.2f * Math.cos(alpha));
		GL11.glColor4f(transparency, transparency, transparency, 0.4f);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_ONE_MINUS_SRC_COLOR, GL11.GL_SRC_ALPHA); // ONE_MINUS_SRC_COLOR);
*/
		float colorFactor=0.2f;
		GL11.glColor4f(colorFactor, colorFactor, colorFactor, 0.1f);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_ZERO, GL11.GL_ONE_MINUS_SRC_COLOR);

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, ClientEngineZildo.tileEngine.texCloudId);
		super.render();

		GL11.glDisable(GL11.GL_BLEND);

		GL11.glPopMatrix();

		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		return true;
	}
}
