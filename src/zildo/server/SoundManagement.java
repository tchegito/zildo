package zildo.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import zildo.client.SoundPlay.BankSound;
import zildo.fwk.net.NetServer;
import zildo.fwk.net.TransferObject;
import zildo.monde.WaitingSound;
import zildo.monde.map.Point;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.persos.PersoZildo;

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
	
	private void addSound(BankSound p_name, int p_x, int p_y, boolean p_broadcast, TransferObject p_object) {
		soundQueue.add(new WaitingSound(p_name, new Point(p_x, p_y), p_broadcast, p_object));
	}
	
	/**
	 * Send to all clients a sound from given entity's location
	 * @param p_soundName
	 * @param p_source
	 */
	public void broadcastSound(BankSound p_name, Point p_location) {
		soundQueue.add(new WaitingSound(p_name, p_location, true, null));
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
	public void broadcastSound(BankSound p_soundName, SpriteEntity p_source) {
	    addSound(p_soundName, (int) p_source.x / 16, (int) p_source.y / 16, true, null);
	}
	
	/**
	 * Send to one client a sound from given entity's location
	 * @param p_soundName
	 * @param p_source
	 */
	public void playSound(BankSound p_soundName, PersoZildo p_zildo) {
		TransferObject obj=null;
		if (p_zildo != null) {
			ClientState cl=Server.getClientFromZildo(p_zildo);
			obj=cl != null ? cl.location : null;
		}
	    addSound(p_soundName, 0,0, false, obj);
	}
}
