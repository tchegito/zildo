/**
 * Legend of Zildo
 * Copyright (C) 2006-2012 Evariste Boussaton
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

package zeditor.tools.builder;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.LogManager;

import zildo.monde.Game;
import zildo.resource.Constantes;
import zildo.server.EngineZildo;
import zildo.server.MapManagement;
import zildo.server.Server;

/**
 * Class providing a repetitive action to proceed on every single map.
 * 
 * @author Tchegito
 *
 */
public abstract class AllMapProcessor {

	String mapName;
	
	protected abstract boolean run();

	public String getMapname() {
		return mapName;
	}
	
	/**
	 * For each map : load, call run method, and save.
	 */
	public void modifyAllMaps() {
		String path=Constantes.DATA_PATH + Constantes.MAP_PATH;
		
		FilenameFilter mapFilter = new FilenameFilter() {
    		public boolean accept(File dir, String name) {
    			return name.toLowerCase().endsWith(".map");
    		}
		};
		List<File> mapsFile=new ArrayList<File>();
		File[] scenarioMaps = new File(path).listFiles(mapFilter);
		mapsFile.addAll(Arrays.asList(scenarioMaps));
		LogManager.getLogManager().reset();
		
        Game game = new Game(null, true);
        new Server(game, true);
		for (File f : mapsFile) {
			mapName=f.getName();
			System.out.println("Processing "+mapName+"...");
			MapManagement mapManagement=EngineZildo.mapManagement;

			mapManagement.loadMap(mapName, false);

			// Do the thing !
			boolean shouldSave = run();
			
			if (shouldSave) {
				mapManagement.saveMapFile(mapName);
			}
		}		
	}
}
