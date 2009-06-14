package zildo.network;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.Sys;

import zildo.fwk.engine.EngineZildo;
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
	
	static final long TIMER_DELAY = (long) (1000 * (1f / 60f));
	
	List<Client> clients;
	boolean gameRunning;
	Game game;
	
	EngineZildo engineZildo;
	
	public Server(Game p_game) {
		engineZildo=new EngineZildo(p_game);
		clients=new ArrayList<Client>();
		gameRunning=true;
	}
	
	public EngineZildo getEngineZildo() {
		return engineZildo;
	}
	
	public void run() {
		// Timer
		long time, timeRef;
		long delta;
		time=Sys.getTime();
		while (gameRunning){
			timeRef=Sys.getTime(); //System.currentTimeMillis();
			delta=timeRef - time;
			if (delta > TIMER_DELAY) {
				// Do the server job
				engineZildo.serverSide();
				
				// Reinitialize timer
				time=timeRef;
			}
			if (delta < TIMER_DELAY) {
				try {
					sleep(TIMER_DELAY-delta);
				} catch (InterruptedException e) {
					
				}
			}
		}
	}
	
	/**
	 * A client is coming into the game.
	 * @param client
	 */
	public void connectClient(Client client) {
		clients.add(client);
	}
	
	/**
	 * Client leave the game
	 * @param client
	 */
	public void disconnectClient(Client client) {
		clients.remove(client);
		if (clients.isEmpty()) {
			// No clients anymore, we shut down the server
			gameRunning=false;
		}
	}
}
