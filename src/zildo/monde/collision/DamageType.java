package zildo.monde.collision;

public enum DamageType {

	BLUNT, // Boomerang
	PIERCING, // Arrow
	CUTTING, // Sword
	CUTTING_FRONT, // Sword just in front of character
	EXPLOSION; // Bomb
	
	public boolean isCutting() {
		return CUTTING==this || CUTTING_FRONT==this || EXPLOSION==this; 
	}
}
