/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
 * 
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import zildo.client.PlatformDependentPlugin;

public class UIText {

	static ResourceBundle menuBundle;
	static ResourceBundle gameBundle;
	static ResourceBundle creditBundle;
	
	static String characterName = "Tchegito"; // Default
	
	final static Pattern pdRegex = Pattern.compile("%([a-z|A-Z| |\\.]*)%");
	
	static {
		menuBundle=Utf8ResourceBundle.getBundle("zildo.resource.bundle.menu");
		gameBundle=Utf8ResourceBundle.getBundle("zildo.resource.bundle.game");
		creditBundle=Utf8ResourceBundle.getBundle("zildo.resource.bundle.credits");
	}
	
	static private String getText(ResourceBundle p_bundle, String p_key, Object... p_params) {
		try {
			String message = p_bundle.getString(p_key);
			message = message.replaceAll("'", "''");
			String returned = MessageFormat.format(message, p_params);
			if (returned.contains("%")) {
				// Platform specific messages
				Matcher matcher = pdRegex.matcher(returned);
				StringBuffer sb = new StringBuffer();
				while (matcher.find()) {
					String pdKey = matcher.group(1);
					String expr = PlatformDependentPlugin.currentPlugin.toString() + "." + pdKey.replaceAll("%", "");
					String value = gameBundle.getString(expr);
					
					if (matcher.start() == 0) {
						value = Character.toUpperCase(value.charAt(0)) + value.substring(1);
					}
					matcher.appendReplacement(sb,  value);
				}
				matcher.appendTail(sb);
				returned = sb.toString();
			}
			return returned;
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
	 * Returns the complete credits text.
	 * @param p_key
	 * @param p_params (unused now)
	 * @return String
	 */
	public static String getCreditText(String p_key, Object... p_params) {
		return getText(creditBundle, p_key, p_params);
	}
	
	public static void setCharacterName(String name) {
		characterName = name.trim();
	}
	
	/**
	 * Way or knowing the hero name for current game.
	 */
	public static String getCharacterName() {
		return characterName;
	}
	
	/**
	 * Returns a label with a given key and given parameters from the <b>game</b> bundle properties.
	 * @param p_key
	 * @param p_params
	 * @return String
	 */
	public static String getGameText(String p_key) {
		return getText(gameBundle, p_key, characterName);
	}
	
	public static String getGameText(String p_key, Object param) {
		return getText(gameBundle, p_key, param);
	}
	
}
