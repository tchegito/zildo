package zildo.fwk;

import java.util.EnumMap;

import org.lwjgl.input.Keyboard;

import zildo.fwk.file.EasyBuffering;
import zildo.prefs.KeysConfiguration;

public class KeyboardInstant {

	EnumMap<KeysConfiguration, Boolean> kbdInstant;

	private static int KEYS_LENGTH=KeysConfiguration.values().length;
    
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
	
	/**
	 * Serialize this object into a ByteBuffer
	 * @return EasyBuffering
	 */
	public EasyBuffering serialize() {
        EasyBuffering b=new EasyBuffering(KEYS_LENGTH * 4);
        for (KeysConfiguration key : KeysConfiguration.values()) {
			b.put(kbdInstant.get(key));
		}
		return b;
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
