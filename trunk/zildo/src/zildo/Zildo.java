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
package zildo;
import java.util.logging.LogManager;

import zildo.client.Client;
import zildo.client.PlatformDependentPlugin;
import zildo.client.gui.menu.StartMenu;


public class Zildo {

	public static boolean soundEnabled=false;
	public static boolean fullScreen=false;
	// Define the game viewport
	public static final int viewPortX=320;
	public static final int viewPortY=240;
	// Define the platform resolution (viewport will be adapated to fit)
	public static int screenX=800; //640;
	public static int screenY=600; //480;
	public static boolean infoDebug=false;
	public static boolean infoDebugCollision=true;
	public static boolean infoDebugCase=false;
	public static boolean log=false;
	public static boolean logNetwork=true;

	public static final PlatformDependentPlugin pdPlugin = new PlatformDependentPlugin();
	
	public static void main(String[] args) {
		
		for (String arg : args) {
			if ("fullscreen".equals(arg)) {
				fullScreen=true;
			}
		}
		if (!log) {	// Disable all logging
			LogManager.getLogManager().reset();
		}
		
        final Client client=new Client(false);

        client.handleMenu(new StartMenu());
        client.run();
        client.cleanUp();
        
        System.exit(0);
	}
}
