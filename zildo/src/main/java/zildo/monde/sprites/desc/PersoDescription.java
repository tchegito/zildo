/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
 * 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package zildo.monde.sprites.desc;

import zildo.fwk.bank.SpriteBank;
import zildo.fwk.collection.IntSet;
import zildo.monde.collision.DamageType;

public enum PersoDescription implements SpriteDescription {

	// PNJ.SPR
	PRINCESSE(0, 1, 2, 3, 4, 5), // , 6, 7),
	BAS_GARDEVERT(8, 9, 10, 11, 12, 13, 14, 15),
	HAUT_GARDEVERT(16, 17, 18, 19),
	GARDE_CANARD(20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34),
	POULE(35, 36), //, 37, 38, 76, 77, 78, 79), // 4 à gauche et 4 à droite
	CANARD(37, 38),
	ENFANT(39, 40, 41),
	VOYANT(42, 43),
	DRESSEUR_SERPENT(44, 45),
	VIEUX(46, 47, 48),
	SOFIASKY(49, 50, 51, 52, 53, 54),
	GARCON_BRUN(55, 56),
	RITASKY(57, 58, 59, 60, 61, 62, 63, 64),
	VIEILLE_BALAI(65, 66),
	SORCIERE(67, 68),
	CURE(69, 70, 71, 72),
	VOLEUR(73, 74, 75),
	SORCIER_CAGOULE(80, 81),
	ALCOOLIQUE(82, 83),
	PANNEAU(84),
	GARDE_BOUCLIER(85, 86, 87, 88),
	SPECTRE(89, 90),
	CORBEAU(91, 92, 93, 94, 95, 96),
	CRABE(97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111), // Les 3 derniers=crabe nu
	ABEILLE(112, 113, 114, 115),
	ARME_EPEE(116, 117, 118, 119),
	ARME_LANCE(120, 121, 122, 123),

	// PNJ2.SPR
	MOUSTACHU(128, 194, 195, 196),
	BUCHERON_ASSIS(129),
	VIEUX_SAGE(130),
	BANDIT(131, 132, 133),
	BANDIT_CHAPEAU(225, 226, 227, 228, 134, 229),
	BANDIT_VERT(135, 136, 137, 138, 139, 140, 141, 142),
	MOUSTACHU_ASSIS(143),
	VAUTOUR(144, 145, 146),
	ECTOPLASME(147, 148), // (?),),
	VIEUX_SAGE2(149, 150),
	ARC(151, 152, 153, 154, 155, 156, 157, 158),
	GREEN_BLOB(159, 160, 161, 197, 198, 199, 200),
	SQUELETTE(162, 163, 164, 165, 166, 167, 168, 169),
	CREATURE(170, 171, 172, 173, 174, 175, 176, 177),
	LAPIN(178, 179),
	OISEAU_VERT(180, 181),
	VOLANT_BLEU(182, 183, 184, 185, 186, 187),
	PRINCESSE_COUCHEE(188, 189, 190, 191),
	ARBUSTE_VIVANT(192, 193),
	CHAUVESOURIS(201, 202),
	PRINCESS_BUNNY(203, 204, 
			313, 314, 315, 316, 
			317, 318, 319, 320, 
			321, 322, 323, 324,
			// Dying +44
			391, 392, 393, 394, 395, 396
			),
	GARCON_BLEU(205, 206),
	GARCON_JAUNE(207, 208),
	FERMIERE(209, 210, 211, 212, 213, 214),
	BUCHERON_DEBOUT(124),
	HECTOR(215, 216, 217, 218),
	RABBIT(219, 220, 221, 222, 223),
	KING(224),
	SINGER(230),
	FIRETHING(232, 233, 234, 235),
	RAT(242, 243, 238, 239, 236, 237, 240, 241),
	FOX(248, 249, 246, 247, 244, 245, 256, 257, 258),
	SORCERER(250, 251),
	BROWN_CAT(76, 77, 6),
	GREY_CAT(78, 79, 7),
	VIEUX_SAGE3(252),
	STONE_SPIDER(253, 254),
	PAPER_NOTE(255),
	// Pnj3
	FALCOR(263, 264, 261, 262, 259, 260, 265), 
	BRAMBLE(266),
	FLYINGSERPENT(267, 268, 269),
	FISHER(270, 271, 272, 273),
	FISH(274, 275, 276, 277, 293),
	IGOR(284, 285, 286, 281, 282, 283, 278, 279, 280),
	LOUISE(291, 292, 289, 290, 287, 288),
	MINSK(295, 294, 296, 297),
	INVENTOR(298, 299, 300, 301),
	COOK(302, 303, 304, 305),
	BIG_RAT(310, 311, 308, 309, 306, 307, 312),
	DRAGON(325, 326, 327, 328, 329, 330, 331, 332, 333, 334, 335),
	BITEY( 405, 406, 407, 408, 409, 410, 411,	// Idle near
			412, 413, 414, 415,	// Idle far
			// Attacking
			416, 417, 418, 419, 420, 421, 422, 423, 424, 425, 426, 427, 428, 429, 430, 431, 432, 433
			),
	TURTLE(343, 344, 345, 346, 
			347, 348, 349, 350, 
			351, 352, 353,
			354, 355, 356, 357,	358, 359, 360, 
			361, 362, 363, 364, 365, 366, 367),
	FIREFLY(255),
	// Pnj4
	TURRET(368, 369, 370, 371, 372, 373),
	TURRET_HEART(374, 375),		/* turret vulnerable part */
	SLEEPING_KING(376, 377, 378),
	ELEORIC(379, 382, 380, 381),
	VIEILLE_BANC(383, 384),
	VACTO(389, 390, 387, 388, 385, 386),
	FIRE_ELEMENTAL(397, 398, 399, 400),
	COAL(401, 402, 403),
	COAL_COLD(404),     
	CACTUS(434),
	SCORPION(435, 436, 437, 438, 439, 440),
	MOLE(441, 442, 443, 444, 445, 446, 447),
	DARKGUY(448, 456, 457, 458, 459, 460, 461, 462, 463, 464, 465 /* lighting dynamite*/, 466, 467, 468),
	BUTCHER(450, 449, 451, 452, 453, 454, 455),
	
