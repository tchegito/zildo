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

package zildo.fwk.script.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import zildo.fwk.script.context.IEvaluationContext;
import zildo.fwk.script.context.LocaleVarContext;
import zildo.fwk.script.logic.FloatExpression;
import zildo.monde.items.ItemKind;
import zildo.monde.items.StoredItem;
import zildo.monde.sprites.magic.Affection.AffectionKind;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.server.EngineZildo;

/**
 * Zildo-Script Expression.
 * 
 * @author Tchegito
 * 
 */
public class ZSExpression {

	// Reserved words
	private static final String RW_MONEY = "money";
	private static final String RW_MOON = "moon";
	private static final String RW_NETTLE = "nettle";
	private static final String RW_ITEM = "item";
	private static final String RW_INIT = "init";
	private static final String RW_MAP = "M#";
	private static final String RW_PERSO = "P#";
	private static final String RW_ELEMENT = "E#";
	private static final String RW_STORE = "ooo";
	private static final String RW_AFFECT = "aff";
	
	String questName;
	boolean done;	// True if predicate is prefixed by a '!'

	Pattern pattern;	// For wildcards regex
	
	FloatExpression floatExpr = null;
	
	public ZSExpression(String p_questName) {
		questName = p_questName.trim();
		done = true;
	}

	public ZSExpression(String p_questName, boolean p_done) {
		this(p_questName);
		done = p_done;
		
		if (p_questName.indexOf('<') != -1 || p_questName.indexOf('>') != -1 || p_questName.indexOf('=' )!= -1) {
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

	/** Returns TRUE if given expression is evaluated to TRUE. A lot of predefined expressions are permitted.
	 * We used context only for floatExpr evaluation.
	 */
	public boolean isTrue(IEvaluationContext context) {
		boolean result = false;

		if (floatExpr != null) {
			result = floatExpr.evaluate(context) == 1;
		} else if (questName.startsWith(RW_MONEY)) {
			int price=Integer.valueOf(questName.substring(RW_MONEY.length()));
			int zildoMoney = zildo().getMoney();
			result = price<=zildoMoney;
		} else if (questName.startsWith(RW_MOON)) {
			int moonFragment=Integer.valueOf(questName.substring(RW_MOON.length()));
			int currentFragmentNb = zildo().getMoonHalf();
			result = currentFragmentNb >= moonFragment;
		} else if (questName.startsWith(RW_NETTLE)) {
			int nettleAmount=Integer.valueOf(questName.substring(RW_NETTLE.length()));
			int currentAmount = zildo().getCountNettleLeaf();
			result = currentAmount >= nettleAmount;
		} else if (questName.startsWith(RW_ITEM) && zildo() != null) {
			String itemName=questName.substring(RW_ITEM.length());
			ItemKind kind = ItemKind.fromString(itemName);
			result = zildo().hasItem(kind);
		} else if (questName.equals(RW_INIT)) {
			result = zildo().getDialoguingWith().getCompte_dialogue() == 0;
		} else if (questName.startsWith(RW_MAP)) {
			// Expression could be a map name, to match current map
			String mapName = questName.substring(RW_MAP.length());
			String currentMap = EngineZildo.mapManagement.getCurrentMap().getName();
			if (mapName.contains("*")) {	// Wildcards
				if (pattern == null) {
					pattern = Pattern.compile(mapName.replace("*", ".*"));
				}
				result = pattern.matcher(currentMap).matches();
			} else {
				result = mapName.equals(currentMap);
			}
		} else if (questName.startsWith(RW_PERSO)) {
			String persoName = questName.substring(RW_PERSO.length());
			result = EngineZildo.persoManagement.getNamedPerso(persoName) != null;
		} else if (questName.startsWith(RW_ELEMENT)) {
			String elementName = questName.substring(RW_ELEMENT.length());
			while (elementName != null && elementName.startsWith(LocaleVarContext.VAR_IDENTIFIER)) {
				String attemptContext = context.getString(elementName);
				if (attemptContext == null) {
					attemptContext = EngineZildo.scriptManagement.getVarValue(elementName);
				}
				if (attemptContext == null) break;
				elementName = attemptContext;
			}
			result = EngineZildo.spriteManagement.getNamedElement(elementName) != null;
		} else if (questName.startsWith(RW_STORE)) {
			// Does merchant's store is empty ?
			String persoName = questName.substring(RW_STORE.length());
			String itemsAsString = EngineZildo.scriptManagement.getVarValue(persoName);
			return StoredItem.fromString(itemsAsString).isEmpty(); 
		} else if (questName.startsWith(RW_AFFECT)) {
			String expected = questName.substring(RW_AFFECT.length());
			result = zildo().isAffectedBy(AffectionKind.valueOf(expected));
		} else {
			// Default case : expression is the quest name
			result = EngineZildo.scriptManagement.isQuestOver(questName);
		}
		if (!done) {
			result = !result;
		}
		return result;
	}

	
	/** Convenience method to reduce code **/
	private PersoPlayer zildo() {
		return EngineZildo.persoManagement.getZildo();
	}
	
	@Override
	public String toString() {
		return (!done ? "!" : "") + questName;
	}
}
