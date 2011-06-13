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

package zeditor.tools.sprites;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.LogManager;

import zeditor.tools.banque.Foret2;
import zeditor.tools.banque.Foret4;
import zeditor.tools.banque.Grotte;
import zeditor.tools.tiles.MotifBankEdit;
import zildo.client.gui.GUIDisplay;
import zildo.client.sound.Ambient.Atmosphere;
import zildo.fwk.bank.SpriteBank;
import zildo.monde.Game;
import zildo.monde.dialog.Behavior;
import zildo.monde.dialog.MapDialog;
import zildo.monde.map.Area;
import zildo.monde.map.Zone;
import zildo.monde.sprites.SpriteModel;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.prefs.Constantes;
import zildo.server.EngineZildo;
import zildo.server.MapManagement;
import zildo.server.Server;

/**
 * Test class, doesn't apart to real project.<br/>
 * 
 * We can find here a bunch of methods dealing some connex issues like :<ul>
 * <li>saving a sprite/motif bank</li>
 * <li>fix a bank</li>
 * <li>generate an image</li>
 * <li>modify all maps</li>
 * <li>and so on</li>
 * </ul>
 * @author Tchegito
 *
 */
public class Modifier {
	
	public final static int COLOR_BLUE = 255;
	
     public static void main(String[] args) {
         // Intialize game engine
        Game g=new Game(null, true);
        new EngineZildo(g);
       

        //new Modifier().fixPnj2();
        //new Modifier().saveElements2();
        //new Modifier().saveFontes2();
        new Modifier().saveBanque();
        //new Modifier().saveGears();
        //new Modifier().saveAllMaps();
        //new Modifier().generateImg();
        //new Modifier().fixZildo();
       // new Modifier().ripDialogFromAllMaps();
    }
     
     public void generateImg() {
    	 MotifBankEdit bankEdit=new MotifBankEdit(new Grotte());
    	 bankEdit.charge_motifs(bankEdit.getName()+".dec");
    	 bankEdit.generateImg();
     }
     
     public void saveBanque() {
    	 new Foret2().save();
     }
     
     public void saveElements2() {
         SpriteBankEdit bankElem=new SpriteBankEdit(EngineZildo.spriteManagement.getSpriteBank(SpriteBank.BANK_ELEMENTS));
         bankElem.clear();
         bankElem.loadImage("objets", COLOR_BLUE);
         int nSpr=bankElem.getNSprite();
         Zone[] elements=new ElementsPlus().getZones();
         for (Zone z : elements) {
         	bankElem.addSprFromImage(nSpr, z.x1, z.y1, z.x2, z.y2);
         	nSpr++;
         }
         bankElem.setName("elements2.spr");
         bankElem.saveBank();
     }

     public void saveFontes2() {
         SpriteBankEdit bankElem=new SpriteBankEdit(EngineZildo.spriteManagement.getSpriteBank(SpriteBank.BANK_FONTES));
         bankElem.clear();
         bankElem.loadImage("fontes", COLOR_BLUE);
         
         // Capture the fonts
         int startX=0;
         int startY=0;
         for (int i=0;i<GUIDisplay.transcoChar.length();i++) {
        	 // Get size
        	 int width=bankElem.getWidth(startX, startY, 16);
        	 if (width > 1) {
	        	 bankElem.addSprFromImage(i, startX, startY, width, 16);
	        	 
	        	 System.out.println(startX+" , "+startY+" size="+width);

	        	 startX+=width + 1;
	        	 if (startX >= 184) {
	        		 startX=0;
	        		 startY+=16;
	        	 }
        	 } else {
        		 startX = 0;
        		 startY+=16;
        		 i--;
        	 }
         }
         int nSpr=bankElem.getNSprite();
         Zone[] elements=new Fontes().getZones();
         for (Zone z : elements) {
         	bankElem.addSprFromImage(nSpr, z.x1, z.y1, z.x2, z.y2);
         	nSpr++;
         }
         bankElem.saveBank();
     }
     
     public void saveGears() {
         SpriteBankEdit bankElem=new SpriteBankEdit(EngineZildo.spriteManagement.getSpriteBank(SpriteBank.BANK_ELEMENTS));
         
         bankElem.loadImage("interia2", COLOR_BLUE);
         int nSpr=bankElem.getNSprite();
    	 for (int i=0;i<nSpr;i++) {
    		 bankElem.removeSpr(0);
    	 }
    	 // Add doors
    	 Zone[] elements=new Gears().getZones();
    	 nSpr=0;
         for (Zone z : elements) {
          	bankElem.addSprFromImage(nSpr, z.x1, z.y1, z.x2, z.y2);
          	nSpr++;
          }
    	 bankElem.setName("gear.spr");
    	 bankElem.saveBank();
     }
     
