package zildo.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import zildo.monde.WaitingSound;
import zildo.monde.map.Point;

public class SoundManagement {

	List<WaitingSound> soundQueue=new ArrayList<WaitingSound>();
	
	public void broadcastSound(String p_name, int p_x, int p_y) {
		soundQueue.add(new WaitingSound(p_name, new Point(p_x, p_y)));
	}
	
	public void broadcastSound(String p_name, Point p_location) {
		soundQueue.add(new WaitingSound(p_name, p_location));
	}
	
	public List<WaitingSound> getQueue() {
		return Collections.unmodifiableList(soundQueue);
	}
	
	public void resetQueue() {
		soundQueue.clear();
	}
}
