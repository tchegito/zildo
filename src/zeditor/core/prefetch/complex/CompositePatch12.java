package zeditor.core.prefetch.complex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import zildo.monde.map.Area;
import zildo.monde.map.Point;

/**
 * Composite of several AbstractPatch12, imbricated. Each one is greater than the previous one.
 * @author eboussaton
 *
 */
public class CompositePatch12 extends TraceDelegateDraw {

    List<AbstractPatch12> patchs;
    
    public CompositePatch12(AbstractPatch12... p_patch) {
	patchs = new ArrayList();
	int dimension = 3;
	for (AbstractPatch12 p : p_patch) {
	    patchs.add(p);
	    // A - B - B - C
	    // D           E
	    // D           E
	    // F - G - G - H
	    if (dimension > 3) {
		// Create the expanded pattern
		int[] pattern=new int[dimension*dimension];
		int[] originalPatch = p.getBigPatch();
		for (int i=0;i<dimension*dimension;i++) {
		    pattern[i]=-1;
		}
		pattern[0] = originalPatch[0];			// A
		pattern[dimension-1] = originalPatch[2];	// C
		for (int j = 1; j < dimension - 1; j++) {
		    pattern[j] = originalPatch[1];		// B
		    pattern[dimension * (dimension - 1) + j] = originalPatch[3 * 2 + 1];	// G

		    pattern[j * dimension] = originalPatch[3];	// D
		    pattern[j * dimension + dimension - 1] = originalPatch[3 + 2];	// E
		}
		pattern[dimension*(dimension-1)] = originalPatch[3*2];	// F
		pattern[dimension*(dimension-1) + dimension-1] = originalPatch[3*2 + 2];	// H
		
		p.setBigPatch(pattern);
	    }
	    dimension+=2;
	}
	Collections.reverse(patchs);
    }
    
    @Override
    public void draw(Area p_map, Point p_start) {
	for (AbstractPatch12 p : patchs) {
	    p.draw(p_map, p_start);
	    p_start.x++;
	    p_start.y++;
	}
    }

}
