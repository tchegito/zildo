package zildo;
import java.util.logging.LogManager;

import zildo.monde.Game;
import zildo.prefs.Constantes;


public class Zildo {

	public static boolean soundEnabled=true;
	public static boolean fullScreen=false;
	public static int viewPortX=320;
	public static int viewPortY=240;
	public static boolean infoDebug=false;
	public static boolean log=false;
	public static boolean logNetwork=false;

	public static void main(String[] args) {
		
		for (String arg : args) {
			if ("fullscreen".equals(arg)) {
				fullScreen=true;
			}
		}
		if (!log) {	// Disable all logging
			LogManager.getLogManager().reset();
		}
		
		Constantes.DATA_PATH="C:\\ZildoDist\\Version 1.06\\Data\\";
		
        Game game = new Game("polaky", false);
        //new SinglePlayer(game);
        new MultiPlayer(); //, true);
	}
}
