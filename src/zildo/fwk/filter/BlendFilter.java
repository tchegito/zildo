package zildo.fwk.filter;

import org.lwjgl.opengl.GL11;

import zildo.Zildo;
import zildo.client.ClientEngineZildo;

/**
 * Draw boxes more and more large onto the screen, to get a soft focus effect.
 * 
 * If square boxes are 1-sized, don't do anything.
 * 
 * @author tchegito
 *
 */
public class BlendFilter extends ScreenFilter {

	static final int SQUARE_SIZE = 20;
	
	private int getCurrentSquareSize() {
		return 1 + (int) ((SQUARE_SIZE * getFadeLevel()) / 256.0f);
	}

	@Override
	public boolean renderFilter() {
		int currentSquareSize = getCurrentSquareSize();

		if (currentSquareSize == 1) {
			return true;
		}
		fbo.endRendering();

		// Get on top of screen and disable blending
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
    	GL11.glLoadIdentity();
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		GL11.glTranslatef(0,-sizeY,0);
		
		// Draw squares
		int nSquareX=Zildo.viewPortX / currentSquareSize;
		int nSquareY=Zildo.viewPortY / currentSquareSize;
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);

		GL11.glBegin(GL11.GL_QUADS);
		for (int i=0;i<nSquareY+1;i++) {
			for (int j=0;j<nSquareX+1;j++) {
				ClientEngineZildo.ortho.boxTexturedOpti(j*currentSquareSize, i*currentSquareSize,
						              currentSquareSize, currentSquareSize, 
						              (j*currentSquareSize) / (float) ScreenFilter.realX, 
						              (i*currentSquareSize) / (float) ScreenFilter.realY,0, 0);
			}
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glDisable(GL11.GL_BLEND);

		return true;
	}

	@Override
	public void preFilter() {
		if (getCurrentSquareSize() == 1) {
			return;
		}
		// Copy last texture in TexBuffer
		fbo.bindToTextureAndDepth(textureID, depthTextureID, fboId);
		fbo.startRendering(fboId, sizeX, sizeY);
   		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT); // Clear The Screen And The Depth Buffer
	}
}
