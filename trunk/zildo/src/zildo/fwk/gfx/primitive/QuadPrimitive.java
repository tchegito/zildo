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

package zildo.fwk.gfx.primitive;

import zildo.Zildo;
import zildo.fwk.opengl.compatibility.VBO;
import zildo.fwk.opengl.compatibility.VBOBuffers;
import zildo.monde.sprites.Reverse;

/**
 * @author Tchegito
 *
 */
public class QuadPrimitive {

    // Class variables
    protected int nPoints;
    protected int nIndices;
    
    private boolean isLock;

    protected VBOBuffers bufs;
    protected VBO vbo;
    
    private int textureSizeX = 256;
    private int textureSizeY = 256;

    public QuadPrimitive(int numPoints) {
    	initialize(numPoints);
    }
    
    public QuadPrimitive(int numPoints, int texSizeX, int texSizeY) {
        textureSizeX = texSizeX;
        textureSizeY = texSizeY;
        initialize(numPoints);
    }
    
    protected void initialize(int numPoints) {
        // Initialize VBO IDs
    	vbo = Zildo.pdPlugin.gfxStuff.createVBO();
        bufs=vbo.create(numPoints, false);
        
        nPoints = 0;
        nIndices = 0;

        // Generate all indices at primitve instanciation (it never change)
       	generateAllIndices();
    }
    
    protected boolean isTiles() {
    	return false;
    }
    
    // /////////////////////////////////////////////////////////////////////////////////////
    // startInitialization
    // /////////////////////////////////////////////////////////////////////////////////////
    // Lock VertexBuffer to gain access to data
    // /////////////////////////////////////////////////////////////////////////////////////
    public void startInitialization() {
    	nPoints = 0;
    	nIndices = 0;
    	if (isTiles()) {
    		// Reinit indices and textures buffer
    		bufs.indices.position(0);
    		bufs.textures.limit(bufs.textures.capacity());
    		bufs.indices.limit(bufs.indices.capacity());
    	}
    }

    // /////////////////////////////////////////////////////////////////////////////////////
    // endInitialization
    // /////////////////////////////////////////////////////////////////////////////////////
    public void endInitialization() {
        vbo.endInitialization(bufs);
    }
    
    public void cleanUp() {
       	vbo.cleanUp(bufs);
        nPoints = 0;
        nIndices = 0;
    }
    
    // Generate indices for maximum tile authorized.
    // We can do this once for all, because every tiles is a quad made by 2 triangles
    // where indices are like this :
    // (v1,v2,v3) - (v2,v4,v3)
    void generateAllIndices() {
    	/*
    	if (bufs.indices.position() > 0) {
    		return;	// Already generated
    	}
    	*/
    	int numIndices=bufs.indices.limit();
        // 3 Indices
        for (int i = 0; i < numIndices / 6; i++) {
            // Tile's first triangle
            bufs.indices.put((short) (i * 4)).put((short) (i * 4 + 1)).put((short) (i * 4 + 2));
            // Tile's second triangle
            bufs.indices.put((short) (i * 4 + 1)).put((short) (i * 4 + 3)).put((short) (i * 4 + 2));
        }
        
    }
    
    
    public boolean isLock() {
        return isLock;
    }

    /**
     *  Move a tile and reset its texture (don't change size)<br/>
     * {@link #startInitialization()} should be called first.
     * @param x
     * @param y
     * @param u
     * @param v
     * @param reverse reverse attribute (horizonta and/or vertical)
     */
    public void updateQuad(float x, float y, float u, float v, Reverse reverse) {
        // Get size
        int vBufferPos = bufs.vertices.position(); // - 3*4;
		int tBufferPos = bufs.textures.position(); // - 2*4;

        if (bufs.vertices.limit() <= vBufferPos) {
            // On rajoute une place
            bufs.vertices.limit(vBufferPos + 2 * 4);
        }
		if (bufs.textures.limit() <= tBufferPos) {
			bufs.textures.limit(tBufferPos + 2 * 4);
		}
        
        float sizeX = bufs.vertices.get(vBufferPos + 2) - bufs.vertices.get(vBufferPos);
        float sizeY = bufs.vertices.get(vBufferPos + 2 * 2 + 1) - bufs.vertices.get(vBufferPos + 1);

        if (sizeX == 0) {
        	sizeX=16;
        	sizeY=16;
        }
		
		int revX = reverse.isHorizontal() ? -1 : 1;
		int revY = reverse.isVertical() ? -1 : 1;
		
        // Move tile
        putQuadSized(x, y, sizeX * revX, sizeY * revY, u, v);

    }
    

    protected void putTexture(float xTex, float yTex, float sizeX, float sizeY) {
        float texStartX=xTex;
        float texStartY=yTex;
        if (sizeX < 0) {
        	texStartX-=sizeX;
        }
        if (sizeY < 0) {
        	texStartY-=sizeY;
        }
        for (int i = 0; i < 4; i++) {
            // Get right tile-texture
            float texPosX = texStartX + sizeX * (i % 2);
            float texPosY = texStartY + sizeY * (i / 2);

            bufs.textures.put(texPosX / textureSizeX);
            bufs.textures.put(texPosY / textureSizeY);
        }
    }
    
    private void putQuadSized(float x, float y, float sizeX, float sizeY, float xTex, float yTex) {
    	
        // 4 bufs.vertices
        if (bufs.vertices.position() == bufs.vertices.limit()) {
            // On rajoute une place
            bufs.vertices.limit(bufs.vertices.position() + 2 * 4);
            bufs.textures.limit(bufs.textures.position() + 2 * 4);
        }
        float pixSizeX=Math.abs(sizeX);
        float pixSizeY=Math.abs(sizeY);
        putTexture(xTex, yTex, sizeX, sizeY);
        
        for (int i = 0; i < 4; i++) {
            bufs.vertices.put(x + pixSizeX * (i % 2)); // x
            bufs.vertices.put(y + pixSizeY * (i / 2)); // y
        }

        nPoints += 4;
        nIndices += 6;
    }

    // Return the quad position in Vertex Buffer
    protected int addQuadSized(int x, int y, float xTex, float yTex, int sizeX, int sizeY) {
        putQuadSized(x, y, sizeX, sizeY, xTex, yTex);

        return nPoints - 4;
    }

    /**
     * Ask OpenGL to render quad from this mesh, from a position to another
     * @param startingQuad starting quad
     * @param nbQuadsToRender number of quads to render
     */
    void renderPartial(int startingQuad, int nbQuadsToRender) {
        int position = bufs.indices.position();
        int saveNIndices = nIndices;
        int limit = bufs.indices.limit();

        //bufs.indices.limit(startingQuad * 6 + nbQuadsToRender * 6);
        bufs.indices.position(startingQuad * 6);
        nIndices = nbQuadsToRender * 6 + startingQuad * 6;
        render();
        bufs.indices.position(position);
        nIndices = saveNIndices;
        bufs.indices.limit(limit);
    }
    
    /**
     * Ask OpenGL to render every quad from this mesh.
     */
    public void render() {

        // Indices buffer contains indices for 4096 tiles. We have to limit it to the real number of used tiles.
        bufs.indices.limit(nIndices);

        vbo.draw(bufs);

    }
    
    public boolean isEmpty() {
        return nPoints == 0;
    }
}
