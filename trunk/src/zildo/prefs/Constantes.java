/**
 * Legend of Zildo
 * Copyright (C) 2006-2010 Evariste Boussaton
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

package zildo.prefs;

import java.io.File;

import zildo.Zildo;


public class Constantes {

	// File system data
	public static String DATA_PATH = "."+File.separator+"Data"+File.separator;	// Pour livraison
	//public static String DATA_PATH = "C:\\ZildoDist\\Version 1.092\\Data\\";

	public static String CONFIGURATION_FILE = "player.ini";
	public static String SERVER_FILE = "servers.ini";
	
	// Tile Engine
	public static final int TILEENGINE_WIDTH = 64;		// Max Number of tiles
														// on a row
	public static final int TILEENGINE_HEIGHT = 64;		// Max Number of tiles
														// on a column
	// Total vertices on the tile map
	public static final int TILEENGINE_MAXPOINTS = 4 * (TILEENGINE_WIDTH * TILEENGINE_HEIGHT);	

	// Tile Banks
	public static final	int NB_MOTIFBANK = 8;

	// Sprite Banks
	public static final	int NB_SPRITEBANK = 7;
	public static final int NB_SPRITE_PER_PRIMITIVE = 400;

	public static final int SORTY_MAX = Zildo.viewPortY+40;
	public static final int SORTY_REALMAX = Zildo.viewPortY+40+80;
	public static final int SORTY_ROW_PER_LINE = 32;
	// A line can contain multiple objects

	public static final int MAX_SPRITES_ON_SCREEN = SORTY_MAX * SORTY_ROW_PER_LINE;

	// Perso
	public static final	int speed = 3;
	public static final	int MAX_PERSO = 80;
	public static final	int ZILDO_SPEED = 1;
	public static final	float MONSTER_SPEED = 0.85f; // Vitesse du monstre en alerte
	public static final	int MAX_DIALOG = 256;
	public static final	int MAX_TOPICS = 100;

	// Texter
	public static final	int TEXTER_SIZEX = 230;
	public static final int TEXTER_SIZESPACE = 4;
	public static final int TEXTER_SIZELINE = 16;
	public static final int TEXTER_NUMLINE = 3;
	public static final int TEXTER_COORDINATE_X = 50;
	public static final int TEXTER_COORDINATE_Y = 180;
	public static final int TEXTER_TOPIC_SIZELINE = 12;
	public static final int TEXTER_MENU_SIZEY = 16;

	public static final	int FADE_SPEED = 5;

	public static final	float mathPi = 3.1415926535f;

	// Hazard
	public static final int hazardBushes_Diamant = 6;
	public static final int hazardBushes_Heart = 5;
	public static final int hazardBushes_Arrow = 7;
	

	// Sounds
	public static final int MAX_SOUNDS = 31;
}
