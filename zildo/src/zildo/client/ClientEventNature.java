/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
 * 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package zildo.client;


public enum ClientEventNature {

	NOEVENT,
	CHANGINGMAP_ASKED,			// Server says "we need to change the current map"
	FADE_OUT,
	FADING_OUT,		// Client has started a fade out (for new map)
	FADEOUT_OVER,	// Client has ended his fade out
	CHANGINGMAP_LOADED,			// Server has loaded the new map
	FADE_IN,
	FADING_IN,			// Client has started a fade in (for new map)
	CHANGINGMAP_SCROLL_ASKED, 	// Server says "we need to change the current map, because of client is along a border"
	CHANGINGMAP_SCROLL_START,	// Client has captured current screen
	CHANGINGMAP_WAITSCRIPT,	// Client is waiting for the 'map scripts' being rendered
	CHANGINGMAP_SCROLL_WAIT_MAP, // Client is awaiting for the new map
	CHANGINGMAP_SCROLL_LOADED,	// Serve has loaded the new map 
	CHANGINGMAP_SCROLL,			// Client is scrolling
	CHANGINGMAP_SCROLLOVER,		// Client has finished scrolling
	SCRIPT,
	DIALOG_FULLDISPLAY,		// Client has displayed the whole sentence
	CLEAR;				// Disable all fades and clear current map
}
