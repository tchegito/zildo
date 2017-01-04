package zeditor.fwk.awt;

import zildo.monde.map.ChainingPoint;

public interface MapCapturer {

	public ChainingPoint loadMap(String p_mapName, ChainingPoint p_fromChangingPoint);
	
	public void askCapture();
	public boolean isCaptureDone();
	
}
