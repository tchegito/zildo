package zildo.fwk.gfx;

import org.lwjgl.opengl.GL11;

import zildo.fwk.opengl.OpenGLStuff;
import zildo.fwk.opengl.compatibility.VBOBuffers;

/**
 * Class describing the TileEngine main element :
 * -set of vertices
 * -set of indices
 * -set of normals (all the same)
 * -set of textures coordinates
 * 
 * @author tchegito
 *
 */

public class TilePrimitive extends OpenGLStuff {

    // Class variables
    protected int nPoints;
    protected int nIndices;
    private boolean isLock;

    protected VBOBuffers bufs;
    
    private int textureSizeX = 256;
    private int textureSizeY = 256;

    // ////////////////////////////////////////////////////////////////////
    // Construction/Destruction
    // ////////////////////////////////////////////////////////////////////

    public TilePrimitive() { // Should never been called
        nPoints = 0;
        nIndices = 0;
    }

    public TilePrimitive(int numPoints, int numIndices) {
        initialize(numPoints, numIndices);
    }

    private void initialize(int numPoints, int numIndices) {
        // Initialize VBO IDs
        bufs=vbo.create(numPoints, numIndices);
        
        nPoints = 0;
        nIndices = 0;

        // Generate all indices at primitve instanciation (it never change)
        generateAllIndices(numIndices);
    }

    public TilePrimitive(int numPoints, int numIndices, int texSizeX, int texSizeY) {
        textureSizeX = texSizeX;
        textureSizeY = texSizeY;
        initialize(numPoints, numIndices);
    }

    public void cleanUp() {
       	vbo.cleanUp(bufs);
    }

    // /////////////////////////////////////////////////////////////////////////////////////
    // startInitialization
    // /////////////////////////////////////////////////////////////////////////////////////
    // Lock VertexBuffer to gain access to data
    // /////////////////////////////////////////////////////////////////////////////////////
    public void startInitialization() {

    }

    // /////////////////////////////////////////////////////////////////////////////////////
    // endInitialization
    // /////////////////////////////////////////////////////////////////////////////////////
    public void endInitialization() {
        vbo.endInitialization(bufs);
    }

    // /////////////////////////////////////////////////////////////////////////////////////
    // renderPartial
    // /////////////////////////////////////////////////////////////////////////////////////
    // IN : startingQuad, number of quads to render
    // /////////////////////////////////////////////////////////////////////////////////////
    // Ask OpenGL to render quad from this mesh, from a position to another
    // /////////////////////////////////////////////////////////////////////////////////////
    void renderPartial(int startingQuad, int nbQuadsToRender) {
        int position = bufs.indices.position();
        int saveNIndices = nIndices;
        int limit = bufs.indices.limit();

        bufs.indices.position(startingQuad * 6);
        nIndices = nbQuadsToRender * 6 + startingQuad * 6;
        render();
        bufs.indices.position(position);
        nIndices = saveNIndices;
        bufs.indices.limit(limit);

        /*
         * ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ELEMENT_ARRAY_BUFFER_ARB, indiceBufferId);
         * GL12.glDrawRangeElements(GL11.GL_TRIANGLES, startingQuad * 2, 2*(nbQuadsToRender + startingQuad) -1, nIndices * 3,
         * GL11.GL_UNSIGNED_INT, 0);
         */
    }

    // /////////////////////////////////////////////////////////////////////////////////////
    // render
    // /////////////////////////////////////////////////////////////////////////////////////
    // Ask OpenGL to render every quad from this mesh
    // /////////////////////////////////////////////////////////////////////////////////////
    public void render() {

        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
        GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

        vbo.draw(bufs);

        // Le buffer d'indices contient les indices pour 4096 tiles. On doit le limiter au nombre de tiles
        // réellement utilisé.
        bufs.indices.limit(nIndices);
        GL11.glDrawElements(GL11.GL_TRIANGLES, bufs.indices);

    }

