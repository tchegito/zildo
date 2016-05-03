/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
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

package zildo.fwk.net.packet;

import zildo.fwk.file.EasyBuffering;
import zildo.fwk.net.Packet;

/**
 * Meaning of this packet:
 * 
 * The client need one resource. So he send this packet to the server to ask for the desired one.
 * @author tchegito
 *
 */
public class AskPacket extends Packet {

    public enum ResourceType {
    	MAP, MAP_PART, ENTITY, KEYBOARD, SOUND, DIALOG, CLIENTINFO; // Note: Keyboard, Dialog, Map part and Sound are never asked, only sent.		
    }
	
    public ResourceType resourceType;
    public boolean entire; // TRUE=client want entire entities list (just for ENTITY resource type)

    /**
     * Empty constructor (called by {@link Packet#receive(java.nio.ByteBuffer)})
     */
    public AskPacket() {
        super();
    }

    public AskPacket(ResourceType p_type, boolean p_entire) {
        super();
        resourceType = p_type;
        entire = p_entire;
    }

    @Override
    protected void buildPacket() {
        b.put((byte) resourceType.ordinal());
        if (resourceType == ResourceType.ENTITY) {
            b.put(entire);
        }
    }

    @Override
    protected void deserialize(EasyBuffering p_buffer) {
        resourceType = ResourceType.values()[p_buffer.readByte()];
        if (resourceType == ResourceType.ENTITY) {
            entire = p_buffer.readBoolean();
        }
    }
}