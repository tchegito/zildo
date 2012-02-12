/**
 * Legend of Zildo
 * Copyright (C) 2006-2012 Evariste Boussaton
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

package zildo.server.state;

import zildo.fwk.file.EasyBuffering;
import zildo.fwk.file.EasySerializable;

/**
 * Object manipulated by client. It's the simple view of the complete
 * ClientState, used by server only.
 * <p/>
 * Each time a player connects/leaves, or kills another one, server sends to all
 * client the updated object.
 * 
 * @author eboussaton
 */
public class PlayerState implements EasySerializable {

	// Deathmatch
	public int zildoId; // Player's ID in the game
	public String playerName;
	public int nDied = 0;
	public int nKill = 0;

	public PlayerState(String p_playerName, int p_zildoId) {
		zildoId = p_zildoId;
		playerName = p_playerName;
	}

	@Override
	public void serialize(EasyBuffering p_buf) {
		p_buf.put(playerName);
		p_buf.put(zildoId);
		p_buf.put(nDied);
		p_buf.put(nKill);
	}

	public static PlayerState deserialize(EasyBuffering p_buffer) {
		PlayerState s = new PlayerState(p_buffer.readString(),
				p_buffer.readInt());
		s.nDied = p_buffer.readInt();
		s.nKill = p_buffer.readInt();
		return s;
	}
}