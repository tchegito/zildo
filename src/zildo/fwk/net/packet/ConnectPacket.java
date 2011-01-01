/**
 * Legend of Zildo
 * Copyright (C) 2006-2011 Evariste Boussaton
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

package zildo.fwk.net.packet;

import zildo.fwk.file.EasyBuffering;
import zildo.fwk.net.Packet;

/**
 * Packet intended to join/leave a game.
 * 
 * @author tchegito
 *
 */
public class ConnectPacket extends Packet {

	boolean connect;	// TRUE=client is joining / FALSE=client is leaving
	String playerName;
	int version;
	
	/**
	 * Empty constructor (called by {@link Packet#receive(java.nio.ByteBuffer)})
	 */
	public ConnectPacket() {
		super();
	}
	
	public ConnectPacket(boolean p_connect, String p_playerName, int p_numVersion) {
		super();
		connect=p_connect;
		playerName=p_playerName;
		version=p_numVersion;
	}
	
	public boolean isJoining() {
		return connect;
	}
	
	public String getPlayerName() {
		return playerName;
	}
	
	public int getVersion() {
	    return version;
	}
	
	@Override
	protected void buildPacket() {
		b.put(connect);
		b.put(playerName);
		b.put(version);
	}

	@Override
	protected void deserialize(EasyBuffering p_buffer) {
		connect=p_buffer.readBoolean();
		playerName=p_buffer.readString();
		if (p_buffer.eof()) {	// Previous releases didn't provide the client version. So we deduce that the version is bad.
		    version=0;
		} else {
		    version=p_buffer.readInt();
		}
	}
}
