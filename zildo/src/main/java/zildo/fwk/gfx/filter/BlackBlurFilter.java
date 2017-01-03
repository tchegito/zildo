package zildo.fwk.gfx.filter;

import zildo.fwk.gfx.GraphicStuff;

public class BlackBlurFilter extends FadeScreenFilter {

	public BlackBlurFilter(GraphicStuff graphicStuff) {
		super(graphicStuff);
	}

	@Override
	public boolean renderFilter() {
		return false;
	}

}
