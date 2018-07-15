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
import zildo.fwk.gfx.engine.TileEngine;
import zildo.monde.map.TileCollision;
import zildo.monde.map.TileInfo;

/**
 * Load a DEC file.<br/>
 * 
 * Such file is modelized by a sequence of following buffers:<ul>
 * <li>1 byte : collision value (represents a {@link TileInfo} element)</li>
 * </ul>
 * @author evariste.boussaton
 *
 */
public class TileBank {

	private String name;				// Max length should be 12
	public int nb_motifs;		// Nombre de motifs dans la banque

	public final static int motifSize = 1; // 1 byte for collision

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TileBank() {
	}
	
	/** Read a DEC file: contains only collision data about tiles **/
	public void charge_motifs(String filename) {
		EasyBuffering file=Zildo.pdPlugin.openFile(filename+".dec");
		int size=file.getSize();
		
		name=filename;
		nb_motifs=size / motifSize;

		// Load collision
		List<Integer> infoCollisions = new ArrayList<Integer>();
		for (int i=0;i<nb_motifs;i++) {
			int infoColl = file.readUnsignedByte();
			infoCollisions.add(infoColl);
		}
		TileCollision.getInstance().updateInfoCollision(name, infoCollisions);
	}
	
	public int getIndex() {
		return TileEngine.getBankFromName(getName());
	}
}
