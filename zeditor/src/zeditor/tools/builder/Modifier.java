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

package zeditor.tools.builder;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.LogManager;

import zeditor.tools.banque.LavaCave;
import zeditor.tools.palette.PaletteExtractor;
import zeditor.tools.sprites.ElementsPlus;
import zeditor.tools.sprites.Fontes;
import zeditor.tools.sprites.Gears;
import zeditor.tools.sprites.PjZildo;
import zeditor.tools.sprites.Pnj;
import zeditor.tools.sprites.Pnj2;
import zeditor.tools.sprites.Pnj3;
import zeditor.tools.sprites.SpriteBankEdit;
import zeditor.tools.sprites.SpriteBanque;
import zeditor.tools.tiles.Banque;
import zildo.Zildo;
import zildo.client.Client;
import zildo.client.gui.GUIDisplay;
import zildo.fwk.ZUtils;
import zildo.fwk.bank.SpriteBank;
import zildo.fwk.bank.TileBank;
import zildo.fwk.gfx.engine.TileEngine;
import zildo.monde.Game;
import zildo.monde.dialog.Behavior;
import zildo.monde.dialog.MapDialog;
import zildo.monde.map.Area;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.SpriteModel;
import zildo.monde.sprites.SpriteStore;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.desc.EntityType;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.util.Zone;
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
    	 Zildo.soundEnabled = false;
    	 //new Modifier().textureBuilder();
    	 //if (true) System.exit(0);
        Game g=new Game(null, true);
        new EngineZildo(g);
        System.out.println();
       
        // ***IMPORTANT ***
        // Exteria1 is the reference picture for palette
        // ***IMPORTANT ***
        
   	 // To enable OpenGL
		Client cl = new Client(false);
		//new Modifier().saveAllMotifBank();
		//new Modifier().saveAllSpriteBank();
		/*
        new Modifier().saveNamedTileBank("lavacave");

        new Modifier().saveNamedSpriteBank("pnj3.spr");
*/
        //new Modifier().saveFontes2();
        //new Modifier().saveAllSpriteBank();
        //if (false) {
        //    new Modifier().savePalette();
        //new Modifier().saveAllMaps();
        //new Modifier().fixPnj2();
        //new Modifier().saveElements();
        new Modifier().saveFontes2();
        //new Modifier().saveAllMotifBank();
        //new ReplaceAllMapsWindows().modifyAllMaps();
        //new AdjustGrotte().modifyAllMaps();
        //new Modifier().saveZildo();
        //new ReplaceSpritesModel().modifyAllMaps();
        //new AdjustChestBackTiles().modifyAllMaps();
        //new AdjustBackTiles().modifyAllMaps();
        //new ReplaceAllMapsFloor().modifyAllMaps();
        //new Modifier().saveGears();
        //new Modifier().saveZildo();
        /*
        new Modifier().savePnj();
        new Modifier().savePnj2();
        new Modifier().savePnj3();
        new Modifier().saveLavaCave();
        */
        //new Modifier().modifyAllMaps();
        //new Modifier().adjustSpritePositionOnAllMaps();
        //new Modifier().generateImg();
        //new Modifier().fixZildo();
       //new Modifier().ripDialogFromAllMaps();
        
        //new Modifier().temporaryFixPolakym();
        //}
        
        //new AdjustRotations().modifyAllMaps();
