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

package zildo.platform.filter;

import javax.microedition.khronos.opengles.GL11;

import zildo.client.ClientEngineZildo;
import zildo.fwk.gfx.GraphicStuff;
import zildo.fwk.gfx.filter.BlurFilter;
import zildo.fwk.gfx.filter.FilterEffect;
import zildo.platform.opengl.AndroidOpenGLGestion;

public class AndroidBlurFilter extends BlurFilter {

	GL11 gl11;
	
	final int nImages=10;
	final float startCoeff=0.3f;
	final float incCoeff=0.1f;
	int texBuffer[];
	int currentImage;
	int nImagesSaved;
	
	public AndroidBlurFilter(GraphicStuff graphicStuff) {
		super(graphicStuff);
		texBuffer=new int[nImages];
		texBuffer[0]=textureID;
		for (int i=1;i<nImages;i++) {
			texBuffer[i]=graphicStuff.generateTexture(sizeX, sizeY);
		}
		currentImage=0;
		gl11 = (GL11) AndroidOpenGLGestion.gl10;
	}
	
	@Override
	public boolean renderFilter() {

		boolean result=true;
		
		graphicStuff.fbo.endRendering();
		
		// Get on top of screen and disable blending
		gl11.glMatrixMode(GL11.GL_MODELVIEW);
		gl11.glLoadIdentity();
		gl11.glMatrixMode(GL11.GL_PROJECTION);
		gl11.glPushMatrix();
		gl11.glTranslatef(0,-sizeY,0);

		//GL11.glDisable(GL11.GL_BLEND);
		
		if (nImagesSaved < nImages) {
			// All images are not stored yet. So we just display the current one.
			nImagesSaved++;
			gl11.glBindTexture(GL11.GL_TEXTURE_2D, texBuffer[currentImage]);
			super.render();
			result=false;
		} else {
			super.focusOnZildo();
	
	        // Draw each image, with intensified colors
			for (int i=0;i<nImages;i++) {
				float coeff=startCoeff+ i*(incCoeff / nImages);
				gl11.glColor4f(coeff, coeff, coeff, 1.0f);
				gl11.glBindTexture(GL11.GL_TEXTURE_2D, texBuffer[(i+currentImage+1) % nImages]);
				super.render();
				if (i==0) {
			        // Enable blend from second one
					gl11.glEnable(GL11.GL_BLEND);
					gl11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE); //_MINUS_SRC_ALPHA);
				}
			}

		}
        
		gl11.glPopMatrix();
		
		gl11.glMatrixMode(GL11.GL_MODELVIEW);
		gl11.glDisable(GL11.GL_BLEND);
		
		// Switch
		currentImage=(currentImage + 1) % nImages;

		return result;
	}
	
	@Override
	public void preFilter() {
		// Copy last texture in TexBuffer
		graphicStuff.fbo.bindToTextureAndDepth(texBuffer[currentImage], depthTextureID, fboId);
		graphicStuff.fbo.startRendering(fboId, sizeX, sizeY);
		gl11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT); // Clear The Screen And The Depth Buffer
	}
	
	/**
	 * Re-initialize z coordinate
	 */
	@Override
	public void doOnInactive(FilterEffect effect) {
		ClientEngineZildo.openGLGestion.setZ(0);
		if (gl11 != null) {
			gl11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		}
   		nImagesSaved=0;
   		currentImage=0;
	}
	
}