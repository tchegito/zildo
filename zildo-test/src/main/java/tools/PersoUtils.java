package tools;

import zildo.monde.sprites.persos.Perso;
import zildo.server.EngineZildo;

public class PersoUtils {

	public Perso persoByName(String name) {
		return EngineZildo.persoManagement.getNamedPerso(name);
	}
	
	public void removePerso(String name) {
		EngineZildo.persoManagement.removePerso(persoByName(name));
	}
}
