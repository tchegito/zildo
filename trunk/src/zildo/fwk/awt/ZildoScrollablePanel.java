package zildo.fwk.awt;

import java.awt.BorderLayout;

import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

import org.lwjgl.LWJGLException;

import zildo.client.ClientEngineZildo;
import zildo.monde.map.Point;

/**
 * Panel handling map display.<p/>
 * 
 * Provide two scrollbars to navigate through the map. Navigation is done by moving camera in the 
 * {@link MapDisplay} class.
 * 
 * @author tchegito
 *
 */
public class ZildoScrollablePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final int viewSizeX=640;
	private static final int viewSizeY=600;
	
	ZildoCanvas zildoCanvas;
	
	public ZildoScrollablePanel(String p_defaultMap) throws LWJGLException {
		this.setLayout(new BorderLayout());
		this.add(new MapScrollBar(JScrollBar.HORIZONTAL), BorderLayout.SOUTH);
		this.add(new MapScrollBar(JScrollBar.VERTICAL), BorderLayout.EAST);
		this.setSize(viewSizeX,viewSizeY);
		zildoCanvas = new ZildoCanvas(p_defaultMap);
        zildoCanvas.setSize(viewSizeX,viewSizeY);
		this.add(zildoCanvas);
	}

	public ZildoCanvas getZildoCanvas() {
		return zildoCanvas;
	}
	
	public class MapScrollBar extends JScrollBar {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public MapScrollBar(int i) {
			super(i);
			int max;
			int extent;
			if (orientation == JScrollBar.HORIZONTAL) {
				max=64*16;
				extent=viewSizeX;
			} else {
				max=64*16;
				extent=viewSizeY;
			}
			setMaximum(max);
			BoundedRangeModel model=new DefaultBoundedRangeModel(0, extent, 0, max);
			setModel(model);
			setUnitIncrement(16);
		}
		
		@Override
		public void setValue(int val) {
			Point p=ClientEngineZildo.mapDisplay.getCamera();
			if (orientation == JScrollBar.HORIZONTAL) {
				p.x=val;
			} else {
				p.y=val;
			}
			ClientEngineZildo.mapDisplay.setCamera(p);
			super.setValue(val);
		}
	}
}
