/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
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

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.GL11;

import zildo.fwk.opengl.compatibility.VBOBuffers;
import zildo.platform.opengl.GLUtils;

public class VBOHardware extends VBOSoftware {
	
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
		
        ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, p_bufs.vertexBufferId);
        GL11.glVertexPointer(2, GL11.GL_FLOAT, 0, 0);

        ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, p_bufs.textureBufferId);
        GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 0, 0);	

        //ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ELEMENT_ARRAY_BUFFER_ARB, p_bufs.indiceBufferId);

		int count = p_bufs.indices.remaining();
        //GL12.glDrawRangeElements(GL11.GL_TRIANGLES, 0, count, count, GL11.GL_UNSIGNED_SHORT, 0);
        GL11.glDrawElements(GL11.GL_TRIANGLES, p_bufs.indices);
	}

	@Override
	public void draw(VBOBuffers p_bufs, int start, int count) {
		preDraw();
		
		int stride = 4 * 4;
        ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, p_bufs.vertexBufferId);
        GL11.glVertexPointer(2, GL11.GL_FLOAT, stride, 0);
        GL11.glTexCoordPointer(2, GL11.GL_FLOAT, stride, 2 * 4);	

        //GL12.glDrawRangeElements(GL11.GL_TRIANGLES, 0, count, count, GL11.GL_UNSIGNED_SHORT, 0);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, start, count);
	}
	
	@Override
	public void cleanUp(VBOBuffers p_bufs) {
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
	}
	
	boolean done = false;
	
	@Override
	public void endInitialization(VBOBuffers p_bufs) {
		super.endInitialization(p_bufs);
		if (p_bufs.indices != null) {
			GLUtils.bufferData(p_bufs.indiceBufferId, p_bufs.indices, false);
		}
		GLUtils.bufferData(p_bufs.textureBufferId, p_bufs.textures, false);
		GLUtils.bufferData(p_bufs.vertexBufferId, p_bufs.vertices, false);
	}
}
