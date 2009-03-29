package zildo.fwk.filter;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector4f;

import zildo.Zildo;
import zildo.fwk.FilterCommand;
import zildo.fwk.engine.EngineZildo;
import zildo.fwk.gfx.TilePrimitive;


public abstract class ScreenFilter extends TilePrimitive {

	protected static final int sizeX=Zildo.viewPortX;
	protected static final int sizeY=Zildo.viewPortY;

	// Fade
	protected int blankTextureID;
	protected int depthTextureID;
	protected int fboId=-1;
	protected boolean active=true;
	protected FilterCommand filterCommand;
	
	//////////////////////////////////////////////////////////////////////
	// Construction/Destruction
	//////////////////////////////////////////////////////////////////////

	public ScreenFilter()
	{
		super(4,6, adjustTexSize(sizeX), adjustTexSize(sizeY));
		// Create a screen sized quad
		super.startInitialization();
		this.addTileSized(0,0,0.0f,0.0f,sizeX, sizeY); //Constantes.SCREEN_SIZEX,Constantes.SCREEN_SIZEY);
		this.endInitialization();
	
		// Create texture for alpha blending
		this.createBlankTexture();
		
		// Transmit filter commander
		this.filterCommand=EngineZildo.filterCommand;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// renderFilter
	///////////////////////////////////////////////////////////////////////////////////////
	// Render filter on screen, after GUI done.
	///////////////////////////////////////////////////////////////////////////////////////
	public abstract void renderFilter();

	
	///////////////////////////////////////////////////////////////////////////////////////
	// drawFilter
	///////////////////////////////////////////////////////////////////////////////////////
	// Draw a box on texture used for screen filter.
	///////////////////////////////////////////////////////////////////////////////////////
	protected void drawFilter()
	{
		startRenderingOnFBO(fboId, sizeX, sizeY);
		GL11.glPushMatrix();

		GL11.glLoadIdentity();
		GL11.glTranslated(0, -sizeY,0);

		//draw a misc scene
		drawScene();

		GL11.glPopMatrix();
		endRenderingOnFBO();
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// createBlankTexture
	///////////////////////////////////////////////////////////////////////////////////////
	// Create a OpenGL texture object, and attach a FBO to it.
	///////////////////////////////////////////////////////////////////////////////////////
	void createBlankTexture() {
		
		blankTextureID=generateTexture(sizeX, sizeY, true);
		//depthTextureID=generateTexture(sizeX, sizeY, false);
		
        attachTextureToFBO(blankTextureID, depthTextureID);
	}
	
	void attachTextureToFBO(int texId, int texDepthId) {
		if (fboId == -1) {
			fboId=createFBO();
		}
        bindFBOToTexture(texId, fboId, true);
        //bindFBOToTexture(texDepthId, fboId, false);
        
        checkCompleteness(fboId);
	}

	protected void drawScene() {
		// On dessine un simple carré noir
		EngineZildo.ortho.box(0, 0, sizeX, sizeY, 1, new Vector4f(0.0f,0.0f,0, getFadeLevel()/256.0f));
		
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
	
	final public void setActive(boolean activ) {
		active=activ;
		if (!activ) {
			doOnInactive();
		}
	}
	
	final public boolean isActive() {
		return active;
	}
	
	final public int getFadeLevel() {
		return filterCommand.getFadeLevel();
	}
}