	// Shift numbers once darkguy will have all its sprites
	HOODED(/*Idle face*/ 469, 470, 471, 472, /*Walking*/ 473, 474, 475, 476,
			/* Attacking */509, 510, 511, 512, 513, 514, 
			/* Bouncing */ 491, 492, 493, 494, 495, 496, 497, 498),
	POULPA(516, 517, 518, 519, 524, 525, 526, 527),
	ICE_ELEMENTAL(520, 521, 522, 523),
	
	// 22
	ZILDO(ZildoDescription.DOWN_FIXED.ordinal());

	IntSet sprUsed;

	private PersoDescription(int... sprUsed) {
		this.sprUsed = new IntSet(sprUsed);
	}

	/**
	 * Return character's identity from given integer value.
	 * 
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
		return nth(0);
	}

	/**
	 * Returns the n-th element
	 * 
	 * @param p_nth
	 * @return int
	 */
	public int nth(int p_nth) {
		int f = sprUsed.get(p_nth).intValue();
		if (f > 463+2+3) {
			f-= 464+2+3;
		} else if (f > 367) {
			f-=368;
		} else if (f > 258) {
			f-=259;
		} else if (f > 127) {
			f-=128;
		}
		return f;
	}

	public int getNSpr() {
		return first();
	}

	public int getBank() {
		if (this == ZILDO) {
			return SpriteBank.BANK_ZILDO;
		}
		int s = sprUsed.get(0);
		if (s < 128) {
			return SpriteBank.BANK_PNJ;
		} else if (s < 259) {
			return SpriteBank.BANK_PNJ2;
		} else if (s < 368) {
			return SpriteBank.BANK_PNJ3;
		} else if (s < 469) {
			return SpriteBank.BANK_PNJ4;
		} else {
			return SpriteBank.BANK_PNJ5;
		}
	}

	public int getRadius() {
		switch (this) {
		case GARDE_BOUCLIER:
		case BUCHERON_ASSIS:
		case BRAMBLE:
			return 8;
		case POULE:
		case CRABE:
			return 5;
		case COAL:
			return 10;
		case COAL_COLD:
			return 0;
		default:
			return 6;
		}
	}

	/**
	 * Returns TRUE if this character resists to a given type of damage.
	 */
	public boolean resistToDamageType(DamageType dmgType) {
		switch (this) {
			case CACTUS:
				return true;
			case SCORPION:
				return dmgType == DamageType.SLOWNESS;
			case BRAMBLE:
				switch (dmgType) {
					case SMASH:
					case PIERCING:
					case BLUNT:
					case PEEBLE:
						return true;
					default:
						return false;
				}
			case FIRE_ELEMENTAL:
				return true;
			case COAL:
				return dmgType != DamageType.FIRE && dmgType != DamageType.EXPLOSION;
			case DRAGON:
				return dmgType != DamageType.BIG_BLUNT;	// Only a big rock can hurt dragon
		}
		return false;
	}
	
	@Override
	public boolean isBlocking() {
		return false;
	}

	@Override
	public boolean isDamageable() {
		return this == ARME_EPEE || this == ARME_LANCE;
	}
	
	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	public boolean isNotFixe() {
		return false;
	}

	@Override
	public boolean isSliping() {
		return true;
	}
	
	public boolean isTakable() {
		return this == CANARD || this == POULE || this == FISH;
	}
	
	/** Returns TRUE if character is projected when wound **/
	public boolean isProjectable() {
		switch (this) {
		case TURRET:
		case TURRET_HEART:
		case BRAMBLE:
		case DRAGON:
		case BITEY:
		case MOLE:
			return false;
			default:
				return true;
		}
	}
	
	public boolean is8Angles() {
		return this == SCORPION;
	}
	
	@Override
	public boolean isOnGround() {
		return false;
	}
	
	// Height of the character, to allow jump
	public int getSizeZ() {
		switch (this) {
		case BRAMBLE:
			return 12;
		default:
			return 16;	// Too high to get above it
		}
	}
	
	@Override
	public boolean doesImpact() {
		return false;
	}
}