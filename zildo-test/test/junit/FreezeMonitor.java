package junit;

import junit.perso.EngineUT;
import zildo.fwk.ZUtils;

public class FreezeMonitor extends Thread {

	boolean done = false;
	int lastOne;
	int cnt = 0;

	boolean isDebug;
	
	EngineUT engine;

	public FreezeMonitor(EngineUT engine) {
		this.engine = engine;
		// Determine if we're in a debugging process
		isDebug = java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString()
				.indexOf("-agentlib:jdwp") > 0;
	}

	@Override
	public void run() {
		while (!done) {
			if (lastOne == engine.nFrame) {
				// Still on the same frame ?
				if (++cnt == 5 && !isDebug) {
					System.out.println("We got a freeze !");
					// Rude, but no bugs are tolerated in Alembrume !
					ZUtils.sleep(5000);
					System.exit(1);
				}
			} else {
				lastOne = engine.nFrame;
				cnt = 0;
			}
			ZUtils.sleep(500);
		}
	};
	
	public void cutItOut() {
		done = true;
	}
}
