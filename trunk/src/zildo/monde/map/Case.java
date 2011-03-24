/**
 * Legend of Zildo
 * Copyright (C) 2006-2011 Evariste Boussaton
 * Based on original Zelda : link to the past (C) Nintendo 1992
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

package zildo.monde.map;

import zildo.fwk.file.EasyBuffering;
import zildo.fwk.file.EasySerializable;


public class Case implements EasySerializable {

	private int n_motif;
	private int n_banque;
	private int n_motif_masque;
	private int n_banque_masque;
	private int n_tile;	
	private int z;	// Result of analysis
	
	
	public Case(Case p_original) {
		this.n_motif = p_original.n_motif;
		this.n_banque = p_original.n_banque;
		this.n_motif_masque = p_original.n_motif_masque;
		this.n_banque_masque = p_original.n_banque_masque;
		this.n_tile = p_original.n_tile;
		this.z = p_original.z;
	}

	public int getN_motif() {
		return n_motif;
	}

	public void setN_motif(int n_motif) {
		this.n_motif = n_motif;
	}

	public int getN_banque() {
		return n_banque;
	}

	public void setN_banque(int n_banque) {
		this.n_banque = n_banque;
	}

	public int getN_motif_masque() {
		return n_motif_masque;
	}

	public void setN_motif_masque(int n_motif_masque) {
		this.n_motif_masque = n_motif_masque;
	}

	public int getN_banque_masque() {
		return n_banque_masque;
	}

	public void setN_banque_masque(int n_banque_masque) {
		this.n_banque_masque = n_banque_masque;
	}

	public int getN_tile() {
		return n_tile;
	}

	public void setN_tile(int n_tile) {
		this.n_tile = n_tile;
	}

	public Case() {
		z=0;
	}

	public int getAnimatedMotif(int compteur_animation)
	{
		int motif=this.n_motif;
		switch (this.n_banque)
		{
		// On gère les sprites animés
			case 0:
			// L'eau
				if (motif>=108 && motif<=130) {
					if (compteur_animation > 40)
						motif+=100;
					else if (compteur_animation > 20)
						motif+=100+23;
				} else if (motif==52 || motif==53) {
					//Les fleurs
					if (compteur_animation > 40)
						motif+=3;
					else if (compteur_animation > 20)
						motif+=111;
				}
				break;
	
	
			case 1:
				if (motif>=235 && motif<=237)
					motif+=(compteur_animation / 20)*3;
				break;
	
			case 2:
				switch (motif) 
				{
					case 174:    
						if (compteur_animation>=20)
							motif=175+(compteur_animation / 20);
						break;
					case 142:
						if (compteur_animation>=40)
							motif=178;
						else if (compteur_animation>=20)
							motif=194;
						break;
					case 144:
						if (compteur_animation>=40)
							motif=179;
						else if (compteur_animation>=20)
							motif=195;
						break;
				    case 235:
						motif=235+(compteur_animation / 20);
				}
				break;
			case 3:
			// L'eau dans les grottes/palais
				if (motif==78)
					motif=78+(compteur_animation / 20);
				break;
	
			case 5:
				// FORET3.DEC animation d'eau supplémentaire
				if (motif>=96 && motif<=104)
					motif+=(compteur_animation / 20)*3;
				break;
		}
	
		// Return computed motif
		return motif;
	}
	


	/**
	 * Serialize useful fields from this map case.
	 * @param p_buffer
	 */
	public void serialize(EasyBuffering p_buffer) {
		p_buffer.put(this.getN_banque());
		p_buffer.put(this.getN_banque_masque());
		p_buffer.put(this.getN_motif());
		p_buffer.put(this.getN_motif_masque());
		p_buffer.put(this.getN_tile());
	}
	
	/**
	 * Deserialize a byte buffer into a Case
	 * @param p_buffer
	 * @return SpriteEntity
	 */
	public static Case deserialize(EasyBuffering p_buffer) {
		Case mapCase=new Case();
		mapCase.setN_banque(p_buffer.readInt());
		mapCase.setN_banque_masque(p_buffer.readInt());
		mapCase.setN_motif(p_buffer.readInt());
		mapCase.setN_motif_masque(p_buffer.readInt());
		mapCase.setN_tile(p_buffer.readInt());
		
		return mapCase;
	}

	public void setMasked(boolean p_mask) {
		if (p_mask) {
			setN_banque(getN_banque() | 128);
		} else {
			setN_banque(getN_banque() & 127);
		}
	}
	
	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}
}