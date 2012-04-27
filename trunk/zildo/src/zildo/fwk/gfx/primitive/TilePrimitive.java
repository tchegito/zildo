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
import zildo.fwk.CycleIntBuffer;
import zildo.monde.sprites.Reverse;
import zildo.monde.util.Point;

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


	int startCamera;
	int endCamera;
	
	int indexBuffer[][];

	CycleIntBuffer freeIndex;
	//int freeIndex[];
	//int freeCount;
	CycleIntBuffer displayed;
	int bufSize;
	//int displayed[];
	//int displayCount;
	
    // ////////////////////////////////////////////////////////////////////
    // Construction/Destruction
    // ////////////////////////////////////////////////////////////////////
    public TilePrimitive(int numPoints) {
    	super(numPoints);
    	
    	indexBuffer = new int[200][200];

    	bufSize = 512;
    	int nbTiles = numPoints / 8;
    	while (nbTiles > bufSize) {
    		bufSize+= 256;
    	}
    	freeIndex = new CycleIntBuffer(bufSize);
    	displayed = new CycleIntBuffer(bufSize);

    	clearBuffers();
    }

    @Override
	public void startInitialization() {
    	super.startInitialization();
    	// Reinit indices and textures buffer
    	bufs.textures.position(0);
    	bufs.vertices.position(0);
    	bufs.vertices.limit(bufs.vertices.capacity());
    	bufs.textures.limit(bufs.textures.capacity());
    	
    	startCamera = 6*16;
    	endCamera = 0;
    }
    
    @Override
	protected void initialize(int numPoints) {
        // Initialize VBO IDs
    	vbo = Zildo.pdPlugin.gfxStuff.createVBO();
        bufs=vbo.create(numPoints, true);
        
        nPoints = 0;
        nIndices = 0;
    }

    @Override
	protected boolean isTiles() {
    	return true;
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

    	addSprite(x, y, xTex, yTex, sizeX, sizeY);
    	
        // Get the highest indices
        if (nIndices-6 < startCamera) {
        	startCamera = nIndices-6;
        }
    	if (nIndices > endCamera) {
    		endCamera = nIndices;
    	}
    }


    /**
     * Update a tile's texture (don't change size or location)<br/>
     * {@link #startInitialization()} should be called first.
     * @param gridX 0..64
     * @param gridY 0..64
     * @param u
     * @param v
     * @param reverse reverse attribute (horizontal and/or vertical)
     */
    public void updateTile(int gridX, int gridY, float u, float v, Reverse reverse, boolean changed) {
        int sizeX = 16;
        int sizeY = 16;

		int revX = reverse.isHorizontal() ? -1 : 1;
		int revY = reverse.isVertical() ? -1 : 1;
		
		// Locate at the right position in vertex/texture buffers
		boolean tileReused = reuseIndex(gridX, gridY);
		
		if (changed || !tileReused) {
			// Move tile
			addTile(gridX << 4, gridY << 4, u, v, sizeX * revX, sizeY * revY );
		} else {
			skipTile(gridX, gridY);
		}
    }
    
    void fillFreeIndex(Point camera) {
		int tileStartX = camera.x >> 4;
		int tileStartY = camera.y >> 4;
		int tileEndX = tileStartX + (Zildo.viewPortX >> 4);
		int tileEndY = tileStartY + (Zildo.viewPortY >> 4);
    	for (int i=0;i<displayed.length();i++) {
    		int disp = displayed.get(i);
    		if (disp == -1) {
    			continue;
    		}
    		int gridX = disp & 255;
    		int gridY = disp >> 8;
    		boolean out = false;
    		if (gridX < tileStartX || gridX > (tileEndX + 1)) {
    			out = true;
    		}
    		if (gridY < tileStartY || gridY > (tileEndY + 1)) {
    			out = true;
    		}
    		if (out) {
				// Out of the map !
        		int index = getIndexBuffer(gridX, gridY);
				
				// Add it to the free index buffer
				freeIndex.lookForEmpty();
				freeIndex.push(index);
				// Notify that it is no longer displayed
				displayed.set(i, -1);
				setIndexBuffer(gridX, gridY, -1);
    		}
    	}
    	freeIndex.rewind();
    	
    }
    boolean reuseIndex(int gridX, int gridY) {
    	int index = getIndexBuffer(gridX, gridY);
    	boolean reused = true;
    	if (index != -1) { // Tile is already present
    		nIndices = index;
    	} else {
    		// New tile to render
    		nIndices = getFreePosition();
    		setIndexBuffer(gridX, gridY, nIndices);
    		// Look for a value
    		displayed.lookForEmpty();
    		displayed.push((((gridY+64)%64) << 8) + (gridX+64) % 64);
    		// Notify that tile is new
    		reused=false;
    	}
    	bufs.vertices.position(nIndices * 2);
		bufs.textures.position(nIndices * 2);
		return reused;
    }
    
    int getFreePosition() {
    	int val = freeIndex.pop();
    	if (val == -1) {
    		throw new RuntimeException("r");
    		//return displayed.getNbValues() * 6; //endCamera;
    	} else {
    		return val;
    	}
    }
    
    public int getIndexBuffer(int gridX, int gridY) {
    	return indexBuffer[(gridY + 64) % 64][(gridX + 64) % 64];
    }
    
    public void setIndexBuffer(int gridX, int gridY, int value) {
    	indexBuffer[(gridY + 64) % 64][(gridX + 64) % 64] = value;
    }

    public void clearBuffers() {
    	for (int i=0;i<indexBuffer.length;i++) {
        	for (int j=0;j<indexBuffer.length;j++) {
        		indexBuffer[i][j] = -1;
        	}
    	}
    	for (int i=0;i<bufSize;i++) {
    		freeIndex.set(i, i * 6);
    	}
    	displayed.init(-1);

    }
    
    void skipTile(int gridX, int gridY) {
        
        nPoints += 4;
        nIndices += 6;

        // Get the highest indices
        if ((nIndices-6) < startCamera) {
        	startCamera = nIndices-6;
        }
    	if (nIndices > endCamera) {
    		endCamera = nIndices;
    	}
    }
    
    /**
     * Ask OpenGL to render every quad from this mesh.
     */
    @Override
	public void render() {

        // Indices buffer contains indices for 4096 tiles. We have to limit it to the real number of used tiles.
        vbo.draw(bufs, startCamera, endCamera);
    }
}