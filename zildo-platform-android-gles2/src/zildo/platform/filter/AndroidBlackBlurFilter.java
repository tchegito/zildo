package zildo.platform.filter;

import shader.Shaders;
import shader.Shaders.GLShaders;
import zildo.fwk.gfx.GraphicStuff;
import zildo.fwk.gfx.filter.BlackBlurFilter;
import zildo.fwk.gfx.filter.FilterEffect;
import zildo.platform.opengl.AndroidPixelShaders;
import android.opengl.GLES20;

public class AndroidBlackBlurFilter extends BlackBlurFilter {

	public AndroidBlackBlurFilter(GraphicStuff graphicStuff) {
		super(graphicStuff);
	}
	
	@Override
	public void doOnActive(FilterEffect effect) {
		super.doOnActive(effect);
	}

	@Override
	public boolean renderFilter() {
		AndroidPixelShaders.shaders.setCurrentShader(GLShaders.blackBlur);
		GLShaders shader = Shaders.GLShaders.blackBlur;
        GLES20.glUseProgram(shader.id);
		
		AndroidPixelShaders.shaders.uniform1f("iFrame", (float) getFadeLevel());
		
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
