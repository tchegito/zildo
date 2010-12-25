package zildo.monde.sprites.desc;

import java.util.ArrayList;
import java.util.Arrays;

@SuppressWarnings("serial")
public class ZSpriteLibrary extends ArrayList<SpriteDescription> {

	public ZSpriteLibrary() {
		addAll(Arrays.asList(ElementDescription.values()));
	}
}
