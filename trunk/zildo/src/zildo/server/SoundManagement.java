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

package zildo.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import zildo.client.sound.AudioBank;
import zildo.fwk.net.NetServer;
import zildo.fwk.net.TransferObject;
import zildo.monde.WaitingSound;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.persos.PersoZildo;
import zildo.monde.util.Point;
import zildo.server.state.ClientState;

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
	boolean forceMusic=false;	// TRUE if music is forced by script (ex:Angoisse) / FALSE if music is automatic with map
	
	private void addSound(AudioBank p_name, int p_x, int p_y, boolean p_broadcast, TransferObject p_object) {
		soundQueue.add(new WaitingSound(p_name, new Point(p_x, p_y), p_broadcast, p_object));
	}
	
	/**
	 * Send to all clients a sound from given entity's location
	 * @param p_soundName
	 * @param p_source
	 */
	public void broadcastSound(AudioBank p_name, Point p_location) {
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
	public void broadcastSound(AudioBank p_soundName, SpriteEntity p_source) {
	    addSound(p_soundName, (int) p_source.x, (int) p_source.y, true, null);
	}
	
	/**
	 * Send to one client a sound from given entity's location
	 * @param p_soundName
	 * @param p_source
	 */
	public void playSound(AudioBank p_soundName, PersoZildo p_zildo) {
		TransferObject obj=null;
		int x=0, y=0;
		if (p_zildo != null) {
			ClientState cl=Server.getClientFromZildo(p_zildo);
			obj=cl != null ? cl.location : null;
			x = (int) p_zildo.x;
			y = (int) p_zildo.y;
		}
	    addSound(p_soundName, x, y, false, obj);
	}

	public boolean isForceMusic() {
		return forceMusic;
	}

	public void setForceMusic(boolean forceMusic) {
		this.forceMusic = forceMusic;
	}
}
