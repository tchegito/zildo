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

package zildo.fwk.script.xml.element.action;

import org.w3c.dom.Element;

import zildo.fwk.script.logic.FloatExpression;
import zildo.fwk.script.model.ZSSwitch;
import zildo.fwk.script.model.point.IPoint;
import zildo.fwk.script.xml.element.LanguageElement;
import zildo.monde.sprites.persos.Perso.PersoInfo;
import zildo.server.EngineZildo;

public class ActionElement extends LanguageElement {

	public enum ActionKind {
		actions, pos, moveTo, speak, script, angle, wait, sound, clear, fadeIn, fadeOut, map, focus, spawn, exec, take, mapReplace, zikReplace, nameReplace, // History
																																								// actions
		music, animation, impact, remove, markQuest, putDown, attack, activate, tile, filter, end, visible, respawn, zoom, herospecial, perso, timer, lookFor, _throw;

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
	public Boolean foreground = false; // Is sprite/perso on foreground?
	public ActionKind kind;
	public IPoint location;
	public IPoint target; // For 'launch' action
	public String text;
	public String way; // For 'launch' action
	public int val;
	public ZSSwitch reverse;
	public int rotation;
	public int attente;
	public int floor;
	public FloatExpression z; // Z coordinate for location
	public PersoInfo info; // PersoInfo
	public float speed;
	public boolean activate;
	public String action; // To run a PersoAction, with "perso" ActionKind
	public String shadow; // Only used in "spawn"
	public String weapon; // For 'perso' and 'spawn'

	private ZSSwitch switchExpression;

	public FloatExpression[] v;
	public FloatExpression[] a;
	public FloatExpression[] f;
	public FloatExpression alphaA;
	public int alpha;
	public int deltaFloor;	// Only for 'perso' action
	
	public int back, back2, fore; // just for Tile action

	public ActionElement(ActionKind p_kind) {
		kind = p_kind;
	}

	@Override
	public void parse(Element p_elem) {
		if (kind == null) {
			throw new RuntimeException("Action kind is unknown !");
		}

		// Store XML element in order to read easier from ZEditor, and use all
		// convenience read methods
		super.parse(p_elem);

		// Read common attributes
		who = readAttribute("who");
		what = readAttribute("what");
		effect = readAttribute("effect");
		unblock = isTrue("unblock");
		speed = Float.valueOf("0" + p_elem.getAttribute("speed"));
		unstoppable = isTrue("unstoppable");
		foreground = readBoolean("foreground");
		floor = readInt("floor", 1);
		// Read less common ones
		String strPos = p_elem.getAttribute("pos");
		String strAngle = p_elem.getAttribute("angle");
		switch (kind) {
		case _throw:
			target = IPoint.fromString(readAttribute("to"));
			way = readAttribute("way");
		case spawn:
			delta = isTrue("delta");
			String strReverse = readAttribute("reverse");
			if (strReverse == null) {
				reverse = null;
			} else {
				reverse = ZSSwitch.parseForDialog(strReverse);
			}
			rotation = readInt("rotation");
			String temp = readAttribute("z");
			if (temp != null) {
				z = new FloatExpression(temp);
			}
			shadow = readAttribute("shadow");
		case animation:
		case impact:
			location = IPoint.fromString(strPos);
			if (!"".equals(strAngle)) {
				val = Integer.valueOf(strAngle);
			}
		case perso:
			text = readAttribute("type");
			String strInfo = readAttribute("info");
			if (strInfo != null) {
				info = PersoInfo.valueOf(strInfo);
			}
			attente = readInt("attente", -1);
			action = readAttribute("action"); // Empty string means "no action"
			weapon = readAttribute("weapon");
			alpha = readInt("alpha", -1);
			alphaA = getFloatExpr("alphaA");
			deltaFloor = readInt("deltaFloor", 0);
			break;
		case speak:
			text = readAttribute("text");
			break;
		case sound:
		case map:
		case music:
			// String
			text = readNonEmptyAttribute("name");
			break;
		case moveTo:
			backward = isTrue("backward");
			open = isTrue("open");
			text = readAttribute("way");
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
		case nameReplace:
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
			back = (int) evaluateFloat("back", -2);
			back2 = (int) evaluateFloat("back2", -2);
			fore = (int) evaluateFloat("fore", -2);
			action = readAttribute("action"); // Empty string means "no action"
			break;
		case remove:
			text = readAttribute("chaining");
			break;
		case herospecial:			
			text = readAttribute("arg");
			val = readInt("value");
			break;
		}

		// As several variables are used for different usage (which is bad),
		// make specific here
		if (kind == ActionKind.spawn || kind == ActionKind._throw) {
			switchExpression = ZSSwitch.parseForDialog(text);
			// Read V,A and F coordinates
			v = read3Coordinates("v");
			a = read3Coordinates("a");
			f = read3Coordinates("f");
			alphaA = getFloatExpr("alphaA");
			alpha = readInt("alpha", -1);
		}

		// Not needed anymore : free some memory
		if (!EngineZildo.game.editing) {
			xmlElement = null;
		}
	}

	private FloatExpression[] read3Coordinates(String prefix) {
		FloatExpression[] coords = null;
		FloatExpression coordX = getFloatExpr(prefix + "x");
		FloatExpression coordY = getFloatExpr(prefix + "y");
		FloatExpression coordZ = getFloatExpr(prefix + "z");
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

	@Override
	public void reset() {
		done = false;
		waiting = false;
	}
}