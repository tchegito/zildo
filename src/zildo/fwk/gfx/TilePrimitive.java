package zildo.fwk.gfx;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.GL11;

import zildo.fwk.opengl.OpenGLStuff;

// TilePrimitive.cpp: implementation of the TilePrimitive class.
//
// Class describing the TileEngine main element :
// -set of vertices
// -set of indices
//
// It's used to build a set of polygon from an initial position (x,y) with texture
// coordinates.
//
// The vertices are transformed and lighted.
//
// All polygons from this class are referring to the same texture sized 256x256.
//
// V1.0 : VertexBuffer and IndexBuffer are used
//		  .Need optimization
//////////////////////////////////////////////////////////////////////


public class TilePrimitive extends OpenGLStuff {
	
	// Class variables
	protected int nPoints;
	protected int nIndices;
	private boolean isLock;

    protected FloatBuffer vertices;
    protected FloatBuffer normals;
    protected FloatBuffer textures;
    protected IntBuffer indices;
    
    private int vertexBufferId;
    private int normalBufferId;
    private int textureBufferId;
    private int indiceBufferId;
    
    private int textureSizeX=256;
    private int textureSizeY=256;
    
	//////////////////////////////////////////////////////////////////////
	// Construction/Destruction
	//////////////////////////////////////////////////////////////////////
	
	public TilePrimitive()
	{	// Should never been called
		nPoints=0;
		nIndices=0;
	}
	
	public TilePrimitive(int numPoints, int numIndices)
	{
		initialize(numPoints, numIndices);
	}
	
	private void initialize(int numPoints, int numIndices) {
		// Initialize VBO IDs
		vertexBufferId=createVBO();
		normalBufferId=createVBO();
		textureBufferId=createVBO();
		indiceBufferId=createVBO();
	
		// Allocate buffers
		int numFaces=numIndices / 3;
		vertices=BufferUtils.createFloatBuffer(3*numPoints);
		normals=BufferUtils.createFloatBuffer(3*numFaces);
		indices=BufferUtils.createIntBuffer(3*numIndices);
		textures=BufferUtils.createFloatBuffer(2*numPoints);
	
		nPoints=0;
		nIndices=0;
	
		// Generate all indices at primitve instanciation (it never change)
		generateAllIndices(numIndices);
	}
	
	public TilePrimitive(int numPoints, int numIndices, int texSizeX, int texSizeY) {
		textureSizeX=texSizeX;
		textureSizeY=texSizeY;
		initialize(numPoints, numIndices);
	}
	
	public void finalize()
	{
		IntBuffer buf = BufferUtils.createIntBuffer(1);
		buf.put(vertexBufferId);
		buf.flip();
		ARBVertexBufferObject.glDeleteBuffersARB(buf);
		buf = BufferUtils.createIntBuffer(1);
		buf.put(textureBufferId);
		buf.flip();
		ARBVertexBufferObject.glDeleteBuffersARB(buf);
		buf = BufferUtils.createIntBuffer(1);
		buf.put(normalBufferId);
		buf.flip();
		ARBVertexBufferObject.glDeleteBuffersARB(buf);
		buf = BufferUtils.createIntBuffer(1);
		buf.put(indiceBufferId);
		buf.flip();
		ARBVertexBufferObject.glDeleteBuffersARB(buf);
	
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// startInitialization
	///////////////////////////////////////////////////////////////////////////////////////
	// Lock VertexBuffer to gain access to data
	///////////////////////////////////////////////////////////////////////////////////////
	public void startInitialization() {

	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// endInitialization
	///////////////////////////////////////////////////////////////////////////////////////
	public void endInitialization() {
		if (vertices.position() != 0) {
			// On se repositionne à zéro uniquement si on y est pas déjà
	    	vertices.flip();
		}
		if (normals.position() != 0) {
	    	normals.flip();
		}
		if (textures.position() != 0) {
	    	textures.flip();
		}
		if (indices.position() != 0) {
	    	indices.flip();
		}
		
    	bufferData(vertexBufferId, vertices);
    	bufferData(normalBufferId, normals);
    	bufferData(textureBufferId, textures);
    	bufferData(indiceBufferId, indices);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// renderPartial
	///////////////////////////////////////////////////////////////////////////////////////
	// IN : startingQuad, number of quads to render
	///////////////////////////////////////////////////////////////////////////////////////
	// Ask OpenGL to render quad from this mesh, from a position to another
	///////////////////////////////////////////////////////////////////////////////////////
	void renderPartial(int startingQuad, int nbQuadsToRender) {
	    int position=indices.position();
	    int saveNIndices=nIndices;
	    int limit=indices.limit();

	    indices.position(startingQuad * 6);
	    nIndices=nbQuadsToRender * 6 + startingQuad * 6;
		render();
		indices.position(position);
		nIndices=saveNIndices;
	    indices.limit(limit);

		/*
	    ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ELEMENT_ARRAY_BUFFER_ARB, indiceBufferId);
	    GL12.glDrawRangeElements(GL11.GL_TRIANGLES, startingQuad * 2, 
	    										2*(nbQuadsToRender + startingQuad) -1, 
	    										nIndices * 3, GL11.GL_UNSIGNED_INT, 0);
	    										*/
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// render
	///////////////////////////////////////////////////////////////////////////////////////
	// Ask OpenGL to render every quad from this mesh
	///////////////////////////////////////////////////////////////////////////////////////
	public void render() {
		
	    GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
	    GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
	    GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

	    ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, vertexBufferId);
	    GL11.glVertexPointer(3, GL11.GL_FLOAT, 0, 0);
	   
	    ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, normalBufferId);
	    GL11.glNormalPointer(GL11.GL_FLOAT, 0, 0);

	    ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, textureBufferId);
	    GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 0, 0);
		
	    // Le buffer d'indices contient les indices pour 4096 tiles. On doit le limiter au nombre de tiles
	    // réellement utilisé.
	    indices.limit(nIndices);
	    GL11.glDrawElements(GL11.GL_TRIANGLES, indices);

	}

