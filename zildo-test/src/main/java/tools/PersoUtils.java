package tools;

import zildo.monde.sprites.persos.Perso;
import zildo.server.EngineZildo;

public class PersoUtils {

	public Perso persoByName(String name) {
		return EngineZildo.persoManagement.getNamedPerso(name);
	}
	
	public void removePerso(String name) {
		Perso perso = persoByName(name);
		EngineZildo.spriteManagement.deleteSprite(persoByName(name));
		EngineZildo.persoManagement.removePerso(perso);
	}
}
