package zildo.client;

import zildo.fwk.filter.FilterEffect;
import zildo.monde.map.Angle;
import zildo.monde.map.ChainingPoint;

public class ClientEvent {

	public ClientEventNature nature;
	public Angle angle;	// Only used with the CHANGINGMAP_SCROLL_START nature, now.
	public ChainingPoint chPoint;
	public boolean mapChange;
	public FilterEffect effect;
	public int wait;
	public boolean script;
	
	
	public ClientEvent(ClientEventNature p_nature) {
		nature=p_nature;
		angle=null;
		wait=0;
		mapChange=false;
		effect=FilterEffect.BLEND;
		script=false;
	}
	
	public ClientEvent(ClientEventNature p_nature, FilterEffect p_effect) {
		this(p_nature);
		effect=p_effect;
	}
	
}
