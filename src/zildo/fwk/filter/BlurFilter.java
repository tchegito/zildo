package zildo.fwk.filter;

import org.lwjgl.opengl.GL11;

import zildo.fwk.engine.EngineZildo;
import zildo.monde.Point;
import zildo.monde.Sprite;
import zildo.monde.persos.PersoZildo;

public class BlurFilter extends BilinearFilter {

	final int nImages=10;
	final float startCoeff=0.3f;
	final float incCoeff=0.1f;
	int texBuffer[];
	int currentImage;
	
	public BlurFilter() {
		super();
		texBuffer=new int[nImages];
		texBuffer[0]=blankTextureID;
		for (int i=1;i<nImages;i++) {
			texBuffer[i]=generateTexture(sizeX, sizeY, true);
		}
		currentImage=0;
	}
	
	@Override
	public void renderFilter() {

		endRenderingOnFBO();
		
		PersoZildo zildo=EngineZildo.persoManagement.getZildo();
		Point zildoPos=new Point(zildo.getScrX(), zildo.getScrY());
		Sprite spr=zildo.getSprModel();
		zildoPos.addX(spr.getTaille_x() / 2);
		zildoPos.addY(spr.getTaille_y() / 2);
		EngineZildo.getOpenGLGestion().setZoomPosition(zildoPos);
		float z=2.0f * (float) Math.sin(getFadeLevel() * (0.25f*Math.PI / 256.0f));
		EngineZildo.getOpenGLGestion().setZ(z);
		
		// Get on top of screen and disable blending
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
    	GL11.glLoadIdentity();
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		GL11.glTranslatef(0,-sizeY,0);

		//GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);

        // Draw each image, with intensified colors
		for (int i=0;i<nImages;i++) {
			float coeff=startCoeff+ (float)i*(incCoeff / (float) nImages);
	   		GL11.glColor4f(coeff, coeff, coeff, 1.0f);
	        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texBuffer[(i+currentImage+1) % nImages]);
			super.render();
			if (i==0) {
		        // Enable blend from second one
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE); //_MINUS_SRC_ALPHA);
			}
		}
        
		GL11.glPopMatrix();
		
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glDisable(GL11.GL_BLEND);
    	
		// Switch
		currentImage=(currentImage + 1) % nImages;
		bindFBOToTexture(texBuffer[currentImage], fboId, true);
	}
	
	@Override
	public void preFilter() {
		// Copy last texture in TexBuffer
		startRenderingOnFBO(fboId, sizeX, sizeY);

	}
	
	/**
	 * Re-initialize z coordinate
	 */
	public void doOnInactive() {
		EngineZildo.getOpenGLGestion().setZ(0);
	}
}
