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

package zildo.platform.engine;

import org.lwjgl.opengl.GL11;

import zildo.client.ClientEngineZildo;
import zildo.fwk.gfx.engine.TextureEngine;
import zildo.fwk.gfx.engine.TileEngine;
import zildo.monde.util.Point;
import zildo.monde.util.Vector3f;
import zildo.resource.Constantes;

// V1.0
// --------------------------------------------
// 4 vertices ---> 2 triangles ---> 1 tile
// 6 vertices ---> 4 triangles ---> 2 tiles
// 8 vertices ---> 6 triangles ---> 3 tiles
// (...)
// 42 vertices --> 40 triangles ---> 20 tiles

// x----x----x----x ... x----x				a=TILEENGINE_WIDTH
// |0   |1   |2   | ... |a-1 |a
// |    |    |    | ... |    |
// |    |    |    | ... |    |
// x----x----x----x ... x----x
// |a+1 |a+2 |a+3 | ... |2a  |2a+1

// Indices : (0,a+2,a+1) - (0,1,a+2)
//			 (1,a+3,a+2) - (1,2,a+3)
//                (...)
//           (a-1,2a+1,2a) - (a-1,a,2a+1)

// V2.0
// --------------------------------------------
// 4 vertices ---> 2 triangles ---> 1 tile
// 8 vertices ---> 4 triangles ---> 2 tiles
// 12 vertices --> 6 triangles ---> 3 tiles
// (...)
// 80 vertices --> 40 triangles --> 20 tiles

// x----x x----x x----x ... x----x				a=TILEENGINE_WIDTH
// |0  1| |2  3| |4  5| ... |2a-2|2a-1
// |    | |    | |    | ... |    |
// |2a  | |2a+2| |2a+4| ... |4a-2|
// x----x x----x x----x ... x----x
//   2a+1   2a+3   2a+5       4a-1
// x----x x----x x----x ... x----x
// |4a  | |4a+2| |4a+4| ... |6a-2|6a-1

// Indices : (0,2a+1,2a)   - (0,1,2a+1)
//			 (2,2a+3,2a+2) - (2,3,2a+3)

public class LwjglTileEngine extends TileEngine {

	public LwjglTileEngine(TextureEngine texEngine) {
		super(texEngine);
	}
	
	@Override
	public void render(boolean backGround) {

		if (initialized) {
			Vector3f ambient = ClientEngineZildo.ortho.getAmbientColor();
			if (ambient != null) {
				GL11.glColor3f(ambient.x, ambient.y, ambient.z);
			}
			
			Point p = ClientEngineZildo.mapDisplay.getCamera();
			GL11.glPushMatrix();
			GL11.glTranslatef(-p.x, -p.y, 0f);

			if (backGround) {
				// Display BACKGROUND
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				GL11.glEnable(GL11.GL_BLEND);
				for (int i = 0; i < Constantes.NB_MOTIFBANK; i++) {
					if (!meshBACK[i].isEmpty()) {
						GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureEngine.getNthTexture(i)); 
						meshBACK[i].render();
					}
				}
				GL11.glDisable(GL11.GL_BLEND);
			}
			else {
				// Display FOREGROUND
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				GL11.glEnable(GL11.GL_BLEND);

				for (int i = 0; i < Constantes.NB_MOTIFBANK; i++) {
					if (!meshFORE[i].isEmpty()) {
						GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureEngine.getNthTexture(i)); 
						meshFORE[i].render();
					}
				}
				GL11.glDisable(GL11.GL_BLEND);
			}

			// GL11.glColor3f(1f, 1f, 1f);
			GL11.glPopMatrix();
		}
	}
}
