package zildo.fwk.gfx.engine;

import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector4f;
import org.newdawn.slick.TrueTypeFont;

import zildo.client.ClientEngineZildo;
import zildo.fwk.bank.SpriteBank;
import zildo.fwk.gfx.GFXBasics;
import zildo.fwk.gfx.SpritePrimitive;
import zildo.fwk.gfx.PixelShaders.EngineFX;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.SpriteModel;
import zildo.monde.sprites.SpriteStore;
import zildo.monde.sprites.elements.Element;
import zildo.prefs.Constantes;
import zildo.server.SpriteManagement;

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

    boolean pixelShaderSupported;
    
	//////////////////////////////////////////////////////////////////////
	// Construction/Destruction
	//////////////////////////////////////////////////////////////////////
	
	public SpriteEngine()
	{
        super();
		
        pixelShaderSupported = isPixelShaderSupported();
    }
	
	public void init(SpriteStore p_spriteStore) {
		prepareSprites(p_spriteStore);
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
			SpriteModel spr=sBank.get_sprite(n);
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
						long modifiedColor=-1;
						if (pixelShaderSupported) {
							modifiedColor=sBank.modifyPixel(n,a);
						}
						replacedColor=modifiedColor==-1?null:createColor(modifiedColor);
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
	void prepareSprites(SpriteStore p_spriteStore) {
		int i;
	
		// Allocate meshes
		for (i=0;i<Constantes.NB_SPRITEBANK;i++) {
			if (i==SpriteBank.BANK_COPYSCREEN) {
				meshSprites[i] = new SpritePrimitive(4, 6, 512, 256);
			} else {
				meshSprites[i] = new SpritePrimitive(
											Constantes.NB_SPRITE_PER_PRIMITIVE*4,
											Constantes.NB_SPRITE_PER_PRIMITIVE*3*2);	//NB_SPRITE_PER_PRIMITIVE
			}
		}
		// Load sprite banks
		for (i=0;i<SpriteManagement.sprBankName.length;i++) {
			SpriteBank sprBank=p_spriteStore.getSpriteBank(i);

			// Create a DirectX9 texture based on the current tiles
			createTextureFromSpriteBank(sprBank);
		}
		
		// Prepare screen copy texture
		textureTab[SpriteBank.BANK_COPYSCREEN]=generateTexture(0,64); //, 1024); //, Zildo.viewPortY);
		n_Texture++;
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
	
		SpriteModel spr=entity.getSprModel();
		entity.setLinkVertices(
		meshSprites[entity.getNBank()].addSprite(entity.getScrX(),
											  entity.getScrY() - z,
											  spr.getTexPos_x(),
											  spr.getTexPos_y(),
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
			z=entity.z;

		// Reverse attribute
		int revX = (entity.reverse & SpriteEntity.REVERSE_HORIZONTAL)!=0 ? -1 : 1;
		int revY = (entity.reverse & SpriteEntity.REVERSE_VERTICAL)!=0   ? -1 : 1;
		
		SpriteModel spr=entity.getSprModel();
		entity.setLinkVertices(
		meshSprites[entity.getNBank()].synchronizeSprite(entity.getScrX(),
				  									entity.getScrY() - z,
				  									spr.getTexPos_x(),
				  									spr.getTexPos_y(),
				  									revX * spr.getTaille_x(),
				  									revY * spr.getTaille_y()));
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
		boolean effectWithPixelShader=false;
		while (!endSequence) {
			int numBank=bankOrder[phase][posBankOrder*3];
			if (numBank == -1) {
				endSequence=true;
			} else {
				// Render the n sprites from this bank
				int nbQuads=bankOrder[phase][posBankOrder*3 + 1];
				int iCurrentFX=bankOrder[phase][posBankOrder*3 + 2];
				EngineFX currentFX=EngineFX.values()[iCurrentFX];
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureTab[numBank]);

				// Select the right pixel shader (if needed)
                if (pixelShaderSupported) {
                	effectWithPixelShader=true;
					if (currentFX == EngineFX.NO_EFFECT) {
						ARBShaderObjects.glUseProgramObjectARB(0);
					} else if (currentFX == EngineFX.PERSO_HURT) {
						// A sprite has been hurt
						ARBShaderObjects.glUseProgramObjectARB(ClientEngineZildo.pixelShaders.getPixelShader(1));
						ClientEngineZildo.pixelShaders.setParameter(1, "randomColor", new Vector4f((float) Math.random(), (float) Math.random(), (float) Math.random(), 1));
	
						// And enable the 'color addition' pixel shader
					} else if (currentFX.needPixelShader()) {
						// This is a color replacement, so get the right ones
						Vector4f[] tabColors=ClientEngineZildo.pixelShaders.getConstantsForSpecialEffect(currentFX);
	
						// And enable the 'color replacement' pixel shader
						ARBShaderObjects.glUseProgramObjectARB(ClientEngineZildo.pixelShaders.getPixelShader(0));
						ClientEngineZildo.pixelShaders.setParameter(0, "Color1", tabColors[2]);
						ClientEngineZildo.pixelShaders.setParameter(0, "Color2", tabColors[3]);
						ClientEngineZildo.pixelShaders.setParameter(0, "Color3", tabColors[0]);
						ClientEngineZildo.pixelShaders.setParameter(0, "Color4", tabColors[1]);
					} else {
						effectWithPixelShader=false;
					}
                }
                if (currentFX == EngineFX.SHINY) {
                    GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE); // _MINUS_SRC_ALPHA);
                    GL11.glColor4f(1, (float) Math.random(), 0, (float) Math.random());
                } else if (currentFX == EngineFX.QUAD) {
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                    GL11.glColor4f(0.5f + 0.5f * (float) Math.random(), 0.5f * (float) Math.random(), 0, 1);
                } else if (!effectWithPixelShader) {
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                    GL11.glColor4f(1, 1, 1, 1);
                    if (pixelShaderSupported) {
                    	ARBShaderObjects.glUseProgramObjectARB(0);
                    }
                }
				meshSprites[numBank].render(nbQuads);
				posBankOrder++;
			}
		}

		// Deactivate pixel shader
		if (pixelShaderSupported) {
			ARBShaderObjects.glUseProgramObjectARB(0);
		}
		GL11.glDisable(GL11.GL_BLEND);
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
	
	/**
	 * Capture screen before map scroll.
	 */
	public void captureScreen() {
		saveScreen(textureTab[SpriteBank.BANK_COPYSCREEN]);
	}
}