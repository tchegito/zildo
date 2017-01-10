package zeditor.fwk.awt;

import zildo.monde.map.ChainingPoint;

/** Contract for an object able to load a map and capture a screen view of it.
 * Mostly created to differentiate the realtime MapCaturer used in ZEditor, and the one for unit tests.
 * @author Tchegito
 *
 */
public interface MapCapturer {

	public ChainingPoint loadMap(String p_mapName, ChainingPoint p_fromChangingPoint);
	
	public void askCapture();
	public boolean isCaptureDone();
	
}
