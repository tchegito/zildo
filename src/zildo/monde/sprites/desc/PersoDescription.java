package zildo.monde.sprites.desc;

import zildo.fwk.IntSet;

public enum PersoDescription {

	// PNJ.SPR
	PRINCESSE(0,7),
	BAS_GARDEVERT(8,9,10,11,12,13,14,15),
	HAUT_GARDEVERT(16,17,18,19),
	GARDE_CANARD(20,21,22,23,24,25,26,27,28,29,30,31,32,33,34),
	POULE(35,36,37,38,76,77,78,79),	// 4 à gauche et 4 à droite
	ENFANT(39,40,41),
	VOYANT(42,43),
	DRESSEUR_SERPENT(44,45),
	VIEUX(46,47,48),
	SOFIASKY(49,50,51,52,53,54,55,56),
	RITASKY(57,58,59,60,61,62,63,64),
	VIEILLE_BALAI(65,66),
	SORCIERE(67,68),
	CURE(69,70,71,72),
	VOLEUR(73,74,75),
	SORCIER_CAGOULE(80,81),
	ALCOOLIQUE(82,83),
	PANNEAU(84),
	GARDE_BOUCLIER(85,86,87,88),
	SPECTRE(89,90),
	CORBEAU(91,92,93,94,95,96),
	CRABE(97,98,99,100,101,102,103,104,105,106,107,108,109,110,111),	// Les 3 derniers=crabe nu
	ABEILLE(112,113,114,115),
	ARME_EPEE(116,117,118,119),
	ARME_LANCE(120,121,122,123),

	// PNJ2.SPR
	MOUSTACHU(128),
	BUCHERON_ASSIS(129),
	VIEUX_SAGE(130),
	BANDIT(131,132,133),
	BANDIT_CHAPEAU(134),
	BANDIT_VERT(135,136,137,138,139,140,141,142),
	MOUSTACHU_ASSIS(143),
	VAUTOUR(144,145,146),
	ECTOPLASME(147,148), // (?),),
	VIEUX_SAGE2(149,150),
	ARC(151,152,153,154,155,156,157,158),
	ELECTRIQUE(159,160,161),
	SQUELETTE(162,163,164,165,166,167,168,169),
	CREATURE(170,171,172,173,174,175,176,177),
	LAPIN(178,179);
	
	IntSet sprUsed;

	//final static int add=128;

	private PersoDescription(int... sprUsed) {
		this.sprUsed=new IntSet(sprUsed);
	}
	
	/**
	 * Renvoie l'identité du personnage suivant un numéro de sprite
	 * @param nSpr
	 * @return PersoDescription
	 */
	public static PersoDescription fromNSpr(int nSpr) {
		for (PersoDescription desc : PersoDescription.values()) {
			if (desc.sprUsed.contains(nSpr)) {
				return desc;
			}
		}
		return null;
	}
	
	public int first() {
		return sprUsed.first().intValue();
	}
}