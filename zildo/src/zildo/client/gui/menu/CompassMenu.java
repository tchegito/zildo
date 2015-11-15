package zildo.client.gui.menu;

import java.util.ArrayList;

import zildo.client.stage.TexterStage;
import zildo.client.stage.TexterStage.TexterKind;
import zildo.fwk.ui.ItemMenu;
import zildo.fwk.ui.Menu;

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
				// Game should be blocked, until texter stage is over
				client.askStage(new TexterStage(TexterKind.LAST_DIALOGS));
				client.handleMenu(null);
			}
		});
		items.add(new ItemMenu("m13.guide") {
			@Override
			public void run() {
				// Game should be blocked, until texter stage is over
				client.askStage(new TexterStage(TexterKind.GUIDE));
				client.handleMenu(null);
			}
		});
		setMenu(items);
	}
}
