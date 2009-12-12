package zildo.fwk.ui;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class UIText {

	static ResourceBundle bundle;
	
	static {
		bundle=ResourceBundle.getBundle("zildo.prefs.bundle.menu");
	}
	
	public static String getText(String p_key) {
		try {
			return bundle.getString(p_key);
		} catch (MissingResourceException e) {
			return p_key;
		}
	}
	
	public static String getText(String p_key, Object... p_params) {
		try {
			String message= bundle.getString(p_key);
			return MessageFormat.format(message, p_params);
		} catch (MissingResourceException e) {
			return p_key;
		}
	}
}
