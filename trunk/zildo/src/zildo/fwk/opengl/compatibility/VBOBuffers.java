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

package zildo.fwk.opengl.compatibility;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import zildo.fwk.ZUtils;

public class VBOBuffers {
	
	// As indices never changes, we just fix a maximum and use the generated array at start.
	static final int maxIndices = 2 * 6 * 64 * 64;
	
	public int vertexBufferId;
	//public int	normalBufferId;
	public int textureBufferId;
	public int indiceBufferId;

    public FloatBuffer vertices;
    public FloatBuffer textures;
    public ShortBuffer indices;
    
    static ShortBuffer tileIndices;		// Same indice buffer for all tiles
    static FloatBuffer tileVertices;	// Same vertex buffer for all tiles
    static FloatBuffer tileTextures;	// Same texture buffer for all tiles
    
    public VBOBuffers(int p_numPoints, boolean p_forTiles) {
        // Allocate buffers
        indices = ZUtils.createShortBuffer(3 * p_numPoints / 2);
        if (!p_forTiles || tileTextures == null) {
            vertices = ZUtils.createFloatBuffer(2 * p_numPoints);
            textures = ZUtils.createFloatBuffer(2 * p_numPoints);    	
            if (p_forTiles) {
            	tileTextures = textures;	// Same reference
            	tileVertices = vertices;	// Same reference
            }
        } else {
        	vertices = tileVertices;
        	textures = tileTextures;
        }
    }
    
    public void doubleCapacity() {
    	
    }
	
	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder();
		sb.append("Vertices : [");
		for (int i=0;i<vertices.limit();i++) {
			sb.append(vertices.get(i)+", ");
		}
		sb.append("]\nTextures : [");
		for (int i=0;i<textures.limit();i++) {
			sb.append(textures.get(i)+", ");
		}
		sb.append("]\nIndices : [");
		for (int i=0;i<indices.limit();i++) {
			sb.append(indices.get(i)+", ");
		}
		sb.append("]");
		return sb.toString();
	}
}
