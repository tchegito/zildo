package zildo.fwk;

public class ZUtils {

	public static void sleep(long p_millis) {
		try {
			Thread.sleep(p_millis);
		} catch (InterruptedException e) {
			
		}
	}
}
