package zildo.fwk.filter;

import org.lwjgl.opengl.GL11;


public class BilinearFilter extends ScreenFilter {

	@Override
	public boolean renderFilter() {
		endRenderingOnFBO();
		
		// Select right texture
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, blankTextureID);
        // Disable blend
		GL11.glDisable(GL11.GL_BLEND);

		GL11.glMatrixMode(GL11.GL_MODELVIEW);
    	GL11.glLoadIdentity();
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		GL11.glTranslatef(0,-sizeY,1);

		// Draw texture with depth
		super.render();
		GL11.glPopMatrix();
		
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		
		return true;
	}
	
	@Override
	public void preFilter() {
		startRenderingOnFBO(fboId, sizeX, sizeY);
   		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT); // Clear The Screen And The Depth Buffer

	}
	
}