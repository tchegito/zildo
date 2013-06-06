/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
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
 * Server send an acknowledgement to client for him to join the game.
 * 
 * Send a new Zildo's id by the way.
 * 
 * @author tchegito
 *
 */
public class AcceptPacket extends Packet {

	public int zildoId;
	
	public AcceptPacket() {
	}
	
	public AcceptPacket(int p_zildoId) {
		zildoId=p_zildoId;
	}
	
	@Override
	protected void buildPacket() {
		b.put(zildoId);
	}
	
	@Override
	protected void deserialize(EasyBuffering p_buffer) {
		zildoId=p_buffer.readInt();
	}

}
