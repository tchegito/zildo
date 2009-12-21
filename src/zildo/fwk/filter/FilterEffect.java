package zildo.fwk.filter;

public enum FilterEffect {
	FADE(FadeFilter.class), BLEND(BlendFilter.class), BLUR(BlurFilter.class), ZOOM(ZoomFilter.class);
	
	private Class<? extends ScreenFilter> clazz;
	
	public Class<? extends ScreenFilter> getFilterClass() {
		return clazz;
	}
	
	private FilterEffect(Class<? extends ScreenFilter> p_clazz) {
		clazz = p_clazz;
	}
	
	public static FilterEffect fromInt(int p_int) {
		return values()[p_int];
	}
}
