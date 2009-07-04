package zildo.network;

import java.util.List;

import zildo.fwk.engine.EngineZildo;
import zildo.monde.Area;

public class Send extends NetSend {

	final private EngineZildo engineZildo;
	
	public Send(EngineZildo p_engineZildo) {
		engineZildo=p_engineZildo;
	}
	
	public void sendStartingData(final List<Client> clients) {
		Area currentMap=engineZildo.mapManagement.getCurrentMap();
		for (Client c : clients) {
			sendSerializedData(currentMap, c.getAddress());
		}
	}

}
