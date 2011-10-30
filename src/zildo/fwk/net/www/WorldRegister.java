/**
 * Legend of Zildo
 * Copyright (C) 2006-2011 Evariste Boussaton
 * Based on original Zelda : link to the past (C) Nintendo 1992
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

package zildo.fwk.net.www;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import zildo.fwk.ZUtils;
import zildo.fwk.net.ServerInfo;
import zildo.fwk.net.www.NetMessage.Command;

/**
 * @author Tchegito
 * 
 */
public class WorldRegister extends Thread {

	private final static String url = "http://legendofzildo.appspot.com";
	private final static String displayServlet = "display";
	private final static String serverServlet = "srv";
	private final static String charset = "UTF-8";

	private Deque<NetMessage> messages = new ArrayDeque<NetMessage>();

	public void askMessage(NetMessage p_message, boolean p_asynchronous) {
		if (p_asynchronous) {
			messages.add(p_message);
		} else {
			treatMessage(p_message);
		}
	}

	/**
	 * Return registered internet servers based on informations on the appspot
	 * site.
	 * 
	 * @return List<ServerInfo>
	 */
	public static List<ServerInfo> getStartedServers() {
		List<ServerInfo> infos = new ArrayList<ServerInfo>();
		try {
			StringBuilder request = new StringBuilder();
			request.append(url).append("/").append(displayServlet);
			request.append("?ingame=1");
			URL objUrl = new URL(request.toString());
			URLConnection urlConnect = objUrl.openConnection();

			InputStream in = urlConnect.getInputStream();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			while (reader.ready()) {
				// 4 line per server
				String name = reader.readLine();
				String ip = reader.readLine();
				int port = Integer.valueOf(reader.readLine());
				ServerInfo server = new ServerInfo(name, ip, port);
				server.nbPlayers = Integer.valueOf(reader.readLine());
				infos.add(server);
			}
			in.close();

			return infos;
		} catch (Exception e) {
			return infos;
		}
	}

	@Override
	public void run() {
		while (true) {
			if (!messages.isEmpty()) {
				// Retrieve the first message and treat it
				NetMessage mess = messages.removeFirst();
				treatMessage(mess);
			}
			ZUtils.sleep(500);
		}
	}

	private void treatMessage(NetMessage p_message) {
		sendRequest(p_message.command, p_message.server);
	}

	private boolean sendRequest(Command p_command, ServerInfo p_serverInfo) {
		try {
			StringBuilder request = new StringBuilder();
			request.append(url).append("/").append(serverServlet);
			request.append("?command=").append(p_command.toString());
			request.append("&name=").append(
					URLEncoder.encode(p_serverInfo.name, charset));
			request.append("&port=").append(p_serverInfo.port);
			if (p_serverInfo.ip != null) {
				request.append("&ip=").append(p_serverInfo.ip);
			}
			request.append("&nbPlayers=").append(p_serverInfo.nbPlayers);
			URL objUrl = new URL(request.toString());
			URLConnection urlConnect = objUrl.openConnection();

			// Add server infos
			InputStream in = urlConnect.getInputStream();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			int result = reader.read();
			in.close();

			return result == 48; // ASCII code of '0'
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
