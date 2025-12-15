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

import java.io.File;

import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;

import zildo.client.ClientEngineZildo;
import zildo.fwk.gfx.effect.CloudGenerator;
import zildo.fwk.gfx.engine.TextureEngine;
import zildo.fwk.gfx.engine.TileEngine;
import zildo.fwk.gfx.primitive.TileGroupPrimitive.ActionNthRunner;
import zildo.monde.util.Point;
import zildo.monde.util.Vector3f;
import zildo.resource.Constantes;

/**
 * LWJGL customisation for tile engine.<p/>
 * 
 * Two things are important here, and splitted into each platform-dependent part :<ol>
 * <li><b>render</b> : obviously, this is specific for each targeted platform</li>
 * <li><b>texture</b> : according to platform performance, we create texture from bank (lwjgl) or use directly ready-to-use textures (android).</li>
 * </ol>
 * @author evariste.boussaton
 *
 */
public class LwjglTileEngine extends TileEngine {

	public LwjglTileEngine(TextureEngine texEngine) {
		super(texEngine);
	}
	
	float alpha = 0f;
	
	private TextureBinder texBinder = new TextureBinder();
	
	@Override
	public void render(int floor, boolean backGround) {

		if (initialized) {
			Vector3f ambient = ClientEngineZildo.ortho.getAmbientColor();
			if (ambient != null) {
				GL11.glColor3f(ambient.x, ambient.y, ambient.z);
			}
			
			Point p = ClientEngineZildo.mapDisplay.getCamera();
			GL11.glMatrixMode(GL11.GL_MODELVIEW);

			GL11.glPushMatrix();
			GL11.glTranslatef(-p.x, -p.y, 0f);

			if (backGround) {
				// Display BACKGROUND
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				GL11.glEnable(GL11.GL_BLEND);
				meshBACK.render(floor, texBinder);
				meshBACK2.render(floor, texBinder);
				GL11.glBlendFunc(GL11.GL_CONSTANT_ALPHA, GL11.GL_ONE_MINUS_CONSTANT_ALPHA);
				GL14.glBlendColor(0f, 0f, 0f, 0.5f);
				meshBACK2Shader.render(floor, texBinder);
				disableWaterShader();
				GL11.glDisable(GL11.GL_BLEND);
			}
			else {
				// Display FOREGROUND
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				GL11.glEnable(GL11.GL_BLEND);

				meshFORE.render(floor, texBinder);
				
				GL11.glDisable(GL11.GL_BLEND);
			}

			// GL11.glColor3f(1f, 1f, 1f);
			GL11.glPopMatrix();
		}
	}
	
	private void disableWaterShader() {
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		ARBShaderObjects.glUseProgramObjectARB(0);
	}

	private class TextureBinder implements ActionNthRunner {
		public void execute(final int numTexture, boolean shaderAvailable) {
			// Configure right texture on first texturer
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureEngine.getNthTexture(numTexture));
			if (shaderAvailable) {
				// Enable water shader and pass uniform values
				int shaderProgram = ClientEngineZildo.pixelShaders.getPixelShader(8);
				ARBShaderObjects.glUseProgramObjectARB(shaderProgram);
				GL20.glUniform1f(GL20.glGetUniformLocation(shaderProgram, "alpha"), alpha);
				Point camera = ClientEngineZildo.mapDisplay.getCamera();
				GL20.glUniform2f(GL20.glGetUniformLocation(shaderProgram, "camera"), camera.x, -camera.y);
				alpha += 0.06f;
			} else {
				ARBShaderObjects.glUseProgramObjectARB(0);
			}
		}
	}

	@Override
	public void loadTextures() {
		loadTiles();

		createCloudTexture();
		createBackMenuTexture();
	}

	private void loadTiles() {
		// Create a texture based on the current tiles
		textureEngine.init();
		for (int i = 0; i < tileBankNames.length; i++) {
			String filename = "tile"+i;
			boolean optional = false;
			if (tileBankNames[i].equals("pyxel")) {
				File f = new File(Constantes.DATA_PATH+"textures/"+filename+".png");
				optional |= (!f.exists());
			}
			if (!optional) {
				textureEngine.loadTexture(filename);
			}
		}
	}
	
	private void createCloudTexture() {
		textureEngine.prepareSurfaceForTexture(false);

		CloudGenerator cGen = new CloudGenerator(textureEngine.getBuffer());
		cGen.generate();

		texCloudId = textureEngine.generateTexture();
	}
	
	private void createBackMenuTexture() {
		//textureEngine.prepareSurfaceForTexture(false);
		
		texBackMenuId = textureEngine.loadTexture("menuBack256");
		//texBackMenuId = textureEngine.generateTexture();
	}

}
