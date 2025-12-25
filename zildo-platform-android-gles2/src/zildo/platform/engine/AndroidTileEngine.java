/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
 * 
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

import android.opengl.GLES20;
import shader.Shaders;
import shader.Shaders.GLShaders;
import zildo.client.ClientEngineZildo;
import zildo.fwk.file.ShaderReader.TileShader;
import zildo.fwk.gfx.engine.TextureEngine;
import zildo.fwk.gfx.engine.TileEngine;
import zildo.fwk.gfx.primitive.TileGroupPrimitive.ActionNthRunner;
import zildo.monde.util.Point;
import zildo.monde.util.Vector2f;
import zildo.monde.util.Vector3f;
import zildo.platform.opengl.AndroidPixelShaders;

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

public class AndroidTileEngine extends TileEngine {

	Shaders shaders;
	
	public AndroidTileEngine(TextureEngine texEngine) {
		super(texEngine);
    	shaders = AndroidPixelShaders.shaders;
	}
	
	private TextureBinder texBinder = new TextureBinder();
	
	@Override
	public void render(int floor, boolean backGround) {

		if (initialized) {
			Vector3f ambient = ClientEngineZildo.ortho.getAmbientColor();
			if (ambient != null) {
				shaders.setColor(ambient);
			}

			Point p = ClientEngineZildo.mapDisplay.getCamera();
			shaders.setTranslation(new Vector2f(-p.x, -p.y));
			
			if (backGround) {
				// Display BACKGROUND
				GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
				GLES20.glEnable(GLES20.GL_BLEND);
				meshBACK.render(floor, texBinder);
				meshBACKShader.render(floor, texBinder);
				meshBACK2.render(floor, texBinder);
				GLES20.glBlendFunc(GLES20.GL_CONSTANT_ALPHA, GLES20.GL_ONE_MINUS_CONSTANT_ALPHA);
				GLES20.glBlendColor(0f, 0f, 0f, 0.5f);
				meshBACK2Shader.render(floor, texBinder);
				GLES20.glUseProgram(0);
				GLES20.glDisable(GLES20.GL_BLEND);
			}
			else {
				// Display FOREGROUND
				GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
				GLES20.glEnable(GLES20.GL_BLEND);

				meshFORE.render(floor, texBinder);

				GLES20.glDisable(GLES20.GL_BLEND);
			}
			shaders.setTranslation(new Vector2f(0, 0));
		}

	}
	
	private class TextureBinder implements ActionNthRunner {
		public void execute(final int i, TileShader shader) {
			
			// Configure right texture on first texturer
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureEngine.getNthTexture(i)); 
			if (shader != null) {
				// Enable water shader and pass uniform values
				float alpha = ClientEngineZildo.getTime() * 0.06f;
				switch (shader) {
				case water:
			        GLES20.glUseProgram(GLShaders.watered.id);
			        AndroidPixelShaders.shaders.uniform1f("alpha", alpha);
					Point camera = ClientEngineZildo.mapDisplay.getCamera();
			        AndroidPixelShaders.shaders.uniform2f("camera", new Vector2f(camera.x, -camera.y));
					break;
				case underwater:
			        GLES20.glUseProgram(GLShaders.underWater.id);
			        AndroidPixelShaders.shaders.uniform1f("alpha", alpha);
				}
			} else {
		        GLES20.glUseProgram(0);
			}
		}
	}

	@Override
	public void loadTextures() {
		textureEngine.init();
	    for (int i=0;i<tileBankNames.length;i++) {
		    ((AndroidTextureEngine)textureEngine).loadTexture("tile"+i);
	    }
	    texCloudId = textureEngine.loadTexture("tile11");
		texBackMenuId = textureEngine.loadTexture("tile12");
	}
}
