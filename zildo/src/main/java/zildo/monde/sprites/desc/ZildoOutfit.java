package zildo.monde.sprites.desc;

import zildo.fwk.bank.SpriteBank;
import zildo.monde.util.Point;

/**
 * Different outfits for Zildo.<p/>
 * 
 * It's made of color replacements, from the classic Zildo (blue tunic and red hat)
 * 
 * @author eboussaton
 *
 */
public enum ZildoOutfit implements Outfit {

    
	Zildo(),
	
    Zildor(new Point(223, 174), new Point(162, 218),    // tunic
	                new Point(150, 253),    // Hairs
	                new Point(111, 123), new Point(107, 208), new Point(246, 192)),	// Hat
    
    Zildoriginal(new Point(223, 249), new Point(162, 250),
	    	 new Point(150, 136),
	    	 new Point(111, 251), new Point(107, 252),
	    	 new Point(45, 210)),	// Belt
    
    Zildazur(new Point(223, 96), new Point(162, 6),
	    new Point(150, 218), // 34
	    new Point(180, 86),	// Skin
	     new Point(111, 163), new Point(107, 164), new Point(246, 80),
	     new Point(45, 240)),
	     
	Zildesbois(new Point(223, 16), new Point(162, 17),
			new Point(111, 251), new Point(107, 252)),
    
	Schtroumpf(new Point(223, 6), new Point(162, 61),
			new Point(45, 109),
			new Point(180, 115), new Point(243, 162),	// Skin
			new Point(254, 115), new Point(253, 162),	// Arm - hand
			new Point(111, 6), new Point(107, 61), new Point(246, 108)),
		
	Zildemon(new Point(223, 111), new Point(162, 107),
			new Point(45, 3),
			new Point(3, 106),	// Outline
			new Point(6, 111),	// Eyes
			new Point(150, 3)),
			
	Zildange(new Point(223, 96), new Point(162, 6),
			new Point(246, 166), 
			new Point(150, 254),
			new Point(180, 174), new Point(243, 132), 
			new Point(254, 96), new Point(253, 132),
			new Point(111, 96), new Point(107, 6));
    
    Point[] transforms;
    
    private ZildoOutfit(Point... p_transforms) {
    	transforms = p_transforms;
    }
    
    public Point[] getTransforms() {
	return transforms;
    }

    public int getNBank() {
    	if (this == Zildo) {
    		return SpriteBank.BANK_ZILDO;
    	} else {
    		return SpriteBank.BANK_ZILDOOUTFIT + ordinal() - 1;
    	}
    }
}
