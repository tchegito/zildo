package zildo;

import java.util.logging.LogManager;

import zildo.monde.Game;

public class ZildoServer {

	public static void main(String[] args) {
		
		for (String arg : args) {
			if ("fullscreen".equals(arg)) {
				Zildo.fullScreen=true;
			}
		}
		if (!Zildo.log) {	// Disable all logging
			LogManager.getLogManager().reset();
		}
		
        Game game = new Game("polaky", false);
        //new SinglePlayer(game);
        new MultiPlayer(game);
	}
}
