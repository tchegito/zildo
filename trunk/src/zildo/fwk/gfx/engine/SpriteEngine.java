package zildo.fwk.gfx.engine;

import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector4f;
import org.newdawn.slick.TrueTypeFont;

import zildo.fwk.GFXBasics;
import zildo.fwk.bank.SpriteBank;
import zildo.fwk.engine.EngineZildo;
import zildo.fwk.gfx.PixelShaders;
import zildo.fwk.gfx.SpritePrimitive;
import zildo.monde.Sprite;
import zildo.monde.decors.Element;
import zildo.monde.decors.SpriteEntity;
import zildo.prefs.Constantes;

// SpriteEngine.cpp: implementation of the SpriteEngine class.
//
//////////////////////////////////////////////////////////////////////





public class SpriteEngine extends TextureEngine {

	
	///////////////////////
	// Variables
	///////////////////////
	// Sorting by bank and by Y axis
	private int[][] bankOrder;   // Reference to an int array from SpriteManagement


	// 3D Objects (vertices and indices per bank)
	SpritePrimitive meshSprites[]=new SpritePrimitive[Constantes.NB_SPRITEBANK];
	
	//////////////////////////////////////////////////////////////////////
	// Construction/Destruction
	//////////////////////////////////////////////////////////////////////
	
	public SpriteEngine()
	{
		super();
		prepareSprites();
	}
	
