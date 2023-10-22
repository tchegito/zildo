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

package zildo.fwk.script.xml.element.action;

/**
 * @author Tchegito
 *
 */
public enum ActionKind {

	actions, pos, moveTo, speak, script, angle, wait, sound, clear, fadeIn, fadeOut, map, focus, spawn, exec, stop, take, mapReplace, zikReplace, nameReplace, // History
																																							// actions
	music, animation, remove, markQuest, putDown, attack, activate, tile, filter, end, visible, respawn, zoom, herospecial, perso, sprite, 
	timer, loop, _for, lookFor, _throw, listen, seq, seqPerso;

	public static ActionKind fromString(String p_name) {
		for (ActionKind kind : values()) {
			if (kind.toString().equalsIgnoreCase(p_name)) {
				return kind;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return this == _throw ? "throw" : super.toString();
	}
}
