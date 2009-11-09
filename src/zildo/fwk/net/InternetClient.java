package zildo.fwk.net;

import java.util.ArrayList;
import java.util.List;

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
	 * Returns a list of every responding servers.
	 * @return List
	 */
	public static List<ServerInfo> scanExistingServers() {
		List<ServerInfo> servers=new ArrayList<ServerInfo>();
		for (ServerInfo srv : ServerInfo.values()) {
			if (srv.port !=0) {
				TransferObject obj=new TransferObject(srv.ip, srv.port);
				if (!obj.address.getAddress().isSiteLocalAddress()) {
					servers.add(srv);
				}
			}
		}
		return servers;
	}
}