	public void cleanUp()
	{
		for (SpritePrimitive sp : meshSprites) {
			sp.cleanUp();
		}
		for (int i=0;i<n_Texture;i++) {
			int id=textureTab[i];
			cleanTexture(id);
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// createTextureFromSpriteBank
	///////////////////////////////////////////////////////////////////////////////////////
	// IN: Bank to transform into texture
	///////////////////////////////////////////////////////////////////////////////////////
	// Create a Direct3DTexture9 object from a sprite bank. Every sprite is added at the
	// right side of the previous one. If it is too long, we shift to next line, which is
	// calculated as the highest sprite on the line. And so on.
	// Here we use a GFXBasics object to gain in readability and comfort. This one will be deleted
	// at the end, indeed. So we can say this is a beautiful method.
	public void createTextureFromSpriteBank(SpriteBank sBank) {
	
		GFXBasics surfaceGfx = prepareSurfaceForTexture();

		surfaceGfx.StartRendering();
		surfaceGfx.box(0,0,320,256,32,new Vector4f(0,0,0,0));
	
		int x=0,y=0,highestLine=0;
		for (int n=0;n<sBank.getNSprite();n++)
		{
			Sprite spr=sBank.get_sprite(n);
			int longX=spr.getTaille_x();
			int longY=spr.getTaille_y();
			// Test de dépassement sur la texture
			if ( (x+longX) > 256 ) {
				x=0;
				y+=highestLine;
				highestLine=0;
			}
			// On stocke la position du sprite sur la texture
			spr.setTexPos_x(x);
			spr.setTexPos_y(y); //+1);
			// On place le sprite sur la texture
			short[] motif=sBank.getSpriteGfx(n);
			Vector4f replacedColor;
			for (int j=0;j< longY;j++)
				
				for (int i=0;i< longX;i++)
				{
					int a=motif[i+j*longX];
					if (a!=255)
					{
						// Regular size
						long modifiedColor=sBank.modifyPixel(n,a);
						replacedColor=modifiedColor==0?null:createColor(modifiedColor);
						surfaceGfx.pset(i+x,j+y,a,replacedColor);
					}
				}

			// Next position
			x+=longX;
			if (longY > highestLine)	// Mark the highest sprite on the row
				highestLine = longY;
		}
		generateTexture();
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// createTextureFromFontStyle
	///////////////////////////////////////////////////////////////////////////////////////
	public void createTextureFromFontStyle(SpriteBank sprBank)
	{
		GFXBasics surfaceGfx = prepareSurfaceForTexture();


		TrueTypeFont ttFont=(TrueTypeFont) surfaceGfx.getFont();
		
		surfaceGfx.StartRendering();
		surfaceGfx.box(0,0,256,8,255,new Vector4f(0,0,0,0));

		// Draw fonts on it
	
		String alphabet= "ABCDEFGHIJKLMNOPQRSTUVWXYZ-.,<>!?()'#$abcdefghijklmnopqrstuvwxyz";
		int posX=0, posY=0, maxHeight=0;
		maxHeight=ttFont.getHeight();
		for (int i=0;i<alphabet.length();i++) {
			String a=alphabet.charAt(i)+"";
			int size=ttFont.getWidth(a);
	
			// Increase for outline effect
			size+=1;
	
			// We calculate the location for next font, then display it
			if ( (posX + size) > 256) {
				posX=0;
				posY+=maxHeight;
				maxHeight=0;
			}
			//if (size.bottom > maxHeight) {
			//	maxHeight=size.bottom;
			//}
			sprBank.addSpriteReference(posX, posY+1, size, maxHeight);
	
			surfaceGfx.aff_texte(posX, posY+1, a ,0xff0000ff, false);
	
			posX+=size;
	
		}

		/*
		surfaceGfx.copyFromRenderedSurface();
		surfaceGfx.outlineBox(0,0,256,256,
				0xff0000ff,
				0xff00ff00,false);
				*/
		generateTexture();

	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// prepareSprites
	///////////////////////////////////////////////////////////////////////////////////////
	// IN: Bank to transform into texture
	///////////////////////////////////////////////////////////////////////////////////////
	// Prepare vertices and indices for drawing tiles
	void prepareSprites() {
		int i;
	
		// Allocate meshes
		for (i=0;i<Constantes.NB_SPRITEBANK;i++) {
			meshSprites[i] = new SpritePrimitive(
										Constantes.NB_SPRITE_PER_PRIMITIVE*4,
										Constantes.NB_SPRITE_PER_PRIMITIVE*3*2);	//NB_SPRITE_PER_PRIMITIVE
	
		}
	
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// addSprite
	///////////////////////////////////////////////////////////////////////////////////////
	// IN:  Entity to add into the primitive
	// OUT: Quad indice from the added entity
	///////////////////////////////////////////////////////////////////////////////////////
	// Ajoute un sprite dans les IB/VB
	// *On considère que StartInitialization a déjà été appelé*
	public void addSprite(SpriteEntity entity) {
		float z=0.0f;
		if (entity.getEntityType() == SpriteEntity.ENTITYTYPE_ELEMENT)
			z=((Element)entity).z;
	
		Sprite spr=entity.getSprModel();
		entity.setLinkVertices(
		meshSprites[entity.getNBank()].addSprite((float) entity.getScrX(),
											  (float) entity.getScrY() - z,
											  (float)spr.getTexPos_x(),
											  (float)spr.getTexPos_y(),
											  spr.getTaille_x(),
											  spr.getTaille_y()));
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// synchronizeSprite
	///////////////////////////////////////////////////////////////////////////////////////
	// IN:  Entity to synchronize into the primitive
	///////////////////////////////////////////////////////////////////////////////////////
	// *On considère que StartInitialization a déjà été appelé*
	public void synchronizeSprite(SpriteEntity entity) {
	
		float z=0.0f;
		if (entity.getEntityType() == SpriteEntity.ENTITYTYPE_ELEMENT ||
				entity.getEntityType() == SpriteEntity.ENTITYTYPE_PERSO)
			z=((Element)entity).z;
	
		Sprite spr=entity.getSprModel();
		entity.setLinkVertices(
		meshSprites[entity.getNBank()].synchronizeSprite((float) entity.getScrX(),
				  									(float) entity.getScrY() - z,
				  									(float)spr.getTexPos_x(),
				  									(float)spr.getTexPos_y(),
				  									spr.getTaille_x(),
				  									spr.getTaille_y()));
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// endInitialization
	///////////////////////////////////////////////////////////////////////////////////////
	public void endInitialization()
	{
		// Close meshes initialization
		for (int i=0;i<Constantes.NB_SPRITEBANK;i++) {
			meshSprites[i].endInitialization();
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// startInitialization
	///////////////////////////////////////////////////////////////////////////////////////
	public void startInitialization()
	{
		// Start meshes initialization
		for (int i=0;i<Constantes.NB_SPRITEBANK;i++) {
			meshSprites[i].startInitialization();
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// spriteRender
	///////////////////////////////////////////////////////////////////////////////////////
	// Draw every sprite's primitives
	///////////////////////////////////////////////////////////////////////////////////////
	// IN: true=render BACKground
	//	   false=render FOREground
	///////////////////////////////////////////////////////////////////////////////////////
	public void spriteRender(boolean backGround) {
	
		//pD3DDevice9.SetFVF(D3DFVF_TLVERTEX);
	
		// Display every sprites
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_BLEND);
		
		// Respect order from bankOrder
		boolean endSequence=false;
		int posBankOrder=0;
		if (backGround) {
			// Initialize mesh the first half-time for background
			// Because foreground display will continue rendering just after it.
			for (int i=0;i<Constantes.NB_SPRITEBANK;i++)
				meshSprites[i].initRendering();
		}
	
		int phase=(backGround)?0:1;
		while (!endSequence) {
			int numBank=bankOrder[phase][posBankOrder*3];
			if (numBank == -1) {
				endSequence=true;
			} else {
				// Render the n sprites from this bank
				int nbQuads=bankOrder[phase][posBankOrder*3 + 1];
		        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureTab[numBank]);

				// Select the right pixel shader (if needed)
				
				int currentFX=bankOrder[phase][posBankOrder*3 + 2];
	
				if (currentFX == PixelShaders.ENGINEFX_NO_EFFECT) {
					ARBShaderObjects.glUseProgramObjectARB(0);
				} else if (currentFX == PixelShaders.ENGINEFX_PERSO_HURT) {
					// A sprite has been hurt
					ARBShaderObjects.glUseProgramObjectARB(EngineZildo.pixelShaders.getPixelShader(1));
					EngineZildo.pixelShaders.setParameter(1, "randomColor", new Vector4f((float) Math.random(), (float) Math.random(), (float) Math.random(), 1));

					// And enable the 'color addition' pixel shader
				} else {
					// This is a color replacement, so get the right ones
					Vector4f[] tabColors=EngineZildo.pixelShaders.getConstantsForSpecialEffect(currentFX);

					// And enable the 'color replacement' pixel shader
					ARBShaderObjects.glUseProgramObjectARB(EngineZildo.pixelShaders.getPixelShader(0));
					EngineZildo.pixelShaders.setParameter(0, "Color1", tabColors[2]);
					EngineZildo.pixelShaders.setParameter(0, "Color2", tabColors[3]);
					EngineZildo.pixelShaders.setParameter(0, "Color3", tabColors[0]);
					EngineZildo.pixelShaders.setParameter(0, "Color4", tabColors[1]);
				}
				
				meshSprites[numBank].render(nbQuads);
				posBankOrder++;
			}
		}

		// Deactivate pixel shader
		ARBShaderObjects.glUseProgramObjectARB(0);
		GL11.glDisable(GL11.GL_BLEND);

		/*pD3DDevice9.SetRenderState(D3DRS_SRCBLEND, D3DBLEND_SRCALPHA);
		pD3DDevice9.SetRenderState(D3DRS_DESTBLEND, D3DBLEND_ONE);
		pD3DDevice9.SetRenderState(D3DRS_ALPHABLENDENABLE,0);
		pD3DDevice9.SetPixelShader(null);
		*/
	}

	///////////////////////////////////////////////////////////////////////////////////////
	// buildIndexBuffers
	///////////////////////////////////////////////////////////////////////////////////////
	// IN: quad order (sorted by texture, and by Y-position), number of quad on each one
	///////////////////////////////////////////////////////////////////////////////////////
	// Call the homonym method on each sprite's primitive
	///////////////////////////////////////////////////////////////////////////////////////
	public void buildIndexBuffers(int[][] quadOrder) {
		for (int i=0;i<Constantes.NB_SPRITEBANK;i++) {
			int[] quadOrderForOneBank=quadOrder[i];
			if (quadOrderForOneBank != null) {
				meshSprites[i].buildIndexBuffer(quadOrderForOneBank);
			}
		}
	}

	public int[][] getBankOrder() {
		return bankOrder;
	}

	public void setBankOrder(int[][] bankOrder) {
		this.bankOrder = bankOrder;
	}
}