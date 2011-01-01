/**
 * Legend of Zildo
 * Copyright (C) 2006-2011 Evariste Boussaton
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

package zildo.fwk.gfx.engine;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import zildo.fwk.bank.MotifBank;
import zildo.fwk.gfx.GFXBasics;
import zildo.fwk.gfx.TilePrimitive;
import zildo.monde.map.Area;
import zildo.monde.map.Case;
import zildo.monde.map.Point;
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
    
    private boolean initialized=false;
    List<MotifBank> motifBanks;
    private int n_banquemotif; // Nombre de banque de motifs en mémoire

	static public String[] tileBankNames={"foret1.dec",
		"village.dec",
		"maison.dec",
		"grotte.dec",
		"foret2.dec",
		"foret3.dec",
		"foret4.dec",
		"palais1.dec",
		"palais2.dec"};
            
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
		
        // Load graphs
        motifBanks = new ArrayList<MotifBank>();
        this.charge_tous_les_motifs();

        loadTiles();
    }
	
	public void cleanUp()
	{
		for (TilePrimitive tp : meshFORE) {
			if (tp != null) {
				tp.cleanUp();
			}
		}
		for (TilePrimitive tp : meshBACK) {
			if (tp != null) {
				tp.cleanUp();
			}
		}
		
		initialized=false;
	}
	
    // /////////////////////////////////////////////////////////////////////////////////////
    // charge_tous_les_motifs
    // /////////////////////////////////////////////////////////////////////////////////////
    // Load every tile banks
    // /////////////////////////////////////////////////////////////////////////////////////
    void charge_tous_les_motifs() {
        n_banquemotif = 0;
        for (String bankName : tileBankNames) {
            this.charge_motifs(bankName.toUpperCase());
        }
    }

    // /////////////////////////////////////////////////////////////////////////////////////
    // charge_motifs
    // /////////////////////////////////////////////////////////////////////////////////////
    // IN:filename to load as a tile bank
    // /////////////////////////////////////////////////////////////////////////////////////
    void charge_motifs(String filename) {
        MotifBank motifBank = new MotifBank();

        motifBank.charge_motifs(filename);

        motifBanks.add(motifBank);

        // Relase memory allocated for tile graphics, because it's in directX memory now.
        // delete motifBank;

        // Increase number of loaded banks
        n_banquemotif++;
    }

    public MotifBank getMotifBank(int n) {
        return motifBanks.get(n);
    }
    
    public void loadTiles() {
        // Create a texture based on the current tiles
        for (int i = 0; i < tileBankNames.length; i++) {
            MotifBank motifBank = getMotifBank(i);
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
			meshFORE[i] = new TilePrimitive(Constantes.TILEENGINE_MAXPOINTS);
			meshBACK[i] = new TilePrimitive(Constantes.TILEENGINE_MAXPOINTS);
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
				int yTex=(n_motif / 16) * 16 +1;
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
					int yTex=(n_motif / 16) * 16+1;
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
		
		initialized=true;
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

		if (initialized) {
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
		}
	}
	
	// Redraw all tiles with updating VertexBuffers.
	// No need to access particular tile, because we draw every one.
	// **BUT THIS COULD BE DANGEROUS WHEN A TILE SWITCHES FROM ONE BANK TO ANOTHER**
	public void updateTiles(Point cameraNew, Area theMap, int compteur_animation) {
	
		if (initialized && cameraX!=-1 && cameraY!=-1) {
	
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
					int yTex=(n_motif / 16) * 16;
					int bank=mapCase.getN_banque() & 63;
					if (bank<0 || bank>=Constantes.NB_MOTIFBANK) {
						throw new RuntimeException("We got a big problem");
					}
					meshBACK[bank].updateTile( (16 * x) - cameraNew.x,
												(16 * y) - cameraNew.y,
												xTex,
												yTex);
					if ((mapCase.getN_banque() & Area.M_MOTIF_MASQUE)!=0) {
						n_motif=mapCase.getN_motif_masque();
						xTex=(n_motif % 16) * 16;
						yTex=(n_motif / 16) * 16; //+1;
						bank=mapCase.getN_banque_masque() & 63;
						meshFORE[bank].updateTile( (16 * x) - cameraNew.x,
												(16 * y) - cameraNew.y,
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
	
		cameraX=cameraNew.x;
		cameraY=cameraNew.y;
		
	}

	/**
	 * Return the bank's index in loaded ones from a given name
	 * @param p_name
	 * @return int
	 */
	public int getBankFromName(String p_name) {
		int i=0;
		for (String s : tileBankNames) {
			if (s.toUpperCase().indexOf(p_name.toUpperCase()) == 0) {
				return i;
			}
			i++;
		}
		throw new RuntimeException("Bank "+p_name+" doesn't exist.");
	}
	
	/**
	 * Return the bank's name by index
	 * @param nBank
	 * @return String
	 */
	public String getBankNameFromInt(int nBank) {
		String response=tileBankNames[nBank];
		return response.substring(0, response.indexOf("."));
	}
}
