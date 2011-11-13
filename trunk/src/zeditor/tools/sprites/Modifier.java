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

import com.sun.xml.internal.ws.util.StringUtils;

import zeditor.tools.banque.Foret1;
import zeditor.tools.banque.Foret2;
import zeditor.tools.banque.Grotte;
import zeditor.tools.banque.Maison;
import zeditor.tools.banque.Palais1;
import zeditor.tools.banque.Village;
import zeditor.tools.tiles.Banque;
import zeditor.tools.tiles.MotifBankEdit;
import zildo.client.gui.GUIDisplay;
import zildo.fwk.bank.SpriteBank;
import zildo.fwk.gfx.engine.TileEngine;
import zildo.monde.Game;
import zildo.monde.dialog.Behavior;
import zildo.monde.dialog.MapDialog;
import zildo.monde.map.Area;
import zildo.monde.map.Zone;
import zildo.monde.sprites.SpriteModel;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.resource.Constantes;
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
       
        //new Modifier().saveAllMaps();
        //new Modifier().fixPnj2();
        //new Modifier().saveElements3();
        //new Modifier().saveFontes2();
        //new Modifier().saveAllMotifBank();
        //new Modifier().saveBanque();
        //new Modifier().saveGears();
        //new Modifier().savePnj();
        new Modifier().savePnj2();
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
    	 new Foret1().save();
     }
     
     public void saveAllMotifBank() {
    	 for (String name : TileEngine.tileBankNames) {
    		 try {
    			 
				Class<?> clazz = Class.forName("zeditor.tools.banque."+StringUtils.capitalize(name));
				 if (Banque.class.isAssignableFrom(clazz)) {
					 Banque b = (Banque) clazz.newInstance();
					 b.save();
				 } else {
					 throw new RuntimeException("Class "+name+" should be a motif bank !");
				 }
			} catch (Exception e) {
				throw new RuntimeException("Can't instantiate class "+name);
			}
    	 }
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

     public void saveElements3() {
         SpriteBankEdit bankElem=new SpriteBankEdit(EngineZildo.spriteManagement.getSpriteBank(SpriteBank.BANK_ELEMENTS));
   		 //bankElem.removeSpr(ElementDescription.SCEPTER.getNSpr()+1);
    	 bankElem.loadImage("elem", COLOR_BLUE);
    	 //bankElem.removeSpr(ElementDescription.BOOK_SIGN.getNSpr());
    	 //bankElem.removeSpr(ElementDescription.CASTLE_RED_FLAG.getNSpr());
    	 //bankElem.removeSpr(ElementDescription.CASTLE_RED_FLAG.getNSpr());
    	 //bankElem.removeSpr(ElementDescription.CASTLE_RED_FLAG.getNSpr());
    	 //bankElem.removeSpr(ElementDescription.CASTLE_RED_FLAG.getNSpr());
    	 //bankElem.removeSpr(ElementDescription.CASTLE_RED_FLAG.getNSpr());
    	 //bankElem.removeSpr(ElementDescription.CASTLE_RED_FLAG.getNSpr());
    	 //bankElem.removeSpr(ElementDescription.WINDOW_WOOD.getNSpr());
    	 //bankElem.removeSpr(ElementDescription.WINDOW_WOOD.getNSpr());
    	 //bankElem.removeSpr(ElementDescription.WINDOW_WOOD.getNSpr());
    	 // SPADE : 0,177,11,19
    	 //bankElem.addSprFromImage(ElementDescription.BOOK_SIGN.getNSpr(), 11, 177, 32, 20);
    	 //bankElem.addSprFromImage(ElementDescription.LEAF.getNSpr(), 16, 169, 8, 7);
    	 //bankElem.addSprFromImage(ElementDescription.MILK.getNSpr(), 43, 181, 11, 16);
    	 Zone[] zones = new ElementsPlus().zones;
    	 int nSpr=123;
    	 for (Zone z : zones) {
    		 if (bankElem.getNSprite() <= nSpr) {
    			 bankElem.addSprFromImage(nSpr, z.x1, z.y1, z.x2, z.y2);
    		 }
    		 nSpr++;
    		 if (nSpr == 161) {
    			 bankElem.loadImage("interia2", COLOR_BLUE);
    		 }
    		 if (nSpr == 163) {
    			 bankElem.loadImage("elem", COLOR_BLUE);
    		 }
    	 }
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
         SpriteBankEdit bank=new SpriteBankEdit(EngineZildo.spriteManagement.getSpriteBank(SpriteBank.BANK_ELEMENTS));

         bank.clear();
         bank.loadImage("interia2", COLOR_BLUE);

    	 // Add doors
    	 bank.addSpritesFromBank(new Gears());
    	 bank.setName("gear.spr");
    	 bank.saveBank();
     }
     
     public void savePnj2() {
         SpriteBankEdit bank=new SpriteBankEdit(EngineZildo.spriteManagement.getSpriteBank(SpriteBank.BANK_PNJ2));
         bank.clear();
    	 bank.addSpritesFromBank(new Pnj2());
    	 bank.saveBank();
     }
   
     public void savePnj() {
         SpriteBankEdit bank=new SpriteBankEdit(EngineZildo.spriteManagement.getSpriteBank(SpriteBank.BANK_PNJ));
         bank.clear();
    	 bank.addSpritesFromBank(new Pnj());
    	 bank.saveBank();
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
    	
		String path=Constantes.DATA_PATH + Constantes.MAP_PATH;
		
		FilenameFilter mapFilter = new FilenameFilter() {
    		public boolean accept(File dir, String name) {
    			return name.toLowerCase().endsWith(".map");
    		}
		};
		List<File> mapsFile=new ArrayList<File>();
		File[] scenarioMaps = new File(path).listFiles(mapFilter);
		mapsFile.addAll(Arrays.asList(scenarioMaps));
		LogManager.getLogManager().reset();
		
        Game game = new Game(null, true);
        new Server(game, true);
		for (File f : mapsFile) {
			String name=f.getName();
			System.out.println("Processing "+name+"...");
			MapManagement mapManagement=EngineZildo.mapManagement;

			mapManagement.loadMap(name, false);

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