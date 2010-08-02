package zildo.fwk.engine.debug;

import java.io.FileWriter;

import zildo.fwk.gfx.TilePrimitive;

public class TilePrimitiveDebug  extends TilePrimitive {

	public TilePrimitiveDebug(int numPoints) {
		super(numPoints);
	}
	
	public synchronized void writeFile(boolean p_writeIndice, boolean p_writeVertices) {
    	
		if (nIndices == 0 || nPoints == 0) {
			return;
		}
		
    	try {
	    	FileWriter file=new FileWriter("file"+System.identityHashCode(this));
	    	if (p_writeIndice) {
		    	for (int i=0;i<nIndices;i++) {
		    		StringBuffer line=new StringBuffer("face");
		    		line.append(i).append("=(");
		    		for (int j=0;j<6;j++) {
		    			line.append(bufs.indices.get());
		    			if (j!=5) {
		    				line.append(", ");
		    			}
		    		}
		    		file.append(line+")\n");
		    	}
		    	bufs.indices.flip();
	    	}
	    	
	    	if (p_writeVertices) {
		    	for (int i=0;i<nPoints;i++) {
		    		StringBuffer line=new StringBuffer("point");
		    		line.append(i).append("=(");
		    		for (int j=0;j<3;j++) {
		    			float value=bufs.vertices.get();
		    			line.append(value);
		    			if (j!=2) {
		    				line.append(", ");
		    			}
		    		}
		    		file.append(line+")\n");
		    	}
		    	bufs.vertices.flip();
	    	}
	    	file.close();
    	} catch (Exception e) {
    		
    	}
    }
}
