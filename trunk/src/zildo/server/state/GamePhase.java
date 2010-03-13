/**
 * Legend of Zildo
 * Copyright (C) 2006-2010 Evariste Boussaton
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

package zildo.server.state;


/**
 *  We need a guide that authorizes actions following the game phase
 *
 * @author tchegito
 */
public enum GamePhase {
	INGAME(true, true, true), 
	DIALOG(false, true, false), 
	MAPCHANGE(false, false, false), 
	SCRIPT(false, true, false);
	
	private GamePhase(boolean p_moves, boolean p_action, boolean p_others) {
		moves=p_moves;
		action=p_action;
		others=p_others;
	}
	
	public boolean moves;	// Zildo moves
	public boolean action;	// Action button
	public boolean others;	// Inventory, weapon
}
