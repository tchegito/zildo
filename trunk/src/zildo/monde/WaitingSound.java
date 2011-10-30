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

package zildo.monde;

import zildo.client.sound.AudioBank;
import zildo.client.sound.BankMusic;
import zildo.client.sound.BankSound;
import zildo.fwk.file.EasyBuffering;
import zildo.fwk.file.EasySerializable;
import zildo.fwk.net.TransferObject;
import zildo.monde.map.Point;

/**
 * @author tchegito
 */

public class WaitingSound implements EasySerializable {
	public AudioBank name;
	public boolean isSoundFX; // TRUE=soundFX / FALSE=music
	public Point location; // (0..64, 0..64) coordinates
	public TransferObject client;
	public boolean broadcast; // TRUE=this sound is for all clients / FALSE=just
								// one client (GUI sound)

	private static EasyBuffering buf = new EasyBuffering(40);

	public WaitingSound(AudioBank p_name, Point p_location,
			boolean p_broadcast, TransferObject p_client) {
		name = p_name;
		location = p_location;
		client = p_client;
		broadcast = p_broadcast;
	}

	@Override
	public void serialize(EasyBuffering p_buffer) {
		isSoundFX = name instanceof BankSound;
		p_buffer.clear();
		p_buffer.put(isSoundFX);
		p_buffer.put(name.ordinal());
		p_buffer.put(location.getX());
		p_buffer.put(location.getY());
	}

	public EasyBuffering serialize() {
		serialize(buf);
		return buf;
	}

	public static WaitingSound deserialize(EasyBuffering p_buffer) {
		boolean isSoundFX = p_buffer.readBoolean();
		int ord = p_buffer.readInt();
		AudioBank name;
		if (isSoundFX) {
			name = BankSound.values()[ord];
		} else {
			name = BankMusic.values()[ord];
		}
		int x = p_buffer.readInt();
		int y = p_buffer.readInt();
		WaitingSound s = new WaitingSound(name, new Point(x, y), false, null);
		return s;
	}
}
