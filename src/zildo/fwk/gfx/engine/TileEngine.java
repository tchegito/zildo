package zildo.fwk.gfx.engine;

import org.lwjgl.opengl.GL11;

import zildo.fwk.bank.MotifBank;
import zildo.fwk.engine.EngineZildo;
import zildo.fwk.gfx.GFXBasics;
import zildo.fwk.gfx.TilePrimitive;
import zildo.monde.Area;
import zildo.monde.Case;
import zildo.monde.serveur.MapManagement;
import zildo.prefs.Constantes;

// V1.0
// --------------------------------------------
// 4 vertices ---> 2 triangles ---> 1 tile
// 6 vertices ---> 4 triangles ---> 2 tiles
// 8 vertices ---> 6 triangles ---> 3 tiles
// (...)
// 42 vertices --> 40 triangles ---> 20 tiles

// x----x----x----x ... x----x				a=TILEENGINE_WIDTH
// |0   |1   |2   | ... |a-1 |a
// |    |    |    | ... |    |
// |    |    |    | ... |    |
// x----x----x----x ... x----x
// |a+1 |a+2 |a+3 | ... |2a  |2a+1

// Indices : (0,a+2,a+1) - (0,1,a+2)
//			 (1,a+3,a+2) - (1,2,a+3)
//                (...)
//           (a-1,2a+1,2a) - (a-1,a,2a+1)

// V2.0
// --------------------------------------------
// 4 vertices ---> 2 triangles ---> 1 tile
// 8 vertices ---> 4 triangles ---> 2 tiles
// 12 vertices --> 6 triangles ---> 3 tiles
// (...)
// 80 vertices --> 40 triangles --> 20 tiles

// x----x x----x x----x ... x----x				a=TILEENGINE_WIDTH
// |0  1| |2  3| |4  5| ... |2a-2|2a-1
// |    | |    | |    | ... |    |
// |2a  | |2a+2| |2a+4| ... |4a-2|
// x----x x----x x----x ... x----x
//   2a+1   2a+3   2a+5       4a-1
// x----x x----x x----x ... x----x
// |4a  | |4a+2| |4a+4| ... |6a-2|6a-1

// Indices : (0,2a+1,2a)   - (0,1,2a+1)
//			 (2,2a+3,2a+2) - (2,3,2a+3)

public class TileEngine extends TextureEngine {


	///////////////////////
	// Variables
	///////////////////////
    private int cameraX;
    private int cameraY;

	// Current tile engine dimensions
    private int tileEngineWidth;
    private int tileEngineHeight;

	// 3D Objects (vertices and indices per bank)
    protected TilePrimitive[] meshFORE;
    protected TilePrimitive[] meshBACK;

    
	//////////////////////////////////////////////////////////////////////
	// Construction/Destruction
	//////////////////////////////////////////////////////////////////////
	
	public TileEngine()
	{
		super();
		
		cameraX=-1;
		cameraY=-1;
		
		meshFORE=new TilePrimitive[Constantes.NB_MOTIFBANK];
		meshBACK=new TilePrimitive[Constantes.NB_MOTIFBANK];
		
		loadTiles();
	}
	
	public void cleanUp()
	{
		for (TilePrimitive tp : meshFORE) {
			tp.cleanUp();
		}
		for (TilePrimitive tp : meshBACK) {
			tp.cleanUp();
		}
	}
	
	public void loadTiles() {
		
		// Create a DirectX9 texture based on the current tiles
		for (int i=0;i<MapManagement.tileBankNames.length;i++) {
			MotifBank motifBank=EngineZildo.mapManagement.getMotifBank(i);
			this.createTextureFromMotifBank(motifBank);
		}
	}
	// Prepare vertices and indices for drawing tiles
	public void prepareTiles(Area theMap) {
	
		// Define tile map according to map dimension
		tileEngineWidth= theMap.getDim_x();
		tileEngineHeight=theMap.getDim_y();
	
		int i,x,y;
		
		for (i=0;i<Constantes.NB_MOTIFBANK;i++) {
			meshFORE[i] = new TilePrimitive(Constantes.TILEENGINE_MAXPOINTS,
										6*Constantes.TILEENGINE_WIDTH * Constantes.TILEENGINE_HEIGHT);
			meshBACK[i] = new TilePrimitive(Constantes.TILEENGINE_MAXPOINTS,
										6*Constantes.TILEENGINE_WIDTH * Constantes.TILEENGINE_HEIGHT);
			meshFORE[i].startInitialization();
			meshBACK[i].startInitialization();
		}
	
		// Prepare vertices and indices for background map (tiles displayed UNDER sprites)
		// For each tile bank
		for (y=0;y<tileEngineHeight;y++)
		{
			for (x=0;x<tileEngineWidth;x++)
			{
				// Get corresponding case on the map
				Case mapCase=theMap.get_mapcase(x,y+4);
				int n_motif=mapCase.getN_motif();
				int xTex=(n_motif % 16) * 16;
				int yTex=(int)(n_motif / 16) * 16 +1;
				int bank=mapCase.getN_banque() & 63;
				int nTile=0;
				nTile=meshBACK[bank].addTile( (16 * x),
												   (16 * y),
												   xTex,
												   yTex);
	
				// Store tile number in Case object to future access
				mapCase.setN_tile(nTile);
			}
		}
	
	
		// Prepare vertices and indices for foreground map (tiles displayed ON sprites)
		// For each tile bank
		for (y=0;y<tileEngineHeight;y++)
		{
			for (x=0;x<tileEngineWidth;x++)
			{
				// Get corresponding foreground case on the map
				Case mapCase=theMap.get_mapcase(x,y+4);
				if ((mapCase.getN_banque() & Area.M_MOTIF_MASQUE)!=0) {
					int n_motif=mapCase.getN_motif_masque();
					int xTex=(n_motif % 16) * 16;
					int yTex=(int)(n_motif / 16) * 16+1;
					int bank=mapCase.getN_banque_masque() & 63;
					meshFORE[bank].addTile( 16 * x,
									  16 * y,
									  xTex,
									  yTex);
	
				}
			}
		}
		for (i=0;i<Constantes.NB_MOTIFBANK;i++) {
			meshFORE[i].endInitialization();
			meshBACK[i].endInitialization();
		}
	}

