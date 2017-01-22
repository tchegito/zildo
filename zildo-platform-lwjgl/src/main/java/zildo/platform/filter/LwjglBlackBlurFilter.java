package zildo.platform.filter;

import org.lwjgl.opengl.ARBShaderObjects;

import zildo.client.ClientEngineZildo;
import zildo.fwk.gfx.GraphicStuff;
import zildo.fwk.gfx.filter.FilterEffect;
import zildo.fwk.gfx.filter.BlackBlurFilter;
import zildo.monde.util.Vector4f;

public class LwjglBlackBlurFilter extends BlackBlurFilter {

	public LwjglBlackBlurFilter(GraphicStuff graphicStuff) {
		super(graphicStuff);
	}

	@Override
	public void doOnActive(FilterEffect effect) {
		super.doOnActive(effect);
	}
	@Override
	public boolean renderFilter() {
		ARBShaderObjects.glUseProgramObjectARB(ClientEngineZildo.pixelShaders.getPixelShader(6));
		ClientEngineZildo.pixelShaders.setParameter(6, "iFrame", new Vector4f((float) getFadeLevel(), 0, 0, 1));
		
		// End render on buffered texture, and draw it on screen using blackBlur shader
		filter.renderFilter();
		
		return true;
	}
	
	@Override
	public void preFilter() {
		// Start to render on a buffered texture
		filter.preFilter();
	}
}
