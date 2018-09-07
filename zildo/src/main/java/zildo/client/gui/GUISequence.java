package zildo.client.gui;

public enum GUISequence {
	DIALOG, TEXT_MENU, FRAME_DIALOG, GUI, CREDIT, INFO;
	
	static GUISequence[] all() {
		return values();
	}
}