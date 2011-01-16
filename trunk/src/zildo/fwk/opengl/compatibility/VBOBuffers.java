/**
 * Legend of Zildo
 * Copyright (C) 2006-2011 Evariste Boussaton
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
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

public class VBOBuffers {
	
	// As indices never changes, we just fix a maximum and use the generated array at start.
	static final int maxIndices = 2 * 6 * 64 * 64;
	
	int vertexBufferId;
	int	normalBufferId;
	int textureBufferId;
	int indiceBufferId;

    public FloatBuffer vertices;
    public FloatBuffer normals;
    public FloatBuffer textures;
    public IntBuffer indices;
    
    public VBOBuffers(int p_numPoints) {
        // Allocate buffers
        int numFaces = maxIndices / 3;
        vertices = BufferUtils.createFloatBuffer(3 * p_numPoints);
        normals = BufferUtils.createFloatBuffer(3 * numFaces);
        indices = BufferUtils.createIntBuffer(3 * maxIndices);
        textures = BufferUtils.createFloatBuffer(2 * p_numPoints);    	
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
