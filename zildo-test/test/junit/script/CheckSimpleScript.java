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

package junit.script;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import static org.mockito.Mockito.*;

import zildo.fwk.script.logic.FloatVariable;
import zildo.fwk.script.model.ZSCondition;
import zildo.fwk.script.model.ZSSwitch;
import zildo.monde.items.Item;
import zildo.monde.items.ItemKind;
import zildo.monde.map.Area;
import zildo.monde.sprites.persos.PersoNJ;
import zildo.monde.sprites.persos.PersoZildo;
import zildo.monde.util.Angle;
import zildo.server.EngineZildo;
import zildo.server.MapManagement;
import zildo.server.PersoManagement;

/**
 * Checks the {@link ZSSwitch} and linked classes.
 * @author Tchegito
 *
 */
public class CheckSimpleScript extends SimpleEngineScript {

	@Test
	public void simpleSwitch() {
		// Create a switch case : case 'flut' => 8 / case 'enlevement' => 3 / default => 0
		ZSSwitch simple = new ZSSwitch(0).addCondition("flut", 8).addCondition("enlevement",3);
		
		checkSwitch(simple);
	}
	
	@Test
	public void parsedSwitch() {
		ZSSwitch parsed = ZSSwitch.parseForDialog("flut&!ferme:8,enlevement:3,0");
		
		checkSwitch(parsed);
	}
	
	private void checkSwitch(ZSSwitch sw) {
		// Default
		Assert.assertEquals(0, sw.evaluateInt());
		
		// enlevement
		scriptMgmt.accomplishQuest("enlevement", false);
		Assert.assertEquals(3, sw.evaluateInt());

		// flut
		scriptMgmt.accomplishQuest("flut", false);
		Assert.assertEquals(8, sw.evaluateInt());
	
	}
	
	/**
	 * Note that 'dice10' built-in function is defined into {@link FloatVariable}.
	 */
	@Test
	public void conditional() {
		ZSSwitch parsed = ZSSwitch.parseForDialog("dice10 > 5:NOTE2,NOTE"	);
		int cnt1 = 0;
		int cnt2 = 0;
		for (int i=0;i<20;i++) {
			String res = parsed.evaluate();
			if ("NOTE2".equals(res)) {
				cnt1++;
			} else if ("NOTE".equals(res)) {
				cnt2++;
			}
		}
		Assert.assertTrue(cnt1 > 0 && cnt2 > 0);
	}
	
	@Test
	public void withoutCondition() {
		ZSSwitch parsed = ZSSwitch.parseForDialog("NOTE2");
		Assert.assertTrue(parsed.evaluate().equals("NOTE2"));
	}
	
	@Test
	public void parenthese() {
		// Make sure this doesn't hang up
		ZSSwitch sw = ZSSwitch.parseForScript("!voleursm3(8,8)");
		
		Assert.assertTrue(sw.evaluate().equals(ZSCondition.TRUE));
		
		EngineZildo.scriptManagement.accomplishQuest("voleursm3(8,8)", false);
		Assert.assertTrue(!sw.evaluate().equals(ZSCondition.TRUE));
		
	}
	
	@Test
	public void reservedWords() {
		// 1) Money
		ZSSwitch simple = new ZSSwitch(0).addCondition("money30", 1).addCondition("money20",3);
		
		PersoZildo zildo = new PersoZildo(0);
		zildo.setAngle(Angle.NORD);
		// Mock persomanagement to return our Zildo
		PersoManagement pm = mock(PersoManagement.class);
		EngineZildo.persoManagement = pm;
		when(pm.getZildo()).thenReturn(zildo);
		
		zildo.setMoney(0);
		Assert.assertSame(0, simple.evaluateInt());
		zildo.setMoney(25);
		Assert.assertSame(3, simple.evaluateInt());
		zildo.setMoney(31);
		Assert.assertSame(1, simple.evaluateInt());
		
		// 2) Moon half
		simple = new ZSSwitch(0).addCondition("moon3", 1).addCondition("itemEMPTY_BAG", 2).addCondition("itemSWORD", 3);
		Assert.assertSame(0, simple.evaluateInt());
		zildo.setMoonHalf(4);
		Assert.assertSame(1, simple.evaluateInt());
		
		// 3) Items
		zildo.setMoonHalf(0);
		zildo.getInventory().add(new Item(ItemKind.SWORD));
		Assert.assertSame(3, simple.evaluateInt());
		zildo.getInventory().add(new Item(ItemKind.EMPTY_BAG));
		Assert.assertSame(2, simple.evaluateInt());
		
		// 4) Init
		simple = new ZSSwitch(0).addCondition("init", 2);
		PersoNJ who = new PersoNJ();
		zildo.setDialoguingWith(who);
		Assert.assertSame(2, simple.evaluateInt());
		who.setCompte_dialogue(1);
		Assert.assertSame(0, simple.evaluateInt());
	}
	
	@Test
	public void mapscriptCondition() {
		// We are going to check map exclusions
		ZSSwitch sw = ZSSwitch.parseForMapCondition("!chateaucoucou1-!chateaucoucou2");
		
		Area fakeMap = new Area();
		fakeMap.setName("kikoo");
		MapManagement mm = mock(MapManagement.class);
		EngineZildo.mapManagement = mm;
		when(mm.getCurrentMap()).thenReturn(fakeMap);
		
		// Check if current map respect conditions (different from chateaucoucou1/2)
		Assert.assertTrue(sw.evaluateInt() == 1);
		
		fakeMap.setName("chateaucoucou1");

		// Now check being on an expected map 
		Assert.assertTrue(sw.evaluateInt() == 0);
		
	}
	
	@Override
	@After
	public void tearDown() {
		EngineZildo.scriptManagement = null;
	}
}
