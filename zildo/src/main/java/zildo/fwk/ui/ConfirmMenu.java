/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
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

package zildo.fwk.ui;

/**
 * Confirm menu, with two provided item menu.
 * 
 * It hasn't a high interest, for now ...
 * 
 * @author Tchegito
 * 
 */
public class ConfirmMenu extends Menu {

	public ConfirmMenu(String p_message, final ItemMenu p_nextYes, final ItemMenu p_nextNo) {
		super(p_message);
		
		setMenu(p_nextYes, p_nextNo);
	}
}
