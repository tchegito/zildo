package junit.script;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import zildo.fwk.script.xml.ScriptReader;
import zildo.fwk.script.xml.element.AdventureElement;
import zildo.fwk.script.xml.element.ConditionElement;
import zildo.fwk.script.xml.element.ContextualActionElement;
import zildo.fwk.script.xml.element.LanguageElement;
import zildo.fwk.script.xml.element.MapscriptElement;
import zildo.fwk.script.xml.element.QuestElement;
import zildo.fwk.script.xml.element.SceneElement;
import zildo.fwk.script.xml.element.action.ActionElement;
import zildo.fwk.script.xml.element.action.ActionKind;
import zildo.fwk.script.xml.element.action.ActionsElement;
import zildo.fwk.script.xml.element.action.ForElement;
import zildo.fwk.script.xml.element.action.TimerElement;
import zildo.monde.Game;
import zildo.monde.quest.QuestEvent;
import zildo.server.EngineZildo;
import zildo.server.state.ScriptManagement;

public class CheckScriptParsing {

	@Test
	public void read() {
		EngineZildo.game = new Game(false);
		AdventureElement adventure = (AdventureElement) ScriptReader.loadScript("junit/script/parsing");
		
		Assert.assertEquals(3, adventure.getScenes().size());
		Assert.assertEquals(1, adventure.getPersoActions().size());
		Assert.assertEquals(1,  adventure.getQuests().size());
		
		// Mapscripts
		Assert.assertEquals(1, adventure.getMapScripts().size());
		MapscriptElement msElement = adventure.getMapScripts().get(0);
		Assert.assertEquals(2, msElement.getConditions().size());
		ConditionElement ce = msElement.getConditions().get(1);
		Assert.assertEquals(2,  ce.getActions().size());
		
		// Scene
		SceneElement scene = adventure.getSceneNamed("caller");
		Assert.assertEquals(3, scene.actions.size());
		
		// Perso action
		ContextualActionElement persoAction = adventure.getPersoActions().get(0);
		Assert.assertEquals(3, persoAction.actions.size());
		
		// Timer
		ActionElement act = (ActionElement) persoAction.actions.get(2);
		Assert.assertTrue(act.kind == ActionKind.timer);
		TimerElement timerAction = (TimerElement) act;
		Assert.assertEquals(1, timerAction.actions.size());
		Assert.assertEquals(1, timerAction.end.size());
		Assert.assertEquals("EQUALS(attente, 2.0)",  timerAction.endCondition.toString());
		
		// Tile action
		ContextualActionElement tileAction = adventure.getTileActionNamed("myTileAction");
		Assert.assertEquals(3,  tileAction.actions.size());
		
		// Quest (trigger + action + history)
		QuestElement quest = adventure.getQuests().get(0);
		Assert.assertEquals("vg5_back", quest.name);
		Assert.assertEquals(2,  quest.getTriggers().size());
		Assert.assertEquals(QuestEvent.LOCATION, quest.getTriggers().get(0).kind);
		Assert.assertEquals(QuestEvent.QUESTDONE, quest.getTriggers().get(1).kind);
		// We should have our 3 actions + 1 bonus because quest has 'repeat' attribute set to TRUE
		Assert.assertEquals(4,  quest.getActions().size());
		Assert.assertEquals(2, quest.getHistory().size());
		
		// For & actions
		scene = adventure.getSceneNamed("doStuff");
		List<LanguageElement> actions = scene.actions;
		Assert.assertEquals(3, actions.size());
		Assert.assertTrue(actions.get(0) instanceof ActionsElement);
		Assert.assertEquals(1, ((ActionsElement)actions.get(0)).actions.size());
		Assert.assertTrue(actions.get(1) instanceof ActionsElement);
		ActionsElement acs = (ActionsElement) actions.get(1);
		// We got 2 actions + 1 bonus added by syntaxic sugar (ForElement#addSyntaxicSugarBefore)
		Assert.assertEquals(3,  acs.actions.size());
		Assert.assertEquals(ActionKind.sound, ((ActionElement)acs.actions.get(0)).kind);
		Assert.assertEquals(ActionKind._for, ((ActionElement)acs.actions.get(2)).kind);
		ForElement forElement = (ForElement) acs.actions.get(2);
		// Here we still have a bonus action, added as incrementor for our 'for' loop
		Assert.assertEquals(2, forElement.actions.size());
		
		// Seq
		scene = adventure.getSceneNamed("testSeq");
		Assert.assertEquals(6, scene.actions.size());
	}
	
	@Test
	public void scene() {
		EngineZildo.game = new Game(false);
		ScriptManagement sm = new ScriptManagement();
		Assert.assertNotNull(sm.getAdventure().getSceneNamed("dieInPit"));
	}
	
	/** Ensures that 2 scenes with the same name are forbidden **/
	@Test(expected=RuntimeException.class)
	public void doubleSceneName() {
		ScriptReader.loadScript("junit/script/error");
	}
}
