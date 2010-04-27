/**
 * Legend of Zildo
 * Copyright (C) 2006-2010 Evariste Boussaton
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

package zildo.fwk.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import zildo.prefs.Constantes;

/**
 * Simple object to create a file from a buffer.<p/>
 * You just have to instantiate an {@link EasyWritingFile} with the buffer, then call {@link #saveFile(String)}
 * with the filename.
 * @author tchegito
 *
 */
public class EasyWritingFile extends EasyBuffering {
    
	public EasyWritingFile(EasyBuffering p_buffer) {
		data=p_buffer.data;
	}
	
    public void saveFile(String p_fileName) {
    	// Wrap the buffer
    	data.flip();
    	
        OutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream(new File(Constantes.DATA_PATH+p_fileName));
            fileOut.write(data.array(), 0, data.limit());
        } catch (Exception e) {
            throw new RuntimeException("Unable to write " + p_fileName + " !");
        }
    }

}
