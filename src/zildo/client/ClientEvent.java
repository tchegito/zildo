package zildo.client;

public enum ClientEvent {

	NOEVENT,
	CHANGINGMAP_ASKED,			// Server says "we need to change the current map"
	CHANGINGMAP_FADEOUT,		// Client has started a fade out (for new map)
	CHANGINGMAP_FADEOUT_OVER,	// Client has ended his fade out
	CHANGINGMAP_LOADED,			// Server has loaded the new map
	CHANGINGMAP_FADEIN;			// Client has started a fade in (for new map)
}
