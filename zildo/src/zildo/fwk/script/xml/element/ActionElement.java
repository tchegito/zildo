/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
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

package zildo.fwk.script.xml.element;

import org.w3c.dom.Element;

import zildo.fwk.script.logic.FloatExpression;
import zildo.fwk.script.model.ZSSwitch;
import zildo.fwk.script.model.point.IPoint;

public class ActionElement extends AnyElement {

	public enum ActionKind {
		actions, pos, moveTo, speak, script, angle, wait, sound, clear, fadeIn, fadeOut, 
		map, focus, spawn, exec, take, mapReplace, zikReplace, music, animation, impact, remove, 
		markQuest, putDown, attack, activate,
		tile, filter, end, visible, respawn, zoom, herospecial, perso,
		timer;

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
	public String effect;
	public boolean unblock;
	public boolean backward = false; // Character moves back
	public boolean open = false; // Can open doors
	public boolean delta = false; // for 'moveTo' and 'pos' : means that 'pos'
									// should be added to current location
	public boolean unstoppable = false; // TRUE=no collision for this movement
										// (for 'moveTo')
	public Boolean foreground = false;	// Is sprite/perso on foreground?
	public ActionKind kind;
	public IPoint location;
	public String text;
	public int val;
	public int reverse;
	public int rotation;
	public int attente;
	public FloatExpression z;	// Z coordinate for location
	public String info;	// PersoInfo
	public float speed;
	public boolean activate;
	
	private ZSSwitch switchExpression;
	
	public FloatExpression[] v;
	public FloatExpression[] a;
	public FloatExpression[] f;
	public FloatExpression alphaA;
	
	public int back, back2, fore;	// just for Tile action
	
	public ActionElement(ActionKind p_kind) {
		kind = p_kind;
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
		effect = readAttribute("effect");
		unblock = isTrue("unblock");
		speed = Float.valueOf("0" + p_elem.getAttribute("speed"));
		unstoppable = isTrue("unstoppable");
		foreground = readBoolean("foreground");
		// Read less common ones
		String strPos = p_elem.getAttribute("pos");
		String strAngle = p_elem.getAttribute("angle");
		switch (kind) {
		case spawn:
			delta = isTrue("delta");
			reverse = readInt("reverse");
			rotation = readInt("rotation");
			String temp = readAttribute("z");
			if (temp != null) {
				z = new FloatExpression(temp);
			}
		case animation:
		case impact:
			location = IPoint.fromString(strPos);
			if (!"".equals(strAngle)) {
				val = Integer.valueOf(strAngle);
			}
		case perso:
			text = readAttribute("type");
			info = readAttribute("info");
			attente = readInt("attente", -1);
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
			if (!strPos.isEmpty()) {
				location = IPoint.fromString(strPos);
			}
			delta = isTrue("delta");
			z = new FloatExpression(readInt("z", -1));
			break;
		case script:
			text = readAttribute("text");
		case angle:
		case wait:
		case zoom:
		case herospecial:
			val = readInt("value");
			break;
		case fadeIn:
		case fadeOut:
		case filter:
		case end:
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
			text = readAttribute("item");
			break;
		case exec:
			text = p_elem.getAttribute("script");
			break;
		case mapReplace:
		case zikReplace:
			text = p_elem.getAttribute("name");
			break;
		case markQuest:
			text = readAttribute("name");
			val = isTrue("value") ? 1 : 0;
			break;
		case visible:
		case activate:
			activate = isTrue("value");
			break;
		case tile:
			location = IPoint.fromString(strPos);
			back = readInt("back", -1);
			back2 = readInt("back2", -1);
			fore = readInt("fore", -1);
			break;
		}
		
		// As several variables are used for different usage (which is bad), make specific here
		if (kind == ActionKind.spawn) {
			switchExpression = ZSSwitch.parseForDialog(text);
			// Read V,A and F coordinates
			v = read3Coordinates("v");
			a = read3Coordinates("a");
			f = read3Coordinates("f");
			alphaA = getFloatExpr("alphaA");
		}
	}

	private FloatExpression[] read3Coordinates(String prefix) {
		FloatExpression[] coords = null;
		FloatExpression coordX = getFloatExpr(prefix+"x");
		FloatExpression coordY = getFloatExpr(prefix+"y");
		FloatExpression coordZ = getFloatExpr(prefix+"z");
		if (coordX != null || coordY != null || coordZ != null) {
			coords = new FloatExpression[3];
			coords[0] = coordX;
			coords[1] = coordY;
			coords[2] = coordZ;
		}
		return coords;
	}
	
	private FloatExpression getFloatExpr(String attName) {
		String expr = readAttribute(attName);
		if (expr == null || expr.length() == 0) {
			return null;
		}
		return new FloatExpression(expr);
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

	public String getSpawnType() {
		return switchExpression.evaluate();
	}

	public void reset() {
		done = false;
	}
}