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

package zildo.fwk.ui;

import zildo.client.ClientEngineZildo;

public class InfoMenu extends Menu {

	public InfoMenu(String p_message, final Menu p_next) {
		this(p_message, "Ok", p_next);
	}
	
	public InfoMenu(String p_message, String p_itemText, final Menu p_next) {
		super(p_message);
		
		ItemMenu itemOk=new ItemMenu(p_itemText) {
			@Override
			public void run() {
				ClientEngineZildo.getClientForMenu().handleMenu(p_next);
			}
		};
		
        setMenu(itemOk);
	}
}
