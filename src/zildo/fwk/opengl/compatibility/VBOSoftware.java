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
