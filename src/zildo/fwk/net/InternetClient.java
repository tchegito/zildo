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
		
		ConnectPacket connectPacket=new ConnectPacket(true);
		sendPacket(connectPacket, server);
		serverFound=true;
	}
}
