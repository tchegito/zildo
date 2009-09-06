package zildo;
import java.util.logging.LogManager;

import zildo.client.Client;
import zildo.client.gui.menu.StartMenu;
import zildo.fwk.net.NetSend;


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
			} else if (arg.startsWith("ip")) {
				NetSend.NET_PORT_IP=arg.substring(2);
			} else if (arg.startsWith("port")) {
				NetSend.NET_PORT_SERVER=Integer.parseInt(arg.substring(4), 10);
			}
		}
		if (!log) {	// Disable all logging
			LogManager.getLogManager().reset();
		}
		
		//Constantes.DATA_PATH="C:\\ZildoDist\\Version 1.08\\Data\\";
		
        final Client client=new Client(false);

        client.handleMenu(new StartMenu(client));
        client.run();
        client.cleanUp();
        
        System.exit(0);
	}
}
