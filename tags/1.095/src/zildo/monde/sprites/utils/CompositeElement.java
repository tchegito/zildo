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

package zildo.monde.sprites.utils;

import java.util.ArrayList;
import java.util.List;

import zildo.monde.collision.Collision;
import zildo.monde.map.Point;
import zildo.monde.sprites.Reverse;
import zildo.monde.sprites.SpriteModel;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.persos.Perso;
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
        SQUARE, DISPERSED, FOLLOWING;
    }
	
	List<Element> composite;

	Shape shape;
	
	int gapX, gapY;	// Space in pixels between middle and element
	
	public CompositeElement(Element p_refElement) {
		composite=new ArrayList<Element>();
		composite.add(p_refElement);
		shape=null;
	}
	
	/**
	 * Duplicate the initial element (called 'Referenced' further) in 3 other symetrical sprites to get a square.
	 * 
	 * @param p_gapX Horizontal space between sprites
	 * @param p_gapY Vertical space between sprites
	 */
	public void squareShape(int p_gapX, int p_gapY) {
		if (shape == Shape.SQUARE) {
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
		copyRight.reverse=Reverse.HORIZONTAL;
		copyBottomRight.reverse=Reverse.ALL;
		copyBottomLeft.reverse=Reverse.VERTICAL;
		
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
	
    public void followShape() {
        if (shape == Shape.FOLLOWING) {
            return; // Already following shaped
        }
        Element refElement = composite.get(0);
        Element copy1 = new Element(refElement);
        Element copy2 = new Element(refElement);
        copy1.setAddSpr(-1);
        copy2.setAddSpr(-2);

        // Spawn
        EngineZildo.spriteManagement.spawnSprite(copy1);
        EngineZildo.spriteManagement.spawnSprite(copy2);

        composite.add(copy1);
        composite.add(copy2);

        shape = Shape.FOLLOWING;
    }
    
    public void animate() {
        if (composite.size() <= 1) {
            return;
        }
        switch (shape) {
            case FOLLOWING:
                Element refElement = composite.get(0);
                Element elem1 = composite.get(1);
                Element elem2 = composite.get(2);
                elem1.visible = refElement.visible;
                elem2.visible = refElement.visible;
                if (refElement.visible) {
                    elem2.x = elem1.x;
                    elem2.y = elem1.y;
                    elem2.z = elem1.z;
                    elem1.x = refElement.x;
                    elem1.y = refElement.y;
                    elem1.z = refElement.z;

                    elem1.y -= elem1.getSprModel().getTaille_y() / 2;
                    elem2.y -= elem2.getSprModel().getTaille_y() / 2;
                }
                break;
          }
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
        // I had to comment this line to bomb have correct collision region.
        // But it's weird ! Because nothing changed about this before.
        //center=center.translate(0, -model.getTaille_y() / 2);
        return new Collision(center, size, (Perso) refElement.getLinkedPerso(), refElement.getDamageType(), null);
    }
    
    /**
     * Returns the intial element of this composition.
     * @return Element
     */
    public Element getRefElement() {
    	return composite.get(0);
    }
}