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

package zildo.fwk.bank;

import java.util.ArrayList;
import java.util.List;

import zildo.Zildo;
import zildo.fwk.file.EasyBuffering;
import zildo.monde.map.TileCollision;
import zildo.monde.map.TileInfo;

/**
 * Load a DEC file.<br/>
 * 
 * Such file is modelized by a sequence of following buffers:<ul>
 * <li>16*16 bytes (values between 0..255 corresponding to a palette index)</li>
 * <li>1 byte : collision value (represents a {@link TileInfo} element)</li>
 * </ul>
 * @author evariste.boussaton
 *
 */
public class TileBank {

	protected short[] motifs_map; // Pointeur sur nos graphs
	private String name;				// Max length should be 12
	protected int nb_motifs;		// Nombre de motifs dans la banque

	public final static int motifSize = 16*16 + 1; // 1 byte for collision and 256 for graphic

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNb_motifs() {
		return nb_motifs;
	}

	public void setNb_motifs(int nb_motifs) {
		this.nb_motifs = nb_motifs;
	}

	public TileBank() {
	}
	
	public void charge_motifs(String filename) {
		// On récupère la taille du fichier .DEC
		EasyBuffering file=Zildo.pdPlugin.openFile(filename+".dec");
		int size=file.getSize();
		
		name=filename;
		nb_motifs=size / motifSize;
	
		// Load the mini-pictures
		motifs_map=file.readUnsignedBytes();
		
		List<Integer> infoCollisions = new ArrayList<Integer>();
		for (int i=0;i<nb_motifs;i++) {
			int infoColl = motifs_map[motifSize * (i+1) - 1];
			infoCollisions.add(infoColl);
		}
		TileCollision.getInstance().updateInfoCollision(name, infoCollisions);
	}
	
	public short[] get_motif(int quelmotif) {
		short[] coupe=new short[motifSize-1];
		int a=quelmotif * motifSize;
		System.arraycopy(motifs_map, a, coupe, 0, motifSize - 1);	// Doesn't copy collision info
		return coupe;
	}

	public short[] getMotifs_map() {
		return motifs_map;
	}
	
	public void freeTempBuffer() {
		//motifs_map = null;	// Optim for android but wrong for zeditor
	}
}
