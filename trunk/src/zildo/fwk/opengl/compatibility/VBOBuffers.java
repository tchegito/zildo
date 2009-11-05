package zildo.fwk.opengl.compatibility;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

public class VBOBuffers {
	
	int vertexBufferId;
	int	normalBufferId;
	int textureBufferId;
	int indiceBufferId;

    public FloatBuffer vertices;
    public FloatBuffer normals;
    public FloatBuffer textures;
    public IntBuffer indices;
    
    public VBOBuffers(int p_numPoints, int p_numIndices) {
        // Allocate buffers
        int numFaces = p_numIndices / 3;
        vertices = BufferUtils.createFloatBuffer(3 * p_numPoints);
        normals = BufferUtils.createFloatBuffer(3 * numFaces);
        indices = BufferUtils.createIntBuffer(3 * p_numIndices);
        textures = BufferUtils.createFloatBuffer(2 * p_numPoints);    	
    }
}
