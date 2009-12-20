package zildo.client;

import zildo.monde.map.Angle;
import zildo.monde.map.ChainingPoint;

public class ClientEvent {

	public ClientEventNature nature;
	public Angle angle;	// Only used with the CHANGINGMAP_SCROLL_START nature, now.
	public ChainingPoint chPoint;
	public boolean mapChange;
	public int wait;
	
	public ClientEvent(ClientEventNature p_nature) {
		nature=p_nature;
		angle=null;
		wait=0;
		mapChange=false;
	}
	
}
