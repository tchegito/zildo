package zeditor.tools.builder;

import java.util.Iterator;

import zeditor.tools.sprites.SpriteBankEdit;
import zeditor.tools.sprites.SpriteBanque;
import zeditor.tools.tiles.GraphChange;
import zildo.fwk.bank.SpriteBank;
import zildo.fwk.gfx.GFXBasics;
import zildo.monde.util.Zone;
import zildo.platform.opengl.GLUtils;

/** Load a sprite bank, and rebuild images according to sprite location on original sheet **/
public class ReverseSpriteBank extends SpriteBankEdit {

	SpriteBank graphics;
	SpriteBanque bankFactory;
	
	public ReverseSpriteBank(SpriteBanque bankFactory, SpriteBank graphics) {
		// Iterate over factory, opens image and set pixels according to graphics
		super(graphics);
		this.bankFactory = bankFactory;
		System.out.println("go");		
	
	}
	
	/** Starts from a copy/paste from SpriteBankEdit#addSpritesFromBank **/
	public void rebuildImages() {
	   	 Zone[] elements=bankFactory.getZones();
	   	 Iterator<GraphChange> itChanges = bankFactory.getPkmChanges().iterator();
	   	 GraphChange current = null;
		 int startSpr=getNSprite();
		 int i=0;
		 Iterator<int[]> buffers = bankEdit.gfxs.iterator();
		 String imageName = null;
	     for (Zone z : elements) {
	    	 if (current == null && itChanges.hasNext()) {
	    		 current = itChanges.next();
	    	 }
	    	 if (current != null) {
				if (current.nTile == i) {
					saveImage(imageName);
					
					imageName = current.imageName + current.nTile;
					loadImage(current.imageName, Modifier.COLOR_BLUE);
					current = null;
				}
	    	 }
	    	 try {
	    		 bankEdit.setRectFromImage(z, buffers.next());
	    	 } catch (Exception e) {
	    		 throw new RuntimeException("Unable to insert sprite "+i+"/"+elements.length+" on bank "+bankFactory, e);
	    	 }
	    		 i++;
	      }
		saveImage(imageName);
	}
	
	private void saveImage(String imageName) {
		if (bankEdit.imgPixels != null) {	// Do we have an image already ?
			System.out.println("save " + imageName);
			GLUtils.saveBufferAsPNG("c:\\kikoo\\freegraph\\" + imageName, bankEdit.imgPixels, bankEdit.getImageWidth(), bankEdit.getImageHeight());
		}
	}
}
