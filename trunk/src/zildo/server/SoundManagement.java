package zildo.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import zildo.fwk.net.NetServer;
import zildo.monde.WaitingSound;
import zildo.monde.decors.SpriteEntity;
import zildo.monde.map.Point;

/**
 * We just manage here a sound queue.
 * 
 * It will be sent to all clients, every frame, then cleared.
 * See {@link NetServer#sendSounds()}
 * 
 * @author tchegito
 *
 */
public class SoundManagement {

	List<WaitingSound> soundQueue=new ArrayList<WaitingSound>();
	
	private void broadcastSound(String p_name, int p_x, int p_y) {
		soundQueue.add(new WaitingSound(p_name, new Point(p_x, p_y)));
	}
	
	/**
	 * Send to all clients a sound from given entity's location
	 * @param p_soundName
	 * @param p_source
	 */
	public void broadcastSound(String p_name, Point p_location) {
		soundQueue.add(new WaitingSound(p_name, p_location));
	}
	
	public List<WaitingSound> getQueue() {
		return Collections.unmodifiableList(soundQueue);
	}
	
	public void resetQueue() {
		soundQueue.clear();
	}

	/**
	 * Send to all clients a sound from given entity's location
	 * @param p_soundName
	 * @param p_source
	 */
	public void broadcastSound(String p_soundName, SpriteEntity p_source) {
	    broadcastSound(p_soundName, (int) p_source.x / 16, (int) p_source.y / 16);
	}
}
