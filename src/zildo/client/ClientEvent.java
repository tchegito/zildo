package zildo.client;

import zildo.monde.map.Angle;

public class ClientEvent {

	public ClientEventNature nature;
	public Angle angle;	// Only used with the CHANGINGMAP_SCROLL_START nature, now.
	public int wait;
	
	public ClientEvent(ClientEventNature p_nature) {
		nature=p_nature;
		angle=null;
		wait=0;
	}
	
}
