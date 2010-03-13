/**
 * Legend of Zildo
 * Copyright (C) 2006-2010 Evariste Boussaton
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


package zildo.monde;

import zildo.client.SoundPlay.BankSound;
import zildo.fwk.file.EasyBuffering;
import zildo.fwk.net.TransferObject;
import zildo.monde.map.Point;

/**
 * @author tchegito
 */

public class WaitingSound {
    public BankSound name;
    public Point location; // (0..64, 0..64) coordinates
    public TransferObject client;
    public boolean broadcast;	// TRUE=this sound is for all clients / FALSE=just one client (GUI sound)
    
    private static EasyBuffering buf=new EasyBuffering(40);
    
    public WaitingSound(BankSound p_name, Point p_location, boolean p_broadcast, TransferObject p_client) {
        name = p_name;
        location = p_location;
        client = p_client;
        broadcast = p_broadcast;
    }

    public EasyBuffering serialize() {
        buf.clear();
        buf.put(name.ordinal());
        buf.put(location.getX());
        buf.put(location.getY());
        return buf;
    }

    public static WaitingSound deserialize(EasyBuffering p_buffer) {
    	BankSound name = BankSound.values()[p_buffer.readInt()];
        int x = p_buffer.readInt();
        int y = p_buffer.readInt();
        WaitingSound s = new WaitingSound(name, new Point(x, y), false, null);
        return s;
    }
}
