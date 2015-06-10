/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
 * 
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

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.DatagramChannel;

import zildo.Zildo;

public class TransferObject {

	public DatagramChannel channel;
	public DatagramSocket socket;
	public InetSocketAddress address;
	
	private int hash=-1;
	
	public TransferObject(String p_ip, int p_port) {
		InetAddress ia;
		try {
			ia=InetAddress.getByName(p_ip);
			address = new InetSocketAddress(ia, p_port);
			initialize();
		} catch (Exception e) {
			throw new RuntimeException("Unable to reach "+p_ip);
		}
	}
	/**
	 * Create mandatory objects to exchange data between client and server. 
	 * @param p_address
	 * @param p_port
	 */
	public TransferObject(InetAddress p_address, int p_port) {
		if (p_address == null) {
			try {
				// Consider that we are localhost
				p_address=InetAddress.getLocalHost();
			} catch (UnknownHostException e) {
				throw new RuntimeException("Unable to get localhost address");
			}
		}

		address = new InetSocketAddress(p_address, p_port);
		initialize();
	}

	/**
	 * Create a broadcastable address and every objects related.
	 * @param p_port
	 */
	public TransferObject(int p_port) {
		try {
			address = new InetSocketAddress(p_port);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		initialize();
	}
	
    public static TransferObject createBroadCastObject(int p_port) {
        byte[] b = { 0, 0, 0, 0 };
        try {
            b = InetAddress.getLocalHost().getAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String adr = "";
        for (int i = 0; i < 3; i++) {
            adr += ((short) 0xff & b[i]) + ".";
        }
        adr += "255";
        return new TransferObject(adr, p_port);
    }

    public void initialize() {
		try {
			log("Initializing: "+address);
			TransferObject existingOnSameAddress=NetSend.getTransferObject(address, false);
			if (existingOnSameAddress != null) {
				log("Reuse previous");
				channel=existingOnSameAddress.channel;
				socket=existingOnSameAddress.socket;
			} else {
				channel=DatagramChannel.open();
				channel.configureBlocking(false);
				socket=channel.socket();
				socket.bind(address);
				socket.setBroadcast(true);
				NetSend.addTransferObject(this);
			}
		} catch (IOException e) {
			// Exception, but connection works anyway.
		}
	}
	
	public boolean isOpen() {
		return channel != null && channel.isOpen();
	}

	public void close() {
		log("Shutting down connection: "+address);
		try {
			// Shut down the connection
			if (socket != null) {
				socket.close();
			}
			channel.close();
		} catch (IOException e) {
			throw new RuntimeException("Unable to close socket/channel.");
		}
	}
	
	protected void log(String s) {
		if (Zildo.logNetwork) {
			System.out.println(this.getClass().getSimpleName()+":"+s);
		}
	}
	
	/**
	 * hashCode for TransferObject : only the adress is significant for check.
	 */
    /**
     * hashCode for TransferObject : only the adress is significant for check.
     */
    @Override
	public int hashCode() {
        if (hash == -1) {
            byte[] b = this.address.getAddress().getAddress();
            hash = 7;
            for (int i = 0; i < 4; i++) {
                hash += 31 * hash + b[i];
            }
        }
        return hash;
    }
	
	@Override
	public boolean equals(Object p_other) {
		if (!p_other.getClass().isAssignableFrom(TransferObject.class)) {
			return false;
		}
		return this.hashCode() == p_other.hashCode();
	}
}