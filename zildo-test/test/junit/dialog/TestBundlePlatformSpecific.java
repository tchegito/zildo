package junit.dialog;

import junit.framework.Assert;
import junit.framework.TestCase;
import zildo.client.PlatformDependentPlugin;
import zildo.client.PlatformDependentPlugin.KnownPlugin;
import zildo.fwk.ui.UIText;

public class TestBundlePlatformSpecific extends TestCase {

	public void testPlatformSpecificMessages() {
		for (KnownPlugin plugin : KnownPlugin.values()) {
			PlatformDependentPlugin.currentPlugin = plugin;
			checkKey("preintro.0");
			checkKey("pext.hector.7");
			checkStartingCapital("preintro.1");
		}
	}

	private void checkKey(String key) {
		String value = UIText.getGameText(key);
		System.out.println(key+" ==> "+value);
		Assert.assertTrue(!value.contains("%"));
	}
	
	private void checkStartingCapital(String key) {
		String value = UIText.getGameText(key);
		Assert.assertTrue("Sentences should have started with a capital !", Character.isUpperCase(value.charAt(0)));
	}
}
