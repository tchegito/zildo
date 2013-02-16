/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
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

import org.lwjgl.opengl.GL11;

import zildo.client.ClientEngineZildo;
import zildo.fwk.bank.MotifBank;
import zildo.fwk.gfx.GFXBasics;
import zildo.fwk.gfx.effect.CloudGenerator;
import zildo.fwk.gfx.engine.TextureEngine;
import zildo.fwk.gfx.engine.TileEngine;
import zildo.fwk.gfx.primitive.TileGroupPrimitive.ActionNthRunner;
import zildo.monde.util.Point;
import zildo.monde.util.Vector3f;

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
	
	private TextureBinder texBinder = new TextureBinder();
	
	@Override
	public void render(boolean backGround) {

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
				meshBACK.render(texBinder);
				meshBACK2.render(texBinder);
				GL11.glDisable(GL11.GL_BLEND);
			}
			else {
				// Display FOREGROUND
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				GL11.glEnable(GL11.GL_BLEND);

				meshFORE.render(texBinder);
				
				GL11.glDisable(GL11.GL_BLEND);
			}

			// GL11.glColor3f(1f, 1f, 1f);
			GL11.glPopMatrix();
		}
	}
	
	private class TextureBinder implements ActionNthRunner {
		public void execute(final int i) {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureEngine.getNthTexture(i));
		}
	}

	@Override
	public void loadTextures() {
		loadTiles();

		createCloudTexture();
	}

	private void loadTiles() {
		// Create a texture based on the current tiles
		textureEngine.init();
		for (int i = 0; i < tileBankNames.length; i++) {
			MotifBank motifBank = getMotifBank(i);
			//textureEngine.loadTexture("tile"+i);
			createTextureFromMotifBank(motifBank);
			//motifBank.freeTempBuffer();
		}
	}
	
	private void createTextureFromMotifBank(MotifBank mBank) {

		GFXBasics surface = textureEngine.prepareSurfaceForTexture(true);

		// Display tiles on it
		int x = 0, y = 0;
		
		for (int n = 0; n < mBank.getNb_motifs(); n++)
		{
			short[] motif = mBank.get_motif(n);
			int i,j;
			for (int ij = 0 ; ij < 256; ij++) {
				i = ij & 0xf;
				j = ij >> 4;
				int a = motif[i + j * 16];
				if (a != 255) {
					surface.pset(i + x, j + y, a, null);
				}
			}
			// Next position
			x += 16;
			if (x >= 256) {
				x = 0;
				y += 16;
			}
		}

		textureEngine.generateTexture();
	}

	private void createCloudTexture() {
		textureEngine.prepareSurfaceForTexture(false);

		CloudGenerator cGen = new CloudGenerator(textureEngine.getBuffer());
		cGen.generate();

		texCloudId = textureEngine.generateTexture();
	}
	
	@Override
	public void saveTextures() {
		// Default : do nothing. Only LWJGL version can do that.
		textureEngine.saveAllTextures("tile");
	}

	
}
