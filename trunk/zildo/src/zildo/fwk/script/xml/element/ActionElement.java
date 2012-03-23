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

package zildo.fwk.script.xml.element;

import org.w3c.dom.Element;

import zildo.monde.util.Point;

public class ActionElement extends AnyElement {

	public enum ActionKind {
		actions, pos, moveTo, speak, script, angle, wait, sound, clear, fadeIn, fadeOut, 
		map, focus, spawn, exec, take, mapReplace, music, animation, impact, remove, markQuest, putDown, attack, activate;

		public static ActionKind fromString(String p_name) {
			for (ActionKind kind : values()) {
				if (kind.toString().equalsIgnoreCase(p_name)) {
					return kind;
				}
			}
			return null;
		}
	}

	public String who; // Characters
	public String what; // Camera, elements
	public String fx;
	public boolean unblock;
	public boolean backward = false; // Character moves back
	public boolean open = false; // Can open doors
	public boolean delta = false; // for 'moveTo' and 'pos' : means that 'pos'
									// should be added to current location
	public boolean unstoppable = false; // TRUE=no collision for this movement
										// (for 'moveTo')
	public ActionKind kind;
	public Point location;
	public String text;
	public int val;
	public float speed;
	public boolean activate;

	public ActionElement(ActionKind p_kind) {
		kind = p_kind;
		// xmlElement = ScriptWriter.document.createElement(kind.toString());
	}

	@Override
	public void parse(Element p_elem) {
		if (kind == null) {
			throw new RuntimeException("Action kind is unknown !");
		}

		// Store XML element in order to read easier from ZEditor
		xmlElement = p_elem;

		// Read common attributes
		who = readAttribute("who");
		what = readAttribute("what");
		fx = p_elem.getAttribute("fx");
		unblock = isTrue("unblock");
		speed = Float.valueOf("0" + p_elem.getAttribute("speed"));
		unstoppable = isTrue("unstoppable");
		// Read less common ones
		String strPos = p_elem.getAttribute("pos");
		String strAngle = p_elem.getAttribute("angle");
		switch (kind) {
		case spawn:
		case animation:
		case impact:
			location = Point.fromString(strPos);
			if (!"".equals(strAngle)) {
				val = Integer.valueOf(strAngle);
			}
			text = readAttribute("type");
			break;
		case speak:
			text = readAttribute("text");
			break;
		case sound:
		case map:
		case music:
			// String
			text = readAttribute("name");
			break;
		case moveTo:
			backward = isTrue("backward");
			open = isTrue("open");
		case pos:
			// Position
			location = Point.fromString(strPos);
			delta = isTrue("delta");
			break;
		case script:
		case angle:
		case wait:
			val = readInt("value");
			break;
		case fadeIn:
		case fadeOut:
			// Integer
			val = readInt("type");
			break;
		case focus:
			delta = isTrue("delta");
			if (readAttribute("unblock") == null) { 
				unblock = true;
			}
			break;
		case take:
			val = readInt("value");
		case putDown:
		case attack:
			text = p_elem.getAttribute("item");
			break;
		case exec:
			text = p_elem.getAttribute("script");
			break;
		case mapReplace:
			text = p_elem.getAttribute("name");
			break;
		case markQuest:
			text = readAttribute("name");
			val = isTrue("value") ? 1 : 0;
			break;
		case activate:
			activate = isTrue("value");
			break;
		}
	}

	/**
	 * Update attribute's value. (used only for ZEditor)
	 * 
	 * @param p_name
	 * @param p_value
	 */
	public void setAttribute(String p_name, String p_value) {
		xmlElement.setAttribute(p_name, p_value);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(kind).append(" ");
		if (who != null) {
			sb.append(who).append(" ");
		}
		if (what != null) {
			sb.append(what).append(" ");
		}
		if (location != null) {
			sb.append(location).append(" ");
		}
		if (text != null) {
			sb.append(text).append(" ");
		}
		return sb.toString();
	}

}