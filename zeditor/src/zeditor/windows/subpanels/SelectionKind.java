/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
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

package zeditor.windows.subpanels;

/**
 * Kind of selection done with tabbed panes (Tiles, prefetch, chaining points ...)
 * @author Tchegito
 *
 */
public enum SelectionKind {

	TILES, PREFETCH, SPRITES, PERSOS, CHAININGPOINT, STATS, SCRIPTS;
	
	public static SelectionKind fromInt(int p_value) {
		if (p_value > SelectionKind.values().length) {
			return null;
		}
		return values()[p_value];
	}
}
