/**
 *
 */
package zildo.monde.map;

import zildo.monde.map.Angle;

/**
 * @author tchegito
 */
public class TileInfo {

    public boolean walkable; // Entire tile is walkable.
    public boolean corner; // One quarter of the tile is walkable.
    public boolean cornerDiagonal; // One quarter + one half quarter (diagonal) is walkable
    public boolean half; // One half of the tile is walkable.
    public boolean inverse; // Inverse walkable and blocking part (in conjunction with 'corner' and 'corner diagonal')
    public boolean down;	// TRUE if tiles altitude decrease in 'blockAngle' direction
    public Angle blockAngle; // Indicates the collider angle. Can be diagonal

    public TileInfo() {
    	walkable=true;
    	corner=false;
    	cornerDiagonal=false;
    	half=false;
    	inverse=false;
    	blockAngle=null;
    	down=false;
    }
    
    /**
     * Return TRUE/FALSE depending on the given position (p_posX, p_posY) in (0..15,0..15)
     * @param p_posX
     * @param p_posY
     * @return boolean
     */
    public boolean collide(int p_posX, int p_posY) {

    	// 1- half
        if (half) {
            switch (blockAngle) {
                case NORD:
                    return (p_posY < 8);
                case NORDEST:
                    return (p_posY < p_posX);
                case EST:
                    return (p_posX > 7);
                case SUDEST:
                    return (p_posY > (16 - p_posX));
                case SUD:
                    return (p_posY > 7);
                case SUDOUEST:
                    return (p_posY > p_posX);
                case OUEST:
                    return (p_posX < 8);
                case NORDOUEST:
                    return (p_posY < (16 - p_posX));
            }
        }

        // 2- corner
        if (corner) { // Only diagonal angles
            boolean result = collideCorner(blockAngle, p_posX, p_posY);
            if (inverse) {
                result = !result;
            }
            return result;
        }

        // 3- cornerDiagonal
        if (cornerDiagonal) { // Only diagonal angles
            boolean result = collideCornerDiagonal(blockAngle, p_posX, p_posY);
            if (inverse) {
                result = !result;
            }
            return result;
        }

        // 4- walkable
        return !walkable;
    }

    private boolean collideCorner(Angle p_angle, int p_posX, int p_posY) {
        switch (p_angle) {
            case NORDEST:
                return (p_posX > 8 && p_posY < 8);
            case SUDEST:
                return (p_posX > 8 && p_posY > 8);
            case SUDOUEST:
                return (p_posX < 8 && p_posY > 8);
            case NORDOUEST:
                return (p_posX < 8 && p_posY < 8);
            default:
                throw new RuntimeException("No way to determine the tile collision !");
        }
    }

    /**
     * XXXX CornerDiagonal with angle=Angle.NORDEST
     * 0XXX
     * 0000
     * 0000
     * @param p_angle
     * @param p_posX
     * @param p_posY
     * @return
     */
    private boolean collideCornerDiagonal(Angle p_angle, int p_posX, int p_posY) {
        switch (p_angle) {
            case NORDEST:
                return (p_posX < 8 && p_posY < p_posX) || (p_posX > 7 && p_posY < 8); // Never used for now
            case SUDEST:
                return (p_posX < 8 && p_posY >= 16 - p_posX) || (p_posX > 7 && p_posY > 7);
            case SUDOUEST:
                return (p_posX > 7 && p_posY > p_posX) || (p_posX < 8 && p_posY > 7);
            case NORDOUEST:
                return (p_posX > 7 && p_posY >= 16 - p_posX) || (p_posX < 8 && p_posY < 8);
            default:
                throw new RuntimeException("No way to determine the tile collision !");
        }
    }
}