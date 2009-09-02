/**
 *
 */
package zildo.monde;

import zildo.fwk.file.EasyBuffering;
import zildo.fwk.net.TransferObject;
import zildo.monde.map.Point;

/**
 * @author tchegito
 */

public class WaitingSound {
    public String name;
    public Point location; // (0..64, 0..64) coordinates
    public TransferObject client;
    public boolean broadcast;	// TRUE=this sound is for all clients / FALSE=just one client (GUI sound)
    
    private static EasyBuffering buf=new EasyBuffering(40);
    
    public WaitingSound(String p_name, Point p_location, boolean p_broadcast, TransferObject p_client) {
        name = p_name;
        location = p_location;
        client = p_client;
        broadcast = p_broadcast;
    }

    public EasyBuffering serialize() {
        buf.clear();
        buf.put(name);
        buf.put(location.getX());
        buf.put(location.getY());
        return buf;
    }

    public static WaitingSound deserialize(EasyBuffering p_buffer) {
        String name = p_buffer.readString();
        int x = p_buffer.readInt();
        int y = p_buffer.readInt();
        WaitingSound s = new WaitingSound(name, new Point(x, y), false, null);
        return s;
    }
}
