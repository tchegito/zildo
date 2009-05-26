package zildo;
import java.util.logging.LogManager;

import zildo.monde.Game;
import zildo.network.Client;
import zildo.network.Server;


public class Zildo {

	public static boolean soundEnabled=true;
	public static boolean fullScreen=false;
	public static int viewPortX=320;
	public static int viewPortY=240;
	public static boolean infoDebug=false;
	public static boolean log=false;

	public static void main(String[] args) {
		
		for (String arg : args) {
			if ("fullscreen".equals(arg)) {
				fullScreen=true;
			}
		}
		if (!log) {	// Disable all logging
			LogManager.getLogManager().reset();
		}
		
		/*
		OpenGLZildo glGestion=new OpenGLZildo(fullScreen);
		EngineZildo engineZildo=new EngineZildo(glGestion);
		glGestion.setEngineZildo(engineZildo);
*/
		Game game=new Game("polaky", false);
		Server server=new Server(game);
		Client client=new Client(server.getEngineZildo(), false);
		
		server.start();
		server.connectClient(client);
		client.run();
		server.disconnectClient(client);
	}
}
