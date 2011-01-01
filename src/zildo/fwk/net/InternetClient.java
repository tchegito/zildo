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

package zildo.fwk.net;

import zildo.client.Client;
import zildo.fwk.net.packet.ConnectPacket;
import zildo.prefs.Constantes;

/**
 * Client on a the WWW network.
 * 
 * @author tchegito
 *
 */
public class InternetClient extends NetClient {

	public InternetClient(Client p_client, String p_serverIp, int p_serverPort) {
		super(p_client);
		
		server=new TransferObject(p_serverIp, p_serverPort);
		
		ConnectPacket connectPacket=new ConnectPacket(true, playerName, Constantes.CURRENT_VERSION);
		sendPacket(connectPacket, server);
		serverFound=true;
	}
	
	/**
	 * Returns TRUE if given server is a valid one.
	 * @return boolean
	 */
	public static boolean isResponding(ServerInfo p_serverInfo) {
			if (p_serverInfo.port !=0) {
				TransferObject obj=new TransferObject(p_serverInfo.ip, p_serverInfo.port);
				if (!obj.address.getAddress().isSiteLocalAddress()) {
					return true;
				}
			}
		return false;
	}
}
