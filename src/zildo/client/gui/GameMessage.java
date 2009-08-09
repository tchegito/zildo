package zildo.client.gui;

public class GameMessage {

	public String text;
	int duration;
	
	public GameMessage(String p_text) {
		text=p_text;
		duration=200;	// message will be visible during 200 frames
	}
}
