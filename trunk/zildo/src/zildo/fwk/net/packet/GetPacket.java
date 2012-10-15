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

package zildo.fwk.net.packet;

import java.nio.ByteBuffer;

import zildo.fwk.file.EasyBuffering;
import zildo.fwk.net.Packet;
import zildo.fwk.net.packet.AskPacket.ResourceType;

public class GetPacket extends Packet {

	public String name;	// Only used for MAP resource type
	private ByteBuffer buffer;
	public ResourceType resourceType;
	
	/**
	 * Empty constructor (called by {@link Packet#receive(java.nio.ByteBuffer)})
	 */
	public GetPacket() {
		super();
	}
	
	public GetPacket(ResourceType p_resType, ByteBuffer p_buffer, String p_name) {
		buffer=p_buffer;
		name=p_name;
		resourceType=p_resType;
		buffer.flip();
	}
	
	@Override
    protected void buildPacket() {
        buffer.position(0);

        b.put((byte) resourceType.ordinal());
        if (resourceType == ResourceType.MAP) {
            b.put(name);
        }
        b.put(buffer);
    }
	
	@Override
	protected void deserialize(EasyBuffering p_buffer) {
		resourceType=ResourceType.values()[p_buffer.readByte()];
		if (resourceType == ResourceType.MAP) {
		    name=p_buffer.readString();
		}
		buffer=ByteBuffer.allocate(p_buffer.getAll().limit());
		buffer.put(p_buffer.getAll());
		buffer.flip();
	}
	
	public ByteBuffer getBuffer() {
		return buffer;
	}

}
