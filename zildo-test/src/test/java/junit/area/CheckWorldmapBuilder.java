/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
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

package junit.area;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import tools.EngineUT;
import tools.annotations.DisableFreezeMonitor;
import zeditor.core.Constantes;
import zeditor.fwk.awt.MapCapturer;
import zeditor.tools.builder.WorldmapBuilder;
import zeditor.tools.builder.WorldmapBuilder.WorldMap;
import zildo.monde.map.Area;
import zildo.monde.map.ChainingPoint;
import zildo.server.EngineZildo;

/**
 * @author Tchegito
 *
 */
@Ignore
public class CheckWorldmapBuilder extends EngineUT {

	
	@Rule
	public TemporaryFolder folder= new TemporaryFolder();
	
	@Before
	public void createTempFolders() throws IOException {
		File createdFolder = folder.newFolder("maps");
		Constantes.PATH_MAPS = createdFolder.getAbsolutePath();
	}
	
	@Test @DisableFreezeMonitor
	public void basic() {
		String firstMap = "coucou";
		new WorldmapBuilder(firstMap, getMapCapturer());
	}

	// Don't forget the @DisableFreezeMonitor here for Gradle on Jenkins, because it can crashes with:
	// Process 'Gradle Test Executor 1' finished with non-zero exit value 1
	@Test @DisableFreezeMonitor
	public void advanced() {
		String firstMap = "sousbois4";
		WorldmapBuilder wmb = new WorldmapBuilder(firstMap, getMapCapturer());
		
		for (WorldMap wm : wmb.getWorldMap().values()) {
			System.out.println(wm + " ("+16*wm.theMap.getDim_x()+" x "+16*wm.theMap.getDim_y()+")");
		}
	}

	// Disable freeze monitor, cause this can take a while (6 seconds ?)
	@Test @DisableFreezeMonitor
	public void assembleImages() {
		String firstMap = "coucou";
		
		WorldmapBuilder wmb = new WorldmapBuilder(firstMap, getMapCapturer());
		wmb.savePng();
	}
	
	private MapCapturer getMapCapturer() {
		return new MapCapturer() {
			
			@Override
			public ChainingPoint loadMap(String p_mapName, ChainingPoint p_fromChangingPoint) {
				EngineZildo.mapManagement.loadMap(p_mapName, false);
				return null;
			}
			
			@Override
			public void askCapture() {
				// Create fake image entirely black
				Area area = EngineZildo.mapManagement.getCurrentMap();
				String name = area.getName().replace(".map", "");
				System.out.println("Saving image "+name);
				File path = new File(Constantes.pathCapturedMaps());
				path.mkdirs();
				String filename = path+File.separator+name;
				int totalWidth = area.getDim_x() * 16;
				int totalHeight = area.getDim_y() * 16;
		    	BufferedImage bufImage = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_RGB);
				try {
					ImageIO.write(bufImage, "png", new File(filename+".png"));
				} catch (IOException e) {
					throw new RuntimeException("Unable to write image !", e);
				}
				System.out.println("Capture finished");
			}

			@Override
			public boolean isCaptureDone() {
				return true;
			}
		};
	}
}
