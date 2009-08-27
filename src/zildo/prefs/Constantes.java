package zildo.prefs;

public class Constantes {

	// File system data
	public static String DATA_PATH = ".\\Data\\";	// Pour livraison
	//public static String DATA_PATH = "C:\\ZildoDist\\Version 1.06\\Data\\";

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
	public static final	int NB_SPRITEBANK = 6;
	public static final int NB_SPRITE_PER_PRIMITIVE = 400;

	public static final int SORTY_MAX = 280;
	public static final int SORTY_REALMAX = 320;
	public static final int SORTY_ROW_PER_LINE = 32;
	// A line can contain multiple objects

	public static final int MAX_SPRITES_ON_SCREEN = SORTY_MAX * SORTY_ROW_PER_LINE;

	// Perso
	public static final	int speed = 5;
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

	// Engine events
	public static final int ENGINEEVENT_NOEVENT = 0;
	public static final int ENGINEEVENT_CHANGINGMAP_FADEOUT = 1;
	public static final int ENGINEEVENT_CHANGINGMAP_FADEIN = 2;

	public static final	int FADE_SPEED = 5;

	public static final	float mathPi = 3.1415926535f;

	// Hazard
	public static final int hazardBushes_Diamant = 6;
	public static final int hazardBushes_Heart = 5;
	public static final int hazardBushes_Arrow = 7;
	

	// Sounds
	public static final int MAX_SOUNDS = 28;
}
