package tools;

import org.junit.Assert;

import zildo.client.Client;
import zildo.client.ClientEngineZildo;
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
		client.setAction(item);
		renderFrames(2);
		Menu nextMenu = client.getCurrentMenu();
		Assert.assertNotNull(nextMenu);
		// Check that we're in a different menu
		Assert.assertNotEquals(nextMenu, currentMenu);		
	}
}
