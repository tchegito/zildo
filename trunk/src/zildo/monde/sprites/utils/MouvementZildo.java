package zildo.monde.sprites.utils;

public enum MouvementZildo {
	
	// Mouvements de Zildo
	MOUVEMENT_VIDE,
	MOUVEMENT_SOULEVE,
	MOUVEMENT_BRAS_LEVES,
	MOUVEMENT_TIRE,			// Pas à l'arc : pousser/tirer
	MOUVEMENT_POUSSE,
	MOUVEMENT_ATTAQUE_EPEE,
	MOUVEMENT_ATTAQUE_ARC,
    MOUVEMENT_ATTAQUE_BOOMERANG,
    MOUVEMENT_TOUCHE,   	// Quand Zildo se fait toucher
	MOUVEMENT_SAUTE,   		// Zildo saute une colline ! -> inactif pendant le saut
	MOUVEMENT_FIERTEOBJET;	// Zildo brandit fièrement un objet

}
