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

package zildo.resource;

import java.io.File;

import zildo.Zildo;
import zildo.client.gui.ScreenConstant;
import zildo.fwk.gfx.engine.TileEngine;
import zildo.monde.sprites.desc.ZildoOutfit;

/**
 * All size concerning screen sprites are referring to a standard of 320x240.<p/>
 * 
 * An adjustment is done via {@link ScreenConstant} class.
 * @author evariste.boussaton
 *
 */
public class Constantes {

	// File system data
	public static String DATA_PATH = "."+File.separator+"Data"+File.separator;	// Pour livraison

	public static String MAP_PATH = "maps"+File.separator;
	public static String INI_DIR = "ini"+File.separator;
	public static String CONFIGURATION_FILE = "player.ini";
	public static String SERVER_FILE = INI_DIR + "servers.ini";
	public static String SAVEGAME_DIR = "saves"+File.separator;
	public static String SAVEGAME_FILE = "save.z";
	
	// When player finish the game, he'll be registered with this episode name
	public static final String[] allEpisodes = {"Alemb1", "Alemb2"};
	public static final String currentEpisode = "Alemb2";

	public static int CURRENT_VERSION = 1096;	// Version 1.096
	public static String CURRENT_VERSION_DISPLAYED = "1.096";	// Dynamic value, initialized at startup
	
	// Tile Engine
	public static final int TILEENGINE_FLOOR = 3;	// Max number of floors
	public static final int TILEENGINE_WIDTH = 64;		// Max Number of tiles on a row
	public static final int TILEENGINE_HEIGHT = 64;		// Max Number of tiles on a column
	// Total vertices on the tile map (4 vertices per tile, double it for two map at a same time)
	public static final int TILEENGINE_MAXPOINTS = 2 * 4 * (TILEENGINE_WIDTH * TILEENGINE_HEIGHT);	

	// Tile Banks
	public static final	int NB_MOTIFBANK = TileEngine.tileBankNames.length;

	// Sprite Banks
	public static final	int NB_SPRITEBANK = 8 + ZildoOutfit.values().length;
	public static final int NB_SPRITE_PER_PRIMITIVE = 500;	// Increased from 400 to 500 when dialogs history appeared (fixed ArrayIndexOutOfBounds in SpriteOrder)

	public static final int SORTY_MAX = Zildo.viewPortY+40;
	public static final int SORTY_REALMAX = Zildo.viewPortY+40+80;
	public static final int SORTY_ROW_PER_LINE = 32;
	// A line can contain multiple objects

	public static final int MAX_SPRITES_ON_SCREEN = SORTY_MAX * SORTY_ROW_PER_LINE;

	// Perso
	public static final	int speed = 3;
	public static final	int MAX_PERSO = 80;
	public static final float ZILDO_SPEED = 1.5f;
	public static final float ROXY_SPEED = 1f;
	public static final	float MONSTER_SPEED = 0.85f; // Vitesse du monstre en alerte
	public static final	int MAX_DIALOG = 256;

	// Texter
	public static final	int TEXTER_SIZEX = 230;
	public static final int TEXTER_SIZESPACE = 4;
	public static final int TEXTER_SIZELINE = 16;
	public static final int TEXTER_NUMLINE = 3;
	public static final int BIGTEXTER_X = 20;
	public static final int BIGTEXTER_Y = 20;
	public static final int BIGTEXTER_WIDTH = 285;
	public static final int BIGTEXTER_HEIGHT = Zildo.viewPortY - 38;
	public static final int TEXTER_COORDINATE_X = 50;
	public static final int TEXTER_COORDINATE_Y = 180;
	public static final int TEXTER_MENU_SIZEY = 16;
	public static final int TEXTER_SIZELINE_SCRIPT = 16;
	
	public static final	int FADE_SPEED = 5;

	public static final	float mathPi = 3.1415926535f;
	public static final float cosPiSur4 = 0.66f; // Nearly

	// Sounds
	public static final int MAX_SOUNDS = 49;
	
	public static final int NB_MAX_DIALOGS_HISTORY = 20;
	
	static {
		// Check if environment variable is provided to get data folder
		String dataFolder = System.getProperty("ZILDO_DATA");
		if (dataFolder != null) {
			if (!dataFolder.endsWith(File.separator)) {
				dataFolder += File.separator;
			}
			Constantes.DATA_PATH = dataFolder;
		}
	}
}
