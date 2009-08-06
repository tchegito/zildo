package zildo.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import zildo.fwk.KeyboardInstant;
import zildo.fwk.ZUtils;
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
			// Deals with network
			if (netServer != null) {
				netServer.run();
			}
			
			timeRef=ZUtils.getTime();
			delta=timeRef - time;
			if (delta > TIMER_DELAY) {
				if (isClients()) {
					// 	Do the server job
					engineZildo.renderFrame(clients.values());
				}
				
				// Reinitialize timer
				time=timeRef;
			}
			if (delta < TIMER_DELAY) {
				ZUtils.sleep(TIMER_DELAY-delta);
			}
		}
		netServer.close();
	}
	
	/**
	 * A client is coming into the game.
	 * @param client
	 * @return new Zildo's id
	 */
	public int connectClient(TransferObject client) throws ClientAlreadyInException {
		if (clients.get(client) != null) {
			throw new ClientAlreadyInException();
		}
		int zildoId=engineZildo.spawnClient();
		clients.put(client, new ClientState(client, zildoId));
		return zildoId;
	}
	
	/**
	 * Client leave the game
	 * @param client
	 */
	public void disconnectClient(TransferObject p_client) {
		clients.remove(p_client);
		if (clients.isEmpty()) {
			// No clients anymore, we shut down the server
			gameRunning=false;
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
		clients.put(p_client, state);
	}
	
	/**
	 * Send to all clients data they need to start the game:
	 * -map
	 * -sprites positions
	 * -dialogs
	 */
	public void sendStartingData() {
		//send.sendStartingData(clients);
	}
}
