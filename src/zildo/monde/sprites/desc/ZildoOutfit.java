package zildo.monde.sprites.desc;

import zildo.monde.map.Point;

/**
 * Different outfits for Zildo.<p/>
 * 
 * It's made of color replacements, from the classic Zildo (blue tunic and red hat)
 * 
 * @author eboussaton
 *
 */
public enum ZildoOutfit {

    
    Zildor(new Point(223, 174), new Point(162, 218),    // tunic
	                new Point(150, 253),    // Hairs
	                new Point(111, 123), new Point(107, 208), new Point(246, 192)),	// Hat
    
    Zildoriginal(new Point(223, 249), new Point(162, 250),
	    	 new Point(150, 136),
	    	 new Point(111, 251), new Point(107, 252),
	    	 new Point(45, 210)),	// Belt
    
    Pingouin(new Point(223, 96), new Point(162, 6),
	    new Point(150, 218), // 34
	    new Point(180, 86),	// Skin
	     new Point(111, 163), new Point(107, 164), new Point(246, 80),
	     new Point(45, 240));
    
    public Point[] transforms;
    
    private ZildoOutfit(Point... p_transforms) {
	transforms = p_transforms;
    }
}
