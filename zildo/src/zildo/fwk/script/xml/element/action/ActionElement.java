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

package zildo.fwk.script.xml.element.action;

import org.w3c.dom.Element;

import zildo.fwk.script.logic.FloatExpression;
import zildo.fwk.script.model.ZSSwitch;
import zildo.fwk.script.model.point.IPoint;
import zildo.fwk.script.xml.element.LanguageElement;
import zildo.monde.sprites.persos.Perso.PersoInfo;

public class ActionElement extends LanguageElement {

	public String who; // Characters
	public String what; // Camera, elements
	public String effect;
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
	public String parent;	// For 'perso'
	public String impact;	// For 'spawn' => describe a ImpactKind element
	public FloatExpression addSpr;	// For 'perso' and 'spawn' (never a float value, but we can use context variables)
	
	// Only used for 'spawn' on 'chained' attribute
	public int chainCount = -1;
	public FloatExpression chainDelay;
	
	private ZSSwitch switchExpression;

	public FloatExpression[] v;
	public FloatExpression[] a;
	public FloatExpression[] f;
	public FloatExpression alphaA;
	public FloatExpression alphaV;
	public FloatExpression zoom;	// Used in 'moveTo'
	public int alpha;
	public int deltaFloor;	// Only for 'moveTo' action
	public int pv;
	
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
		String strReverse = readAttribute("reverse");
		// Read less common ones
		String strPos = p_elem.getAttribute("pos");
		String strAngle = p_elem.getAttribute("angle");
		switch (kind) {
		case sprite:
			text = readAttribute("type");
			if (strReverse != null) {
				reverse = ZSSwitch.parseForDialog(strReverse);
			}
			break;
		case _throw:
			target = IPoint.fromString(readAttribute("to"));
			way = readAttribute("way");
		case spawn:
			delta = isTrue("delta");
			if (strReverse == null) {
				reverse = null;
			} else {
				reverse = ZSSwitch.parseForDialog(strReverse);
			}
			rotation = readInt("rotation");

			shadow = readAttribute("shadow");
			addSpr = getFloatExpr("addSpr", "0");
			// Chained
			String temp = readAttribute("chained");
			if (temp != null) {	// Expect well-formed content
				int virgulePos = temp.indexOf(",");
				chainCount = Integer.parseInt(temp.substring(0,  virgulePos));
				chainDelay = new FloatExpression(temp.substring(virgulePos+1));
			}
			zoom = getFloatExpr("zoom");
			impact = readAttribute("impact");
		case animation:
			location = IPoint.fromString(strPos);
			if (!"".equals(strAngle)) {
				val = Integer.valueOf(strAngle);
			}
		case perso:
			text = readAttribute("type");
			parent = readAttribute("parent");
			String strInfo = readAttribute("info");
			if (strInfo != null) {
				info = PersoInfo.valueOf(strInfo);
			}
			attente = readInt("attente", -1);
			action = readAttribute("action"); // Empty string means "no action"
			weapon = readAttribute("weapon");
			alpha = readInt("alpha", -1);
			alphaA = getFloatExpr("alphaA");
			alphaV = getFloatExpr("alphaV");
			if (kind == ActionKind.perso) addSpr = getFloatExpr("addSpr", "-1");
			temp = readAttribute("z");
			if (temp != null) {
				z = new FloatExpression(temp);
			}
			if (strReverse != null) {
				reverse = ZSSwitch.parseForDialog(strReverse);
			}
			pv = readInt("pv", -1);
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
			zoom = getFloatExpr("zoom");
			deltaFloor = readInt("deltaFloor", 0);
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
		case exec:	// Execute a script (scene)
		case stop:	// Stop a script
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
			if (strReverse != null) {
				reverse = ZSSwitch.parseForDialog(strReverse);
			}
			break;
		case remove:
			text = readAttribute("chaining");
			way = readAttribute("type");	// To remove any entites of given type
			break;
		case herospecial:			
			text = readAttribute("arg");
			val = readInt("value");
		default:
			break;
		}

		// As several variables are used for different usage (which is bad),
		// make specific here
		if (kind == ActionKind.spawn || kind == ActionKind._throw || kind == ActionKind.perso || kind == ActionKind.sprite) {
			if (text != null) {
				switchExpression = ZSSwitch.parseForDialog(text);
			}
			// Read V,A and F coordinates
			v = read3Coordinates("v");
			a = read3Coordinates("a");
			f = read3Coordinates("f");
			alphaA = getFloatExpr("alphaA");
			alpha = readInt("alpha", -1);
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

	private FloatExpression getFloatExpr(String attName, String defaultValue) {
		String expr = readAttribute(attName);
		if (expr == null || expr.length() == 0) {
			expr = defaultValue;
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
}