    /**
     * Add standard tile : 16x16
     * @return position in bufs.vertices buffer for the added tile's first vertex
     */
    public int addTile(int x, int y, float u, float v) {
        int nTileToReturn = nPoints;
        addTileSized(x, y, u, v, 16, 16);
        return nTileToReturn;
    }

    private void putTileSized(float x, float y, float sizeX, float sizeY, float xTex, float yTex) {
        // 4 bufs.vertices
        if (bufs.vertices.position() == bufs.vertices.limit()) {
            // On rajoute une place
            bufs.vertices.limit(bufs.vertices.position() + 3 * 4);
            bufs.textures.limit(bufs.textures.position() + 2 * 4);
        }
        float pixSizeX=Math.abs(sizeX);
        float pixSizeY=Math.abs(sizeY);
        float texStartX=xTex;
        float texStartY=yTex;
        if (sizeX < 0) {
        	texStartX-=sizeX;
        }
        if (sizeY < 0) {
        	texStartY-=sizeY;
        }
        for (int i = 0; i < 4; i++) {
            bufs.vertices.put(x + pixSizeX * (i % 2)); // x
            bufs.vertices.put(y + pixSizeY * (i / 2)); // y
            bufs.vertices.put(0.0f); // z

            // Get right tile-texture
            float texPosX = texStartX + sizeX * (i % 2);
            float texPosY = texStartY + sizeY * (i / 2);

            bufs.textures.put(texPosX / textureSizeX);
            bufs.textures.put(texPosY / textureSizeY);
        }
    }

    // Return the quad position in Vertex Buffer
    protected int addTileSized(int x, int y, float xTex, float yTex, int sizeX, int sizeY) {
        putTileSized(x, y, sizeX, sizeY, xTex, yTex);

        nPoints += 4;
        nIndices += 6;

        return nPoints - 4;
    }

    public boolean isLock() {
        return isLock;
    }

    // Move a tile and reset its texture (don't change size)
    // **IMPORTANT** : VertexBuffer MUST BE locked
    public void updateTile(float x, float y, float u, float v) {
        // Get size
        int vBufferPos = bufs.vertices.position(); // - 3*4;
        // int tBufferPos=bufs.textures.position(); // - 2*4;

        float sizeX = bufs.vertices.get(vBufferPos + 3) - bufs.vertices.get(vBufferPos);
        float sizeY = bufs.vertices.get(vBufferPos + 3 * 2 + 1) - bufs.vertices.get(vBufferPos + 1);

        // Move tile
        putTileSized(x, y, sizeX, sizeY, u, v);

    }

    void removeTile(int numTile) {
    }

    // Generate indices for maximum tile authorized.
    // We can do this once for all, because every tiles is a quad made by 2 triangles
    // where indices are like this :
    // (v1,v2,v3) - (v2,v4,v3)
    void generateAllIndices(int numIndices) {
        // 3 Indices
        for (int i = 0; i < (numIndices / 6); i++) {
            // Tile's first triangle
            bufs.indices.put(i * 4).put(i * 4 + 1).put(i * 4 + 2);
            // Tile's second triangle
            bufs.indices.put(i * 4 + 1).put(i * 4 + 3).put(i * 4 + 2);

            // Two bufs.normals oriented accross the screen (0,0,-1)
            bufs.normals.put(0).put(i).put(1);
            bufs.normals.put(0).put(0).put(1);
        }
    }

    public int getNPoints() {
        return nPoints;
    }

    public void setNPoints(int points) {
        nPoints = points;
    }

    public int getNIndices() {
        return nIndices;
    }

    public void setNIndices(int indices) {
        nIndices = indices;
    }

    /*
     * void translate(float diffx, float diffy) { if (nPoints>0) { startInitialization(); for (int i=0;i<nPoints;i++) { if (pVertex!=null) {
     * pVertex.x+=diffx; pVertex.y+=diffy; } pVertex++; } endInitialization(); } }
     */
}