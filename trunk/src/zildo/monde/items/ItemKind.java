package zildo.monde.items;

import zildo.monde.decors.ElementDescription;

public enum ItemKind {

	SWORD(ElementDescription.SMOKE), 
	BOOMERANG(ElementDescription.BOOMERANG1), 
	WHIP(ElementDescription.BAR_HORIZONTAL), 
	BOW(ElementDescription.ARROW_UP),
	BOMB(ElementDescription.BOMB);
	
	public ElementDescription representation;
	
	private ItemKind(ElementDescription p_itemRepresentation) {
		representation=p_itemRepresentation;
	}
}
