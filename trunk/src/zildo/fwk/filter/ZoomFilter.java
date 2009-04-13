package zildo.fwk.filter;

import org.lwjgl.opengl.GL11;

import zildo.fwk.engine.EngineZildo;
import zildo.monde.Point;
import zildo.monde.Sprite;
import zildo.monde.persos.PersoZildo;

public class ZoomFilter extends ScreenFilter {

	
	public boolean renderFilter()
	{
		// Focus camera on Zildo, and zoom according to the 'fadeLevel'
		PersoZildo zildo=EngineZildo.persoManagement.getZildo();
		Point zildoPos=new Point(zildo.getScrX(), zildo.getScrY());
		Sprite spr=zildo.getSprModel();
		zildoPos.addX(spr.getTaille_x() / 2);
		zildoPos.addY(spr.getTaille_y() / 2);
		EngineZildo.getOpenGLGestion().setZoomPosition(zildoPos);
		EngineZildo.getOpenGLGestion().setZ((float) Math.sin(getFadeLevel() * (0.5f*Math.PI / 256.0f)));
				
		GL11.glDisable(GL11.GL_BLEND);

		return true;
	}
	
	
	/**
	 * Re-initialize z coordinate
	 */
	public void doOnInactive() {
		EngineZildo.getOpenGLGestion().setZ(0);
	}	
}
