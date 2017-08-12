package zildo.monde.map;

import zildo.monde.sprites.desc.ElementDescription;

/** Represents an item with information in a string, usually for an item coming out of a map case. **/
public class CaseItem {
	public final ElementDescription desc;
	public final String name;

	public CaseItem(ElementDescription desc, String name) {
		this.desc = desc;
		this.name = name;
	}
}