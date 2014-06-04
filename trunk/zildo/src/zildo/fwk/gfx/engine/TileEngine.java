/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
 * 
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

import zildo.Zildo;
import zildo.fwk.bank.TileBank;
import zildo.fwk.gfx.primitive.TileGroupPrimitive;
import zildo.monde.map.Area;
import zildo.monde.map.Case;
import zildo.monde.map.Tile;
import zildo.monde.map.accessor.AreaAccessor;
import zildo.monde.map.accessor.OneFloorAreaAccessor;
import zildo.monde.map.accessor.AreaAccessor.AccessedCase;
import zildo.monde.util.Point;
import zildo.resource.Constantes;

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

/**
 * Tile engine.<p/>
 * 
 * Two things are important here, and splitted into each platform-dependent part via abstract methods:<ol>
 * <li><b>render</b> : obviously, this is specific for each targeted platform</li>
 * <li><b>texture</b> : according to platform performances, we create textures from banks (lwjgl) or load directly ready-to-use textures (android).</li>
 * </ol>
 * @author tchegito
 *
 */
public abstract class TileEngine {

	// /////////////////////
	// Variables
	// /////////////////////
	private int cameraX;
	private int cameraY;

	// 3D Objects (vertices and indices per bank)
	protected TileGroupPrimitive meshFORE;
	protected TileGroupPrimitive meshBACK;
	protected TileGroupPrimitive meshBACK2;

	AreaAccessor areaAccessor;
	
	protected boolean initialized = false;
	List<TileBank> motifBanks;
	public int texCloudId;

	protected TextureEngine textureEngine;
	
	static public String[] tileBankNames = { "foret1",
			"village",
			"maison",
			"grotte",
			"foret2",
			"foret3",
			"foret4",
			"palais1",
			"palais2",
			"palais3"};

	// ////////////////////////////////////////////////////////////////////
	// Construction/Destruction
	// ////////////////////////////////////////////////////////////////////

	public TileEngine(TextureEngine texEngine)
	{
		textureEngine = texEngine;
		
		cameraX = -1;
		cameraY = -1;

		meshFORE = new TileGroupPrimitive(Constantes.NB_MOTIFBANK);
		meshBACK = new TileGroupPrimitive(Constantes.NB_MOTIFBANK);
		meshBACK2 = new TileGroupPrimitive(Constantes.NB_MOTIFBANK);

		// Load graphs
		motifBanks = new ArrayList<TileBank>();

		loadAllTileBanks();

		loadTextures();
		
		areaAccessor = new OneFloorAreaAccessor();	// Default one : 1 floor
	}
	
	public void cleanUp()
	{
		meshFORE.cleanUp();
		meshBACK.cleanUp();
		meshBACK2.cleanUp();

		initialized = false;
	}

	public abstract void loadTextures();
	
