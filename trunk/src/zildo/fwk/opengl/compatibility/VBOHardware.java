package zildo.fwk.opengl.compatibility;

import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.GL11;

import zildo.fwk.opengl.Utils;

public class VBOHardware extends VBOSoftware {
	
	static Map<Integer, VBOBuffers> vboId = new HashMap<Integer, VBOBuffers>();
	
	public VBOBuffers create(int p_numPoints, int p_numIndices) {
        
		VBOBuffers bufs=super.create(p_numPoints, p_numIndices);
		bufs.vertexBufferId = Utils.createVBO();
		bufs.normalBufferId = Utils.createVBO();
		bufs.textureBufferId = Utils.createVBO();
		bufs.indiceBufferId = Utils.createVBO();
        
        return bufs;
	}
	
	public void draw(VBOBuffers p_bufs) {
        ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, p_bufs.vertexBufferId);
        GL11.glVertexPointer(3, GL11.GL_FLOAT, 0, 0);

        ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, p_bufs.normalBufferId);
        GL11.glNormalPointer(GL11.GL_FLOAT, 0, 0);

        ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, p_bufs.textureBufferId);
        GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 0, 0);		
	}
	
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
        buf.put(p_bufs.normalBufferId);
        buf.flip();
        ARBVertexBufferObject.glDeleteBuffersARB(buf);
        buf = BufferUtils.createIntBuffer(1);
        buf.put(p_bufs.indiceBufferId);
        buf.flip();
        ARBVertexBufferObject.glDeleteBuffersARB(buf);		
	}
	
	public void endInitialization(VBOBuffers p_bufs) {
		super.endInitialization(p_bufs);
        Utils.bufferData(p_bufs.vertexBufferId, p_bufs.vertices);
        Utils.bufferData(p_bufs.normalBufferId, p_bufs.normals);
        Utils.bufferData(p_bufs.textureBufferId, p_bufs.textures);
        Utils.bufferData(p_bufs.indiceBufferId, p_bufs.indices);		
	}
}
