package zildo.prefs;

import org.lwjgl.input.Keyboard;

// All these keys should be send to server.
// Its lead to player's movement/action.

public enum KeysConfiguration {

	PLAYERKEY_ACTION(Keyboard.KEY_Q),
	PLAYERKEY_ATTACK(Keyboard.KEY_W),
	PLAYERKEY_INVENTORY(Keyboard.KEY_X),
	PLAYERKEY_UP(Keyboard.KEY_UP),
	PLAYERKEY_DOWN(Keyboard.KEY_DOWN),
	PLAYERKEY_RIGHT(Keyboard.KEY_RIGHT),
	PLAYERKEY_LEFT(Keyboard.KEY_LEFT),
	PLAYERKEY_TOPIC(Keyboard.KEY_E),
	PLAYERKEY_TAB(Keyboard.KEY_TAB);
	
	public int code;
	
	private KeysConfiguration(int p_code) {
		this.code=p_code;
	}
}
