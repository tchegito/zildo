package zildo.platform.filter;

import org.lwjgl.opengl.ARBShaderObjects;

import zildo.client.ClientEngineZildo;
import zildo.fwk.gfx.GraphicStuff;
import zildo.fwk.gfx.filter.FilterEffect;
import zildo.fwk.gfx.filter.RotativeFilter;

public class LwjglRotativeFilter extends RotativeFilter {

	public LwjglRotativeFilter(GraphicStuff graphicStuff) {
		super(graphicStuff);
	}
		
	@Override
	public void doOnActive(FilterEffect effect) {
		super.doOnActive(effect);
	}

	@Override
	public boolean renderFilter() {
		ARBShaderObjects.glUseProgramObjectARB(ClientEngineZildo.pixelShaders.getPixelShader(7));
		ClientEngineZildo.pixelShaders.setParameter(7, "alpha", computeValue());
		// End render on buffered texture, and draw it on screen using blackBlur shader
		filter.renderFilter();
		
		ARBShaderObjects.glUseProgramObjectARB(0);
		return true;
	}
	
	@Override
	public void preFilter() {
		// Start to render on a buffered texture
		filter.preFilter();
	}

}
