/**
 * Legend of Zildo
 * Copyright (C) 2006-2011 Evariste Boussaton
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

import org.lwjgl.opengl.GL11;

import zildo.client.ClientEngineZildo;
import zildo.monde.sprites.Reverse;
import zildo.monde.util.Pointf;

/**
 * @author Tchegito
 *
 */
public class CloudFilter extends ScreenFilter {

	static float u=0;
	static float v=0;
	static Pointf wind=new Pointf(0.01f, 0);
	static Pointf move=new Pointf(0,0);
	
	@Override
	public boolean renderFilter() {
		
		super.startInitialization();
		updateTile(0, 0, u, v, Reverse.NOTHING);
		this.endInitialization();
		
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
    	GL11.glLoadIdentity();
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		GL11.glTranslatef(0,-sizeY,1);

		GL11.glColor3f(1.0f, 1.0f, 1.0f);
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

	public static void setPosition(int x, int y) {
		u = x;
		v = -y;
		
		// Blow the wind
		move.add(wind);
		u+=move.x;
		v+=move.y;
	}
}
