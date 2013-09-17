/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
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
	PRINCESS_BUNNY(203, 204),
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
	FALCOR(263, 264, 261, 262, 259, 260, 265), 
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
		if (f > 258) {
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
		} else {
			return SpriteBank.BANK_PNJ3;
		}
	}

	public int getRadius() {
		switch (this) {
		case GARDE_BOUCLIER:
		case BUCHERON_ASSIS:
			return 8;
		case POULE:
		case CRABE:
			return 5;
		default:
			return 6;
		}
	}

	@Override
	public boolean isBlocking() {
		return false;
	}

	@Override
	public boolean isDamageable() {
		return false;
	}
	
	@Override
	public boolean isPushable() {
		return false;
	}


}