	// /////////////////////////////////////////////////////////////////////////////////////
	// charge_tous_les_motifs
	// /////////////////////////////////////////////////////////////////////////////////////
	// Load every tile banks (but doesn't create any texture)
	// /////////////////////////////////////////////////////////////////////////////////////
	void loadAllTileBanks() {
		for (String bankName : tileBankNames) {
			loadTileBank(bankName.toLowerCase());
		}
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// loadTileBank
	// /////////////////////////////////////////////////////////////////////////////////////
	// IN:filename to load as a tile bank
	// /////////////////////////////////////////////////////////////////////////////////////
	private void loadTileBank(String filename) {
		TileBank motifBank = new TileBank();

		motifBank.charge_motifs(filename);

		motifBanks.add(motifBank);
	}

	public TileBank getMotifBank(int n) {
		return motifBanks.get(n);
	}

	public abstract void render(int floor, boolean backGround);

	// Prepare vertices and indices for drawing tiles
	public void prepareTiles() {
		initialized=true;
		meshBACK2.clearBuffers();
		meshBACK.clearBuffers();
		meshFORE.clearBuffers();
	}
	
	// Redraw all tiles with updating VertexBuffers.
	// No need to access particular tile, because we draw every one.
	// **BUT THIS COULD BE DANGEROUS WHEN A TILE SWITCHES FROM ONE BANK TO
	// ANOTHER**
	public void updateTiles(Point cameraNew, Area[] p_areas, int compteur_animation) {

		if (meshBACK != null && cameraX != -1 && cameraY != -1) {

			meshBACK.startInitialization();
			meshBACK2.startInitialization();
			meshFORE.startInitialization();
			
			int previousX = cameraX >> 4;
			int previousY = cameraY >> 4;
			int tileStartX = cameraNew.x >> 4;
			int tileStartY = cameraNew.y >> 4;
			int tileEndX = tileStartX + (Zildo.viewPortX >> 4);
			int tileEndY = tileStartY + (Zildo.viewPortY >> 4);
			
			if (previousX != tileStartX || previousY != tileStartY) {
				meshBACK.initFreeBuffer(cameraNew);
				meshBACK2.initFreeBuffer(cameraNew);
				meshFORE.initFreeBuffer(cameraNew);
			}
			for (Area theMap : p_areas) {
				if (theMap == null) {
					break;
				}
				Point offset = theMap.getOffset();

				boolean yOut = false;
				boolean xOut = false;

				int sizeX = theMap.getDim_x();
				int sizeY = theMap.getDim_y();
				int dx = sizeX;
				int dy = sizeY;
				if (offset.x > 0) {
					dx = offset.x >> 4; 
				}
				if (offset.y > 0) {
					dy = offset.y >> 4;
				}

				areaAccessor.setArea(theMap);
				
				for (int ay = 0; ay < sizeY; ay++)
				{
					int y = ay + (offset.y >> 4);
					yOut = (y < tileStartY || y > tileEndY+1);
					if (yOut) {
						continue;
					}

					for (int ax = 0; ax < sizeX; ax++)
					{
						int x = ax + (offset.x >> 4);
						xOut = x < tileStartX || x > tileEndX;
						if (xOut) {
							continue;
						}
						int n_motif = 0, n_animated_motif;
						// Get corresponding case on the map

						AccessedCase accCase = areaAccessor.get_mapcase((x+dx) % dx, (y+dy) % dy);
						Case mapCase = accCase.c;
						int floor = accCase.floor;
						
						if (mapCase != null) {
							boolean changed = mapCase.isModified();
							Tile back = mapCase.getBackTile();
							n_motif = back.index;
							n_animated_motif = mapCase.getAnimatedMotif(compteur_animation);
							if (n_animated_motif != back.renderedIndex) {
								changed = true;
								n_motif = n_animated_motif;
								back.renderedIndex = n_motif;
							}
							meshBACK.updateTile(back, x, y, floor, n_motif, changed);
							
							Tile back2 = mapCase.getBackTile2();
							if (back2 != null) {
								meshBACK2.updateTile(back2, x, y, floor, back2.index, changed);
							} else if (mapCase.isBack2Removed()) {
								meshBACK2.removeTile(mapCase.getBack2Removed(), x, y, floor);
								mapCase.clearBack2Removed();
							}
							
							Tile fore = mapCase.getForeTile();
							if (fore != null) {
								meshFORE.updateTile(fore, x, y, floor, fore.index, changed);

							}
							mapCase.setModified(false);
						}
					}
				}
			}
			meshBACK.endInitialization();
			meshBACK2.endInitialization();
			meshFORE.endInitialization();
		}

		cameraX = cameraNew.x;
		cameraY = cameraNew.y;

	}

	/**
	 * Return the bank's index in loaded ones from a given name
	 * 
	 * @param p_name
	 * @return int
	 */
	public static int getBankFromName(String p_name) {
		int i = 0;
		for (String s : tileBankNames) {
			if (s.equalsIgnoreCase(p_name)) {
				return i;
			}
			i++;
		}
		throw new RuntimeException("Bank " + p_name + " doesn't exist.");
	}

	/**
	 * Return the bank's name by index
	 * 
	 * @param nBank
	 * @return String
	 */
	public String getBankNameFromInt(int nBank) {
		return tileBankNames[nBank];
	}
	
	public void saveTextures() {
		// Default : do nothing. Only LWJGL version can do that.
	}
	
	public void setAreaAccessor(AreaAccessor aa) {
		areaAccessor = aa;
		prepareTiles();
	}
}
