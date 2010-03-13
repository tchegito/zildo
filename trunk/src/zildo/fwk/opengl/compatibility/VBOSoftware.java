/**
 * Legend of Zildo
 * Copyright (C) 2006-2010 Evariste Boussaton
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

package zildo.fwk.opengl.compatibility;

import org.lwjgl.opengl.GL11;

public class VBOSoftware implements VBO {

	public VBOBuffers create(int p_numPoints, int p_numIndices) {
		return  new VBOBuffers(p_numPoints, p_numIndices);
	}

	public void draw(VBOBuffers p_bufs) {
        GL11.glVertexPointer(3, 0, p_bufs.vertices);
        GL11.glNormalPointer(0, p_bufs.normals);
        GL11.glTexCoordPointer(2, 0, p_bufs.textures);		
	}
	
	public void cleanUp(VBOBuffers p_bufs) {
		p_bufs.vertices.clear();
		p_bufs.textures.clear();
	}
	
	public void endInitialization(VBOBuffers p_bufs) {
        if (p_bufs.vertices.position() != 0) {
            // On se repositionne à zéro uniquement si on y est pas déjà
        	p_bufs.vertices.flip();
        }
        if (p_bufs.normals.position() != 0) {
        	p_bufs.normals.flip();
        }
        if (p_bufs.textures.position() != 0) {
        	p_bufs.textures.flip();
        }
        if (p_bufs.indices.position() != 0) {
        	p_bufs.indices.flip();
        }		
	}
}
