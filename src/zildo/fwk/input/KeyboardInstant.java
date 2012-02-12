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

package zildo.fwk.input;

import java.util.EnumMap;

import zildo.fwk.file.EasyBuffering;
import zildo.fwk.file.EasySerializable;
import zildo.resource.KeysConfiguration;

public class KeyboardInstant implements EasySerializable {

	EnumMap<KeysConfiguration, Boolean> kbdInstant;
	boolean[] bools = new boolean[KEYS_LENGTH];

	static int KEYS_LENGTH = KeysConfiguration.values().length;

	static EasyBuffering buf = new EasyBuffering(KEYS_LENGTH * 4);

	public KeyboardInstant() {
		kbdInstant = new EnumMap<KeysConfiguration, Boolean>(
				KeysConfiguration.class);
		// Default : all keys are unpressed
		for (KeysConfiguration key : KeysConfiguration.values()) {
			kbdInstant.put(key, false);
		}
	}

	public KeyboardInstant(EnumMap<KeysConfiguration, Boolean> p_keys) {
		kbdInstant = p_keys;
	}

	public boolean isKeyDown(KeysConfiguration key) {
		return kbdInstant.get(key);
	}

	/**
	 * Update keyboard state
	 */
	public void update() {
		for (KeysConfiguration key : KeysConfiguration.values()) {
			kbdInstant.put(key, KeyboardHandler.isKeyDown(key.code));
		}
	}

	public EasyBuffering serialize() {
		serialize(buf);
		return buf;
	}

	/**
	 * Serialize this object into a ByteBuffer
	 * 
	 * @return EasyBuffering
	 */
	@Override
	public void serialize(EasyBuffering p_buffer) {
		p_buffer.clear();
		int index = 0;
		for (KeysConfiguration key : KeysConfiguration.values()) {
			bools[index++] = kbdInstant.get(key);
		}
		p_buffer.putBooleans(bools);
	}

	/**
	 * Deserialize a ByteBuffer into a KeyboardInstant object.
	 * 
	 * @param p_buffer
	 * @return
	 */
	public static KeyboardInstant deserialize(EasyBuffering p_buffer) {
		EnumMap<KeysConfiguration, Boolean> instant = new EnumMap<KeysConfiguration, Boolean>(
				KeysConfiguration.class);
		boolean[] bools = p_buffer.readBooleans(KEYS_LENGTH);
		int index = 0;
		for (KeysConfiguration key : KeysConfiguration.values()) {
			instant.put(key, bools[index++]);
		}
		return new KeyboardInstant(instant);
	}
}