	/**
	 * Add standard tile : 16x16
	 * @return position in vertices buffer for the added tile's first vertex
	 */
	public int addTile(int x, int y, float u, float v)
	{
		int nTileToReturn=nPoints;
		addTileSized(x,y,u,v,16,16);
		return nTileToReturn;
	}
	
	private void putTileSized(float x, float y, float sizeX, float sizeY, float xTex, float yTex) {
		// 4 Vertices
		if (vertices.position() == vertices.limit()) {
			// On rajoute une place
			vertices.limit(vertices.position() + 3 * 4);
			textures.limit(textures.position() + 2 * 4);
		}
		for (int i=0;i<4;i++)
		{
			vertices.put(x + sizeX * ((float)(i % 2)));	// x
			vertices.put(y + sizeY * ((float)(i / 2))); // y
			vertices.put(0.0f);							// z
			//tilePoints[quelPoint].color=D3DCOLOR_COLORVALUE(1,1,1,1);
	
			// Get right tile-texture
			//float texPosX=xTex+0.5f+16.0f*(sizeX-1.0f/16.0f)*(float)(i%2);
			//float texPosY=yTex+0.5f+16.0f*(sizeY-1.0f/16.0f)*(int)(i/2);
			float texPosX=xTex+sizeX*(float)(i%2);
			float texPosY=yTex+sizeY*(int)(i/2);
			
			textures.put(texPosX / (float) textureSizeX); //i % 2;
			//textures.put((float)(yTex+0.5f+(16*sizeY-1.0f)*(int)(i/2)) / 256.0f); //1 - (i % 2);
			textures.put(texPosY / (float) textureSizeY); //1 - (i % 2);
		}
	}
	
	// Return the quad position in Vertex Buffer
	protected int addTileSized(int x, int y, float xTex, float yTex, int sizeX, int sizeY)
	{
		putTileSized( x, y, sizeX, sizeY, xTex, yTex);

		nPoints+=4;
		nIndices+=6;
	
		return nPoints - 4;
	}
	
	public boolean isLock() {
		return isLock;
	}
	
	// Move a tile and reset its texture (don't change size)
	// **IMPORTANT** : VertexBuffer MUST BE locked
	public void updateTile(float x, float y, float u, float v)
	{
		// Get size
		int vBufferPos=vertices.position(); // - 3*4;
		//int tBufferPos=textures.position(); // - 2*4;
		
		float sizeX=vertices.get(vBufferPos + 3) - vertices.get(vBufferPos);
		float sizeY=vertices.get(vBufferPos + 3*2 + 1) - vertices.get(vBufferPos + 1);
	
		// Move tile
		putTileSized(x, y, sizeX, sizeY, u, v);

	}
	
	void removeTile(int numTile)
	{
	}
	
	// Generate indices for maximum tile authorized.
	// We can do this once for all, because every tiles is a quad made by 2 triangles
	// where indices are like this :
	// (v1,v2,v3) - (v2,v4,v3)
	void generateAllIndices(int numIndices)
	{
		// 3 Indices
		for (int i=0;i<(numIndices/6);i++) {
			// Tile's first triangle
			indices.put(i*4).put(i*4+1).put(i*4+2);
			// Tile's second triangle
			indices.put(i*4+1).put(i*4+3).put(i*4+2);
			
			// Two normals oriented accross the screen (0,0,-1)
			normals.put(0).put(i).put(1);
			normals.put(0).put(0).put(1);
		}
	}

	public int getNPoints() {
		return nPoints;
	}

	public void setNPoints(int points) {
		nPoints = points;
	}

	public int getNIndices() {
		return nIndices;
	}

	public void setNIndices(int indices) {
		nIndices = indices;
	}
	
	
	/*void translate(float diffx, float diffy) {
		if (nPoints>0) {
			startInitialization();
			for (int i=0;i<nPoints;i++) {
				if (pVertex!=null) {
					pVertex.x+=diffx;
					pVertex.y+=diffy;
				}
				pVertex++;
			}
			endInitialization();
		}
	}
	*/	
}

