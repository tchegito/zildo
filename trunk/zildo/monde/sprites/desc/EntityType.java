package zildo.monde.sprites.desc;

public enum EntityType {

    ENTITY, ELEMENT, PERSO, FONT;
    
    public static EntityType fromInt(int p_value) {
	return values()[p_value];
    }
    
    public int intValue() {
	return ordinal();
    }
    
    public boolean isEntity() {
	return this == ENTITY;
    }
    
    public boolean isElement() {
	return this == ELEMENT;
    }
    
    public boolean isPerso() {
	return this == PERSO;
    }
    
    public boolean isFont() {
	return this == FONT;
    }
}
