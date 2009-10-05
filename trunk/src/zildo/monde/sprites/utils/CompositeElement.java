package zildo.monde.sprites.utils;

import java.util.ArrayList;
import java.util.List;

import zildo.monde.collision.Collision;
import zildo.monde.map.Point;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.SpriteModel;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.elements.Element;
import zildo.server.EngineZildo;

/**
 * Elements composition.
 * 
 * Used for sophisticated animations like explosions.
 * 
 * Basically, it's a square of 4 identical elements, performing a perfect symetry, using the 'reverse' attribute
 * provided by SpriteEntity.
 * 
 * There is a 'gap' attribute to add space between the elements.
 * 
 * Example: Square with gapX=1, gapY=1 is a set of 4 symetrical sprites almost sticked together, 2 pixels spaced.
 * 
 * @author tchegito
 *
 */
public class CompositeElement {

	enum Shape {
		SQUARE, DISPERSED;
	}
	
	List<Element> composite;

	Shape shape;
	
	int gapX, gapY;	// Space in pixels between middle and element
	
	public CompositeElement(Element p_refElement) {
		composite=new ArrayList<Element>();
		composite.add(p_refElement);
		shape=null;
	}
	
	public boolean isSquare() {
		return shape == Shape.SQUARE;
	}
	
	/**
	 * Duplicate the initial element (called 'Referenced' further) in 3 other symetrical sprites to get a square.
	 * 
	 * @param p_gapX Horizontal space between sprites
	 * @param p_gapY Vertical space between sprites
	 */
	public void squareShape(int p_gapX, int p_gapY) {
		if (isSquare()) {
			return;	// Already square shaped
		}
		if (composite.size() != 1) {
			throw new RuntimeException("Impossible to transform to square : we need just one element at this time.");
		}
		Element refElement=composite.get(0);
		SpriteModel model=refElement.getSprModel();
		refElement.x-=p_gapX + model.getTaille_x() / 2;
		refElement.y-=p_gapY + model.getTaille_y() / 2;
		Element copyRight=new Element(refElement);
		Element copyBottomRight=new Element(refElement);
		Element copyBottomLeft=new Element(refElement);
		
		// Adjust locations
		copyRight.x+=2*p_gapX + model.getTaille_x();
		copyBottomRight.x+=2*p_gapX + model.getTaille_x() + p_gapX;
		copyBottomRight.y+=2*p_gapY + model.getTaille_y() + p_gapY;
		copyBottomLeft.y+=2*p_gapY + model.getTaille_y() + p_gapY;
		
		// Reverse
		copyRight.reverse=SpriteEntity.REVERSE_HORIZONTAL;
		copyBottomRight.reverse=SpriteEntity.REVERSE_HORIZONTAL | SpriteEntity.REVERSE_VERTICAL;
		copyBottomLeft.reverse=SpriteEntity.REVERSE_VERTICAL;
		
		// Spawn
		EngineZildo.spriteManagement.spawnSprite(copyRight);
		EngineZildo.spriteManagement.spawnSprite(copyBottomRight);
		EngineZildo.spriteManagement.spawnSprite(copyBottomLeft);
		
		// Update object
		composite.add(copyRight);
		composite.add(copyBottomRight);
		composite.add(copyBottomLeft);
		shape=Shape.SQUARE;
		gapX=p_gapX;
		gapY=p_gapY;
	}
	
	private static final int[] squareShiftX={-1,0,0,-1};
	private static final int[] squareShiftY={-1,-1,0,0};
	
	/**
	 * Propagate a new sprite model to the entire composition.
	 * Shift sprites if the new size is different so that animation center is keeped.
	 * @param p_desc
	 * @param p_addSpr
	 */
	public void setSprModel(ElementDescription p_desc, int p_addSpr) {
		Element refElement=composite.get(0);
		SpriteModel modelBefore=refElement.getSprModel();

		refElement.setSprModel(p_desc, p_addSpr);
		SpriteModel modelAfter=refElement.getSprModel();
		
		int shiftX=modelAfter.getTaille_x() - modelBefore.getTaille_x();
		int shiftY=modelAfter.getTaille_y() - modelBefore.getTaille_y();
		
		int n=0;
		for (Element elmt : composite) {
			elmt.setSprModel(p_desc, p_addSpr);
			elmt.x+=shiftX * squareShiftX[n];
			elmt.y+=shiftY * squareShiftY[n];
			elmt.setAjustedX((int) elmt.x);
			elmt.setAjustedY((int) elmt.y);
			n++;
		}
	}
	
	/**
	 * Remove every element of this composition.
	 * @param p_keepRef TRUE=keep the referenced element, and put it at his initial location.
	 */
	public void die(boolean p_keepRef) {
		for (int i=0;i<composite.size();i++) {
			Element elmt=composite.get(i);
			if (i!=0 || !p_keepRef) {
				elmt.dying=true;
				elmt.visible=false;
			}
		}
		if (p_keepRef) {
			// Kill every element, except the referenced one
			Element refElement=composite.get(0);
			SpriteModel model=refElement.getSprModel();
			refElement.x+=gapX + model.getTaille_x() / 2;
			refElement.y+=gapY + model.getTaille_y() / 2;

			composite.clear();
			composite.add(refElement);
		} else {
			composite.clear();
		}
		shape=null;
	}
	
    public Collision getCollision() {
    	if (composite.size() == 0) {
    		return null;
    	}
        Element refElement = composite.get(0);
        // Determine the bottom right corner of composite
        Point topLeft = new Point((int) refElement.x, (int) refElement.y);
        SpriteModel model = refElement.getSprModel();
        
        Point bottomRight = new Point(topLeft);
        for (Element elmt : composite) {
            int right = (int) elmt.x;
            int bottom = (int) elmt.y;
            if (right > bottomRight.x) {
                bottomRight.x = right;
            }
            if (bottom > bottomRight.y) {
                bottomRight.y = bottom;
            }
        }

        Point size = new Point(bottomRight.x - topLeft.x, bottomRight.y - topLeft.y);
        size=size.translate(model.getTaille_x(), model.getTaille_y());
        Point center=new Point((topLeft.x + bottomRight.x) / 2, (topLeft.y + bottomRight.y) /2);
        center=center.translate(0, -model.getTaille_y() / 2);
        return new Collision(center, size, null, refElement.getDamageType());
    }
}