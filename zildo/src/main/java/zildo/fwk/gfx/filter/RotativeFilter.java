package zildo.fwk.gfx.filter;

import zildo.Zildo;
import zildo.client.ClientEngineZildo;
import zildo.fwk.gfx.GraphicStuff;
import zildo.monde.Function;
import zildo.monde.Trigo;
import zildo.monde.util.Vector4f;
import zildo.resource.Constantes;

public class RotativeFilter extends FadeScreenFilter {

	float counter;
	Function easinZoom;
	Function easingRot;
		
	final float pas = (256.0f / Constantes.FADE_SPEED);
	final int zoomAplitude = 8;
	final Vector4f ether = new Vector4f(0.1f, 0.05f, 0.09f, 0.1f);

	protected BilinearFilter filter;
	
	public RotativeFilter(GraphicStuff graphicStuff) {
		super(graphicStuff);
		// We use the filter already created for BilinearFiltering
		filter = Zildo.pdPlugin.getFilter(BilinearFilter.class);
	}

	@Override
	public void doOnActive(FilterEffect effect) {
		super.doOnActive(effect);
		// Angle goes from PI to 2*PI, and zoom goes from (1+zoomAmplitude) to 1
		easinZoom = Trigo.easeInOut((int) zoomAplitude, zoomAplitude / pas);
		easingRot = Trigo.easeInOut((int) (Math.PI*1000f), (float) (Math.PI / pas * 1000));
	}
	
	@Override
	public boolean renderFilter() {
		return false;
	}
	
	protected Vector4f computeValue() {
		int cnt = getFadeLevel() / Constantes.FADE_SPEED;
		Vector4f curColor =ether.interp(new Vector4f(1f, 1f, 1f, 1f), 1 - Constantes.FADE_SPEED * (float) cnt / 256.0f);
		ClientEngineZildo.pixelShaders.setParameter(7, "curColor", curColor);
		float zoomValue = zoomAplitude + 1 - easinZoom.apply(pas - cnt) * (float)zoomAplitude;
		float rotValue = (float) (Math.PI + easingRot.apply(pas - cnt) * Math.PI);
		
		return new Vector4f(rotValue, zoomValue, 0, 0);
	}
}
