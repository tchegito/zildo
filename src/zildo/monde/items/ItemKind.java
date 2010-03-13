package zildo.monde.items;

import zildo.monde.sprites.desc.ElementDescription;

public enum ItemKind {

	SWORD(ElementDescription.SMOKE), 
	BOOMERANG(ElementDescription.BOOMERANG1), 
	WHIP(ElementDescription.BAR_HORIZONTAL), 
	BOW(ElementDescription.ARROW_UP),
	BOMB(ElementDescription.BOMB),
	FLUT(ElementDescription.FLUT),
	SHIELD(null);
	
	public ElementDescription representation;
	
	private ItemKind(ElementDescription p_itemRepresentation) {
		representation=p_itemRepresentation;
	}
	
	public static ItemKind fromString(String p_str) {
		for (ItemKind kind : values()) {
			if (kind.toString() .equalsIgnoreCase(p_str)) {
				return kind;
			}
		}
		throw new RuntimeException("Item "+p_str+" doesn't exists.");
	}
}
