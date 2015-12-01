/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
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

package zildo.monde.sprites.utils;

import zildo.client.sound.BankSound;
import zildo.fwk.ZUtils;
import zildo.server.EngineZildo;

/**
 * @author Tchegito
 *
 * Just a basic class to return a wrapping sequenced sound.
 */
public class SoundGetter {

	final BankSound start;
	final int end;	// The next sound index after given end
    private BankSound currentSnd;
	int duration;
	long lastTime = 0;
	
    boolean shuffle;	// TRUE=random sound, but different than the previous one
    
	public SoundGetter(BankSound p_start, BankSound p_end, int p_duration) {
		this(p_start, p_end, p_duration, false);
	}
	
	public SoundGetter(BankSound p_start, BankSound p_end, int p_duration, boolean p_shuffle) {
		start = p_start;
		end = p_end.ordinal();
		
		duration = p_duration;
		
		currentSnd = p_start;
		shuffle = p_shuffle;
	}
	
	public int getDuration() {
		return duration;
	}
    
    public final BankSound getSound() {
    	BankSound next;
    	if (shuffle) {
    		// Picks a random sound between given range, but not the same that last one
    		int chosen = currentSnd.ordinal();
    		int previous = chosen;
    		while (chosen == previous) {
    			chosen = EngineZildo.hasard.rangeInt(start.ordinal(), end);
    		}
    		next = BankSound.values()[chosen];
    	} else {
    		// Picks the following sound, wrapping if necessary
    		if (currentSnd.ordinal() == end) {
    			next = start;
    		} else {
    			next = currentSnd.next();
    		}
    	}
        currentSnd = next;
        return next;
    }
    
    /**
     * Returns the next sound, considering a delay between last one at least equals to 'duration'.<br/>
     * If delay is not fullfilled, returns NULL.
     * @return BankSound
     */
    public final BankSound getSingleSound() {
    	long now = ZUtils.getTime();
    	if (now > (lastTime + duration)) {
    		lastTime = ZUtils.getTime();
    		return getSound();
    	}
    	return null;
    }
}
