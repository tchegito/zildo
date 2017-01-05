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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;

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
	  ResourceBundle bundle = ResourceBundle.getBundle(baseName, new UTF8Control());
	  
	  noUtf = true;
	  // Since properties file are now in UTF-8, we shouldn't need a conversion anymore
	  // If current machine isn't UTF8 friendly, convert each value from the bundle
	  return bundle;
	}

	// Allow to load a properties file encoded in UTF-8 (by default, it considers it as ISO-8859-15)
	static class UTF8Control extends Control {
	    public ResourceBundle newBundle
	        (String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
	            throws IllegalAccessException, InstantiationException, IOException
	    {
	        // The below is a copy of the default implementation.
	        String bundleName = toBundleName(baseName, locale);
	        String resourceName = toResourceName(bundleName, "properties");
	        ResourceBundle bundle = null;
	        InputStream stream = null;
	        // Workaround for Android, because provided one (BootClassLoader) never find any resources
	        ClassLoader workingLoader = Utf8ResourceBundle.class.getClassLoader();
	        if (reload) {
	            URL url = workingLoader.getResource(resourceName);
	            if (url != null) {
	                URLConnection connection = url.openConnection();
	                if (connection != null) {
	                    connection.setUseCaches(false);
	                    stream = connection.getInputStream();
	                }
	            }
	        } else {
	            stream = workingLoader.getResourceAsStream(resourceName);
	        }
	        if (stream != null) {
	            try {
	                // Only this line is changed to make it to read properties files as UTF-8.
	                bundle = new PropertyResourceBundle(new InputStreamReader(stream, "UTF-8"));
	            } finally {
	                stream.close();
	            }
	        }
	        return bundle;
	    }
	}
	
}
