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


public abstract class EditableItemMenu extends ItemMenu {

	public static String acceptableChar="ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.,()- ";
	
	StringBuilder text;
	
	public EditableItemMenu(StringBuilder p_text) {
		text=p_text;
	}
	
	/**
	 * Add character to the item's text. Replace space by underscore.
	 * @param p_ch
	 */
	public void addText(char p_ch) {
		if (text.length() < 20) {
			text.append(p_ch);
		}
	}
	
	public void removeLastChar() {
		if (text.length() > 0) {
			text.deleteCharAt(text.length() - 1);
		}
	}
	
	@Override
	public String getText() {
		return text.toString();
	}

}
