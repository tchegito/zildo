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

package zildo.client;

import zildo.Zildo;
import zildo.fwk.Injector;
import zildo.fwk.gfx.Ortho;
import zildo.fwk.gfx.engine.TileEngine;
import zildo.fwk.input.KeyboardHandler;

/**
 * Handle all platform-dependent classes.
 * 
 * It's a singleton warehouse.
 *
 * @author Tchegito
 *
 */
public class PlatformDependentPlugin {

	private Injector injector = new Injector();
	
    public final KeyboardHandler kbHandler;
    public final Ortho ortho;
    public final TileEngine tileEngine;
    
    enum KnownPlugin { Lwjgl, Android };
    
    private KnownPlugin currentPlugin = KnownPlugin.Lwjgl;	// Constant for now
    
    public PlatformDependentPlugin() {
        // Look for existing stuff in the class loader and create all needed singletons
        kbHandler = createSingleton("zildo.platform.input.KeyboardHandler");
        ortho = createSingleton("zildo.platform.opengl.Ortho", Zildo.viewPortX, Zildo.viewPortY);
        tileEngine = createSingleton("zildo.platform.engine.TileEngine");
    }
    
    @SuppressWarnings("unchecked")
	private <T> T createSingleton(String p_className, Object...p_param) {
    	// Format the class name with the knonw plugin
    	int posPoint = p_className.lastIndexOf(".");
    	String formattedClassName = p_className.substring(0, posPoint+1)+
    								currentPlugin.name()+
    								p_className.substring(posPoint+1);
    	return (T) injector.createSingleton(formattedClassName, p_param);
    }
   
    
}