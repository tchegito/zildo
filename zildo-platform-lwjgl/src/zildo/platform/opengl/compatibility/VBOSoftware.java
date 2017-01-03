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

package zildo.platform.opengl.compatibility;

import org.lwjgl.opengl.GL11;

import zildo.fwk.opengl.compatibility.VBO;
import zildo.fwk.opengl.compatibility.VBOBuffers;

public class VBOSoftware implements VBO {

	
	@Override
	public VBOBuffers create(int p_numPoints, boolean p_forTiles) {
		return new VBOBuffers(p_numPoints, p_forTiles);
	}

	protected void preDraw() {
        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
	}

	/**
	 * Render vertices/textures without indices. So one quad is a double triangle represented by 
	 * 6 vertices and 6 texcoords.
	 */
	@Override
	public void draw(VBOBuffers p_bufs, int start, int count) {
		preDraw();
		GL11.glVertexPointer(2, 0, p_bufs.vertices);
		GL11.glTexCoordPointer(2, 0, p_bufs.textures);
		
		GL11.glDrawArrays(GL11.GL_TRIANGLES, start, count);
	}
	
	@Override
	public void cleanUp(VBOBuffers p_bufs) {
		p_bufs.vertices.clear();
		p_bufs.textures.clear();
	}

	@Override
	public void endInitialization(VBOBuffers p_bufs) {
		if (p_bufs.vertices.position() != 0) {
			// On se repositionne à zéro uniquement si on y est pas déjà
			p_bufs.vertices.flip();
		}
		if (p_bufs.textures.position() != 0) {
			p_bufs.textures.flip();
		}
	}
}