     /** Not useful anymore. It remains here as an example. **/
    public void fixPnj2() {
        EngineZildo.spriteManagement.charge_sprites("PNJ3.SPR");
       
        // Remove spector
        SpriteBankEdit bankIn=new SpriteBankEdit(EngineZildo.spriteManagement.getSpriteBank(SpriteBank.BANK_PNJ));
        SpriteBankEdit bankOut=new SpriteBankEdit(EngineZildo.spriteManagement.getSpriteBank(6));
        int fin=bankOut.getNSprite();
        for (int i=0;i<6;i++) {
            int nSprOriginal=PersoDescription.VOLANT_BLEU.getNSpr() + i;
            SpriteModel model=bankIn.get_sprite(nSprOriginal);
            System.out.println("On copie le sprite no"+nSprOriginal);
            bankOut.addSpr(fin+i, model.getTaille_x(), model.getTaille_y(), bankIn.getSpriteGfx(nSprOriginal));
        }
        bankIn.removeSpr(124);
        bankIn.removeSpr(124);
        bankIn.removeSpr(124);
        bankIn.removeSpr(124);
        bankIn.removeSpr(124);
        bankIn.removeSpr(124);
        bankIn.saveBank();
        bankOut.saveBank();
    }

    public void fixZildo() {
        SpriteBankEdit bankIn=new SpriteBankEdit(EngineZildo.spriteManagement.getSpriteBank(SpriteBank.BANK_ZILDO));
        bankIn.loadImage("link3b", COLOR_BLUE);
        int pos=bankIn.getNSprite();
        bankIn.addSprFromImage(pos,   67, 70, 16, 8);
        bankIn.addSprFromImage(pos+1, 67, 79, 16, 9);
        bankIn.addSprFromImage(pos+2, 67, 89, 16, 8);
        bankIn.saveBank();
    }

    public void saveAllMaps() {
    	
		String path=Constantes.DATA_PATH;
		File directory=new File(path);
		
		FilenameFilter mapFilter = new FilenameFilter() {
    		public boolean accept(File dir, String name) {
    			return name.toLowerCase().endsWith(".map");
    		}
		};
		File[] maps = directory.listFiles(mapFilter);
		List<File> mapsFile=new ArrayList();
		//mapsFile.addAll(Arrays.asList(maps));
		File[] scenarioMaps = new File(path+"/scenario").listFiles(mapFilter);
		mapsFile.addAll(Arrays.asList(scenarioMaps));
		LogManager.getLogManager().reset();
		
        Game game = new Game(null, true);
        new Server(game, true);
		for (File f : mapsFile) {
			String name="scenario/"+f.getName();
			System.out.println("Processing "+name+"...");
			MapManagement mapManagement=EngineZildo.mapManagement;

			mapManagement.loadMap(name, false);
		    
			Area area = mapManagement.getCurrentMap();
			area.setAtmosphere(Atmosphere.OUTSIDE);
			String mapName=name.substring(0, name.indexOf("."));
			if (mapName.indexOf("m") != -1) {
				area.setAtmosphere(Atmosphere.HOUSE);
			} else if (mapName.startsWith("polaky") && !mapName.equals("polaky")) {
				area.setAtmosphere(Atmosphere.CAVE);
			}
			System.out.println(area.getAtmosphere());
	        // Save the map into a temporary file
			mapManagement.saveMapFile(name);
		}
    }
    
    public void ripDialogFromAllMaps() {
    	
		String path=Constantes.DATA_PATH+"anciens";
		File directory=new File(path);
		
		File[] maps = directory.listFiles(new FilenameFilter() {
    		public boolean accept(File dir, String name) {
    			return name.toLowerCase().endsWith(".map");
    		}
		});
		LogManager.getLogManager().reset();
		
        Game game = new Game(null, true);
        new Server(game, true);
		for (File f : maps) {
			String name=f.getName();
			EngineZildo.mapManagement.loadMap("..\\anciens\\"+name, false);
		        
	        // Save the map into a temporary file
			MapManagement mapManagement=EngineZildo.mapManagement;
			Area map = mapManagement.getCurrentMap();
			MapDialog dialogs = map.getMapDialog();
			
			String mapName=map.getName().substring("..\\anciens\\".length());
			mapName=mapName.substring(0, mapName.indexOf("."));
			
			// Behavior
			Map<String, Behavior> behaviors = dialogs.getBehaviors();

			System.out.println("\n## "+mapName);

			for (String key : behaviors.keySet()) {
				Behavior b=behaviors.get(key);
				for (int i=0;i<10;i++) {
					if (b.replique[i] == 0) {
						break;
					}
					String s = dialogs.getSentence(b, i);
					if (s != null) {

						String sentenceKey=mapName+"."+key+"."+i;
						System.out.println(sentenceKey+"="+s);
						
						// Replace sentence by key in the map file
			    		dialogs.setSentence(b, i, sentenceKey);

					}
				}
			}
			mapManagement.saveMapFile(name);
		}
    }
}