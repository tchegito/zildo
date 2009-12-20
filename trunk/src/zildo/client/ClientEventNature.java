package zildo.client;


public enum ClientEventNature {

	NOEVENT,
	CHANGINGMAP_ASKED,			// Server says "we need to change the current map"
	FADEOUT,		// Client has started a fade out (for new map)
	FADEOUT_OVER,	// Client has ended his fade out
	CHANGINGMAP_LOADED,			// Server has loaded the new map
	FADEIN,			// Client has started a fade in (for new map)
	CHANGINGMAP_SCROLL_ASKED, 	// Server says "we need to change the current map, because of client is along a border"
	CHANGINGMAP_SCROLL_CAPTURE, // Client capture screen
	CHANGINGMAP_SCROLL_START,	// Client has captured current screen
	CHANGINGMAP_SCROLL_WAIT_MAP, // Client is awaiting for the new map
	CHANGINGMAP_SCROLL_LOADED,	// Serve has loaded the new map 
	CHANGINGMAP_SCROLL,			// Client is scrolling
	CHANGINGMAP_SCROLLOVER,		// Client has finished scrolling
	SCRIPT;
	
	public boolean isChangingMap() {
		int i=ordinal();
		return (i>= CHANGINGMAP_ASKED.ordinal() && i<=CHANGINGMAP_SCROLLOVER.ordinal());
	}
}
