package zildo;

import java.util.logging.LogManager;

public class ZildoClient {

	public static void main(String[] args) {
		
		for (String arg : args) {
			if ("fullscreen".equals(arg)) {
				Zildo.fullScreen=true;
			}
		}
		if (!Zildo.log) {	// Disable all logging
			LogManager.getLogManager().reset();
		}
		
        new MultiPlayer();
	}

}
