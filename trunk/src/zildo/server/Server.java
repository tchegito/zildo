package zildo.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import zildo.client.ClientEngineZildo;
import zildo.fwk.ZUtils;
import zildo.fwk.input.KeyboardInstant;
import zildo.fwk.net.NetServer;
import zildo.fwk.net.TransferObject;
import zildo.monde.Game;
import zildo.monde.sprites.persos.PersoZildo;

/**
 * Server job:
 * -----------
 * -Animate the world : move entities, update map
 * -Proceed clients movements
 * 
 * @author tchegito
 *
 */
public class Server extends Thread {
	
	public static final long TIMER_DELAY = (long) (1000.f / 75f);
	public static final int CLIENT_TIMEOUT = 300;
	
	// This map contains informations about all clients at the current instant in the game.
	// The server is a special one, because he doesn't have any TransferObject. To get his state, we have to do :
	// clients.get(null)
    static Map<TransferObject, ClientState> clients = new HashMap<TransferObject, ClientState>();

	boolean gameRunning;
	
	// The network server engine
	NetServer netServer;
	
	EngineZildo engineZildo;
	
	public Server(Game p_game, boolean p_lan) {
		engineZildo=new EngineZildo(p_game);
		gameRunning=true;
		netServer=new NetServer(this, p_lan);
	}
	
	public EngineZildo getEngineZildo() {
		return engineZildo;
	}
	
	public void run() {
		// Timer
		long time, timeRef;
		long delta;
		time=ZUtils.getTime();
		while (gameRunning){
			networkJob();
			
			timeRef=ZUtils.getTime();
			delta=timeRef - time;
			if (delta > TIMER_DELAY) {
				if (isClients()) {
					// 	Do the server job
					engineZildo.renderFrame(getClientStates());
				}
				
				// Reinitialize timer
				time=timeRef;
			}
			if (delta < TIMER_DELAY) {
				ZUtils.sleep(TIMER_DELAY-delta);
			}
        }
        cleanUp();
    }
	
	public void networkJob() {
		// Deals with network
		if (netServer != null) {
        	
        	checkInactivity();

			netServer.run();
		}
	}
	
	/**
	 * A client is coming into the game.
	 * @param p_client
	 * @return new Zildo's id
	 */
	public int connectClient(TransferObject p_client) {
		if (clients.get(p_client) != null) {
			return clients.get(p_client).zildo.getId();
		}
		int zildoId=engineZildo.spawnClient();
		ClientState srv=new ClientState(p_client, zildoId);
		clients.put(p_client, srv);
		if (p_client == null) {
			// Connecting server : so take the name from adress
			srv.playerName=netServer.address.getHostName();
		}
		
		if (p_client != null) {
			ClientEngineZildo.guiDisplay.displayMessage(p_client.address.getHostName()+" join the game");
		}
		
		return zildoId;
	}
	
	/**
	 * Client leave the game
	 * @param p_client
	 */
	public void disconnectClient(TransferObject p_client) {
		// Delete the client's zildo
		ClientState state=clients.get(p_client);
		if (state != null) {
			EngineZildo.spriteManagement.deleteSprite(state.zildo);
			// Remove client from the list
			clients.remove(p_client);
			if (clients.isEmpty()) {
				// No clients anymore, we shut down the server
				gameRunning=false;
			}
			ClientEngineZildo.guiDisplay.displayMessage(p_client.address.getHostName()+" left the game");
		}
	}
	
	public Set<TransferObject> getClientsLocation() {
		return clients.keySet();
	}
	
	public boolean isClients() {
		return !clients.isEmpty();
	}
	
	/**
	 * Update client commands from keyboard.
	 * @param p_client
	 * @param p_instant
	 */
	public void updateClientKeyboard(TransferObject p_client, KeyboardInstant p_instant) {
        ClientState state=clients.get(p_client);
        if (state == null) {
        	throw new RuntimeException("Client isn't registered on server !");
        }
        state.keys=p_instant;
        state.inactivityTime=0;
		clients.put(p_client, state);
	}
	
    /**
     * Check client's inactivity. Disconnect the one who crosses the timeout line.
     */
    public void checkInactivity() {
        List<TransferObject> clientsDisconnected = new ArrayList<TransferObject>();
        for (ClientState state : clients.values()) {
            if (state.location != null && state.inactivityTime > CLIENT_TIMEOUT) {
                clientsDisconnected.add(state.location);
            }
            state.inactivityTime++;
        }
        for (TransferObject obj : clientsDisconnected) {
            disconnectClient(obj);
        }
    }

    static public ClientState getClientState(TransferObject p_object) {
        return clients.get(p_object);
    }

    static public ClientState getClientFromZildo(PersoZildo p_zildo) {
    	if (!EngineZildo.game.multiPlayer) {
    		return null;
    	}
        for (ClientState cl : clients.values()) {
            if (cl.zildo == p_zildo) {
                return cl;
            }
        }
        throw new RuntimeException("This zildo isn't referenced anymore !");
    }
	
	public Collection<ClientState> getClientStates() {
		return clients.values();
	}
	
    public void cleanUp() {
        netServer.close();
    }
}
