package zildo.fwk.gfx.filter;

import zildo.client.ClientEngineZildo;
import zildo.fwk.gfx.GraphicStuff;
import zildo.monde.util.Pointf;

/**
 * Filter shaking the screen.
 * 
 */
public class EarthQuakeFilter extends ScreenFilter {

	boolean toggle = false;
	
	public EarthQuakeFilter(GraphicStuff graphicStuff) {
		super(graphicStuff);
	}

	@Override
	public boolean renderFilter() {
		if (toggle) {
			ClientEngineZildo.ortho.setOffsetScreen(new Pointf(0,4));
		} else{
			ClientEngineZildo.ortho.setOffsetScreen(new Pointf(0,0));
		}
		toggle = !toggle;
		return true;
	}
	
	public void doOnInactive(FilterEffect effect) {
		toggle = false;
		renderFilter();
	}
}
