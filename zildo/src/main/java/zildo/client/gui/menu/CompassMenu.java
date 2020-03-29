package zildo.client.gui.menu;

import java.util.ArrayList;

import zildo.client.stage.TexterStage;
import zildo.client.stage.TexterStage.TexterKind;
import zildo.fwk.ui.InfoMenu;
import zildo.fwk.ui.ItemMenu;
import zildo.fwk.ui.Menu;

public class CompassMenu extends Menu {

	public CompassMenu() {
		items = new ArrayList<ItemMenu>();
		items.add(new ItemMenu("m13.quests") {
			@Override
			public void run() {
				client.askStage(new TexterStage(TexterKind.QUEST_LOG));
			}
		});
		items.add(new ItemMenu("m13.map") {
			@Override
			public void run() {
				client.handleMenu(new InfoMenu("m13.map.unavailable", null));
			}
		});
		items.add(new ItemMenu("m13.dial") {
			@Override
			public void run() {
				// Game should be blocked, until texter stage is over
				client.askStage(new TexterStage(TexterKind.LAST_DIALOGS));
			}
		});
		items.add(new ItemMenu("m13.guide") {
			@Override
			public void run() {
				// Game should be blocked, until texter stage is over
				client.askStage(new TexterStage(TexterKind.GUIDE));
			}
		});
		setMenu(items);
	}
}
