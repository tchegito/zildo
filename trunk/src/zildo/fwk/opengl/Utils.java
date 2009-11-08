package zildo.fwk.opengl;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.GL11;

/**
 * @author eboussaton
 */
public class Utils {

    public static int generateTexture(int sizeX, int sizeY) {
        IntBuffer buf = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
        GL11.glGenTextures(buf); // Create Texture In OpenGL
        int textureID = buf.get(0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, adjustTexSize(sizeX), adjustTexSize(sizeY), 0, GL11.GL_RGBA,
                GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);

        return textureID;
    }

    public static void cleanTexture(int id) {
        GL11.glDeleteTextures(getBufferWithId(id));
    }

    public static int createVBO() {
        IntBuffer buffer = BufferUtils.createIntBuffer(1);
        ARBVertexBufferObject.glGenBuffersARB(buffer);
        return buffer.get();    	
    }
    

    // Vertex & Indices Buffer
    public static void bufferData(int id, Buffer buffer) {
        ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, id);
        if (buffer instanceof FloatBuffer) {
            ARBVertexBufferObject.glBufferDataARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, (FloatBuffer) buffer,
                    ARBVertexBufferObject.GL_STREAM_DRAW_ARB);
        } else if (buffer instanceof IntBuffer) {
            ARBVertexBufferObject.glBufferDataARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, (IntBuffer) buffer,
                    ARBVertexBufferObject.GL_STREAM_DRAW_ARB);
        }
    }
    
    /**
     * OpenGL likes "adjusted" size for texture. We take multiple of 256.
     * @param n Initial size
     * @return Adjusted size
     */
    static public int adjustTexSize(int n) {
        if (n % 256 == 0) {
            return n;
        }
        return (n & 0xff00) + 256;
    }

    public static IntBuffer getBufferWithId(int id) {
        IntBuffer buf = BufferUtils.createIntBuffer(1);
        buf.put(id);
        buf.rewind();
        return buf;
    }

    public static void copyScreenToTexture(int p_texId) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, p_texId);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, 0, 0, 1024, 512, 0);
    }
}