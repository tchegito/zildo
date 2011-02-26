/**
 * Legend of Zildo
 * Copyright (C) 2006-2011 Evariste Boussaton
 * Based on original Zelda : link to the past (C) Nintendo 1992
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package zildo.fwk.ui;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class UIText {

	static ResourceBundle menuBundle;
	static ResourceBundle gameBundle;
	
	static {
		menuBundle=ResourceBundle.getBundle("zildo.prefs.bundle.menu");
		gameBundle=ResourceBundle.getBundle("zildo.prefs.bundle.game");
	}
	
	static private String getText(ResourceBundle p_bundle, String p_key, Object... p_params) {
		try {
			String message = p_bundle.getString(p_key);
			message = message.replaceAll("'", "''");
			return MessageFormat.format(message, p_params);
		} catch (MissingResourceException e) {
			return p_key;	// This is mandatory for item menus with parameters (ex: player name)
		}
	}
	
	/**
	 * Returns a label with a given key and given parameters from the <b>menu</b> bundle properties.
	 * @param p_key
	 * @param p_params
	 * @return String
	 */
	public static String getMenuText(String p_key, Object... p_params) {
		return getText(menuBundle, p_key, p_params);
	}
	
	/**
	 * Returns a label with a given key and given parameters from the <b>game</b> bundle properties.
	 * @param p_key
	 * @param p_params
	 * @return String
	 */
	public static String getGameText(String p_key, Object... p_params) {
		return getText(gameBundle, p_key, p_params);
	}
	
}
