package tools;

import org.junit.Assert;

import zildo.client.Client;
import zildo.client.ClientEngineZildo;
import zildo.client.stage.GameStage;
import zildo.client.stage.MenuStage;
import zildo.fwk.ui.ItemMenu;
import zildo.fwk.ui.Menu;

public class EngineWithMenuUT extends EngineUT {

	protected void pickItem(String id) {
		Client client = ClientEngineZildo.getClientForMenu();
		Menu currentMenu = client.getCurrentMenu();
		ItemMenu item = currentMenu.getItemNamed(id);
		if (item == null) {
			throw new RuntimeException("Item "+id+" doesn't exist in "+currentMenu+" !");
		}
		for (GameStage stage : client.getCurrentStages()) {
			if (stage instanceof MenuStage) {
				((MenuStage) stage).askForItemMenu(item);
			}
		}
		//currentMenu.selectItem(item);
		//client.setAction(item);
		renderFrames(2);
	}
	
	protected void pickItemAndCheckDifferentMenu(String id) {
		Client client = ClientEngineZildo.getClientForMenu();
		Menu currentMenu = client.getCurrentMenu();
		pickItem(id);
		
		// Item has been activated, we should be in another menu
		Menu nextMenu = client.getCurrentMenu();
		Assert.assertNotNull(nextMenu);
		// Check that we're in a different menu
		Assert.assertNotEquals(nextMenu, currentMenu);		
	}
}
