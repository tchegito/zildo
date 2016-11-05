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

package junit.dialog;

import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.junit.Assert;
import org.junit.Test;

import zildo.monde.dialog.Behavior;

/**
 * @author Tchegito
 *
 */
public class CheckDialogs {

	static ResourceBundle dialFR;
	static ResourceBundle dialEN;
	
	static {
		dialFR = ResourceBundle.getBundle("zildo.resource.bundle.game", Locale.FRANCE);
		Locale.setDefault(Locale.US);
		dialEN = ResourceBundle.getBundle("zildo.resource.bundle.game");
	}

	@Test
	public void testCompletude() {
		Enumeration<String> keys = dialFR.getKeys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			if (!key.startsWith("people.")) {
				String valueEN = dialEN.getString(key);
				Assert.assertTrue("No value for key "+key+" in game.properties", valueEN != null);
			}
		}				
	}
	
	@Test
	public void testTranslation() {
		Enumeration<String> keys = dialFR.getKeys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			try {
				String valueFR = dialFR.getString(key);
				String valueEN = dialEN.getString(key);
				if (valueEN != null) {
					if (valueEN.equals(valueFR)) {
						System.out.println(key);
					}
					//Assert.assertTrue("Sentence isn't translated for "+key+" in game.properties", !valueEN.equals(valueFR));
				}		
			} catch (MissingResourceException m) {
				
			}
		}
	}

	@Test
	public void testSpecialChars() {
		Enumeration<String> keys = dialFR.getKeys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			try {
				String valueFR = dialFR.getString(key);
				String valueEN = dialEN.getString(key);
				if (valueEN != null) {
					boolean sharp = charIdem(valueFR, valueEN, '#');
					boolean arobase = charIdem(valueFR, valueEN, '@');
					Assert.assertTrue("Translation hasn't the same special character for "+key+" in game.properties", sharp && arobase);
					
					int i = valueFR.indexOf("#");
					if (i != -1) {
						char pos = valueFR.charAt(i+1);
						char pos2 = valueEN.charAt(valueEN.indexOf("#") + 1);
						Assert.assertTrue("Sharp doesn't lead to same sentence for "+key+" in game.properties", pos == pos2);
					}
				}
			} catch (MissingResourceException m) {
				
			}
		}
	}
	
	/** Just basic test about {@link Behavior#getLength} class. **/
	@Test
	public void behaviors() {
		Behavior behav = new Behavior("nanie");
		Assert.assertEquals(0, behav.getLength());
		
		behav.replique[0] = 2;
		Assert.assertEquals(1, behav.getLength());
		
		behav.replique[4] = 3;
		Assert.assertEquals(5, behav.getLength());
		
	}
	private boolean charIdem(String s1, String s2, char a) {
		return (s1.indexOf(a) * s2.indexOf(a)) > 0;
	}
}
