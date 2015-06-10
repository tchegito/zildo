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

package zildo.fwk.script.model;

import java.util.ArrayList;
import java.util.List;

import zildo.fwk.script.logic.FloatExpression;
import zildo.monde.items.ItemKind;
import zildo.server.EngineZildo;

/**
 * Zildo-Script Expression.
 * 
 * @author Tchegito
 * 
 */
public class ZSExpression {

	String questName;
	boolean done;	// True if predicate is prefixed by a '!'

	FloatExpression floatExpr = null;
	
	public ZSExpression(String p_questName) {
		questName = p_questName.trim();
		done = true;
	}

	public ZSExpression(String p_questName, boolean p_done) {
		this(p_questName);
		done = p_done;
		
		if (p_questName.indexOf('<') != -1 || p_questName.indexOf('>') != -1) {
			floatExpr = new FloatExpression(p_questName);
		}
	}

	/**
	 * Parse a string whose form is a list of "[!]<questName>[&]" expression.
	 * 
	 * @param p_parseableString
	 * @return
	 */
	public static List<ZSExpression> parse(String p_parseableString) {
		List<ZSExpression> expressions = new ArrayList<ZSExpression>();
		String[] exprs = p_parseableString.split("&");
		for (String s : exprs) {
			boolean not = s.startsWith("!");
			expressions.add(new ZSExpression(s.replaceAll("&", "").replaceAll(
					"!", ""), !not));
		}
		return expressions;
	}

	public boolean isTrue() {
		boolean result = false;

		if (floatExpr != null) {
			result = floatExpr.evaluate(null) == 1;
		} else if (questName.startsWith("money")) {
			int price=Integer.valueOf(questName.substring("money".length()));
			int zildoMoney = EngineZildo.persoManagement.getZildo().getMoney();
			result = price<=zildoMoney;
		} else if (questName.startsWith("moon")) {
			int moonFragment=Integer.valueOf(questName.substring("moon".length()));
			int currentFragmentNb = EngineZildo.persoManagement.getZildo().getMoonHalf();
			result = currentFragmentNb >= moonFragment;
		} else if (questName.startsWith("item")) {
			String itemName=questName.substring("item".length());
			ItemKind kind = ItemKind.fromString(itemName);
			result = EngineZildo.persoManagement.getZildo().hasItem(kind);
		} else if (questName.equals("init")) {
			result = EngineZildo.persoManagement.getZildo().getDialoguingWith().getCompte_dialogue() == 0;
		} else if (questName.startsWith("M#")) {
			// Expression could be a map name, to match current map
			String mapName = questName.substring(2);
			result = mapName.equals(EngineZildo.mapManagement.getCurrentMap().getName());
		} else {
			// Default case : expression is the quest name
			result = EngineZildo.scriptManagement.isQuestOver(questName);
		}
		if (!done) {
			result = !result;
		}
		return result;
	}

	@Override
	public String toString() {
		return (!done ? "!" : "") + questName;
	}
}
