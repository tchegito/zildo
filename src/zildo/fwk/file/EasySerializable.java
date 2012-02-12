/**
 * Legend of Zildo
 * Copyright (C) 2006-2012 Evariste Boussaton
 * Based on original Zelda : link to the past (C) Nintendo 1992
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

package zildo.fwk.file;

/**
 * This interface provides a marker for class being serializable within the game.<p/>
 * 
 * We only mention <b>serialize</b> operation in this contract. The opposite operation, <b>deserialize</b>, can't be
 * proposed because of its static character. It's not permitted in Java to have 'static' method in an interface.
 * Though, it should be part of the contract !
 * @author Tchegito
 *
 */
public interface EasySerializable {

	/**
	 * Serialize the instance into the given buffer
	 * @param p_buffer
	 */
	void serialize(EasyBuffering p_buffer);
	

	
	/**
	 * Unserialize the given buffer into parameterized type and return.<p/>
	 * 
	 * @param p_buffer
	 * @return T
	 */
	// No declaration here !
}
