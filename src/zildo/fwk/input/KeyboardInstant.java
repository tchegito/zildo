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

package zildo.fwk.input;

import java.util.EnumMap;

import org.lwjgl.input.Keyboard;

import zildo.fwk.file.EasyBuffering;
import zildo.fwk.file.EasySerializable;
import zildo.prefs.KeysConfiguration;

public class KeyboardInstant implements EasySerializable {

	EnumMap<KeysConfiguration, Boolean> kbdInstant;

	private static int KEYS_LENGTH=KeysConfiguration.values().length;
    
    private static EasyBuffering buf=new EasyBuffering(KEYS_LENGTH * 4);

	public KeyboardInstant(EnumMap<KeysConfiguration, Boolean> p_keys) {
		kbdInstant=p_keys;
	}
	
	public boolean isKeyDown(KeysConfiguration key) {
		return kbdInstant.get(key);
	}
	
	public static KeyboardInstant getKeyboardInstant() {
		EnumMap<KeysConfiguration, Boolean> instant=new EnumMap<KeysConfiguration, Boolean>(KeysConfiguration.class);
		for (KeysConfiguration key : KeysConfiguration.values()) {
			Boolean b=Boolean.FALSE;
			if (Keyboard.isKeyDown(key.code)) {
				b=Boolean.TRUE;
			}
			instant.put(key, b);
		}
		
		return new KeyboardInstant(instant);
	}
	
	public EasyBuffering serialize() {
		serialize(buf);
		return buf;
	}
	
	/**
	 * Serialize this object into a ByteBuffer
	 * @return EasyBuffering
	 */
	public void serialize(EasyBuffering p_buffer) {
        p_buffer.clear();
        for (KeysConfiguration key : KeysConfiguration.values()) {
			p_buffer.put(kbdInstant.get(key));
		}
	}
	
	/**
	 * Deserialize a ByteBuffer into a KeyboardInstant object.
	 * @param p_buffer
	 * @return
	 */
	public static KeyboardInstant deserialize(EasyBuffering p_buffer) {
		EnumMap<KeysConfiguration, Boolean> instant=new EnumMap<KeysConfiguration, Boolean>(KeysConfiguration.class);
		for (KeysConfiguration key : KeysConfiguration.values()) {
			boolean b=p_buffer.readBoolean();
			instant.put(key, b ? Boolean.TRUE : Boolean.FALSE);
		}
		return new KeyboardInstant(instant);
	}
}
