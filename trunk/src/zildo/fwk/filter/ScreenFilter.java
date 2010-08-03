/**
 * Legend of Zildo
 * Copyright (C) 2006-2010 Evariste Boussaton
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

package zildo.fwk.filter;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector4f;

import zildo.Zildo;
import zildo.client.ClientEngineZildo;
import zildo.fwk.gfx.TilePrimitive;
import zildo.fwk.opengl.Utils;


/**
 * Defines a screen filter.
 * 
 * Provides basically:
 * -a FBO
 * -a texture
 * -a depth rendered buffer
 * -a screen sized tile 
 * 
 * A simple super.render() from a derived class draw binded texture at screen.
 * 
 * @author tchegito
 *
 */
public abstract class ScreenFilter extends TilePrimitive {

	// Screen size
	protected static final int sizeX=Zildo.viewPortX;
	protected static final int sizeY=Zildo.viewPortY;
	// Resizing for OpenGL storage
	protected static final int realX=Utils.adjustTexSize(sizeX);
	protected static final int realY=Utils.adjustTexSize(sizeY);
	
	// common members
	protected int textureID;
	protected int depthTextureID;
	protected int fboId=-1;
	protected boolean active=true;
	
	//////////////////////////////////////////////////////////////////////
	// Construction/Destruction
	//////////////////////////////////////////////////////////////////////
	public ScreenFilter()
	{
		super(4,6, Utils.adjustTexSize(sizeX), Utils.adjustTexSize(sizeY));
		// Create a screen sized quad
		super.startInitialization();
		this.addTileSized(0,0,0.0f,0.0f,sizeX, sizeY);
		this.endInitialization();
	
		// Create texture for alpha blending
		this.createBlankTexture(true);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// renderFilter
	///////////////////////////////////////////////////////////////////////////////////////
	// Render filter on screen, after GUI done.
	///////////////////////////////////////////////////////////////////////////////////////
	public abstract boolean renderFilter();

	
	///////////////////////////////////////////////////////////////////////////////////////
	// drawFilter
	///////////////////////////////////////////////////////////////////////////////////////
	// Draw a box on texture used for screen filter.
	///////////////////////////////////////////////////////////////////////////////////////
	protected void drawFilter()
	{
		fbo.startRendering(fboId, sizeX, sizeY);
		GL11.glPushMatrix();

		GL11.glLoadIdentity();
		GL11.glTranslated(0, -sizeY,0);

		//draw a misc scene
		drawScene();

		GL11.glPopMatrix();
		fbo.endRendering();
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// createBlankTexture
	///////////////////////////////////////////////////////////////////////////////////////
	// Create a OpenGL texture object, and attach a FBO to it.
	///////////////////////////////////////////////////////////////////////////////////////
	private void createBlankTexture(boolean p_withDepth) {
		
		textureID=generateTexture(sizeX, sizeY);
		if (p_withDepth) {
			depthTextureID=generateDepthBuffer();
		}
		
        attachTextureToFBO(textureID, depthTextureID);
	}
	
	private void attachTextureToFBO(int texId, int texDepthId) {
		if (fboId == -1) {
			fboId=fbo.create();
		}
        fbo.bindToTextureAndDepth(texId, texDepthId, fboId);
	}

	protected void drawScene() {
		// On dessine un simple carré noir
		ClientEngineZildo.ortho.box(0, 0, sizeX, sizeY, 1, new Vector4f(0.0f,0.0f,0, getFadeLevel()/256.0f));
		
		/*
		PersoZildo zildo=EngineZildo.persoManagement.getZildo();
		int x=(int) zildo.x-EngineZildo.mapManagement.getCamerax() - 20;
		int y=(int) zildo.y-EngineZildo.mapManagement.getCameray() - 20;
		float alpha=0.5f-0.01f;
		int i=0;
		for (i=0;i<10;i++) {
			EngineZildo.ortho.boxv(x+i, y+i, 40-2*i, 40-2*i, 1, new Vector4f(0,0,0, alpha));
			alpha-=0.02f;
		}
		EngineZildo.ortho.box(x+i, y+i, 40-2*i, 40-2*i, 1, new Vector4f(0,0,0, 0.0f));
		*/
	}

	public void preFilter() {
		
	}
	public void postFilter() {
		
	}
	
	public void doOnInactive() {
		
	}
	
	public void doOnActive() {
		
	}
	
	@Override
	final public void cleanUp() {
		if (fboId > 0) {
			cleanTexture(textureID);
			cleanDepthBuffer(depthTextureID);
			cleanFBO(fboId);
		}
	}
	
	final public void setActive(boolean activ) {
		active=activ;
		if (!activ) {
			doOnInactive();
		} else {
			doOnActive();
		}
	}
	
	final public boolean isActive() {
		return active;
	}
	
	final public int getFadeLevel() {
		return ClientEngineZildo.filterCommand.getFadeLevel();
	}
}