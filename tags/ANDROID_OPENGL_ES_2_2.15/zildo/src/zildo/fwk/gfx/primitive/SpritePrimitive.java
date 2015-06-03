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

package zildo.fwk.gfx.primitive;

import zildo.monde.sprites.Rotation;


/**
 * Provides a set of sprite from the same 256x256 texture..
 * This is segmented for performance issues, to avoid too much texture switching.
 * 
 * @author tchegito
 *
 */

public class SpritePrimitive extends QuadPrimitive {

	private int nbQuadsRendered;
	private int numQuadSynchronizing;	// To know the synchronizeSprite situation
	public boolean locked;



	//////////////////////////////////////////////////////////////////////
	// Construction/Destruction
	//////////////////////////////////////////////////////////////////////
	

	public SpritePrimitive(int numPoints){
		super(numPoints);
		// VB/IB aren't locked at start
		locked=false;
	}
	
	public SpritePrimitive(int numPoints, int numIndices, int texSizeX, int texSizeY) {
		super(numPoints, texSizeX, texSizeY);
	}
    
	///////////////////////////////////////////////////////////////////////////////////////
	// startInitialization
	///////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void startInitialization()
	{
		if (!locked) {
			super.startInitialization();
			locked=true;
			numQuadSynchronizing=0;
		} else {
			throw new RuntimeException("Unable to start initialization of SpritePrimitive : it is already running.");
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// endInitialization
	///////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void endInitialization()
	{
		if (locked) {
			super.endInitialization();
			locked=false;
		} else {
			throw new RuntimeException("Unable to end initialization of SpritePrimitive : it hasn't started.");
		}
	}
	
	// Ajout d'un sprite dans le mesh
	public int addSprite(float x, float y, float xTex, float yTex, int sizeX, int sizeY)
	{
		// Simpliest way to manage VB/IB locking, but need to improve !
		return super.addQuadSized((int)x,(int)y,xTex,yTex,sizeX,sizeY);
	
	}
	
	public void synchronizeSprite(float x, float y, float xTex, float yTex, int sizeX, int sizeY, int repeatX, int repeatY, 
			Rotation rotation, int zoom, boolean normalizeTex)
	{
		int yy = (int) y;
		int sx = sizeX; int sy = sizeY;
		if (rotation == Rotation.CLOCKWISE || rotation == Rotation.COUNTERCLOCKWISE) {
			sx = sizeY ; sy = sizeX;
		}
		for (int i=0;i<repeatY;i++) {
			int xx = (int) x;
			for (int j=0;j<repeatX;j++) {
				nPoints-=4;
				nIndices-=6;
				super.addSprite(xx, yy, xTex, yTex, sizeX, sizeY, rotation, zoom, normalizeTex);
				numQuadSynchronizing++;
				xx+=sx;
			}
			yy+=sy;
		}
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	// initRendering
	///////////////////////////////////////////////////////////////////////////////////////
	// Clear the variable containing the number of quads rendered yet.
	///////////////////////////////////////////////////////////////////////////////////////
	public void initRendering() {
		nbQuadsRendered=0;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// render
	///////////////////////////////////////////////////////////////////////////////////////
	// IN : number of quads to render
	///////////////////////////////////////////////////////////////////////////////////////
	// Render the given number of quad, and update quad
	///////////////////////////////////////////////////////////////////////////////////////
	public void render(int nbQuads) {
	
		renderPartial(nbQuadsRendered, nbQuads);
		nbQuadsRendered+=nbQuads;
	}
	
    
    /**
     * Ask OpenGL to render quad from this mesh, from a position to another
     * @param startingQuad starting quad
     * @param nbQuadsToRender number of quads to render
     */
    void renderPartial(int startingQuad, int nbQuadsToRender) {
        vbo.draw(bufs, startingQuad * 6, nbQuadsToRender * 6);
    }

}