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
import zildo.monde.Hasard;

/**
 * @author Tchegito
 *
 * Just a basic class to return a wrapping sequenced sound.
 */
public class SoundGetter {

	final BankSound start;
	final BankSound out;	// The next sound after given end
    private BankSound currentSnd;
	
    boolean shuffle;	// TRUE=random sound, but different than the previous one
    
	public SoundGetter(BankSound p_start, BankSound p_end) {
		this(p_start, p_end, false);
	}
	
	public SoundGetter(BankSound p_start, BankSound p_end, boolean p_shuffle) {
		start = p_start;
		out = p_end.next();
		
		currentSnd = p_start;
		shuffle = p_shuffle;
	}
	
    
    public final BankSound getSound() {
    	BankSound next;
    	if (shuffle) {
    		// Picks a random sound between given range, but not the same that last one
    		int chosen = currentSnd.ordinal();
    		int previous = chosen;
    		while (chosen == previous) {
    			chosen = Hasard.rangeInt(start.ordinal(), out.ordinal() - 1);
    		}
    		next = BankSound.values()[chosen];
    	} else {
    		// Picks the following sound, wrapping if necessary
	        next = currentSnd.next();
	        if (next == out) {
	        	next = start;
	        }
    	}
        currentSnd = next;
        return next;
    }
}
