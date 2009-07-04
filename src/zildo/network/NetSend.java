package zildo.network;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class NetSend {

	private final static int NET_PORT = 80;
	
	public void sendSerializedData(Object p_data, InetAddress p_address) {
		try {
			Socket s = new Socket(p_address, NET_PORT);

			ObjectOutputStream out = null;
			out=new ObjectOutputStream(s.getOutputStream());
			out.writeObject(p_data);
			out.flush();
			out.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
