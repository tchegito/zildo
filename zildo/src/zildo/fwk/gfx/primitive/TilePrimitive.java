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
import zildo.monde.sprites.Reverse;

/**
 * Class describing the TileEngine main element :<br/>
 * <ul>
 * <li>set of vertices</li>
 * <li>set of indices</li>
 * <li>set of normals (all the same)</li>
 * <li>set of textures coordinates</li>
 * </ul>
 * @author tchegito
 */

public class TilePrimitive extends QuadPrimitive {


    // ////////////////////////////////////////////////////////////////////
    // Construction/Destruction
    // ////////////////////////////////////////////////////////////////////
    public TilePrimitive(int numPoints) {
    	super(numPoints);
    }

    @Override
	protected void initialize(int numPoints) {
        // Initialize VBO IDs
    	vbo = Zildo.pdPlugin.gfxStuff.createVBO();
        bufs=vbo.create(numPoints, true);
        
        nPoints = 0;
        nIndices = 0;

        // Generate all indices at primitve instanciation (it never change)
        generateAllVertices();
    }

    @Override
	protected boolean isTiles() {
    	return true;
    }

    /**
     * Add standard tile : 16x16
     * @return position in bufs.vertices buffer for the added tile's first vertex
     */
    public void addTile(int x, int y, float u, float v) {
        addTile(x, y, u, v, 16, 16);
    }



    /**
     * Add a tile, considering that we have a unique vertices buffer, with the full grid.<br/>
     * We just have to append in <b>indice</b> and <b>texture</b> buffers.
     * @param x
     * @param y
     * @param xTex
     * @param yTex
     * @param sizeX (16 or -16)
     * @param sizeY (16 or -16)
     */
    private void addTile(int x, int y, float xTex, float yTex, int sizeX, int sizeY) {

        // Find the right vertices
        int indexVertices =  x / 4 + (y / 16) * 64 * 4;
    	bufs.indices.put((short) indexVertices).put((short) (indexVertices+1)).put((short) (indexVertices+2));
    	bufs.indices.put((short) (indexVertices+1)).put((short) (indexVertices+3)).put((short) (indexVertices+2));
    	bufs.textures.position(indexVertices*2);
        
        putTexture(xTex, yTex, sizeX, sizeY);
        
        nPoints += 4;
        nIndices += 6;
    }


    /**
     * Update a tile's texture (don't change size or location)<br/>
     * {@link #startInitialization()} should be called first.
     * @param x
     * @param y
     * @param u
     * @param v
     * @param reverse reverse attribute (horizonta and/or vertical)
     */
    public void updateTile(int x, int y, float u, float v, Reverse reverse) {
        int sizeX = 16;
        int sizeY = 16;

		int revX = reverse.isHorizontal() ? -1 : 1;
		int revY = reverse.isVertical() ? -1 : 1;
		
        // Move tile
        addTile(x, y, u, v, sizeX * revX, sizeY * revY );
    }
    
    /**
     * Generate the complete grid of 64x64 tiles : each one has 4 vertices.
     */
    void generateAllVertices() {
    	bufs.vertices.position(0);
    	float x = 0;
    	float y = 0;
    	int numVertices = bufs.vertices.limit() / 2 / 4;
    	for (int i = 0; i < numVertices; i++) {
    		bufs.vertices.put(x).put(y);
    		bufs.vertices.put(x + 16).put(y);
    		bufs.vertices.put(x).put(y + 16);
    		bufs.vertices.put(x + 16).put(y + 16);
    		x += 16;
    		if (x == 64*16) {
    			x = 0;
    			y += 16;
    		}
    	}
    }
    
    void skipTile() {
    	bufs.indices.position(bufs.indices.position() + 6);
    	bufs.textures.position(bufs.textures.position() + 8);
        nPoints += 4;
        nIndices += 6;
    }

}