	public void createTextureFromMotifBank(MotifBank mBank) {

		GFXBasics surface = prepareSurfaceForTexture();
		
		// Display tiles on it
		int x=0,y=0;
		for (int n=0;n<mBank.getNb_motifs();n++)
		{
			short[] motif=mBank.get_motif(n);
			for (int j=0;j< 16;j++)
				for (int i=0;i< 16;i++) 
				{
					int a=motif[i+j*16];
					if (a!=255)	{
						surface.pset(i+x,j+y,a,null);
					}
				}
			// Next position
			x+=16;
			if (x>=256)	{
				x=0;
				y+=16;
			}
		}

		generateTexture();
	}

	public void tileRender(boolean backGround) {

		// Small optimization: do not draw invisible faces ! (counterclock wise vertices)
		//pD3DDevice9.SetRenderState(D3DRS_CULLMODE, D3DCULL_CCW);
		if (backGround) {
			// Display BACKGROUND
			for (int i=0;i<Constantes.NB_MOTIFBANK;i++) {
				if (meshBACK[i].getNPoints()>0) {
			        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureTab[i]);                 // Select Our Second Texture
					meshBACK[i].render();
				}
			}
		}
		else {
			// Display FOREGROUND
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glEnable(GL11.GL_BLEND);

			for (int i=0;i<Constantes.NB_MOTIFBANK;i++) {
				if (meshFORE[i].getNPoints()>0) {
			        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureTab[i]);                 // Select Our Second Texture
					meshFORE[i].render();
				}
			}
			GL11.glDisable(GL11.GL_BLEND);
		}
		// pD3DDevice9.SetPixelShader(null);
	
	}
	
	// Redraw all tiles with updating VertexBuffers.
	// No need to access particular tile, because we draw every one.
	// **BUT THIS COULD BE DANGEROUS WHEN A TILE SWITCHES FROM ONE BANK TO ANOTHER**
	public void updateTiles(int cameraXnew, int cameraYnew, Area theMap, int compteur_animation) {
	
		if (cameraX!=-1 && cameraY!=-1) {
	
			int i;
			for (i=0;i<Constantes.NB_MOTIFBANK;i++) {
				meshBACK[i].startInitialization();
				meshFORE[i].startInitialization();
			}
			for (int y=0;y<tileEngineHeight;y++)
			{
				for (int x=0;x<tileEngineWidth;x++)
				{
					// Get corresponding case on the map
					Case mapCase=theMap.get_mapcase(x,y+4);
					int n_motif=mapCase.getAnimatedMotif(compteur_animation);
					int xTex=(n_motif % 16) * 16;
					int yTex=(int)(n_motif / 16) * 16;
					int bank=mapCase.getN_banque() & 63;
					if (bank<0 || bank>=Constantes.NB_MOTIFBANK) {
						throw new RuntimeException("We got a big problem");
					}
					meshBACK[bank].updateTile( (16 * x) - cameraXnew,
												(16 * y) - cameraYnew,
												xTex,
												yTex);
					if ((mapCase.getN_banque() & Area.M_MOTIF_MASQUE)!=0) {
						n_motif=mapCase.getN_motif_masque();
						xTex=(n_motif % 16) * 16;
						yTex=(int)(n_motif / 16) * 16; //+1;
						bank=mapCase.getN_banque_masque() & 63;
						meshFORE[bank].updateTile( (16 * x) - cameraXnew,
												(16 * y) - cameraYnew,
												xTex,
												yTex);
					}
				}
			}
	
			for (i=0;i<Constantes.NB_MOTIFBANK;i++) {
				meshBACK[i].endInitialization();
				meshFORE[i].endInitialization();
			}
		}
	
		cameraX=cameraXnew;
		cameraY=cameraYnew;
		
	}
}
