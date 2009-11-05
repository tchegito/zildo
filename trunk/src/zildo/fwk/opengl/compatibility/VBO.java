package zildo.fwk.opengl.compatibility;


/**
 * 
 *  Vertex Buffer Object : provide data storage (vertex, indices, textures, normals) in the
 *  VRAM to speed up the rendering process.Close from the DirectX mix of Vertex Buffer and Indices Buffer.
 *  
 * Supports two modes:
 * -software
 * -hardware (unherits from software mode)
 * 
 * @author tchegito
 *
 */

public interface VBO {

	public VBOBuffers create(int p_numPoints, int p_numIndices);
	
	public void draw(VBOBuffers p_bufs);
	
	public void cleanUp(VBOBuffers p_bufs);
	
	public void endInitialization(VBOBuffers p_bufs);
}
