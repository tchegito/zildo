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

import zildo.fwk.opengl.compatibility.VBOBuffers;
import zildo.monde.sprites.Reverse;
import zildo.resource.Constantes;

/**
 * Groupe of primitive from same layer. They share all the same vertex and textures buffer.<br/>
 * But we make sure that texture buffer is unique for this group with {@link VBOBuffers#resetTextureBuffer()}
 * @author Tchegito
 *
 */
public class TileGroupPrimitive {

	protected TilePrimitive[] meshes;
	
	public TileGroupPrimitive(int nbGroup) {
		meshes = new TilePrimitive[nbGroup];
	}
	
	public void startInitialization() {
		for (int i = 0; i < meshes.length; i++) {
			if (meshes[i] == null) {
				if (i == 0) {
					VBOBuffers.resetTextureBuffer();
				}
				meshes[i] = new TilePrimitive(Constantes.TILEENGINE_MAXPOINTS);
			}		
			meshes[i].startInitialization();
		}
	}
	
	public void endInitialization() {
		for (int i = 0; i < meshes.length; i++) {
			meshes[i].endInitialization();
		}
	}
	
	public void cleanUp()
	{
		for (TilePrimitive tp : meshes) {
			if (tp != null) {
				tp.cleanUp();
			}
		}
	}
	
    public void addTile(int nth, int x, int y, float u, float v) {
    	meshes[nth].addTile(x, y, u, v);
    }
    
    public void updateTile(int nth, int x, int y, int n_motif, Reverse reverse, boolean hasChanged) {
    	if (hasChanged) {
			int xTex = (n_motif % 16) * 16;
			int yTex = (n_motif / 16) * 16; // +1;
	
			meshes[nth].updateTile(16 * x,
					16 * y,
					xTex,
					yTex, 
					reverse);
    	} else {
    		meshes[nth].skipTile();
    	}
	}
    
    public boolean isEmpty(int nth) {
    	return meshes[nth].isEmpty();
    }
    
    /**
     * Render every mesh only if there's some vertices in it.
     * @param action action to be executed before render of each mesh (useful for texture binding)
     */
    public void render(ActionNthRunner action) {
    	for (int i=0;i<meshes.length;i++) {
    		if (!meshes[i].isEmpty()) {
    			action.execute(i);
    			meshes[i].render();
    		}
    	}
    }

    /**
     * Based on Action design pattern, just to be elegant with this class' philosophy : simple call
     * from outside, and all job is done here.
     */
    public interface ActionNthRunner {
    	void execute(int i);
    }
}
