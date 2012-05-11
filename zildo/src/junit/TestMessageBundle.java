package junit;

import junit.framework.Assert;

import org.junit.Test;

import zildo.client.PlatformDependentPlugin;
import zildo.client.PlatformDependentPlugin.KnownPlugin;
import zildo.fwk.ui.UIText;

public class TestMessageBundle {

	@Test
	public void platformSpecificMessages() {
		for (KnownPlugin plugin : KnownPlugin.values()) {
			PlatformDependentPlugin.currentPlugin = plugin;
			checkKey("preintro.0");
			checkKey("pext.hector.7");
		}
	}

	private void checkKey(String key) {
		String value = UIText.getGameText(key);
		System.out.println(key+" ==> "+value);
		Assert.assertTrue(!value.contains("%"));
	}
}
