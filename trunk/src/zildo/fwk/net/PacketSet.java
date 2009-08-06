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
