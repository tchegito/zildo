/**
 * Legend of Zildo
 * Copyright (C) 2006-2012 Evariste Boussaton
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


package zildo.monde.collision;

import zildo.monde.util.Point;
import zildo.monde.util.Zone;

/**
 * @author tchegito
 */
public class Rectangle {

    private Point[] coordinates;
    private Point center;
    private Point size;
    
    /**
     * Create a rectangle with a given center and a size.
     * @param p_center
     * @param p_size
     */
    public Rectangle(Point p_center, Point p_size) {
    	center=p_center;
        Point cornerTopLeft = new Point(p_center.x - p_size.x / 2, p_center.y - p_size.y / 2);
        Point cornerTopRight = cornerTopLeft.translate(p_size.x, 0);
        Point cornerBottomLeft = cornerTopLeft.translate(0, p_size.y);
        Point cornerBottomRight = cornerBottomLeft.translate(p_size.x, 0);
        coordinates = new Point[4];
        coordinates[0] = cornerTopLeft;
        coordinates[1] = cornerTopRight;
        coordinates[2] = cornerBottomLeft;
        coordinates[3] = cornerBottomRight;
        size=p_size;
    }
    
    public Rectangle(Zone p_zone) {
	coordinates = new Point[4];
	coordinates[0] = new Point(p_zone.x1, p_zone.y1);
	coordinates[1] = new Point(p_zone.x2+p_zone.x1, p_zone.y1);
	coordinates[2] = new Point(p_zone.x1, p_zone.y2+p_zone.y1);
	coordinates[3] = new Point(p_zone.x2+p_zone.x1, p_zone.y2+p_zone.y1);
	center = new Point((p_zone.x1 + p_zone.x2 / 2), (p_zone.y1 + p_zone.y2) / 2);
	size = new Point(p_zone.x2 - p_zone.x1, p_zone.y2 - p_zone.y1);
    }

    public void translate(int p_shiftX, int p_shiftY) {
    	for (int i=0;i<4;i++) {
    		coordinates[i]=coordinates[i].translate(p_shiftX, p_shiftY);
    	}
    	center=center.translate(p_shiftX, p_shiftY);
    }
    
    /**
     * Multiplies each corner of the rectangle.
     * @param p_factor
     */
    public void multiply(float p_factor) {
    	for (int i=0;i<4;i++) {
    		coordinates[i]=coordinates[i].multiply(p_factor);
    	}
    	center=center.multiply(p_factor);
    	size=size.multiply(p_factor);
    }
    
    /**
     * Zoom in/out the rectangle. Center remains at the same location.
     * @param p_factorX
     * @param p_factorY
     */
    public void scale(float p_factorX, float p_factorY) {
    	int x,y;
    	for (int i=0;i<4;i++) {
    		x=(int) ((coordinates[i].x - center.x) * p_factorX) + center.x;
    		y=(int) ((coordinates[i].y - center.y) * p_factorX) + center.y;
    		
    		coordinates[i]=new Point(x,y);
    	}
    	size=new Point(size.x * p_factorX, size.y * p_factorY);
    }
    
    public boolean isInside(Point p_point) {
    	if (p_point == null) {
    		return false;
    	}
        return (p_point.x >= coordinates[0].x && p_point.y >= coordinates[0].y && p_point.x <= coordinates[3].x && p_point.y <= coordinates[3].y);
    }

    /**
     * Check if the two rectangles are colliding.
     * @param p_other
     * @return boolean
     */
    public boolean isCrossing(Rectangle p_other) {
        return p_other.coordinates[0].x <= coordinates[3].x && coordinates[0].x <= p_other.coordinates[3].x &&
        	   p_other.coordinates[0].y <= coordinates[3].y && coordinates[0].y <= p_other.coordinates[3].y;
    }

    /**
     * Check if the given circle is colliding with our rectangle.
     * @param p_center
     * @param p_radius
     * @return boolean
     */
    public boolean isCrossingCircle(Point p_center, int p_radius) {
    	float dist=center.distance(p_center);
    	
    	// 1) Very close
    	if (dist < p_radius) {
    		return true;
    	}
    	
    	// 2) Too far away
    	if (dist > (p_radius+Math.max(size.x, size.y))) {
    		return false;
    	}
    	
    	// 3) Which side of the rectangle should we consider?
    	Line distLine=new Line(p_center, center);
    	int ob=0, oc=0;
    	for (int i=0;i<4;i++) {
    		Line side=new Line(coordinates[i], coordinates[(i+1) % 4]);
    		if (isInside(side.intersect(distLine))) {
    			// Intersection between this rectangle's side and the 'distLine', is inside the rectangle
    			if (i % 2 == 0) {
        			ob=size.y / 2;
    				oc=Math.abs(center.y - p_center.y);
    			} else {
    				ob=size.x / 2;
    				oc=Math.abs(center.x - p_center.x);
    			}
    			break;
    		}
    	}
    	// Do the Thales theorem
    	int oa=(int) (ob * dist / oc);

    	return dist <= (oa + p_radius);
    }
    
    public Point getSize() {
    	return size;
    }
    
    public Point getCornerTopLeft() {
    	return coordinates[0];
    }
    
    @Override
    public String toString() {
    	return coordinates[0].toString()+" - "+coordinates[3].toString();
    }
}
