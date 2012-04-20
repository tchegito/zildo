/**
 * Legend of Zildo
 * Copyright (C) 2006-2012 Evariste Boussaton
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

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * Class inspired by http://www.thoughtsabout.net/blog/archives/000044.html, allowing resources bundles to be 
 * read in UTF8.
 * 
 * For Android compatibility, we can use this tweek class, or no, depending on the default charset of the machine.
 * @author tchegito
 *
 */
public abstract class Utf8ResourceBundle {

	static Boolean noUtf;
	
	public static final ResourceBundle getBundle(String baseName) {
	  ResourceBundle bundle = ResourceBundle.getBundle(baseName);
	  if (noUtf == null) {	// Read once
		  noUtf = !Charset.defaultCharset().toString().contains("UTF");
	  }
	  // If current machine isn't UTF8 friendly, convert each value from the bundle
	  if (noUtf) {
		  return createUtf8PropertyResourceBundle(bundle);
	  } else {
		  return bundle;
	  }
	}

	private static ResourceBundle createUtf8PropertyResourceBundle(ResourceBundle bundle) {
	  if (!(bundle instanceof PropertyResourceBundle)) return bundle;

	  return new Utf8PropertyResourceBundle((PropertyResourceBundle)bundle);
	}
	
	
	private static class Utf8PropertyResourceBundle extends ResourceBundle {
		PropertyResourceBundle bundle;

		private Utf8PropertyResourceBundle(PropertyResourceBundle bundle) {
			this.bundle = bundle;
		}

		public Enumeration<String> getKeys() {
			return bundle.getKeys();
		}

		protected Object handleGetObject(String key) {
			String value = (String) bundle.handleGetObject(key);
			if (value == null) {
				return null;
			}
			try {
				return new String(value.getBytes("ISO-8859-1"), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException("Unable to read "+key+" from resource bundle.");
			}
		}

	}
}
