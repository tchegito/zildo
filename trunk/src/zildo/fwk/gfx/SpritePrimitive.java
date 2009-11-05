package zildo.fwk.gfx;
// SpritePrimitive.cpp: implementation of the SpritePrimitive class.
//
//////////////////////////////////////////////////////////////////////
// Provides a set of sprite from the same 256x256 texture..
// This is segmented for performance issues, to avoid much access to setTexture.
//////////////////////////////////////////////////////////////////////


public class SpritePrimitive extends TilePrimitive {

	private int nbQuadsRendered;
	private int numQuadSynchronizing;	// To know the synchronizeSprite situation
	public boolean locked;



	//////////////////////////////////////////////////////////////////////
	// Construction/Destruction
	//////////////////////////////////////////////////////////////////////
	
	public SpritePrimitive() {
		super();
	}
	
	public SpritePrimitive(int numPoints, int numIndices){
		super(numPoints, numIndices);
		// VB/IB aren't locked at start
		locked=false;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// startInitialization
	///////////////////////////////////////////////////////////////////////////////////////
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
		return super.addTileSized((int)x,(int)y,xTex,yTex,sizeX,sizeY);
	
	}
	
	public int synchronizeSprite(float x, float y, float xTex, float yTex, int sizeX, int sizeY)
	{
		// Do not increase indice's and vertice's count
		nPoints-=4;
		nIndices-=6;
		super.addTileSized((int)x,(int)y,xTex,yTex,sizeX,sizeY);
		return (numQuadSynchronizing++)*4;
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
	
		super.renderPartial(nbQuadsRendered, nbQuads);
		nbQuadsRendered+=nbQuads;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// render
	///////////////////////////////////////////////////////////////////////////////////////
	// Render every sprite from this primitive
	///////////////////////////////////////////////////////////////////////////////////////
	public void render()
	{
		super.render();
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// buildIndexBuffer
	///////////////////////////////////////////////////////////////////////////////////////
	// IN : quad order (ex: {1,3,6,2,-1}
	///////////////////////////////////////////////////////////////////////////////////////
	// Build index buffer as described in the received order sequence.
	// Always -1 marks the end of sequence.
	///////////////////////////////////////////////////////////////////////////////////////
	public void buildIndexBuffer(int quadOrder[]) {
	
		startInitialization();
		
		// 3 Indices
		int i=0;
		while (true) {
	
			// Get the first quad's vertex
			int numQuad=quadOrder[i];
			if (numQuad == -1)
				break;
	
			// Tile's first triangle
			if (bufs.indices.position() == bufs.indices.limit()) {
				bufs.indices.limit(bufs.indices.position() + 6);
			}
			bufs.indices.put(numQuad).put(numQuad+1).put(numQuad+2);
			// Tile's second triangle
			bufs.indices.put(numQuad+1).put(numQuad+3).put(numQuad+2);
			i++;
		}
	
		endInitialization();
		
	}

}