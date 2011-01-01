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

package zildo.fwk.bank;

import java.util.logging.Level;
import java.util.logging.Logger;

import zildo.fwk.file.EasyReadingFile;


public class MotifBank {

	protected final Logger logger=Logger.getLogger("MotifBank");
	
	private short[] motifs_map; // Pointeur sur nos graphs
	private long motifs_size;  // Taille de la banque
	private String nom;				// Max length should be 12
	private boolean charge;       // TRUE=en mémoire FALSE=en attente
	private int nb_motifs;		// Nombre de motifs dans la banque



	
	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public int getNb_motifs() {
		return nb_motifs;
	}

	public void setNb_motifs(int nb_motifs) {
		this.nb_motifs = nb_motifs;
	}

	public MotifBank() {
		charge=false;
	
		logger.log(Level.INFO, "Creating MotifBank");
	
	}
	
	//Assignment operator to work out with STL
	public MotifBank(MotifBank original) {
		this.charge=original.charge;
		this.motifs_map=original.motifs_map;
		this.motifs_size=original.motifs_size;
		this.nb_motifs=original.nb_motifs;
		this.nom=original.nom;
	}
	
	public void charge_motifs(String filename)
	{
		// On récupère la taille du fichier .DEC
		EasyReadingFile file=new EasyReadingFile(filename);
		int size=file.getSize();
		if (!charge) {
			//motifs_map=new short[size];
		}
		
		nom=filename;
		motifs_size=size;
		nb_motifs=(int) (motifs_size / (16*16));
	
		// Load the mini-pictures
		motifs_map=file.readUnsignedBytes();
			
		charge=true;

	}
	
	public short[] get_motif(int quelmotif)
	{
		/*
		// Retrieve motif from bank buffer
		byte* temp;
		temp=(byte*)malloc(sizeof(byte) * 16*16);
		temp=new byte[16*16];
		for (int i=0;i<16*16;i++)
			*(temp+i) = *(motifs_map + a + i);
	
		// Return extracted one
		return temp;
	*/
		short[] coupe=new short[16*16];
		int a=quelmotif * 16*16;
		System.arraycopy(motifs_map, a, coupe, 0, 16*16);
		return coupe;
	}
}
