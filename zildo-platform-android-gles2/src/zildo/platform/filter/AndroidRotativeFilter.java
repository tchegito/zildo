package zildo.platform.filter;

import android.opengl.GLES20;
import shader.Shaders;
import shader.Shaders.GLShaders;
import zildo.fwk.gfx.GraphicStuff;
import zildo.fwk.gfx.filter.FilterEffect;
import zildo.fwk.gfx.filter.RotativeFilter;
import zildo.platform.opengl.AndroidPixelShaders;

public class AndroidRotativeFilter extends RotativeFilter {

	public AndroidRotativeFilter(GraphicStuff graphicStuff) {
		super(graphicStuff);
	}
	
	@Override
	public void doOnActive(FilterEffect effect) {
		super.doOnActive(effect);
	}

	@Override
	public boolean renderFilter() {
		AndroidPixelShaders.shaders.setCurrentShader(GLShaders.circular);
		GLShaders shader = Shaders.GLShaders.circular;
        GLES20.glUseProgram(shader.id);
		AndroidPixelShaders.shaders.uniform4f("alpha", computeValue());
		
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
