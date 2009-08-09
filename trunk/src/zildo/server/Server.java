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
	
    Map<TransferObject, ClientState> clients = new HashMap<TransferObject, ClientState>();

	boolean gameRunning;
	Game game;
	
	// The network server engine
	NetServer netServer;
	
	EngineZildo engineZildo;
	
	public Server(Game p_game) {
		engineZildo=new EngineZildo(p_game);
		gameRunning=true;
		netServer=new NetServer(this);
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
		clients.put(p_client, new ClientState(p_client, zildoId));
		
		ClientEngineZildo.guiDisplay.displayMessage(p_client.address.getHostName()+" join the game");

		return zildoId;
	}
	
	/**
	 * Client leave the game
	 * @param p_client
	 */
	public void disconnectClient(TransferObject p_client) {
		// Delete the client's zildo
		ClientState state=clients.get(p_client);
		EngineZildo.spriteManagement.deleteSprite(state.zildo);
		// Remove client from the list
		clients.remove(p_client);
		if (clients.isEmpty()) {
			// No clients anymore, we shut down the server
			gameRunning=false;
		}
		ClientEngineZildo.guiDisplay.displayMessage(p_client.address.getHostName()+" left the game");
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
	
	public void checkInactivity() {
		List<TransferObject> clientsDisconnected=new ArrayList<TransferObject>();
		for (ClientState state : clients.values()) {
			if (state.inactivityTime > CLIENT_TIMEOUT) {
				clientsDisconnected.add(state.location);
			}
			state.inactivityTime++;
		}
		for (TransferObject obj : clientsDisconnected) {
			disconnectClient(obj);
		}

	}
	public Collection<ClientState> getClientStates() {
		return clients.values();
	}
	
    public void cleanUp() {
        netServer.close();
    }
}
