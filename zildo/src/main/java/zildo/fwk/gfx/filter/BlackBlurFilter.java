package zildo.fwk.gfx.filter;

import zildo.Zildo;
import zildo.fwk.gfx.GraphicStuff;

public class BlackBlurFilter extends FadeScreenFilter {

	protected BilinearFilter filter;
	
	public BlackBlurFilter(GraphicStuff graphicStuff) {
		super(graphicStuff);
		// We use the filter already created for BilinearFiltering
		filter = Zildo.pdPlugin.getFilter(BilinearFilter.class);
	}

	@Override
	public boolean renderFilter() {
		return false;
	}

}
