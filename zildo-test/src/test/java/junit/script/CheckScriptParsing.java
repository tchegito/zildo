package junit.script;

import org.junit.Assert;
import org.junit.Test;

import zildo.fwk.script.xml.ScriptReader;
import zildo.fwk.script.xml.element.AdventureElement;
import zildo.fwk.script.xml.element.ConditionElement;
import zildo.fwk.script.xml.element.ContextualActionElement;
import zildo.fwk.script.xml.element.MapscriptElement;
import zildo.fwk.script.xml.element.QuestElement;
import zildo.fwk.script.xml.element.SceneElement;
import zildo.monde.Game;
import zildo.monde.quest.QuestEvent;
import zildo.server.EngineZildo;

public class CheckScriptParsing {

	@Test
	public void read() {
		EngineZildo.game = new Game(false);
		AdventureElement adventure = (AdventureElement) ScriptReader.loadScript("junit/script/parsing");
		
		Assert.assertEquals(2, adventure.getScenes().size());
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
		Assert.assertEquals(2, persoAction.actions.size());
		
		// Tile action
		ContextualActionElement tileAction = adventure.getTileActionNamed("myTileAction");
		Assert.assertEquals(3,  tileAction.actions.size());
		
		// Quest
		QuestElement quest = adventure.getQuests().get(0);
		Assert.assertEquals("vg5_back", quest.name);
		Assert.assertEquals(2,  quest.getTriggers().size());
		Assert.assertEquals(QuestEvent.LOCATION, quest.getTriggers().get(0).kind);
		Assert.assertEquals(QuestEvent.QUESTDONE, quest.getTriggers().get(1).kind);
		Assert.assertEquals(3,  quest.getActions().size());
	}
}
