package zildo;
import java.util.logging.LogManager;

import zildo.fwk.engine.EngineZildo;
import zildo.fwk.opengl.OpenGLZildo;


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
		
		OpenGLZildo glGestion=new OpenGLZildo(fullScreen);
		EngineZildo engineZildo=new EngineZildo(glGestion);
		glGestion.setEngineZildo(engineZildo);

		glGestion.run();
	}
}
