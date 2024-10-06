package junit.area;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

import tools.EngineUT;
import zeditor.core.selection.SpriteSelection;
import zildo.fwk.file.EasyReadingFile;
import zildo.monde.map.Area;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.Perso.PersoInfo;
import zildo.monde.sprites.persos.PersoNJ;
import zildo.monde.util.Point;
import zildo.resource.Constantes;
import zildo.server.EngineZildo;
import zildo.server.MapManagement;
import zildo.server.PersoManagement;
import zildo.server.SpriteManagement;

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

/**
 * @author Tchegito
 *
 */
public class CheckMapSave extends EngineUT {

	final static String MAP_ORIGINAL = "dragon.map";
	final static String MAP_TEST = "maptest.map";

	
	void prepareSaveMap() {
		EngineZildo.mapManagement.loadMap(MAP_ORIGINAL, false);
	        
        // Save the map into a temporary file
		MapManagement mapManagement=EngineZildo.mapManagement;
        mapManagement.saveMapFile(MAP_TEST);
	}

	@AfterClass
	public static void clean() throws Exception {
		// Remove potential saved map
		File file=new File(Constantes.DATA_PATH + Constantes.MAP_PATH + MAP_TEST);
		if (file.canRead() && file.canWrite()) {
			Assert.assertTrue(file.delete());
		}
	}
	
	/**
	 * Compare each file binarily.
	 */
	@Test
	public void testBinary() {
		prepareSaveMap();
        // Now compare files
        EasyReadingFile original=new EasyReadingFile("maps"+File.separator+MAP_ORIGINAL);
        EasyReadingFile copied=new EasyReadingFile("maps"+File.separator+MAP_TEST);
        
        String message="Size1="+original.getSize()+" / Size2="+copied.getSize();

        Assert.assertTrue(msg(message), original.getSize() == copied.getSize());

        byte[] a1=original.getAll().array();
        byte[] a2=copied.getAll().array();
        for (int i=0;i<original.getSize();i++) {
        	Assert.assertEquals(msg("difference at byte "+i), a1[i], a2[i]);
        }
	}
	
	/**
	 * Compare map's statistics.
	 */
	@Test
	public void testMapStats() {
		prepareSaveMap();
		MapManagement mapMgt=EngineZildo.mapManagement;
		SpriteManagement sprMgt=EngineZildo.spriteManagement;
		PersoManagement perMgt=EngineZildo.persoManagement;
		sprMgt.clearSprites(false);
		
		mapMgt.loadMap(MAP_ORIGINAL, false);
		Area original=mapMgt.getCurrentMap();
		
		List<SpriteEntity> originalEntities=sprMgt.getSpriteEntities(null);
		List<Perso> originalPersos=original.filterExportablePersos(perMgt.tab_perso);
		
		mapMgt.loadMap(MAP_TEST, false);
		Area copied=mapMgt.getCurrentMap();
		List<SpriteEntity> copiedEntities=sprMgt.getSpriteEntities(null);
		List<Perso> copiedPersos=copied.filterExportablePersos(perMgt.tab_perso);
		
		Assert.assertTrue(msg("Bad dimension x"), original.getDim_x() == copied.getDim_x());
		Assert.assertTrue(msg("Bad dimension y"), original.getDim_y() == copied.getDim_y());
		Assert.assertTrue(msg("Bad dialogs"), original.getMapDialog().equals(copied.getMapDialog()));
		Assert.assertTrue(msg("Bad chaining point"), original.getChainingPoints().equals(copied.getChainingPoints()));
		Assert.assertTrue(msg("Entities are not the same size"), originalEntities.size() == copiedEntities.size());
		Assert.assertTrue(msg("Persos are not the same size"), originalPersos.size() == copiedPersos.size());
		
		// Sprite arrays are same sized
		for (int i=0;i<originalEntities.size();i++) {
			SpriteEntity e1=originalEntities.get(i);
			SpriteEntity e2=copiedEntities.get(i);
			if (!compareEntity(e1, e2)) {
				System.out.println(e1);
			}
			Assert.assertTrue(msg("entity n°"+i+" is different."), compareEntity(e1, e2));
		}
		
		// Perso arrays are same sized too
		for (int i=0;i<originalPersos.size();i++) {
			Perso p1=originalPersos.get(i);
			Perso p2=copiedPersos.get(i);
			Assert.assertTrue(msg("perso n°"+i+" is different."), comparePerso(p1, p2));
		}
	}

	/** We used to have a bug when a sprite was horizontally reversed, its locaction was altered **/
	@Test
	public void checkSpriteLocation() {
		final int EXPECTED_X = 600;
		final int EXPECTED_Y = 485;
		SpriteEntity doorBar = ElementDescription.DOOR_OPEN2.createElement();
		
		SpriteSelection<SpriteEntity> sel = new SpriteSelection<>(Arrays.asList(doorBar));
		sel.place(new Point(EXPECTED_X, EXPECTED_Y), 0);
		sel.reverse(true);
		doorBar.setForeground(true);
		
        EngineZildo.mapManagement.saveMapFile(MAP_TEST);
        
        EngineZildo.mapManagement.loadMap(MAP_TEST, false);
        SpriteEntity found = findEntityByDesc(ElementDescription.DOOR_OPEN2);
		Assert.assertNotNull(found);

		Assert.assertEquals(EXPECTED_X, found.x, 0);
		Assert.assertEquals(EXPECTED_Y, found.y, 0);
	}
	
	@Test
	public void savePnj() {
		PersoNJ hooded = (PersoNJ) spawnPerso(PersoDescription.HOODED, "hooded", 160, 100);
        hooded.setInfo(PersoInfo.ENEMY);
		EngineZildo.mapManagement.saveMapFile(MAP_TEST);
        
        EngineZildo.mapManagement.loadMap(MAP_TEST, false);
        Assert.assertNotNull(findEntityByDesc(PersoDescription.HOODED));
	}
	
	private boolean compareEntity(SpriteEntity p_e1, SpriteEntity p_e2) {
		return (p_e1.x == p_e2.x && p_e1.y == p_e2.y && p_e1.z == p_e2.z && p_e1.nSpr == p_e2.nSpr);
	}
	
	private boolean comparePerso(Perso p_p1, Perso p_p2) {
		return (p_p1.getAngle() == p_p2.getAngle() &&
				//p_p1.getEn_bras().equals(p_p2.getEn_bras()) &&
				p_p1.getQuel_deplacement() == p_p2.getQuel_deplacement() &&
				p_p1.getInfo() == p_p2.getInfo() &&
				p_p1.getName().equals(p_p2.getName()));
	}
	
	private String msg(String mess) {
		return MAP_ORIGINAL+": "+mess;
	}
	
}
