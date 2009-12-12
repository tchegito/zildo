package zildo.fwk.net;

import zildo.client.Client;
import zildo.fwk.net.packet.ConnectPacket;

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
		
		ConnectPacket connectPacket=new ConnectPacket(true, playerName);
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