/*
		new ReverseSpriteBank(new ElementsPlus(), EngineZildo.spriteManagement.getSpriteBank(SpriteBank.BANK_ELEMENTS))
		.rebuildImages();

		new ReverseSpriteBank(new Pnj3(), EngineZildo.spriteManagement.getSpriteBank(SpriteBank.BANK_PNJ3))
			.rebuildImages();
		new ReverseSpriteBank(new Pnj4(), EngineZildo.spriteManagement.getSpriteBank(SpriteBank.BANK_PNJ4))
		.rebuildImages();
		*/
    }
     
     public void saveNamedTileBank(String tileBankName) {
    	 new TileBank().charge_motifs(tileBankName);
    	 getTileBankClass(tileBankName).save();
     }
     
     public void saveNamedSpriteBank(String spriteBankName) {
    	 int indexBank;
    	 for (indexBank=0;indexBank<Constantes.NB_SPRITEBANK;indexBank++) {
    		 if (SpriteStore.sprBankName[indexBank].equalsIgnoreCase(spriteBankName)) {
    			 break;
    		 }
    	 }
         SpriteBankEdit bank=new SpriteBankEdit(EngineZildo.spriteManagement.getSpriteBank(indexBank));
         bank.clear();
         String nameWithoutExtension = spriteBankName.substring(0, spriteBankName.indexOf("."));
    	 bank.addSpritesFromBank(getSpriteBankClass(nameWithoutExtension));
    	 bank.saveBank();
     }
     
     public void saveAllMotifBank() {
    	 for (String name : TileEngine.tileBankNames) {
    		 Banque b = getTileBankClass(name);
			 b.save();
    	 }
     }
     
     public void saveAllSpriteBank() {
    	 for (String name : SpriteStore.sprBankName) {
    		 if ("font.spr".equals(name)) {
    			 saveFontes2();	// Particular case for fontes
    		 } else {
    			 saveNamedSpriteBank(name);
    		 }
    	 }
     }
     
     private Banque getTileBankClass(String bankName) {
		 try {
			if (bankName.equals("lavacave")) {
				bankName = "LavaCave";
			}
			Class<?> clazz = Class.forName("zeditor.tools.banque."+ZUtils.capitalize(bankName));
			 if (Banque.class.isAssignableFrom(clazz)) {
				 Banque b = (Banque) clazz.newInstance();
				 return b;
			 } else {
				 throw new RuntimeException("Class "+bankName+" should be a motif bank !");
			 }
		} catch (Exception e) {
			throw new RuntimeException("Can't instantiate class "+bankName);
		}
     }
     
     private SpriteBanque getSpriteBankClass(String bankName) {
		 try {
			 if ("elem".equals(bankName)) {
				 bankName = "elementsPlus";
			 } else if ("zildo".equals(bankName)) {
				 bankName = "PjZildo";
			 } else if ("gear".equals(bankName)) {
				 bankName = "Gears";
			 } else if ("font".equals(bankName)) {
				 bankName = "Fontes";
			 }
			Class<?> clazz = Class.forName("zeditor.tools.sprites."+ZUtils.capitalize(bankName));
			 if (SpriteBanque.class.isAssignableFrom(clazz)) {
				 SpriteBanque b = (SpriteBanque) clazz.newInstance();
				 return b;
			 } else {
				 throw new RuntimeException("Class "+bankName+" should be a sprite bank !");
			 }
		} catch (Exception e) {
			throw new RuntimeException("Can't instantiate class "+bankName, e);
		}
     }
     
     public void saveElements2() {
         SpriteBankEdit bankElem=new SpriteBankEdit(EngineZildo.spriteManagement.getSpriteBank(SpriteBank.BANK_ELEMENTS));
         bankElem.clear();
         bankElem.loadImage("objets", COLOR_BLUE);
         int nSpr=bankElem.getNSprite();
         Zone[] elements=new ElementsPlus().getZones();
         for (Zone z : elements) {
         	bankElem.addSprFromImage(nSpr, z);
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
    	 Zone[] zones = new ElementsPlus().getZones();
    	 int nSpr=123;
    	 for (Zone z : zones) {
    		 if (bankElem.getNSprite() <= nSpr) {
    			 bankElem.addSprFromImage(nSpr, z);
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
     
     public void saveElements() {
         SpriteBankEdit bankElem=new SpriteBankEdit(EngineZildo.spriteManagement.getSpriteBank(SpriteBank.BANK_ELEMENTS));
   		 //bankElem.removeSpr(ElementDescription.SCEPTER.getNSpr()+1);
    	 bankElem.loadImage("elem", COLOR_BLUE);
    	 bankElem.clear();
    	 Zone[] zones = new ElementsPlus().getZones();
    	 System.out.println("elemntsplus size="+zones.length);
    	 System.out.println("elemDescription size="+ElementDescription.class.getEnumConstants().length);
    	 bankElem.addSpritesFromBank(new ElementsPlus());
    	 bankElem.saveBank();
     }
     
     public void saveZildo() {
         SpriteBankEdit bankZildo=new SpriteBankEdit(EngineZildo.spriteManagement.getSpriteBank(SpriteBank.BANK_ZILDO));
         bankZildo.clear();
         bankZildo.addSpritesFromBank(new PjZildo());
         /*
    	 bankZildo.loadImage("link3b", COLOR_BLUE);
    	 Zone[] zones = new PjZildo().getZones();
    	 int nSpr=bankZildo.getNSprite();
    	 for (Zone z : zones) {
    		 if (bankZildo.getNSprite() <= nSpr) {
    			 bankZildo.addSprFromImage(nSpr, z.x1, z.y1, z.x2, z.y2);
    		 }
    		 nSpr++;
    	 }*/
    	 bankZildo.saveBank();
     }
      
	public void saveFontes2() {
		SpriteBankEdit bankFont = new SpriteBankEdit(EngineZildo.spriteManagement.getSpriteBank(SpriteBank.BANK_FONTES));
		bankFont.clear();
		bankFont.fillNSprite(GUIDisplay.transcoChar.length());
		bankFont.loadImage("fontes6", COLOR_BLUE);

		String fontOrder = "KRWMX'" + "NLQTDAt" + "UVYBHPO,"+ "EwSZJmCId" + "fü?7uxûùv£y#"+
		"4np%qa65-zàâ2" + "39r~hjk0o§$8ce" + "gçèéêëbs!>iïl1<.)(" + ":;äîôö/FG";
		/*
		String fontOrder = "KRWXMLNQFDAUTYHBVG." + "EOPwCZJSmIfnd7?xüuvûyù40pq-k%"
				+ "9a6z£àâ5oceghj~$23çèéêë8r§#bsi!>ïtl1)<;:" + "',(äîôö/";
				*/

		// bankFont.captureFonts(156, 22, fontOrder, 0, 0);	// Small fonts
		bankFont.captureFonts(0, 43, fontOrder, 0, 0);
		bankFont.loadImage("fontes5", COLOR_BLUE);
		//bankFont.captureFonts(261, 16, null, 0, 0);

		int nSpr = bankFont.getNSprite();
		Zone[] elements = new Fontes().getZones();
		for (Zone z : elements) {
			bankFont.addSprFromImage(nSpr, z);
			nSpr++;
		}
		bankFont.saveBank();
	}
     
     public void saveGears() {
         SpriteBankEdit bank=new SpriteBankEdit(EngineZildo.spriteManagement.getSpriteBank(SpriteBank.BANK_ELEMENTS));

         bank.clear();
         bank.loadImage("interia3", COLOR_BLUE);

    	 // Add doors
    	 bank.addSpritesFromBank(new Gears());
    	 bank.setName("gear.spr");
    	 bank.saveBank();
     }
     
     public void saveLavaCave() {
    	 new LavaCave().save();
     }
     
     public void savePnj3() {
         SpriteBankEdit bank=new SpriteBankEdit(EngineZildo.spriteManagement.getSpriteBank(SpriteBank.BANK_PNJ3));
         bank.clear();
    	 bank.addSpritesFromBank(new Pnj3());
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
    
     public void savePalette() {
    	 new PaletteExtractor("exteria1.png").save("game1.pal");
    	 new PaletteExtractor("dragonpal.png").save("game2.pal");
     }
     
     public void saveFontes() {
         SpriteBankEdit bank=new SpriteBankEdit(EngineZildo.spriteManagement.getSpriteBank(SpriteBank.BANK_FONTES));
         bank.clear();
    	 bank.addSpritesFromBank(new Fontes());
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
            //bankOut.addSpr(fin+i, model.getTaille_x(), model.getTaille_y(), null, bankIn.getSpriteGfx(nSprOriginal));
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
        bankIn.addSprFromImage(pos,   new Zone(67, 70, 16, 8));
        bankIn.addSprFromImage(pos+1, new Zone(67, 79, 16, 9));
        bankIn.addSprFromImage(pos+2, new Zone(67, 89, 16, 8));
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
    	new AllMapProcessor() {
    		@Override
    		public boolean run() {
    			boolean polakyMap = mapName.startsWith("polaky");
    			if (polakyMap) {
    				MapManagement mapManagement=EngineZildo.mapManagement;
    				Area map = mapManagement.getCurrentMap();
    				MapDialog dialogs = map.getMapDialog();
    				
    				String name=mapName.substring(0, mapName.indexOf("."));
    				
    				// Behavior
    				Map<String, Behavior> behaviors = dialogs.getBehaviors();
    				
    				for (String key : behaviors.keySet()) {
    					Behavior b=behaviors.get(key);
    					for (int i=0;i<10;i++) {
    						if (b.replique[i] == 0) {
    							break;
    						}
    						String s = dialogs.getSentence(b, i);
    						if (s != null) {

    							String sentenceKey=name+"."+key+"."+i;
    							System.out.println(sentenceKey+"="+s);
    							
    							// Replace sentence by key in the map file
    				    		dialogs.setSentence(b, i, sentenceKey);

    						}
    					}
    				}
    				return true;
    			} else {
    				return false;
    			}
    			
    		}
    	}.modifyAllMaps();
    }
    
	public void adjustSpritePositionOnAllMaps() {
		new AllMapProcessor() {
			
			@Override
			public boolean run() {
				List<SpriteEntity> entities = EngineZildo.spriteManagement.getSpriteEntities(null);
				for (SpriteEntity entity : entities) {
					if (entity.getEntityType() == EntityType.ELEMENT ||
							entity.getEntityType() == EntityType.ENTITY) {
						entity.x+=entity.getSprModel().getTaille_x();
					}
				}
				return true;
			}
		}.modifyAllMaps();
	}
	
	public void temporaryFixPolakym() {
		new AdjustBackTiles().modifyOneMap("polakym.map");
	}
	
}