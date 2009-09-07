package zildo.client.gui.menu;

import java.util.List;

import zildo.MultiPlayer;
import zildo.fwk.net.ServerInfo;

public class JoinGameMenu extends Menu {

	public JoinGameMenu(List<ServerInfo> p_servers) {
		super("Choose a server");
		ItemMenu[] items=new ItemMenu[p_servers.size()];
		int i=0;
		for (final ServerInfo srv : p_servers) {
			items[i++]=new ItemMenu(srv.toString()) {
				public void run() {
					new MultiPlayer(srv);
				}
			};
		}

		setMenu(items);
	}
}
