package zildo.monde.sprites.utils;

import java.util.Arrays;
import java.util.List;

public enum MouvementPerso {

	// Mouvement des persos
	SCRIPT_ZONE(0),
	SCRIPT_POULE(1),
	SCRIPT_OBSERVE(2),
	SCRIPT_IMMOBILE(3),
	SCRIPT_VOLESPECTRE(4),
	SCRIPT_RAT(5),
	SCRIPT_ZONELANCE(6),
	SCRIPT_ZONEARC(7),
	SCRIPT_ELECTRIQUE(8),
	SCRIPT_ABEILLE(9);
	
	public int valeur;
	
	public static List<MouvementPerso> persoDiagonales=Arrays.asList(SCRIPT_POULE,SCRIPT_VOLESPECTRE,SCRIPT_ELECTRIQUE);
	
	private MouvementPerso(int val) {
		this.valeur=val;
	}
	
	public static MouvementPerso fromInt(int a) {
		for (MouvementPerso mvt : MouvementPerso.values()) {
			if (mvt.valeur == a) {
				return mvt;
			}
		}
		throw new RuntimeException("Le script de mouvement "+a+" n'existe pas.");
	}
}
