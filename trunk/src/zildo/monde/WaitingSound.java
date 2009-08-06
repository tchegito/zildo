/**
 *
 */
package zildo.monde;

import zildo.fwk.file.EasyBuffering;
import zildo.monde.Point;

/**
 * @author tchegito
 */

public class WaitingSound {
    public String name;
    public Point location; // (0..64, 0..64) coordinates

    public WaitingSound(String p_name, Point p_location) {
        name = p_name;
        location = p_location;
    }

    public EasyBuffering serialize() {
        EasyBuffering b = new EasyBuffering(40);
        b.put(name);
        b.put(location.getX());
        b.put(location.getY());
        return b;
    }

    public static WaitingSound deserialize(EasyBuffering p_buffer) {
        String name = p_buffer.readString();
        int x = p_buffer.readInt();
        int y = p_buffer.readInt();
        WaitingSound s = new WaitingSound(name, new Point(x, y));
        return s;
    }
}
