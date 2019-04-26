package zildo.monde.sprites.utils;

public class FlagPerso {

	public static int F_IMMATERIAL = 1;	// Unblocking and causes no damage
	public static int F_STATIC = 2;		// Blocks animation counter
	public static int F_INVULNERABLE = 4;	// Hero can't wound him
	public static int F_UNDERGROUND = 8;	// Always displayed before others characters
	public static int F_NOWAIT = 16;		// Character stops if target is unreachable
}
