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

package zildo.platform.opengl.compatibility;

import javax.microedition.khronos.opengles.GL11;

import zildo.fwk.opengl.compatibility.VBO;
import zildo.fwk.opengl.compatibility.VBOBuffers;
import zildo.platform.opengl.AndroidOpenGLGestion;

public class VBOSoftware implements VBO {

	GL11 gl11;
	
	@Override
	public VBOBuffers create(int p_numPoints, boolean p_forTiles) {
		return new VBOBuffers(p_numPoints, p_forTiles);
	}

	protected void preDraw() {
		if (gl11 == null) {	// get the GL instance
			gl11 = (GL11) AndroidOpenGLGestion.gl10;
		}
		gl11.glEnable(GL11.GL_TEXTURE_2D);
		gl11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		gl11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
	}
	
	@Override
	public void draw(VBOBuffers p_bufs) {
		preDraw();
		gl11.glVertexPointer(2, GL11.GL_FLOAT, 0, p_bufs.vertices);
		gl11.glTexCoordPointer(2, GL11.GL_FLOAT, 0, p_bufs.textures);
		
		int count = p_bufs.indices.remaining();
		gl11.glDrawElements(GL11.GL_TRIANGLES, count, GL11.GL_UNSIGNED_SHORT, p_bufs.indices);
		
		gl11.glDisable(GL11.GL_TEXTURE_2D);
		gl11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
		gl11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
	}		

	@Override
	public void draw(VBOBuffers p_bufs, int start, int count) {
		preDraw();
		gl11.glVertexPointer(2, GL11.GL_FLOAT, 0, p_bufs.vertices);
		gl11.glTexCoordPointer(2, GL11.GL_FLOAT, 0, p_bufs.textures);
		
		gl11.glDrawArrays(GL11.GL_TRIANGLES, start, count);
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
		if (p_bufs.indices != null && p_bufs.indices.position() != 0) {
			p_bufs.indices.flip();
		}
	}
}
