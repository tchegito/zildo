package junit.dialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

public class TestDialogs {

	final static String BUNDLE_GAME = "zildo.resource.bundle.game";
	final static String BUNDLE_MENU = "zildo.resource.bundle.menu";

	// Some sentences are the same in french than in english : we must authorize those exceptions
	final static List<String> sameSentecesTranslated=Arrays.asList("m7.options", "m12.butOk");
	
	@Test
	public void checkGameComplete() {
		checkGivenBundleComplete(BUNDLE_GAME);
	}

	@Test
	public void checkMenuComplete() {
		checkGivenBundleComplete(BUNDLE_MENU);
	}

	@Test
	public void checkInconsistence() {
		Locale.setDefault(Locale.FRANCE);
		ResourceBundle bundleRef = ResourceBundle.getBundle(BUNDLE_GAME);
		Locale.setDefault(Locale.US);
		ResourceBundle bundleTranslated = ResourceBundle.getBundle(BUNDLE_GAME);

		Set<String> keys = bundleRef.keySet();
		for (String s : keys) {
			checkSentence(bundleRef, s);
			checkSentence(bundleTranslated, s);
		}
	}

	private void checkSentence(ResourceBundle bundle, String s) {
		try {
			String value = bundle.getString(s);
			int posSharp = value.indexOf("#");
			// 1) # must be followed by a number
			Assert.assertTrue(posSharp == -1 || posSharp < (value.length() - 1));
			// 2) #n must be a 'n-th' sentence for perso
			value.charAt(posSharp + 1);

		} catch (MissingResourceException e) {
			// Silent catch => another test is already doing this
		}
	}

	/**
	 * Check translated strings (must be not null and different)
	 * 
	 * @param bundleName
	 */
	private void checkGivenBundleComplete(String bundleName) {
		Locale.setDefault(Locale.FRANCE);
		ResourceBundle bundleRef = ResourceBundle.getBundle(bundleName);
		Locale.setDefault(Locale.US);
		ResourceBundle bundleTranslated = ResourceBundle.getBundle(bundleName);

		Enumeration<String> keys = bundleRef.getKeys();
		List<String> emptyKey = new ArrayList<String>();
		List<String> untranslated = new ArrayList<String>();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			try {
				String translated = bundleTranslated.getString(key);
				if (translated.equals(bundleRef.getString(key)) && !sameSentecesTranslated.contains(key)) {
					untranslated.add(key);
				}
			} catch (MissingResourceException e) {
				emptyKey.add(key);
			}
		}
		System.out.println("bundle: " + bundleName);
		if (emptyKey.size() != 0) {
			System.out.println("No english messages for keys:\n-----------------------------");
			for (String s : emptyKey) {
				System.out.println(s);
			}
		}
		if (untranslated.size() != 0) {
			System.out.println("\n\nMessages aren't translated for keys:\n------------------------------------");
			for (String s : untranslated) {
				System.out.println(s);
			}
		}
		System.out.println("\n\n");
		Assert.assertEquals("No english messages for keys: "+emptyKey, 0, emptyKey.size());
		Assert.assertEquals("Messages aren't translated for keys: "+untranslated, 0, untranslated.size());
	}
}
