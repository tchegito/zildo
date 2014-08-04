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

package zildo.fwk.net;

import java.util.HashSet;

import zildo.fwk.net.Packet.PacketType;

public class PacketSet extends HashSet<Packet> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PacketSet() {
		
	}
	
	/**
	 * Get the first packet from given type
	 * @param p_type
	 * @return Packet
	 */
	public Packet getUniqueTyped(PacketType p_type) {
		PacketSet set=getTyped(p_type);
		if (set.size()==0) {
			return null;
		} else {
			return set.iterator().next();
		}
	}
	
	/**
	 * Get all packets from same type.
	 * @param p_type
	 * @return PacketSet
	 */
	public PacketSet getTyped(PacketType p_type) {
		PacketSet set=new PacketSet();
		for (Packet p : this) {
			if (p.type == p_type) {
				set.add(p);
			}
		}
		return set;
	}
}
