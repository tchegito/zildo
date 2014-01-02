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

package zeditor.windows.subpanels;

import javax.swing.JScrollBar;

/**
 * Just a fake scroll bar to get scrolling without displaying a grey bar.
 * User just have to use the mouse wheel. So we save some space on the right.
 * 
 * @author Tchegito
 *
 */
@SuppressWarnings("serial")
public class TweekedScrollbar extends JScrollBar {

	public TweekedScrollbar() {
		setUnitIncrement(16);
	}
	
	@Override
	public boolean isVisible() {
		// With this wrong information (scrollbar is hidden actually), we allow
		// user to scroll with the wheel.
		return true;
	}
}
