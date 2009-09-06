package zildo;
import java.util.logging.LogManager;

import zildo.client.Client;
import zildo.client.gui.menu.StartMenu;
import zildo.prefs.Constantes;


public class Zildo {

	public static boolean soundEnabled=true;
	public static boolean fullScreen=false;
	public static int viewPortX=320;
	public static int viewPortY=240;
	public static boolean infoDebug=false;
	public static boolean infoDebugCollision=true;
	public static boolean infoDebugCase=false;
	public static boolean log=false;
	public static boolean logNetwork=true;

	public static void main(String[] args) {
		
		for (String arg : args) {
			if ("fullscreen".equals(arg)) {
				fullScreen=true;
			}
		}
		if (!log) {	// Disable all logging
			LogManager.getLogManager().reset();
		}
		
		Constantes.DATA_PATH="C:\\ZildoDist\\Version 1.07\\Data\\";
		
        final Client client=new Client(false);

        client.handleMenu(new StartMenu(client));
        client.run();
        client.cleanUp();
        
        System.exit(0);
	}
}
