package zeditor.tools.builder.texture;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import zildo.fwk.bank.SpriteBank;
import zildo.fwk.gfx.GFXBasics;
import zildo.fwk.gfx.Occluder;
import zildo.fwk.gfx.engine.TileEngine;
import zildo.monde.sprites.SpriteModel;
import zildo.monde.sprites.SpriteStore;
import zildo.monde.util.Point;
import zildo.monde.util.Vector4f;
import zildo.monde.util.Zone;
import zildo.platform.engine.LwjglTextureEngine;
import zildo.platform.opengl.GLUtils;
import zildo.platform.opengl.LwjglGraphicStuff;
import zildo.resource.Constantes;

public class TileTexture {

	LwjglTextureEngine textureEngine;
	
	List<int[]> gfxs;
	
	public TileTexture(List<int[]> gfxs) {
		this.gfxs = gfxs;
		textureEngine = new LwjglTextureEngine(new LwjglGraphicStuff());
	}
	
	public void createTextureFromMotifBank(String bankName, int nbTiles) {

		final boolean alpha = true;
		GFXBasics surface = textureEngine.prepareSurfaceForTexture(alpha);

		// Display tiles on it
		int x = 0, y = 0;
		
		if (nbTiles > 256) {
			throw new RuntimeException("Unable to record +256 tiles in a bank ! ("+nbTiles+" for "+bankName+")");
		}
		for (int n = 0; n < nbTiles; n++)
		{
			int[] motif = gfxs.get(n);
			int i,j;
			for (int ij = 0 ; ij < 256; ij++) {
				i = ij & 0xf;
				j = ij >> 4;
				int a = motif[i + j * 16];

				Vector4f color = GFXBasics.splitRGBA(a);
				surface.pset(i + x, j + y, color);
			}
			// Next position
			x += 16;
			if (x >= 256) {
				x = 0;
				y += 16;
			}
		}

		int idx = Arrays.asList(TileEngine.tileBankNames).indexOf(bankName.toLowerCase());
    	GLUtils.saveBufferAsPNG(Constantes.DATA_PATH+"\\textures\\tile"+idx, textureEngine.getBuffer(), 256, 256, alpha);
	}

	/**
	 * Fills the {@link SpriteModel} objects with real texture coordinates (in range 0..256, 0..256) based on
	 * sizes from {@link SpriteBank} objects.
	 * @param sBank
	 */
	public void createModelsFromSpriteBank(SpriteBank sBank) {
		int x=0,y=0,highestLine=0;


		// First pass to sort sprites on lower heights
		List<SpriteModel> modelSorted = new java.util.ArrayList<SpriteModel>();
		int n;
		for (n=0;n<sBank.getNSprite();n++) {
			SpriteModel spr=sBank.get_sprite(n);
			modelSorted.add(spr);
		}
		Collections.sort(modelSorted, new Comparator<SpriteModel>() {

			@Override
			public int compare(SpriteModel o1, SpriteModel o2) {
				return -Integer.valueOf(o1.getTaille_y()).compareTo(o2.getTaille_y());
			}
		});
		
		Occluder occ = new Occluder(256, 256);
		boolean withOccluder = false;
		for (SpriteModel spr : modelSorted) {
			int longX=spr.getTaille_x();
			int longY=spr.getTaille_y();
			// check for outer boundaries
			if ( (x+longX) > 256 ) {
				x=0;
				y+=highestLine;
				highestLine=0;
			}

			if (withOccluder || (y + longY) > 256) {
				// Needs occlusion to find some space inside the texture !
				Point p = occ.allocate(longX, longY);
				if (p == null) {
					//return;
					throw new RuntimeException("Unable to allocate "+longX+" x "+longY+" for "+sBank.getName()+" !");
				}
				x = p.x ; y = p.y;
				withOccluder = true;
			}
			occ.remove(new Zone(x, y, longX, longY));
			// Store sprite location on texture
			spr.setTexPos_x(x);
			spr.setTexPos_y(y); //+1);

			// Next position
			if (!withOccluder) {
				x+=longX;
				if (longY > highestLine)	// Mark the highest sprite on the row
					highestLine = longY;
			}
		}
	}
	
	public void createTextureFromSpriteBank(SpriteBank sBank) {
		final boolean alpha = true;
		GFXBasics surfaceGfx = textureEngine.prepareSurfaceForTexture(alpha);

		surfaceGfx.StartRendering();

		for (int n=0;n<sBank.getNSprite();n++)
		{
			SpriteModel spr=sBank.get_sprite(n);
			int longX=spr.getTaille_x();
			int longY=spr.getTaille_y();
			int x = spr.getTexPos_x();
			int y = spr.getTexPos_y();
			// On place le sprite sur la texture
			int[] motif=gfxs.get(n);
			for (int j=0;j< longY;j++) {
				
				for (int i=0;i< longX;i++) {
					int a=motif[i+j*longX];
					// Regular size
					a=sBank.modifyPixel(n,a);

					Vector4f color = GFXBasics.splitRGBA(a);
					surfaceGfx.pset(i+x, j+y, color);
				}
			}
		}
		int idx = Arrays.asList(SpriteStore.sprBankName).indexOf(sBank.getName());
    	GLUtils.saveBufferAsPNG(Constantes.DATA_PATH+"\\textures\\sprite"+idx, textureEngine.getBuffer(), 256, 256, alpha);
	}
	
}
