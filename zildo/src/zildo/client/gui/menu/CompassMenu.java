package zildo.client.gui.menu;

import java.util.ArrayList;

import zildo.fwk.ui.ItemMenu;
import zildo.fwk.ui.Menu;
import zildo.server.EngineZildo;

public class CompassMenu extends Menu {

	public CompassMenu() {
		items = new ArrayList<ItemMenu>();
		items.add(new ItemMenu("m13.map") {
			@Override
			public void run() {
			}
		});
		items.add(new ItemMenu("m13.dial") {
			
			@Override
			public void run() {
				System.out.println(EngineZildo.game.getLastDialog());
			}
		});
		items.add(new ItemMenu("m13.guide") {
			@Override
			public void run() {
			}
		});
		setMenu(items);
	}
}
