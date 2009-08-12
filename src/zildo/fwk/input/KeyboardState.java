package zildo.fwk.input;

public class KeyboardState {
	
	public boolean key_actionPressed;
	public boolean key_attackPressed;
	public boolean key_topicPressed;
	public boolean key_inventoryPressed;
	
	public boolean key_upPressed;
	public boolean key_downPressed;
	
	public KeyboardState() {
		// Init keys
		key_actionPressed=false;
		key_attackPressed=false;
		key_inventoryPressed=false;
	}
}
