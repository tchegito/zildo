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

import java.util.HashMap;
import java.util.Map;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import zildo.fwk.opengl.compatibility.VBOBuffers;
import zildo.platform.opengl.GLUtils;

public class VBOHardware extends VBOSoftware {
	
	static Map<Integer, VBOBuffers> vboId = new HashMap<Integer, VBOBuffers>();
	
	@Override
	public VBOBuffers create(int p_numPoints, boolean p_forTiles) {
        
		VBOBuffers bufs=super.create(p_numPoints, p_forTiles);
		bufs.vertexBufferId = GLUtils.createVBO();
		bufs.textureBufferId = GLUtils.createVBO();
		bufs.indiceBufferId = GLUtils.createVBO();
        
        return bufs;
	}
	
	@Override
	public void draw(VBOBuffers p_bufs) {
		preDraw();
		gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, p_bufs.vertexBufferId);
		gl11.glVertexPointer(2, GL10.GL_FLOAT, 0, 0);
		
		gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, p_bufs.textureBufferId);
		gl11.glTexCoordPointer(2, GL10.GL_FLOAT,0, 0);
        
		int count = p_bufs.indices.remaining();
        gl11.glDrawElements(GL11.GL_TRIANGLES, count, GL10.GL_UNSIGNED_SHORT, p_bufs.indices);
		
        gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
	}
	
	@Override
	public void cleanUp(VBOBuffers p_bufs) {
		/*
        IntBuffer buf = BufferUtils.createIntBuffer(1);
        buf.put(p_bufs.vertexBufferId);
        buf.flip();
        ARBVertexBufferObject.glDeleteBuffersARB(buf);
        buf = BufferUtils.createIntBuffer(1);
        buf.put(p_bufs.textureBufferId);
        buf.flip();
        ARBVertexBufferObject.glDeleteBuffersARB(buf);
        buf = BufferUtils.createIntBuffer(1);
        buf.put(p_bufs.indiceBufferId);
        buf.flip();
        ARBVertexBufferObject.glDeleteBuffersARB(buf);		
        */
	}
	
	@Override
	public void endInitialization(VBOBuffers p_bufs) {
		super.endInitialization(p_bufs);
        //GLUtils.bufferData(p_bufs.indiceBufferId, p_bufs.indices, false);		
        GLUtils.bufferData(p_bufs.textureBufferId, p_bufs.textures, false);
        GLUtils.bufferData(p_bufs.vertexBufferId, p_bufs.vertices, false);
	}
}
